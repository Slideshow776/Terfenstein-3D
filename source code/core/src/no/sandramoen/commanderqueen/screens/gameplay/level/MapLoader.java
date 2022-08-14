package no.sandramoen.commanderqueen.screens.gameplay.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;

import no.sandramoen.commanderqueen.actors.Barrel;
import no.sandramoen.commanderqueen.actors.Door;
import no.sandramoen.commanderqueen.actors.Tile;
import no.sandramoen.commanderqueen.actors.characters.Hund;
import no.sandramoen.commanderqueen.actors.characters.Menig;
import no.sandramoen.commanderqueen.actors.characters.Player;
import no.sandramoen.commanderqueen.actors.characters.Sersjant;
import no.sandramoen.commanderqueen.actors.hud.HUD;
import no.sandramoen.commanderqueen.actors.pickups.Bullets;
import no.sandramoen.commanderqueen.actors.pickups.Armor;
import no.sandramoen.commanderqueen.actors.pickups.Health;
import no.sandramoen.commanderqueen.actors.pickups.Pickup;
import no.sandramoen.commanderqueen.actors.pickups.Shells;
import no.sandramoen.commanderqueen.actors.pickups.Shotgun;
import no.sandramoen.commanderqueen.actors.props.Prop;
import no.sandramoen.commanderqueen.actors.utils.baseActors.BaseActor3D;
import no.sandramoen.commanderqueen.actors.characters.enemy.Enemy;
import no.sandramoen.commanderqueen.actors.utils.TilemapActor;
import no.sandramoen.commanderqueen.utils.BaseGame;
import no.sandramoen.commanderqueen.utils.Stage3D;
import no.sandramoen.commanderqueen.utils.pathFinding.TileGraph;

public class MapLoader {
    public Player player;
    public TileGraph tileGraph;

    private Stage3D stage3D;
    private TilemapActor tilemap;
    private Stage stage;
    private HUD hud;

    private Array<Tile> tiles;
    private Array<Enemy> enemies;
    private Array<Pickup> pickups;
    private Array<Tile> floorTiles;
    private Array<BaseActor3D> shootable;
    private Array<Door> doors;
    private DecalBatch decalBatch;

    private Array<Array<Tile>> patrols = new Array();

    public MapLoader(TilemapActor tilemap, Array<Tile> tiles, Stage3D stage3D, Player player, Array<BaseActor3D> shootable,
                     Array<Pickup> pickups, Array<Enemy> enemies, Stage stage, HUD hud, DecalBatch decalBatch, Array<Door> doors) {
        this.tilemap = tilemap;
        this.tiles = tiles;
        this.stage3D = stage3D;
        this.player = player;
        this.shootable = shootable;
        this.pickups = pickups;
        this.enemies = enemies;
        this.stage = stage;
        this.hud = hud;
        this.decalBatch = decalBatch;
        this.doors = doors;

        floorTiles = new Array();

        initializeTiles();
        initializePlayer();
        initializeDoors();
        initializeBarrels();
        initializeEnemies();
        initializePickups();
        initializeLights();
        initializeProps();
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
        tileTextures.add("lights 0", "flag 0");

        for (String type : tileTypes) {
            for (String texture : tileTextures) {
                for (MapObject obj : tilemap.getTileList(type, texture)) {
                    MapProperties props = obj.getProperties();
                    float y = (Float) props.get("x") * BaseGame.unitScale;
                    float z = (Float) props.get("y") * BaseGame.unitScale;

                    float width = (Float) props.get("width") * BaseGame.unitScale;
                    float height = (Float) props.get("height") * BaseGame.unitScale;

                    float rotation = 0;
                    if (props.get("rotation") != null)
                        rotation = (Float) props.get("rotation");
                    rotation %= 360;

                    float depth = (float) props.get("depth");

                    Tile tile = new Tile(y, z, width, depth, height, type, texture, stage3D, rotation);
                    tiles.add(tile);
                    shootable.add(tile);

                    String patrol = (String) props.get("patrol");
                    if (patrol != null && patrol.length() > 0 && patrol.equalsIgnoreCase("a")) {
                        if (patrols.size == 0)
                            patrols.add(new Array());
                        patrols.get(0).add(tile);
                    }
                }
            }
        }
    }

    private void addTilesToAIGraph() {
        tileGraph = new TileGraph();
        for (Tile tile : tiles)
            if (tile.type == "floors") {
                tileGraph.addTile(tile);
                floorTiles.add(tile);
            }
        // tileGraph.debugConnections();
    }

    private void createAIGraphConnections() {
        for (int i = 0; i < tileGraph.tiles.size; i++) {
            for (int j = 0; j < tileGraph.tiles.size; j++) {
                if (tileGraph.tiles.get(i) != tileGraph.tiles.get(j)) {
                    if (tileGraph.tiles.get(i).position.dst(tileGraph.tiles.get(j).position) <= Tile.height * 1.1f) {
                        tileGraph.connectTiles(tileGraph.tiles.get(i), tileGraph.tiles.get(j));
                    }
                }
            }
        }
    }

    private void initializeDoors() {
        for (MapObject obj : tilemap.getTileList("actors", "door")) {
            MapProperties props = obj.getProperties();
            float x = (Float) props.get("x") * BaseGame.unitScale;
            float y = (Float) props.get("y") * BaseGame.unitScale;
            float rotation = 0;
            if (props.get("rotation") != null)
                rotation = (Float) props.get("rotation");
            rotation %= 360;

            Door door = new Door(x, y, stage3D, stage, rotation, player);
            door.isLocked = (boolean) props.get("isLocked");

            doors.add(door);
            shootable.add(door);
        }
    }

    private void initializeLights() {
        int lightCount = 0;
        for (MapObject obj : tilemap.getTileList("actors", "light")) {
            MapProperties props = obj.getProperties();
            float y = (Float) props.get("x") * BaseGame.unitScale;
            float z = (Float) props.get("y") * BaseGame.unitScale;
            PointLight pLight = new PointLight();
            pLight.set(new Color(.6f, .6f, .9f, 1f), new Vector3(Tile.height / 2, y, z), 50f);
            stage3D.environment.add(pLight);
            lightCount++;
        }
        if (lightCount > 0)
            Gdx.app.log(getClass().getSimpleName(), "added " + lightCount + " pointLights to map");
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
        initializeEnemy("hund");
        initializeEnemy("menig");
        initializeEnemy("sersjant");
    }

    private void initializeEnemy(String type) {
        for (MapObject obj : tilemap.getTileList("actors", type)) {
            MapProperties props = obj.getProperties();
            float x = (Float) props.get("x") * BaseGame.unitScale;
            float y = (Float) props.get("y") * BaseGame.unitScale;
            float rotation = 0;
            if (props.get("rotation") != null)
                rotation = (Float) props.get("rotation");
            rotation %= 360;
            if (type.equalsIgnoreCase("hund"))
                enemies.add(new Hund(x, y, stage3D, player, rotation, tileGraph, floorTiles, stage, hud, decalBatch));
            if (type.equalsIgnoreCase("menig"))
                enemies.add(new Menig(x, y, stage3D, player, rotation, tileGraph, floorTiles, stage, hud, decalBatch));
            if (type.equalsIgnoreCase("sersjant"))
                enemies.add(new Sersjant(x, y, stage3D, player, rotation, tileGraph, floorTiles, stage, hud, decalBatch));
            shootable.add(enemies.get(enemies.size - 1));

            String patrol = (String) props.get("patrol");
            if (patrol != null && patrol.equalsIgnoreCase("a") && patrols.size > 0)
                enemies.get(enemies.size - 1).setPatrol(patrols.get(0));
        }

        for (Enemy enemy : enemies)
            enemy.setShootableList(shootable);

        for (int i = 0; i < enemies.size; i++)
            enemies.get(i).setEnemiesList(enemies);
    }

    private void initializeBarrels() {
        for (MapObject obj : tilemap.getTileList("actors", "barrel")) {
            MapProperties props = obj.getProperties();
            float x = (Float) props.get("x") * BaseGame.unitScale;
            float y = (Float) props.get("y") * BaseGame.unitScale;
            shootable.add(new Barrel(x, y, stage3D, player, floorTiles));
        }
    }

    private void initializePickups() {
        initializePickup("health small", 1);
        initializePickup("health medium", 100);
        initializePickup("bullets", 2);
        initializePickup("shells", 2);
        initializePickup("armor small", 1);
        initializePickup("armor medium", 100);
        initializePickup("armor big", 200);
        initializePickup("shotgun", 8);
    }

    private void initializePickup(String type, int amount) {
        for (MapObject obj : tilemap.getTileList("actors", type)) {
            MapProperties props = obj.getProperties();
            float x = (Float) props.get("x") * BaseGame.unitScale;
            float y = (Float) props.get("y") * BaseGame.unitScale;

            if (type.equalsIgnoreCase("health small") || type.equalsIgnoreCase("health medium"))
                pickups.add(new Health(x, y, stage3D, amount, player, floorTiles));

            else if (type.equalsIgnoreCase("bullets"))
                pickups.add(new Bullets(x, y, stage3D, amount, player, floorTiles));
            else if (type.equalsIgnoreCase("shells"))
                pickups.add(new Shells(x, y, stage3D, amount, player, floorTiles));

            else if (type.equalsIgnoreCase("shotgun"))
                pickups.add(new Shotgun(x, y, stage3D, amount, player, floorTiles));

            else if (type.equalsIgnoreCase("armor small") || type.equalsIgnoreCase("armor medium") || type.equalsIgnoreCase("armor big"))
                pickups.add(new Armor(x, y, stage3D, amount, player, floorTiles));
        }
    }

    private void initializeProps() {
        Array<String> types = new Array<>();
        types.add("computer 0", "suitcase 0");

        for (String type : types) {
            for (MapObject obj : tilemap.getTileList("props", type)) {
                MapProperties props = obj.getProperties();
                float y = (Float) props.get("x") * BaseGame.unitScale;
                float z = (Float) props.get("y") * BaseGame.unitScale;
                new Prop(y, z, stage3D, type, player);
            }
        }
    }
}
