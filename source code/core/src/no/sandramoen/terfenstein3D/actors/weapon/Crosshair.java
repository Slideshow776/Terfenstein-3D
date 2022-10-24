package no.sandramoen.terfenstein3D.actors.weapon;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;

import no.sandramoen.terfenstein3D.actors.Barrel;
import no.sandramoen.terfenstein3D.actors.characters.Menig;
import no.sandramoen.terfenstein3D.actors.utils.baseActors.BaseActor;
import no.sandramoen.terfenstein3D.actors.utils.baseActors.BaseActor3D;
import no.sandramoen.terfenstein3D.utils.BaseGame;

public class Crosshair extends BaseActor {
    public Crosshair(Stage stage) {
        super(0, 0, stage);
        loadImage("crosshair");
        setSize(Gdx.graphics.getWidth() * .008f, Gdx.graphics.getWidth() * .008f);
        centerAtPosition(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f);
    }

    public void setColorIfShootable(Array<BaseActor3D> shootable, int index) {
        if (index >= 0) {
            if (shootable.get(index) instanceof Menig || shootable.get(index) instanceof Barrel)
                setColor(BaseGame.redColor);
            else
                setColor(BaseGame.whiteColor);
        } else
            setColor(BaseGame.whiteColor);
    }

    public void setCrosshairSize(boolean isHoldingDown) {
        if (isHoldingDown) {
            loadImage("crosshair_holdDown");
            setSize(Gdx.graphics.getWidth() * .05f, Gdx.graphics.getWidth() * .05f);
        }
        else {
            loadImage("crosshair");
            setSize(Gdx.graphics.getWidth() * .008f, Gdx.graphics.getWidth() * .008f);
        }
        centerAtPosition(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f);
    }
}
