package mg.rivolink.ai.linkai.screens.ui;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import mg.rivolink.ai.linkai.Data;

public class StatsPanel extends Group {

    private Image topImage;
    private Image leftImage;
    private Image rightImage;

    private Label topLabel;
    private Label leftLabel;
    private Label rightLabel;

    private Drawable[] colors = Data.DrawableLoader.colors;

    public StatsPanel(Skin skin){

        leftImage = new Image();
        leftImage.setSize(50, 50);
        leftImage.setPosition(0, 0);
        addActor(leftImage);

        leftLabel = new Label("", skin);
        leftLabel.setPosition(25, 25);
        addActor(leftLabel);

        topImage = new Image();
        topImage.setSize(50, 50);
        topImage.setPosition(50, 50);
        addActor(topImage);

        topLabel = new Label("", skin);
        topLabel.setPosition(75, 75);
        addActor(topLabel);

        rightImage = new Image();
        rightImage.setSize(50, 50);
        rightImage.setPosition(100, 0);
        addActor(rightImage);

        rightLabel = new Label("", skin);
        rightLabel.setPosition(125, 25);
        addActor(rightLabel);
    }

    public void setStats(int[] stats){
        leftLabel.setText(Integer.toString(stats[0]));
        leftImage.setDrawable(colors[stats[0]]);

        topLabel.setText(Integer.toString(stats[1]));
        topImage.setDrawable(colors[stats[1]]);

        rightLabel.setText(Integer.toString(stats[2]));
        rightImage.setDrawable(colors[stats[2]]);
    }
}
