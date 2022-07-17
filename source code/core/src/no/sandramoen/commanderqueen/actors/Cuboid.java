package no.sandramoen.commanderqueen.actors;

import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import no.sandramoen.commanderqueen.utils.Stage3D;

public class Cuboid extends BaseActor3D {
    public Cuboid(float x, float y, float z, float width, float height, float depth, Stage3D s) {
        super(x, y, z, s);
        setup(width, height, depth);
    }

    public Cuboid(float x, float y, float z, float cubeSize, Stage3D s) {
        super(x, y, z, s);
        setup(cubeSize, cubeSize, cubeSize);
    }

    public Cuboid(float x, float z, float width, float height, float depth, Stage3D s) {
        super(x, 0, z, s);
        setup(width, height, depth);
    }

    public Cuboid(float x, float z, float cubeSize, Stage3D s) {
        super(x, 0, z, s);
        setup(cubeSize, cubeSize, cubeSize);
    }

    public Cuboid(Vector2 position, float width, float height, float depth, Stage3D s) {
        super(position.x, 0, position.y, s);
        setup(width, height, depth);
    }

    public Cuboid(Vector3 position, float width, float height, float depth, Stage3D s) {
        super(position.x, position.y, position.y, s);
        setup(width, height, depth);
    }

    private void setup(float width, float height, float depth) {
        buildModel(width, height, depth);
        setBaseRectangle();
    }

    private void buildModel(float width, float height, float depth) {
        ModelBuilder modelBuilder = new ModelBuilder();
        Material boxMaterial = new Material();

        int usageCode = Usage.Position + Usage.ColorPacked + Usage.Normal + Usage.TextureCoordinates;

        Model boxModel = modelBuilder.createBox(width, height, depth, boxMaterial, usageCode);
        Vector3 position = new Vector3(0, 0, 0);

        setModelInstance(new GameObject(boxModel, position));
    }
}
