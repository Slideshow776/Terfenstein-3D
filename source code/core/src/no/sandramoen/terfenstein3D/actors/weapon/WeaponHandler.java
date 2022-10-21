package no.sandramoen.terfenstein3D.actors.weapon;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;
import com.badlogic.gdx.utils.Array;

import no.sandramoen.terfenstein3D.actors.characters.Player;
import no.sandramoen.terfenstein3D.actors.hud.HUD;
import no.sandramoen.terfenstein3D.actors.utils.baseActors.BaseActor;
import no.sandramoen.terfenstein3D.actors.utils.baseActors.BaseActor3D;
import no.sandramoen.terfenstein3D.actors.weapon.weapons.Boot;
import no.sandramoen.terfenstein3D.actors.weapon.weapons.Chaingun;
import no.sandramoen.terfenstein3D.actors.weapon.weapons.Chainsaw;
import no.sandramoen.terfenstein3D.actors.weapon.weapons.Pistol;
import no.sandramoen.terfenstein3D.actors.weapon.weapons.RocketLauncher;
import no.sandramoen.terfenstein3D.actors.weapon.weapons.Shotgun;
import no.sandramoen.terfenstein3D.actors.weapon.weapons.Weapon;
import no.sandramoen.terfenstein3D.utils.BaseGame;
import no.sandramoen.terfenstein3D.utils.GameUtils;
import no.sandramoen.terfenstein3D.utils.Stage3D;

public class WeaponHandler extends BaseActor {
    public Crosshair crosshair;
    public Weapon currentWeapon;
    public Array<Weapon> weapons;
    public boolean isReadyToShoot;

    private float totalTime = 5f;
    private float swayAmount = .01f;
    private float swayFrequency = .5f;
    private float originalWidth;

    private float isReadyCounter;

    private HUD hud;
    private Player player;
    private Stage3D stage3D;
    private Vector2 restPosition;
    private Array<BaseActor3D> shootable;

    public WeaponHandler(Stage stage, HUD hud, Player player, Array<BaseActor3D> shootable, Stage3D stage3D) {
        super(0, 0, stage);
        this.hud = hud;
        this.player = player;
        this.shootable = shootable;
        this.stage3D = stage3D;

        setSize(Gdx.graphics.getWidth() * .25f, Gdx.graphics.getWidth() * .25f);
        originalWidth = getWidth();

        setPosition();
        moveUp();
        crosshair = new Crosshair(stage);

        weapons = new Array();
        weapons.add(new Boot(), new Pistol(), new Shotgun(), new Chaingun());
        weapons.add(new RocketLauncher());
        setWeapon(1);
    }

    @Override
    public void act(float dt) {
        super.act(dt);
        totalTime += dt;

        checkIfReadyToShoot(dt);
        if (hud.getHealth() > 0)
            sway(player.isMoving);

        setCrosshairColor(shootable, stage3D.camera);
        currentWeapon.act(dt);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        if (currentWeapon.shootAnimation.isAnimationFinished(totalTime) && currentWeapon.idleAnimation != null)
            batch.draw(currentWeapon.idleAnimation.getKeyFrame(totalTime), getX(), getY(), getWidth(), getHeight());
        else if (!currentWeapon.shootAnimation.isAnimationFinished(totalTime))
            batch.draw(currentWeapon.shootAnimation.getKeyFrame(totalTime), getX(), getY(), getWidth(), getHeight());
    }

    public void playerDied() {
        moveDown();
        crosshair.setVisible(false);
        totalTime = 5f;
    }

    public boolean setWeapon(int i) {
        if (i >= 0 && i < weapons.size && weapons.get(i).isAvailable) {
            totalTime = 5f;
            if (currentWeapon != null)
                currentWeapon.stopSound();
            currentWeapon = weapons.get(i);
            if (currentWeapon instanceof Chaingun || currentWeapon instanceof RocketLauncher || currentWeapon instanceof Chainsaw)
                setWidth(originalWidth * 2f);
            else
                setWidth(originalWidth);

            isReadyCounter = -.25f;
            setPosition();
            moveUp();
            return true;
        }/* else if (i >= 0 && i < weapons.size && !weapons.get(i).isAvailable) {
            Gdx.app.error(getClass().getSimpleName(), "Error: " + weapons.get(i).getClass().getSimpleName() + " is not available");
        }*/ else {
            Gdx.app.error(getClass().getSimpleName(), "Error: Weapon change to out of bounds => i: " + i + ", weapons size: " + weapons.size);
        }
        return false;
    }

    public void scrollWeapon(float i) {
        if (i < 0) { // up
            if (currentWeapon.inventoryIndex + 1 < weapons.size) {
                int j = 0;
                while (currentWeapon.inventoryIndex + 1 + j < weapons.size) {
                    if (setWeapon(currentWeapon.inventoryIndex + 1 + j))
                        return;
                    j++;
                }
            }
            setWeapon(0);
        } else if (i >= 0) { // down
            int j = 0;
            while (currentWeapon.inventoryIndex - 1 - j >= 0) {
                if (setWeapon(currentWeapon.inventoryIndex - 1 - j))
                    return;
                j++;
            }

            j = 0;
            while (weapons.size - 1 - j > 0) {
                if (setWeapon(weapons.size - 1 - j))
                    return;
                j++;
            }
        }
        totalTime = 5f;
    }

    public boolean shoot(int ammo) {
        if (isReadyToShoot) {
            isReadyToShoot = false;
            isReadyCounter = 0;

            if (ammo > 0 && currentWeapon.isAmmoDependent) {
                totalTime = 0f;
                currentWeapon.attackSound();
                shakyCam();
                return true;
            } else if (currentWeapon.isAmmoDependent) {
                currentWeapon.emptySound();
            }
        }
        return false;
    }

    public void melee(boolean isHit) {
        if (isReadyToShoot) {
            isReadyToShoot = false;
            isReadyCounter = 0;
            totalTime = 0f;

            if (isHit || currentWeapon instanceof Chainsaw)
                currentWeapon.attackSound();
            else
                currentWeapon.emptySound();

            shakyCam();
        }
    }

    public Vector2 getSpread(boolean holdingDown, float fieldOfView) {
        int maxSpreadX = 0;
        int maxSpreadY = 0;
        if (holdingDown || currentWeapon instanceof Shotgun) {
            maxSpreadX = (int) (Gdx.graphics.getWidth() / fieldOfView * currentWeapon.getSpreadAngle());
            maxSpreadY = (int) (maxSpreadX / BaseGame.aspectRatio);
        }
        return new Vector2(maxSpreadX, maxSpreadY);
    }

    public void moveDown() {
        clearActions();
        addAction(Actions.moveBy(0, -getHeight(), 1f));
    }

    public int getDamage() {
        return MathUtils.random(5, 20);
    }

    public void makeAvailable(String weapon) {
        if (weapon.equalsIgnoreCase("boot"))
            weapons.get(0).isAvailable = true;
        else if (weapon.equalsIgnoreCase("pistol"))
            weapons.get(1).isAvailable = true;
        else if (weapon.equalsIgnoreCase("shotgun"))
            weapons.get(2).isAvailable = true;
        else if (weapon.equalsIgnoreCase("chaingun"))
            weapons.get(3).isAvailable = true;
        else if (weapon.equalsIgnoreCase("rocketLauncher"))
            weapons.get(4).isAvailable = true;
        else if (weapon.equalsIgnoreCase("chainsaw")) {
            weapons.removeIndex(0);
            weapons.insert(0, new Chainsaw());
        }
    }

    private void setCrosshairColor(Array<BaseActor3D> shootable, PerspectiveCamera camera) {
        crosshair.setColorIfShootable(
                shootable,
                GameUtils.getRayPickedListIndex(
                        Gdx.graphics.getWidth() / 2,
                        Gdx.graphics.getHeight() / 2,
                        shootable,
                        camera
                )
        );
    }

    private void checkIfReadyToShoot(float dt) {
        if (isReadyCounter > currentWeapon.getRateOfFire()) {
            isReadyToShoot = true;
            isReadyCounter = 0;
        } else {
            isReadyCounter += dt;
        }
    }

    private void sway(boolean isMoving) {
        if (!hasActions() && isMoving) {
            addAction(sway());
        } else if (!isMoving) {
            clearActions();
            addAction(Actions.moveTo(restPosition.x, restPosition.y, .5f));
        }
    }

    private RepeatAction sway() {
        return Actions.forever(Actions.sequence(
                Actions.moveBy(Gdx.graphics.getWidth() * swayAmount, Gdx.graphics.getHeight() * swayAmount, swayFrequency),
                Actions.moveBy(-Gdx.graphics.getWidth() * 2 * swayAmount, -Gdx.graphics.getHeight() * 2 * swayAmount, 2 * swayFrequency),
                Actions.moveBy(Gdx.graphics.getWidth() * swayAmount, Gdx.graphics.getHeight() * swayAmount, swayFrequency)
        ));
    }

    private void moveUp() {
        clearActions();
        addAction(Actions.moveTo(restPosition.x, restPosition.y, 1f));
    }

    private void setPosition() {
        restPosition = new Vector2(Gdx.graphics.getWidth() * 4 / 5 - getWidth() / 2, -Gdx.graphics.getHeight() * swayAmount);
        setPosition(restPosition.x, -getHeight());
    }

    private void shakyCam() {
        if (currentWeapon instanceof Pistol)
            player.shakeyCam(.1f, .05f);
        else if (currentWeapon instanceof Shotgun)
            player.shakeyCam(.1f, .3f);
        else if (currentWeapon instanceof Chaingun)
            player.shakeyCam(.1f, .2f);
    }
}
