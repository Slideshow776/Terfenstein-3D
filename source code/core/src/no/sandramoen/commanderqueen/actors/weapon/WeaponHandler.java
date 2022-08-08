package no.sandramoen.commanderqueen.actors.weapon;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;
import com.badlogic.gdx.utils.Array;

import no.sandramoen.commanderqueen.actors.characters.Player;
import no.sandramoen.commanderqueen.actors.hud.HUD;
import no.sandramoen.commanderqueen.actors.utils.baseActors.BaseActor;
import no.sandramoen.commanderqueen.actors.utils.baseActors.BaseActor3D;
import no.sandramoen.commanderqueen.actors.weapon.weapons.Boot;
import no.sandramoen.commanderqueen.actors.weapon.weapons.Pistol;
import no.sandramoen.commanderqueen.actors.weapon.weapons.Weapon;
import no.sandramoen.commanderqueen.utils.BaseGame;
import no.sandramoen.commanderqueen.utils.GameUtils;
import no.sandramoen.commanderqueen.utils.Stage3D;

public class WeaponHandler extends BaseActor {
    public Crosshair crosshair;
    public Weapon currentWeapon;

    private float totalTime = 5f;
    private float swayAmount = .01f;
    private float swayFrequency = .5f;

    public boolean isReady;
    private float isReadyCounter;

    private HUD hud;
    private Player player;
    private Stage3D stage3D;
    private Vector2 restPosition;
    private Array<BaseActor3D> shootable;

    private Array<Weapon> weapons;

    public WeaponHandler(Stage stage, HUD hud, Player player, Array<BaseActor3D> shootable, Stage3D stage3D) {
        super(0, 0, stage);
        this.hud = hud;
        this.player = player;
        this.shootable = shootable;
        this.stage3D = stage3D;

        restPosition = new Vector2(Gdx.graphics.getWidth() * 3 / 5 - getWidth() / 2, -Gdx.graphics.getHeight() * swayAmount);
        crosshair = new Crosshair(stage);

        setSize(Gdx.graphics.getWidth() * .25f, Gdx.graphics.getWidth() * .25f);
        setPosition(restPosition.x, -getHeight());
        moveUp();

        weapons = new Array();
        weapons.add(new Boot(), new Pistol());
        currentWeapon = weapons.get(1);
    }

    @Override
    public void act(float dt) {
        super.act(dt);
        totalTime += dt;

        checkIfReadyToShoot(dt);
        if (hud.getHealth() > 0)
            sway(player.isMoving);

        setCrosshairColor(shootable, stage3D.camera);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        if (currentWeapon.shootAnimation.isAnimationFinished(totalTime) && currentWeapon.restAnimation != null)
            batch.draw(currentWeapon.restAnimation.getKeyFrame(totalTime), getX(), getY(), getWidth(), getHeight());
        else if (!currentWeapon.shootAnimation.isAnimationFinished(totalTime))
            batch.draw(currentWeapon.shootAnimation.getKeyFrame(totalTime), getX(), getY(), getWidth(), getHeight());
    }

    public void setWeapon(int i) {
        if (i >= 0 && i < weapons.size)
            currentWeapon = weapons.get(i);
        else
            Gdx.app.error(getClass().getSimpleName(), "Error: Weapon change to out of bounds => i: " + i + ", weapon size: " + weapons.size);
    }

    public void scrollWeapon(float i) {
        if (i < 0) { // up
            if (currentWeapon.index + 1 < weapons.size)
                setWeapon(currentWeapon.index + 1);
            else
                setWeapon(0);
        } else if (i >= 0) { // down
            if (currentWeapon.index - 1 >= 0)
                setWeapon(currentWeapon.index - 1);
            else
                setWeapon(weapons.size - 1);
        }
    }

    public void shoot(int ammo) {
        if (isReady) {
            isReady = false;
            isReadyCounter = 0;

            if (ammo > 0 || !currentWeapon.isAmmoDependent) {
                totalTime = 0f;
                currentWeapon.attackSound();
            } else {
                currentWeapon.emptySound();
            }
        }
    }

    public Vector2 getSpread(boolean holdingDown, float fieldOfView) {
        int maxSpreadX = 0;
        int maxSpreadY = 0;
        if (holdingDown) {
            maxSpreadX = (int) (Gdx.graphics.getWidth() / fieldOfView * currentWeapon.getSpreadAngle());
            maxSpreadY = (int) (maxSpreadX / BaseGame.aspectRatio);
        }
        return new Vector2(maxSpreadX, maxSpreadY);
    }

    public void sway(boolean isMoving) {
        if (!hasActions() && isMoving) {
            addAction(getSwayAction());
        } else if (!isMoving) {
            clearActions();
            addAction(Actions.moveTo(restPosition.x, restPosition.y, .5f));
        }
    }

    public void moveDown() {
        clearActions();
        addAction(Actions.moveBy(0, -getHeight(), 1f));
    }

    public int getDamage() {
        return MathUtils.random(5, 20);
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
            isReady = true;
            isReadyCounter = 0;
        } else {
            isReadyCounter += dt;
        }
    }

    private RepeatAction getSwayAction() {
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
}
