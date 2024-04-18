package mg.rivolink.ai.linkai.screens.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class SettingWindow extends Window {

    public interface SettingListener {
        public void onSettingChange(Setting setting);
    }

    public class Setting {
        public int linkX, linkY, linkOri;
        public int zeldaX, zeldaY, zeldaOri;
    }

    private static final String[] ORI = {
        "Up", "Right", "Down", "Left"
    };

    private Label notif_label;

    private Label link_label;
    private TextField link_fieldX;
    private TextField link_fieldY;
    private SelectBox<String> link_selectOri;

    private Label zelda_label;
    private TextField zelda_fieldX;
    private TextField zelda_fieldY;
    private SelectBox<String> zelda_selectOri;

    private Button save_button;

    private SettingListener settingListener;

    int tileW = 16, tileH = 16;

    public SettingWindow(String title, Skin skin){
        super(title, skin);

        link_label = new Label("--- Link --------------------", skin);
        top().add(link_label).pad(3).expandX().fillX().row();

        link_fieldX = new TextField("Origin X: ", skin);
        //link_fieldX.setTextFieldFilter(new DigitsOnlyFilter());
        top().add(link_fieldX).pad(3).padRight(1.5f).expandX().fillX();

        link_fieldY = new TextField("Origin Y: ", skin);
        //link_fieldY.setTextFieldFilter(new DigitsOnlyFilter());
        top().add(link_fieldY).pad(3).padLeft(1.5f).expandX().fillX();

        link_selectOri = new SelectBox<>(skin);
        link_selectOri.setItems(ORI);
        top().add(link_selectOri).pad(3).padLeft(1.5f).expandX().fillX().row();

        zelda_label = new Label("--- Zelda --------------------", skin);
        top().add(zelda_label).pad(3).expandX().fillX().row();

        zelda_fieldX = new TextField("Origin X: ", skin);
        //zelda_fieldX.setTextFieldFilter(new DigitsOnlyFilter());
        top().add(zelda_fieldX).pad(3).padRight(1.5f).expandX().fillX();

        zelda_fieldY = new TextField("Origin Y: ", skin);
        //zelda_fieldY.setTextFieldFilter(new DigitsOnlyFilter());
        top().add(zelda_fieldY).pad(3).padLeft(1.5f).expandX().fillX();

        zelda_selectOri = new SelectBox<>(skin);
        zelda_selectOri.setItems(ORI);
        top().add(zelda_selectOri).pad(3).padLeft(1.5f).expandX().fillX().row();

        top().add().expandY().fillY().row();

        notif_label = new Label("", skin);
        top().add(notif_label).colspan(2).expandX().fillX().padLeft(3);

        save_button = new TextButton("Save", skin);
        save_button.addListener(new ClickListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int index){
                if(settingListener != null){
                    Setting setting = new Setting();
                    setting.linkX = (int)getAgentX(0);
                    setting.linkY = (int)getAgentY(0);
                    //setting.linkOri =
                    setting.zeldaX = (int)getAgentX(1);
                    setting.zeldaY = (int)getAgentY(1);
                    //setting.zeldaOri =

                    notif_label.setText("Setting saved.");
                    settingListener.onSettingChange(setting);
                }
                save_button.setChecked(true);
                return super.touchDown(event, x, y, pointer, index);
            }
        });
        top().add(save_button).expandX().pad(3).size(65, 30).right();
    }

    public void setSettingListener(SettingListener listener){
        settingListener = listener;
    }

    public void canSave(boolean automate){
        save_button.setDisabled(automate);
        if(automate)
            notif_label.setText("Please, stop automate before saving...");
        else
            notif_label.setText("");
    }

    public void setAgentX(int agent, int x){
        TextField fieldX = (agent == 0)?link_fieldX:zelda_fieldX;
        fieldX.setText("Origin X: " + x/tileW);
    }

    public void setAgentY(int agent, int y){
        TextField fieldY = (agent == 0)?link_fieldY:zelda_fieldY;
        fieldY.setText("Origin Y: " + y/tileH);
    }

    public void setOrientation(){
    }

    public float getAgentX(int agent){
        int x = 0;
        TextField fieldX = (agent == 0)?link_fieldX:zelda_fieldX;

        try {
            String textX = fieldX.getText().split(":")[1];
            x = tileW*Integer.parseInt(textX.trim());
        }
        catch(NumberFormatException e) {
        }

        return x;
    }

    public float getAgentY(int agent){
        int y = 0;
        TextField fieldY = (agent == 0)?link_fieldY:zelda_fieldY;

        try{
            String textX = fieldY.getText().split(":")[1];
            y = tileH*Integer.parseInt(textX.trim());
        }
        catch(NumberFormatException e) {
        }

        return y;
    }
}
