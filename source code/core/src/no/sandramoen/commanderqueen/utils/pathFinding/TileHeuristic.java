package no.sandramoen.commanderqueen.utils.pathFinding;

import com.badlogic.gdx.ai.pfa.Heuristic;
import com.badlogic.gdx.math.Vector3;

import no.sandramoen.commanderqueen.actors.Tile;

public class TileHeuristic implements Heuristic<Tile> {
    @Override
    public float estimate(Tile node, Tile endNode) {
        return Vector3.dst(
                node.position.x,
                node.position.y,
                node.position.z,
                endNode.position.x,
                endNode.position.y,
                endNode.position.z
        );
    }
}
