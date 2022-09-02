package no.sandramoen.commanderqueen.actors.characters;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

import no.sandramoen.commanderqueen.actors.utils.baseActors.BaseActor3D;
import no.sandramoen.commanderqueen.utils.BaseGame;
import no.sandramoen.commanderqueen.utils.GameUtils;
import no.sandramoen.commanderqueen.utils.Stage3D;

public class HolyBall extends BaseActor3D {
    public boolean isRemovable;

    private Player player;
    private PointLight light;

    private float movementSpeed = Player.movementSpeed / 17f;
    private float movementAngle;

    public float time;
    private final float TIME_TO_DIE = 10;
    private final float EXPLODE_DURATION = .1f;

    private boolean isExploding;

    private int minDamage = 3;
    private int maxDamage = 24;

    public HolyBall(Vector3 position, Stage3D stage3D, Player player) {
        super(0, position.y, position.z, stage3D);
        this.player = player;

        buildModel(1, 1, 1, true);
        setBaseRectangle();
        loadImage("enemies/holy ball/travelling 0");

        light = new PointLight();
        light.setColor(BaseGame.yellowColor);
        light.setIntensity(12);
        stage3D.environment.add(light);
        movementAngle = GameUtils.getAngleTowardsBaseActor3D(this, player);
        GameUtils.playSoundRelativeToDistance(BaseGame.holyBallSpawnSound, distanceBetween(player), VOCAL_RANGE, MathUtils.random(.9f, 1.1f));
    }

    @Override
    public void act(float dt) {
        super.act(dt);
        time += dt;
        if (time > TIME_TO_DIE - EXPLODE_DURATION && !isExploding)
            startExplosionAnimation();
        else if (time > TIME_TO_DIE)
            isRemovable = true;

        if (!isExploding) {
            setTurnAngle(movementAngle);
            moveForward(movementSpeed);
            light.setPosition(position);
        }
        setTurnAngle(GameUtils.getAngleTowardsBaseActor3D(this, player));
    }

    @Override
    public void remove() {
        super.remove();
        GameUtils.playSoundRelativeToDistance(BaseGame.holyBallExplosionSound, distanceBetween(player), VOCAL_RANGE, MathUtils.random(.9f, 1.1f));
        stage3D.environment.remove(light);
    }

    @Override
    public void setColor(Color c) {
    }

    public int getDamage() {
        return MathUtils.random(minDamage, maxDamage);
    }

    public void explode() {
        if (!isExploding) {
            time = TIME_TO_DIE - EXPLODE_DURATION;
            isCollisionEnabled = false;
        }
    }

    private void startExplosionAnimation() {
        loadImage("enemies/holy ball/exploding 0");
        isExploding = true;
    }
}
