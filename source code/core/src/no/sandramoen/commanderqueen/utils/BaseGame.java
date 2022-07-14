package no.sandramoen.commanderqueen.utils;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;

public abstract class BaseGame extends Game {

    private static BaseGame game;
    public static float mouseMovementSensitivity = .05f;

    public BaseGame() {
        game = this;
    }

    public void create() {
        InputMultiplexer im = new InputMultiplexer();
        Gdx.input.setInputProcessor(im);
    }

    public static void setActiveScreen(BaseScreen s) {
        game.setScreen(s);
    }
}
