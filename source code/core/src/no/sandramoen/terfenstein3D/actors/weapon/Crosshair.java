package no.sandramoen.terfenstein3D.actors.weapon;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;

import no.sandramoen.terfenstein3D.actors.Barrel;
import no.sandramoen.terfenstein3D.actors.characters.Menig;
import no.sandramoen.terfenstein3D.actors.utils.baseActors.BaseActor;
import no.sandramoen.terfenstein3D.actors.utils.baseActors.BaseActor3D;
import no.sandramoen.terfenstein3D.utils.BaseGame;

public class Crosshair extends BaseActor {
    public Crosshair(Stage stage) {
        super(0, 0, stage);
        loadImage("crosshair");
        centerAtPosition(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f);
        stage.addActor(this);
    }

    public void setColorIfShootable(Array<BaseActor3D> shootable, int index) {
        if (index >= 0) {
            if (shootable.get(index) instanceof Menig || shootable.get(index) instanceof Barrel)
                setColor(BaseGame.redColor);
            else
                setColor(Color.WHITE);
        } else
            setColor(Color.WHITE);
    }
}
