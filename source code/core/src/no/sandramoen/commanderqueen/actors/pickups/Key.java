package no.sandramoen.commanderqueen.actors.pickups;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;

import no.sandramoen.commanderqueen.actors.characters.Player;
import no.sandramoen.commanderqueen.utils.BaseGame;
import no.sandramoen.commanderqueen.utils.GameUtils;
import no.sandramoen.commanderqueen.utils.Stage3D;

public class Key extends Pickup {
    public String color;
    public Key(float y, float z, Stage3D s, String color, Player player) {
        super(y, z, s, player);
        this.color = color;

        buildModel(1f, .5f, .001f, true);
        setPosition(GameUtils.getPositionRelativeToFloor(.5f), y, z);
        setBaseRectangle();


        if (color.equalsIgnoreCase("red"))
            loadImage("pickups/redKey");
        else if (color.equalsIgnoreCase("green"))
            loadImage("pickups/greenKey");
        else if (color.equalsIgnoreCase("blue"))
            loadImage("pickups/blueKey");
        else
            Gdx.app.error(getClass().getSimpleName(), "Error: unknown color for key => " + color);
    }

    @Override
    public void playSound() {
        super.playSound();
        BaseGame.holyBallExplosionSound.play(BaseGame.soundVolume); // TODO: need it's own sound
    }
}
