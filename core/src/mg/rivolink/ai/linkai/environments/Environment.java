package mg.rivolink.ai.linkai.environments;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;

import mg.rivolink.ai.linkai.agents.Agent;
import mg.rivolink.ai.linkai.environments.maps.MapRenderer;

public class Environment {

    public static final int LEFT = 0;
    public static final int AHEAD = 1;
    public static final int RIGHT = 2;

    public static final int DEST = 0;
    public static final int FREE = 1;
    public static final int BLOCKED = 2;
    public static final int INTERDIT = 3;

    public static final float INTERVAL = 0.1f;

    public static class Transition{

        public int x = 0;
        public int y = 0;
        public Agent.Orientation ori;

        // left, ahead, right
        public int[] s = {0, 0, 0};
        public int a = 0;
        public int[] sp = {0, 0, 0};
        public int r = 0;

        public Transition(){
            x = y = 0;
            ori = Agent.Orientation.DOWN;
        }

        public Transition(int x, int y, Agent.Orientation ori){
            this.x = x;
            this.y = y;
            this.ori = ori;
        }

        public void init(){
            s(sp);
        }

        public void init(int[] s){
            s(s);
        }

        public void copyTo(Transition t){
            t.x = x;
            t.y = y;
            t.ori = ori;
            t.sp(sp);
        }

        public void s(int[] s){
            this.s[LEFT] = s[LEFT];
            this.s[AHEAD] = s[AHEAD];
            this.s[RIGHT] = s[RIGHT];
        }

        public void sp(int[] sp){
            this.sp[LEFT] = sp[LEFT];
            this.sp[AHEAD] = sp[AHEAD];
            this.sp[RIGHT] = sp[RIGHT];
        }

        @Override
        public String toString(){
            return String.format(
                "s:{%d, %d, %d} a:%d r:%d sp:{%d, %d, %d}",
                s[0], s[1], s[2], a, r, sp[0], sp[1], sp[2]
            );
        }
    }

    private float time;

    private Transition t;
    private Transition iniTrans;

    private MapRenderer map;

    public Environment(TiledMap map){
        this.t = new Transition();
        this.map = new MapRenderer(map);
    }

    public Transition init(Agent agent){
        iniTrans = new Transition(
            agent.getX(),
            agent.getY(),
            agent.getOrientation()
        );

        iniTrans.copyTo(t);
        state(false);

        return t;
    }

    public MapRenderer getMap(){
        return map;
    }

    public void showStates(boolean show){
        map.getCollisionLayer().setVisible(show);
    }

    public boolean check(float delta){
        if((time += delta) >= INTERVAL){
            time -= INTERVAL;
            return true;
        }
        return false;
    }

    private int translate(int tileID){
        switch(tileID){
            case MapRenderer.DEST:return DEST;
            case MapRenderer.FREE:return FREE;
            case MapRenderer.BLOCKED:return BLOCKED;
            default:
            case MapRenderer.INTERDIT:return INTERDIT;
        }
    }

    private void sp(int xL, int yL, int xA, int yA, int xR, int yR){
        t.sp[LEFT] = translate(map.getId(xL, yL));
        t.sp[AHEAD] = translate(map.getId(xA, yA));
        t.sp[RIGHT] = translate(map.getId(xR, yR));
    }

    private void state(boolean move){
        int x = t.x/map.tileW;
        int y = t.y/map.tileH;

        switch(t.ori){
            case UP:{
                if(move)
                    t.y = (++y)*map.tileH;
                sp(x-1, y, x, y+1, x+1, y);
                break;
            }
            case DOWN:{
                if(move)
                    t.y = (--y)*map.tileH;
                sp(x+1, y, x, y-1, x-1, y);
                break;
            }
            case LEFT:{
                if(move)
                    t.x = (--x)*map.tileW;
                sp(x, y-1, x-1, y, x, y+1);
                break;
            }
            case RIGHT:{
                if(move)
                    t.x = (++x)*map.tileW;
                sp(x, y+1, x+1, y, x, y-1);
                break;
            }
        }
    }

    public Transition step(Agent.Action action){
        t.init();
        t.r = 0;
        t.a = action.index;
        switch(action){
            case GO_AHEAD:{
                if(t.s[1] == INTERDIT){
                    iniTrans.copyTo(t);
                    t.r = -3;
                    state(false);
                    return t;
                }
                else if(t.s[1] == BLOCKED){
                    t.sp(t.s);
                    return t;
                }
                else if(t.s[1] == DEST){
                    iniTrans.copyTo(t);
                    t.r = 5;
                    state(false);
                    return t;
                }
                else{
                    t.r = 0;
                }
                break;
            }
            case TURN_LEFT:{
                t.ori = t.ori.minus();
                break;
            }
            case TURN_RIGHT:{
                t.ori = t.ori.plus();
                break;
            }
        }
        state(action == Agent.Action.GO_AHEAD);
        return t;
    }

    public void setView(OrthographicCamera camera){
        map.setView(camera);
    }

    public void render(float delta){
        map.render(delta);
    }
}
