package no.sandramoen.commanderqueen.actors.pickups;

import no.sandramoen.commanderqueen.actors.characters.Player;
import no.sandramoen.commanderqueen.utils.BaseGame;
import no.sandramoen.commanderqueen.utils.GameUtils;
import no.sandramoen.commanderqueen.utils.Stage3D;

public class Health extends Pickup {

    public Health(float y, float z, Stage3D s, int amount, Player player) {
        super(y, z, s, player);
        this.amount = amount;

        buildModel(1.2f, 1.2f, .001f, true);
        setPosition(GameUtils.getPositionRelativeToFloor(1.2f), y, z);
        setBaseRectangle();

        if (amount == 1)
            loadImage("pickups/health small");
        else if (amount == 100)
            loadImage("pickups/health medium");
    }

    @Override
    public void playSound() {
        super.playSound();
        BaseGame.healthPickupSound.play(BaseGame.soundVolume);
    }
}
