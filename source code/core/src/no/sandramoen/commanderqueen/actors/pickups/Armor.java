package no.sandramoen.commanderqueen.actors.pickups;

import no.sandramoen.commanderqueen.actors.characters.Player;
import no.sandramoen.commanderqueen.utils.GameUtils;
import no.sandramoen.commanderqueen.utils.Stage3D;

public class Armor extends Pickup {

    public Armor(float y, float z, Stage3D s, Player player) {
        super(y, z, s, player);
        buildModel(1f, 1f, .001f, true);
        loadImage("pickups/armor");
        setPosition(GameUtils.getPositionRelativeToFloor(1f), y, z);
        setBaseRectangle();
    }
}

