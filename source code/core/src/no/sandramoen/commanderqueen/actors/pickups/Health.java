package no.sandramoen.commanderqueen.actors.pickups;

import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.utils.Array;

import no.sandramoen.commanderqueen.actors.Tile;
import no.sandramoen.commanderqueen.utils.Stage3D;

public class Health extends Pickup {

    public Health(float y, float z, Stage3D s, int amount, DecalBatch batch, Array<Tile> tiles) {
        super(y, z, s, batch, tiles);
        this.amount = amount;

        if (amount == 1)
            setImage("pickups/health small");
        else if (amount == 100)
            setImage("pickups/health medium");
    }
}
