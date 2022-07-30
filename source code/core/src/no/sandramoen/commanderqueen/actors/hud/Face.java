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
    public boolean isLocked;

    private int healthIndex;
    private Array<Animation> stAnimations = new Array();

    public Face(Stage stage, int healthIndex) {
        super(0, 0, stage);
        this.healthIndex = healthIndex;
        initializeSTAnimations(healthIndex);
        setZIndex(0);
    }

    public void setSTAnimation(int faceHealthIndex) {
        if (isLocked)
            return;
        healthIndex = faceHealthIndex;
        setAnimation(stAnimations.get(faceHealthIndex));
        setDimensions();
    }

    public void setOuch(int faceHealthIndex) {
        setTemporaryFace(faceHealthIndex, "OUCH");
    }

    public void setPain(int faceHealthIndex) {
        setTemporaryFace(faceHealthIndex, "PAIN");
    }

    public void setKillFace(int faceHealthIndex) {
        setTemporaryFace(faceHealthIndex, "KILL");
    }

    public void setDead() {
        setLockedFace("DEAD");
    }

    public void setGod() {
        setLockedFace("GOD");
    }

    private void setLockedFace(String face) {
        isLocked = true;
        clearActions();
        loadImage("hud/" + face + " 0");
        setDimensions();
    }

    private void setTemporaryFace(int faceHealthIndex, String face) {
        if (isLocked)
            return;

        if (faceHealthIndex < 0 || faceHealthIndex > 4) {
            Gdx.app.error(getClass().getSimpleName(), "Error: tried to create a face out of range [0, 4], i is: " + faceHealthIndex);
            return;
        }

        clearActions();
        healthIndex = faceHealthIndex;
        loadImage("hud/" + face + faceHealthIndex + " 0");
        setDimensions();
        setDelayedStAnimation();
    }

    private void setDelayedStAnimation() {
        addAction(Actions.sequence(
                Actions.delay(1f),
                Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        setSTAnimation(healthIndex);
                    }
                })
        ));
    }

    private void setDimensions() {
        setWidth(Gdx.graphics.getWidth() * 1 / 3f);
        setSize(getWidth(), getWidth() / (5 / 1f));
        setPosition(Gdx.graphics.getWidth() * 1 / 2f - getWidth() / 2f, 0f);
    }

    private void initializeSTAnimations(int faceHealthIndex) {
        Array<TextureAtlas.AtlasRegion> animationImages = new Array();
        for (int i = 0; i <= 4; i++) {
            animationImages.add(BaseGame.textureAtlas.findRegion("hud/ST" + i + " 0"));
            for (int j = 0; j < 6; j++)
                animationImages.add(BaseGame.textureAtlas.findRegion("hud/ST" + i + " 1"));
            animationImages.add(BaseGame.textureAtlas.findRegion("hud/ST" + i + " 2"));
            stAnimations.add(new Animation(.5f, animationImages, Animation.PlayMode.LOOP_RANDOM));
            animationImages.clear();
        }
        setSTAnimation(faceHealthIndex);
    }
}
