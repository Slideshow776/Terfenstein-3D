package no.sandramoen.commanderqueen.actors.characters;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;

import no.sandramoen.commanderqueen.actors.Tile;
import no.sandramoen.commanderqueen.actors.hud.HUD;
import no.sandramoen.commanderqueen.actors.utils.Enemy;
import no.sandramoen.commanderqueen.utils.BaseGame;
import no.sandramoen.commanderqueen.utils.GameUtils;
import no.sandramoen.commanderqueen.utils.Stage3D;
import no.sandramoen.commanderqueen.utils.pathFinding.TileGraph;

public class Menig extends Enemy {

    public Menig(float y, float z, Stage3D s, Player player, Float rotation, TileGraph tileGraph, Array<Tile> floorTiles, Stage stage, HUD hud) {
        super(y, z, s, player, rotation, tileGraph, floorTiles, stage, hud);
        movementSpeed = .065f;
        setHealth(20);
        shootImageDelay = .5f;
        minDamage = 3;
        maxDamage = 15;
        score = 10;
        painChance = .78f;

        initializeAnimations();
    }

    @Override
    public void die() {
        GameUtils.playSoundRelativeToDistance(BaseGame.menigDeathSound, distanceBetween(player), VOCAL_RANGE);
        super.die();
    }

    @Override
    public void decrementHealth(int amount) {
        if (health - amount > 0 && amount > 0)
            GameUtils.playSoundRelativeToDistance(BaseGame.menigHurtSound, distanceBetween(player), VOCAL_RANGE);
        super.decrementHealth(amount);
    }

    @Override
    protected void shootSound() {
        GameUtils.playSoundRelativeToDistance(BaseGame.pistolShotSound, distanceBetween(player), VOCAL_RANGE, .6f);
        super.shootSound();
    }

    @Override
    protected void meleeWeapon() {
        BaseGame.menigMeleeSound.play(BaseGame.soundVolume);
        super.meleeWeapon();
    }

    @Override
    protected void playActivateSound() {
        GameUtils.playSoundRelativeToDistance(BaseGame.menigActiveSound, distanceBetween(player) * 1.2f, VOCAL_RANGE);
        super.playActivateSound();
    }

    private void initializeAnimations() {
        initializeIdleAnimations();
        initializeWalkingAnimations();
        initializeShootAnimation();
        initializeHurtAnimation();
        initializeMeleeAnimation();
        initializeGibAnimation();
        initializeDeathAnimation();
        setDirectionalSprites();
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

    private void initializeShootAnimation() {
        Array<TextureAtlas.AtlasRegion> animationImages = new Array();
        animationImages.add(BaseGame.textureAtlas.findRegion("enemies/menig/shoot 0"));
        animationImages.add(BaseGame.textureAtlas.findRegion("enemies/menig/shoot 1"));
        animationImages.add(BaseGame.textureAtlas.findRegion("enemies/menig/shoot 0"));
        shootAnimation = new Animation(shootImageDelay, animationImages, Animation.PlayMode.NORMAL);
    }

    private void initializeMeleeAnimation() {
        Array<TextureAtlas.AtlasRegion> animationImages = new Array();
        animationImages.add(BaseGame.textureAtlas.findRegion("enemies/menig/melee 0"));
        animationImages.add(BaseGame.textureAtlas.findRegion("enemies/menig/melee 1"));
        animationImages.add(BaseGame.textureAtlas.findRegion("enemies/menig/melee 0"));
        meleeAnimation = new Animation(shootImageDelay, animationImages, Animation.PlayMode.NORMAL);
    }

    private void initializeGibAnimation() {
        Array<TextureAtlas.AtlasRegion> animationImages = new Array();
        for (int i = 0; i < 7; i++)
            animationImages.add(BaseGame.textureAtlas.findRegion("enemies/menig/gib " + i));
        gibAnimation = new Animation(.1f, animationImages, Animation.PlayMode.NORMAL);
    }

    private void initializeHurtAnimation() {
        Array<TextureAtlas.AtlasRegion> animationImages = new Array();
        animationImages.add(BaseGame.textureAtlas.findRegion("enemies/menig/hurt 0"));
        hurtAnimation = new Animation(.25f, animationImages, Animation.PlayMode.NORMAL);
    }

    private void initializeDeathAnimation() {
        Array<TextureAtlas.AtlasRegion> animationImages = new Array();
        for (int i = 0; i < 5; i++)
            animationImages.add(BaseGame.textureAtlas.findRegion("enemies/menig/die " + i));
        dieAnimation = new Animation(.25f, animationImages, Animation.PlayMode.NORMAL);
    }
}
