package no.sandramoen.terfenstein3D.actors.pickups;

import com.badlogic.gdx.math.MathUtils;

import no.sandramoen.terfenstein3D.actors.characters.Player;
import no.sandramoen.terfenstein3D.utils.BaseGame;
import no.sandramoen.terfenstein3D.utils.GameUtils;
import no.sandramoen.terfenstein3D.utils.Stage3D;

public class Health extends Pickup {

    public Health(float y, float z, Stage3D s, int amount, Player player) {
        super(y, z, s, player);
        this.amount = amount;

        buildModel(1.4f, 1.4f, .001f, true);
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
        BaseGame.healthPickupSound.play(BaseGame.soundVolume, MathUtils.random(.9f, 1.1f), 0);
    }
}
