package no.sandramoen.commanderqueen.actors.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import no.sandramoen.commanderqueen.actors.characters.Player;
import no.sandramoen.commanderqueen.actors.pickups.Bullets;
import no.sandramoen.commanderqueen.actors.pickups.Pickup;
import no.sandramoen.commanderqueen.actors.pickups.Shells;
import no.sandramoen.commanderqueen.actors.utils.baseActors.BaseActor;
import no.sandramoen.commanderqueen.actors.utils.baseActors.BaseActor3D;
import no.sandramoen.commanderqueen.actors.weapon.WeaponHandler;
import no.sandramoen.commanderqueen.actors.weapon.weapons.Boot;
import no.sandramoen.commanderqueen.actors.weapon.weapons.Chaingun;
import no.sandramoen.commanderqueen.actors.weapon.weapons.Pistol;
import no.sandramoen.commanderqueen.actors.weapon.weapons.Shotgun;
import no.sandramoen.commanderqueen.actors.weapon.weapons.Weapon;
import no.sandramoen.commanderqueen.utils.BaseGame;
import no.sandramoen.commanderqueen.utils.GameUtils;

public class HUD extends BaseActor {
    public Player player;
    public Table weaponsTable;
    public boolean isInvulnerable;

    public int armor;
    public int health;
    public int bullets;
    public int shells;
    private int score;

    private float armorProtectionValue = 1 / 3f;

    private float invulnerableCounter;
    private final float INVULNERABLE_MAX_COUNT = 30f;

    private Label armorLabel;
    private Label healthLabel;
    private Label ammoLabel;
    private Label scoreLabel;

    private Face face;
    private OverlayIndicator overlayIndicator;

    private Array<Image> weaponImages;

    public HUD(Stage stage, int health, int armor, int bullets, int shells) {
        super(0, 0, stage);
        this.health = health;
        this.armor = armor;
        this.bullets = bullets;
        this.shells = shells;
        initializeLabels();

        setWidth(Gdx.graphics.getWidth() * 1 / 3f);
        setSize(getWidth(), getWidth() / (5 / 1f));
        setPosition(Gdx.graphics.getWidth() * 1 / 2f - getWidth() / 2f, 0f);

        overlayIndicator = new OverlayIndicator(stage);
        face = new Face(stage, getFaceHealthIndex());
    }

    @Override
    public void act(float dt) {
        super.act(dt);
        checkInvulnerability(dt);
    }

    public void setWeaponsTable(WeaponHandler weaponHandler) {
        weaponImages = new Array();
        weaponImages.add(new Image(BaseGame.textureAtlas.findRegion("weapons/boot/icon")));
        weaponImages.add(new Image(BaseGame.textureAtlas.findRegion("weapons/pistol/icon")));
        weaponImages.add(new Image(BaseGame.textureAtlas.findRegion("weapons/shotgun/icon")));
        weaponImages.add(new Image(BaseGame.textureAtlas.findRegion("weapons/chaingun/icon")));

        Table table = new Table();
        table.defaults().grow().width(Gdx.graphics.getWidth() * .05f).height(Gdx.graphics.getHeight() * .05f);
        for (int i = 0; i < weaponImages.size; i++)
            if (weaponHandler.weapons.get(i).isAvailable)
                table.add(weaponImages.get(i));
        /*table.setDebug(true);*/

        weaponsTable = table;
        fadeWeaponsTableInAndOut(weaponHandler.currentWeapon);
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
        return true;
    }

    public void decrementHealth(int amount, BaseActor3D source) {
        if (isInvulnerable) return;

        amount = decrementArmor(amount);
        if (health - amount <= 0)
            health = 0;
        else
            health -= amount;

        float angle = getAngleToSource(source);
        setHurtFace(amount, angle);
        setOverlayAngle(amount, angle);
        healthLabel.setText(health + "%");
        player.shakeyCam(.1f, .1f);
    }


    public void incrementAmmo(Pickup pickup, Weapon currentWeapon) {
        if (pickup instanceof Bullets)
            bullets += pickup.amount;
        else if (pickup instanceof Shells)
            shells += pickup.amount;

        overlayIndicator.flash(BaseGame.yellowColor, .1f);

        if (currentWeapon instanceof Pistol || currentWeapon instanceof Chaingun)
            ammoLabel.setText(bullets + "");
        else if (currentWeapon instanceof Shotgun)
            ammoLabel.setText(shells + "");
    }

    public void decrementAmmo(Weapon currentWeapon) {
        if ((currentWeapon instanceof Pistol || currentWeapon instanceof Chaingun) && bullets > 0) {
            bullets--;
            ammoLabel.setText(bullets + "");
        } else if (currentWeapon instanceof Shotgun && shells > 0) {
            shells--;
            ammoLabel.setText(shells + "");
        }
    }

    public int getAmmo(Weapon currentWeapon) {
        if (currentWeapon instanceof Pistol || currentWeapon instanceof Chaingun)
            return bullets;
        else if (currentWeapon instanceof Shotgun)
            return shells;
        Gdx.app.error(getClass().getSimpleName(), "Error: couldn't get ammo, current weapon is unknown => " + currentWeapon.getClass().getSimpleName());
        return -1;
    }

    public void setAmmo(Weapon currentWeapon) {
        if (currentWeapon instanceof Boot)
            ammoLabel.setText("");
        else if (currentWeapon instanceof Pistol || currentWeapon instanceof Chaingun)
            ammoLabel.setText(bullets + "");
        else if (currentWeapon instanceof Shotgun)
            ammoLabel.setText(shells + "");
        else
            Gdx.app.error(getClass().getSimpleName(), "Error could not set ammo label, unrecognized weapon => " + currentWeapon);
        fadeWeaponsTableInAndOut(currentWeapon);
    }


    public boolean incrementArmor(int amount, boolean improved) {
        if (isStatIgnore(amount, armor))
            return false;
        setArmorProtectionValue(improved);
        armor = setStat(amount, armor);
        armorLabel.setText(armor + "%");
        overlayIndicator.flash(BaseGame.yellowColor, .1f);
        return true;
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

    public void setEvilFace() {
        face.setEvilFace(getFaceHealthIndex());
    }

    public void setDeadFace() {
        face.setDead();
    }

    public void setInvulnerable() {
        face.setGod();
        isInvulnerable = true;
        BaseGame.invulnerableSound.play(BaseGame.soundVolume);
    }

    private void setVulnerable() {
        face.isLocked = false;
        isInvulnerable = false;
        invulnerableCounter = 0;
        face.setSTAnimation(getFaceHealthIndex());
        BaseGame.vulnerableSound.play(BaseGame.soundVolume);
    }

    private void fadeWeaponsTableInAndOut(Weapon currentWeapon) {
        weaponsTable.addAction(Actions.sequence(
                Actions.fadeIn(.75f),
                Actions.fadeOut(.75f)
        ));

        for (Image image : weaponImages)
            image.setColor(Color.DARK_GRAY);

        if (currentWeapon instanceof Boot)
            weaponImages.get(0).setColor(Color.WHITE);
        else if (currentWeapon instanceof Pistol)
            weaponImages.get(1).setColor(Color.WHITE);
        else if (currentWeapon instanceof Shotgun)
            weaponImages.get(2).setColor(Color.WHITE);
        else if (currentWeapon instanceof Chaingun)
            weaponImages.get(3).setColor(Color.WHITE);
    }


    private float getAngleToSource(BaseActor3D source) {
        float angleToSource = 0;
        if (source != null)
            angleToSource = GameUtils.getAngleTowardsBaseActor3D(player, source) - player.getTurnAngle();
        while (angleToSource < 0)
            angleToSource += 360;
        return angleToSource;
    }

    private void checkInvulnerability(float delta) {
        if (isInvulnerable) {
            invulnerableCounter += delta;
            if (invulnerableCounter > INVULNERABLE_MAX_COUNT)
                setVulnerable();
        }
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


    private void setOverlayAngle(int amount, float angle) {
        if (angle < 130)
            overlayIndicator.flashRight(BaseGame.redColor, .5f * amount / 12);
        else if (angle > 230)
            overlayIndicator.flashLeft(BaseGame.redColor, .5f * amount / 12);
        else
            overlayIndicator.flash(BaseGame.redColor, .5f * amount / 12);
    }

    private void setHurtFace(int amount, float angle) {
        if (health > 0) {
            if (amount >= 20)
                face.setOuch(getFaceHealthIndex());
            else {
                if (angle < 130)
                    face.setTurnRight(getFaceHealthIndex());
                else if (angle > 230)
                    face.setTurnLeft(getFaceHealthIndex());
                else
                    face.setPain(getFaceHealthIndex());
            }
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
        Label label = new Label(text, new Label.LabelStyle(BaseGame.mySkin.get("arcade26", BitmapFont.class), null));
        label.setColor(BaseGame.redColor);
        label.setAlignment(Align.center);
        return label;
    }

    private void initializeLabels() {
        armorLabel = initializeLabel(armor + "%");
        healthLabel = initializeLabel(health + "%");
        scoreLabel = initializeLabel(score + "");
        ammoLabel = initializeLabel("");
    }
}
