package no.sandramoen.terfenstein3D.screens.gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Array;

import no.sandramoen.terfenstein3D.actors.Barrel;
import no.sandramoen.terfenstein3D.actors.Door;
import no.sandramoen.terfenstein3D.actors.Elevator;
import no.sandramoen.terfenstein3D.actors.characters.Fenrik;
import no.sandramoen.terfenstein3D.actors.characters.Menig;
import no.sandramoen.terfenstein3D.actors.characters.Rocket;
import no.sandramoen.terfenstein3D.actors.characters.Sersjant;
import no.sandramoen.terfenstein3D.actors.decals.BloodDecals;
import no.sandramoen.terfenstein3D.actors.decals.BulletDecals;
import no.sandramoen.terfenstein3D.actors.hud.HUD;
import no.sandramoen.terfenstein3D.actors.characters.Player;
import no.sandramoen.terfenstein3D.actors.Tile;
import no.sandramoen.terfenstein3D.actors.pickups.Shells;
import no.sandramoen.terfenstein3D.actors.weapon.WeaponHandler;
import no.sandramoen.terfenstein3D.actors.pickups.Bullets;
import no.sandramoen.terfenstein3D.actors.pickups.Pickup;
import no.sandramoen.terfenstein3D.actors.utils.baseActors.BaseActor;
import no.sandramoen.terfenstein3D.actors.utils.baseActors.BaseActor3D;
import no.sandramoen.terfenstein3D.actors.characters.enemy.Enemy;
import no.sandramoen.terfenstein3D.actors.weapon.weapons.Chaingun;
import no.sandramoen.terfenstein3D.actors.weapon.weapons.Weapon;
import no.sandramoen.terfenstein3D.screens.gameplay.level.BarrelExplosionHandler;
import no.sandramoen.terfenstein3D.screens.gameplay.level.EnemyHandler;
import no.sandramoen.terfenstein3D.screens.gameplay.level.MapLoader;
import no.sandramoen.terfenstein3D.actors.utils.TilemapActor;
import no.sandramoen.terfenstein3D.screens.gameplay.level.TileShade;
import no.sandramoen.terfenstein3D.screens.shell.LevelFinishScreen;
import no.sandramoen.terfenstein3D.screens.shell.MenuScreen;
import no.sandramoen.terfenstein3D.utils.BaseGame;
import no.sandramoen.terfenstein3D.utils.BaseScreen3D;
import no.sandramoen.terfenstein3D.utils.GameUtils;
import no.sandramoen.terfenstein3D.screens.gameplay.level.PickupHandler;
import no.sandramoen.terfenstein3D.screens.gameplay.level.TileHandler;
import no.sandramoen.terfenstein3D.screens.gameplay.level.UIHandler;
import no.sandramoen.terfenstein3D.utils.Stage3D;

public class LevelScreen extends BaseScreen3D {
    private HUD hud;
    private Player player;
    private WeaponHandler weaponHandler;
    private MapLoader mapLoader;
    private TilemapActor tilemap;

    private Array<Tile> tiles;
    private Array<Door> doors;
    private Array<Enemy> enemies;
    private Array<Enemy> deadEnemies;
    private Array<Pickup> originalPickups;
    private Array<Pickup> newPickups;
    private Array<BaseActor3D> shootable;
    private Array<BaseActor3D> projectiles;
    private Array<TileShade> tileShades;

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

    private int startingHealth;
    private int startingArmor;
    private int startingBullets;
    private int startingShells;
    private int startingRockets;
    private Array<Weapon> startingWeapons;

    public LevelScreen(float parTime, TiledMap map, String numLevel, int health, int armor, int bullets, int shells, int rockets, Array<Weapon> weapons) {
        long startTime = System.currentTimeMillis();
        this.numLevel = numLevel;
        numSecrets = 0;
        foundSecrets = 0;

        PAR_TIME = parTime;
        tiledMap = map;


        playLevelMusic();
        GameUtils.playLoopingMusic(BaseGame.ambientFanMusic);
        BaseGame.ambientFanMusic.setPosition(MathUtils.random(0, 15));
        GameUtils.playLoopingMusic(BaseGame.metalWalkingMusic, 0);

        initializeMap(health, armor, bullets, shells, rockets);
        initializePlayer(weapons);

        uiHandler = new UIHandler(uiTable, enemies, hud);
        hud.setAmmo(weaponHandler.currentWeapon);
        bulletDecals = new BulletDecals(mainStage3D.camera, decalBatch);
        bloodDecals = new BloodDecals(mainStage3D.camera, decalBatch);

        if (!Gdx.input.isCursorCatched())
            Gdx.input.setCursorCatched(true);

        numEnemies = enemies.size;
        numPickups = originalPickups.size;
        setStartingVariables();

        GameUtils.printLoadingTime(getClass().getSimpleName(), "Level", startTime);
    }

    @Override
    public void initialize() {
    }

    @Override
    public void update(float dt) {
        totalTime += dt;
        checkGameOverCondition();

        TileHandler.updateTiles(dt, tiles, player, uiHandler);
        EnemyHandler.update(enemies, tiles, doors, projectiles, player, shootable, hud, mainStage3D);
        shadeHandler();

        for (int i = 0; i < enemies.size; i++)
            if (enemies.get(i).isDead) removeEnemy(enemies.get(i));

        updateBarrels();
        PickupHandler.update(originalPickups, player, hud, weaponHandler, uiTable, uiHandler, mainStage3D);
        PickupHandler.update(newPickups, player, hud, weaponHandler, uiTable, uiHandler, mainStage3D);

        updateUI();
        for (Door door : doors)
            player.preventOverlap(door);
        for (Elevator elevator : mapLoader.elevators)
            player.preventOverlap(elevator);

        mouseButtonPolling();

        bulletDecals.render(dt);
        bloodDecals.render(dt);
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Keys.ESCAPE || keycode == Keys.Q) {
            stopLevel();
            BaseGame.levelScreen = this;
            BaseGame.setActiveScreen(new MenuScreen());
        } else if (keycode == Keys.R)
            BaseGame.setActiveScreen(new LevelScreen(PAR_TIME, BaseGame.testMap, "test", 100, 0, 50, 50, 50, null)); else if (isGameOver && totalTime > 2)
            restartLevel();
        else if (keycode == Keys.F) {
            player.isCollisionEnabled = !player.isCollisionEnabled;
            Gdx.app.log(getClass().getSimpleName(), "player.isCollisionEnabled: " + player.isCollisionEnabled);
        } else if (keycode == Keys.V)
            hud.setInvulnerable();
        else if (keycode == Keys.B)
            hud.incrementHealth(1);
        else if (keycode == Keys.G) {
            for (Tile tile : tiles)
                tile.isVisible = !tile.isVisible;
        } else if (keycode == Keys.NUMPAD_ADD) {
            Player.movementSpeed += 1;
        } else if (keycode == Keys.NUMPAD_SUBTRACT) {
            Player.movementSpeed -= 1;
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
        } else if (keycode == Keys.NUM_4) {
            weaponHandler.setWeapon(3);
            hud.setAmmo(weaponHandler.currentWeapon);
        } else if (keycode == Keys.NUM_5) {
            weaponHandler.setWeapon(4);
            hud.setAmmo(weaponHandler.currentWeapon);
        } else if (keycode == Keys.SPACE) {
            for (Door door : doors)
                if (player.isWithinDistance(Tile.height * .8f, door)) {
                    String message = door.tryToOpenDoor(hud.keys.getKeys());
                    if (!message.isEmpty())
                        uiHandler.setPickupLabel(message, true);
                }
            for (Elevator elevator : mapLoader.elevators)
                if (player.isWithinDistance(Tile.height * 1.1f, elevator) && !elevator.activated)
                    levelFinished();
        }

        return super.keyDown(keycode);
    }

    @Override
    public void dispose() {
        mainStage3D.dispose();
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (isGameOver && totalTime > 2)
            restartLevel();
        return super.touchDown(screenX, screenY, pointer, button);
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        holdingDown = false;
        if (weaponHandler.currentWeapon instanceof Chaingun)
            BaseGame.chaingunPowerDownSound.play(BaseGame.soundVolume * .5f);
        return super.touchUp(screenX, screenY, pointer, button);
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        weaponHandler.scrollWeapon(amountY);
        hud.setAmmo(weaponHandler.currentWeapon);
        return super.scrolled(amountX, amountY);
    }

    @Override
    public void show() {
        super.show();
        if (!Gdx.input.isCursorCatched())
            Gdx.input.setCursorCatched(true);
        totalTime = 0;
        playLevelMusic();
    }

    private void playLevelMusic() {
        BaseGame.menuMusic.stop();
        BaseGame.levelFinishMusic.stop();

        if (numLevel.equalsIgnoreCase("level 1"))
            GameUtils.playLoopingMusic(BaseGame.level1Music);
        else if (numLevel.equalsIgnoreCase("level 2"))
            GameUtils.playLoopingMusic(BaseGame.level2Music);
        else if (numLevel.equalsIgnoreCase("level 3"))
            GameUtils.playLoopingMusic(BaseGame.level3Music);
        else if (numLevel.equalsIgnoreCase("level 4"))
            GameUtils.playLoopingMusic(BaseGame.level4Music, BaseGame.musicVolume * 1.5f);
        else if (numLevel.equalsIgnoreCase("level 5"))
            GameUtils.playLoopingMusic(BaseGame.level5Music, BaseGame.musicVolume * .7f);
        else if (numLevel.equalsIgnoreCase("level 6"))
            GameUtils.playLoopingMusic(BaseGame.level6Music, BaseGame.musicVolume * .5f);
    }

    private void mouseButtonPolling() {
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && !isGameOver && totalTime > 1) {
            if (weaponHandler.isReadyToShoot)
                shoot();
            holdingDown = true;
        }
    }

    private void shoot() {
        if (!weaponHandler.currentWeapon.isMelee && !weaponHandler.currentWeapon.isProjectile) {
            weaponHandler.shoot(hud.getAmmo(weaponHandler.currentWeapon));
            if (weaponHandler.currentWeapon.isAmmoDependent && hud.getAmmo(weaponHandler.currentWeapon) > 0) {
                player.muzzleLight();
                hud.decrementAmmo(weaponHandler.currentWeapon);
                EnemyHandler.activateEnemies(enemies, Enemy.activationRange, player);
                for (int i = 0; i < weaponHandler.currentWeapon.numShotsFired; i++)
                    rayPickTarget();
            }
        } else if (weaponHandler.currentWeapon.isProjectile) {
            if (weaponHandler.shoot(hud.getAmmo(weaponHandler.currentWeapon))) {
                projectiles.add(new Rocket(player.position, player.getTurnAngle(), mainStage3D, player));
                player.muzzleLight();
                hud.decrementAmmo(weaponHandler.currentWeapon);
                EnemyHandler.activateEnemies(enemies, Enemy.activationRange, player);
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
                } else {
                    Vector3 temp = new Vector3().set(ray.direction).scl(player.distanceBetween(shootable.get(i)) - (Tile.diagonalLength * .4f)).add(ray.origin);
                    bulletDecals.addDecal(temp.x, temp.y, temp.z);
                }
                return true;
            }
        }
        return false;
    }

    private void setBloodyTiles(Enemy enemy) {
        setBloodyTile(new Ray(enemy.position, new Vector3(0, 0, 1)), enemy);
        setBloodyTile(new Ray(enemy.position, new Vector3(0, 0, -1)), enemy);
        setBloodyTile(new Ray(enemy.position, new Vector3(0, 1, 0)), enemy);
        setBloodyTile(new Ray(enemy.position, new Vector3(0, -1, 0)), enemy);/*
        setBloodyTile(new Ray(enemy.position, new Vector3(1, 0, 0)), enemy);*/
        setBloodyTile(new Ray(enemy.position, new Vector3(-1, 0, 0)), enemy);
    }

    private void setBloodyTile(Ray ray, Enemy enemy) {
        int i = GameUtils.getClosestListIndex(ray, shootable);
        if (i >= 0)
            if (shootable.get(i) instanceof Tile && enemy.distanceBetween(shootable.get(i)) <= 6)
                ((Tile) shootable.get(i)).setBloody();
    }

    private void removeEnemy(Enemy enemy) {
        enemy.die();
        if (enemy instanceof Menig || enemy instanceof Fenrik)
            newPickups.add(new Bullets(enemy.position.y + MathUtils.random(-1, 1), enemy.position.z + MathUtils.random(-1, 1), mainStage3D, 4, player));
        if (enemy instanceof Sersjant)
            newPickups.add(new Shells(enemy.position.y + MathUtils.random(-1, 1), enemy.position.z + MathUtils.random(-1, 1), mainStage3D, 2, player));
        deadEnemies.add(enemy);
        enemies.removeValue(enemy, false);
        shootable.removeValue(enemy, false);
        EnemyHandler.updateEnemiesShootableList(enemies, shootable);
        EnemyHandler.updateEnemiesEnemiesList(enemies);
        hud.incrementScore(enemy.score, false);
        hud.setKillFace();
        uiHandler.statusLabel.setText("enemies left: " + enemies.size);
        setBloodyTiles(enemy);
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
            uiHandler.gameLabel.restart();
            uiHandler.gameLabel.setText("G A M E   O V E R !");
            mainStage3D.camera.position.x = -Tile.height * .48f;
            totalTime = 0;
        }

        BaseGame.chainSawAttackingMusic.stop();
        BaseGame.chainSawIdleMusic.stop();
    }

    private void setStartingVariables() {
        startingHealth = hud.health;
        startingArmor = hud.armor;
        startingBullets = hud.bullets;
        startingShells = hud.shells;
        startingRockets = hud.rockets;

        setStartingWeaponsAvailability();
    }

    public void setStartingWeaponsAvailability() {
        WeaponHandler temp = new WeaponHandler(uiStage, hud, player, shootable, mainStage3D);
        temp.pause = true;
        temp.setAnimationPaused(true);
        for (Weapon weaponA : weaponHandler.weapons)
            for (Weapon weaponB : temp.weapons)
                weaponB.isAvailable = weaponA.isAvailable;
        startingWeapons = temp.weapons;
    }

    private void restartLevel() {
        if (numLevel.equalsIgnoreCase("test"))
            BaseGame.setActiveScreen(new LevelScreen(65, BaseGame.testMap, "test", startingHealth, startingArmor, startingBullets, startingShells, startingRockets, startingWeapons));
        else if (numLevel.equalsIgnoreCase("level 1"))
            BaseGame.setActiveScreen(new LevelScreen(95, BaseGame.level1Map, "level 1", startingHealth, startingArmor, startingBullets, startingShells, startingRockets, startingWeapons));
        else if (numLevel.equalsIgnoreCase("level 2"))
            BaseGame.setActiveScreen(new LevelScreen(95, BaseGame.level2Map, "level 2", startingHealth, startingArmor, startingBullets, startingShells, startingRockets, startingWeapons));
        else if (numLevel.equalsIgnoreCase("level 3"))
            BaseGame.setActiveScreen(new LevelScreen(95, BaseGame.level3Map, "level 3", startingHealth, startingArmor, startingBullets, startingShells, startingRockets, startingWeapons));
        else if (numLevel.equalsIgnoreCase("level 4"))
            BaseGame.setActiveScreen(new LevelScreen(38, BaseGame.level4Map, "level 4", startingHealth, startingArmor, startingBullets, startingShells, startingRockets, startingWeapons));
        else if (numLevel.equalsIgnoreCase("level 5"))
            BaseGame.setActiveScreen(new LevelScreen(38, BaseGame.level5Map, "level 5", startingHealth, startingArmor, startingBullets, startingShells, startingRockets, startingWeapons));
        else if (numLevel.equalsIgnoreCase("level 6"))
            BaseGame.setActiveScreen(new LevelScreen(38, BaseGame.level6Map, "level 6", startingHealth, startingArmor, startingBullets, startingShells, startingRockets, startingWeapons));
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
            uiHandler.reset();
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


    private void initializeMap(int health, int armor, int bullets, int shells, int rockets) {
        tilemap = new TilemapActor(tiledMap, mainStage3D);
        tiles = new Array();
        shootable = new Array();
        originalPickups = new Array();
        newPickups = new Array();
        enemies = new Array();
        deadEnemies = new Array();
        doors = new Array();
        projectiles = new Array();
        tileShades = new Array();
        hud = new HUD(uiStage, health, armor, bullets, shells, rockets);
        mapLoader = new MapLoader(tilemap, tiles, mainStage3D, player, shootable, originalPickups, enemies, uiStage, hud, decalBatch, doors, projectiles, tileShades);
    }

    private void initializePlayer(Array<Weapon> weapons) {
        player = mapLoader.player;
        hud.player = player;
        weaponHandler = new WeaponHandler(uiStage, hud, player, shootable, mainStage3D);
        if (weapons != null)
            for (Weapon weapon : weapons)
                if (weapon.isAvailable)
                    weaponHandler.makeAvailable(weapon.getClass().getSimpleName());
        hud.setWeaponsTable(weaponHandler);
    }

    private void stopLevel() {
        BaseGame.metalWalkingMusic.stop();
        BaseGame.menuMusic.stop();
        BaseGame.level1Music.stop();
        BaseGame.level2Music.stop();
        BaseGame.level3Music.stop();
        BaseGame.level4Music.stop();
        BaseGame.level5Music.stop();
        BaseGame.level6Music.stop();
        BaseGame.level7Music.stop();
        BaseGame.ambientFanMusic.stop();
        BaseGame.chainSawAttackingMusic.stop();
        BaseGame.chainSawIdleMusic.stop();
    }

    private String getLevelName() {
        if (numLevel.equalsIgnoreCase("level 1"))
            return "Hangar";
        else if (numLevel.equalsIgnoreCase("level 2"))
            return "Loading Bay";
        else if (numLevel.equalsIgnoreCase("level 3"))
            return "Complex";
        else if (numLevel.equalsIgnoreCase("level 4"))
            return "Great Hall";
        else if (numLevel.equalsIgnoreCase("level 5"))
            return "Bunkers";
        else if (numLevel.equalsIgnoreCase("level 6"))
            return "Factory";
        else
            return "Test";
    }

    private Array getLevelData() {
        Array levelData = new Array();
        levelData.add(getLevelName());
        levelData.add((int) ((1 - (enemies.size / (float) numEnemies)) * 100));
        levelData.add((int) ((1 - (originalPickups.size / (float) numPickups)) * 100));
        levelData.add((int) ((foundSecrets / (float) numSecrets) * 100));
        levelData.add(totalTime);
        levelData.add(PAR_TIME);
        levelData.add(getNextLevelName());
        return levelData;
    }

    private String getNextLevelName() {
        if (numLevel.equalsIgnoreCase("level 1"))
            return "Loading Bay";
        else if (numLevel.equalsIgnoreCase("level 2"))
            return "Complex";
        else if (numLevel.equalsIgnoreCase("level 3"))
            return "Great Hall";
        else if (numLevel.equalsIgnoreCase("level 4"))
            return "Bunkers";
        else if (numLevel.equalsIgnoreCase("level 5"))
            return "Factory";
        else if (numLevel.equalsIgnoreCase("level 6"))
            return "Laboratories";
        else
            return "Test";
    }

    private void levelFinished() {
        for (int i = 0; i < mapLoader.elevators.size; i++)
            mapLoader.elevators.get(i).activate();
        BaseGame.elevatorSound.play(BaseGame.soundVolume);
        stopLevel();
        Array levelData = getLevelData();
        uiStage.addAction(Actions.sequence(
                Actions.delay(.5f),
                Actions.run(() -> BaseGame.setActiveScreen(new LevelFinishScreen(levelData, numLevel, hud.health, hud.armor, hud.bullets, hud.shells, hud.rockets, weaponHandler.weapons)))
        ));
    }

    private void shadeHandler() {
        for (BaseActor3D baseActor3D : mainStage3D.getActors3D()) {
            baseActor3D.setColor(Color.WHITE);
            for (TileShade shade : tileShades) {
                shade.setActorColor(baseActor3D);
            }
        }

        for (Enemy enemy : enemies) {
            if (!enemy.isDead) {
                enemy.setColor(Color.WHITE);
                for (TileShade shade : tileShades) {
                    shade.setActorColor(enemy);
                }
            }
        }

        for (Enemy enemy : deadEnemies) {
            enemy.setColor(Color.WHITE);
            for (TileShade shade : tileShades) {
                shade.setActorColor(enemy);
            }
        }
    }
}
