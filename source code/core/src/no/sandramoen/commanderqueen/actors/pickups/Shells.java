package no.sandramoen.commanderqueen.actors.pickups;

import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.utils.Array;

import no.sandramoen.commanderqueen.actors.Tile;
import no.sandramoen.commanderqueen.utils.GameUtils;
import no.sandramoen.commanderqueen.utils.Stage3D;

public class Shells extends Pickup {

    public Shells(float y, float z, Stage3D s, int amount, DecalBatch batch, Array<Tile> tiles) {
        super(y, z, s, batch, tiles);
        this.amount = amount;

        setImage("pickups/shells");

        decal.setDimensions(1f, .5f);
        decal.setPosition(GameUtils.getPositionRelativeToFloor(.5f), y, z);
    }
}
