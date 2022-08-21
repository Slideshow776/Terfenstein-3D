package no.sandramoen.commanderqueen.actors.weapon.weapons;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

import no.sandramoen.commanderqueen.utils.BaseGame;

public class Chaingun extends Weapon {

    public Chaingun() {
        RATE_OF_FIRE = 60 / 525f;
        SPREAD_ANGLE = 5.5f / 2;
        minDamage = 5;
        maxDamage = 15;
        numShotsFired = 2;
        isAmmoDependent = true;
        isAvailable = false;
        inventoryIndex = 3;
        initializeAnimations();
    }

    @Override
    public void attackSound() {
        BaseGame.pistolShotSound.play(BaseGame.soundVolume, MathUtils.random(.5f, .7f), 0f);
        BaseGame.caseDroppingSound.play(BaseGame.soundVolume, MathUtils.random(.8f, 1.2f), 0f);
        BaseGame.chaingunPowerDownSound.stop();
    }

    @Override
    public void emptySound() {
        BaseGame.outOfAmmoSound.play(BaseGame.soundVolume, MathUtils.random(.5f, .9f), 0);
    }

    private void initializeAnimations() {
        Array<TextureAtlas.AtlasRegion> animationImages = new Array();
        animationImages.add(BaseGame.textureAtlas.findRegion("weapons/chaingun/shooting 1"));
        animationImages.add(BaseGame.textureAtlas.findRegion("weapons/chaingun/shooting 2"));
        shootAnimation = new Animation(RATE_OF_FIRE / animationImages.size, animationImages, Animation.PlayMode.NORMAL);

        animationImages.clear();
        animationImages.add(BaseGame.textureAtlas.findRegion("weapons/chaingun/shooting 0"));
        idleAnimation = new Animation(.1f, animationImages, Animation.PlayMode.NORMAL);
    }
}
