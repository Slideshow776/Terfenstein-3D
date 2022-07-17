package no.sandramoen.commanderqueen.actors.utils;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
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

    public Cuboid(float y, float z, float width, float height, float depth, Stage3D s) {
        super(0, y, z, s);
        setup(width, height, depth);
    }

    public Cuboid(float y, float z, float cubeSize, Stage3D s) {
        super(0, y, z, s);
        setup(cubeSize, cubeSize, cubeSize);
    }

    public Cuboid(Vector2 position, float width, float height, float depth, Stage3D s) {
        super(0, position.x, position.y, s);
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

        boxMaterial.set(new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA));
        int usageCode = Usage.Position + Usage.ColorPacked + Usage.Normal + Usage.TextureCoordinates;

        Model boxModel = modelBuilder.createBox(height, width, depth, boxMaterial, usageCode);
        Vector3 position = new Vector3(0, 0, 0);

        setModelInstance(new GameObject(boxModel, position));
    }
}
