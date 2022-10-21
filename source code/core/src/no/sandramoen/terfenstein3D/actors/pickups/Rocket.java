package no.sandramoen.terfenstein3D.actors.pickups;

import no.sandramoen.terfenstein3D.actors.characters.Player;
import no.sandramoen.terfenstein3D.utils.BaseGame;
import no.sandramoen.terfenstein3D.utils.GameUtils;
import no.sandramoen.terfenstein3D.utils.Stage3D;

public class Rocket extends Pickup {

    public Rocket(float y, float z, Stage3D s, int amount, Player player) {
        super(y, z, s, player);
        this.amount = amount;

        buildModel(.9f, .9f, .001f, true);
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
