package no.sandramoen.commanderqueen.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.MathUtils;
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
import no.sandramoen.commanderqueen.utils.BaseGame;
import no.sandramoen.commanderqueen.utils.BaseScreen3D;

public class LevelScreen extends BaseScreen3D {
    private Player player;
    private Weapon weapon;
    private Array<Tile> tiles;
    private Array<Enemy> enemies;
    private Label debugLabel;

    private Array<BaseActor3D> shootable;
    private Vector3 position = new Vector3();

    public void initialize() {
        initializeActors();
        initializeUI();
    }

    public void update(float dt) {
        for (Tile tile : tiles) {
            if (player.overlaps(tile)) {
                player.preventOverlap(tile);
            }
        }
        for (Enemy enemy : enemies) {
            if (player.overlaps(enemy)) {
                player.preventOverlap(enemy);
            }
        }
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
        if (keycode == Keys.SPACE) {
            BaseGame.pistolShotSound.play(BaseGame.soundVolume, MathUtils.random(.9f, 1.1f), 0f);
            weapon.shoot();
        }
        return super.keyDown(keycode);
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.LEFT) {
            BaseGame.pistolShotSound.play(BaseGame.soundVolume, MathUtils.random(.9f, 1.1f), 0f);

            weapon.shoot();

            int rayPickedIndex = rayPickShootableObject(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
            if (rayPickedIndex > 0) {
                if (shootable.get(rayPickedIndex).getClass().getSimpleName().equals("Ghoul")) {
                    Ghoul ghoul = (Ghoul) shootable.get(rayPickedIndex);
                    ghoul.die();
                }
            }
        }
        return super.touchDown(screenX, screenY, pointer, button);
    }


    public int rayPickShootableObject(int screenX, int screenY) {
        Ray ray = mainStage3D.camera.getPickRay(screenX, screenY);

        int result = -1;
        float distance = -1;

        for (int i = 0; i < shootable.size; ++i) {
            final BaseActor3D.GameObject instance = shootable.get(i).modelData;

            instance.transform.getTranslation(position);
            position.add(instance.center);

            final float len = ray.direction.dot(position.x - ray.origin.x, position.y - ray.origin.y, position.z - ray.origin.z);
            if (len < 0f)
                continue;

            float dist2 = position.dst2(ray.origin.x + ray.direction.x * len, ray.origin.y + ray.direction.y * len, ray.origin.z + ray.direction.z * len);
            if (distance >= 0f && dist2 > distance)
                continue;

            if (dist2 <= instance.radius * instance.radius) {
                result = i;
                distance = dist2;
            }
        }
        return result;
    }

    private void initializeActors() {
        shootable = new Array();

        tiles = new Array();
        tiles.add(new Tile(-5f, -5f, mainStage3D));
        tiles.add(new Tile(5f, -5f, mainStage3D));
        tiles.add(new Tile(-5f, 5f, mainStage3D));
        tiles.add(new Tile(5f, 5f, mainStage3D));
        for (Tile tile : tiles)
            shootable.add(tile);

        player = new Player(0, 10f, mainStage3D);
        weapon = new Weapon(uiStage);

        enemies = new Array();
        enemies.add(new Ghoul(0f, 0f, mainStage3D, player));
        for (Enemy enemy : enemies)
            shootable.add(enemy);
    }

    private void initializeUI() {
        debugLabel = new Label(" ", BaseGame.label26Style);
        uiTable.add(debugLabel).expand().top().left().padTop(Gdx.graphics.getHeight() * .01f).padLeft(Gdx.graphics.getWidth() * .01f);
    }
}
