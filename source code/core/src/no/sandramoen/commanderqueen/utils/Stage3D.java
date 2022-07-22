package no.sandramoen.commanderqueen.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.scenes.scene2d.Actor;

import java.util.ArrayList;

import no.sandramoen.commanderqueen.actors.Tile;
import no.sandramoen.commanderqueen.actors.utils.BaseActor3D;

public class Stage3D {
    public Environment environment;
    private final ModelBatch modelBatch;
    private ArrayList<BaseActor3D> actorList3D;
    private ArrayList<Actor> actorList;
    private Vector3 position = new Vector3();

    public int visibleCount = 0;
    public PerspectiveCamera camera;

    public Stage3D() {
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.2f, 0.2f, 0.2f, 1f));

        /*DirectionalLight dLight = new DirectionalLight();
        Color lightColor = new Color(0.0f, 0.0f, 0.9f, 1);
        Vector3 lightVector = new Vector3(Tile.height / 2, 28.25f, 10.35f);
        dLight.set(lightColor, lightVector);
        environment.add(dLight);*/

        PointLight pLight = new PointLight();
        pLight.set(new Color(.8f, .1f, .8f, 1f), new Vector3(Tile.height / 2, 22f, 8f), 50f);
        environment.add(pLight);

        camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.rotate(-90, 0, 0, 1);
        camera.lookAt(0, 0, 0);
        camera.near = .01f;
        camera.far = 100f;
        camera.update();

        modelBatch = new ModelBatch();

        actorList3D = new ArrayList();
        actorList = new ArrayList();
    }

    public void act(float dt) {
        camera.update();
        for (BaseActor3D ba : actorList3D)
            ba.act(dt);
    }

    public void draw() {
        modelBatch.begin(camera);
        visibleCount = 0;
        for (int i = 0; i < actorList3D.size(); i++) {
            if (actorList3D.get(i).modelData.isVisible(camera)) {
                actorList3D.get(i).draw(modelBatch, environment);
                modelBatch.render(actorList3D.get(i).modelData, environment);
                visibleCount++;
            }
        }
        modelBatch.end();
    }

    public void addActor(BaseActor3D ba) {
        actorList3D.add(ba);
    }

    public void addActor(Actor a) {
        actorList.add(a);
    }

    public void removeActor(BaseActor3D ba) {
        actorList3D.remove(ba);
    }

    public void removeActor(Actor a) {
        actorList.remove(a);
    }

    public ArrayList<BaseActor3D> getActors3D() {
        return actorList3D;
    }

    public ArrayList<Actor> getActors() {
        return actorList;
    }

    public void setCameraPosition(float x, float y, float z) {
        camera.position.set(x, y, z);
    }

    public void setCameraPosition(Vector3 v) {
        camera.position.set(v);
    }

    public void moveCamera(float x, float y, float z) {
        camera.position.add(x, y, z);
    }

    public void moveCamera(Vector3 v) {
        camera.position.add(v);
    }

    public void moveCameraForward(float dist) {
        Vector3 forward = new Vector3(camera.direction.x, 0, camera.direction.z).nor();
        moveCamera(forward.scl(dist));
    }

    public void moveCameraRight(float dist) {
        Vector3 right = new Vector3(camera.direction.z, 0, -camera.direction.x).nor();
        moveCamera(right.scl(dist));
    }

    public void moveCameraUp(float dist) {
        moveCamera(dist, 0, 0);
    }

    public void setCameraDirection(Vector3 v) {
        camera.lookAt(v);
        camera.up.set(0, 1, 0);
    }

    public void setCameraDirection(float x, float y, float z) {
        setCameraDirection(new Vector3(x, y, z));
    }

    public void turnCamera(float angle) {
        camera.rotate(Vector3.X, -angle);
    }

    public void tiltCamera(float angle) {
        Vector3 side = new Vector3(camera.direction.z, 0, -camera.direction.x);
        camera.direction.rotate(side, angle);
    }
}