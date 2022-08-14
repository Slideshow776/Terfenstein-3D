package no.sandramoen.commanderqueen.actors;

import com.badlogic.gdx.scenes.scene2d.Stage;

import no.sandramoen.commanderqueen.actors.characters.Player;
import no.sandramoen.commanderqueen.actors.utils.baseActors.BaseActor3D;
import no.sandramoen.commanderqueen.utils.Stage3D;

public class Elevator extends BaseActor3D {

    private Player player;

    public Elevator(float y, float z, Stage3D stage3D, Stage stage, float rotation, Player player) {
        super(0, y, z, stage3D);
        this.player = player;

        buildModel(4, 4, 4f, false);
        loadImage("elevator");
        turnBy(-180 + rotation);
        setBaseRectangle();
    }
}
