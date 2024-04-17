package mg.rivolink.ai.linkai.screens.ui;

import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Window;

public class PointerPop extends Window {

    private Label text;

    public PointerPop(Skin skin){
        super("Pointer", skin);

        text = new Label("px:00, py:00", skin);
        top().add(text).expand().fill();

        addAction(Actions.alpha(0));
        setSize(text.getPrefWidth()*1.1f, text.getPrefHeight()*2f);
    }

    private void setPointerPosition(int px, int py){
        text.setText(String.format("px:%s, py:%s", px/16, py/16));
    }

    public void showPointerPosition(int px, int py){
        float dx = 16;
        dx = (px > 800-1.5f*getPrefWidth())?-(dx+getPrefWidth()):dx;

        setPosition(px+dx, py);
        setPointerPosition(px, py);

        if(0 < getActions().size)
            getActions().pop();

        addAction(Actions.sequence(
            Actions.fadeIn(0.3f),
            Actions.delay(1),
            Actions.fadeOut(0.5f)
        ));
    }
}
