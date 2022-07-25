package no.sandramoen.commanderqueen.actors.characters;

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
    private float timeToStopMoving = 1.1f;
    private float attackCounter = 0f;
    private final float ATTACK_FREQUENCY = 2f;

    public Ghoul(float y, float z, Stage3D s, Player player) {
        super(y, z, s, player);
        movementSpeed = .05f;

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
    }

    @Override
    public void act(float dt) {
        super.act(dt);
        if (isPause)
            return;
        if (isForcedToMove)
            forceMove(dt);
        else if (!isDead || totalTime < timeToStopMoving)
            moveForward(movementSpeed);

        if (attackCounter > ATTACK_FREQUENCY)
            isReadyToAttack = true;
        else {
            attackCounter += dt;
            isReadyToAttack = false;
        }
    }

    @Override
    public void draw(ModelBatch batch, Environment env) {
        super.draw(batch, env);
        loadImage(currentAnimation.getKeyFrame(totalTime).toString());
    }

    @Override
    public void die() {
        isDead = true;
        BaseGame.ghoulDeathSound.play(BaseGame.soundVolume);
        totalTime = 0f;
        currentAnimation = dieAnimation;
        isCollisionEnabled = false;
    }

    public boolean isReadyToAttack() {
        if (isReadyToAttack == true) {
            attackCounter = 0;
        }
        return isReadyToAttack;
    }
}
