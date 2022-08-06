package no.sandramoen.commanderqueen.screens.gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Array;

import no.sandramoen.commanderqueen.actors.Barrel;
import no.sandramoen.commanderqueen.actors.decals.BloodDecals;
import no.sandramoen.commanderqueen.actors.decals.BulletDecals;
import no.sandramoen.commanderqueen.actors.hud.HUD;
import no.sandramoen.commanderqueen.actors.characters.Menig;
import no.sandramoen.commanderqueen.actors.characters.Player;
import no.sandramoen.commanderqueen.actors.Tile;
import no.sandramoen.commanderqueen.actors.hud.Weapon;
import no.sandramoen.commanderqueen.actors.pickups.Ammo;
import no.sandramoen.commanderqueen.actors.pickups.Pickup;
import no.sandramoen.commanderqueen.actors.utils.baseActors.BaseActor;
import no.sandramoen.commanderqueen.actors.utils.baseActors.BaseActor3D;
import no.sandramoen.commanderqueen.actors.utils.Enemy;
import no.sandramoen.commanderqueen.utils.level.BarrelExplosionHandler;
import no.sandramoen.commanderqueen.utils.level.EnemyHandler;
import no.sandramoen.commanderqueen.utils.level.MapLoader;
import no.sandramoen.commanderqueen.actors.utils.TilemapActor;
import no.sandramoen.commanderqueen.utils.BaseGame;
import no.sandramoen.commanderqueen.utils.BaseScreen3D;
import no.sandramoen.commanderqueen.utils.GameUtils;
import no.sandramoen.commanderqueen.utils.level.PickupHandler;
import no.sandramoen.commanderqueen.utils.level.TileHandler;
import no.sandramoen.commanderqueen.utils.level.UIHandler;

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

    private boolean isGameOver;
    private boolean holdingDown;

    private UIHandler uiHandler;
    private BulletDecals bulletDecals;
    private BloodDecals bloodDecals;

    @Override
    public void initialize() {
        long startTime = System.currentTimeMillis();

        /*GameUtils.playLoopingMusic(BaseGame.level0Music);*/
        GameUtils.playLoopingMusic(BaseGame.metalWalkingMusic, 0);
        initializeMap();
        initializePlayer();
        uiHandler = new UIHandler(uiTable, enemies, hud);
        bulletDecals = new BulletDecals(mainStage3D.camera, decalBatch);
        bloodDecals = new BloodDecals(mainStage3D.camera, decalBatch);

        GameUtils.printLoadingTime(getClass().getSimpleName(), startTime);
    }

    @Override
    public void update(float dt) {
        checkGameOverCondition();

        TileHandler.updateTiles(tiles, player);
        updateEnemies();
        updateBarrels();
        PickupHandler.updatePickups(pickups, player, hud);
        weapon.update(hud, player, shootable, mainStage3D);

        uiHandler.debugLabel.setText("FPS: " + Gdx.graphics.getFramesPerSecond() + "\nVisible: " + mainStage3D.visibleCount);

        if (!Gdx.input.isCursorCatched())
            Gdx.input.setCursorCatched(true);

        buttonPolling();

        bulletDecals.render(dt);
        bloodDecals.render(dt);
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

    private void shoot() {
        weapon.shoot(hud.getAmmo());
        if (hud.getAmmo() > 0) {
            player.muzzleLight();
            hud.decrementAmmo();
            rayPickTarget();
            EnemyHandler.activateEnemies(enemies, 45, player);
        }
    }

    private void rayPickTarget() {
        Vector2 spread = weapon.getSpread(holdingDown, mainStage3D.camera.fieldOfView);
        int screenX = (int) (Gdx.graphics.getWidth() / 2 + MathUtils.random(-spread.x, spread.y));
        int screenY = (int) (Gdx.graphics.getHeight() / 2 + MathUtils.random(-spread.y, spread.y));

        Ray ray = mainStage3D.camera.getPickRay(screenX, screenY);
        int i = GameUtils.getClosestListIndex(ray, shootable);

        if (i >= 0) {
            if (GameUtils.isActor(shootable.get(i), "menig")) {
                Menig menig = (Menig) shootable.get(i);
                EnemyHandler.activateEnemies(enemies, 45, player);
                menig.decrementHealth(weapon.getDamage());

                Vector3 temp = new Vector3().set(ray.direction).scl(player.distanceBetween(menig) - .2f).add(ray.origin);
                bloodDecals.addDecal(temp.x, temp.y, temp.z);

            } else if (GameUtils.isActor(shootable.get(i), "barrel")) {
                Barrel barrel = (Barrel) shootable.get(i);
                barrel.decrementHealth(weapon.getDamage(), player.distanceBetween(barrel));
            } else if (GameUtils.isActor(shootable.get(i), "tile") && hud.getAmmo() > 0) {
                Tile tile = (Tile) shootable.get(i);
                if (tile.type.equalsIgnoreCase("walls")) {
                    Vector3 temp = new Vector3().set(ray.direction).scl(player.distanceBetween(tile) - (Tile.diagonalLength / 2)).add(ray.origin);
                    bulletDecals.addDecal(temp.x, temp.y, temp.z);
                }
            }
        }
    }


    private void updateEnemies() {
        for (int i = 0; i < enemies.size; i++) {
            if (enemies.get(i).health <= 0) {
                removeEnemy(enemies.get(i));
                continue;
            }

            EnemyHandler.preventOverlapWithOtherEnemies(enemies, i);

            for (Tile tile : tiles) {
                EnemyHandler.preventOverLapWithTile(enemies, tile, i);
                if (mainStage3D.intervalFlag)
                    EnemyHandler.illuminateEnemy(enemies, tile, i);
            }
        }
    }

    private void removeEnemy(Enemy enemy) {
        enemy.die();
        pickups.add(new Ammo(enemy.position.y + MathUtils.random(-1, 1), enemy.position.z + MathUtils.random(-1, 1), mainStage3D, player, 2));
        enemies.removeValue(enemy, false);
        shootable.removeValue(enemy, false);
        EnemyHandler.updateEnemiesShootableList(enemies, shootable);
        EnemyHandler.updateEnemiesEnemiesList(enemies);
        hud.incrementScore(enemy.score, false);
        hud.setKillFace();
        uiHandler.statusLabel.setText("enemies left: " + enemies.size);
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
            uiHandler.gameLabel.setText("G A M E   O V E R !");
            mainStage3D.camera.position.x = -Tile.height * .48f;
            weapon.crosshair.setVisible(false);
        }
    }


    private void updateBarrels() {
        for (BaseActor3D baseActor3D : shootable) {
            if (GameUtils.isActor(baseActor3D, "barrel")) {
                Barrel barrel = (Barrel) baseActor3D;
                if (barrel.health <= 0)
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
        EnemyHandler.activateEnemies(enemies, 45, barrel);
        shootable.removeValue(barrel, false);
        EnemyHandler.updateEnemiesShootableList(enemies, shootable);
        BarrelExplosionHandler.checkExplosionRange(hud, player, enemies, shootable, barrel);
        barrel.explode();
    }


    private void initializeMap() {
        tilemap = new TilemapActor(BaseGame.level0Map, mainStage3D);
        tiles = new Array();
        shootable = new Array();
        enemies = new Array();
        pickups = new Array();
        hud = new HUD(uiStage);
        mapLoader = new MapLoader(tilemap, tiles, mainStage3D, player, shootable, pickups, enemies, uiStage, hud);
    }

    private void initializePlayer() {
        player = mapLoader.player;
        hud.player = player;
        weapon = new Weapon(uiStage);
    }
}
