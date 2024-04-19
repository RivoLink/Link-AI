package mg.rivolink.ai.linkai.agents;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import mg.rivolink.ai.linkai.agents.animations.Loopaction;
import mg.rivolink.ai.linkai.environments.Environment;

public class Target extends Loopaction implements Environment.Destination {

    private int lastX;
    private int lastY;

    public Target(TextureAtlas atlas, int count){
        super(atlas, count);
        super.setPosition(-1, -1);
    }

    @Override
    public int getLastX(){
        return lastX;
    }

    @Override
    public int getLastY(){
        return lastY;
    }

    @Override
    public void setPosition(int x, int y){
        lastX = getX();
        lastY = getY();
        super.setPosition(x, y);
    }
}
