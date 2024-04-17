package mg.rivolink.ai.linkai.environments.maps.tiles;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class PlayableTiledMapTile extends Animation<StaticTiledMapTile> implements TiledMapTile {

    private final int initialTime = 0;
    private static float deltaTime = 0f;

    private int id;

    private MapObjects objects;
    private MapProperties properties;
    private BlendMode blendMode = BlendMode.ALPHA;

    private boolean play = false;
    private float time = initialTime;

    private float interval;
    private StaticTiledMapTile[] frameTiles;

    public PlayableTiledMapTile(float interval, Array<StaticTiledMapTile> frameTiles){
        super(interval, frameTiles, PlayMode.NORMAL);

        this.interval = interval;
        this.frameTiles = new StaticTiledMapTile[frameTiles.size];

        for(int i = 0; i < frameTiles.size; i++){
            this.frameTiles[i] = frameTiles.get(i);
        }
    }

    @Override
    public int getId(){
        return id;
    }

    @Override
    public void setId(int id){
        this.id = id;
    }

    @Override
    public TiledMapTile.BlendMode getBlendMode(){
        return blendMode;
    }

    @Override
    public void setBlendMode(TiledMapTile.BlendMode blendMode){
        this.blendMode = blendMode;
    }

    public int getCurrentFrameIndex(){
        if(play){
            if(isAnimationFinished(time))
                play = false;
            time += deltaTime;
        }
        return super.getKeyFrameIndex(time);
    }

    public TiledMapTile getCurrentFrame(){
        return frameTiles[getCurrentFrameIndex()];
    }

    @Override
    public TextureRegion getTextureRegion(){
        return getCurrentFrame().getTextureRegion();
    }

    @Override
    public void setTextureRegion(TextureRegion textureRegion){
        throw new GdxRuntimeException("Cannot set the texture region of AnimatedTiledMapTile.");
    }

    @Override
    public float getOffsetX(){
        return getCurrentFrame().getOffsetX();
    }

    @Override
    public void setOffsetX(float offsetX){
        throw new GdxRuntimeException("Cannot set offset of AnimatedTiledMapTile.");
    }

    @Override
    public float getOffsetY(){
        return getCurrentFrame().getOffsetY();
    }

    @Override
    public void setOffsetY(float offsetY){
        throw new GdxRuntimeException("Cannot set offset of AnimatedTiledMapTile.");
    }

    @Override
    public MapObjects getObjects(){
        if(objects == null){
            objects = new MapObjects();
        }
        return objects;
    }

    @Override
    public MapProperties getProperties(){
        if(properties == null){
            properties = new MapProperties();
        }
        return properties;
    }

    public boolean isRemovable(){
        return true;
    }

    public void play(){
        play = !isAnimationFinished(time);
    }

    public void reset(){
        play = false;
        time = initialTime;
    }

    public PlayableTiledMapTile copy(){
        return new PlayableTiledMapTile(new Float(interval), new Array<StaticTiledMapTile>(frameTiles));
    }

    public static void updateDeltaTime(float deltaTime){
        PlayableTiledMapTile.deltaTime = deltaTime;
    }
}
