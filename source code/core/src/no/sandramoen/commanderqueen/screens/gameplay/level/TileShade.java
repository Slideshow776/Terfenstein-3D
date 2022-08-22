package no.sandramoen.commanderqueen.screens.gameplay.level;

import com.badlogic.gdx.graphics.Color;

import no.sandramoen.commanderqueen.actors.utils.baseActors.BaseActor3D;
import no.sandramoen.commanderqueen.utils.GameUtils;
import no.sandramoen.commanderqueen.utils.Stage3D;

public class TileShade extends BaseActor3D {
    public Color color;

    public TileShade(float y, float z, float width, float height, Color color, Stage3D stage3D) {
        super(0, y, z, stage3D);
        this.color = color;
        initializeModel(y, z, width, height, false);
    }

    private void initializeModel(float y, float z, float width, float height, boolean isVisible) {
        buildModel(width, .25f, height, isVisible);
        setPosition(GameUtils.getPositionRelativeToFloor(.25f), y, z);
        this.isVisible = isVisible;
        setBaseRectangle();
        if (isVisible) {
            loadImage("alphaPixel");
            setColor(Color.GREEN);
        }
    }
}
