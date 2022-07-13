package no.sandramoen.commanderqueen.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;

import no.sandramoen.commanderqueen.utils.BaseScreen;

public class LevelScreen extends BaseScreen {
    @Override
    public void initialize() {

    }

    @Override
    public void update(float dt) {

    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Keys.Q)
            Gdx.app.exit();
        return super.keyDown(keycode);
    }
}
