package no.sandramoen.commanderqueen.actors.pickups;

import no.sandramoen.commanderqueen.actors.characters.Player;
import no.sandramoen.commanderqueen.utils.GameUtils;
import no.sandramoen.commanderqueen.utils.Stage3D;

public class Armor extends Pickup {

    public Armor(float y, float z, Stage3D s, Player player, int amount) {
        super(y, z, s, player);
        this.amount = amount;
        buildModel(1f, 1f, .001f, true);

        if (amount == 1)
            loadImage("pickups/armor small");
        else if (amount == 100)
            loadImage("pickups/armor medium");
        else if (amount == 200)
            loadImage("pickups/armor big");

        setPosition(GameUtils.getPositionRelativeToFloor(1f), y, z);
        setBaseRectangle();
    }
}

