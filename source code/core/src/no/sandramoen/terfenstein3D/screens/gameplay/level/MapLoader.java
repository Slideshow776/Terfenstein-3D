package no.sandramoen.terfenstein3D.screens.gameplay.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;

import no.sandramoen.terfenstein3D.actors.Barrel;
import no.sandramoen.terfenstein3D.actors.Door;
import no.sandramoen.terfenstein3D.actors.Elevator;
import no.sandramoen.terfenstein3D.actors.Tile;
import no.sandramoen.terfenstein3D.actors.characters.Fenrik;
import no.sandramoen.terfenstein3D.actors.characters.Hund;
import no.sandramoen.terfenstein3D.actors.characters.Menig;
import no.sandramoen.terfenstein3D.actors.characters.Player;
import no.sandramoen.terfenstein3D.actors.characters.Prest;
import no.sandramoen.terfenstein3D.actors.characters.Sersjant;
import no.sandramoen.terfenstein3D.actors.hud.HUD;
import no.sandramoen.terfenstein3D.actors.pickups.Bullets;
import no.sandramoen.terfenstein3D.actors.pickups.Armor;
import no.sandramoen.terfenstein3D.actors.pickups.Chaingun;
import no.sandramoen.terfenstein3D.actors.pickups.Chainsaw;
import no.sandramoen.terfenstein3D.actors.pickups.Health;
import no.sandramoen.terfenstein3D.actors.pickups.Key;
import no.sandramoen.terfenstein3D.actors.pickups.Pickup;
import no.sandramoen.terfenstein3D.actors.pickups.Rocket;
import no.sandramoen.terfenstein3D.actors.pickups.RocketLauncher;
import no.sandramoen.terfenstein3D.actors.pickups.Shells;
import no.sandramoen.terfenstein3D.actors.pickups.Shotgun;
import no.sandramoen.terfenstein3D.actors.props.Prop;
import no.sandramoen.terfenstein3D.actors.utils.baseActors.BaseActor3D;
import no.sandramoen.terfenstein3D.actors.characters.enemy.Enemy;
import no.sandramoen.terfenstein3D.actors.utils.TilemapActor;
import no.sandramoen.terfenstein3D.screens.gameplay.LevelScreen;
import no.sandramoen.terfenstein3D.utils.BaseGame;
import no.sandramoen.terfenstein3D.utils.GameUtils;
import no.sandramoen.terfenstein3D.utils.Stage3D;
import no.sandramoen.terfenstein3D.utils.pathFinding.TileGraph;

public class MapLoader {
    public Player player;
    public TileGraph tileGraph;
    public Array<Elevator> elevators = new Array();
    public Array<Tile> floorTiles;

    private HUD hud;
    private Stage stage;
    private Stage3D stage3D;
    private TilemapActor tilemap;
    private DecalBatch decalBatch;

    private Array<Tile> tiles;
    private Array<Door> doors;
    private Array<Enemy> enemies;
    private Array<Pickup> pickups;
    private Array<TileShade> tileShades;
    private Array<BaseActor3D> shootable;
    private Array<BaseActor3D> projectiles;

    private Array<Array<Tile>> patrols = new Array();

    public MapLoader(TilemapActor tilemap, Array<Tile> tiles, Stage3D stage3D, Player player, Array<BaseActor3D> shootable,
                     Array<Pickup> pickups, Array<Enemy> enemies, Stage stage, HUD hud, DecalBatch decalBatch,
                     Array<Door> doors, Array<BaseActor3D> projectiles, Array<TileShade> tileShades
    ) {
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
        this.projectiles = projectiles;
        this.tileShades = tileShades;

        floorTiles = new Array();

        initializeTiles();
        initializePlayer();
        initializeDoors();
        initializeElevator();
        initializeBarrels();
        initializeEnemies();
        initializePickups();
        initializePointLights();
        initializeTileShades();
        initializeProps();
    }

    private void initializeTiles() {
        createTiles();
        addTilesToAIGraph();
        createAIGraphConnections();
    }

    private void createTiles() {
        Array<String> tileTypes = new Array<>();
        tileTypes.add("U1", "1st floor", "2nd floor", "3rd floor");
        tileTypes.add("4th floor");
        Array<String> tileTextures = new Array<>();
        tileTextures.add("big plates", "lonplate", "light big plates", "light lonplate");
        tileTextures.add("lights 0", "flag 0", "elevator wall", "light lonplate 2");
        tileTextures.add("hexagon 0", "hexagon 1", "brick 0", "brick 1");
        tileTextures.add("brick 2", "brick 3", "cliff 0", "cliff 1");
        tileTextures.add("light big plates 1", "flag 1", "flag 2", "flag 3");
        tileTextures.add("blank", "cliff 1 flag", "brick 3 flag", "wood 1");
        tileTextures.add("wood 1 flag", "wood 2", "wood 2 flag", "");
        tileTextures.add("light big plates red", "light big plates green", "brick 3 image", "cliff 1 image");
        tileTextures.add("light big plates image", "wood 1 image", "wood 2 image", "brick 3 krans");
        tileTextures.add("cliff 1 flag", "light big plates krans", "wood 1 krans", "wood 2 krans");
        tileTextures.add("brick 3 poster 1", "cliff 1 poster 1", "light big plates poster 1", "wood 1 poster 1");
        tileTextures.add("brick 3 poster 2", "cliff 1 poster 2", "light big plates poster 2", "wood 1 poster 2");
        tileTextures.add("wood 2 poster 1", "wood 2 poster 2", "cliff 1 krans", "light big plates pipe up");
        tileTextures.add("light big plates pipe side", "light big plates pipe end", "light big plates 1 pipe down", "brick 3 prison 1");
        tileTextures.add("wood 1 prison 1", "wood 2 prison 1", "cliff 1 prison 1", "light big plates secret");
        tileTextures.add("brick 3 eagle 1", "cliff 1 eagle 1", "light big plates eagle 1", "wood 1 eagle 1");
        tileTextures.add("wood 2 eagle 1", "light big plates 1 red carpet 1", "light big plates 1 red carpet 2", "light big plates 1 red carpet 3");
        tileTextures.add("brick 3 prison 2", "wood 1 prison 2", "wood 2 prison 2", "cliff 1 prison 2");
        tileTextures.add("brick 3 prison 3", "wood 1 prison 3", "wood 2 prison 3", "cliff 1 prison 3");
        // tileTextures.add("", "", "", "");

        for (String type : tileTypes) {
            for (String texture : tileTextures) {
                for (MapObject obj : tilemap.getTileList(type, texture)) {
                    MapProperties props = obj.getProperties();
                    float y = props.get("x", Float.class) * BaseGame.unitScale;
                    float z = props.get("y", Float.class) * BaseGame.unitScale;

                    float width = props.get("width", Float.class) * BaseGame.unitScale;
                    float height = props.get("height", Float.class) * BaseGame.unitScale;
                    float rotation = getRotation(props);

                    float depth = props.get("depth", Float.class);
                    String secretMovementDirection = (String) props.get("secret");
                    int secretLength = props.get("secret length", Integer.class);
                    if (!secretMovementDirection.isEmpty())
                        LevelScreen.numSecrets++;
                    boolean isAIpath = true;
                    if (props.get("isAIpath", Boolean.class) != null)
                        isAIpath = props.get("isAIpath", Boolean.class);
                    boolean isWinCondition = false;
                    if (props.get("isWinCondition", Boolean.class) != null)
                        isWinCondition = props.get("isWinCondition", Boolean.class);

                    Tile tile = new Tile(y, z, width, depth, height, type, texture, stage3D, rotation, secretMovementDirection, secretLength, isAIpath, isWinCondition);
                    tiles.add(tile);
                    shootable.add(tile);

                    String patrol = props.get("patrol", String.class);
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
            if (tile.type == "U1" && tile.isAIpath) {
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
            float x = props.get("x", Float.class) * BaseGame.unitScale;
            float y = props.get("y", Float.class) * BaseGame.unitScale;
            float rotation = getRotation(props);
            boolean isElevator = false;
            try {
                isElevator = props.get("isElevator", Boolean.class);

            } catch (Exception e) {

            }
            String key = props.get("color", String.class);
            Boolean isLocked = props.get("isLocked", Boolean.class);
            Door door = new Door(x, y, stage3D, stage, rotation, player, key, shootable, isLocked, isElevator);

            doors.add(door);
            shootable.add(door);
        }
    }

    private void initializeElevator() {
        for (MapObject obj : tilemap.getTileList("actors", "elevator")) {
            MapProperties props = obj.getProperties();
            float x = props.get("x", Float.class) * BaseGame.unitScale;
            float y = props.get("y", Float.class) * BaseGame.unitScale;
            float rotation = getRotation(props);

            Elevator elevator = new Elevator(x, y, stage3D, stage, rotation);
            shootable.add(elevator);
            elevators.add(elevator);
        }
    }

    private void initializePointLights() {
        int pointLightCount = 0;
        for (MapObject obj : tilemap.getTileList("actors", "point light")) {
            MapProperties props = obj.getProperties();
            float y = props.get("x", Float.class) * BaseGame.unitScale;
            float z = props.get("y", Float.class) * BaseGame.unitScale;
            Color color = props.get("color", Color.class);
            float height = props.get("map height", Float.class) * Tile.height;
            float intensity = props.get("intensity", Float.class);

            PointLight pLight = new PointLight();
            pLight.set(color, new Vector3(height, y, z), intensity);
            stage3D.environment.add(pLight);
            pointLightCount++;
        }

        if (pointLightCount > 0)
            Gdx.app.log(getClass().getSimpleName(), "added " + pointLightCount + " pointLights to map");
    }

    private void initializeTileShades() {
        for (MapObject obj : tilemap.getTileList("actors", "tile shade")) {
            MapProperties props = obj.getProperties();
            int id = props.get("id", Integer.class);
            float y = props.get("x", Float.class) * BaseGame.unitScale;
            float z = props.get("y", Float.class) * BaseGame.unitScale;
            float width = props.get("width", Float.class) * BaseGame.unitScale;
            float height = props.get("height", Float.class) * BaseGame.unitScale;
            Color color0 = props.get("color 0", Color.class);
            Color color1 = props.get("color 1", Color.class);
            float flickerFrequency = props.get("flickerFrequency", Float.class);

            tileShades.add(new TileShade(id, y, z, width, height, color0, color1, flickerFrequency, stage3D));
        }

        GameUtils.checkShading(tileShades, stage3D.getActors3D());
    }

    private void initializePlayer() {
        if (tilemap.getTileList("actors", "player start").size() == 0)
            Gdx.app.error(getClass().getSimpleName(), "Error: Player position not found!");

        MapObject startPoint = tilemap.getTileList("actors", "player start").get(0);
        MapProperties props = startPoint.getProperties();

        float playerX = props.get("x", Float.class) * BaseGame.unitScale;
        float playerY = props.get("y", Float.class) * BaseGame.unitScale;
        float rotation = getRotation(props);

        player = new Player(playerX, playerY, stage3D, rotation, stage);
    }

    private void initializeEnemies() {
        initializeEnemy("hund");
        initializeEnemy("menig");
        initializeEnemy("sersjant");
        initializeEnemy("prest");
        initializeEnemy("fenrik");
    }

    private void initializeEnemy(String type) {
        for (MapObject obj : tilemap.getTileList("actors", type)) {
            MapProperties props = obj.getProperties();
            float x = props.get("x", Float.class) * BaseGame.unitScale;
            float y = props.get("y", Float.class) * BaseGame.unitScale;
            float rotation = getRotation(props);

            if (type.equalsIgnoreCase("hund"))
                enemies.add(new Hund(x, y, stage3D, player, rotation, tileGraph, floorTiles, stage, hud, decalBatch));
            if (type.equalsIgnoreCase("menig"))
                enemies.add(new Menig(x, y, stage3D, player, rotation, tileGraph, floorTiles, stage, hud, decalBatch));
            if (type.equalsIgnoreCase("sersjant"))
                enemies.add(new Sersjant(x, y, stage3D, player, rotation, tileGraph, floorTiles, stage, hud, decalBatch));
            if (type.equalsIgnoreCase("prest"))
                enemies.add(new Prest(x, y, stage3D, player, rotation, tileGraph, floorTiles, stage, hud, decalBatch, projectiles));
            if (type.equalsIgnoreCase("fenrik"))
                enemies.add(new Fenrik(x, y, stage3D, player, rotation, tileGraph, floorTiles, stage, hud, decalBatch));
            shootable.add(enemies.get(enemies.size - 1));

            int health = -1;
            try {
                health = props.get("health", Integer.class);
            } catch (Exception exception) {

            }
            if (health == 0)
                enemies.get(enemies.size - 1).decrementHealth(20);

            String patrol = props.get("patrol", String.class);
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
            float x = props.get("x", Float.class) * BaseGame.unitScale;
            float y = props.get("y", Float.class) * BaseGame.unitScale;
            shootable.add(new Barrel(x, y, stage3D, player));
        }
    }

    private void initializePickups() {
        initializePickup("health small", 1);
        initializePickup("health medium", 100);
        initializePickup("bullets", 4);
        initializePickup("shells", 4);
        initializePickup("rocket", 1);
        initializePickup("armor small", 1);
        initializePickup("armor medium", 100);
        initializePickup("armor big", 200);
        initializePickup("shotgun", 8);
        initializePickup("chaingun", 20);
        initializePickup("rocketLauncher", 15);
        initializePickup("key", -1);
        initializePickup("chainsaw", -1);
    }

    private void initializePickup(String type, int amount) {
        for (MapObject obj : tilemap.getTileList("actors", type)) {
            MapProperties props = obj.getProperties();
            float x = props.get("x", Float.class) * BaseGame.unitScale;
            float y = props.get("y", Float.class) * BaseGame.unitScale;

            if (type.equalsIgnoreCase("health small") || type.equalsIgnoreCase("health medium"))
                pickups.add(new Health(x, y, stage3D, amount, player));

            else if (type.equalsIgnoreCase("bullets"))
                pickups.add(new Bullets(x, y, stage3D, amount, player));
            else if (type.equalsIgnoreCase("shells"))
                pickups.add(new Shells(x, y, stage3D, amount, player));
            else if (type.equalsIgnoreCase("rocket"))
                pickups.add(new Rocket(x, y, stage3D, amount, player));

            else if (type.equalsIgnoreCase("key"))
                pickups.add(new Key(x, y, stage3D, props.get("color", String.class), player));

            else if (type.equalsIgnoreCase("shotgun"))
                pickups.add(new Shotgun(x, y, stage3D, amount, player));
            else if (type.equalsIgnoreCase("chaingun"))
                pickups.add(new Chaingun(x, y, stage3D, amount, player));
            else if (type.equalsIgnoreCase("rocketLauncher"))
                pickups.add(new RocketLauncher(x, y, stage3D, amount, player));
            else if (type.equalsIgnoreCase("chainsaw"))
                pickups.add(new Chainsaw(x, y, stage3D, amount, player));

            else if (type.equalsIgnoreCase("armor small") || type.equalsIgnoreCase("armor medium") || type.equalsIgnoreCase("armor big"))
                pickups.add(new Armor(x, y, stage3D, amount, player));
        }
    }

    private void initializeProps() {
        Array<String> types = new Array<>();
        types.add("computer 0", "suitcase 0", "statue 0", "lightBulb 0");
        types.add("lightBulb 1", "table 1", "chair 0", "chair 1");
        types.add("barrel 1", "flag 1", "trash 1", "lightBulb 2");
        types.add("vase 1", "vase 2", "palle 1", "forklift");
        types.add("dog bed 1", "bed 1", "lightBulb 3", "skeleton 1");
        types.add("player");
        // types.add("");

        for (String type : types) {
            for (MapObject obj : tilemap.getTileList("props", type)) {
                MapProperties props = obj.getProperties();
                float y = props.get("x", Float.class) * BaseGame.unitScale;
                float z = props.get("y", Float.class) * BaseGame.unitScale;
                new Prop(y, z, stage3D, type, player);
            }
        }
    }

    private float getRotation(MapProperties props) {
        float rotation = 0;
        if (props.get("rotation", Float.class) != null)
            rotation = props.get("rotation", Float.class);
        return rotation % 360;
    }
}
