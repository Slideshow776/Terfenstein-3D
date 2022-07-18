package no.sandramoen.commanderqueen.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;

import no.sandramoen.commanderqueen.actors.Ghoul;
import no.sandramoen.commanderqueen.actors.Player;
import no.sandramoen.commanderqueen.actors.Tile;
import no.sandramoen.commanderqueen.actors.Weapon;
import no.sandramoen.commanderqueen.utils.BaseGame;
import no.sandramoen.commanderqueen.utils.BaseScreen3D;

public class LevelScreen extends BaseScreen3D {
    private Player player;
    private Weapon weapon;
    private Array<Tile> tiles;
    private Label debugLabel;

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
        debugLabel.setText("FPS: " + Gdx.graphics.getFramesPerSecond() + "\nVisible: " + mainStage3D.visibleCount);
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
            Gdx.input.setCursorCatched(true);
        }
        return super.touchDown(screenX, screenY, pointer, button);
    }

    private void initializeActors() {
        tiles = new Array();
        tiles.add(new Tile(-5f, -5f, mainStage3D));
        tiles.add(new Tile(5f, -5f, mainStage3D));
        tiles.add(new Tile(-5f, 5f, mainStage3D));
        tiles.add(new Tile(5f, 5f, mainStage3D));

        player = new Player(0, 10f, mainStage3D);
        weapon = new Weapon(uiStage);
        new Ghoul(0f, 0f, mainStage3D, player);
    }

    private void initializeUI() {
        debugLabel = new Label(" ", BaseGame.label26Style);
        uiTable.add(debugLabel).expand().top().left().padTop(Gdx.graphics.getHeight() * .01f).padLeft(Gdx.graphics.getWidth() * .01f);
    }
}
