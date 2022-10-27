package no.sandramoen.terfenstein3D.actors.characters;

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

public class Hund extends Enemy {

    public Hund(float y, float z, Stage3D s, Player player, Float rotation, TileGraph tileGraph, Array<Tile> floorTiles, Stage stage, HUD hud, DecalBatch batch) {
        super(y, z, s, player, rotation, tileGraph, floorTiles, stage, hud, batch);
        movementSpeed = Player.movementSpeed / 50f;
        setHealth(12);
        shootImageDelay = .25f;
        attackStateChangeFrequency = 3 * shootImageDelay;
        minDamage = 3;
        maxDamage = 15;
        score = 5;
        isRanged = false;

        initializeAnimations();
    }

    @Override
    public void die() {
        GameUtils.playSoundRelativeToDistance(BaseGame.hundDieSound, distanceBetween(player), VOCAL_RANGE, MathUtils.random(.8f, 1.2f));
        super.die();
    }

    @Override
    protected void meleeSound() {
        BaseGame.hundMeleeSound.play(BaseGame.soundVolume, MathUtils.random(.9f, 1.1f), 0);
        super.meleeSound();
    }

    @Override
    protected void playActivateSound() {
        GameUtils.playSoundRelativeToDistance(BaseGame.hundActivateSound, distanceBetween(player) * 10, VOCAL_RANGE);
        super.playActivateSound();
    }

    private void initializeAnimations() {
        initializeWalkingAnimations();
        initializeIdleAnimations();
        initializeHurtAnimation();
        initializeMeleeAnimation();
        initializeGibAnimation();
        initializeDeathAnimation();
        setDirectionalAnimation();
    }

    private void initializeIdleAnimations() {
        idleFrontAnimation = walkFrontAnimation;
        idleFrontSideLeftAnimation = walkFrontSideLeftAnimation;
        idleSideLeftAnimation = walkSideLeftAnimation;
        idleBackSideLeftAnimation = walkBackSideLeftAnimation;
        idleBackAnimation = walkBackAnimation;
        idleBackSideRightAnimation = walkBackSideRightAnimation;
        idleSideRightAnimation = walkSideRightAnimation;
        idleFrontSideRightAnimation = walkFrontSideRightAnimation;
    }

    private void initializeHurtAnimation() {
        Array<TextureAtlas.AtlasRegion> animationImages = new Array();
        animationImages.add(BaseGame.textureAtlas.findRegion("enemies/hund/gib 0"));
        hurtAnimation = new Animation(.1f, animationImages, Animation.PlayMode.NORMAL);
    }

    private void initializeWalkingAnimations() {
        Array<TextureAtlas.AtlasRegion> animationImages = new Array();
        for (int i = 0; i < 4; i++)
            animationImages.add(BaseGame.textureAtlas.findRegion("enemies/hund/walk front " + i));
        walkFrontAnimation = new Animation(.2f, animationImages, Animation.PlayMode.LOOP);
        animationImages.clear();

        for (int i = 0; i < 4; i++)
            animationImages.add(BaseGame.textureAtlas.findRegion("enemies/hund/walk front side left " + i));
        walkFrontSideLeftAnimation = new Animation(.2f, animationImages, Animation.PlayMode.LOOP);
        animationImages.clear();

        for (int i = 0; i < 4; i++)
            animationImages.add(BaseGame.textureAtlas.findRegion("enemies/hund/walk front side right " + i));
        walkFrontSideRightAnimation = new Animation(.2f, animationImages, Animation.PlayMode.LOOP);
        animationImages.clear();

        for (int i = 0; i < 4; i++)
            animationImages.add(BaseGame.textureAtlas.findRegion("enemies/hund/walk side left " + i));
        walkSideLeftAnimation = new Animation(.2f, animationImages, Animation.PlayMode.LOOP);
        animationImages.clear();

        for (int i = 0; i < 4; i++)
            animationImages.add(BaseGame.textureAtlas.findRegion("enemies/hund/walk side right " + i));
        walkSideRightAnimation = new Animation(.2f, animationImages, Animation.PlayMode.LOOP);
        animationImages.clear();

        for (int i = 0; i < 4; i++)
            animationImages.add(BaseGame.textureAtlas.findRegion("enemies/hund/walk back side left " + i));
        walkBackSideLeftAnimation = new Animation(.2f, animationImages, Animation.PlayMode.LOOP);
        animationImages.clear();

        for (int i = 0; i < 4; i++)
            animationImages.add(BaseGame.textureAtlas.findRegion("enemies/hund/walk back side right " + i));
        walkBackSideRightAnimation = new Animation(.2f, animationImages, Animation.PlayMode.LOOP);
        animationImages.clear();

        for (int i = 0; i < 4; i++)
            animationImages.add(BaseGame.textureAtlas.findRegion("enemies/hund/walk back " + i));
        walkBackAnimation = new Animation(.2f, animationImages, Animation.PlayMode.LOOP);
        animationImages.clear();
    }

    private void initializeMeleeAnimation() {
        Array<TextureAtlas.AtlasRegion> animationImages = new Array();
        animationImages.add(BaseGame.textureAtlas.findRegion("enemies/hund/melee 0"));
        animationImages.add(BaseGame.textureAtlas.findRegion("enemies/hund/melee 1"));
        animationImages.add(BaseGame.textureAtlas.findRegion("enemies/hund/melee 2"));
        meleeAnimation = new Animation(shootImageDelay, animationImages, Animation.PlayMode.NORMAL);
    }

    private void initializeGibAnimation() {
        Array<TextureAtlas.AtlasRegion> animationImages = new Array();
        for (int i = 0; i < 6; i++)
            animationImages.add(BaseGame.textureAtlas.findRegion("enemies/hund/gib " + i));
        gibAnimation = new Animation(.15f, animationImages, Animation.PlayMode.NORMAL);
    }

    private void initializeDeathAnimation() {
        Array<TextureAtlas.AtlasRegion> animationImages = new Array();
        for (int i = 0; i < 5; i++)
            animationImages.add(BaseGame.textureAtlas.findRegion("enemies/hund/die " + i));
        dieAnimation = new Animation(.3f, animationImages, Animation.PlayMode.NORMAL);
    }
}
