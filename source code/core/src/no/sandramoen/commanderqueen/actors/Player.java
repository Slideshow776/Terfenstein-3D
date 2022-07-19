package no.sandramoen.commanderqueen.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

import no.sandramoen.commanderqueen.actors.utils.BaseActor3D;
import no.sandramoen.commanderqueen.utils.BaseGame;
import no.sandramoen.commanderqueen.utils.Stage3D;

public class Player extends BaseActor3D {
    private float speed = 6.0f;
    private float rotateSpeed = 90f * .05f;
    private float totalTime = 0;

    public Player(float y, float z, Stage3D s) {
        super(0, y, z, s);
        buildModel(1, 1, 1);
        setBaseRectangle();
    }

    @Override
    public void act(float dt) {
        super.act(dt);
        if (isPause)
            return;

        if (totalTime < 1f)
            totalTime += dt;
        movementPolling(dt);
        stage.camera.position.set(position);
    }

    private void movementPolling(float dt) {
        keyboardPolling(dt);
        if (totalTime >= .15f)
            mousePolling();
    }

    private void keyboardPolling(float dt) {
        if (Gdx.input.isKeyPressed(Input.Keys.W))
            moveForward(-speed * dt);
        if (Gdx.input.isKeyPressed(Input.Keys.A))
            moveRight(speed * dt);
        if (Gdx.input.isKeyPressed(Input.Keys.S))
            moveForward(speed * dt);
        if (Gdx.input.isKeyPressed(Input.Keys.D))
            moveRight(-speed * dt);
    }

    private void mousePolling() {
        turnBy(rotateSpeed * Gdx.input.getDeltaX() * BaseGame.mouseMovementSensitivity);
        stage.turnCamera(rotateSpeed * Gdx.input.getDeltaX() * BaseGame.mouseMovementSensitivity);
    }
}
