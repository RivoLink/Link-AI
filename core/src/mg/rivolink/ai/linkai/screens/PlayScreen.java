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
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import mg.rivolink.ai.linkai.Data;
import mg.rivolink.ai.linkai.agents.Agent;
import mg.rivolink.ai.linkai.agents.Target;
import mg.rivolink.ai.linkai.agents.qlearning.QLearn;
import mg.rivolink.ai.linkai.agents.qlearning.DeepQLearn;
import mg.rivolink.ai.linkai.algorithm.Algorithm;
import mg.rivolink.ai.linkai.environments.Environment;
import mg.rivolink.ai.linkai.joypad.Joypad;
import mg.rivolink.ai.linkai.joypad.JoypadListener;
import mg.rivolink.ai.linkai.screens.ui.AlgorithmSelect;
import mg.rivolink.ai.linkai.screens.ui.LogWindow;
import mg.rivolink.ai.linkai.screens.ui.PointerPop;
import mg.rivolink.ai.linkai.screens.ui.SettingWindow;
import mg.rivolink.ai.linkai.screens.ui.StatsPanel;
import mg.rivolink.ai.linkai.screens.ui.VertiPanel;
import mg.rivolink.ai.linkai.screens.viewports.GameViewport;

public class PlayScreen extends ScreenAdapter implements JoypadListener, SettingWindow.SettingListener {

    public static final int START_X = 250;

    private Button butt_grid;
    private Button butt_reset;
    private Button butt_learning;
    private Button butt_automate;

    private StatsPanel spanel;

    private PointerPop pointer_pop;
    private AlgorithmSelect select_algo;

    private Button butt_setting;
    private SettingWindow win_setting;

    private TextButton butt_qtable;
    private LogWindow win_qtable;

    private Button butt_stats;
    private LogWindow win_stats;

    private Stage stage;
    private VertiPanel panel;

    private Batch batch;
    private Joypad joypad;

    private TextureRegion[] link;
    private TextureRegion[] colors;
    private OrthographicCamera camera;

    private Skin skin = Data.SkinLoader.skin;

    private float epsilon = 1.0f;
    private float epsilonMin = 0.01f;
    private float epsilonDecay = 0.995f;

    private Agent agent;
    private Target zelda;

    private Environment env;
    private Environment.Transition t;

    private Algorithm currentAlgorithm = Algorithm.NEURAL_NETWORK;
    
    private int stepCount = 0;
    private int episodeCount = 0;
    private float totalReward = 0;

    public PlayScreen(){

        batch = new SpriteBatch();
        camera = GameViewport.getInstance().getCamera();

        stage = new Stage();
        stage.getViewport().setCamera(camera);

        colors = Data.TextureRegionLoader.colors;

        link = Data.TextureRegionLoader.link;

        zelda = new Target(Data.AtlasLoader.zelda_atlas, 6);
        zelda.setPosition(9*16, 7*16);

        agent = new Agent(4*16, 3*16, Agent.Orientation.DOWN);

        env = new Environment(Data.MapLoader.level_01);
        t = env.init(agent, zelda);

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

        select_algo = new AlgorithmSelect(skin);
        select_algo.setItems(Algorithm.ALL);
        select_algo.setSelected(Algorithm.NEURAL_NETWORK);
        select_algo.addListener(changeListener);
        panel.addActor(select_algo);

        butt_setting = new TextButton("Setting", skin);
        butt_setting.addListener(clickListener);
        panel.addActor(butt_setting, true);

        butt_automate = new TextButton("Automate", skin);
        butt_automate.addListener(clickListener);
        panel.addActor(butt_automate, true);

        butt_learning = new TextButton("Learning", skin);
        butt_learning.addListener(clickListener);
        butt_learning.setChecked(true);
        panel.addActor(butt_learning, true);

        butt_reset = new TextButton("Reset", skin);
        butt_reset.addListener(clickListener);
        panel.addActor(butt_reset, true);

        butt_grid = new TextButton("Grid", skin);
        butt_grid.addListener(clickListener);
        panel.addActor(butt_grid, true);

        win_setting = new SettingWindow("Setting", skin);
        win_setting.setSettingListener(this);
        win_setting.setSize(400, 300);
        win_setting.setPosition(295, 100);
        win_setting.setVisible(false);
        stage.addActor(win_setting);

        butt_qtable = new TextButton("Network", skin);
        butt_qtable.addListener(clickListener);
        panel.addActor(butt_qtable);

        win_qtable = new LogWindow("DQN Info", skin);
        win_qtable.setPosition(START_X, HEIGHT/2-20);
        win_qtable.setSize(320, 240);
        win_qtable.setVisible(false);
        stage.addActor(win_qtable);

        butt_stats = new TextButton("Stats", skin);
        butt_stats.addListener(clickListener);
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
            setting.linkOri
        );

        env.init(agent, zelda);

        resetEpisode();
    }

    @Override
    public void joypadInput(int type, int direction, int px, int py){
        Agent.Action action = null;

        switch(direction){
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
                if(type == Joypad.POINTER) {
                    pointer_pop.showPointerPosition(px, py);
                }
            }
        }

        if(action != null && !DeepQLearn.useRL && !QLearn.useRL){
            step(action);
        }
    }

    private void step(Agent.Action action){
        env.step(action);

        agent.setPosition(t.x, t.y);
        agent.setOrientation(t.ori);

        if(!DeepQLearn.stopLearning && !QLearn.stopLearning) {
            if(currentAlgorithm == Algorithm.NEURAL_NETWORK) {
                DeepQLearn.update(t.s, t.a, t.r, t.sp, t.end);
            } else {
                QLearn.update(t.s, t.a, t.r, t.sp);
            }
        }

        // Track stats
        stepCount++;
        totalReward += t.r;

        // Update UI
        updateNetworkInfo();
        updateStats();

        // Check for episode end
        if(t.end) {
            resetEpisode();
        }
    }

    private void resetEpisode() {
        episodeCount++;

        stepCount = 0;
        totalReward = 0;
        
        // Decay epsilon
        if(epsilon > epsilonMin) {
            epsilon *= epsilonDecay;
        }
    }

    private void updateNetworkInfo() {
        if(currentAlgorithm == Algorithm.NEURAL_NETWORK) {
            win_qtable.setText(DeepQLearn.qualities());
        } else {
            win_qtable.setText(QLearn.qualities());
        }
    }

    private void updateStats() {
        String sep = "----------\n";
        String stats = String.format(
            "Episode: %d\nSteps: %d\nTotal Reward: %.1f\nEpsilon: %.3f\n\n%s\nQ-values: %s",
            episodeCount,
            stepCount,
            totalReward,
            epsilon,
            t.toString(),
            currentAlgorithm == Algorithm.NEURAL_NETWORK ? 
                java.util.Arrays.toString(DeepQLearn.getQValues(t.sp)) : 
                String.format("[%.2f, %.2f, %.2f]", 
                    QLearn.Q(t.sp, 0), 
                    QLearn.Q(t.sp, 1), 
                    QLearn.Q(t.sp, 2))
        );
        win_stats.addText(sep + stats);
    }

    public void drawState(Batch batch){
        batch.draw(colors[t.sp[0]], WIDTH-48, HEIGHT-48);
        batch.draw(colors[t.sp[1]], WIDTH-32, HEIGHT-32);
        batch.draw(colors[t.sp[2]], WIDTH-16, HEIGHT-48);
    }

    @Override
    public void render(float delta){
        GameViewport.clear();

        boolean isNN = (currentAlgorithm == Algorithm.NEURAL_NETWORK);
        boolean useRL = isNN ? DeepQLearn.useRL : QLearn.useRL;

        if(useRL && env.check(delta)){
            Agent.Action action;

            if(currentAlgorithm == Algorithm.NEURAL_NETWORK) {
                action = DeepQLearn.e_greedy(epsilon, t.sp);
            } else {
                action = QLearn.e_greedy(epsilon, t.sp);
            }

            step(action);
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

    ChangeListener changeListener = new ChangeListener(){
        @Override
        public void changed(ChangeEvent event, Actor actor) {
            if (actor == select_algo) {
                switch (currentAlgorithm = select_algo.getSelected()) {
                    case Q_TABLE:
                        butt_qtable.setText("Table");
                        win_qtable.getTitleLabel().setText("Q-Table");
                        break;
                    case NEURAL_NETWORK:
                        butt_qtable.setText("Network");
                        win_qtable.getTitleLabel().setText("DQN Info");
                        break;
                    default:
                        break;
                }
            }
        }
    };

    ClickListener clickListener = new ClickListener(){
        @Override
        public boolean touchDown(InputEvent event, float x, float y, int pointer, int index){
            Actor actor = event.getListenerActor();

            Window window = null;
            boolean isVisible = false;

            if(actor == butt_automate){
                if(currentAlgorithm == Algorithm.NEURAL_NETWORK) {
                    DeepQLearn.useRL = !DeepQLearn.useRL;
                    win_setting.canSave(DeepQLearn.useRL);
                } else {
                    QLearn.useRL = !QLearn.useRL;
                    win_setting.canSave(QLearn.useRL);
                }
            }
            else if(actor == butt_learning){
                if(currentAlgorithm == Algorithm.NEURAL_NETWORK) {
                    DeepQLearn.stopLearning = butt_learning.isChecked();
                } else {
                    QLearn.stopLearning = butt_learning.isChecked();
                }
            }
            else if(actor == butt_reset){
                if(currentAlgorithm == Algorithm.NEURAL_NETWORK) {
                    DeepQLearn.reset();
                }
                epsilon = 1.0f;
                episodeCount = 0;
                resetEpisode();
            }
            else if(actor == butt_grid){
                env.showStates(!butt_grid.isChecked());
            }
            else if(actor == butt_setting){
                isVisible = win_setting.isVisible();

                win_setting.setAgentX(0, zelda.getX());
                win_setting.setAgentY(0, zelda.getY());

                win_setting.setAgentX(1, agent.getX());
                win_setting.setAgentY(1, agent.getY());
                win_setting.setAgentOri(1, agent.getOrientation());

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
