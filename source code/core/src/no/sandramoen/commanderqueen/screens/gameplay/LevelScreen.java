package no.sandramoen.commanderqueen.screens.gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Array;

import no.sandramoen.commanderqueen.actors.Barrel;
import no.sandramoen.commanderqueen.actors.Door;
import no.sandramoen.commanderqueen.actors.Elevator;
import no.sandramoen.commanderqueen.actors.characters.HolyBall;
import no.sandramoen.commanderqueen.actors.characters.Hund;
import no.sandramoen.commanderqueen.actors.characters.Menig;
import no.sandramoen.commanderqueen.actors.characters.Prest;
import no.sandramoen.commanderqueen.actors.characters.Sersjant;
import no.sandramoen.commanderqueen.actors.decals.BloodDecals;
import no.sandramoen.commanderqueen.actors.decals.BulletDecals;
import no.sandramoen.commanderqueen.actors.hud.HUD;
import no.sandramoen.commanderqueen.actors.characters.Player;
import no.sandramoen.commanderqueen.actors.Tile;
import no.sandramoen.commanderqueen.actors.pickups.Shells;
import no.sandramoen.commanderqueen.actors.weapon.WeaponHandler;
import no.sandramoen.commanderqueen.actors.pickups.Bullets;
import no.sandramoen.commanderqueen.actors.pickups.Pickup;
import no.sandramoen.commanderqueen.actors.utils.baseActors.BaseActor;
import no.sandramoen.commanderqueen.actors.utils.baseActors.BaseActor3D;
import no.sandramoen.commanderqueen.actors.characters.enemy.Enemy;
import no.sandramoen.commanderqueen.actors.weapon.weapons.Weapon;
import no.sandramoen.commanderqueen.screens.gameplay.level.BarrelExplosionHandler;
import no.sandramoen.commanderqueen.screens.gameplay.level.EnemyHandler;
import no.sandramoen.commanderqueen.screens.gameplay.level.MapLoader;
import no.sandramoen.commanderqueen.actors.utils.TilemapActor;
import no.sandramoen.commanderqueen.screens.shell.LevelFinishScreen;
import no.sandramoen.commanderqueen.screens.shell.MenuScreen;
import no.sandramoen.commanderqueen.utils.BaseGame;
import no.sandramoen.commanderqueen.utils.BaseScreen3D;
import no.sandramoen.commanderqueen.utils.GameUtils;
import no.sandramoen.commanderqueen.screens.gameplay.level.PickupHandler;
import no.sandramoen.commanderqueen.screens.gameplay.level.TileHandler;
import no.sandramoen.commanderqueen.screens.gameplay.level.UIHandler;

public class LevelScreen extends BaseScreen3D {
    private HUD hud;
    private Player player;
    private WeaponHandler weaponHandler;
    private MapLoader mapLoader;
    private TilemapActor tilemap;

    private Array<Tile> tiles;
    private Array<Door> doors;
    private Array<Enemy> enemies;
    private Array<Pickup> originalPickups;
    private Array<Pickup> newPickups;
    private Array<BaseActor3D> shootable;
    private Array<BaseActor3D> projectiles;

    private boolean isGameOver;
    private boolean holdingDown;

    private UIHandler uiHandler;
    private BulletDecals bulletDecals;
    private BloodDecals bloodDecals;

    private int numEnemies;
    private int numPickups;

    public static int numSecrets;
    public static int foundSecrets;
    private float totalTime;

    private float PAR_TIME;
    private TiledMap tiledMap;
    private String numLevel;

    public LevelScreen(float parTime, TiledMap map, String numLevel, int health, int armor, int bullets, int shells, Array<Weapon> weapons) {
        long startTime = System.currentTimeMillis();
        this.numLevel = numLevel;
        numSecrets = 0;
        foundSecrets = 0;

        PAR_TIME = parTime;
        tiledMap = map;

        /*GameUtils.playLoopingMusic(BaseGame.level0Music);*/
        GameUtils.playLoopingMusic(BaseGame.metalWalkingMusic, 0);
        initializeMap(health, armor, bullets, shells);
        initializePlayer(weapons);
        uiHandler = new UIHandler(uiTable, enemies, hud);
        hud.setAmmo(weaponHandler.currentWeapon);
        bulletDecals = new BulletDecals(mainStage3D.camera, decalBatch);
        bloodDecals = new BloodDecals(mainStage3D.camera, decalBatch);

        if (!Gdx.input.isCursorCatched())
            Gdx.input.setCursorCatched(true);

        numEnemies = enemies.size;
        numPickups = originalPickups.size;

        GameUtils.printLoadingTime(getClass().getSimpleName(), "Level", startTime);
    }

    @Override
    public void initialize() {
    }

    @Override
    public void update(float dt) {
        totalTime += dt;
        checkGameOverCondition();

        TileHandler.updateTiles(dt, tiles, player);
        EnemyHandler.update(mainStage3D.intervalFlag, enemies, tiles, doors, projectiles, player, shootable, hud);
        for (int i = 0; i < enemies.size; i++)
            if (enemies.get(i).isDead) removeEnemy(enemies.get(i));
        updateBarrels();
        PickupHandler.update(originalPickups, player, hud, weaponHandler, uiTable, uiHandler, mainStage3D, tiles);
        PickupHandler.update(newPickups, player, hud, weaponHandler, uiTable, uiHandler, mainStage3D, tiles);

        updateUI();
        for (Door door : doors)
            player.preventOverlap(door);
        for (Elevator elevator : mapLoader.elevators)
            player.preventOverlap(elevator);

        buttonPolling();

        bulletDecals.render(dt);
        bloodDecals.render(dt);
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Keys.ESCAPE || keycode == Keys.Q) {
            stopLevel();
            BaseGame.setActiveScreen(new MenuScreen());
        } else if (keycode == Keys.R)
            BaseGame.setActiveScreen(new LevelScreen(PAR_TIME, BaseGame.testMap, "test", 100, 0, 50, 50, null));
        else if (keycode == Keys.Q)
            hud.setInvulnerable();
        else if (keycode == Keys.F) {
            player.isCollisionEnabled = !player.isCollisionEnabled;
            Gdx.app.log(getClass().getSimpleName(), "player.isCollisionEnabled: " + player.isCollisionEnabled);
        } else if (keycode == Keys.V)
            hud.setInvulnerable();
        else if (keycode == Keys.G) {
            for (Tile tile : tiles)
                tile.isVisible = !tile.isVisible;
        }
        // ------------------------------------------
        else if (keycode == Keys.NUM_1) {
            weaponHandler.setWeapon(0);
            hud.setAmmo(weaponHandler.currentWeapon);
        } else if (keycode == Keys.NUM_2) {
            weaponHandler.setWeapon(1);
            hud.setAmmo(weaponHandler.currentWeapon);
        } else if (keycode == Keys.NUM_3) {
            weaponHandler.setWeapon(2);
            hud.setAmmo(weaponHandler.currentWeapon);
        } else if (keycode == Keys.SPACE) {
            for (Door door : doors)
                if (player.isWithinDistance(Tile.height * .8f, door))
                    door.openAndClose();
            for (Elevator elevator : mapLoader.elevators)
                if (player.isWithinDistance(Tile.height * 1.1f, elevator))
                    levelFinished();
        }

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

    @Override
    public boolean scrolled(float amountX, float amountY) {
        weaponHandler.scrollWeapon(amountY);
        hud.setAmmo(weaponHandler.currentWeapon);
        return super.scrolled(amountX, amountY);
    }

    private void buttonPolling() {
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && !isGameOver) {
            if (weaponHandler.isReady)
                shoot();
            holdingDown = true;
        }
    }

    private void shoot() {
        if (!weaponHandler.currentWeapon.isMelee) {
            weaponHandler.shoot(hud.getAmmo(weaponHandler.currentWeapon));
            if (weaponHandler.currentWeapon.isAmmoDependent && hud.getAmmo(weaponHandler.currentWeapon) > 0) {
                if (hud.getAmmo(weaponHandler.currentWeapon) > 0) {
                    player.muzzleLight();
                    hud.decrementAmmo(weaponHandler.currentWeapon);
                    for (int i = 0; i < weaponHandler.currentWeapon.numShotsFired; i++)
                        rayPickTarget();
                    EnemyHandler.activateEnemies(enemies, Enemy.activationRange, player);
                }
            }
        } else {
            weaponHandler.melee(rayPickTarget());
        }
    }

    private boolean rayPickTarget() {
        Vector2 spread = weaponHandler.getSpread(holdingDown, mainStage3D.camera.fieldOfView);
        int screenX = (int) (Gdx.graphics.getWidth() / 2 + MathUtils.random(-spread.x, spread.x));
        int screenY = (int) (Gdx.graphics.getHeight() / 2 + MathUtils.random(-spread.y, spread.y));

        Ray ray = mainStage3D.camera.getPickRay(screenX, screenY);
        int i = GameUtils.getClosestListIndex(ray, shootable);

        return consequencesOfPick(ray, i);
    }

    private boolean consequencesOfPick(Ray ray, int i) {
        if (i >= 0) {
            if (player.distanceBetween(shootable.get(i)) <= weaponHandler.currentWeapon.range) {
                if (shootable.get(i) instanceof Enemy) {
                    Enemy enemy = (Enemy) shootable.get(i);
                    EnemyHandler.activateEnemies(enemies, Enemy.activationRange, player);
                    enemy.decrementHealth(weaponHandler.getDamage());
                    if (enemy.isDead) removeEnemy(enemy);

                    Vector3 temp = new Vector3().set(ray.direction).scl(player.distanceBetween(enemy) - .2f).add(ray.origin);
                    bloodDecals.addDecal(temp.x, temp.y, temp.z);
                } else if (shootable.get(i) instanceof Barrel) {
                    Barrel barrel = (Barrel) shootable.get(i);
                    barrel.decrementHealth(weaponHandler.getDamage(), player.distanceBetween(barrel));
                } else if (!weaponHandler.currentWeapon.isMelee) {
                    Vector3 temp = new Vector3().set(ray.direction).scl(player.distanceBetween(shootable.get(i)) - (Tile.diagonalLength / 2)).add(ray.origin);
                    bulletDecals.addDecal(temp.x, temp.y, temp.z);
                }
                return true;
            }
        }
        return false;
    }

    private void removeEnemy(Enemy enemy) {
        enemy.die();
        if (enemy instanceof Menig)
            newPickups.add(new Bullets(enemy.position.y + MathUtils.random(-1, 1), enemy.position.z + MathUtils.random(-1, 1), mainStage3D, 2, player, tiles));
        if (enemy instanceof Sersjant)
            newPickups.add(new Shells(enemy.position.y + MathUtils.random(-1, 1), enemy.position.z + MathUtils.random(-1, 1), mainStage3D, 2, player, tiles));
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
            player.isPause = true;
            hud.setDeadFace();
            weaponHandler.playerDied();
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
        }
    }


    private void updateBarrels() {
        for (BaseActor3D baseActor3D : shootable) {
            if (baseActor3D instanceof Barrel) {
                Barrel barrel = (Barrel) baseActor3D;
                if (barrel.health <= 0)
                    explodeBarrelWithDelay(barrel);
            }
        }
    }

    private void updateUI() {
        if (uiHandler.isReset) {
            uiHandler = new UIHandler(uiTable, enemies, hud);
            uiHandler.isReset = false;
        }
        uiHandler.debugLabel.setText("FPS: " + Gdx.graphics.getFramesPerSecond() + "\nVisible: " + mainStage3D.visibleCount);
    }

    private void explodeBarrelWithDelay(final Barrel barrel) {
        new BaseActor(0, 0, uiStage).addAction(Actions.sequence(
                Actions.delay(MathUtils.random(0, .4f)),
                Actions.run(() -> explodeBarrel(barrel))
        ));
    }

    private void explodeBarrel(Barrel barrel) {
        EnemyHandler.activateEnemies(enemies, Enemy.activationRange, barrel);
        shootable.removeValue(barrel, false);
        EnemyHandler.updateEnemiesShootableList(enemies, shootable);
        BarrelExplosionHandler.checkExplosionRange(hud, player, enemies, shootable, barrel);
        barrel.explode();
    }


    private void initializeMap(int health, int armor, int bullets, int shells) {
        tilemap = new TilemapActor(tiledMap, mainStage3D);
        tiles = new Array();
        shootable = new Array();
        originalPickups = new Array();
        newPickups = new Array();
        enemies = new Array();
        doors = new Array();
        projectiles = new Array();
        hud = new HUD(uiStage, health, armor, bullets, shells);
        mapLoader = new MapLoader(tilemap, tiles, mainStage3D, player, shootable, originalPickups, enemies, uiStage, hud, decalBatch, doors, projectiles);
    }

    private void initializePlayer(Array<Weapon> weapons) {
        player = mapLoader.player;
        hud.player = player;
        weaponHandler = new WeaponHandler(uiStage, hud, player, shootable, mainStage3D);
        hud.setWeaponsTable(weaponHandler);
        if (weapons != null)
            for (Weapon weapon : weapons)
                if (weapon.isAvailable)
                    weaponHandler.makeAvailable(weapon.getClass().getSimpleName());
    }

    private void stopLevel() {
        BaseGame.metalWalkingMusic.stop();
        BaseGame.level0Music.stop();
    }

    private void levelFinished() {
        BaseGame.elevatorSound.play(BaseGame.soundVolume);
        stopLevel();
        Array levelData = new Array();
        if (numLevel.equalsIgnoreCase("test"))
            levelData.add("Test");
        else if (numLevel.equalsIgnoreCase("level 0"))
            levelData.add("Hangar");
        levelData.add((int) ((1 - (enemies.size / (float) numEnemies)) * 100));
        levelData.add((int) ((1 - (originalPickups.size / (float) numPickups)) * 100));
        levelData.add((int) ((foundSecrets / (float) numSecrets) * 100));
        levelData.add(totalTime);
        levelData.add(PAR_TIME);

        BaseGame.setActiveScreen(new LevelFinishScreen(levelData, numLevel, hud.health, hud.armor, hud.bullets, hud.shells, weaponHandler.weapons));
    }
}
