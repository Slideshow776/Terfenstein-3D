package no.sandramoen.commanderqueen.utils.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;

import no.sandramoen.commanderqueen.actors.Barrel;
import no.sandramoen.commanderqueen.actors.Tile;
import no.sandramoen.commanderqueen.actors.characters.Menig;
import no.sandramoen.commanderqueen.actors.characters.Player;
import no.sandramoen.commanderqueen.actors.hud.HUD;
import no.sandramoen.commanderqueen.actors.pickups.Ammo;
import no.sandramoen.commanderqueen.actors.pickups.Armor;
import no.sandramoen.commanderqueen.actors.pickups.Health;
import no.sandramoen.commanderqueen.actors.pickups.Pickup;
import no.sandramoen.commanderqueen.actors.utils.baseActors.BaseActor3D;
import no.sandramoen.commanderqueen.actors.utils.Enemy;
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

    public MapLoader(TilemapActor tilemap, Array<Tile> tiles, Stage3D stage3D, Player player, Array<BaseActor3D> shootable,
                     Array<Pickup> pickups, Array<Enemy> enemies, Stage stage, HUD hud) {
        this.tilemap = tilemap;
        this.tiles = tiles;
        this.stage3D = stage3D;
        this.player = player;
        this.shootable = shootable;
        this.pickups = pickups;
        this.enemies = enemies;
        this.stage = stage;
        this.hud = hud;

        floorTiles = new Array();

        initializeTiles();
        initializePlayer();
        initializeBarrels();
        initializeEnemies();
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
                    float rotation = 0;
                    if (props.get("rotation") != null)
                        rotation = (Float) props.get("rotation");
                    rotation %= 360;
                    Tile tile = new Tile(y, z, type, texture, stage3D, rotation);
                    tiles.add(tile);
                    shootable.add(tile);
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
                    if (tileGraph.tiles.get(i).position.dst(tileGraph.tiles.get(j).position) <= Tile.height) {
                        tileGraph.connectTiles(tileGraph.tiles.get(i), tileGraph.tiles.get(j));
                    }
                }
            }
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
        player = new Player((int) playerX, (int) playerY, stage3D, rotation);
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
            enemies.add(new Menig((int) x, (int) y, stage3D, player, rotation, tileGraph, floorTiles, stage, hud));
            shootable.add(enemies.get(enemies.size - 1));
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
            shootable.add(new Barrel((int) x, (int) y, stage3D, player));
        }
    }

    private void initializeHealth() {
        for (MapObject obj : tilemap.getTileList("actors", "health small")) {
            MapProperties props = obj.getProperties();
            float x = (Float) props.get("x") * BaseGame.unitScale;
            float y = (Float) props.get("y") * BaseGame.unitScale;
            pickups.add(new Health((int) x, (int) y, stage3D, player, 1));
        }

        for (MapObject obj : tilemap.getTileList("actors", "health medium")) {
            MapProperties props = obj.getProperties();
            float x = (Float) props.get("x") * BaseGame.unitScale;
            float y = (Float) props.get("y") * BaseGame.unitScale;
            pickups.add(new Health((int) x, (int) y, stage3D, player, 100));
        }
    }

    private void initializeAmmo() {
        for (MapObject obj : tilemap.getTileList("actors", "ammo")) {
            MapProperties props = obj.getProperties();
            float x = (Float) props.get("x") * BaseGame.unitScale;
            float y = (Float) props.get("y") * BaseGame.unitScale;
            pickups.add(new Ammo((int) x, (int) y, stage3D, player, 2));
        }
    }

    private void initializeArmor() {
        for (MapObject obj : tilemap.getTileList("actors", "armor small")) {
            MapProperties props = obj.getProperties();
            float x = (Float) props.get("x") * BaseGame.unitScale;
            float y = (Float) props.get("y") * BaseGame.unitScale;
            pickups.add(new Armor((int) x, (int) y, stage3D, player, 1));
        }

        for (MapObject obj : tilemap.getTileList("actors", "armor medium")) {
            MapProperties props = obj.getProperties();
            float x = (Float) props.get("x") * BaseGame.unitScale;
            float y = (Float) props.get("y") * BaseGame.unitScale;
            pickups.add(new Armor((int) x, (int) y, stage3D, player, 100));
        }

        for (MapObject obj : tilemap.getTileList("actors", "armor big")) {
            MapProperties props = obj.getProperties();
            float x = (Float) props.get("x") * BaseGame.unitScale;
            float y = (Float) props.get("y") * BaseGame.unitScale;
            pickups.add(new Armor((int) x, (int) y, stage3D, player, 200));
        }
    }
}
