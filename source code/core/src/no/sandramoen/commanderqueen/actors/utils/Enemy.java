package no.sandramoen.commanderqueen.actors.utils;

import com.badlogic.gdx.math.MathUtils;

import no.sandramoen.commanderqueen.actors.Player;
import no.sandramoen.commanderqueen.utils.GameUtils;
import no.sandramoen.commanderqueen.utils.Stage3D;

public class Enemy extends Cuboid {
    private Player player;
    private float angleFacingPlayer;

    public Enemy(float y, float z, Stage3D s, Player player) {
        // super(y, z, 4, 3, .0001f, s);
        super(y, z, 4, 3, .1f, s);
        this.player = player;
        setPosition(GameUtils.getPositionRelativeToFloor(3), y, z);
    }

    @Override
    public void act(float dt) {
        super.act(dt);
        angleFacingPlayer = MathUtils.atan(
                Math.abs(position.z - player.position.z) /
                        Math.abs(position.y - player.position.y)
        ) * MathUtils.radiansToDegrees - 90;

        if (position.y - player.position.y > 0)
            angleFacingPlayer *= -1;
        if (position.z - player.position.z > 0)
            angleFacingPlayer *= -1;

        setTurnAngle(angleFacingPlayer);
    }
}
