package no.sandramoen.commanderqueen.actors.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;
import com.badlogic.gdx.utils.Array;

import no.sandramoen.commanderqueen.actors.characters.Player;
import no.sandramoen.commanderqueen.actors.utils.baseActors.BaseActor;
import no.sandramoen.commanderqueen.actors.utils.baseActors.BaseActor3D;
import no.sandramoen.commanderqueen.utils.BaseGame;
import no.sandramoen.commanderqueen.utils.GameUtils;
import no.sandramoen.commanderqueen.utils.Stage3D;

public class Weapon extends BaseActor {
    public Crosshair crosshair;

    private float totalTime = 5f;
    private float swayAmount = .01f;
    private float swayFrequency = .5f;

    private final float RATE_OF_FIRE = 60 / 150f;
    public final float SPREAD_ANGLE = 5.5f / 2;
    public boolean isReady;
    private float isReadyCounter;

    private Animation<TextureRegion> shootAnimation;
    private Vector2 restPosition = new Vector2(Gdx.graphics.getWidth() * 3 / 5 - getWidth() / 2, -Gdx.graphics.getHeight() * swayAmount);

    public Weapon(Stage stage) {
        super(0, 0, stage);
        crosshair = new Crosshair(stage);
        initializeShootAnimation();

        setSize(Gdx.graphics.getWidth() * .25f, Gdx.graphics.getWidth() * .25f);
        setPosition(restPosition.x, -getHeight());
        moveUp();
    }

    @Override
    public void act(float dt) {
        super.act(dt);
        checkIfReadyToShoot(dt);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        totalTime += Gdx.graphics.getDeltaTime();
        batch.draw(shootAnimation.getKeyFrame(totalTime), getX(), getY(), getWidth(), getHeight());
    }

    public void update(HUD hud, Player player, Array<BaseActor3D> shootable, Stage3D stage3D) {
        if (hud.getHealth() > 0)
            sway(player.isMoving);

        crosshair.setColorIfShootable(
                shootable,
                GameUtils.getRayPickedListIndex(
                        Gdx.graphics.getWidth() / 2,
                        Gdx.graphics.getHeight() / 2,
                        shootable,
                        stage3D.camera
                )
        );
    }

    public void shoot(int ammo) {
        if (isReady) {
            isReady = false;
            isReadyCounter = 0;

            if (ammo > 0) {
                totalTime = 0f;
                BaseGame.pistolShotSound.play(BaseGame.soundVolume, MathUtils.random(.9f, 1.1f), 0f);
            } else {
                BaseGame.outOfAmmoSound.play(BaseGame.soundVolume, MathUtils.random(.8f, 1.2f), 0);
            }
        }
    }

    public Vector2 getSpread(boolean holdingDown, float fieldOfView) {
        int maxSpreadX = 0;
        int maxSpreadY = 0;
        if (holdingDown) {
            maxSpreadX = (int) (Gdx.graphics.getWidth() / fieldOfView * SPREAD_ANGLE);
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

    private void checkIfReadyToShoot(float dt) {
        if (isReadyCounter > RATE_OF_FIRE) {
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

    private void initializeShootAnimation() {
        Array<TextureAtlas.AtlasRegion> animationImages = new Array();
        animationImages.add(BaseGame.textureAtlas.findRegion("player/shooting 1"));
        animationImages.add(BaseGame.textureAtlas.findRegion("player/shooting 2"));
        animationImages.add(BaseGame.textureAtlas.findRegion("player/shooting 3"));
        animationImages.add(BaseGame.textureAtlas.findRegion("player/shooting 0"));
        shootAnimation = new Animation(.1f, animationImages, Animation.PlayMode.NORMAL);
        animationImages.clear();
    }
}
