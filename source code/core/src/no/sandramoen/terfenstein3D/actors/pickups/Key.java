package no.sandramoen.terfenstein3D.actors.pickups;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;

import no.sandramoen.terfenstein3D.actors.characters.Player;
import no.sandramoen.terfenstein3D.utils.BaseGame;
import no.sandramoen.terfenstein3D.utils.GameUtils;
import no.sandramoen.terfenstein3D.utils.Stage3D;

public class Key extends Pickup {
    public String color;
    private float totalTime;

    public Key(float y, float z, Stage3D s, String color, Player player) {
        super(y, z, s, player);
        this.color = color;

        buildModel(1.2f, .7f, .001f, true);
        setPosition(GameUtils.getPositionRelativeToFloor(4), y, z);
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
    public void act(float dt) {
        super.act(dt);
        totalTime += dt;

        setScale(
                scale.x * MathUtils.cos(2 * totalTime) * .1f + 1,
                scale.y * MathUtils.sin(2 * totalTime) * .1f + 1,
                .001f
        );
    }

    @Override
    public void playSound() {
        super.playSound();
        BaseGame.keySound.play(BaseGame.soundVolume);
    }
}
