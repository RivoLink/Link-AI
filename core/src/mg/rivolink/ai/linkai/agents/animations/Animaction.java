package mg.rivolink.ai.linkai.agents.animations;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class Animaction {

    public static final int[] DIRECTIONS = {0, 1, 2, 3};

    public static final float TIME_WALK = 0.08f;
    public static final float TIME_PAUSE = 0.16f;
    public static final float TIME_ATTACK = 0.025f;

    private final Type type;

    private float position[][];
    private float time = TIME_WALK;

    private boolean looping = false;
    private boolean withItem = false;

    protected Animation<TextureRegion>[] playerAnim;
    protected Animation<TextureRegion>[] itemAnim;

    public enum Type{
        WALK,
        ATTACK,
        PAUSE,
    }

    public Animaction(Animation<TextureRegion>[] playerAnimation){
        this(playerAnimation, Type.WALK);
    }

    public Animaction(Animation<TextureRegion>[] playerAnimation, Type type){
        playerAnim = playerAnimation;
        itemAnim = null;
        withItem = false;
        setType(this.type = type);
    }

    public Animaction(Animation<TextureRegion>[] playerAnimation, Animation<TextureRegion>[] itemAnimation, Animaction.Type type, float[][] itemPosition){
        playerAnim = playerAnimation;
        itemAnim = itemAnimation;
        position = itemPosition;
        withItem = (itemAnim != null);
        setType(this.type = type);
    }

    private void setType(Type type){
        Animation.PlayMode playmode = Animation.PlayMode.NORMAL;

        switch(type){
            case ATTACK:{
                time = TIME_ATTACK;
                playmode = Animation.PlayMode.NORMAL;
                break;
            }
            case PAUSE:{
                time = TIME_PAUSE;
                playmode = Animation.PlayMode.NORMAL;
                break;
            }
            case WALK:{
                time = TIME_WALK;
                playmode = Animation.PlayMode.LOOP;
                looping = true;
            }
        }

        for(int direction:DIRECTIONS){
            playerAnim[direction].setFrameDuration(time);
            playerAnim[direction].setPlayMode(playmode);

            if(withItem){
                itemAnim[direction].setFrameDuration(time);
                itemAnim[direction].setPlayMode(playmode);
            }
        }
    }

    public void setDeltaPosition(float[][] deltaPosition){
        position = deltaPosition;
    }

    public Type getType(){
        return type;
    }

    public float getDuration(){
        return playerAnim[0].getAnimationDuration();
    }

    public Animation<TextureRegion>[] getPlayerAnimation(){
        return playerAnim;
    }

    public int getPlayerKeyFrameCount(int direction){
        return playerAnim[direction].getKeyFrames().length;
    }

    public int getPlayerKeyFrameIndex(float stateTime, int direction){
        return playerAnim[direction].getKeyFrameIndex(stateTime);
    }

    public int getPlayerFrameWidth(){
        return ((TextureRegion)playerAnim[0].getKeyFrame(0)).getRegionWidth();
    }

    public int getPlayerFrameHeight(){
        return ((TextureRegion)playerAnim[0].getKeyFrame(0)).getRegionHeight();
    }

    public TextureRegion getPlayerKeyFrame(float stateTime, int direction){
        return (TextureRegion)playerAnim[direction].getKeyFrame(stateTime, looping);
    }

    public TextureRegion getItemKeyFrame(float stateTime, int direction){
        if(withItem)
            return (TextureRegion)itemAnim[direction].getKeyFrame(stateTime, looping);
        return null;
    }

    public Animation<TextureRegion> getItem(int direction){
        if(withItem)
            return itemAnim[direction];
        return null;
    }

    public Float getItemX(int direction){
        return position[direction][0];
    }

    public Float getItemY(int direction){
        return position[direction][1];
    }

    public boolean isTypeAttack(){
        return type == Type.ATTACK;
    }

    public boolean isFinished(float stateTime, int direction){
        return !looping && playerAnim[direction].isAnimationFinished(stateTime);
    }

    public boolean isWithItem(){
        return withItem;
    }

    public boolean isLoop(){
        return looping;
    }

    public void drawItem(Batch batch, float time, float playerX, float playerY, int direction){
        if(withItem){
            batch.draw((TextureRegion)itemAnim[direction].getKeyFrame(time, looping),
                playerX+position[direction][0], playerY+position[direction][1]);
        }
    }

    public static Animation<TextureRegion> createAnimation(String fileName, int frames){
        int tileW, tileH;
        Texture tileSet = new Texture(Gdx.files.internal(fileName+".png"));

        tileH = tileSet.getHeight();
        tileW = tileSet.getWidth()/frames;

        TextureRegion[][] tiles = TextureRegion.split(tileSet, tileW, tileH);
        return new Animation<TextureRegion>(TIME_WALK, tiles[0]);
    }

    public static Animation<TextureRegion>[] createAnimationArray(String fileName, int frames){
        Texture tileSet = new Texture(Gdx.files.internal(fileName+".png"));

        int tileH = tileSet.getHeight()/DIRECTIONS.length;
        int tileW = tileSet.getWidth()/frames;

        TextureRegion[][] tiles = TextureRegion.split(tileSet, tileW, tileH);
        Array<Animation<TextureRegion>> anim = new Array<>(true, DIRECTIONS.length);

        for(int direction:DIRECTIONS){
            anim.set(direction, new Animation<>(TIME_WALK, tiles[direction]));
        }

        return anim.toArray();
    }

    public static Animation<TextureRegion>[] createAnimationArrayWithCombine(String fileName, int frames){
        Array<Animation<TextureRegion>> anim = new Array<>(true, DIRECTIONS.length);

        for(int direction:DIRECTIONS){
            anim.set(direction, createAnimation(fileName+direction, frames));
        }

        return anim.toArray();
    }
}
