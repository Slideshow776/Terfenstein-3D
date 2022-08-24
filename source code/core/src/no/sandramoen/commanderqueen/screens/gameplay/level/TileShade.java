package no.sandramoen.commanderqueen.screens.gameplay.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;

import no.sandramoen.commanderqueen.actors.characters.enemy.Enemy;
import no.sandramoen.commanderqueen.actors.utils.baseActors.BaseActor3D;
import no.sandramoen.commanderqueen.utils.GameUtils;
import no.sandramoen.commanderqueen.utils.Stage3D;

public class TileShade extends BaseActor3D {
    public Color color0;
    public Color color1;
    public boolean isColor0 = true;

    private float flickerFrequency;
    private float flickerCounter;

    public TileShade(long id, float y, float z, float width, float height, Color color0, Color color1, float flickerFrequency, Stage3D stage3D) {
        super(0, y, z, stage3D);
        this.color0 = color0;
        this.color1 = color1;
        this.flickerFrequency = flickerFrequency;

        initializeModel(y, z, width, height, false);

        checkColorAlpha(id, color0);
        checkColorAlpha(id, color1);
    }

    @Override
    public void act(float dt) {
        super.act(dt);

        if (flickerFrequency > 0)
            if (flickerCounter > flickerFrequency) {
                flickerCounter = 0;
                isColor0 = !isColor0;
            } else {
                flickerCounter += dt;
            }
    }

    @Override
    public void setColor(Color color) {
    }

    public void setActorColor(BaseActor3D baseActor3D) {
        if (baseActor3D.boundingPolygon != null && overlaps(baseActor3D)) {
            if (isColor0)
                baseActor3D.setColor(color0);
            else
                baseActor3D.setColor(color1);
        }
    }

    private void initializeModel(float y, float z, float width, float height, boolean isVisible) {
        buildModel(width, .001f, height, isVisible);
        setPosition(GameUtils.getPositionRelativeToFloor(.25f), y, z);
        this.isVisible = isVisible;
        setBaseRectangle();
        if (isVisible) {
            loadImage("alphaPixel");
            setColor(Color.GREEN);
        }
    }

    private void checkColorAlpha(long id, Color color) {
        if (color.a != 1)
            Gdx.app.error(getClass().getSimpleName(), "Warning: Shade #" + id + "color's alpha is: " + color.a);
    }
}
