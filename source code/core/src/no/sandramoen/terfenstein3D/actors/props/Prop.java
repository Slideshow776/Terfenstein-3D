package no.sandramoen.terfenstein3D.actors.props;

import no.sandramoen.terfenstein3D.actors.characters.Player;
import no.sandramoen.terfenstein3D.actors.utils.baseActors.BaseActor3D;
import no.sandramoen.terfenstein3D.utils.GameUtils;
import no.sandramoen.terfenstein3D.utils.Stage3D;

public class Prop extends BaseActor3D {
    private Player player;

    public Prop(float y, float z, Stage3D stage3D, String type, Player player) {
        super(0, y, z, stage3D);
        this.player = player;

        if (type.equalsIgnoreCase("computer 0"))
            initializeModel(y, z, 3.5f, 2.5f);
        else if (type.equalsIgnoreCase("suitcase 0"))
            initializeModel(y, z, 1, 1);
        else if (type.equalsIgnoreCase("statue 0"))
            initializeModel(y, z, 8, 8);
        else if (type.equalsIgnoreCase("forklift"))
            initializeModel(y, z, 8, 4);
        else if (type.equalsIgnoreCase("lightBulb 2") || type.equalsIgnoreCase("lightBulb 3"))
            initializeModel(y, z, 4, 8);
        else if (type.equalsIgnoreCase("player"))
            initializeModel(y, z, 3, 3);
        else
            initializeModel(y, z, 4, 4);
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
