package no.sandramoen.commanderqueen.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
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

    public Barrel(float y, float z, Stage3D stage3D, Player player) {
        super(0, y, z, stage3D);
        this.player = player;
        this.stage3D = stage3D;
        buildModel(2.8f, 3.5f, .001f);
        setPosition(GameUtils.getPositionRelativeToFloor(3.5f), y, z);
        setBaseRectangle();
        loadImage("barrel/barrel");

        Array<TextureAtlas.AtlasRegion> animationImages = new Array();
        animationImages.add(BaseGame.textureAtlas.findRegion("barrel/barrel explode 1"));
        animationImages.add(BaseGame.textureAtlas.findRegion("barrel/barrel explode 2"));
        animationImages.add(BaseGame.textureAtlas.findRegion("barrel/barrel explode 3"));
        animationImages.add(BaseGame.textureAtlas.findRegion("barrel/barrel explode 4"));
        animationImages.add(BaseGame.textureAtlas.findRegion("barrel/barrel explode 5"));
        animationImages.add(BaseGame.textureAtlas.findRegion("barrel/barrel explode 6"));
        animationImages.add(BaseGame.textureAtlas.findRegion("barrel/barrel explode 7"));
        animationImages.add(BaseGame.textureAtlas.findRegion("barrel/barrel explode 8"));
        explodeAnimation = new Animation(.2f, animationImages, Animation.PlayMode.NORMAL);
        animationImages.clear();
    }

    @Override
    public void act(float dt) {
        super.act(dt);
        totalTime += Gdx.graphics.getDeltaTime();
        setTurnAngle(GameUtils.getAngleTowardsPlayer(this, player));
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
    }
}
