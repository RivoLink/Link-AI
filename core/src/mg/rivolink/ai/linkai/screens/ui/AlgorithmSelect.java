package mg.rivolink.ai.linkai.screens.ui;

import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;

import mg.rivolink.ai.linkai.algorithm.Algorithm;

public class AlgorithmSelect extends SelectBox<Algorithm> {

    public AlgorithmSelect(Skin skin){
        super(skin);

        SelectBoxStyle style = new SelectBoxStyle(this.getStyle());

        // no arrow
        style.background = skin.getDrawable("textfield");
        style.backgroundOver = style.background;
        style.backgroundOpen = style.background;
        this.setStyle(style);

        // text center
        this.setAlignment(Align.center);
        this.getList().setAlignment(Align.center);
    }
}
