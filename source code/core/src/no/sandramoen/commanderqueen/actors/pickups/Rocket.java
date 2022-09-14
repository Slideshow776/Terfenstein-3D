package no.sandramoen.commanderqueen.actors.pickups;

import no.sandramoen.commanderqueen.actors.characters.Player;
import no.sandramoen.commanderqueen.utils.BaseGame;
import no.sandramoen.commanderqueen.utils.GameUtils;
import no.sandramoen.commanderqueen.utils.Stage3D;

public class Rocket extends Pickup {

    public Rocket(float y, float z, Stage3D s, int amount, Player player) {
        super(y, z, s, player);
        this.amount = amount;

        buildModel(.5f, .8f, .001f, true);
        setPosition(GameUtils.getPositionRelativeToFloor(.8f), y, z);
        setBaseRectangle();

        loadImage("pickups/rocket");
    }

    @Override
    public void playSound() {
        super.playSound();
        BaseGame.ammoPickupSound.play(BaseGame.soundVolume);
    }
}
