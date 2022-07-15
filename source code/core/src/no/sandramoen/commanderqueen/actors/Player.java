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

public class Player extends Cube {
    private float speed = 4.0f;
    private float rotateSpeed = 90f * .05f;

    public Player(float x, float z, Stage3D s) {
        super(x, 0, z, 1, s);
        Gdx.input.setCursorCatched(true);
    }

    @Override
    public void act(float dt) {
        super.act(dt);
        movementPolling(dt);
        stage.camera.position.set(position);
    }

    private void movementPolling(float dt) {
        keyboardPolling(dt);
        mousePolling();
    }

    private void keyboardPolling(float dt) {
        if (Gdx.input.isKeyPressed(Input.Keys.W))
            moveForward(speed * dt);
        if (Gdx.input.isKeyPressed(Input.Keys.A))
            moveRight(-speed * dt);
        if (Gdx.input.isKeyPressed(Input.Keys.S))
            moveForward(-speed * dt);
        if (Gdx.input.isKeyPressed(Input.Keys.D))
            moveRight(speed * dt);
    }

    private void mousePolling() {
        turnBy(rotateSpeed * Gdx.input.getDeltaX() * BaseGame.mouseMovementSensitivity);
        stage.turnCamera(rotateSpeed * Gdx.input.getDeltaX() * BaseGame.mouseMovementSensitivity);
    }
}
