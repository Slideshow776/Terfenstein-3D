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
            animationImages.add(BaseGame.textureAtlas.findRegion("hud/HUD-ST" + j + "0"));
            for (int k = 0; k < 6; k++)
                animationImages.add(BaseGame.textureAtlas.findRegion("hud/HUD-ST" + j + "1"));
            animationImages.add(BaseGame.textureAtlas.findRegion("hud/HUD-ST" + j + "2"));
            stAnimations.add(new Animation(.5f, animationImages, Animation.PlayMode.LOOP));
            animationImages.clear();
        }
        setSTAnimation(i);
        setZIndex(0);
    }

    public void setKillFace(final int i) {
        currentHealthIndecy = i;
        loadImage("hud/HUD-KILL" + i);
        setDimensions();
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

    public void setSTAnimation(int st) {
        currentHealthIndecy = st;
        setAnimation(stAnimations.get(st));
        setDimensions();
    }

    public void setDead() {
        loadImage("hud/HUD-DEAD");
        setDimensions();
    }

    private void setDimensions() {
        setWidth(Gdx.graphics.getWidth() * 1 / 3);
        setSize(getWidth(), getWidth() / (5 / 1));
        setPosition(Gdx.graphics.getWidth() * 1 / 2 - getWidth() / 2, 0f);
    }
}
