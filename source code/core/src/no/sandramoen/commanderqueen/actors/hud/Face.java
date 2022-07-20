package no.sandramoen.commanderqueen.actors.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Array;

import no.sandramoen.commanderqueen.actors.utils.BaseActor;
import no.sandramoen.commanderqueen.utils.BaseGame;

public class Face extends BaseActor {
    private Animation<TextureRegion> idleAnimation;

    public Face(Stage stage) {
        super(0, 0, stage);

        Array<TextureAtlas.AtlasRegion> animationImages = new Array();
        for (int i = 0; i < 20; i++)
            animationImages.add(BaseGame.textureAtlas.findRegion("hud/face idle 1"));
        animationImages.add(BaseGame.textureAtlas.findRegion("hud/face idle 2"));
        idleAnimation = new Animation(.1f, animationImages, Animation.PlayMode.LOOP);
        setAnimation(idleAnimation);
        animationImages.clear();

        setDimensions();
    }

    public void happy() {
        loadImage("hud/face happy");
        setDimensions();
        addAction(Actions.sequence(
                Actions.delay(1f),
                Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        setAnimation(idleAnimation);
                        setDimensions();
                    }
                })
        ));
    }

    private void setDimensions() {
        setWidth(Gdx.graphics.getWidth() * 1 / 3);
        setSize(getWidth(), getWidth() / (5 / 1));
        setPosition(Gdx.graphics.getWidth() * 1 / 2 - getWidth() / 2, 0f);
    }
}
