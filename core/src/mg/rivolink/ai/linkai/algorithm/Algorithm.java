package mg.rivolink.ai.linkai.algorithm;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public enum Algorithm {
    Q_TABLE("Q-Table"),
    NEURAL_NETWORK("DQN");

    public static final Algorithm[] ALL = {
        Q_TABLE,
        NEURAL_NETWORK,
    };

    private final String label;
    private static final Preferences prefs = Gdx.app.getPreferences("linkai");

    Algorithm(String label) {
        this.label = label;
    }

    public static void save(Algorithm algo) {
        prefs.putString("algorithm", algo.name());
        prefs.flush();
    }

    public static Algorithm getSaved() {
        String dfault = Algorithm.Q_TABLE.name();
        String saved = prefs.getString("algorithm", dfault);
        return Algorithm.valueOf(saved);
    }

    @Override
    public String toString() {
        return label;
    }
}
