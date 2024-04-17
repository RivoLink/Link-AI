package mg.rivolink.ai.linkai;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class Data {

    public static final String PATH_MAP = "data/map/";
    public static final String PATH_ITEM = "data/item/";
    public static final String PATH_SKIN = "data/skin/";
    public static final String PATH_PLAYER = "data/player/";
    public static final String PATH_JOYPAD = "data/joypad/";
    public static final String PATH_TILESET = "data/map/tilesets/";
    public static final String PATH_ITEM_SPRITE = "data/item/sprite/";

    public static void load(){
        SkinLoader.load();
        MapLoader.load();
        AtlasLoader.load();
        TextureLoader.load();
        DrawableLoader.load();
        TextureRegionLoader.load();
    }

    public static class SkinLoader {

        public static Skin skin;

        public static void load(){
            skin = new Skin(Gdx.files.internal(PATH_SKIN + "defaultskin.json"));

        }
    }

    public static class MapLoader {

        public static TiledMap level_01, lynna_city;

        private static void load(){
            lynna_city = new TmxMapLoader().load(PATH_MAP + "Lynna-City.tmx");
            level_01 = new TmxMapLoader().load(PATH_MAP + "Level-01.tmx");
        }
    }

    public static class AtlasLoader {

        public static TextureAtlas link_atlas, zelda_atlas, color_atlas;

        public static void load(){
            link_atlas = new TextureAtlas(PATH_PLAYER + "link_cap.atlas");
            zelda_atlas = new TextureAtlas(PATH_PLAYER + "zelda_dacing.atlas");
            color_atlas = new TextureAtlas(PATH_MAP + "tilesets/color.atlas");
        }
    }

    public static class TextureLoader {

        public static Texture joypad_direction;

        public static void load(){
            joypad_direction = new Texture(Gdx.files.internal(PATH_JOYPAD + "Direction.png"));
        }
    }

    public static class DrawableLoader {

        public static Drawable[] colors;

        public static void load(){
            TextureAtlas color_atlas = AtlasLoader.color_atlas;

            colors = new TextureRegionDrawable[]{
                new TextureRegionDrawable(color_atlas.findRegion("green")),  //DEST
                new TextureRegionDrawable(color_atlas.findRegion("pink")),   //FREE
                new TextureRegionDrawable(color_atlas.findRegion("sky")),    //BLOCKED
                new TextureRegionDrawable(color_atlas.findRegion("red")),    //INTERDIT
            };
        }
    }

    public static class TextureRegionLoader {

        public static TextureRegion[] link, colors;

        public static void load(){

            //agent orientation, clockwise
            link = new TextureRegion[]{
                Data.AtlasLoader.link_atlas.findRegion("up"),
                Data.AtlasLoader.link_atlas.findRegion("right"),
                Data.AtlasLoader.link_atlas.findRegion("down"),
                Data.AtlasLoader.link_atlas.findRegion("left"),
            };

            colors = new TextureRegion[]{
                Data.AtlasLoader.color_atlas.findRegion("green"),  //DEST
                Data.AtlasLoader.color_atlas.findRegion("pink"),   //FREE
                Data.AtlasLoader.color_atlas.findRegion("sky"),    //BLOCKED
                Data.AtlasLoader.color_atlas.findRegion("red"),    //INTERDIT
            };
        }
    }
}
