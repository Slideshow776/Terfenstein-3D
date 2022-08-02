package no.sandramoen.commanderqueen.actors.pickups;

import no.sandramoen.commanderqueen.actors.characters.Player;
import no.sandramoen.commanderqueen.utils.GameUtils;
import no.sandramoen.commanderqueen.utils.Stage3D;

public class Ammo extends Pickup {

    public Ammo(float y, float z, Stage3D s, Player player, int amount) {
        super(y, z, s, player);
        this.amount = amount;
        buildModel(.4f, .5f, .001f, true);
        loadImage("pickups/ammo");
        setPosition(GameUtils.getPositionRelativeToFloor(.5f), y, z);
        setBaseRectangle();
    }
}
