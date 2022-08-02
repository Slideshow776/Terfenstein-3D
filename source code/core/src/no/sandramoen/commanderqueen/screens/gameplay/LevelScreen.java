package no.sandramoen.commanderqueen.screens.gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;

import no.sandramoen.commanderqueen.actors.Barrel;
import no.sandramoen.commanderqueen.actors.hud.HUD;
import no.sandramoen.commanderqueen.actors.characters.Menig;
import no.sandramoen.commanderqueen.actors.characters.Player;
import no.sandramoen.commanderqueen.actors.Tile;
import no.sandramoen.commanderqueen.actors.hud.Weapon;
import no.sandramoen.commanderqueen.actors.pickups.Ammo;
import no.sandramoen.commanderqueen.actors.pickups.Pickup;
import no.sandramoen.commanderqueen.actors.utils.BaseActor;
import no.sandramoen.commanderqueen.actors.utils.BaseActor3D;
import no.sandramoen.commanderqueen.actors.utils.Enemy;
import no.sandramoen.commanderqueen.utils.level.MapLoader;
import no.sandramoen.commanderqueen.actors.utils.TilemapActor;
import no.sandramoen.commanderqueen.utils.BaseGame;
import no.sandramoen.commanderqueen.utils.BaseScreen3D;
import no.sandramoen.commanderqueen.utils.GameUtils;

public class LevelScreen extends BaseScreen3D {
    private HUD hud;
    private Player player;
    private Weapon weapon;
    private MapLoader mapLoader;
    private TilemapActor tilemap;

    private Array<Tile> tiles;
    private Array<Enemy> enemies;
    private Array<Pickup> pickups;
    private Array<BaseActor3D> shootable;

    private Label debugLabel;
    private Label gameLabel;
    private Label statusLabel;

    private boolean isGameOver = false;

    @Override
    public void initialize() {
        /*GameUtils.playLoopingMusic(BaseGame.level0Music);*/
        GameUtils.playLoopingMusic(BaseGame.metalWalkingMusic, 0);
        initializeMap();
        initializeActors();
        initializeUI();
    }

    @Override
    public void update(float dt) {
        checkGameOverCondition();

        updateTiles();
        updateEnemies();
        updatePickups();
        updateWeapon();

        debugLabel.setText("FPS: " + Gdx.graphics.getFramesPerSecond() + "\nVisible: " + mainStage3D.visibleCount);

        if (!Gdx.input.isCursorCatched())
            Gdx.input.setCursorCatched(true);
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Keys.ESCAPE || keycode == Keys.Q)
            Gdx.app.exit();
        if (keycode == Keys.R)
            BaseGame.setActiveScreen(new LevelScreen());
        if (keycode == Keys.NUM_1)
            hud.decrementHealth(10, null);
        if (keycode == Keys.NUM_2)
            hud.incrementHealth(1);
        if (keycode == Keys.NUM_3)
            hud.setInvulnerable();
        if (keycode == Keys.NUM_4) {
            player.isCollisionEnabled = !player.isCollisionEnabled;
            Gdx.app.log(getClass().getSimpleName(), "player.isCollisionEnabled: " + player.isCollisionEnabled);
        }
        if (keycode == Keys.NUM_5)
            hud.incrementArmor(100, false);
        return super.keyDown(keycode);
    }

    @Override
    public void dispose() {
        mainStage3D.dispose();
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.LEFT && !isGameOver)
            if (hud.getAmmo() > 0)
                shoot();
            else
                BaseGame.outOfAmmoSound.play(BaseGame.soundVolume, MathUtils.random(.8f, 1.2f), 0);
        return super.touchDown(screenX, screenY, pointer, button);
    }

    private void determineConsequencesOfPick(int index) {
        if (index >= 0) {
            if (shootable.get(index).getClass().getSimpleName().equalsIgnoreCase("menig")) {
                Menig menig = (Menig) shootable.get(index);
                activateEnemies(45, menig);
                if (menig.isDeadAfterTakingDamage(1)) {
                    removeEnemy(menig);
                    hud.incrementScore(10, false);
                    hud.setKillFace();
                }
            } else if (shootable.get(index).getClass().getSimpleName().equalsIgnoreCase("barrel")) {
                Barrel barrel = (Barrel) shootable.get(index);
                explodeBarrelWithDelay(barrel);
            }
        }
    }

    private void explodeBarrelWithDelay(final Barrel barrel) {
        new BaseActor(0, 0, uiStage).addAction(Actions.sequence(
                Actions.delay(MathUtils.random(0, .4f)),
                Actions.run(() -> explodeBarrel(barrel))
        ));
    }

    private void explodeBarrel(Barrel barrel) {
        activateEnemies(45, barrel);
        shootable.removeValue(barrel, false);
        checkExplosionRange(barrel);
        barrel.explode();
    }

    private void setCrosshairColorIfEnemy(int index) {
        if (index >= 0) {
            if (shootable.get(index).getClass().getSimpleName().equalsIgnoreCase("menig") || shootable.get(index).getClass().getSimpleName().equalsIgnoreCase("barrel"))
                weapon.crosshair.setColor(BaseGame.redColor);
            else
                weapon.crosshair.setColor(Color.WHITE);
        }
    }

    private void updateTiles() {
        for (Tile tile : tiles) {
            if (tile.type == "walls" && player.overlaps(tile))
                player.preventOverlap(tile);
        }
    }

    private void updateEnemies() {
        for (int i = 0; i < enemies.size; i++) {
            tryToActivateOthers(i);
            preventOverlapWithOtherEnemies(i);

            for (Tile tile : tiles) {
                preventEnemyOverLapWithTile(tile, i);
                illuminateEnemy(tile, i);
            }
        }
    }

    private void tryToActivateOthers(int i) {
        if (mainStage3D.intervalFlag && enemies.get(i).isActive) {
            for (int j = 0; j < enemies.size; j++) {
                if (enemies.get(i) != enemies.get(j))
                    activateEnemies(45, player);
            }
        }
    }

    private void updatePickups() {
        for (Pickup pickup : pickups) {
            if (player.overlaps(pickup)) {
                if (pickup.getClass().getSimpleName().equals("Ammo")) {
                    hud.incrementAmmo(1);
                    removePickup(pickup);
                }
                if (pickup.getClass().getSimpleName().equals("Armor")) {
                    if (hud.incrementArmor(pickup.amount, false))
                        removePickup(pickup);
                }
                if (pickup.getClass().getSimpleName().equals("Health")) {
                    if (hud.incrementHealth(pickup.amount))
                        removePickup(pickup);
                }

            }
        }
    }

    private void removePickup(Pickup pickup) {
        pickups.removeValue(pickup, false);
        pickup.remove();
    }

    private void updateWeapon() {
        if (hud.getHealth() > 0)
            weapon.sway(player.isMoving);
        setCrosshairColorIfEnemy(GameUtils.getRayPickedListIndex(
                Gdx.graphics.getWidth() / 2,
                Gdx.graphics.getHeight() / 2,
                shootable,
                mainStage3D.camera
        ));
    }

    private void checkGameOverCondition() {
        if (hud.getHealth() == 0)
            setGameOver();
    }

    private void setGameOver() {
        if (!isGameOver) {
            isGameOver = true;
            hud.setDeadFace();
            weapon.moveDown();
            player.isPause = true;
            new BaseActor(0, 0, uiStage).addAction(Actions.sequence(
                    Actions.delay(5),
                    Actions.run(() -> {
                        for (Enemy enemy : enemies)
                            enemy.isPause = true;
                    })
            ));
            for (Enemy enemy : enemies)
                enemy.isRanged = false;
            BaseGame.metalWalkingMusic.stop();
            gameLabel.setText("G A M E   O V E R !");
            mainStage3D.camera.position.x = -Tile.height * .48f;
            weapon.crosshair.setVisible(false);
        }
    }

    private void preventOverlapWithOtherEnemies(int i) {
        for (int j = 0; j < enemies.size; j++) {
            if (enemies.get(i) != enemies.get(j))
                enemies.get(i).preventOverlap(enemies.get(j));
        }
    }

    private void preventEnemyOverLapWithTile(Tile tile, int i) {
        if (tile.type == "walls" && enemies.get(i).overlaps(tile))
            enemies.get(i).preventOverlap(tile);
    }

    private void illuminateEnemy(Tile tile, int i) {
        if (enemies.get(i).overlaps(tile) && tile.type == "floors" && tile.illuminated)
            enemies.get(i).setColor(Color.WHITE);
        else if (enemies.get(i).overlaps(tile) && tile.type == "floors")
            enemies.get(i).setColor(enemies.get(i).originalColor);
    }

    private void checkExplosionRange(BaseActor3D source) {
        checkPlayerExplosionDamage(source);
        checkEnemiesExplosionDamage(source);
        checkBarrelsExplosionDamage(source);
    }

    private void checkPlayerExplosionDamage(BaseActor3D source) {
        if (player.isWithinDistance(3f, source))
            hud.decrementHealth(100, source);
        else if (player.isWithinDistance(7f, source))
            hud.decrementHealth(50, source);
        else if (player.isWithinDistance(10f, source))
            hud.decrementHealth(25, source);
        else
            hud.setKillFace();

        if (player.isWithinDistance(10f, source))
            player.forceMoveAwayFrom(source);
    }

    private void checkEnemiesExplosionDamage(BaseActor3D source) {
        for (Enemy enemy : enemies) {
            if (enemy.isWithinDistance(3f, source))
                enemy.isDeadAfterTakingDamage(100);
            else if (enemy.isWithinDistance(7f, source))
                enemy.isDeadAfterTakingDamage(50);
            else if (enemy.isWithinDistance(10f, source))
                enemy.isDeadAfterTakingDamage(25);

            if (enemy.isWithinDistance(10f, source))
                enemy.forceMoveAwayFrom(source);
        }

        for (Enemy enemy : enemies)
            if (enemy.isDead)
                removeEnemy(enemy);
    }

    private void checkBarrelsExplosionDamage(BaseActor3D source) {
        for (int i = 0; i < shootable.size; i++)
            if (shootable.get(i).getClass().getSimpleName().equalsIgnoreCase("barrel"))
                if (shootable.get(i).isWithinDistance(10f, source))
                    explodeBarrelWithDelay((Barrel) shootable.get(i));
    }

    private void removeEnemy(Enemy enemy) {
        enemy.die();
        pickups.add(new Ammo(enemy.position.y + MathUtils.random(-1, 1), enemy.position.z + MathUtils.random(-1, 1), mainStage3D, player));
        enemies.removeValue(enemy, false);
        shootable.removeValue(enemy, false);
        for (int i = 0; i < enemies.size; i++)
            enemies.get(i).setShootableList(shootable);
        statusLabel.setText("enemies left: " + enemies.size);
    }

    private void shoot() {
        weapon.shoot();
        player.muzzleLight();
        hud.decrementAmmo();
        int index = GameUtils.getRayPickedListIndex(
                Gdx.graphics.getWidth() / 2,
                Gdx.graphics.getHeight() / 2,
                shootable,
                mainStage3D.camera
        );
        determineConsequencesOfPick(index);
        activateEnemies(45, player);
    }

    private void activateEnemies(float range, BaseActor3D source) {
        for (Enemy enemy : enemies) {
            if (enemy.isWithinDistance(range, source))
                enemy.activate(source);
        }
    }

    private void initializeMap() {
        pickups = new Array();
        shootable = new Array();
        tiles = new Array();
        enemies = new Array();
        hud = new HUD(uiStage);
        tilemap = new TilemapActor(BaseGame.level0Map, mainStage3D);
        mapLoader = new MapLoader(tilemap, tiles, mainStage3D, player, shootable, pickups, enemies, uiStage, hud);
    }

    private void initializeActors() {
        player = mapLoader.player;
        hud.player = player;
        weapon = new Weapon(uiStage);
    }

    private void initializeUI() {
        statusLabel = new Label("enemies left: " + enemies.size, BaseGame.label26Style);
        uiTable.add(statusLabel)
                .row();

        debugLabel = new Label(" ", BaseGame.label26Style);
        uiTable.add(debugLabel)
                .expandX()
                .top()
                .left()
                .padTop(Gdx.graphics.getHeight() * .01f)
                .padLeft(Gdx.graphics.getWidth() * .01f)
                .row();


        gameLabel = new Label("", BaseGame.label26Style);
        gameLabel.setColor(Color.RED);
        gameLabel.setFontScale(2f);
        uiTable.add(gameLabel)
                .expand()
                .center()
                .colspan(2)
                .row();

        uiTable.add(hud.getLabelTable()).colspan(2).size(hud.getWidth(), hud.getHeight());

        /*uiTable.setDebug(true);*/
    }
}
