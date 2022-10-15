package no.sandramoen.terfenstein3D.utils.pathFinding;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.math.Vector3;

import no.sandramoen.terfenstein3D.actors.Tile;

public class Edge implements Connection<Tile> {
    private float cost;
    private Tile fromTile;
    private Tile toTile;

    public Edge(Tile fromTile, Tile toTile) {
        this.fromTile = fromTile;
        this.toTile = toTile;
        cost = Vector3.dst(
                fromTile.position.x,
                fromTile.position.y,
                fromTile.position.z,
                toTile.position.x,
                toTile.position.y,
                toTile.position.z
        );
    }

    @Override
    public float getCost() {
        return cost;
    }

    @Override
    public Tile getFromNode() {
        return fromTile;
    }

    @Override
    public Tile getToNode() {
        return toTile;
    }
}
