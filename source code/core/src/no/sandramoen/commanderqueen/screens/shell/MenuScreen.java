package no.sandramoen.commanderqueen.screens.shell;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.github.tommyettinger.textra.TypingLabel;

import no.sandramoen.commanderqueen.screens.gameplay.LevelScreen;
import no.sandramoen.commanderqueen.utils.BaseGame;
import no.sandramoen.commanderqueen.utils.BaseScreen;
import no.sandramoen.commanderqueen.utils.GameUtils;

public class MenuScreen extends BaseScreen {
    @Override
    public void initialize() {
        TypingLabel titleLabel = new TypingLabel("Terfenstein 3D", BaseGame.mySkin);
        uiTable.add(titleLabel)
                .row();

        TextButton startButton = new TextButton("Start", BaseGame.mySkin);
        // startButton.getLabel().setFontScale(2f);
        startButton.addListener(
                (Event event) -> {
                    if (GameUtils.isTouchDownEvent(event))
                        BaseGame.setActiveScreen(new LevelScreen());
                    return false;
                }
        );
        uiTable.add(startButton).width(Gdx.graphics.getWidth() * .1f).height(Gdx.graphics.getHeight() * .1f).padTop(Gdx.graphics.getHeight() * .1f);
    }

    @Override
    public void update(float delta) {

    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.ESCAPE || keycode == Input.Keys.Q)
            Gdx.app.exit();
        return super.keyDown(keycode);
    }
}
