package no.sandramoen.commanderqueen.actors;

import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import no.sandramoen.commanderqueen.utils.Stage3D;

public class Cube extends BaseActor3D {
    public Cube(float x, float y, float z, float sizeOfSides, Stage3D s) {
        super(x, y, z, s);
        setup(sizeOfSides);
    }

    public Cube(float x, float z, float sizeOfSides, Stage3D s) {
        super(x, 0, z, s);
        setup(sizeOfSides);
    }

    public Cube(Vector2 position, float sizeOfSides, Stage3D s) {
        super(position.x, 0, position.y, s);
        setup(sizeOfSides);
    }

    public Cube(Vector3 position, float sizeOfSides, Stage3D s) {
        super(position.x, position.y, position.y, s);
        setup(sizeOfSides);
    }

    private void setup(float sizeOfSides) {
        buildModel(sizeOfSides);
        setBaseRectangle();
    }

    private void buildModel(float sizeOfSides) {
        ModelBuilder modelBuilder = new ModelBuilder();
        Material boxMaterial = new Material();

        int usageCode = Usage.Position + Usage.ColorPacked + Usage.Normal + Usage.TextureCoordinates;

        Model boxModel = modelBuilder.createBox(sizeOfSides, sizeOfSides, sizeOfSides, boxMaterial, usageCode);
        Vector3 position = new Vector3(0, 0, 0);

        setModelInstance(new ModelInstance(boxModel, position));
    }
}
