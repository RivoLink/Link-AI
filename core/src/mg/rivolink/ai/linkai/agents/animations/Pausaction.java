package mg.rivolink.ai.linkai.agents.animations;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class Pausaction extends Animaction {

    private final float playTime;

    private float time;

    public Pausaction(Animation<TextureRegion>[] playerAnimation, float interval){
        super(playerAnimation);
        this.playTime = playerAnimation[0].getAnimationDuration()+interval;
    }

    @Override
    public TextureRegion getPlayerKeyFrame(float stateTime,int direction){
        if((playTime < stateTime) && ((time = stateTime % playTime) <= playerAnim[direction].getAnimationDuration()))
            return super.getPlayerKeyFrame(time, direction);
        return super.getPlayerKeyFrame(0, direction);
    }

    public static Pausaction extractFrom(Animation<TextureRegion>[] walkAnimation, float interval){
        Array<Animation<TextureRegion>> pauseAnim = new Array<>(true, DIRECTIONS.length);

        for(int direction:DIRECTIONS){
            float duration = walkAnimation[direction].getFrameDuration();
            TextureRegion texture = (TextureRegion)walkAnimation[direction].getKeyFrame(0);

            pauseAnim.set(direction, new Animation<TextureRegion>(duration, texture, texture));
        }

        return new Pausaction(pauseAnim.toArray(), interval);
    }
}
