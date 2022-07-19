package no.sandramoen.commanderqueen.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.math.Vector3;

import no.sandramoen.commanderqueen.actors.utils.BaseActor3D;
import no.sandramoen.commanderqueen.utils.BaseGame;
import no.sandramoen.commanderqueen.utils.Stage3D;

public class Player extends BaseActor3D {
    private float speed = 6.0f;
    private float rotateSpeed = 90f * .05f;
    private float totalTime = 0;
    private Stage3D stage3d;

    private PointLight muzzleLight;
    private float muzzleCount;

    float bobFrequency = 4;
    float bobAmount = .01f;
    float bobCounter = 0;
    boolean moving = false;

    public Player(float y, float z, Stage3D stage3D) {
        super(0, y, z, stage3D);
        this.stage3d = stage3D;
        buildModel(1, 1, 1);
        setBaseRectangle();
    }

    @Override
    public void act(float dt) {
        super.act(dt);
        if (isPause)
            return;

        movementPolling(dt);

        stage.camera.position.y = position.y;
        stage.camera.position.z = position.z;
        headBobbing(dt);

        turnOffMuzzleLight(dt);
    }

    public void shoot() {
        muzzleLight = new PointLight();
        Color lightColor = new Color(.3f, .1f, 0, 1);
        Vector3 lightVector = new Vector3(position.x, position.y, position.z);
        muzzleLight.set(lightColor, lightVector, 25f);
        stage3d.environment.add(muzzleLight);
        muzzleCount = 0;
    }

    private void turnOffMuzzleLight(float dt) {
        muzzleCount += dt;
        if (muzzleCount > .1f)
            stage3d.environment.remove(muzzleLight);
    }

    private void headBobbing(float dt) {
        if (totalTime < 1f)
            totalTime += dt;

        if (BaseGame.isHeadBobbing) {
            bobCounter += bobFrequency * dt;
            if ((int) bobCounter % 2 == 0 && moving)
                stage.moveCameraUp(bobAmount);
            else if (moving)
                stage.moveCameraUp(-bobAmount);
            moving = false;
        }
    }

    private void movementPolling(float dt) {
        keyboardPolling(dt);
        if (totalTime >= .15f)
            mousePolling();
    }

    private void keyboardPolling(float dt) {
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            moveForward(-speed * dt);
            moving = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            moveRight(speed * dt);
            moving = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            moveForward(speed * dt);
            moving = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            moveRight(-speed * dt);
            moving = true;
        }
    }

    private void mousePolling() {
        turnBy(rotateSpeed * Gdx.input.getDeltaX() * BaseGame.mouseMovementSensitivity);
        stage.turnCamera(rotateSpeed * Gdx.input.getDeltaX() * BaseGame.mouseMovementSensitivity);
    }
}
