package no.sandramoen.commanderqueen.actors;

import com.badlogic.gdx.graphics.Color;

import no.sandramoen.commanderqueen.utils.Stage3D;

public class Tile extends Cube{
    public Tile(float x, float z, float sizeOfSides, Stage3D s) {
        super(x, z, sizeOfSides, s);
        setColor(Color.BROWN);
        loadImage("cliff0");
    }
}
