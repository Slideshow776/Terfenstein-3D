package no.sandramoen.terfenstein3D.actors.characters;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;

import no.sandramoen.terfenstein3D.actors.Tile;
import no.sandramoen.terfenstein3D.actors.characters.enemy.Enemy;
import no.sandramoen.terfenstein3D.actors.hud.HUD;
import no.sandramoen.terfenstein3D.actors.utils.baseActors.BaseActor3D;
import no.sandramoen.terfenstein3D.utils.BaseGame;
import no.sandramoen.terfenstein3D.utils.GameUtils;
import no.sandramoen.terfenstein3D.utils.Stage3D;
import no.sandramoen.terfenstein3D.utils.pathFinding.TileGraph;

public class Prest extends Enemy {
    private Sound shootSound = BaseGame.shotgunSound;
    private Array<BaseActor3D> projectiles;

    public Prest(float y, float z, Stage3D stage3D, Player player, float rotation, TileGraph tileGraph, Array<Tile> floorTiles, Stage stage, HUD hud, DecalBatch batch, Array<BaseActor3D> projectiles) {
        super(y, z, stage3D, player, rotation, tileGraph, floorTiles, stage, hud, batch);
        this.projectiles = projectiles;
        movementSpeed = Player.movementSpeed / 140f;
        setHealth(60);
        shootImageDelay = .75f;
        attackStateChangeFrequency = 1.5f * shootImageDelay;
        minDamage = 3;
        maxDamage = 24;
        score = 10;
        painChance = .78f;
        numShots = 1;
        isHitscan = false;
        rangeThreshold *= 2;

        initializeAnimations();
    }

    @Override
    public void die() {
        if (!isDead)
            GameUtils.playSoundRelativeToDistance(BaseGame.prestDeathSound, distanceBetween(player), VOCAL_RANGE);
        super.die();
    }

    @Override
    public void decrementHealth(int amount) {
        if (health - amount > 0 && amount > 0)
            GameUtils.playSoundRelativeToDistance(BaseGame.prestPainSound, distanceBetween(player), VOCAL_RANGE);
        super.decrementHealth(amount);
    }

    @Override
    protected void generateProjectile() {
        super.generateProjectile();
        projectiles.add(new HolyBall(position, stage3D, player));
    }

    @Override
    protected void shootSound() {
        super.shootSound();
    }

    @Override
    protected void meleeSound() {
        BaseGame.menigMeleeSound.play(BaseGame.soundVolume);
        super.meleeSound();
    }

    @Override
    protected void playActivateSound() {
        GameUtils.playSoundRelativeToDistance(BaseGame.prestActiveSound, distanceBetween(player) * 4, VOCAL_RANGE);
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
        animationImages.add(BaseGame.textureAtlas.findRegion("enemies/prest/idle front 0"));
        idleFrontAnimation = new Animation(.2f, animationImages, Animation.PlayMode.LOOP);
        animationImages.clear();

        animationImages.add(BaseGame.textureAtlas.findRegion("enemies/prest/idle front side left 0"));
        idleFrontSideLeftAnimation = new Animation(.2f, animationImages, Animation.PlayMode.LOOP);
        animationImages.clear();

        animationImages.add(BaseGame.textureAtlas.findRegion("enemies/prest/idle front side right 0"));
        idleFrontSideRightAnimation = new Animation(.2f, animationImages, Animation.PlayMode.LOOP);
        animationImages.clear();

        animationImages.add(BaseGame.textureAtlas.findRegion("enemies/prest/idle side left 0"));
        idleSideLeftAnimation = new Animation(.2f, animationImages, Animation.PlayMode.LOOP);
        animationImages.clear();

        animationImages.add(BaseGame.textureAtlas.findRegion("enemies/prest/idle side right 0"));
        idleSideRightAnimation = new Animation(.2f, animationImages, Animation.PlayMode.LOOP);
        animationImages.clear();

        animationImages.add(BaseGame.textureAtlas.findRegion("enemies/prest/idle back side left 0"));
        idleBackSideLeftAnimation = new Animation(.2f, animationImages, Animation.PlayMode.LOOP);
        animationImages.clear();

        animationImages.add(BaseGame.textureAtlas.findRegion("enemies/prest/idle back side right 0"));
        idleBackSideRightAnimation = new Animation(.2f, animationImages, Animation.PlayMode.LOOP);
        animationImages.clear();

        animationImages.add(BaseGame.textureAtlas.findRegion("enemies/prest/idle back 0"));
        idleBackAnimation = new Animation(.2f, animationImages, Animation.PlayMode.LOOP);
    }

    private void initializeWalkingAnimations() {
        Array<TextureAtlas.AtlasRegion> animationImages = new Array();
        for (int i = 0; i < 4; i++)
            animationImages.add(BaseGame.textureAtlas.findRegion("enemies/prest/walk front " + i));
        walkFrontAnimation = new Animation(.2f, animationImages, Animation.PlayMode.LOOP);
        animationImages.clear();

        for (int i = 0; i < 4; i++)
            animationImages.add(BaseGame.textureAtlas.findRegion("enemies/prest/walk front side left " + i));
        walkFrontSideLeftAnimation = new Animation(.2f, animationImages, Animation.PlayMode.LOOP);
        animationImages.clear();

        for (int i = 0; i < 4; i++)
            animationImages.add(BaseGame.textureAtlas.findRegion("enemies/prest/walk front side right " + i));
        walkFrontSideRightAnimation = new Animation(.2f, animationImages, Animation.PlayMode.LOOP);
        animationImages.clear();

        for (int i = 0; i < 4; i++)
            animationImages.add(BaseGame.textureAtlas.findRegion("enemies/prest/walk side left " + i));
        walkSideLeftAnimation = new Animation(.2f, animationImages, Animation.PlayMode.LOOP);
        animationImages.clear();

        for (int i = 0; i < 4; i++)
            animationImages.add(BaseGame.textureAtlas.findRegion("enemies/prest/walk side right " + i));
        walkSideRightAnimation = new Animation(.2f, animationImages, Animation.PlayMode.LOOP);
        animationImages.clear();

        for (int i = 0; i < 4; i++)
            animationImages.add(BaseGame.textureAtlas.findRegion("enemies/prest/walk back side left " + i));
        walkBackSideLeftAnimation = new Animation(.2f, animationImages, Animation.PlayMode.LOOP);
        animationImages.clear();

        for (int i = 0; i < 4; i++)
            animationImages.add(BaseGame.textureAtlas.findRegion("enemies/prest/walk back side right " + i));
        walkBackSideRightAnimation = new Animation(.2f, animationImages, Animation.PlayMode.LOOP);
        animationImages.clear();

        for (int i = 0; i < 4; i++)
            animationImages.add(BaseGame.textureAtlas.findRegion("enemies/prest/walk back " + i));
        walkBackAnimation = new Animation(.2f, animationImages, Animation.PlayMode.LOOP);
        animationImages.clear();
    }

    private void initializeShootAnimation() {
        Array<TextureAtlas.AtlasRegion> animationImages = new Array();
        animationImages.add(BaseGame.textureAtlas.findRegion("enemies/prest/shoot 0"));
        animationImages.add(BaseGame.textureAtlas.findRegion("enemies/prest/shoot 1"));
        animationImages.add(BaseGame.textureAtlas.findRegion("enemies/prest/shoot 0"));
        shootAnimation = new Animation(shootImageDelay, animationImages, Animation.PlayMode.NORMAL);
    }

    private void initializeMeleeAnimation() {
        Array<TextureAtlas.AtlasRegion> animationImages = new Array();
        animationImages.add(BaseGame.textureAtlas.findRegion("enemies/prest/melee 0"));
        animationImages.add(BaseGame.textureAtlas.findRegion("enemies/prest/melee 1"));
        animationImages.add(BaseGame.textureAtlas.findRegion("enemies/prest/melee 0"));
        meleeAnimation = new Animation(shootImageDelay, animationImages, Animation.PlayMode.NORMAL);
    }

    private void initializeGibAnimation() {
        Array<TextureAtlas.AtlasRegion> animationImages = new Array();
        for (int i = 0; i < 7; i++)
            animationImages.add(BaseGame.textureAtlas.findRegion("enemies/prest/gib " + i));
        gibAnimation = new Animation(.15f, animationImages, Animation.PlayMode.NORMAL);
    }

    private void initializeHurtAnimation() {
        Array<TextureAtlas.AtlasRegion> animationImages = new Array();
        animationImages.add(BaseGame.textureAtlas.findRegion("enemies/prest/hurt 0"));
        hurtAnimation = new Animation(.25f, animationImages, Animation.PlayMode.NORMAL);
    }

    private void initializeDeathAnimation() {
        Array<TextureAtlas.AtlasRegion> animationImages = new Array();
        for (int i = 0; i < 5; i++)
            animationImages.add(BaseGame.textureAtlas.findRegion("enemies/prest/die " + i));
        dieAnimation = new Animation(.25f, animationImages, Animation.PlayMode.NORMAL);
    }
}
