package no.sandramoen.commanderqueen.actors.weapon.weapons;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

public abstract class Weapon {
    public Animation<TextureRegion> shootAnimation = null;
    public Animation<TextureRegion> restAnimation = null;
    public boolean isAmmoDependent;
    public boolean isMelee;
    public float range = 100f;
    public int index;

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
