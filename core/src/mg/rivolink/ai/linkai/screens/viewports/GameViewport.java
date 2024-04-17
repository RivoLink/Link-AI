package mg.rivolink.ai.linkai.screens.viewports;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FillViewport;

public class GameViewport extends FillViewport {

    public static final int WIDTH = 800;
    public static final int HEIGHT = 480;

    public static final int SCREEN_WIDTH = Gdx.graphics.getWidth();
    public static final int SCREEN_HEIGHT = Gdx.graphics.getHeight();

    private static float ratioWidth;
    private static float ratioHeight;

    private static GameViewport instance;
    private static OrthographicCamera camera;

    private GameViewport(){
        super(WIDTH, HEIGHT);

        camera = new OrthographicCamera();
        camera.setToOrtho(false);
        camera.update();

        this.setCamera(camera);
        this.apply(true);

        ratioWidth = (float)SCREEN_WIDTH/(float)WIDTH;
        ratioHeight = (float)SCREEN_HEIGHT/(float)HEIGHT;
    }

    public static GameViewport getInstance(){
        if(instance == null)
            instance = new GameViewport();

        return instance;
    }

    public static void clear(){
        Gdx.gl.glClearColor(1, 0, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glViewport(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
    }

    public static int ratioW(int width){
        return (int)ratioWidth*width;
    }

    public static int ratioH(int height){
        return (int)ratioHeight*height;
    }

    public static Vector2 ratio(float width, float height){
        return new Vector2(ratioWidth*width, ratioHeight*height);
    }

    public static Vector3 unproject(float x, float y){
        Vector3 unprojected = new Vector3(x, y, 0);
        camera.unproject(unprojected);

        return unprojected;
    }

    public OrthographicCamera getCamera(){
        return camera;
    }
}
