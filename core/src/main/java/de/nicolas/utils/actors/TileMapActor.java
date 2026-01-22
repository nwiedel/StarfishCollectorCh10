package de.nicolas.utils.actors;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.maps.tiled.renderers.OrthoCachedTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import de.nicolas.utils.Utils;

import java.util.ArrayList;

public class TileMapActor extends Actor {

    public static int windowWidth = Utils.WIDTH;
    public static int windowHeight = Utils.HEIGHT;

    private TiledMap tiledMap;
    private OrthographicCamera tiledCamera;
    private OrthoCachedTiledMapRenderer tiledMapRenderer;

    public TileMapActor(String filename, Stage theStage){

        tiledMap = new TmxMapLoader().load(filename);

        int tileWidth = (int)tiledMap.getProperties().get("tilewidth");
        int tileHeight = (int)tiledMap.getProperties().get("tileheight");
        int numTilesHorizontal = (int)tiledMap.getProperties().get("width");
        int numTilesVertical = (int)tiledMap.getProperties().get("height");
        int mapWidth = tileWidth * numTilesHorizontal;
        int mapHeight = tileHeight * numTilesVertical;

        BaseActor.setWorldBounds(mapWidth, mapHeight);

        tiledMapRenderer = new OrthoCachedTiledMapRenderer(tiledMap);
        tiledMapRenderer.setBlending(true);
        tiledCamera = new OrthographicCamera();
        tiledCamera.setToOrtho(false, windowWidth, windowHeight);
        tiledCamera.update();

        theStage.addActor(this);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        Camera mainCamera = getStage().getCamera();
        tiledCamera.position.x = mainCamera.position.x;
        tiledCamera.position.y = mainCamera.position.y;
        tiledCamera.update();
        tiledMapRenderer.setView(tiledCamera);

        // batch beenden damit der tiledMapRenderer nicht als letztes ausgeführt wird
        batch.end();
        tiledMapRenderer.render();
        batch.begin();
    }

    public ArrayList<MapObject> getRectangleList(String propertyName){
        ArrayList<MapObject> list = new ArrayList<MapObject>();

        for (MapLayer layer : tiledMap.getLayers()){
            for (MapObject object : layer.getObjects()){
                if (!(object instanceof RectangleMapObject)){
                    continue;
                }
                MapProperties properties = object.getProperties();
                if (properties.containsKey("name") &&
                    properties.get("name").equals(propertyName)){
                    list.add(object);
                }
            }
        }
        return list;
    }

    public ArrayList<MapObject> getTileList(String propertyName){
        ArrayList<MapObject> list = new ArrayList<MapObject>();

        for (MapLayer layer : tiledMap.getLayers()){
            for (MapObject object : layer.getObjects()){
                if(!(object instanceof TiledMapTileMapObject)){
                    continue;
                }
                MapProperties properties = object.getProperties();
            }
        }
        return list;
    }
}
