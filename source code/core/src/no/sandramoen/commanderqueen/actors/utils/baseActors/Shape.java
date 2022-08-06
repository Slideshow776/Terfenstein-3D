package no.sandramoen.commanderqueen.actors.utils.baseActors;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;

public interface Shape {
    boolean isVisible(Matrix4 transform, Camera cam);

    /**
     * @return -1 on no intersection, or when there is an intersection: the squared distance between the center of this
     * object and the point on the ray closest to this object when there is intersection.
     */
    float intersects(Matrix4 transform, Ray ray);
}

abstract class BaseShape implements Shape {
    protected final static Vector3 position = new Vector3();
    public final Vector3 center = new Vector3();
    public final Vector3 dimensions = new Vector3();

    public BaseShape(BoundingBox bounds) {
        bounds.getCenter(center);
        bounds.getDimensions(dimensions);
    }
}

 class Box extends BaseShape {
    public Box(BoundingBox bounds) {
        super(bounds);
    }

    @Override
    public boolean isVisible(Matrix4 transform, Camera cam) {
        return cam.frustum.boundsInFrustum(transform.getTranslation(position).add(center), dimensions);
    }

    @Override
    public float intersects(Matrix4 transform, Ray ray) {
        transform.getTranslation(position).add(center);
        if (Intersector.intersectRayBoundsFast(ray, position, dimensions))
            return ray.origin.dst2(position);
        return -1f;
    }
}