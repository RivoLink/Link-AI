package mg.rivolink.ai.linkai.agents.animations;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class Loopaction {

    private final float interval = 0.3f;

    private int x;
    private int y;

    private int count;
    private int index = 3;
    private float time = 0;

    private TextureAtlas atlas;

    public int getX(){
        return x;
    }

    public int getY(){
        return y;
    }

    public Loopaction(TextureAtlas atlas, int count){
        this.atlas = atlas;
        this.count = count;
    }

    public void setPosition(int x, int y){
        this.x = x;
        this.y = y;
    }

    public void draw(Batch batch, float delta){
        if((time += delta) > interval){
            index = (index+1) % count;
            time -= interval;
        }
        batch.draw(atlas.findRegion("sprite"+index), x-8, y);
    }
}
