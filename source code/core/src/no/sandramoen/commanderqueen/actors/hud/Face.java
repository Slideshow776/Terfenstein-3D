package no.sandramoen.commanderqueen.actors.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Array;

import no.sandramoen.commanderqueen.actors.utils.BaseActor;
import no.sandramoen.commanderqueen.utils.BaseGame;

public class Face extends BaseActor {
    private Array<Animation> stAnimations = new Array();
    private int currentHealthIndecy;

    public Face(Stage stage, int i) {
        super(0, 0, stage);
        currentHealthIndecy = i;

        Array<TextureAtlas.AtlasRegion> animationImages = new Array();
        for (int j = 0; j <= 4; j++) {
            animationImages.add(BaseGame.textureAtlas.findRegion("hud/ST" + j + " 0"));
            for (int k = 0; k < 6; k++)
                animationImages.add(BaseGame.textureAtlas.findRegion("hud/ST" + j + " 1"));
            animationImages.add(BaseGame.textureAtlas.findRegion("hud/ST" + j + " 2"));
            stAnimations.add(new Animation(.5f, animationImages, Animation.PlayMode.LOOP_RANDOM));
            animationImages.clear();
        }
        setSTAnimation(i);
        setZIndex(0);
    }

    public void setSTAnimation(int st) {
        currentHealthIndecy = st;
        setAnimation(stAnimations.get(st));
        setDimensions();
    }

    public void setKillFace(int i) {
        if (i < 0 || i > 4) {
            Gdx.app.error(getClass().getSimpleName(), "Error: tried to create a face out of range [0, 4], i is: " + i);
            return;
        }
        clearActions();
        currentHealthIndecy = i;
        loadImage("hud/KILL" + i + " 0");
        setDimensions();
        setDelayedStAnimation();
    }

    public void setOuch(int i) {
        clearActions();
        currentHealthIndecy = i;
        loadImage("hud/OUCH" + i + " 0");
        setDimensions();
        setDelayedStAnimation();
    }

    public void setPain(int i) {
        clearActions();
        currentHealthIndecy = i;
        loadImage("hud/PAIN" + i + " 0");
        setDimensions();
        setDelayedStAnimation();
    }

    public void setDead() {
        clearActions();
        loadImage("hud/DEAD 0");
        setDimensions();
    }

    public void setGod() {
        clearActions();
        loadImage("hud/GOD 0");
        setDimensions();
    }

    private void setDelayedStAnimation() {
        addAction(Actions.sequence(
                Actions.delay(1f),
                Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        setSTAnimation(currentHealthIndecy);
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
