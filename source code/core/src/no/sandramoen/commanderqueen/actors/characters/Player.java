package no.sandramoen.commanderqueen.actors.characters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

import no.sandramoen.commanderqueen.actors.utils.BaseActor3D;
import no.sandramoen.commanderqueen.utils.BaseGame;
import no.sandramoen.commanderqueen.utils.Stage3D;

public class Player extends BaseActor3D {
    private float movementSpeed = 8.0f;
    private float rotateSpeed = 90f * .05f;
    private float totalTime = 0;
    private Stage3D stage3D;

    private float bobFrequency = 4;
    private float bobAmount = .01f;
    private float bobCounter = 0;

    private boolean isForcedToMove;
    private float forceMoveY = movementSpeed;
    private float forceMoveZ = movementSpeed;
    private float forceTime;
    private float secondsForcedToMove = .25f;

    public boolean isMoving = false;

    public Player(float y, float z, Stage3D stage3D) {
        super(0, y, z, stage3D);
        this.stage3D = stage3D;
        buildModel(1.5f, 1.5f, 1.5f, true);
        setBaseRectangle();
        isVisible = false;
    }

    @Override
    public void act(float dt) {
        super.act(dt);
        if (isPause)
            return;

        if (isForcedToMove)
            forceMove(dt);
        else
            movementPolling(dt);

        stage.camera.position.y = position.y;
        stage.camera.position.z = position.z;
        headBobbing(dt);
        if (isMoving)
            BaseGame.metalWalkingMusic.setVolume(BaseGame.soundVolume);
        else
            BaseGame.metalWalkingMusic.setVolume(0);
    }

    public void shoot() {
        stage3D.lightManager.addPointLight(position, .3f, .1f, 0, 25, .1f, .1f / 3);
    }

    public void forceMoveAwayFrom(BaseActor3D source) {
        isForcedToMove = true;
        if (position.y - source.position.y < 1) forceMoveY *= -1;
        if (position.z - source.position.z < 1) forceMoveZ *= -1;
        forceTime = totalTime + secondsForcedToMove;
    }

    private void forceMove(float dt) {
        if (totalTime <= forceTime)
            moveBy(0f, forceMoveY * dt, forceMoveZ * dt);
        else
            isForcedToMove = false;
    }

    private void headBobbing(float dt) {
        totalTime += dt;

        if (BaseGame.isHeadBobbing) {
            bobCounter += bobFrequency * dt;
            if ((int) bobCounter % 2 == 0 && isMoving)
                stage.moveCameraUp(bobAmount);
            else if (isMoving)
                stage.moveCameraUp(-bobAmount);
            else
                setXPosition();
        } else {
            setXPosition();
        }
    }

    private void movementPolling(float dt) {
        keyboardPolling(dt);
        if (totalTime >= .15f)
            mousePolling();
    }

    private void setXPosition() {
        position.x = 0;
        stage3D.camera.position.x = 0;
    }

    private void keyboardPolling(float dt) {
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            moveForward(-movementSpeed * dt);
            isMoving = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            moveRight(movementSpeed * dt);
            isMoving = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            moveForward(movementSpeed * dt);
            isMoving = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            moveRight(-movementSpeed * dt);
            isMoving = true;
        }

        if (!Gdx.input.isKeyPressed(Input.Keys.ANY_KEY))
            isMoving = false;
    }

    private void mousePolling() {
        turnBy(rotateSpeed * Gdx.input.getDeltaX() * BaseGame.mouseMovementSensitivity);
        stage.turnCamera(rotateSpeed * Gdx.input.getDeltaX() * BaseGame.mouseMovementSensitivity);
    }
}
