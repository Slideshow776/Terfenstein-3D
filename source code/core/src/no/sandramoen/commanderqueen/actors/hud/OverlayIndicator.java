package no.sandramoen.commanderqueen.actors.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import no.sandramoen.commanderqueen.actors.utils.BaseActor;

public class OverlayIndicator extends BaseActor {
    private final float MAX_ALPHA = .2f;
    private final float FADE_IN_AND_OUT_DURATION = .25f;

    public OverlayIndicator(Stage stage) {
        super(0, 0, stage);
        loadImage("whitePixel");
        setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        setOpacity(0f);
        setZIndex(0);
    }

    public void flash(Color color) {
        setColor(color);
        addAction(fadeInAndOut(MAX_ALPHA));
    }

    public void flash(Color color, float alpha) {
        setColor(color);
        addAction(fadeInAndOut(alpha));
    }

    private SequenceAction fadeInAndOut(float maxAlpha) {
        setOpacity(0f);
        return Actions.sequence(
                Actions.alpha(0),
                Actions.alpha(maxAlpha, FADE_IN_AND_OUT_DURATION / 2),
                Actions.alpha(0f, FADE_IN_AND_OUT_DURATION / 2)
        );
    }
}
