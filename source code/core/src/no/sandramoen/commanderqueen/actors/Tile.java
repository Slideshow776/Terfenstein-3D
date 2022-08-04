package no.sandramoen.commanderqueen.actors;

import no.sandramoen.commanderqueen.actors.utils.BaseActor3D;
import no.sandramoen.commanderqueen.utils.Stage3D;

public class Tile extends BaseActor3D {
    public static float height = 4;
    public static float diagonalLength = (float) Math.sqrt(2 * Math.pow(height, 2));

    public int index;
    public String type;
    public boolean illuminated = false;

    public Tile(float y, float z, String type, String texture, Stage3D s, float rotation) {
        super(0, y, z, s);
        this.type = type;
        buildModel(height, height, height, false);
        setBaseRectangle();
        loadImage("tiles/" + texture);
        turnBy(-180 + rotation);
        if (texture.split(" ", 2)[0].equals("light"))
            illuminated = true;

        if (type == "ceilings") {
            position.x = Tile.height;
            isCollisionEnabled = false;
        } else if (type == "floors") {
            position.x = -Tile.height;
        }
    }
}
