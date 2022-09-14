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
import no.sandramoen.commanderqueen.actors.Elevator;
import no.sandramoen.commanderqueen.actors.Tile;
import no.sandramoen.commanderqueen.actors.characters.Hund;
import no.sandramoen.commanderqueen.actors.characters.Menig;
import no.sandramoen.commanderqueen.actors.characters.Player;
import no.sandramoen.commanderqueen.actors.characters.Prest;
import no.sandramoen.commanderqueen.actors.characters.Sersjant;
import no.sandramoen.commanderqueen.actors.hud.HUD;
import no.sandramoen.commanderqueen.actors.pickups.Bullets;
import no.sandramoen.commanderqueen.actors.pickups.Armor;
import no.sandramoen.commanderqueen.actors.pickups.Chaingun;
import no.sandramoen.commanderqueen.actors.pickups.Health;
import no.sandramoen.commanderqueen.actors.pickups.Key;
import no.sandramoen.commanderqueen.actors.pickups.Pickup;
import no.sandramoen.commanderqueen.actors.pickups.Rocket;
import no.sandramoen.commanderqueen.actors.pickups.Shells;
import no.sandramoen.commanderqueen.actors.pickups.Shotgun;
import no.sandramoen.commanderqueen.actors.props.Prop;
import no.sandramoen.commanderqueen.actors.utils.baseActors.BaseActor3D;
import no.sandramoen.commanderqueen.actors.characters.enemy.Enemy;
import no.sandramoen.commanderqueen.actors.utils.TilemapActor;
import no.sandramoen.commanderqueen.screens.gameplay.LevelScreen;
import no.sandramoen.commanderqueen.utils.BaseGame;
import no.sandramoen.commanderqueen.utils.GameUtils;
import no.sandramoen.commanderqueen.utils.Stage3D;
import no.sandramoen.commanderqueen.utils.pathFinding.TileGraph;

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
        tileTextures.add("blank");

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

                    Tile tile = new Tile(y, z, width, depth, height, type, texture, stage3D, rotation, secretMovementDirection, secretLength, isAIpath);
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
            String key = props.get("color", String.class);

            Door door = new Door(x, y, stage3D, stage, rotation, player, key, shootable);
            door.isLocked = props.get("isLocked", Boolean.class);

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

            Elevator elevator = new Elevator(x, y, stage3D, stage, rotation, player);
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
            shootable.add(enemies.get(enemies.size - 1));

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
        initializePickup("bullets", 10);
        initializePickup("shells", 4);
        initializePickup("rocket", 1);
        initializePickup("armor small", 1);
        initializePickup("armor medium", 100);
        initializePickup("armor big", 200);
        initializePickup("shotgun", 8);
        initializePickup("chaingun", 20);
        initializePickup("key", -1);
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

            else if (type.equalsIgnoreCase("armor small") || type.equalsIgnoreCase("armor medium") || type.equalsIgnoreCase("armor big"))
                pickups.add(new Armor(x, y, stage3D, amount, player));
        }
    }

    private void initializeProps() {
        Array<String> types = new Array<>();
        types.add("computer 0", "suitcase 0", "statue 0");

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
