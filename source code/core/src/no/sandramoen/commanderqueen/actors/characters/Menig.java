package no.sandramoen.commanderqueen.actors.characters;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.utils.Array;

import no.sandramoen.commanderqueen.actors.Tile;
import no.sandramoen.commanderqueen.actors.utils.Enemy;
import no.sandramoen.commanderqueen.utils.BaseGame;
import no.sandramoen.commanderqueen.utils.GameUtils;
import no.sandramoen.commanderqueen.utils.Stage3D;
import no.sandramoen.commanderqueen.utils.pathFinding.TileGraph;

public class Menig extends Enemy {
    private float timeToStopMoving = 1.1f;
    private float attackCounter = 0f;
    private final float ATTACK_FREQUENCY = 2f;

    public Menig(float y, float z, Stage3D s, Player player, Float rotation, TileGraph tileGraph, Array<Tile> floorTiles) {
        super(y, z, s, player, rotation, tileGraph, floorTiles);
        movementSpeed = .05f;
        health = 2;

        initializeAnimations();
    }

    @Override
    public void act(float dt) {
        super.act(dt);
        if (isPause)
            return;

        if (isForcedToMove)
            forceMove(dt);
        else if (isActive && isAttacking && (!isDead || totalTime < timeToStopMoving))
            moveToward(angleTowardPlayer);

        if (isDead)
            return;

        checkIfReadyToAttack(dt);
    }

    @Override
    public void draw(ModelBatch batch, Environment env) {
        sprite.loadImage(currentAnimation.getKeyFrame(totalTime).toString());
    }

    @Override
    public void die() {
        super.die();
        GameUtils.playSoundRelativeToDistance(BaseGame.menigDeathSound, distanceBetween(player), VOCAL_RANGE);
    }

    @Override
    public boolean isDeadAfterTakingDamage(int amount) {
        if (health - amount > 0)
            BaseGame.menigHurtSound.play(BaseGame.soundVolume);
        return super.isDeadAfterTakingDamage(amount);
    }

    public boolean isReadyToAttack() {
        if (isReadyToAttack == true)
            attackCounter = 0;
        return isReadyToAttack;
    }

    private void checkIfReadyToAttack(float dt) {
        if (attackCounter > ATTACK_FREQUENCY) {
            isReadyToAttack = true;
        } else {
            attackCounter += dt;
            isReadyToAttack = false;
        }
    }

    private void initializeAnimations() {
        initializeIdleAnimations();
        initializeWalkingAnimations();
        initializeHurtAnimation();
        initializeDeathAnimation();
        setDirectionalSprites();
    }

    private void initializeWalkingAnimations() {
        Array<TextureAtlas.AtlasRegion> animationImages = new Array();
        for (int i = 0; i < 4; i++)
            animationImages.add(BaseGame.textureAtlas.findRegion("enemies/menig/walk front " + i));
        walkFrontAnimation = new Animation(.2f, animationImages, Animation.PlayMode.LOOP);
        animationImages.clear();

        for (int i = 0; i < 4; i++)
            animationImages.add(BaseGame.textureAtlas.findRegion("enemies/menig/walk front side left " + i));
        walkFrontSideLeftAnimation = new Animation(.2f, animationImages, Animation.PlayMode.LOOP);
        animationImages.clear();

        for (int i = 0; i < 4; i++)
            animationImages.add(BaseGame.textureAtlas.findRegion("enemies/menig/walk front side right " + i));
        walkFrontSideRightAnimation = new Animation(.2f, animationImages, Animation.PlayMode.LOOP);
        animationImages.clear();

        for (int i = 0; i < 4; i++)
            animationImages.add(BaseGame.textureAtlas.findRegion("enemies/menig/walk side left " + i));
        walkSideLeftAnimation = new Animation(.2f, animationImages, Animation.PlayMode.LOOP);
        animationImages.clear();

        for (int i = 0; i < 4; i++)
            animationImages.add(BaseGame.textureAtlas.findRegion("enemies/menig/walk side right " + i));
        walkSideRightAnimation = new Animation(.2f, animationImages, Animation.PlayMode.LOOP);
        animationImages.clear();

        for (int i = 0; i < 4; i++)
            animationImages.add(BaseGame.textureAtlas.findRegion("enemies/menig/walk back side left " + i));
        walkBackSideLeftAnimation = new Animation(.2f, animationImages, Animation.PlayMode.LOOP);
        animationImages.clear();

        for (int i = 0; i < 4; i++)
            animationImages.add(BaseGame.textureAtlas.findRegion("enemies/menig/walk back side right " + i));
        walkBackSideRightAnimation = new Animation(.2f, animationImages, Animation.PlayMode.LOOP);
        animationImages.clear();

        for (int i = 0; i < 4; i++)
            animationImages.add(BaseGame.textureAtlas.findRegion("enemies/menig/walk back " + i));
        walkBackAnimation = new Animation(.2f, animationImages, Animation.PlayMode.LOOP);
        animationImages.clear();
    }

    private void initializeIdleAnimations() {
        Array<TextureAtlas.AtlasRegion> animationImages = new Array();
        animationImages.add(BaseGame.textureAtlas.findRegion("enemies/menig/idle front 0"));
        idleFrontAnimation = new Animation(.2f, animationImages, Animation.PlayMode.LOOP);
        animationImages.clear();

        animationImages.add(BaseGame.textureAtlas.findRegion("enemies/menig/idle front side left 0"));
        idleFrontSideLeftAnimation = new Animation(.2f, animationImages, Animation.PlayMode.LOOP);
        animationImages.clear();

        animationImages.add(BaseGame.textureAtlas.findRegion("enemies/menig/idle front side right 0"));
        idleFrontSideRightAnimation = new Animation(.2f, animationImages, Animation.PlayMode.LOOP);
        animationImages.clear();

        animationImages.add(BaseGame.textureAtlas.findRegion("enemies/menig/idle side left 0"));
        idleSideLeftAnimation = new Animation(.2f, animationImages, Animation.PlayMode.LOOP);
        animationImages.clear();

        animationImages.add(BaseGame.textureAtlas.findRegion("enemies/menig/idle side right 0"));
        idleSideRightAnimation = new Animation(.2f, animationImages, Animation.PlayMode.LOOP);
        animationImages.clear();

        animationImages.add(BaseGame.textureAtlas.findRegion("enemies/menig/idle back side left 0"));
        idleBackSideLeftAnimation = new Animation(.2f, animationImages, Animation.PlayMode.LOOP);
        animationImages.clear();

        animationImages.add(BaseGame.textureAtlas.findRegion("enemies/menig/idle back side right 0"));
        idleBackSideRightAnimation = new Animation(.2f, animationImages, Animation.PlayMode.LOOP);
        animationImages.clear();

        animationImages.add(BaseGame.textureAtlas.findRegion("enemies/menig/idle back 0"));
        idleBackAnimation = new Animation(.2f, animationImages, Animation.PlayMode.LOOP);
    }

    private void initializeDeathAnimation() {
        Array<TextureAtlas.AtlasRegion> animationImages = new Array();
        for (int i = 0; i < 5; i++)
            animationImages.add(BaseGame.textureAtlas.findRegion("enemies/menig/die " + i));
        dieAnimation = new Animation(.25f, animationImages, Animation.PlayMode.NORMAL);
    }

    private void initializeHurtAnimation() {
        Array<TextureAtlas.AtlasRegion> animationImages = new Array();
        animationImages.add(BaseGame.textureAtlas.findRegion("enemies/menig/hurt 0"));
        hurtAnimation = new Animation(.25f, animationImages, Animation.PlayMode.NORMAL);
    }
}
