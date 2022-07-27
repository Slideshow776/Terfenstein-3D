package no.sandramoen.commanderqueen.utils;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

import no.sandramoen.commanderqueen.actors.Tile;

public class TileGraph implements IndexedGraph<Tile> {
    TileHeuristic tileHeuristic = new TileHeuristic();
    public Array<Tile> tiles = new Array();
    Array<Edge> edges = new Array();

    // Map of Tiles to Edges starting in that Tile.
    ObjectMap<Tile, Array<Connection<Tile>>> edgesMap = new ObjectMap();

    private int lastNodeIndex = 0;

    public void addTile(Tile tile) {
        tile.index = lastNodeIndex;
        lastNodeIndex++;
        tiles.add(tile);
    }

    public void connectTiles(Tile fromTile, Tile toTile) {
        Edge edge = new Edge(fromTile, toTile);
        if (!edgesMap.containsKey(fromTile))
            edgesMap.put(fromTile, new Array<Connection<Tile>>());
        edgesMap.get(fromTile).add(edge);
        edges.add(edge);
    }

    public GraphPath<Tile> findPath(Tile startTile, Tile goalTile) {
        GraphPath<Tile> tilePath = new DefaultGraphPath();
        new IndexedAStarPathFinder<>(this).searchNodePath(startTile, goalTile, tileHeuristic, tilePath);
        return tilePath;
    }

    @Override
    public int getIndex(Tile node) {
        return node.index;
    }

    @Override
    public int getNodeCount() {
        return lastNodeIndex;
    }

    @Override
    public Array<Connection<Tile>> getConnections(Tile fromNode) {
        if (edgesMap.containsKey(fromNode))
            return edgesMap.get(fromNode);
        return new Array<>(0);
    }
}
