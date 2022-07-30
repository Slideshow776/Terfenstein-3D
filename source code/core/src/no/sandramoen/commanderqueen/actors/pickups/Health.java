package no.sandramoen.commanderqueen.actors.pickups;

import no.sandramoen.commanderqueen.actors.characters.Player;
import no.sandramoen.commanderqueen.utils.GameUtils;
import no.sandramoen.commanderqueen.utils.Stage3D;

public class Health extends Pickup {

    public Health(float y, float z, Stage3D s, Player player, int amount) {
        super(y, z, s, player);
        this.amount = amount;
        buildModel(1f, 1f, .001f, true);

        if (amount == 1)
            loadImage("pickups/health small");
        else if (amount == 100)
            loadImage("pickups/health medium");

        setPosition(GameUtils.getPositionRelativeToFloor(1f), y, z);
        setBaseRectangle();
    }
}