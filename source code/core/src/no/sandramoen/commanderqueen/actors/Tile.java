package no.sandramoen.commanderqueen.actors;

import com.badlogic.gdx.graphics.Color;

import no.sandramoen.commanderqueen.actors.utils.BaseActor3D;
import no.sandramoen.commanderqueen.utils.Stage3D;

public class Tile extends BaseActor3D {
    public static float height = 4;

    public Tile(float y, float z, Stage3D s) {
        super(0, y, z, s);
        setup();
    }

    public Tile(float x, float y, float z, Stage3D s) {
        super(x, y, z, s);
        setup();
    }

    private void setup() {
        buildModel(height, height, height);
        setColor(Color.BROWN);
        loadImage("cliff0");
        setBaseRectangle();
    }
}
