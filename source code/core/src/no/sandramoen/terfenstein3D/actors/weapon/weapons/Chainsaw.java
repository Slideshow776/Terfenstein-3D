package no.sandramoen.terfenstein3D.actors.weapon.weapons;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;

import no.sandramoen.terfenstein3D.utils.BaseGame;

public class Chainsaw extends Weapon {

    private Music attackingSound = BaseGame.chainSawAttackingMusic;
    private Music idleSound = BaseGame.chainSawIdleMusic;
    private float stopAttackSoundCounter;
    private float stopAttackSoundFrequency = .1f;

    public Chainsaw() {
        RATE_OF_FIRE = 60 / 525;
        minDamage = 2;
        maxDamage = 20;
        isMelee = true;
        isAvailable = true;
        range = 4.5f;
        inventoryIndex = 0;
        initializeAnimations();

        soundSetup(idleSound);
        soundSetup(attackingSound);
    }

    @Override
    public void act(float dt) {
        super.act(dt);
        if (stopAttackSoundCounter > stopAttackSoundFrequency) {
            if (!idleSound.isPlaying()) {
                attackingSound.stop();
                idleSound.play();
            }
        } else
            stopAttackSoundCounter += dt;
    }

    @Override
    public void attackSound() {
        stopAttackSoundCounter = 0;
        if (!attackingSound.isPlaying()) {
            idleSound.stop();
            attackingSound.play();
        }
    }

    @Override
    public void stopSound() {
        super.stopSound();
        idleSound.stop();
        attackingSound.stop();
    }

    @Override
    public void emptySound() {
    }


    private void initializeAnimations() {
        Array<TextureAtlas.AtlasRegion> animationImages = new Array();
        animationImages.add(BaseGame.textureAtlas.findRegion("weapons/chainsaw/attacking 1"));
        animationImages.add(BaseGame.textureAtlas.findRegion("weapons/chainsaw/attacking 2"));
        shootAnimation = new Animation(.0125f, animationImages, Animation.PlayMode.NORMAL);

        animationImages.clear();
        animationImages.add(BaseGame.textureAtlas.findRegion("weapons/chainsaw/idle 1"));
        animationImages.add(BaseGame.textureAtlas.findRegion("weapons/chainsaw/idle 2"));
        idleAnimation = new Animation(.1f, animationImages, Animation.PlayMode.LOOP);
    }

    private void soundSetup(Music music) {
        music.setLooping(true);
        music.setVolume(BaseGame.soundVolume);
    }
}
