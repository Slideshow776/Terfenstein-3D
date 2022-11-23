package no.sandramoen.terfenstein3D.actors.weapon.weapons;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

import no.sandramoen.terfenstein3D.utils.BaseGame;

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
        BaseGame.bootAttackSound.play(BaseGame.soundVolume, MathUtils.random(.8f, 1.2f), 0f);
    }

    @Override
    public void emptySound() {
        BaseGame.bootMissSound.play(BaseGame.soundVolume);
    }


    private void initializeAnimations() {
        Array<TextureAtlas.AtlasRegion> animationImages = new Array();
        animationImages.add(BaseGame.textureAtlas.findRegion("weapons/handRock/2"));
        animationImages.add(BaseGame.textureAtlas.findRegion("weapons/handRock/3"));
        animationImages.add(BaseGame.textureAtlas.findRegion("weapons/handRock/4"));
        animationImages.add(BaseGame.textureAtlas.findRegion("weapons/handRock/4"));
        animationImages.add(BaseGame.textureAtlas.findRegion("weapons/handRock/5"));
        animationImages.add(BaseGame.textureAtlas.findRegion("weapons/handRock/5"));
        animationImages.add(BaseGame.textureAtlas.findRegion("weapons/handRock/6"));
        animationImages.add(BaseGame.textureAtlas.findRegion("weapons/handRock/6"));
        animationImages.add(BaseGame.textureAtlas.findRegion("weapons/handRock/6"));
        /*
        animationImages.add(BaseGame.textureAtlas.findRegion("weapons/boot/1"));
        animationImages.add(BaseGame.textureAtlas.findRegion("weapons/boot/2"));*/
        shootAnimation = new Animation(.025f, animationImages, Animation.PlayMode.NORMAL);

        animationImages.clear();
        animationImages.add(BaseGame.textureAtlas.findRegion("weapons/handRock/1"));
        idleAnimation = new Animation(.1f, animationImages, Animation.PlayMode.NORMAL);
    }
}
