package no.sandramoen.commanderqueen.actors.weapon.weapons;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

import no.sandramoen.commanderqueen.utils.BaseGame;

public class Pistol extends Weapon {

    public Pistol() {
        RATE_OF_FIRE = 60 / 150f;
        SPREAD_ANGLE = 5.5f / 2;
        minDamage = 5;
        maxDamage = 20;
        isAmmoDependent = true;
        isAvailable = true;
        inventoryIndex = 1;
        initializeAnimations();
    }

    @Override
    public void attackSound() {
        BaseGame.pistolShotSound.play(BaseGame.soundVolume, MathUtils.random(.9f, 1.1f), 0f);
    }

    @Override
    public void emptySound() {
        BaseGame.outOfAmmoSound.play(BaseGame.soundVolume, MathUtils.random(.8f, 1.2f), 0);
    }

    private void initializeAnimations() {
        Array<TextureAtlas.AtlasRegion> animationImages = new Array();
        for (int i = 1; i <= 3; i++)
            animationImages.add(BaseGame.textureAtlas.findRegion("weapons/pistol/shooting " + i));
        shootAnimation = new Animation(.1f, animationImages, Animation.PlayMode.NORMAL);

        animationImages.clear();
        animationImages.add(BaseGame.textureAtlas.findRegion("weapons/pistol/shooting 0"));
        idleAnimation = new Animation(.1f, animationImages, Animation.PlayMode.NORMAL);
    }
}

