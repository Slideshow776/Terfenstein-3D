package no.sandramoen.commanderqueen.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;

import no.sandramoen.commanderqueen.actors.utils.BaseActor;
import no.sandramoen.commanderqueen.utils.BaseGame;

public class Weapon extends BaseActor {
    private Animation<TextureRegion> shootAnimation;
    private float totalTime = 5f;

    public Weapon(Stage stage) {
        super(0, 0, stage);
        setPosition(Gdx.graphics.getWidth() / 2 - getWidth() / 2, 0);

        Array<TextureAtlas.AtlasRegion> animationImages = new Array();
        animationImages.add(BaseGame.textureAtlas.findRegion("player/shooting 1"));
        animationImages.add(BaseGame.textureAtlas.findRegion("player/shooting 2"));
        animationImages.add(BaseGame.textureAtlas.findRegion("player/shooting 3"));
        animationImages.add(BaseGame.textureAtlas.findRegion("player/shooting 0"));
        shootAnimation = new Animation(.1f, animationImages, Animation.PlayMode.NORMAL);
        animationImages.clear();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        if (totalTime < 10f)
            totalTime += Gdx.graphics.getDeltaTime();
        batch.draw(shootAnimation.getKeyFrame(totalTime), getX(), getY());
    }

    public void shoot() {
        totalTime = 0f;
    }
}
