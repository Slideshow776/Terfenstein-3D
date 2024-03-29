package no.sandramoen.terfenstein3D.actors.pickups;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import no.sandramoen.terfenstein3D.actors.characters.Player;
import no.sandramoen.terfenstein3D.actors.utils.baseActors.BaseActor3D;
import no.sandramoen.terfenstein3D.utils.GameUtils;
import no.sandramoen.terfenstein3D.utils.Stage3D;

public class Pickup extends BaseActor3D {
    public int amount;

    private PerspectiveCamera camera;
    private Player player;

    public Pickup(float y, float z, Stage3D s, Player player) {
        super(0, y, z, s);
        this.camera = s.camera;
        this.player = player;

        initializeModel(y, z);
    }

    @Override
    public void act(float dt) {
        super.act(dt);
        setTurnAngle(GameUtils.getAngleTowardsBaseActor3D(this, player));
    }

    public void playSound() {

    }

    private void initializeModel(float y, float z) {
        buildModel(2, .5f, 2, true);
        setPosition(GameUtils.getPositionRelativeToFloor(.5f), y, z);
        setBaseRectangle();
    }
}
