package no.sandramoen.commanderqueen.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.MathUtils;

import no.sandramoen.commanderqueen.actors.Player;
import no.sandramoen.commanderqueen.actors.Tile;
import no.sandramoen.commanderqueen.actors.Weapon;
import no.sandramoen.commanderqueen.utils.BaseGame;
import no.sandramoen.commanderqueen.utils.BaseScreen3D;

public class LevelScreen3D extends BaseScreen3D {
    private Player player;
    private Weapon weapon;
    private Tile tile;

    public void initialize() {
        tile = new Tile(5f, 5f, 4f, mainStage3D);
        player = new Player(0f, 0f, mainStage3D);
        weapon = new Weapon(uiStage);
    }

    public void update(float dt) {
        if (player.overlaps(tile)) {
            player.preventOverlap(tile);
        }
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Keys.ESCAPE || keycode == Keys.Q)
            Gdx.app.exit();
        if (keycode == Keys.R)
            BaseGame.setActiveScreen(new LevelScreen3D());
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
        }
        return super.touchDown(screenX, screenY, pointer, button);
    }
}
