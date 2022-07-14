package no.sandramoen.commanderqueen.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;

import no.sandramoen.commanderqueen.utils.BaseGame;
import no.sandramoen.commanderqueen.utils.Stage3D;

public class Player extends BaseActor3D {
    private float speed = 4.0f;
    private float rotateSpeed = 90f * .05f;

    public Player(float x, float y, float z, Stage3D s) {
        super(x, y, z, s);
        Gdx.input.setCursorCatched(true);
        buildModel();
    }

    @Override
    public void act(float dt) {
        super.act(dt);
        movementPolling(dt);
        this.position.set(stage.camera.position);
    }

    private void movementPolling(float dt) {
        keyboardPolling(dt);
        mousePolling();
    }

    private void keyboardPolling(float dt) {
        if (Gdx.input.isKeyPressed(Input.Keys.W))
            stage.moveCameraForward(speed * dt);
        if (Gdx.input.isKeyPressed(Input.Keys.S))
            stage.moveCameraForward(-speed * dt);
        if (Gdx.input.isKeyPressed(Input.Keys.A))
            stage.moveCameraRight(speed * dt);
        if (Gdx.input.isKeyPressed(Input.Keys.D))
            stage.moveCameraRight(-speed * dt);
    }

    private void mousePolling() {
        stage.turnCamera(rotateSpeed * Gdx.input.getDeltaX() * BaseGame.mouseMovementSensitivity);
    }

    private void buildModel() {
        ModelBuilder modelBuilder = new ModelBuilder();
        Material boxMaterial = new Material();

        int usageCode = VertexAttributes.Usage.Position + VertexAttributes.Usage.ColorPacked + VertexAttributes.Usage.Normal + VertexAttributes.Usage.TextureCoordinates;

        Model boxModel = modelBuilder.createBox(1, 1, 1, boxMaterial, usageCode);
        Vector3 position = new Vector3(0, 0, 0);

        setModelInstance(new ModelInstance(boxModel, position));
    }
}
