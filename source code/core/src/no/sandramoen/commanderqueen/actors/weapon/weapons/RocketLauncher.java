package no.sandramoen.commanderqueen.actors.weapon.weapons;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

import no.sandramoen.commanderqueen.utils.BaseGame;

public class RocketLauncher extends Weapon {

    public RocketLauncher() {
        RATE_OF_FIRE = 60 / 105f;
        SPREAD_ANGLE = 0;
        minDamage = 0;
        maxDamage = 0;
        numShotsFired = 1;
        isAmmoDependent = true;
        isAvailable = true; // TODO:
        isProjectile = true;
        inventoryIndex = 5;
        initializeAnimations();
    }

    @Override
    public void attackSound() {
        BaseGame.rocketLaunchSound.play(BaseGame.soundVolume * 2, MathUtils.random(.7f, 1.3f), 0f);
    }

    @Override
    public void emptySound() {
        BaseGame.outOfAmmoSound.play(BaseGame.soundVolume, MathUtils.random(.5f, .9f), 0);
    }

    private void initializeAnimations() {
        Array<TextureAtlas.AtlasRegion> animationImages = new Array();
        for (int i = 0; i < 4; i++)
            animationImages.add(BaseGame.textureAtlas.findRegion("weapons/rocketlauncher/shooting " + i));
        shootAnimation = new Animation(RATE_OF_FIRE / animationImages.size, animationImages, Animation.PlayMode.NORMAL);

        animationImages.clear();
        animationImages.add(BaseGame.textureAtlas.findRegion("weapons/rocketlauncher/shooting 0"));
        idleAnimation = new Animation(.1f, animationImages, Animation.PlayMode.NORMAL);
    }
}
