package no.sandramoen.commanderqueen.screens.gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;

import no.sandramoen.commanderqueen.actors.Barrel;
import no.sandramoen.commanderqueen.actors.utils.BulletPuffManager;
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

    private boolean isGameOver;
    private boolean holdingDown;

    private BulletPuffManager bulletPuffManager;

    @Override
    public void initialize() {
        long startTime = System.currentTimeMillis();

        /*GameUtils.playLoopingMusic(BaseGame.level0Music);*/
        GameUtils.playLoopingMusic(BaseGame.metalWalkingMusic, 0);
        initializeMap();
        initializeActors();
        initializeUI();
        bulletPuffManager = new BulletPuffManager(mainStage3D.camera, decalBatch);

        GameUtils.printLoadingTime(getClass().getSimpleName(), startTime);
    }

    @Override
    public void update(float dt) {
        checkGameOverCondition();

        updateTiles();
        updateEnemies();
        updateBarrels();
        updatePickups();
        updateWeapon();

        debugLabel.setText("FPS: " + Gdx.graphics.getFramesPerSecond() + "\nVisible: " + mainStage3D.visibleCount);

        if (!Gdx.input.isCursorCatched())
            Gdx.input.setCursorCatched(true);

        buttonPolling();

        bulletPuffManager.render(dt);
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
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        holdingDown = false;
        return super.touchUp(screenX, screenY, pointer, button);
    }

    private void buttonPolling() {
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && !isGameOver) {
            if (weapon.isReady)
                shoot();
            holdingDown = true;
        }
    }

    private void rayPickTarget() {
        Vector2 spread = getSpread();
        int screenX = (int) (Gdx.graphics.getWidth() / 2 + MathUtils.random(-spread.x, spread.y));
        int screenY = (int) (Gdx.graphics.getHeight() / 2 + MathUtils.random(-spread.y, spread.y));

        Ray ray = mainStage3D.camera.getPickRay(screenX, screenY);
        int i = GameUtils.getClosestListIndex(ray, shootable);

        if (i >= 0) {
            if (shootable.get(i).getClass().getSimpleName().equalsIgnoreCase("menig")) {
                Menig menig = (Menig) shootable.get(i);
                activateEnemies(45, menig);
                menig.decrementHealth(weapon.damage);
            } else if (shootable.get(i).getClass().getSimpleName().equalsIgnoreCase("barrel")) {
                Barrel barrel = (Barrel) shootable.get(i);
                barrel.decrementHealth(weapon.damage, player.distanceBetween(barrel));
            } else if (shootable.get(i).getClass().getSimpleName().equalsIgnoreCase("tile") && hud.getAmmo() > 0) {
                Tile tile = (Tile) shootable.get(i);
                if (tile.type.equalsIgnoreCase("walls")) {
                    Vector3 temp = new Vector3().set(ray.direction).scl(player.distanceBetween(tile) - (Tile.diagonalLength / 2)).add(ray.origin);
                    bulletPuffManager.addNewBulletPuff(temp.x, temp.y, temp.z);
                }
            }
        }
    }

    private Vector2 getSpread() {
        int maxSpreadX = 0;
        if (holdingDown)
            maxSpreadX = (int) (Gdx.graphics.getWidth() / mainStage3D.camera.fieldOfView * weapon.SPREAD_ANGLE);
        int maxSpreadY = 0;
        if (holdingDown)
            maxSpreadY = (int) (maxSpreadX / BaseGame.aspectRatio);
        return new Vector2(maxSpreadX, maxSpreadY);
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
            if (enemies.get(i).health <= 0) {
                removeEnemy(enemies.get(i));
                continue;
            }

            tryToActivateOthers(i);
            preventOverlapWithOtherEnemies(i);

            for (Tile tile : tiles) {
                preventEnemyOverLapWithTile(tile, i);
                illuminateEnemy(tile, i);
            }
        }
    }

    private void updateBarrels() {
        for (BaseActor3D baseActor3D : shootable) {
            if (baseActor3D.getClass().getSimpleName().equalsIgnoreCase("barrel")) {
                Barrel barrel = (Barrel) baseActor3D;
                if (barrel.health <= 0)
                    explodeBarrelWithDelay(barrel);
            }
        }
    }

    private void updatePickups() {
        for (Pickup pickup : pickups) {
            if (player.overlaps(pickup)) {
                if (pickup.getClass().getSimpleName().equals("Ammo")) {
                    hud.incrementAmmo(pickup.amount);
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

    private void tryToActivateOthers(int i) {
        if (mainStage3D.intervalFlag && enemies.get(i).isActive) {
            for (int j = 0; j < enemies.size; j++) {
                if (enemies.get(i) != enemies.get(j))
                    activateEnemies(45, player);
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
        if (source.getClass().getSimpleName().equalsIgnoreCase("barrel")) {
            Barrel barrel = (Barrel) source;
            hud.decrementHealth(barrel.getBlastDamage(source.distanceBetween(player)), source);

            if (player.isWithinDistance(barrel.BLAST_RANGE, source))
                player.forceMoveAwayFrom(source);
            else
                hud.setKillFace();
        }
    }

    private void checkEnemiesExplosionDamage(BaseActor3D source) {
        if (source.getClass().getSimpleName().equalsIgnoreCase("barrel")) {
            Barrel barrel = (Barrel) source;

            for (Enemy enemy : enemies) {
                enemy.decrementHealth(barrel.getBlastDamage(source.distanceBetween(enemy)));
                if (enemy.isWithinDistance(barrel.BLAST_RANGE, source))
                    enemy.forceMoveAwayFrom(source);
            }
        }
    }

    private void checkBarrelsExplosionDamage(BaseActor3D source) {
        if (source.getClass().getSimpleName().equalsIgnoreCase("barrel")) {
            Barrel barrel = (Barrel) source;

            for (BaseActor3D baseActor3D : shootable) {
                if (baseActor3D.getClass().getSimpleName().equalsIgnoreCase("barrel")) {
                    Barrel otherBarrel = (Barrel) baseActor3D;
                    otherBarrel.decrementHealth(barrel.getBlastDamage(source.distanceBetween(otherBarrel)), 0);
                }
            }
        }
    }

    private void removeEnemy(Enemy enemy) {
        enemy.die();
        pickups.add(new Ammo(enemy.position.y + MathUtils.random(-1, 1), enemy.position.z + MathUtils.random(-1, 1), mainStage3D, player, 2));
        enemies.removeValue(enemy, false);
        shootable.removeValue(enemy, false);
        for (int i = 0; i < enemies.size; i++)
            enemies.get(i).setShootableList(shootable);
        hud.incrementScore(enemy.score, false);
        hud.setKillFace();
        statusLabel.setText("enemies left: " + enemies.size);
    }

    private void shoot() {
        weapon.shoot(hud.getAmmo());
        if (hud.getAmmo() > 0) {
            player.muzzleLight();
            hud.decrementAmmo();
            rayPickTarget();
            activateEnemies(45, player);
        }
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
