package no.sandramoen.commanderqueen.utils.pathFinding;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

import no.sandramoen.commanderqueen.actors.Tile;

public class TileGraph implements IndexedGraph<Tile> {
    public Array<Tile> tiles = new Array();

    private int lastNodeIndex = 0;
    private TileHeuristic tileHeuristic = new TileHeuristic();
    private Array<Edge> edges = new Array();

    // Map of Tiles to Edges starting in that Tile.
    private ObjectMap<Tile, Array<Connection<Tile>>> edgesMap = new ObjectMap();

    public void addTile(Tile tile) {
        tile.index = lastNodeIndex;
        lastNodeIndex++;
        tiles.add(tile);
    }

    public void connectTiles(Tile fromTile, Tile toTile) {
        Edge edge = new Edge(fromTile, toTile);
        if (!edgesMap.containsKey(fromTile))
            edgesMap.put(fromTile, new Array());
        edgesMap.get(fromTile).add(edge);
        edges.add(edge);
    }

    public GraphPath<Tile> findPath(Tile startTile, Tile goalTile) {
        GraphPath<Tile> tilePath = new DefaultGraphPath();
        new IndexedAStarPathFinder<>(this).searchNodePath(startTile, goalTile, tileHeuristic, tilePath);
        // debugTilePath(tilePath, startTile, goalTile);
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

    public void debugConnections() {
        int tileToBeExamined = 0;
        Gdx.app.log(getClass().getSimpleName(), "tileGraph.tiles.size: " + tiles.size);
        Gdx.app.log(getClass().getSimpleName(), "tileGraph.getConnections(tiles.get(" + tileToBeExamined + ")): " + getConnections(tiles.get(tileToBeExamined)).size);

        tiles.get(tileToBeExamined).setColor(Color.GREEN);
        for (int i = 0; i < getConnections(tiles.get(tileToBeExamined)).size; i++) {
            getConnections(tiles.get(tileToBeExamined)).get(i).getToNode().setColor(Color.YELLOW);
        }
    }

    private void debugTilePath(GraphPath<Tile> tilePath, Tile startTile, Tile goalTile) {
        for (Tile tile : tiles)
            tile.setColor(Color.WHITE);
        for (Tile tile : tilePath)
            tile.setColor(Color.YELLOW);
        startTile.setColor(Color.RED);
        goalTile.setColor(Color.GREEN);
    }
}
