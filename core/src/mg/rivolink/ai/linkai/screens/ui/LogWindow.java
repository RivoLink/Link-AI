package mg.rivolink.ai.linkai.screens.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;

public class LogWindow extends Window {

    private Label text;
    private ScrollPane pane;

    public LogWindow(String title, Skin skin){
        super(title, skin);

        text = new Label("", skin);

        pane = new ScrollPane(text, skin);
        pane.getStyle().background = null;

        top().add(pane).expandX().fillX();

        addListener(new ActorGestureListener(){
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button){
                super.tap(event, x, y, count, button);

                if(2 < count)
                    text.setText("");
            }
        });
    }

    public void addText(String t){
        if(isVisible()){
            text.setText(String.format("%s%s\n", text.getText(), t));
            pane.setScrollPercentY(100);
        }
    }

    public void setText(String t){
        if(isVisible())
            text.setText(t);
    }
}
