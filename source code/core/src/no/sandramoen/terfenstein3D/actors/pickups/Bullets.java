package no.sandramoen.terfenstein3D.actors.pickups;

import no.sandramoen.terfenstein3D.actors.characters.Player;
import no.sandramoen.terfenstein3D.utils.BaseGame;
import no.sandramoen.terfenstein3D.utils.GameUtils;
import no.sandramoen.terfenstein3D.utils.Stage3D;

public class Bullets extends Pickup {

    public Bullets(float y, float z, Stage3D s, int amount, Player player) {
        super(y, z, s, player);
        this.amount = amount;

        buildModel(.8f, .9f, .001f, true);
        setPosition(GameUtils.getPositionRelativeToFloor(.8f), y, z);
        setBaseRectangle();

        loadImage("pickups/bullet");
    }

    @Override
    public void playSound() {
        super.playSound();
        BaseGame.ammoPickupSound.play(BaseGame.soundVolume);
    }
}
