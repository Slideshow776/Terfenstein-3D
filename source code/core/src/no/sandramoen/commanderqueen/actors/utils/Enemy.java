package no.sandramoen.commanderqueen.actors.utils;

import no.sandramoen.commanderqueen.actors.Player;
import no.sandramoen.commanderqueen.utils.GameUtils;
import no.sandramoen.commanderqueen.utils.Stage3D;

public class Enemy extends BaseActor3D {
    protected boolean dead = false;
    private Player player;

    public Enemy(float y, float z, Stage3D s, Player player) {
        super(0, y, z, s);
        this.player = player;
        buildModel(4, 3, .1f);
        setBaseRectangle();
        setPosition(GameUtils.getPositionRelativeToFloor(3), y, z);
    }

    @Override
    public void act(float dt) {
        super.act(dt);
        setTurnAngle(GameUtils.getAngleTowardsPlayer(this, player));
    }
}
