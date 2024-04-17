package mg.rivolink.ai.linkai;

import com.badlogic.gdx.Game;

import mg.rivolink.ai.linkai.screens.PlayScreen;

public class GdxGame extends Game {

    @Override
    public void create(){
        Data.load();

        setScreen(new PlayScreen());
    }
}
