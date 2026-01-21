package de.nicolas.utils.actors;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthoCachedTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import de.nicolas.utils.Utils;

public class TileMapActor extends Actor {

    public static int windowWidth = Utils.WIDTH;
    public static int windowHeight = Utils.HEIGHT;

    private TiledMap tiledMap;
    private OrthographicCamera tiledCamera;
    private OrthoCachedTiledMapRenderer tiledMapRenderer;

    public TileMapActor(String filename, Stage theStage){

        tiledMap = new TmxMapLoader().load(filename);

        int tileWidth = (int)tiledMap.getProperties().get("tilewidth");
    }
}
