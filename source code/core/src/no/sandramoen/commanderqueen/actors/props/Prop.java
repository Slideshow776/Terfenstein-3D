package no.sandramoen.commanderqueen.actors.props;

import no.sandramoen.commanderqueen.actors.characters.Player;
import no.sandramoen.commanderqueen.actors.utils.baseActors.BaseActor3D;
import no.sandramoen.commanderqueen.utils.GameUtils;
import no.sandramoen.commanderqueen.utils.Stage3D;

public class Prop extends BaseActor3D {
    private Player player;

    public Prop(float y, float z, Stage3D stage3D, String type, Player player) {
        super(0, y, z, stage3D);
        this.player = player;

        if (type.equalsIgnoreCase("computer 0"))
            initializeModel(y, z, 3.5f, 2.5f);
        else if (type.equalsIgnoreCase("suitcase 0"))
            initializeModel(y, z, 1, 1);
        loadImage("props/" + type);
    }

    @Override
    public void act(float dt) {
        super.act(dt);
        setTurnAngle(GameUtils.getAngleTowardsBaseActor3D(this, player));
    }

    private void initializeModel(float y, float z, float width, float height) {
        buildModel(width, height, .001f, true);
        setPosition(GameUtils.getPositionRelativeToFloor(height), y, z);
        setBaseRectangle();
    }
}
