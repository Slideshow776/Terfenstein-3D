package no.sandramoen.commanderqueen.actors.utils;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.maps.tiled.renderers.OrthoCachedTiledMapRenderer;

import java.util.ArrayList;
import java.util.Iterator;

import no.sandramoen.commanderqueen.utils.BaseGame;
import no.sandramoen.commanderqueen.utils.Stage3D;

public class TilemapActor extends Actor {
    private TiledMap tiledMap;
    private OrthoCachedTiledMapRenderer tiledMapRenderer;

    public TilemapActor(TiledMap tiledMap, Stage3D stage) {
        this.tiledMap = tiledMap;
        tiledMapRenderer = new OrthoCachedTiledMapRenderer(this.tiledMap, BaseGame.unitScale);
        tiledMapRenderer.setBlending(true);
        stage.addActor(this);
    }

    public ArrayList<MapObject> getRectangleList(String propertyName) {
        ArrayList<MapObject> list = new ArrayList<MapObject>();

        for (MapLayer layer : tiledMap.getLayers()) {
            for (MapObject obj : layer.getObjects()) {
                if (!(obj instanceof RectangleMapObject))
                    continue;

                MapProperties props = obj.getProperties();

                if (props.containsKey("name") && props.get("name").equals(propertyName))
                    list.add(obj);
            }
        }
        return list;
    }

    public ArrayList<MapObject> getTileList(String layerName, String propertyName) {
        ArrayList<MapObject> list = new ArrayList();

        for (MapLayer layer : tiledMap.getLayers()) {
            System.out.println(layer.getName());
            if (layer.getName().equalsIgnoreCase(layerName))
                for (MapObject obj : layer.getObjects()) {
                    if (!(obj instanceof TiledMapTileMapObject))
                        continue;

                    MapProperties props = obj.getProperties();

                    // Default MapProperties are stored within associated Tile object
                    // Instance-specific overrides are stored in MapObject

                    TiledMapTileMapObject tmtmo = (TiledMapTileMapObject) obj;
                    TiledMapTile t = tmtmo.getTile();
                    MapProperties defaultProps = t.getProperties();

                    if (defaultProps.containsKey("name") && defaultProps.get("name").equals(propertyName))
                        list.add(obj);

                    // get list of default property keys
                    Iterator<String> propertyKeys = defaultProps.getKeys();

                    // iterate over keys; copy default values into props if needed
                    while (propertyKeys.hasNext()) {
                        String key = propertyKeys.next();

                        // check if value already exists; if not, create property with default value
                        if (props.containsKey(key))
                            continue;
                        else {
                            Object value = defaultProps.get(key);
                            props.put(key, value);
                        }
                    }
                }
        }
        return list;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        tiledMapRenderer.setView((OrthographicCamera) getStage().getCamera());
        tiledMapRenderer.render();
    }
}
