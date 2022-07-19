package no.sandramoen.commanderqueen.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.utils.Array;

import no.sandramoen.commanderqueen.actors.utils.Enemy;
import no.sandramoen.commanderqueen.utils.BaseGame;
import no.sandramoen.commanderqueen.utils.Stage3D;

public class Ghoul extends Enemy {
    private Animation<TextureRegion> currentAnimation;
    private Animation<TextureRegion> walkAnimation;
    private Animation<TextureRegion> dieAnimation;
    private float totalTime = 0;
    private float movementSpeed = .05f;
    private float timeToStopMoving = 1.1f;

    public Ghoul(float y, float z, Stage3D s, Player player) {
        super(y, z, s, player);

        Array<TextureAtlas.AtlasRegion> animationImages = new Array();
        animationImages.add(BaseGame.textureAtlas.findRegion("enemies/ghoul walk 0"));
        animationImages.add(BaseGame.textureAtlas.findRegion("enemies/ghoul walk 1"));
        animationImages.add(BaseGame.textureAtlas.findRegion("enemies/ghoul walk 2"));
        animationImages.add(BaseGame.textureAtlas.findRegion("enemies/ghoul walk 3"));
        walkAnimation = new Animation(.15f, animationImages, Animation.PlayMode.LOOP);
        animationImages.clear();

        animationImages.add(BaseGame.textureAtlas.findRegion("enemies/ghoul die 0"));
        animationImages.add(BaseGame.textureAtlas.findRegion("enemies/ghoul die 1"));
        animationImages.add(BaseGame.textureAtlas.findRegion("enemies/ghoul die 2"));
        animationImages.add(BaseGame.textureAtlas.findRegion("enemies/ghoul die 3"));
        animationImages.add(BaseGame.textureAtlas.findRegion("enemies/ghoul die 4"));
        dieAnimation = new Animation(.2f, animationImages, Animation.PlayMode.NORMAL);
        animationImages.clear();

        currentAnimation = walkAnimation;
        setBaseRectangle();
    }

    @Override
    public void act(float dt) {
        super.act(dt);
        if (isPause)
            return;

        if (!dead || totalTime < timeToStopMoving)
            moveForward(movementSpeed);
    }

    @Override
    public void draw(ModelBatch batch, Environment env) {
        super.draw(batch, env);
        totalTime += Gdx.graphics.getDeltaTime();
        loadImage(currentAnimation.getKeyFrame(totalTime).toString());
    }

    public void die() {
        if (!dead) {
            BaseGame.ghoulDeathSound.play(BaseGame.soundVolume);
            dead = true;
            totalTime = 0f;
            currentAnimation = dieAnimation;
            isPreventOverlapEnabled = false;
        }
    }
}
