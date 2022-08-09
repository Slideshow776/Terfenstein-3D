package no.sandramoen.commanderqueen.actors.weapon.weapons;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;

import no.sandramoen.commanderqueen.utils.BaseGame;

public class Boot extends Weapon {

    public Boot() {
        RATE_OF_FIRE = 60 / 123.5f;
        minDamage = 2;
        maxDamage = 20;
        isMelee = true;
        isAvailable = true;
        range = 4;
        inventoryIndex = 0;
        initializeAnimations();
    }

    @Override
    public void attackSound() {
        BaseGame.bootAttackSound.play(BaseGame.soundVolume);
    }

    @Override
    public void emptySound() {
        BaseGame.bootMissSound.play(BaseGame.soundVolume);
    }


    private void initializeAnimations() {
        Array<TextureAtlas.AtlasRegion> animationImages = new Array();
        animationImages.add(BaseGame.textureAtlas.findRegion("weapons/boot/1"));
        animationImages.add(BaseGame.textureAtlas.findRegion("weapons/boot/2"));
        shootAnimation = new Animation(.1f, animationImages, Animation.PlayMode.NORMAL);
    }
}
