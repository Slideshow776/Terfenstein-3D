package no.sandramoen.commanderqueen.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.github.tommyettinger.textra.TypingLabel;

import no.sandramoen.commanderqueen.utils.BaseGame;
import no.sandramoen.commanderqueen.utils.GameUtils;

public class BaseSlider extends Table {
    private TypingLabel label;
    private Slider slider;

    public BaseSlider(String labelText, float min, float max, float stepSize) {
        label = new TypingLabel(labelText, new Label.LabelStyle(BaseGame.mySkin.get("arcade26", BitmapFont.class), null));
        setWidgetHoverColor(label);
        add(label).growX().left().padRight(Gdx.graphics.getWidth() * .05f);

        slider = initializeSlider(labelText, min, max, stepSize);
        Container container = initializeContainer(slider, label);

        add(container).growX().right().width(container.getWidth()).height(container.getHeight());

        /*setDebug(true);*/
    }

    private Slider initializeSlider(String value, float min, float max, float stepSize) {
        Slider slider = new Slider(min, max, stepSize, false, BaseGame.mySkin);
        setValue(slider, value);
        addListener(slider, value, min, max);
        return slider;
    }

    private void setValue(Slider slider, String value) {
        if (value.equalsIgnoreCase("sound"))
            slider.setValue(BaseGame.soundVolume);
        else if (value.equalsIgnoreCase("music"))
            slider.setValue(BaseGame.musicVolume);
        else if (value.equalsIgnoreCase("voice"))
            slider.setValue(BaseGame.voiceVolume);
        else if (value.equalsIgnoreCase("mouse sensitivity"))
            slider.setValue(BaseGame.mouseMovementSensitivity);
        else
            Gdx.app.error(getClass().getSimpleName(), "Error: setValue(" + value + ") failed!");
    }

    private void addListener(Slider slider, String value, float min, float max) {
        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                System.out.println(GameUtils.normalizeValue(slider.getValue(), min, max) + ", value: " + slider.getValue() + ", min: " + min + ", max: " + max);
                BaseGame.click1Sound.play(GameUtils.normalizeValue(slider.getValue(), min, max));
                if (value.equalsIgnoreCase("sound"))
                    BaseGame.soundVolume = slider.getValue();
                else if (value.equalsIgnoreCase("music"))
                    BaseGame.musicVolume = slider.getValue();
                else if (value.equalsIgnoreCase("voice"))
                    BaseGame.voiceVolume = slider.getValue();
                else if (value.equalsIgnoreCase("mouse sensitivity"))
                    BaseGame.mouseMovementSensitivity = slider.getValue();
                /*GameUtils.saveGameState(); // TODO*/
            }
        });
    }

    private Container initializeContainer(Slider slider, TypingLabel label) {
        float containerWidth = Gdx.graphics.getWidth() * .4f;
        float containerHeight = Gdx.graphics.getHeight() * .02f;
        float containerScaleX = .8f;
        float containerScaleY = .8f;

        Container container = new Container(slider);
        container.fill();
        container.setTransform(true);
        container.setWidth(containerWidth * containerScaleX);
        container.setHeight(containerHeight * containerScaleY);
        container.setOrigin(container.getWidth() / 2, container.getHeight() / 2);
        container.setScale(containerScaleX, containerScaleY);
        setContainerHoverColor(container, label);

        return container;
    }

    private void setContainerHoverColor(Container container, TypingLabel label) {
        container.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                super.enter(event, x, y, pointer, fromActor);
                label.setColor(BaseGame.redColor);
                BaseGame.hoverOverEnterSound.play(BaseGame.soundVolume);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                super.exit(event, x, y, pointer, toActor);
                label.setColor(BaseGame.whiteColor);
            }
        });
    }

    private void setWidgetHoverColor(Widget widget) {
        widget.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                super.enter(event, x, y, pointer, fromActor);
                widget.setColor(BaseGame.redColor);
                BaseGame.hoverOverEnterSound.play(BaseGame.soundVolume);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                super.exit(event, x, y, pointer, toActor);
                widget.setColor(BaseGame.whiteColor);
            }
        });
    }
}
