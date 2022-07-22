package no.sandramoen.commanderqueen.actors;

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

import no.sandramoen.commanderqueen.actors.utils.BaseActor;
import no.sandramoen.commanderqueen.utils.BaseGame;

public class Weapon extends BaseActor {
    private Animation<TextureRegion> shootAnimation;
    private float totalTime = 5f;
    private float swayFrequency = .5f;
    private float swayAmount = .01f;
    private Vector2 restPosition = new Vector2(Gdx.graphics.getWidth() * 3 / 5 - getWidth() / 2, -Gdx.graphics.getHeight() * swayAmount);

    public Weapon(Stage stage) {
        super(0, 0, stage);

        Array<TextureAtlas.AtlasRegion> animationImages = new Array();
        animationImages.add(BaseGame.textureAtlas.findRegion("player/shooting 1"));
        animationImages.add(BaseGame.textureAtlas.findRegion("player/shooting 2"));
        animationImages.add(BaseGame.textureAtlas.findRegion("player/shooting 3"));
        animationImages.add(BaseGame.textureAtlas.findRegion("player/shooting 0"));
        shootAnimation = new Animation(.1f, animationImages, Animation.PlayMode.NORMAL);
        animationImages.clear();

        setWidth(Gdx.graphics.getWidth() * .25f);
        setSize(getWidth(), getWidth() / BaseGame.aspectRatio);
        setPosition(restPosition.x, -getHeight());
        moveUp();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        totalTime += Gdx.graphics.getDeltaTime();
        batch.draw(shootAnimation.getKeyFrame(totalTime), getX(), getY(), getWidth(), getHeight());
    }

    public void shoot() {
        totalTime = 0f;
        BaseGame.pistolShotSound.play(BaseGame.soundVolume, MathUtils.random(.9f, 1.1f), 0f);
    }

    public void sway(boolean isMoving) {
        if (!hasActions() && isMoving) {
            addAction(Actions.forever(Actions.sequence(
                    Actions.moveBy(Gdx.graphics.getWidth() * swayAmount, Gdx.graphics.getHeight() * swayAmount, swayFrequency),
                    Actions.moveBy(-Gdx.graphics.getWidth() * 2 * swayAmount, -Gdx.graphics.getHeight() * 2 * swayAmount, 2 * swayFrequency),
                    Actions.moveBy(Gdx.graphics.getWidth() * swayAmount, Gdx.graphics.getHeight() * swayAmount, swayFrequency)
            )));
        } else if (!isMoving) {
            clearActions();
            addAction(Actions.moveTo(restPosition.x, restPosition.y, .5f));
        }
    }

    public void moveDown() {
        clearActions();
        addAction(Actions.moveBy(0, -getHeight(), 1f));
    }

    private void moveUp() {
        clearActions();
        addAction(Actions.moveTo(restPosition.x, restPosition.y, 1f));
    }
}
