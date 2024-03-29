package no.sandramoen.terfenstein3D.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.github.tommyettinger.textra.TypingLabel;

import no.sandramoen.terfenstein3D.utils.BaseGame;
import no.sandramoen.terfenstein3D.utils.GameUtils;

public class MadeByLabel extends TypingLabel {

    public MadeByLabel() {
        super("Made by Sandra Moen 2022", new Label.LabelStyle(BaseGame.mySkin.get("arcade26", BitmapFont.class), null));
        setAlignment(Align.center);
        setColor(BaseGame.grayColor);
        addClickListener();
        GameUtils.setWidgetHoverColor(this);
    }

    private void addClickListener() {
        addListener(
                (Event event) -> {
                    if (GameUtils.isTouchDownEvent(event))
                        openURIWithDelay();
                    return false;
                }
        );
    }

    private void openURIWithDelay() {
        BaseGame.click1Sound.play(BaseGame.soundVolume);
        addAction(Actions.sequence(
                Actions.delay(.5f),
                Actions.run(() -> Gdx.net.openURI("https://sandramoen.no"))
        ));
    }
}
