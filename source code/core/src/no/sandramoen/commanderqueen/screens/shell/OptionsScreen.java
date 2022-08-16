package no.sandramoen.commanderqueen.screens.shell;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.github.tommyettinger.textra.TypingLabel;

import no.sandramoen.commanderqueen.ui.BaseSlider;
import no.sandramoen.commanderqueen.utils.BaseGame;
import no.sandramoen.commanderqueen.utils.BaseScreen;
import no.sandramoen.commanderqueen.utils.GameUtils;

public class OptionsScreen extends BaseScreen {
    private BaseSlider soundSlider;
    private BaseSlider musicSlider;
    private BaseSlider voiceSlider;
    private BaseSlider mouseSensitivitySlider;

    @Override
    public void initialize() {
        TypingLabel label = new TypingLabel("Options", new Label.LabelStyle(BaseGame.mySkin.get("arcade64", BitmapFont.class), null));
        uiTable.add(label)
                .growY()
                .row();

        Table optionsTable = new Table();
        soundSlider = new BaseSlider("Sound", 0, 1, .1f);
        musicSlider = new BaseSlider("Music", 0, 1, .1f);
        voiceSlider = new BaseSlider("Voice", 0, 1, .1f);
        mouseSensitivitySlider = new BaseSlider("Mouse Sensitivity", 0, .1f, .01f);

        optionsTable.defaults().spaceTop(Gdx.graphics.getHeight() * .05f).width(Gdx.graphics.getWidth() * .6f);
        optionsTable.add(soundSlider).row();
        optionsTable.add(musicSlider).row();
        optionsTable.add(voiceSlider).row();
        optionsTable.add(mouseSensitivitySlider).row();
        uiTable.add(optionsTable)
                .growY()
                .row();
        /*optionsTable.setDebug(true);*/

        uiTable.add(initializeBackButton()).expandY().width(Gdx.graphics.getWidth() * .125f).height(Gdx.graphics.getHeight() * .075f);

        /*uiTable.setDebug(true);*/
    }

    @Override
    public void update(float delta) {

    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.ESCAPE || keycode == Input.Keys.Q)
            BaseGame.setActiveScreen(new MenuScreen());
        return super.keyDown(keycode);
    }

    private TextButton initializeBackButton() {
        TextButton backButton = new TextButton("Back", BaseGame.mySkin);
        backButton.addListener(
                (Event event) -> {
                    if (GameUtils.isTouchDownEvent(event))
                        BaseGame.setActiveScreen(new MenuScreen());
                    return false;
                }
        );
        return backButton;
    }
}
