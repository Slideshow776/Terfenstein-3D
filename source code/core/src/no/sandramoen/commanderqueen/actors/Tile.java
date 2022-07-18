package no.sandramoen.commanderqueen.actors;

import com.badlogic.gdx.graphics.Color;

import no.sandramoen.commanderqueen.actors.utils.Cuboid;
import no.sandramoen.commanderqueen.utils.Stage3D;

public class Tile extends Cuboid {
    public static float height = 4;

    public Tile(float y, float z, Stage3D s) {
        super(y, z, height, s);
        setup();
    }

    public Tile(float x, float y, float z, Stage3D s) {
        super(x, y, z, height, s);
        setup();
    }

    private void setup() {
        setColor(Color.BROWN);
        loadImage("cliff0");
    }
}
