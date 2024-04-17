package mg.rivolink.ai.linkai.screens.ui;

import static mg.rivolink.ai.linkai.screens.viewports.GameViewport.HEIGHT;
import static mg.rivolink.ai.linkai.screens.viewports.GameViewport.WIDTH;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class VertiPanel extends Group {

    private Button closeButt;
    private boolean open = false;

    private int childY_top;
    private int childY_bottom;

    public VertiPanel(Skin skin){
        setSize(100, HEIGHT);
        setPosition(WIDTH-5, 0);

        createBg();

        closeButt = new TextButton("Close", skin);
        closeButt.setPosition(5, getHeight()-45);
        closeButt.setSize(90, 40);
        closeButt.addListener(listener);

        super.addActor(closeButt);

        addListener(listener);

        childY_top = (int)getHeight()-55;
        childY_bottom = 5;
    }

    void createBg(){
        Pixmap pix = new Pixmap((int)getWidth(), (int)getHeight(), Pixmap.Format.RGB888);
        pix.setColor(Color.RED);
        pix.fill();

        super.addActor(new Image(new Texture(pix)));
        pix.dispose();
    }

    @Override
    public void addActor(Actor actor){
        childY_top -= 50;
        actor.setSize(90, 40);
        actor.setPosition(5, childY_top);
        super.addActor(actor);
    }

    public void addActor(Actor actor, boolean bottom){
        if(!bottom){
            addActor(actor);
        }
        else {
            actor.setSize(90, 40);
            actor.setPosition(5, childY_bottom);
            super.addActor(actor);
            childY_bottom += 50;
        }
    }

    public void close(){
        if(open){
            VertiPanel.this.addAction(Actions.sequence(
                Actions.moveTo(WIDTH-5, 0, 0.5f),
                Actions.run(new Runnable(){
                    @Override
                    public void run(){
                        open = false;
                        closeButt.setChecked(false);
                    }
                })
            ));
        }
    }

    EventListener listener = new ClickListener(){
        @Override
        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
            Actor actor = event.getListenerActor();
            if(!open){
                open = true;
                VertiPanel.this.addAction(
                    Actions.moveTo(WIDTH-getWidth(), 0, 0.5f)
                );
            }
            else if(actor == closeButt){
                VertiPanel.this.addAction(Actions.sequence(
                    Actions.moveTo(WIDTH-5, 0, 0.5f),
                    Actions.run(new Runnable(){
                        @Override
                        public void run(){
                            open = false;
                            closeButt.setChecked(false);
                        }
                    })
                ));
            }
            return super.touchDown(event, x, y, pointer, button);
        }
    };
}
