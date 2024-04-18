package mg.rivolink.ai.linkai.agents;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Agent {

    public enum Action {

        GO_AHEAD(0),
        TURN_LEFT(1),
        TURN_RIGHT(2);

        public final int index;

        Action(int index){
            this.index = index;
        }

        public static Action get(int index){
            switch(index){
                default:
                case 0: return GO_AHEAD;
                case 1: return TURN_LEFT;
                case 2: return TURN_RIGHT;
            }
        }
    }

    public enum Orientation {

        UP(0, "Up"),
        RIGHT(1, "Right"),
        DOWN(2, "Down"),
        LEFT(3, "Left");

        final int val;
        final String text;

        Orientation(int val, String text){
            this.val = val;
            this.text = text;
        }

        public String toString(){
            return text;
        }

        // clockwise
        public Orientation plus(){
            switch(this){
                default:
                case UP: return RIGHT;
                case RIGHT: return DOWN;
                case DOWN: return LEFT;
                case LEFT: return UP;
            }
        }

        public Orientation minus(){
            switch(this){
                default:
                case UP: return LEFT;
                case LEFT: return DOWN;
                case DOWN: return RIGHT;
                case RIGHT: return UP;
            }
        }
    }

    private int x;
    private int y;
    private Orientation ori;

    public Agent(int x, int y, Orientation ori){
        this.x = x;
        this.y = y;
        this.ori = ori;
    }

    public void set(int x, int y, Orientation ori){
        this.x = x;
        this.y = y;
        this.ori = ori;
    }

    public int getX(){
        return x;
    }

    public int getY(){
        return y;
    }

    public void setPosition(int x, int y){
        this.x = x;
        this.y = y;
    }

    public Orientation getOrientation(){
        return ori;
    }

    public void setOrientation(Orientation ori){
        this.ori = ori;
    }

    public void draw(Batch batch, TextureRegion[] regions){
        batch.draw(regions[ori.val], x-8, y-4);
    }
}
