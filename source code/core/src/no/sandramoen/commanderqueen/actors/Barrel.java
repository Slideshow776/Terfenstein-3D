package no.sandramoen.commanderqueen.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import no.sandramoen.commanderqueen.actors.characters.Player;
import no.sandramoen.commanderqueen.actors.utils.BaseActor3D;
import no.sandramoen.commanderqueen.utils.BaseGame;
import no.sandramoen.commanderqueen.utils.GameUtils;
import no.sandramoen.commanderqueen.utils.Stage3D;

public class Barrel extends BaseActor3D {
    private Player player;
    private float totalTime = 0;
    private boolean explode = false;
    private Animation<TextureRegion> explodeAnimation;
    private Stage3D stage3D;

    private PointLight blastLight;
    private float blastCount;

    public Barrel(float y, float z, Stage3D stage3D, Player player) {
        super(0, y, z, stage3D);
        this.player = player;
        this.stage3D = stage3D;
        buildModel(2.8f, 3.5f, .001f);
        setPosition(GameUtils.getPositionRelativeToFloor(3.5f), y, z);
        setBaseRectangle();
        loadImage("barrel/barrel");

        Array<TextureAtlas.AtlasRegion> animationImages = new Array();
        for (int i = 1; i <= 8; i++)
            animationImages.add(BaseGame.textureAtlas.findRegion("barrel/barrel explode " + i));
        explodeAnimation = new Animation(.2f, animationImages, Animation.PlayMode.NORMAL);
        animationImages.clear();
    }

    @Override
    public void act(float dt) {
        super.act(dt);
        totalTime += Gdx.graphics.getDeltaTime();
        setTurnAngle(GameUtils.getAngleTowardsPlayer(this, player));
        turnOffMuzzleLight(dt);
    }

    @Override
    public void draw(ModelBatch batch, Environment env) {
        super.draw(batch, env);
        if (explode) {
            loadImage(explodeAnimation.getKeyFrame(totalTime).toString());
        }
    }

    public void explode() {
        explode = true;
        totalTime = 0;
        BaseGame.explosionSound.play(BaseGame.soundVolume);
        isCollisionEnabled = false;


        blastLight = new PointLight();
        Color lightColor = new Color(.3f, .1f, 0, 1);
        Vector3 lightVector = new Vector3(position.x, position.y, position.z);
        blastLight.set(lightColor, lightVector, 1_000f);
        stage3D.environment.add(blastLight);
        blastCount = 0;
    }

    private void turnOffMuzzleLight(float dt) {
        blastCount += dt;
        if (blastCount > .1f)
            stage3D.environment.remove(blastLight);
    }
}
