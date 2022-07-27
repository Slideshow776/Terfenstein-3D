package no.sandramoen.commanderqueen.actors.pickups;

import no.sandramoen.commanderqueen.actors.characters.Player;
import no.sandramoen.commanderqueen.actors.utils.BaseActor3D;
import no.sandramoen.commanderqueen.utils.GameUtils;
import no.sandramoen.commanderqueen.utils.Stage3D;

public class Pickup extends BaseActor3D {
    private Player player;

    public Pickup(float y, float z, Stage3D s, Player player) {
        super(0, y, z, s);
        this.player = player;
    }

    @Override
    public void act(float dt) {
        super.act(dt);
        setTurnAngle(GameUtils.getAngleTowardsBaseActor3D(this, player));
    }
}
