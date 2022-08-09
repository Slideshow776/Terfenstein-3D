package no.sandramoen.commanderqueen.actors.weapon.weapons;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

import no.sandramoen.commanderqueen.utils.BaseGame;

public class Shotgun extends Weapon {

    public Shotgun() {
        RATE_OF_FIRE = 60 / 57f;
        SPREAD_ANGLE = 5.5f / 2;
        minDamage = 5;
        maxDamage = 15;
        numShotsFired = 7;
        isAmmoDependent = true;
        isAvailable = true;
        inventoryIndex = 2;
        initializeAnimations();
    }

    @Override
    public void attackSound() {
        BaseGame.shotgunSound.play(BaseGame.soundVolume, MathUtils.random(.9f, 1.1f), 0f);
    }

    @Override
    public void emptySound() {
        BaseGame.outOfAmmoSound.play(BaseGame.soundVolume, MathUtils.random(.8f, 1.2f), 0);
    }

    private void initializeAnimations() {
        Array<TextureAtlas.AtlasRegion> animationImages = new Array();
            animationImages.add(BaseGame.textureAtlas.findRegion("weapons/shotgun/shooting 1"));
            animationImages.add(BaseGame.textureAtlas.findRegion("weapons/shotgun/shooting 1"));
            animationImages.add(BaseGame.textureAtlas.findRegion("weapons/shotgun/shooting 2"));
            animationImages.add(BaseGame.textureAtlas.findRegion("weapons/shotgun/shooting 2"));
            animationImages.add(BaseGame.textureAtlas.findRegion("weapons/shotgun/shooting 3"));
            animationImages.add(BaseGame.textureAtlas.findRegion("weapons/shotgun/shooting 3"));
            animationImages.add(BaseGame.textureAtlas.findRegion("weapons/shotgun/shooting 4"));
            animationImages.add(BaseGame.textureAtlas.findRegion("weapons/shotgun/shooting 4"));
            animationImages.add(BaseGame.textureAtlas.findRegion("weapons/shotgun/shooting 3"));
            animationImages.add(BaseGame.textureAtlas.findRegion("weapons/shotgun/shooting 3"));
        shootAnimation = new Animation(RATE_OF_FIRE / animationImages.size, animationImages, Animation.PlayMode.NORMAL);

        animationImages.clear();
        animationImages.add(BaseGame.textureAtlas.findRegion("weapons/shotgun/shooting 0"));
        idleAnimation = new Animation(.1f, animationImages, Animation.PlayMode.NORMAL);
    }
}
