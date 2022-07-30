package no.sandramoen.commanderqueen.actors.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;

import no.sandramoen.commanderqueen.actors.utils.BaseActor;
import no.sandramoen.commanderqueen.utils.BaseGame;

public class HUD extends BaseActor {
    public boolean isInvulnerable;

    private int armor = 0;
    private int health = 100;
    private int ammo = 25;
    private int score = 0;

    private float armorProtectionValue = 1 / 3f;

    private float invulnerableCounter;
    private final float INVULNERABLE_MAX_COUNT = 2f;

    private Label armorLabel;
    private Label healthLabel;
    private Label ammoLabel;
    private Label scoreLabel;

    private Face face;
    private OverlayIndicator overlayIndicator;

    public HUD(Stage stage) {
        super(0, 0, stage);
        initializeLabels();

        setWidth(Gdx.graphics.getWidth() * 1 / 3f);
        setSize(getWidth(), getWidth() / (5 / 1f));
        setPosition(Gdx.graphics.getWidth() * 1 / 2f - getWidth() / 2f, 0f);

        overlayIndicator = new OverlayIndicator(stage);
        face = new Face(stage, getFaceHealthIndex());
    }

    public Table getLabelTable() {
        Table table = new Table();
        table.defaults().width(getWidth() / 5);
        table.add(armorLabel);
        table.add(healthLabel).padRight(getWidth() / 5);
        table.add(ammoLabel);
        table.add(scoreLabel);
        /*table.setDebug(true);*/
        return table;
    }

    @Override
    public void act(float dt) {
        super.act(dt);
        checkInvulnerability(dt);
    }

    public int getHealth() {
        return health;
    }

    public boolean incrementHealth(int amount) {
        if (isStatIgnore(amount, health))
            return false;
        health = setStat(amount, health);
        healthLabel.setText(health + "%");
        face.setSTAnimation(getFaceHealthIndex());
        overlayIndicator.flash(BaseGame.greenColor);
        BaseGame.healthPickupSound.play(BaseGame.soundVolume);
        return true;
    }

    public void decrementHealth(int amount) {
        if (isInvulnerable) return;

        amount = decrementArmor(amount);
        if (health - amount < 0)
            health = 0;
        else
            health -= amount;

        setHurtFace(amount);
        healthLabel.setText(health + "%");
        overlayIndicator.flash(BaseGame.redColor, .5f * amount / 50);
    }

    public boolean incrementArmor(int amount, boolean improved) {
        if (isStatIgnore(amount, armor))
            return false;
        setArmorProtectionValue(improved);
        armor = setStat(amount, armor);
        armorLabel.setText(armor + "%");
        overlayIndicator.flash(BaseGame.yellowColor, .1f);
        BaseGame.armorPickupSound.play(BaseGame.soundVolume);
        return true;
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
        isInvulnerable = true;
        BaseGame.invulnerableSound.play(BaseGame.soundVolume);
    }

    private void checkInvulnerability(float delta) {
        if (isInvulnerable) {
            invulnerableCounter += delta;
            if (invulnerableCounter > INVULNERABLE_MAX_COUNT)
                setVulnerable();
        }
    }

    private void setVulnerable() {
        face.isLocked = false;
        isInvulnerable = false;
        invulnerableCounter = 0;
        face.setSTAnimation(getFaceHealthIndex());
        BaseGame.vulnerableSound.play(BaseGame.soundVolume);
    }

    private int setStat(int amount, int stat) {
        if (amount == 1 && amount + stat <= 200)
            stat++;
        else if (amount == 100 || amount == 200)
            stat = amount;
        return stat;
    }

    private boolean isStatIgnore(int amount, int stat) {
        if ((amount == 1 && stat >= 200) || (amount == 100 && stat >= 100) || (amount == 200 && stat == 200))
            return true;
        return false;
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

    private int getFaceHealthIndex() {
        if (health >= 100) return 0;
        final int numIncrements = 5;
        int i = numIncrements;
        for (int j = 0; j <= 100; j += 100 / numIncrements) {
            if (health <= j)
                return i;
            i--;
        }
        Gdx.app.error(getClass().getSimpleName(), "Error: getFaceHealth could not determine face integer, integer is: " + i);
        return -1;
    }

    private Label initializeLabel(String text) {
        Label label = new Label(text, BaseGame.label26Style);
        label.setColor(BaseGame.redColor);
        label.setAlignment(Align.center);
        return label;
    }

    private void initializeLabels() {
        armorLabel = initializeLabel(armor + "%");
        healthLabel = initializeLabel(health + "%");
        ammoLabel = initializeLabel(ammo + "");
        scoreLabel = initializeLabel(score + "");
    }
}
