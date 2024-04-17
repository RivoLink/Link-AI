package mg.rivolink.ai.linkai.environments.maps;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.TextureMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.utils.Array;

import mg.rivolink.ai.linkai.environments.maps.tiles.PlayableTiledMapTile;

public class MapRenderer extends OrthogonalTiledMapRenderer {

    public static final int DEST = 598;
    public static final int FREE = 605;
    public static final int BLOCKED = 600;
    public static final int INTERDIT = 597;

    private ArrayList<Sprite> sprites;

    public final int tileW;
    public final int tileH;

    private TiledMapTileLayer collisionLayer;
    private TiledMapTileLayer animationLayer;

    public MapRenderer(TiledMap map){
        super(map);

        sprites = new ArrayList<Sprite>();
        animationLayer = (TiledMapTileLayer)map.getLayers().get(Layer.ANIMATION);
        collisionLayer = (TiledMapTileLayer)map.getLayers().get(Layer.COLLISION);

        tileW = (int)animationLayer.getTileWidth();
        tileH = (int)animationLayer.getTileHeight();
    }

    public TiledMapTileLayer getl(){
        return animationLayer;
    }

    public TiledMapTile getTile(int x, int y){
        TiledMapTileLayer.Cell cell = animationLayer.getCell(x, y);
        return cell.getTile();
    }

    public TiledMapTileLayer getCollisionLayer(){
        return collisionLayer;
    }

    public int getId(int tileX, int tileY){
        return collisionLayer.getCell(tileX, tileY).getTile().getId();
    }

    public void addCollisionTile(int x, int y){
        // TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
        // cell.setTile(new StaticTiledMapTile((StaticTiledMapTile)collisionLayer.getCell(1, 2).getTile()));
        // collisionLayer.setCell(x, y, cell);
    }

    public void removeCollisionTile(int x, int y){
        collisionLayer.setCell(x, y, null);
    }

    public void addSprite(Sprite sprite){
        sprites.add(sprite);
    }

    public void addTile(TiledMapTile tile, int x, int y, boolean withCol){
        TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
        cell.setTile(tile);
        animationLayer.setCell(x, y, cell);
        if(withCol)addCollisionTile(x, y);
    }

    public void addTile(TiledMapTile tile, int x, int y){
        TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
        cell.setTile(tile);
        animationLayer.setCell(x, y, cell);
        addCollisionTile(x, y);
    }

    public void addTile(TiledMapTile tile, int[][] positions){
        for(int[] position:positions){
            addTile(tile, position[0], position[1]);
        }
    }

    public void addTiles(final PlayableTiledMapTile tile, final String key){

        class TileData {

            final int x;
            final int y;
            final PlayableTiledMapTile tile;

            TileData(PlayableTiledMapTile tile, int x, int y){
                this.x = x;
                this.y = y;
                this.tile = tile;
            }
        }

        new Thread(new Runnable(){
            @Override
            public void run(){
                final Array<TileData> data = new Array<TileData>();
                for(int x = 0; x < collisionLayer.getWidth(); x++){
                    for(int y = 0; y < collisionLayer.getHeight(); y++){
                        TiledMapTileLayer.Cell cell = collisionLayer.getCell(x, y);
                        if((cell != null) && (cell.getTile() != null) && cell.getTile().getProperties().containsKey(key)){
                            data.add(new TileData(tile.copy(), x, y));
                        }
                    }
                }
                Gdx.app.postRunnable(new Runnable(){
                    @Override
                    public void run(){
                        for(TileData tileData:data){
                            addTile(tileData.tile, tileData.x, tileData.y, false);
                        }
                    }
                });
            }
        }).start();
    }

    @Override
    public void renderObject(MapObject object){
        if(object instanceof TextureMapObject){
            TextureMapObject textureObj  =  (TextureMapObject) object;
            batch.draw(textureObj.getTextureRegion(), textureObj.getX(), textureObj.getY());
        }
    }

    @Override
    public void render(){
        render(Gdx.graphics.getDeltaTime());
    }

    public void render(float delta){
        PlayableTiledMapTile.updateDeltaTime(delta);

        beginRender();

        int nextLayer = 0;
        for(MapLayer layer:map.getLayers()){
            if(layer.isVisible()){
                if(layer instanceof TiledMapTileLayer){
                    renderTileLayer((TiledMapTileLayer)layer);
                    nextLayer++;
                    if(nextLayer == Layer.ROOF){
                        for(Sprite sprite:sprites)
                            sprite.draw(batch);
                    }
                }
                else{
                    for(MapObject object:layer.getObjects()){
                        renderObject(object);
                    }
                }
            }
        }

        endRender();
    }

    public static AnimatedTiledMapTile createAnimatedTile(TextureRegion tileSet, float time, int frame){
        Array<StaticTiledMapTile> staticTiles = new Array<StaticTiledMapTile>();
        TextureRegion[] tiles = tileSet.split(tileSet.getRegionWidth()/frame, tileSet.getRegionHeight())[0];
        for(TextureRegion tile:tiles){
            staticTiles.add(new StaticTiledMapTile(tile));
        }
        return new AnimatedTiledMapTile(time, staticTiles);
    }

    public static PlayableTiledMapTile createPlayableTile(TextureRegion tileSet, float time, int frame){
        Array<StaticTiledMapTile> staticTiles = new Array<StaticTiledMapTile>();
        TextureRegion[] tiles = tileSet.split(tileSet.getRegionWidth()/frame, tileSet.getRegionHeight())[0];
        for(TextureRegion tile:tiles){
            staticTiles.add(new StaticTiledMapTile(tile));
        }
        return new PlayableTiledMapTile(time, staticTiles);
    }

    public class Layer {

        public static final int GROUND = 0;
        public static final int ANIMATION = 1;
        public static final int ROOF = 2;
        public static final int COLLISION = 3;

    }
}
