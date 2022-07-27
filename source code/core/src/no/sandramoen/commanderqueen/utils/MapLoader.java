package no.sandramoen.commanderqueen.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import no.sandramoen.commanderqueen.actors.Barrel;
import no.sandramoen.commanderqueen.actors.Tile;
import no.sandramoen.commanderqueen.actors.characters.Ghoul;
import no.sandramoen.commanderqueen.actors.characters.Player;
import no.sandramoen.commanderqueen.actors.pickups.Ammo;
import no.sandramoen.commanderqueen.actors.pickups.Armor;
import no.sandramoen.commanderqueen.actors.pickups.Health;
import no.sandramoen.commanderqueen.actors.pickups.Pickup;
import no.sandramoen.commanderqueen.actors.utils.BaseActor3D;
import no.sandramoen.commanderqueen.actors.utils.Enemy;
import no.sandramoen.commanderqueen.actors.utils.TilemapActor;

public class MapLoader {
    private Stage3D stage3D;
    private TilemapActor tilemap;
    private Array<Tile> tiles;
    private Array<Enemy> enemies;
    private Array<Pickup> pickups;
    private Array<BaseActor3D> shootable;

    public Player player;
    public TileGraph tileGraph;

    public MapLoader(TilemapActor tilemap, Array<Tile> tiles, Stage3D stage3D, Player player, Array<BaseActor3D> shootable,
                     Array<Pickup> pickups, Array<Enemy> enemies) {
        this.tilemap = tilemap;
        this.tiles = tiles;
        this.stage3D = stage3D;
        this.player = player;
        this.shootable = shootable;
        this.pickups = pickups;
        this.enemies = enemies;

        initializeTiles();
        initializePlayer();
        initializeEnemies();
        initializeBarrels();
        initializeHealth();
        initializeAmmo();
        initializeArmor();
        initializeLights();
    }

    private void initializeTiles() {
        createTiles();
        addTilesToAIGraph();
        createAIGraphConnections();
    }

    private void createTiles() {
        Array<String> tileTypes = new Array<>();
        tileTypes.add("walls", "ceilings", "floors");
        Array<String> tileTextures = new Array<>();
        tileTextures.add("big plates", "lonplate", "light big plates", "light lonplate");
        tileTextures.add("lights 0");

        for (String type : tileTypes) {
            for (String texture : tileTextures) {
                for (MapObject obj : tilemap.getTileList(type, texture)) {
                    MapProperties props = obj.getProperties();
                    float y = (Float) props.get("x") * BaseGame.unitScale;
                    float z = (Float) props.get("y") * BaseGame.unitScale;
                    Tile tile = new Tile(y, z, type, texture, stage3D);
                    tiles.add(tile);
                    shootable.add(tiles.get(tiles.size - 1));
                }
            }
        }
    }

    private void addTilesToAIGraph() {
        tileGraph = new TileGraph();
        for (int i = 0; i < tiles.size; i++)
            tileGraph.addTile(tiles.get(i));
    }

    private void createAIGraphConnections() {
        for (int i = 0; i < tiles.size; i++) {
            for (int j = 0; j < tiles.size; j++) {
                if (tiles.get(i) != tiles.get(j)) {
                    if (tiles.get(i).position.dst(tiles.get(j).position) <= Tile.diagonalLength) {
                        tileGraph.connectTiles(tiles.get(i), tiles.get(j));
                    }
                }
            }
        }
    }

    private void debugAIGraphConnection() {
        int tileToBeExamined = 0;
        tiles.get(tileToBeExamined).setColor(Color.GREEN);
        // tileGraph.findPath(tiles.get(0), tiles.get(tiles.size - 1));
        System.out.println("tileGraph.tiles.size: " + tileGraph.tiles.size);
        System.out.println("tileGraph.getConnections(tiles.get(" + tileToBeExamined + ")): " + tileGraph.getConnections(tiles.get(tileToBeExamined)).size);
        for (int i = 0; i < tileGraph.getConnections(tiles.get(tileToBeExamined)).size; i++) {
            tileGraph.getConnections(tiles.get(tileToBeExamined)).get(i).getToNode().setColor(Color.YELLOW);
        }
    }

    private void initializeLights() {
        int i = 0;
        for (MapObject obj : tilemap.getTileList("actors", "light")) {
            MapProperties props = obj.getProperties();
            float y = (Float) props.get("x") * BaseGame.unitScale;
            float z = (Float) props.get("y") * BaseGame.unitScale;
            PointLight pLight = new PointLight();
            pLight.set(new Color(.6f, .6f, .9f, 1f), new Vector3(Tile.height / 2, y, z), 50f);
            stage3D.environment.add(pLight);
            i++;
        }
        if (i > 0) Gdx.app.log(getClass().getSimpleName(), "added " + i + " pointLights to map");
    }

    private void initializePlayer() {
        MapObject startPoint = tilemap.getTileList("actors", "player start").get(0);
        float playerX = (float) startPoint.getProperties().get("x") * BaseGame.unitScale;
        float playerY = (float) startPoint.getProperties().get("y") * BaseGame.unitScale;
        float rotation = 0;
        if (startPoint.getProperties().get("rotation") != null)
            rotation = (float) startPoint.getProperties().get("rotation");
        rotation %= 360;
        player = new Player(playerX, playerY, stage3D, rotation);
    }

    private void initializeEnemies() {
        for (MapObject obj : tilemap.getTileList("actors", "enemy")) {
            MapProperties props = obj.getProperties();
            float x = (Float) props.get("x") * BaseGame.unitScale;
            float y = (Float) props.get("y") * BaseGame.unitScale;
            float rotation = 0;
            if (props.get("rotation") != null)
                rotation = (Float) props.get("rotation");
            rotation %= 360;
            enemies.add(new Ghoul(x, y, stage3D, player, rotation));
            shootable.add(enemies.get(enemies.size - 1));
        }

        for (Enemy enemy : enemies)
            enemy.setShootable(shootable);
    }

    private void initializeBarrels() {
        for (MapObject obj : tilemap.getTileList("actors", "barrel")) {
            MapProperties props = obj.getProperties();
            float x = (Float) props.get("x") * BaseGame.unitScale;
            float y = (Float) props.get("y") * BaseGame.unitScale;
            shootable.add(new Barrel(x, y, stage3D, player));
        }
    }

    private void initializeHealth() {
        for (MapObject obj : tilemap.getTileList("actors", "health")) {
            MapProperties props = obj.getProperties();
            float x = (Float) props.get("x") * BaseGame.unitScale;
            float y = (Float) props.get("y") * BaseGame.unitScale;
            pickups.add(new Health(x, y, stage3D, player));
        }
    }

    private void initializeAmmo() {
        for (MapObject obj : tilemap.getTileList("actors", "ammo")) {
            MapProperties props = obj.getProperties();
            float x = (Float) props.get("x") * BaseGame.unitScale;
            float y = (Float) props.get("y") * BaseGame.unitScale;
            pickups.add(new Ammo(x, y, stage3D, player));
        }
    }

    private void initializeArmor() {
        for (MapObject obj : tilemap.getTileList("actors", "armor")) {
            MapProperties props = obj.getProperties();
            float x = (Float) props.get("x") * BaseGame.unitScale;
            float y = (Float) props.get("y") * BaseGame.unitScale;
            pickups.add(new Armor(x, y, stage3D, player));
        }
    }
}
