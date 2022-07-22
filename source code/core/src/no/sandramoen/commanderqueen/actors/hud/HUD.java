package no.sandramoen.commanderqueen.actors.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

import no.sandramoen.commanderqueen.actors.utils.BaseActor;
import no.sandramoen.commanderqueen.utils.BaseGame;

public class HUD extends BaseActor {
    private Label armorLabel, healthLabel, ammoLabel, scoreLabel;
    private int armor = 25, health = 100, ammo = 50, score = 0;
    private int maxArmor = 200, maxHealth = 100;
    private float labelScale = 1.0f;
    private boolean invulnerable = false;
    private float invulnerableCounter;

    public Face face;

    public HUD(Stage stage) {
        super(0, 0, stage);
        setWidth(Gdx.graphics.getWidth() * 1 / 3);
        setSize(getWidth(), getWidth() / (5 / 1));
        setPosition(Gdx.graphics.getWidth() * 1 / 2 - getWidth() / 2, 0f);

        face = new Face(stage, 0);

        armorLabel = new Label(armor + "%", BaseGame.label26Style);
        armorLabel.setFontScale(labelScale);
        armorLabel.setColor(new Color(0.647f, 0.188f, 0.188f, 1f));
        addActor(armorLabel);
        armorLabel.setPosition(armorLabel.getWidth() / 4, getHeight() / 2 - armorLabel.getHeight() / 2);

        healthLabel = new Label(health + "%", BaseGame.label26Style);
        healthLabel.setFontScale(labelScale);
        healthLabel.setColor(new Color(0.647f, 0.188f, 0.188f, 1f));
        addActor(healthLabel);
        healthLabel.setPosition(getWidth() * 1 / 5 + healthLabel.getWidth() / 4, getHeight() / 2 - healthLabel.getHeight() / 2);

        ammoLabel = new Label(ammo + "", BaseGame.label26Style);
        ammoLabel.setFontScale(labelScale);
        ammoLabel.setColor(new Color(0.647f, 0.188f, 0.188f, 1f));
        addActor(ammoLabel);
        ammoLabel.setPosition(getWidth() * 3 / 5 + ammoLabel.getWidth() / 4, getHeight() / 2 - ammoLabel.getHeight() / 2);

        scoreLabel = new Label(score + "", BaseGame.label26Style);
        scoreLabel.setFontScale(labelScale);
        scoreLabel.setColor(new Color(0.647f, 0.188f, 0.188f, 1f));
        addActor(scoreLabel);
        scoreLabel.setPosition(getWidth() * 4 / 5 + scoreLabel.getWidth() / 4, getHeight() / 2 - scoreLabel.getHeight() / 2);
        scoreLabel.setZIndex(20);
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if (invulnerable) {
            invulnerableCounter += delta;
            if (invulnerableCounter > 1) {
                invulnerable = false;
                invulnerableCounter = 0;
            }
        }
    }

    public void incrementArmor(int amount) {
        if (armor + amount <= maxArmor) {
            armor += amount;
            armorLabel.setText(armor + "%");
        }
        BaseGame.armorPickupSound.play(BaseGame.soundVolume);
    }

    public void incrementHealth(int amount) {
        if (health + amount <= maxHealth) {
            health += amount;
            healthLabel.setText(health + "%");
            face.setSTAnimation(getFaceHealth());
        }
        BaseGame.healthPickupSound.play(BaseGame.soundVolume);
    }

    public int decrementHealth(int amount) {
        if (!invulnerable) {
            if (health - amount < 0)
                health = 0;
            else
                health -= amount;
            healthLabel.setText(health + "%");
            invulnerable = true;
            if (health > 0)
                face.setSTAnimation(getFaceHealth());
            else
                face.setDead();
        }
        return health;
    }

    public void incrementAmmo(int amount) {
        ammo += amount;
        ammoLabel.setText(ammo + "");
        BaseGame.ammoPickupSound.play(BaseGame.soundVolume);
    }

    public void decrementAmmo() {
        ammo--;
        ammoLabel.setText(ammo + "");
    }

    public void incrementScore(float amount) {
        score += amount;
        scoreLabel.setText(score + "");
    }

    public void setKillFace() {
        face.setKillFace(getFaceHealth());
    }

    private int getFaceHealth() {
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
