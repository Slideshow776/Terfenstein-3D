package no.sandramoen.commanderqueen.utils;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;

public abstract class BaseGame extends Game
{
    /**
     *  Stores reference to game; used when calling setActiveScreen method.
     */
    private static BaseGame game;

    public static LabelStyle labelStyle;

    /**
     *  Called when game is initialized; stores global reference to game object.
     */
    public BaseGame()
    {
        game = this;
    }

    /**
     *  Called when game is initialized,
     *  after Gdx.input and other objects have been initialized.
     */
    public void create()
    {
        // prepare for multiple classes/stages/actors to receive discrete input
        InputMultiplexer im = new InputMultiplexer();
        Gdx.input.setInputProcessor( im );
    }

    /**
     *  Used to switch screens while game is running.
     *  Method is static to simplify usage.
     */
    public static void setActiveScreen(BaseScreen s)
    {
        game.setScreen(s);
    }
}