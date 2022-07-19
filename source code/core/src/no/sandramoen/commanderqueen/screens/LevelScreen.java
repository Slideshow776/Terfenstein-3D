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

import no.sandramoen.commanderqueen.actors.Ghoul;
import no.sandramoen.commanderqueen.actors.Player;
import no.sandramoen.commanderqueen.actors.Tile;
import no.sandramoen.commanderqueen.actors.Weapon;
import no.sandramoen.commanderqueen.actors.utils.BaseActor3D;
import no.sandramoen.commanderqueen.actors.utils.Enemy;
import no.sandramoen.commanderqueen.actors.utils.TilemapActor;
import no.sandramoen.commanderqueen.utils.BaseGame;
import no.sandramoen.commanderqueen.utils.BaseScreen3D;

public class LevelScreen extends BaseScreen3D {
    private Player player;
    private Weapon weapon;
    private Array<Tile> tiles;
    private Array<Enemy> enemies;

    private Label debugLabel;
    private Label gameLabel;

    private Array<BaseActor3D> shootable;
    private Vector3 position = new Vector3();
    private boolean isGameOver = false;
    private TilemapActor tilemap;

    public void initialize() {
        /*GameUtils.playLoopingMusic(BaseGame.levelMusic0);*/
        tilemap = new TilemapActor(BaseGame.level0Map, mainStage3D);
        shootable = new Array();
        initializeActors();
        initializeUI();
    }

    public void update(float dt) {
        if (isGameOver) return;
        updateTiles();
        updateEnemies();

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
                ghoul.die();
                shootable.removeIndex(index);
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

    private void gameOver() {
        if (!isGameOver) {
            gameLabel.setText("G A M E   O V E R !");
            isGameOver = true;
            player.isPause = true;
            for (Enemy enemy : enemies)
                enemy.isPause = true;
        }
    }

    private void initializeActors() {
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
            tiles.add(new Tile(x, y, mainStage3D));
            shootable.add(tiles.get(tiles.size - 1));
        }
        for (MapObject obj : tilemap.getTileList("floor")) {
            MapProperties props = obj.getProperties();
            float x = (Float) props.get("x") * BaseGame.unitScale;
            float y = (Float) props.get("y") * BaseGame.unitScale;
            Tile tile = new Tile(-4, x, y, mainStage3D);
            tile.setColor(Color.DARK_GRAY);
            tile.isCollisionEnabled = false;
        }
    }

    private void initializePlayer() {
        MapObject startPoint = tilemap.getTileList("player start").get(0);
        float playerX = (float) startPoint.getProperties().get("x") * BaseGame.unitScale;
        float playerY = (float) startPoint.getProperties().get("y") * BaseGame.unitScale;
        player = new Player(playerX, playerY, mainStage3D);
        weapon = new Weapon(uiStage);
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
        debugLabel = new Label(" ", BaseGame.label26Style);
        uiTable.add(debugLabel)
                .expand()
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
                .top();

        /*uiTable.setDebug(true);*/
    }
}
