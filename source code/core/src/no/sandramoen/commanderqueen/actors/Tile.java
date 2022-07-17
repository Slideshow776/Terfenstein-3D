package no.sandramoen.commanderqueen.actors;

import com.badlogic.gdx.graphics.Color;

import no.sandramoen.commanderqueen.utils.Stage3D;

public class Tile extends Cuboid {
    public Tile(float x, float z, Stage3D s) {
        super(x, z, 4, s);
        setColor(Color.BROWN);
        loadImage("cliff0");
    }
}
