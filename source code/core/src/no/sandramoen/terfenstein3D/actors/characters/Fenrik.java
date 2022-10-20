package no.sandramoen.terfenstein3D.actors.characters;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;

import no.sandramoen.terfenstein3D.actors.Tile;
import no.sandramoen.terfenstein3D.actors.characters.enemy.Enemy;
import no.sandramoen.terfenstein3D.actors.hud.HUD;
import no.sandramoen.terfenstein3D.utils.BaseGame;
import no.sandramoen.terfenstein3D.utils.GameUtils;
import no.sandramoen.terfenstein3D.utils.Stage3D;
import no.sandramoen.terfenstein3D.utils.pathFinding.TileGraph;

public class Fenrik extends Enemy {
    private Sound shootSound = BaseGame.pistolShotSound;
    private long shootSoundID;

    public Fenrik(float y, float z, Stage3D s, Player player, Float rotation, TileGraph tileGraph, Array<Tile> floorTiles, Stage stage, HUD hud, DecalBatch batch) {
        super(y, z, s, player, rotation, tileGraph, floorTiles, stage, hud, batch);
        movementSpeed = Player.movementSpeed / 140f;
        setHealth(100);
        shootImageDelay = .08f; // 60 / 525f;
        shootFrequency = shootImageDelay;
        attackStateChangeFrequency = 3f;
        rangeThreshold = 120f;
        minDamage = 3;
        maxDamage = 15;
        score = 100;
        painChance = .68f;
        numShots = 1;

        initializeAnimations();
    }

    @Override
    public void die() {
        if (!isDead) {
            if (MathUtils.randomBoolean())
                GameUtils.playSoundRelativeToDistance(BaseGame.fenrikDeathSound, distanceBetween(player) * 5, VOCAL_RANGE);
            shootSound.stop(shootSoundID);
        }
        super.die();
    }

    @Override
    public void decrementHealth(int amount) {
        if (health - amount > 0 && amount > 0)
            GameUtils.playSoundRelativeToDistance(BaseGame.fenrikPainSound, distanceBetween(player) * 5, VOCAL_RANGE);
        super.decrementHealth(amount);
    }

    @Override
    protected void shootSound() {
        shootSoundID = GameUtils.playSoundRelativeToDistance(shootSound, distanceBetween(player), VOCAL_RANGE, .6f);
        super.shootSound();
    }

    @Override
    protected void meleeSound() {
        BaseGame.menigMeleeSound.play(BaseGame.soundVolume);
        super.meleeSound();
    }

    @Override
    protected void playActivateSound() {
        GameUtils.playSoundRelativeToDistance(BaseGame.fenrikActiveSound, distanceBetween(player) * 5, VOCAL_RANGE);
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
        setDirectionalAnimation();
    }

    private void initializeIdleAnimations() {
        Array<TextureAtlas.AtlasRegion> animationImages = new Array();
        animationImages.add(BaseGame.textureAtlas.findRegion("enemies/fenrik/idle front 0"));
        idleFrontAnimation = new Animation(.2f, animationImages, Animation.PlayMode.LOOP);
        animationImages.clear();

        animationImages.add(BaseGame.textureAtlas.findRegion("enemies/fenrik/idle front side right 0"));
        idleFrontSideLeftAnimation = new Animation(.2f, animationImages, Animation.PlayMode.LOOP);
        animationImages.clear();

        animationImages.add(BaseGame.textureAtlas.findRegion("enemies/fenrik/idle front side left 0"));
        idleFrontSideRightAnimation = new Animation(.2f, animationImages, Animation.PlayMode.LOOP);
        animationImages.clear();

        animationImages.add(BaseGame.textureAtlas.findRegion("enemies/fenrik/idle side right 0"));
        idleSideLeftAnimation = new Animation(.2f, animationImages, Animation.PlayMode.LOOP);
        animationImages.clear();

        animationImages.add(BaseGame.textureAtlas.findRegion("enemies/fenrik/idle side left 0"));
        idleSideRightAnimation = new Animation(.2f, animationImages, Animation.PlayMode.LOOP);
        animationImages.clear();

        animationImages.add(BaseGame.textureAtlas.findRegion("enemies/fenrik/idle back side right 0"));
        idleBackSideLeftAnimation = new Animation(.2f, animationImages, Animation.PlayMode.LOOP);
        animationImages.clear();

        animationImages.add(BaseGame.textureAtlas.findRegion("enemies/fenrik/idle back side left 0"));
        idleBackSideRightAnimation = new Animation(.2f, animationImages, Animation.PlayMode.LOOP);
        animationImages.clear();

        animationImages.add(BaseGame.textureAtlas.findRegion("enemies/fenrik/idle back 0"));
        idleBackAnimation = new Animation(.2f, animationImages, Animation.PlayMode.LOOP);
    }

    private void initializeWalkingAnimations() {
        Array<TextureAtlas.AtlasRegion> animationImages = new Array();
        for (int i = 0; i < 4; i++)
            animationImages.add(BaseGame.textureAtlas.findRegion("enemies/fenrik/walk front " + i));
        walkFrontAnimation = new Animation(.2f, animationImages, Animation.PlayMode.LOOP);
        animationImages.clear();

        for (int i = 0; i < 4; i++)
            animationImages.add(BaseGame.textureAtlas.findRegion("enemies/fenrik/walk front side right " + i));
        walkFrontSideLeftAnimation = new Animation(.2f, animationImages, Animation.PlayMode.LOOP);
        animationImages.clear();

        for (int i = 0; i < 4; i++)
            animationImages.add(BaseGame.textureAtlas.findRegion("enemies/fenrik/walk front side left " + i));
        walkFrontSideRightAnimation = new Animation(.2f, animationImages, Animation.PlayMode.LOOP);
        animationImages.clear();

        for (int i = 0; i < 4; i++)
            animationImages.add(BaseGame.textureAtlas.findRegion("enemies/fenrik/walk side right " + i));
        walkSideLeftAnimation = new Animation(.2f, animationImages, Animation.PlayMode.LOOP);
        animationImages.clear();

        for (int i = 0; i < 4; i++)
            animationImages.add(BaseGame.textureAtlas.findRegion("enemies/fenrik/walk side left " + i));
        walkSideRightAnimation = new Animation(.2f, animationImages, Animation.PlayMode.LOOP);
        animationImages.clear();

        for (int i = 0; i < 4; i++)
            animationImages.add(BaseGame.textureAtlas.findRegion("enemies/fenrik/walk back side right " + i));
        walkBackSideLeftAnimation = new Animation(.2f, animationImages, Animation.PlayMode.LOOP);
        animationImages.clear();

        for (int i = 0; i < 4; i++)
            animationImages.add(BaseGame.textureAtlas.findRegion("enemies/fenrik/walk back side left " + i));
        walkBackSideRightAnimation = new Animation(.2f, animationImages, Animation.PlayMode.LOOP);
        animationImages.clear();

        for (int i = 0; i < 4; i++)
            animationImages.add(BaseGame.textureAtlas.findRegion("enemies/fenrik/walk back " + i));
        walkBackAnimation = new Animation(.2f, animationImages, Animation.PlayMode.LOOP);
        animationImages.clear();
    }

    private void initializeShootAnimation() {
        Array<TextureAtlas.AtlasRegion> animationImages = new Array();
        animationImages.add(BaseGame.textureAtlas.findRegion("enemies/fenrik/shoot 0"));
        animationImages.add(BaseGame.textureAtlas.findRegion("enemies/fenrik/shoot 1"));
        animationImages.add(BaseGame.textureAtlas.findRegion("enemies/fenrik/shoot 0"));
        shootAnimation = new Animation(shootImageDelay, animationImages, Animation.PlayMode.NORMAL);
    }

    private void initializeMeleeAnimation() {
        Array<TextureAtlas.AtlasRegion> animationImages = new Array();
        animationImages.add(BaseGame.textureAtlas.findRegion("enemies/fenrik/melee 0"));
        animationImages.add(BaseGame.textureAtlas.findRegion("enemies/fenrik/melee 1"));
        animationImages.add(BaseGame.textureAtlas.findRegion("enemies/fenrik/melee 0"));
        meleeAnimation = new Animation(shootImageDelay, animationImages, Animation.PlayMode.NORMAL);
    }

    private void initializeGibAnimation() {
        Array<TextureAtlas.AtlasRegion> animationImages = new Array();
        for (int i = 0; i < 7; i++)
            animationImages.add(BaseGame.textureAtlas.findRegion("enemies/fenrik/gib " + i));
        gibAnimation = new Animation(.15f, animationImages, Animation.PlayMode.NORMAL);
    }

    private void initializeHurtAnimation() {
        Array<TextureAtlas.AtlasRegion> animationImages = new Array();
        animationImages.add(BaseGame.textureAtlas.findRegion("enemies/fenrik/hurt 0"));
        hurtAnimation = new Animation(.25f, animationImages, Animation.PlayMode.NORMAL);
    }

    private void initializeDeathAnimation() {
        Array<TextureAtlas.AtlasRegion> animationImages = new Array();
        for (int i = 0; i < 5; i++)
            animationImages.add(BaseGame.textureAtlas.findRegion("enemies/fenrik/die " + i));
        dieAnimation = new Animation(.25f, animationImages, Animation.PlayMode.NORMAL);
    }
}
