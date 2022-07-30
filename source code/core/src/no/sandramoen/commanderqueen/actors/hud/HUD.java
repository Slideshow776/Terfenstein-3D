package no.sandramoen.commanderqueen.actors.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

import no.sandramoen.commanderqueen.actors.utils.BaseActor;
import no.sandramoen.commanderqueen.utils.BaseGame;

public class HUD extends BaseActor {
    private Label armorLabel, healthLabel, ammoLabel, scoreLabel;
    private int armor = 0, health = 100, ammo = 50, score = 0, maxArmor = 200, maxHealth = 100;
    private float labelScale = 1.0f, invulnerableCounter, armorProtectionValue = 1 / 3f;
    private final float invulnerableMaxCount = 10f;
    private Face face;
    private OverlayIndicator overlayIndicator;

    public boolean invulnerable = false;

    public HUD(Stage stage) {
        super(0, 0, stage);
        setWidth(Gdx.graphics.getWidth() * 1 / 3);
        setSize(getWidth(), getWidth() / (5 / 1));
        setPosition(Gdx.graphics.getWidth() * 1 / 2 - getWidth() / 2, 0f);
        overlayIndicator = new OverlayIndicator(stage);

        face = new Face(stage, 0);

        armorLabel = new Label(armor + "%", BaseGame.label26Style);
        armorLabel.setFontScale(labelScale);
        armorLabel.setColor(BaseGame.redColor);
        addActor(armorLabel);
        armorLabel.setPosition(armorLabel.getWidth() / 4, getHeight() / 2 - armorLabel.getHeight() / 2);

        healthLabel = new Label(health + "%", BaseGame.label26Style);
        healthLabel.setFontScale(labelScale);
        healthLabel.setColor(BaseGame.redColor);
        addActor(healthLabel);
        healthLabel.setPosition(getWidth() * 1 / 5 + healthLabel.getWidth() / 4, getHeight() / 2 - healthLabel.getHeight() / 2);

        ammoLabel = new Label(ammo + "", BaseGame.label26Style);
        ammoLabel.setFontScale(labelScale);
        ammoLabel.setColor(BaseGame.redColor);
        addActor(ammoLabel);
        ammoLabel.setPosition(getWidth() * 3 / 5 + ammoLabel.getWidth() / 4, getHeight() / 2 - ammoLabel.getHeight() / 2);

        scoreLabel = new Label(score + "", BaseGame.label26Style);
        scoreLabel.setFontScale(labelScale);
        scoreLabel.setColor(BaseGame.redColor);
        addActor(scoreLabel);
        scoreLabel.setPosition(getWidth() * 4 / 5 + scoreLabel.getWidth() / 4, getHeight() / 2 - scoreLabel.getHeight() / 2);
        scoreLabel.setZIndex(20);
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if (invulnerable) {
            invulnerableCounter += delta;
            if (invulnerableCounter > invulnerableMaxCount) {
                invulnerable = false;
                invulnerableCounter = 0;
                face.setSTAnimation(getFaceHealthIndex());
            }
        }
    }

    public boolean incrementArmor(int amount, boolean improved) {
        if ((amount == 100 && armor >= 100) || (amount == 200 && armor == maxArmor))
            return false;
        setArmorProtectionValue(improved);

        if (amount == 1 && amount + armor < 200)
            armor++;
        else if (amount == 100 || amount == maxArmor)
            armor = amount;

        armorLabel.setText(armor + "%");
        overlayIndicator.flash(BaseGame.yellowColor, .1f);
        BaseGame.armorPickupSound.play(BaseGame.soundVolume);
        return true;
    }

    public int getHealth() {
        return health;
    }

    public void incrementHealth(int amount) {
        if (health + amount <= maxHealth) {
            health += amount;
            healthLabel.setText(health + "%");
            face.setSTAnimation(getFaceHealthIndex());
        }
        overlayIndicator.flash(BaseGame.greenColor);
        BaseGame.healthPickupSound.play(BaseGame.soundVolume);
    }

    public void decrementHealth(int amount) {
        if (invulnerable) return;

        amount = decrementArmor(amount);
        if (health - amount < 0)
            health = 0;
        else
            health -= amount;

        setHurtFace(amount);
        healthLabel.setText(health + "%");
        overlayIndicator.flash(BaseGame.redColor, .5f * amount / 50);
    }

    private int decrementArmor(int amount) {
        int armorReduction = (int) (amount * armorProtectionValue);
        for (int i = 0; i < armorReduction; i++) {
            if (armor > 0) {
                armor--;
                amount--;
            } else break;
        }

        armorLabel.setText(armor + "%");
        return amount;
    }

    private void setArmorProtectionValue(boolean improved) {
        if (improved)
            armorProtectionValue = 1 / 2f;
        if (!improved && armor == 0)
            armorProtectionValue = 1 / 3f;
    }

    private void setHurtFace(int amount) {
        if (health > 0) {
            if (amount >= 20)
                face.setOuch(getFaceHealthIndex());
            else
                face.setPain(getFaceHealthIndex());
        } else {
            face.setDead();
        }
    }

    public void incrementAmmo(int amount) {
        ammo += amount;
        ammoLabel.setText(ammo + "");
        BaseGame.ammoPickupSound.play(BaseGame.soundVolume);
        overlayIndicator.flash(BaseGame.yellowColor, .1f);
    }

    public void decrementAmmo() {
        ammo--;
        ammoLabel.setText(ammo + "");
    }

    public int getAmmo() {
        return ammo;
    }

    public void incrementScore(float amount, Boolean isPickup) {
        score += amount;
        scoreLabel.setText(score + "");
        if (isPickup)
            overlayIndicator.flash(BaseGame.yellowColor, .1f);
    }

    public void setKillFace() {
        face.setKillFace(getFaceHealthIndex());
    }

    public void setDeadFace() {
        face.setDead();
    }

    public void setInvulnerable() {
        face.setGod();
        invulnerable = true;
    }

    private int getFaceHealthIndex() {
        final int numIncrements = 5;
        int i = numIncrements;
        for (int j = 0; j <= maxHealth; j += maxHealth / numIncrements) {
            if (health <= j)
                return i;
            i--;
        }
        Gdx.app.error(getClass().getSimpleName(), "Error: getFaceHealth could not determine face integer, integer is: " + i);
        return -1;
    }
}
