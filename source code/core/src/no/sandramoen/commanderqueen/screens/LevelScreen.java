package no.sandramoen.commanderqueen.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;

import no.sandramoen.commanderqueen.actors.Barrel;
import no.sandramoen.commanderqueen.actors.hud.HUD;
import no.sandramoen.commanderqueen.actors.characters.Ghoul;
import no.sandramoen.commanderqueen.actors.characters.Player;
import no.sandramoen.commanderqueen.actors.Tile;
import no.sandramoen.commanderqueen.actors.hud.Weapon;
import no.sandramoen.commanderqueen.actors.pickups.Ammo;
import no.sandramoen.commanderqueen.actors.pickups.Pickup;
import no.sandramoen.commanderqueen.actors.utils.BaseActor3D;
import no.sandramoen.commanderqueen.actors.utils.Enemy;
import no.sandramoen.commanderqueen.actors.utils.MapLoader;
import no.sandramoen.commanderqueen.actors.utils.TilemapActor;
import no.sandramoen.commanderqueen.utils.BaseGame;
import no.sandramoen.commanderqueen.utils.BaseScreen3D;
import no.sandramoen.commanderqueen.utils.GameUtils;

public class LevelScreen extends BaseScreen3D {
    private Player player;
    private Weapon weapon;
    private HUD hud;
    private Array<Tile> tiles;
    private Array<Enemy> enemies;
    private Array<Pickup> pickups;

    private Label debugLabel;
    private Label gameLabel;
    private Label statusLabel;

    private Array<BaseActor3D> shootable;
    private int index = -1;
    private boolean isGameOver = false;
    private TilemapActor tilemap;

    private MapLoader mapLoader;

    public void initialize() {
        /*GameUtils.playLoopingMusic(BaseGame.level0Music);*/
        GameUtils.playLoopingMusic(BaseGame.metalWalkingMusic, 0);
        initializeMap();
        initializeActors();
        initializeUI();
    }

    public void update(float dt) {
        if (isGameOver) return;
        if (hud.getHealth() == 0) {
            setGameOver();
            return;
        }
        updateTiles();
        updateEnemies();
        updatePickups();
        weapon.sway(player.isMoving);

        debugLabel.setText("FPS: " + Gdx.graphics.getFramesPerSecond() + "\nVisible: " + mainStage3D.visibleCount);

        if (!Gdx.input.isCursorCatched())
            Gdx.input.setCursorCatched(true);

        setCrosshairColorIfEnemy(rayPickBaseActor3DFromList(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2, shootable));
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Keys.ESCAPE || keycode == Keys.Q)
            Gdx.app.exit();
        if (keycode == Keys.R)
            BaseGame.setActiveScreen(new LevelScreen());
        if (keycode == Keys.NUM_1)
            hud.decrementHealth(10);
        if (keycode == Keys.NUM_2)
            hud.incrementHealth(10);
        if (keycode == Keys.NUM_3)
            hud.setInvulnerable();
        return super.keyDown(keycode);
    }

    @Override
    public void dispose() {
        mainStage3D.dispose();
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.LEFT && !isGameOver) {
            player.shoot();
            weapon.shoot();
            hud.decrementAmmo();
            int index = rayPickBaseActor3DFromList(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2, shootable);
            determineConsequencesOfPick(index);
        }
        return super.touchDown(screenX, screenY, pointer, button);
    }

    private void setCrosshairColorIfEnemy(int index) {
        if (shootable.get(index).getClass().getSimpleName().equals("Ghoul") || shootable.get(index).getClass().getSimpleName().equals("Barrel"))
            weapon.crosshair.setColor(BaseGame.redColor);
        else
            weapon.crosshair.setColor(Color.WHITE);
    }

    private int rayPickBaseActor3DFromList(int screenX, int screenY, Array<BaseActor3D> list) {
        Ray ray = mainStage3D.camera.getPickRay(screenX, screenY);
        int result = -1;
        float distance = -1;
        for (int i = 0; i < list.size; ++i) {
            final float dist2 = list.get(i).modelData.intersects(ray);
            if (dist2 >= 0f && (distance < 0f || dist2 <= distance)) {
                result = i;
                distance = dist2;
            }
        }
        return result;
    }

    private void determineConsequencesOfPick(int index) {
        if (index >= 0) {
            if (shootable.get(index).getClass().getSimpleName().equals("Ghoul")) {
                Ghoul ghoul = (Ghoul) shootable.get(index);
                pickups.add(new Ammo(ghoul.position.y, ghoul.position.z, mainStage3D, player));
                ghoul.die();
                enemies.removeValue(ghoul, false);
                shootable.removeValue(ghoul, false);
                statusLabel.setText("enemies left: " + enemies.size);
                hud.incrementScore(10, false);
                hud.setKillFace();
            } else if (shootable.get(index).getClass().getSimpleName().equals("Barrel")) {
                Barrel barrel = (Barrel) shootable.get(index);
                checkExplosionRange(barrel);
                barrel.explode();
                shootable.removeValue(barrel, false);
            }
        }
    }

    private void updateTiles() {
        for (Tile tile : tiles) {
            if (tile.type == "walls" && player.overlaps(tile)) {
                player.preventOverlap(tile);
            }
        }
    }

    private void updateEnemies() {
        for (Enemy enemy : enemies) {
            if (player.overlaps(enemy)) {
                player.preventOverlap(enemy);
                if (enemy.getClass().getSimpleName().equals("Ghoul")) {
                    Ghoul ghoul = (Ghoul) enemy;
                    if (ghoul.isReadyToAttack()) {
                        hud.decrementHealth(10);
                        BaseGame.ghoulDeathSound.play(BaseGame.soundVolume, 1.5f, 0);
                    }
                }
            }

            for (Tile tile : tiles) {
                if (tile.type == "walls" && enemy.overlaps(tile))
                    enemy.preventOverlap(tile);

                if (enemy.overlaps(tile) && tile.type == "floors" && tile.illuminated)
                    enemy.setColor(Color.WHITE);
                else if (enemy.overlaps(tile) && tile.type == "floors")
                    enemy.setColor(enemy.originalColor);
            }
        }
    }

    private void updatePickups() {
        for (Pickup pickup : pickups) {
            if (player.overlaps(pickup)) {
                if (pickup.getClass().getSimpleName().equals("Ammo"))
                    hud.incrementAmmo(1);
                if (pickup.getClass().getSimpleName().equals("Armor"))
                    hud.incrementArmor(25);
                if (pickup.getClass().getSimpleName().equals("Health"))
                    hud.incrementHealth(10);
                pickups.removeValue(pickup, false);
                pickup.remove();
            }
        }
    }

    private void setGameOver() {
        if (!isGameOver) {
            mainStage3D.camera.position.x = -Tile.height * .48f;
            hud.setDeadFace();
            BaseGame.metalWalkingMusic.stop();
            weapon.moveDown();
            gameLabel.setText("G A M E   O V E R !");
            player.isPause = true;
            for (Enemy enemy : enemies)
                enemy.isPause = true;
            isGameOver = true;
        }
    }

    private void explosionPushBack(BaseActor3D baseActor3D, BaseActor3D explosion) {
        float moveY = baseActor3D.position.y - explosion.position.y;
        float moveZ = baseActor3D.position.z - explosion.position.z;
        baseActor3D.moveBy(0f, moveY * .5f, moveZ * .5f);
    }

    private void checkExplosionRange(final BaseActor3D source) {
        if (player.isWithinDistance2(2f, source))
            hud.decrementHealth(100);
        else if (player.isWithinDistance2(7f, source))
            hud.decrementHealth(50);
        else if (player.isWithinDistance2(10f, source)) {
            hud.decrementHealth(25);
            // player.position.z = 100; // TODO
        } else
            hud.setKillFace();

        for (final Enemy enemy : enemies) {
            if (enemy.isWithinDistance2(2f, source)) {
                enemy.decrementHealth(100);
            } else if (enemy.isWithinDistance2(7f, source)) {
                enemy.decrementHealth(50);
            } else if (enemy.isWithinDistance2(10f, source)) {
                enemy.decrementHealth(25);
            } else
                continue;

            if (enemy.isDead) {
                enemies.removeValue(enemy, false);
                shootable.removeValue(enemy, false);
            }
        }
    }

    private void initializeMap() {
        pickups = new Array();
        shootable = new Array();
        tiles = new Array();
        enemies = new Array();
        tilemap = new TilemapActor(BaseGame.level0Map, mainStage3D);
        mapLoader = new MapLoader(tilemap, tiles, mainStage3D, player, shootable, pickups, enemies);
    }

    private void initializeActors() {
        player = mapLoader.player;
        hud = new HUD(uiStage);
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
                .colspan(2);

        /*uiTable.setDebug(true);*/
    }
}
