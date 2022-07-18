package no.sandramoen.commanderqueen.actors.utils;

import no.sandramoen.commanderqueen.actors.Player;
import no.sandramoen.commanderqueen.utils.GameUtils;
import no.sandramoen.commanderqueen.utils.Stage3D;

public class Enemy extends Cuboid {
    protected boolean dead = false;
    private Player player;

    public Enemy(float y, float z, Stage3D s, Player player) {
        super(y, z, 4, 3, .0001f, s);
        this.player = player;
        setPosition(GameUtils.getPositionRelativeToFloor(3), y, z);
    }

    @Override
    public void act(float dt) {
        super.act(dt);
        setTurnAngle(GameUtils.getAngleTowardsPlayer(this, player));
    }
}
