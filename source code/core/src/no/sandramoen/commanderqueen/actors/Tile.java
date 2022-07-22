package no.sandramoen.commanderqueen.actors;

import no.sandramoen.commanderqueen.actors.utils.BaseActor3D;
import no.sandramoen.commanderqueen.utils.Stage3D;

public class Tile extends BaseActor3D {
    public static float height = 4;

    public Tile(float y, float z, String type, String texture, Stage3D s) {
        super(0, y, z, s);
        buildModel(height, height, height);
        setBaseRectangle();
        loadImage("tiles/" + texture);

        if (type == "ceilings") {
            position.x = Tile.height;
            isCollisionEnabled = false;
        } else if (type == "floors") {
            position.x = -Tile.height;
            isCollisionEnabled = false;
        }
    }
}
