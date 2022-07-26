package no.sandramoen.commanderqueen.actors.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Intersector.MinimumTranslationVector;

import no.sandramoen.commanderqueen.utils.BaseGame;
import no.sandramoen.commanderqueen.utils.Stage3D;

public class BaseActor3D {
    public GameObject modelData;
    public boolean isCollisionEnabled = true;
    public boolean isPreventOverlapEnabled = true;
    public final Vector3 position;
    public boolean isPause = false;
    public boolean isVisible = true;

    protected final Quaternion rotation;
    protected final Vector3 scale;
    protected Polygon boundingPolygon;
    protected Stage3D stage3D;
    protected BoundingBox bounds = new BoundingBox();

    protected float width;
    protected float height;
    protected float depth;

    public BaseActor3D(float x, float y, float z, Stage3D stage3D) {
        this.stage3D = stage3D;
        this.stage3D.addActor(this);
        modelData = null;
        position = new Vector3(x, y, z);
        rotation = new Quaternion();
        scale = new Vector3(1, 1, 1);
        boundingPolygon = null;
    }

    public void setModelInstance(GameObject m) {
        modelData = m;
    }

    public Matrix4 calculateTransform() {
        return new Matrix4(position, rotation, scale);
    }

    public void act(float dt) {
        if (!isPause)
            modelData.transform.set(calculateTransform());
    }

    public void draw(ModelBatch batch, Environment env) {
        batch.render(modelData, env);
    }

    public void setColor(Color c) {
        for (Material m : modelData.materials)
            m.set(ColorAttribute.createDiffuse(c));
    }

    public void loadImage(String name) {
        TextureRegion region = BaseGame.textureAtlas.findRegion(name);
        if (region == null)
            Gdx.app.error(getClass().getSimpleName(), "Error: region is null. Are you sure the image '" + name + "' exists?");
        for (Material material : modelData.materials)
            material.set(TextureAttribute.createDiffuse(region));
    }

    public Vector3 getPosition() {
        return position;
    }

    public void setPosition(Vector3 v) {
        position.set(v);
    }

    public void setPosition(float x, float y, float z) {
        position.set(x, y, z);
    }

    public void moveBy(Vector3 v) {
        position.add(v);
    }

    public void moveBy(float x, float y, float z) {
        moveBy(new Vector3(x, y, z));
    }

    public void moveForward(float dist) {
        moveBy(rotation.transform(new Vector3(0, 0, 1)).scl(dist));
    }

    public void moveUp(float dist) {
        moveBy(rotation.transform(new Vector3(1, 0, 0)).scl(dist));
    }

    public void moveRight(float dist) {
        moveBy(rotation.transform(new Vector3(0, 1, 0)).scl(dist));
    }

    public float getTurnAngle() {
        return rotation.getAngleAround(1, 0, 0);
    }

    public void setTurnAngle(float degrees) {
        rotation.set(new Quaternion(Vector3.X, degrees));
    }

    public void turnBy(float degrees) {
        rotation.mul(new Quaternion(Vector3.X, -degrees));
    }

    public void setScale(float x, float y, float z) {
        scale.set(x, y, z);
    }

    // 2D collision detection
    public void setBaseRectangle() {
        BoundingBox modelBounds = modelData.calculateBoundingBox(new BoundingBox());
        Vector3 max = modelBounds.max;
        Vector3 min = modelBounds.min;

        float[] vertices =
                {max.z, max.y, min.z, max.y, min.z, min.y, max.z, min.y};

        boundingPolygon = new Polygon(vertices);
        boundingPolygon.setOrigin(0, 0);
    }

    public Polygon getBoundaryPolygon() {
        boundingPolygon.setPosition(position.y, position.z);
        boundingPolygon.setRotation(getTurnAngle());
        boundingPolygon.setScale(scale.y, scale.z);
        return (Polygon) boundingPolygon;
    }

    public boolean overlaps(BaseActor3D other) {
        if (!isCollisionEnabled || !other.isCollisionEnabled) return false;
        Polygon poly1 = this.getBoundaryPolygon();
        Polygon poly2 = other.getBoundaryPolygon();

        if (!poly1.getBoundingRectangle().overlaps(poly2.getBoundingRectangle()))
            return false;

        MinimumTranslationVector mtv = new MinimumTranslationVector();

        return Intersector.overlapConvexPolygons(poly1, poly2, mtv);
    }

    public void preventOverlap(BaseActor3D other) {
        if (!isPreventOverlapEnabled || !other.isPreventOverlapEnabled || !isCollisionEnabled || !other.isCollisionEnabled)
            return;
        Polygon poly1 = this.getBoundaryPolygon();
        Polygon poly2 = other.getBoundaryPolygon();

        // initial test to improve performance
        if (!poly1.getBoundingRectangle().overlaps(poly2.getBoundingRectangle()))
            return;

        MinimumTranslationVector mtv = new MinimumTranslationVector();
        boolean polygonOverlap = Intersector.overlapConvexPolygons(poly1, poly2, mtv);

        if (polygonOverlap)
            this.moveBy(0, mtv.normal.x * mtv.depth, mtv.normal.y * mtv.depth);
    }

    public boolean isWithinDistance(Float distance, BaseActor3D other) {
        float distanceBetween = (float) Math.sqrt(Math.pow(Math.abs(other.position.y - position.y), 2) + Math.pow(Math.abs(other.position.z - position.z), 2));
        return distanceBetween <= distance;
    }

    public void remove() {
        stage3D.removeActor(this);
    }

    public void buildModel(float width, float height, float depth, boolean blending) {
        ModelBuilder modelBuilder = new ModelBuilder();
        Material boxMaterial = new Material();

        if (blending)
            boxMaterial.set(new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA));
        int usageCode = VertexAttributes.Usage.Position + VertexAttributes.Usage.ColorPacked + VertexAttributes.Usage.Normal + VertexAttributes.Usage.TextureCoordinates;

        this.width = width;
        this.height = height;
        this.depth = depth;
        Model boxModel = modelBuilder.createBox(height, width, depth, boxMaterial, usageCode);
        Vector3 position = new Vector3(0, 0, 0);

        GameObject instance = new GameObject(boxModel, position);
        setModelInstance(instance);
        instance.calculateBoundingBox(bounds);
        instance.shape = new Box(bounds);
    }
}
