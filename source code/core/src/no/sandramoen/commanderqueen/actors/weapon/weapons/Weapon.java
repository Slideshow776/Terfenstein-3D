package no.sandramoen.commanderqueen.actors.weapon.weapons;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

public abstract class Weapon {
    public Animation<TextureRegion> shootAnimation;
    public Animation<TextureRegion> idleAnimation;
    public boolean isAmmoDependent;
    public boolean isMelee;
    public boolean isAvailable;
    public float range = 100f;
    public int inventoryIndex;
    public int numShotsFired = 1;

    protected float RATE_OF_FIRE = 0;
    protected float SPREAD_ANGLE = 0;
    protected int minDamage = 0;
    protected int maxDamage = 0;

    public void attackSound() {
    }

    public void emptySound() {
    }

    public float getRateOfFire() {
        return RATE_OF_FIRE;
    }

    public float getSpreadAngle() {
        return SPREAD_ANGLE;
    }

    private int getDamage() {
        return MathUtils.random(minDamage, maxDamage);
    }
}
