package no.sandramoen.commanderqueen.actors.pickups;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.utils.Array;

import no.sandramoen.commanderqueen.actors.Tile;
import no.sandramoen.commanderqueen.actors.utils.baseActors.BaseActor3D;
import no.sandramoen.commanderqueen.utils.BaseGame;
import no.sandramoen.commanderqueen.utils.GameUtils;
import no.sandramoen.commanderqueen.utils.Stage3D;

public class Pickup extends BaseActor3D {
    public int amount;
    public Decal decal;

    private PerspectiveCamera camera;
    private DecalBatch batch;

    public Pickup(float y, float z, Stage3D s, DecalBatch batch, Array<Tile> tiles) {
        super(0, y, z, s);
        this.camera = s.camera;
        this.batch = batch;

        initializeModel(y, z);
        initializeDecal(y, z);
        checkIfIlluminated(tiles);
    }

    @Override
    public void act(float dt) {
        super.act(dt);
        GameUtils.lookAtCameraIn2D(decal, stage3D.camera);
        batch.add(decal);
    }

    protected void setImage(String path) {
        decal.setTextureRegion(BaseGame.textureAtlas.findRegion(path));
    }

    private void checkIfIlluminated(Array<Tile> tiles) {
        for (Tile tile : tiles) {
            if (overlaps(tile)) {
                GameUtils.illuminateDecal(decal, tile);
                break;
            }
        }
    }

    private void initializeModel(float y, float z) {
        isVisible = false;
        buildModel(2, .5f, 2, false);
        setPosition(GameUtils.getPositionRelativeToFloor(.5f), y, z);
        setBaseRectangle();
    }

    private void initializeDecal(float y, float z) {
        decal = Decal.newDecal(BaseGame.textureAtlas.findRegion("whitePixel"), true);
        decal.setDimensions(1, 1);
        decal.setPosition(GameUtils.getPositionRelativeToFloor(1), y, z);
    }
}
