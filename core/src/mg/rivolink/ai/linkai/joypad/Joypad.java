package mg.rivolink.ai.linkai.joypad;

import static mg.rivolink.ai.linkai.Data.TextureLoader.joypad_direction;
import static mg.rivolink.ai.linkai.screens.viewports.GameViewport.WIDTH;
import static mg.rivolink.ai.linkai.screens.viewports.GameViewport.ratioH;
import static mg.rivolink.ai.linkai.screens.viewports.GameViewport.ratioW;
import static mg.rivolink.ai.linkai.screens.viewports.GameViewport.unproject;

import java.util.ArrayList;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;

public class Joypad implements InputProcessor {

    private final int FIRST = 0;
    private final int SECOND = 1;

    public static final int KEY = 0;
    public static final int POINTER = 1;

    public static final int STOP = -1;
    public static final int UP = 0;
    public static final int DOWN = 1;
    public static final int LEFT = 2;
    public static final int RIGHT = 3;

    private int xdRef = WIDTH-150,  ydRef = -30;

    private boolean fixPosition = true;
    private boolean isDirVisible = fixPosition;

    private Pointer pFirst;
    private Pointer pSecond;

    private TextureRegion imgDir;

    private ArrayList<JoypadListener> listeners;

    public Joypad(){
        pFirst = new Pointer();
        pSecond = new Pointer();

        listeners = new ArrayList<>();

        imgDir = new TextureRegion(joypad_direction, 0, 0, 128, 128);
    }

    public boolean isInDir(Pointer p){
        return p.isIn(xdRef, ydRef, 128);
    }

    public int getDirKey(int key){
        switch(key){
            case Keys.UP: return Joypad.UP;
            case Keys.LEFT: return Joypad.LEFT;
            case Keys.RIGHT: return Joypad.RIGHT;
            default: return Joypad.STOP;
        }
    }

    public int[] getDirCode(){
        int[] params = {0, 0, STOP};
        if(isDirVisible){
            if(!isInDir(pSecond)){
                params[0] = pFirst.getX();
                params[1] = pFirst.getY();
                if(pFirst.isIn(xdRef+43, ydRef+86, 43)) params[2] = UP;
                else if(pFirst.isIn(xdRef+43, ydRef, 43)) params[2] = DOWN;
                else if(pFirst.isIn(xdRef, ydRef+43, 43)) params[2] = LEFT;
                else if(pFirst.isIn(xdRef+86, ydRef+43, 43)) params[2] = RIGHT;
            }
            if(!isInDir(pFirst)){
                if(pSecond.isIn(xdRef+43, ydRef+86, 43)) params[2] = UP;
                else if(pSecond.isIn(xdRef+43, ydRef, 43)) params[2] = DOWN;
                else if(pSecond.isIn(xdRef, ydRef+43, 43)) params[2] = LEFT;
                else if(pSecond.isIn(xdRef+86, ydRef+43, 43)) params[2] = RIGHT;
            }
        }
        return params;
    }

    public void addJoypadListener(JoypadListener joypadListener){
        listeners.add(joypadListener);
    }

    public void draw(Batch batch){
        if(isDirVisible)
            batch.draw(joypad_direction, xdRef, ydRef);
    }

    @Override
    public boolean touchDown(int x, int y, int pointer, int button){
        Vector3 unproject = unproject(x, y);
        x = (int)unproject.x;
        y = (int)unproject.y;

        switch(pointer){
            case FIRST:{
                pFirst.setPosition(x, y);
                if(!fixPosition){
                    xdRef = pFirst.x-ratioW(imgDir.getRegionWidth()/2);
                    ydRef = pFirst.y-ratioH(imgDir.getRegionHeight()/2);
                    isDirVisible = true;
                }
                break;
            }
            case SECOND:{
                pSecond.setPosition(x, y);
                break;
            }
            default:
        }

        int[] params = getDirCode();
        for(JoypadListener listener:listeners)
            listener.joypadInput(Joypad.POINTER, params[2], params[0], params[1]);

        return false;
    }

    @Override
    public boolean touchUp(int x, int y, int pointer, int button){
        switch(pointer){
            case FIRST:{
                pFirst.resetPosition();
                isDirVisible = fixPosition;
                break;
            }
            case SECOND:{
                pSecond.resetPosition();
                break;
            }
            default:
        }
        return false;
    }

    @Override
    public boolean keyDown(int key){
        int direction = getDirKey(key);

        for(JoypadListener listener:listeners)
            listener.joypadInput(Joypad.KEY, direction, 0, 0);

        return false;
    }

    @Override
    public boolean touchDragged(int x, int y, int pointer){
        return false;
    }

    @Override
    public boolean mouseMoved(int p1, int p2){
        return false;
    }

    @Override
    public boolean scrolled(int p1){
        return false;
    }

    @Override
    public boolean keyUp(int p1){
        return false;
    }

    @Override
    public boolean keyTyped(char p1){
        return false;
    }
}

class Pointer {

    public int x, y, radius = 8;

    public Pointer(){
        x = y = -2*radius;
    }

    public int getX(){
        return x;
    }

    public int getY(){
        return y;
    }

    public void resetPosition(){
        x = y = -2*radius;
    }

    public void setPosition(int x, int y){
        this.x = x;
        this.y = y;
    }

    public boolean isIn(int xo, int yo, int width){
        return this.isIn(xo, yo, width, width);
    }

    public boolean isIn(int xo, int yo, int width, int height){
        if((xo < x) && (x < xo+width))
            if((yo < y) && (y < yo+height))
                return true;
        return false;
    }
}
