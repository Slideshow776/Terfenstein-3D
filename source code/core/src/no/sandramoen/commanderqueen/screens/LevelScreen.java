package no.sandramoen.commanderqueen.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.utils.FirstPersonCameraController;
import com.badlogic.gdx.math.MathUtils;

import no.sandramoen.commanderqueen.actors.Box;
import no.sandramoen.commanderqueen.actors.Player;
import no.sandramoen.commanderqueen.ui.Weapon;
import no.sandramoen.commanderqueen.utils.BaseGame;
import no.sandramoen.commanderqueen.utils.BaseScreen;

public class LevelScreen extends BaseScreen {
    private Player player;
    private Weapon weapon;

    public void initialize() {
        Box markerO = new Box(0f, 0f, 0f, mainStage3D);
        markerO.setColor(Color.BROWN);
        markerO.loadImage("crate");
        markerO.setScale(2, 2, -2);

        player = new Player(0f, 0f, 0f, mainStage3D);
        weapon = new Weapon(uiStage);
    }

    public void update(float dt) {
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
        }
        return super.touchDown(screenX, screenY, pointer, button);
    }
}
