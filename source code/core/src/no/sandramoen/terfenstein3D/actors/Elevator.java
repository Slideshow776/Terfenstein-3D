package no.sandramoen.terfenstein3D.actors;

import com.badlogic.gdx.scenes.scene2d.Stage;

import no.sandramoen.terfenstein3D.actors.characters.Player;
import no.sandramoen.terfenstein3D.actors.utils.baseActors.BaseActor3D;
import no.sandramoen.terfenstein3D.utils.Stage3D;

public class Elevator extends BaseActor3D {
    public boolean activated;

    public Elevator(float y, float z, Stage3D stage3D, Stage stage, float rotation) {
        super(0, y, z, stage3D);

        buildModel(4, 4, 4f, false);
        loadImage("elevator");
        turnBy(-180 + rotation);
        setBaseRectangle();
    }

    public void activate() {
        activated = true;
        loadImage("elevator down");
    }
}
