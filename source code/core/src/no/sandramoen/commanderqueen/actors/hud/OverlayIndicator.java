package no.sandramoen.commanderqueen.actors.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

import no.sandramoen.commanderqueen.actors.utils.BaseActor;

public class OverlayIndicator extends BaseActor {
    public OverlayIndicator(Stage stage) {
        super(0, 0, stage);
        loadImage("whitePixel");
        setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        setOpacity(0f);
        setZIndex(0);
    }

    public void flash(Color color) {
        setColor(color);
        actionAlpha(.2f);
    }

    public void flash(Color color, float alpha) {
        setColor(color);
        actionAlpha(alpha);
    }

    private void actionAlpha(float maxAlpha) {
        setOpacity(0f);
        float duration = .25f;
        addAction(Actions.sequence(
                Actions.alpha(0),
                Actions.alpha(maxAlpha, duration / 2),
                Actions.alpha(0f, duration / 2)
        ));
    }
}
