package no.sandramoen.commanderqueen.actors;

import no.sandramoen.commanderqueen.actors.utils.BaseActor3D;
import no.sandramoen.commanderqueen.utils.GameUtils;
import no.sandramoen.commanderqueen.utils.Stage3D;

public class ExplosionBlast extends BaseActor3D {

    public ExplosionBlast(float y, float z, float radius, Stage3D s) {
        super(0, y, z, s);
        buildModel(radius, 4f, radius);
        setPosition(GameUtils.getPositionRelativeToFloor(4f), y, z);
        setBaseRectangle();
    }
}
