package no.sandramoen.commanderqueen.actors.characters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import no.sandramoen.commanderqueen.actors.utils.baseActors.BaseActor3D;
import no.sandramoen.commanderqueen.utils.BaseGame;
import no.sandramoen.commanderqueen.utils.GameUtils;
import no.sandramoen.commanderqueen.utils.Stage3D;

public class Rocket extends BaseActor3D {
    public boolean isRemovable;

    private Player player;
    private PointLight light;

    private float movementSpeed = Player.movementSpeed / 10f;

    public float time;
    private final float TIME_TO_DIE = 10;
    private final float EXPLODE_DURATION = 1.6f;

    private boolean isExploding;

    private int minDamage = 20;
    private int maxDamage = 160;
    private float angle;
    private float totalTime;

    private Animation<TextureRegion> explodeAnimation;

    public Rocket(Vector3 position, float angle, Stage3D stage3D, Player player) {
        super(0, position.y, position.z, stage3D);
        this.player = player;
        this.angle = angle;

        buildModel(.5f, .5f, .5f, true);
        setBaseRectangle();
        loadImage("rocket/travelling 0");

        light = new PointLight();
        light.setColor(BaseGame.redColor);
        light.setIntensity(30);
        stage3D.environment.add(light);
        angle += 180;
        setTurnAngle(angle);
    }

    @Override
    public void act(float dt) {
        super.act(dt);
        totalTime += Gdx.graphics.getDeltaTime();
        time += dt;
        if (time > TIME_TO_DIE - EXPLODE_DURATION && !isExploding) {
            isExploding = true;
            setScale(10, 10, 10);
        }
        else if (time > TIME_TO_DIE)
            isRemovable = true;

        if (!isExploding) {
            setTurnAngle(angle - 180);
            moveForward(movementSpeed);
            light.setPosition(position);
        }
        setTurnAngle(GameUtils.getAngleTowardsBaseActor3D(this, player));
        initializeExplosionAnimation();
    }

    @Override
    public void draw(ModelBatch batch, Environment env) {
        super.draw(batch, env);
        if (isExploding)
            loadImage(explodeAnimation.getKeyFrame(totalTime).toString());
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
            stage3D.environment.remove(light);
        }
    }

    private void initializeExplosionAnimation() {
        Array<TextureAtlas.AtlasRegion> animationImages = new Array();
        for (int i = 4; i <= 8; i++)
            animationImages.add(BaseGame.textureAtlas.findRegion("barrel/barrel explode " + i));
        explodeAnimation = new Animation(.2f, animationImages, Animation.PlayMode.NORMAL);
        animationImages.clear();
    }
}
