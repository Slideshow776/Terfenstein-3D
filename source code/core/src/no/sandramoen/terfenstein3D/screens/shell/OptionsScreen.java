package no.sandramoen.terfenstein3D.screens.shell;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.github.tommyettinger.textra.TypingLabel;

import no.sandramoen.terfenstein3D.ui.BaseCheckbox;
import no.sandramoen.terfenstein3D.ui.BaseSlider;
import no.sandramoen.terfenstein3D.utils.BaseGame;
import no.sandramoen.terfenstein3D.utils.BaseScreen;
import no.sandramoen.terfenstein3D.utils.GameUtils;

public class OptionsScreen extends BaseScreen {

    @Override
    public void initialize() {
        TypingLabel label = new TypingLabel("Options", new Label.LabelStyle(BaseGame.mySkin.get("arcade64", BitmapFont.class), null));
        label.font.scale(.8f, .8f);
        uiTable.add(label)
                .growY()
                .padBottom(-Gdx.graphics.getHeight() * .15f)
                .row();

        uiTable.add(optionsTable())
                .growY()
                .row();

        uiTable.add(initializeBackButton())
                .expandY()
                .width(Gdx.graphics.getWidth() * .125f)
                .height(Gdx.graphics.getHeight() * .075f);

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

    private Table optionsTable() {
        Table table = new Table();

        BaseSlider soundSlider = new BaseSlider("Sound", 0, 1, .1f);
        BaseSlider musicSlider = new BaseSlider("Music", 0, 1, .1f);
        BaseSlider voiceSlider = new BaseSlider("Voice", 0, 1, .1f);
        BaseSlider mouseSensitivitySlider = new BaseSlider("Mouse Sensitivity", .01f, .1f, .01f);
        BaseCheckbox headBobCheckBox = new BaseCheckbox("Head Bob");
        headBobCheckBox.checkBox.setChecked(BaseGame.isHeadBobbing);

        table.defaults().spaceTop(Gdx.graphics.getHeight() * .05f).width(Gdx.graphics.getWidth() * .6f);
        table.add(soundSlider).row();
        table.add(musicSlider).row();
        table.add(voiceSlider).row();
        table.add(mouseSensitivitySlider).row();
        table.add(headBobCheckBox);

        /*table.setDebug(true);*/
        return table;
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
