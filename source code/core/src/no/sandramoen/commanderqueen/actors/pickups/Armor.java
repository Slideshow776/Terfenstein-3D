package no.sandramoen.commanderqueen.actors.pickups;

import com.badlogic.gdx.utils.Array;

import no.sandramoen.commanderqueen.actors.Tile;
import no.sandramoen.commanderqueen.actors.characters.Player;
import no.sandramoen.commanderqueen.utils.BaseGame;
import no.sandramoen.commanderqueen.utils.GameUtils;
import no.sandramoen.commanderqueen.utils.Stage3D;

public class Armor extends Pickup {

    public Armor(float y, float z, Stage3D s, int amount, Player player, Array<Tile> tiles) {
        super(y, z, s, player, tiles);
        this.amount = amount;

        buildModel(1f, 1f, .001f, true);
        setPosition(GameUtils.getPositionRelativeToFloor(1f), y, z);
        setBaseRectangle();

        if (amount == 1)
            loadImage("pickups/armor small");
        else if (amount == 100)
            loadImage("pickups/armor medium");
        else if (amount == 200)
            loadImage("pickups/armor big");
    }

    @Override
    public void playSound() {
        super.playSound();
        BaseGame.armorPickupSound.play(BaseGame.soundVolume);
    }
}
