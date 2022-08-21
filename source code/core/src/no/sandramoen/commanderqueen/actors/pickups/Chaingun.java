package no.sandramoen.commanderqueen.actors.pickups;

import com.badlogic.gdx.utils.Array;

import no.sandramoen.commanderqueen.actors.Tile;
import no.sandramoen.commanderqueen.actors.characters.Player;
import no.sandramoen.commanderqueen.utils.BaseGame;
import no.sandramoen.commanderqueen.utils.GameUtils;
import no.sandramoen.commanderqueen.utils.Stage3D;

public class Chaingun extends Pickup {

    public Chaingun(float y, float z, Stage3D s, int amount, Player player, Array<Tile> tiles) {
        super(y, z, s, player, tiles);
        this.amount = amount;

        buildModel(3f, 1f, .001f, true);
        setPosition(GameUtils.getPositionRelativeToFloor(1f), y, z);
        setBaseRectangle();

        loadImage("pickups/chaingun");
    }

    @Override
    public void playSound() {
        super.playSound();
        BaseGame.ammoPickupSound.play(BaseGame.soundVolume);
    }
}
