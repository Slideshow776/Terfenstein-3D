package no.sandramoen.commanderqueen.actors.utils;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;

public class GameObject extends ModelInstance {
    public Shape shape;

    public GameObject(Model boxModel, Vector3 position) {
        super(boxModel, position);
    }

    public boolean isVisible(Camera cam) {
        return shape == null ? false : shape.isVisible(transform, cam);
    }

    public float intersects(Ray ray) {
        return shape == null ? -1f : shape.intersects(transform, ray);
    }
}