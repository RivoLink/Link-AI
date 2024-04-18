package mg.rivolink.ai.linkai.screens;

import static mg.rivolink.ai.linkai.screens.viewports.GameViewport.HEIGHT;
import static mg.rivolink.ai.linkai.screens.viewports.GameViewport.WIDTH;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import mg.rivolink.ai.linkai.Data;
import mg.rivolink.ai.linkai.agents.Agent;
import mg.rivolink.ai.linkai.agents.animations.Loopaction;
import mg.rivolink.ai.linkai.agents.qlearning.QLearn;
import mg.rivolink.ai.linkai.environments.Environment;
import mg.rivolink.ai.linkai.joypad.Joypad;
import mg.rivolink.ai.linkai.joypad.JoypadListener;
import mg.rivolink.ai.linkai.screens.ui.LogWindow;
import mg.rivolink.ai.linkai.screens.ui.PointerPop;
import mg.rivolink.ai.linkai.screens.ui.SettingWindow;
import mg.rivolink.ai.linkai.screens.ui.StatsPanel;
import mg.rivolink.ai.linkai.screens.ui.VertiPanel;
import mg.rivolink.ai.linkai.screens.viewports.GameViewport;

public class PlayScreen extends ScreenAdapter implements JoypadListener,SettingWindow.SettingListener {

    public static final int START_X = 250;

    private Button butt_grid;
    private Button butt_learning;
    private Button butt_automate;

    private StatsPanel spanel;

    private PointerPop pointer_pop;

    private Button butt_setting;
    private SettingWindow win_setting;

    private Button butt_qtable;
    private LogWindow win_qtable;

    private Button butt_stats;
    private LogWindow win_stats;

    private Stage stage;
    private VertiPanel panel;

    private Batch batch;
    private Joypad joypad;
    private Loopaction zelda;

    private TextureRegion[] link;
    private TextureRegion[] colors;
    private OrthographicCamera camera;

    private Skin skin = Data.SkinLoader.skin;

    private float e = 1f;

    private Agent agent;
    private Environment env;
    private Environment.Transition t;

    public PlayScreen(){

        batch = new SpriteBatch();
        camera = GameViewport.getInstance().getCamera();

        stage = new Stage();
        stage.getViewport().setCamera(camera);

        agent = new Agent(4*16, 3*16, Agent.Orientation.DOWN);

        env = new Environment(Data.MapLoader.level_01);
        t = env.init(agent);

        colors = Data.TextureRegionLoader.colors;

        link = Data.TextureRegionLoader.link;

        zelda = new Loopaction(Data.AtlasLoader.zelda_atlas, 6);
        zelda.setPosition(9*16, 7*16);

        joypad = new Joypad();
        joypad.addJoypadListener(this);

        Gdx.input.setInputProcessor(new InputMultiplexer(stage, joypad));

        createStage();
    }

    private void createStage(){
        panel = new VertiPanel(skin);

        spanel = new StatsPanel(skin);
        spanel.setPosition(100,50);
        spanel.setStats(t.sp);

        butt_setting = new TextButton("Setting", skin);
        butt_setting.addListener(listener);
        panel.addActor(butt_setting, true);

        butt_automate = new TextButton("Automate", skin);
        butt_automate.addListener(listener);
        panel.addActor(butt_automate, true);

        butt_learning = new TextButton("Learning", skin);
        butt_learning.addListener(listener);
        butt_learning.setChecked(true);
        panel.addActor(butt_learning, true);

        butt_grid = new TextButton("Grid", skin);
        butt_grid.addListener(listener);
        panel.addActor(butt_grid, true);

        win_setting = new SettingWindow("Setting", skin);
        win_setting.setSettingListener(this);
        win_setting.setSize(400, 300);
        win_setting.setPosition(295, 100);
        win_setting.setVisible(false);
        stage.addActor(win_setting);

        butt_qtable = new TextButton("Q-table", skin);
        butt_qtable.addListener(listener);
        panel.addActor(butt_qtable);

        win_qtable = new LogWindow("Q-table", skin);
        win_qtable.setPosition(START_X, HEIGHT/2-20);
        win_qtable.setSize(320, 240);
        win_qtable.setVisible(false);
        stage.addActor(win_qtable);

        butt_stats = new TextButton("Stats", skin);
        butt_stats.addListener(listener);
        panel.addActor(butt_stats);

        win_stats = new LogWindow("Stats", skin);
        win_stats.setPosition(START_X+30, HEIGHT/2-50);
        win_stats.setSize(310, 240);
        win_stats.setVisible(false);
        stage.addActor(win_stats);

        pointer_pop = new PointerPop(skin);

        stage.addActor(pointer_pop);
        stage.addActor(panel);
    }

    @Override
    public void onSettingChange(SettingWindow.Setting setting){
        zelda.setPosition(
            setting.zeldaX,
            setting.zeldaY
        );

        agent.set(
            setting.linkX,
            setting.linkY,
            agent.getOrientation()
        );

        env.init(agent);
    }

    @Override
    public void joypadInput(int direction, int px, int py){
        Agent.Action action = null;

        switch(direction){
            case Joypad.STOP:{
                break;
            }
            case Joypad.UP:{
                action = Agent.Action.GO_AHEAD;
                break;
            }
            case Joypad.LEFT:{
                action = Agent.Action.TURN_LEFT;
                break;
            }
            case Joypad.RIGHT:{
                action = Agent.Action.TURN_RIGHT;
                break;
            }
            default:{
                pointer_pop.showPointerPosition(px, py);
            }
        }

        if(action != null && !QLearn.useRL){
            step(action);
        }
    }

    private void step(Agent.Action action){
        env.step(action);

        agent.setPosition(t.x, t.y);
        agent.setOrientation(t.ori);

        if(!QLearn.stopLearning)
            QLearn.update(t.s,t.a,t.r,t.sp);

        win_qtable.setText(QLearn.qualities());
        win_stats.addText(String.format(
            "%s %s %s",
            t.toString(),
            QLearn.Q(t.s, t.a),
            QLearn.e_greedy(0, t.sp)
        ));
    }

    public void drawState(Batch batch){
        batch.draw(colors[t.sp[0]], WIDTH-48, HEIGHT-48);
        batch.draw(colors[t.sp[1]], WIDTH-32, HEIGHT-32);
        batch.draw(colors[t.sp[2]], WIDTH-16, HEIGHT-48);
    }

    @Override
    public void render(float delta){
        GameViewport.clear();

        if(QLearn.useRL && env.check(delta)){
            e = Math.max(0.1f, e-0.001f);
            step(QLearn.e_greedy(e, t.sp));
        }

        camera.update();

        env.setView(camera);
        env.render(delta);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        drawState(batch);

        agent.draw(batch, link);
        zelda.draw(batch, delta);
        joypad.draw(batch);

        batch.end();

        stage.act(delta);
        stage.draw();
    }

    ClickListener listener = new ClickListener(){
        @Override
        public boolean touchDown(InputEvent event, float x, float y, int pointer, int index){
            Actor actor = event.getListenerActor();

            Window window = null;
            boolean isVisible = false;

            if(actor == butt_automate){
                QLearn.useRL = !QLearn.useRL;
                win_setting.canSave(QLearn.useRL);
            }
            else if(actor == butt_learning){
                QLearn.stopLearning = butt_learning.isChecked();
            }
            else if(actor == butt_grid){
                env.showStates(!butt_grid.isChecked());
            }
            else if(actor == butt_setting){
                isVisible = win_setting.isVisible();

                win_setting.setX(agent.getX(), 0);
                win_setting.setY(agent.getY(), 0);

                win_setting.setX(zelda.getX(), 1);
                win_setting.setY(zelda.getY(), 1);

                window = win_setting;
            }
            else if(actor == butt_qtable){
                isVisible = win_qtable.isVisible();
                window = win_qtable;
            }
            else if(actor == butt_stats){
                isVisible = win_stats.isVisible();
                window = win_stats;
            }

            if(window != null)
                window.setVisible(!isVisible);

            return super.touchDown(event, x, y, pointer, index);
        }
    };
}
