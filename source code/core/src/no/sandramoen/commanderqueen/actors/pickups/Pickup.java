package no.sandramoen.commanderqueen.actors.pickups;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.utils.Array;

import no.sandramoen.commanderqueen.actors.Tile;
import no.sandramoen.commanderqueen.actors.characters.Player;
import no.sandramoen.commanderqueen.actors.utils.baseActors.BaseActor3D;
import no.sandramoen.commanderqueen.utils.GameUtils;
import no.sandramoen.commanderqueen.utils.Stage3D;

public class Pickup extends BaseActor3D {
    public int amount;

    private PerspectiveCamera camera;
    private Player player;

    public Pickup(float y, float z, Stage3D s, Player player, Array<Tile> tiles) {
        super(0, y, z, s);
        this.camera = s.camera;
        this.player = player;

        initializeModel(y, z);
        checkIfIlluminated(tiles);
    }

    @Override
    public void act(float dt) {
        super.act(dt);
        setTurnAngle(GameUtils.getAngleTowardsBaseActor3D(this, player));
    }

    public void playSound() {

    }

    protected void checkIfIlluminated(Array<Tile> tiles) {
        for (Tile tile : tiles) {
            if (overlaps(tile)) {
                GameUtils.illuminateBaseActor(this, tile);
                break;
            }
        }
    }

    private void initializeModel(float y, float z) {
        buildModel(2, .5f, 2, true);
        setPosition(GameUtils.getPositionRelativeToFloor(.5f), y, z);
        setBaseRectangle();
    }
}
