package no.sandramoen.commanderqueen.screens.shell;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.github.tommyettinger.textra.TypingLabel;

import no.sandramoen.commanderqueen.screens.gameplay.LevelScreen;
import no.sandramoen.commanderqueen.ui.MadeByLabel;
import no.sandramoen.commanderqueen.utils.BaseGame;
import no.sandramoen.commanderqueen.utils.BaseScreen;
import no.sandramoen.commanderqueen.utils.GameUtils;

public class MenuScreen extends BaseScreen {
    @Override
    public void initialize() {
        TypingLabel titleLabel = new TypingLabel("Terfenstein 3D", new Label.LabelStyle(BaseGame.mySkin.get("arcade64", BitmapFont.class), null));
        uiTable.add(titleLabel)
                .padBottom(Gdx.graphics.getHeight() * .09f)
                .row();

        addTextButtons();

        uiTable.add(new MadeByLabel()).padTop(Gdx.graphics.getHeight() * .09f);

        /*uiTable.setDebug(true);*/

        if (Gdx.input.isCursorCatched())
            Gdx.input.setCursorCatched(false);
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

    private void addTextButtons() {
        uiTable.defaults()
                .width(Gdx.graphics.getWidth() * .125f)
                .height(Gdx.graphics.getHeight() * .075f)
                .spaceTop(Gdx.graphics.getHeight() * .01f);

        uiTable.add(startButton()).row();
        uiTable.add(optionsButton()).row();
        uiTable.add(exitButton()).row();
    }

    private TextButton startButton() {
        TextButton startButton = new TextButton("Start", BaseGame.mySkin);
        startButton.addListener(
                (Event event) -> {
                    if (GameUtils.isTouchDownEvent(event))
                        BaseGame.setActiveScreen(new LevelScreen(40, BaseGame.level1Map, "level 1", 100, 0, 10, 0, null));
                    return false;
                }
        );
        return startButton;
    }

    private TextButton optionsButton() {
        TextButton optionsButton = new TextButton("Options", BaseGame.mySkin);
        optionsButton.addListener(
                (Event event) -> {
                    if (GameUtils.isTouchDownEvent(event))
                        BaseGame.setActiveScreen(new OptionsScreen());
                    return false;
                }
        );
        return optionsButton;
    }

    private TextButton exitButton() {
        TextButton exitButton = new TextButton("Exit", BaseGame.mySkin);
        exitButton.addListener(
                (Event event) -> {
                    if (GameUtils.isTouchDownEvent(event))
                        Gdx.app.exit();
                    return false;
                }
        );
        return exitButton;
    }
}
