package no.sandramoen.commanderqueen.actors.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;

import no.sandramoen.commanderqueen.actors.utils.baseActors.BaseActor;

public class Crosshair extends BaseActor {
    public Crosshair(Stage stage) {
        super(0, 0, stage);
        loadImage("crosshair");
        centerAtPosition(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f);
        stage.addActor(this);
    }
}
