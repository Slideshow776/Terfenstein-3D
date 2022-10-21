package no.sandramoen.terfenstein3D.actors.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import no.sandramoen.terfenstein3D.actors.utils.baseActors.BaseActor;

public class OverlayIndicator extends BaseActor {
    private final float MAX_ALPHA = .2f;
    private final float FADE_IN_AND_OUT_DURATION = .25f;

    public OverlayIndicator(Stage stage) {
        super(0, 0, stage);
        setImage("whitePixel");
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

    public void flashBlood(Color color, float alpha) {
        setColor(color);
        addAction(fadeInAndOutBlood(alpha));
    }

    public void flashRight(Color color) {
        setImage("overlayRight_new");
        setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        flash(color);
    }

    public void flashRight(Color color, float alpha) {
        setImage("overlayRight_new");
        setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        flash(color, alpha);
    }

    public void flashLeft(Color color) {
        setImage("overlayLeft_new");
        setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        flash(color);
    }

    public void flashLeft(Color color, float alpha) {
        setImage("overlayLeft_new");
        setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        flash(color, alpha);
    }

    private void setImage(String name) {
        loadImage(name);
        setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    private SequenceAction fadeInAndOut(float maxAlpha) {
        setOpacity(0f);
        return Actions.sequence(
                Actions.alpha(0),
                Actions.alpha(maxAlpha, FADE_IN_AND_OUT_DURATION / 2),
                Actions.alpha(0f, FADE_IN_AND_OUT_DURATION / 2),
                Actions.run(() -> setImage("whitePixel"))
        );
    }

    private SequenceAction fadeInAndOutBlood(float maxAlpha) {
        setOpacity(0f);
        setImage("overlayCentert_new");
        return Actions.sequence(
                Actions.alpha(0),
                Actions.alpha(maxAlpha, FADE_IN_AND_OUT_DURATION / 2),
                Actions.alpha(0f, FADE_IN_AND_OUT_DURATION / 2),
                Actions.run(() -> setImage("whitePixel"))
        );
    }
}
