package no.sandramoen.commanderqueen.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;

import no.sandramoen.commanderqueen.actors.Barrel;
import no.sandramoen.commanderqueen.actors.ExplosionBlast;
import no.sandramoen.commanderqueen.actors.hud.HUD;
import no.sandramoen.commanderqueen.actors.characters.Ghoul;
import no.sandramoen.commanderqueen.actors.characters.Player;
import no.sandramoen.commanderqueen.actors.Tile;
import no.sandramoen.commanderqueen.actors.Weapon;
import no.sandramoen.commanderqueen.actors.pickups.Ammo;
import no.sandramoen.commanderqueen.actors.pickups.Armor;
import no.sandramoen.commanderqueen.actors.pickups.Health;
import no.sandramoen.commanderqueen.actors.pickups.Pickup;
import no.sandramoen.commanderqueen.actors.utils.BaseActor3D;
import no.sandramoen.commanderqueen.actors.utils.Enemy;
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
    private Array<ExplosionBlast> explosionBlasts;

    private Label debugLabel;
    private Label gameLabel;
    private Label statusLabel;

    private Array<BaseActor3D> shootable;
    private Vector3 position = new Vector3();
    private boolean isGameOver = false;
    private TilemapActor tilemap;

    public void initialize() {
        GameUtils.playLoopingMusic(BaseGame.level0Music);
        GameUtils.playLoopingMusic(BaseGame.metalWalkingMusic, 0);
        pickups = new Array();
        shootable = new Array();
        explosionBlasts = new Array();
        tilemap = new TilemapActor(BaseGame.level0Map, mainStage3D);
        initializeActors();
        initializeUI();
    }

    public void update(float dt) {
        if (isGameOver) return;
        updateTiles();
        updateEnemies();
        updatePickups();
        weapon.sway(player.isMoving);

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
        return super.keyDown(keycode);
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
                shootable.removeValue(ghoul, false);
                enemies.removeValue(ghoul, false);
                statusLabel.setText("enemies left: " + enemies.size);
                hud.incrementScore(10);
                hud.face.happy();
            } else if (shootable.get(index).getClass().getSimpleName().equals("Barrel")) {
                Barrel barrel = (Barrel) shootable.get(index);
                barrel.explode();
                explosionBlasts.add(new ExplosionBlast(barrel.position.y, barrel.position.z, 20, mainStage3D));
                shootable.removeIndex(index);
                hud.face.happy();
            }
        }
    }

    private void updateTiles() {
        for (Tile tile : tiles) {
            if (player.overlaps(tile)) {
                player.preventOverlap(tile);
            }
        }
    }

    private void updateEnemies() {
        for (ExplosionBlast explosionBlast : explosionBlasts) {
            if (explosionBlast.overlaps(player)) {
                explosionPushBack(player, explosionBlast);
                if (hud.decrementHealth(50) <= 0)
                    gameOver();
            }
            for (Enemy enemy : enemies) {
                if (explosionBlast.overlaps(enemy)) {
                    explosionPushBack(enemy, explosionBlast);
                    enemy.die();
                    enemies.removeValue(enemy, false);
                    statusLabel.setText("enemies left: " + enemies.size);
                }
            }
            explosionBlasts.removeValue(explosionBlast, false);
            explosionBlast.remove();
        }

        for (Enemy enemy : enemies) {
            if (player.overlaps(enemy)) {
                player.preventOverlap(enemy);
                gameOver();
                break;
            }

            for (Tile tile : tiles) {
                if (enemy.overlaps(tile))
                    enemy.preventOverlap(tile);
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

    private void gameOver() {
        if (!isGameOver) {
            gameLabel.setText("G A M E   O V E R !");
            isGameOver = true;
            player.isPause = true;
            for (Enemy enemy : enemies)
                enemy.isPause = true;
        }
    }

    private void explosionPushBack(BaseActor3D baseActor3D, BaseActor3D explosion) {
        float moveY = baseActor3D.position.y - explosion.position.y;
        float moveZ = baseActor3D.position.z - explosion.position.z;
        baseActor3D.moveBy(0f, moveY * .5f, moveZ * .5f);
    }

    private void initializeActors() {
        hud = new HUD(uiStage);
        weapon = new Weapon(uiStage);
        initializeTiles();
        initializePlayer();
        initializeEnemies();
    }

    private void initializeTiles() {
        tiles = new Array();
        for (MapObject obj : tilemap.getTileList("wall")) {
            MapProperties props = obj.getProperties();
            float x = (Float) props.get("x") * BaseGame.unitScale;
            float y = (Float) props.get("y") * BaseGame.unitScale;
            Tile tile = new Tile(x, y, mainStage3D);
            tile.loadImage("tiles/research0");
            tiles.add(tile);
            shootable.add(tiles.get(tiles.size - 1));
        }
        for (MapObject obj : tilemap.getTileList("floor")) {
            MapProperties props = obj.getProperties();
            float x = (Float) props.get("x") * BaseGame.unitScale;
            float y = (Float) props.get("y") * BaseGame.unitScale;
            Tile tile = new Tile(-Tile.height, x, y, mainStage3D);
            tile.loadImage("tiles/floor0");
            tiles.add(tile);
            tile.isCollisionEnabled = false;
        }
        for (MapObject obj : tilemap.getTileList("ceiling")) {
            MapProperties props = obj.getProperties();
            float x = (Float) props.get("x") * BaseGame.unitScale;
            float y = (Float) props.get("y") * BaseGame.unitScale;
            Tile tile = new Tile(Tile.height, x, y, mainStage3D);
            tile.loadImage("tiles/floor0");
            tiles.add(tile);
            tile.isCollisionEnabled = false;
        }
    }

    private void initializePlayer() {
        MapObject startPoint = tilemap.getTileList("player start").get(0);
        float playerX = (float) startPoint.getProperties().get("x") * BaseGame.unitScale;
        float playerY = (float) startPoint.getProperties().get("y") * BaseGame.unitScale;
        player = new Player(playerX, playerY, mainStage3D);

        pickups.add(new Armor(playerY, playerX + 1, mainStage3D, player));
        pickups.add(new Health(playerY - 1, playerX, mainStage3D, player));

        shootable.add(new Barrel(playerX + 5, playerY, mainStage3D, player));
    }

    private void initializeEnemies() {
        enemies = new Array();
        for (MapObject obj : tilemap.getTileList("enemy")) {
            MapProperties props = obj.getProperties();
            float x = (Float) props.get("x") * BaseGame.unitScale;
            float y = (Float) props.get("y") * BaseGame.unitScale;
            enemies.add(new Ghoul(x, y, mainStage3D, player));
            shootable.add(enemies.get(enemies.size - 1));
        }
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
