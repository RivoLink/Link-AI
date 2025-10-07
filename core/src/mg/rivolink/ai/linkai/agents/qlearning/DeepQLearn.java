package mg.rivolink.ai.linkai.agents.qlearning;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import mg.rivolink.ai.linkai.agents.Agent.Action;
import mg.rivolink.ai.Network;

public class DeepQLearn implements RL {

    private static class Experience {
        int[] s;
        int a;
        float r;
        int[] sp;
        boolean terminal;

        Experience(int[] s, int a, float r, int[] sp, boolean terminal) {
            this.s = s.clone();
            this.a = a;
            this.r = r;
            this.sp = sp.clone();
            this.terminal = terminal;
        }
    }

    private static final int STATE_SIZE = 3;
    private static final int ACTION_SIZE = 3;

    private static final int HIDDEN1_SIZE = 24;
    private static final int HIDDEN2_SIZE = 24;

    private static final int BUFFER_SIZE = 10000;
    private static final int BATCH_SIZE = 64;

    private static final int UPDATE_TARGET_FREQUENCY = 200;

    // Hyperparameters
    private static final float LEARNING_RATE = 0.01f;
    private static final float DISCOUNT_FACTOR = 0.95f;
    
    // Reward normalization
    private static final float REWARD_SCALE = 0.1f;

    public static boolean useRL = false;
    public static boolean stopLearning = false;

    private static Network qNetwork;
    private static Network targetNetwork;

    private static List<Experience> replayBuffer = new ArrayList<>();

    private static Random random = new Random();

    private static int totalUpdates = 0;
    private static int updateCounter = 0;

    static {
        initializeNetworks();
    }

    private static void initializeNetworks() {
        qNetwork = new Network.Builder()
            .inputSize(STATE_SIZE)
            .addHiddenLayer(HIDDEN1_SIZE)
            .addHiddenLayer(HIDDEN2_SIZE)
            .outputSize(ACTION_SIZE)
            .learningRate(LEARNING_RATE)
            .maxGradient(10.0f)
            .build();

        targetNetwork = qNetwork.copy();
    }

    public static void update(int[] s, int a, int r, int[] sp, boolean end) {
        Experience exp = new Experience(s, a, r * REWARD_SCALE, sp, end);

        if (replayBuffer.size() >= BUFFER_SIZE) {
            replayBuffer.remove(0);
        }
        replayBuffer.add(exp);

        // Only train if we have enough experiences
        if (replayBuffer.size() >= BATCH_SIZE * 2) {
            trainOnBatch();
        }

        updateCounter++;
        if (updateCounter >= UPDATE_TARGET_FREQUENCY) {
            updateTarget();
            updateCounter = 0;
        }
    }

    private static void trainOnBatch() {
        for (int i = 0; i < BATCH_SIZE; i++) {
            Experience exp = replayBuffer.get(random.nextInt(replayBuffer.size()));

            float[] state = stateToFloat(exp.s);
            float[] nextState = stateToFloat(exp.sp);

            float[] qValues = qNetwork.predict(state);

            float targetQ;
            if (exp.terminal) {
                targetQ = exp.r;
            } else {
                float[] nextQValues = targetNetwork.predict(nextState);
                float maxNextQ = max(nextQValues);
                targetQ = exp.r + DISCOUNT_FACTOR * maxNextQ;
            }

            // Only update the Q-value for the action taken
            float[] target = qValues.clone();
            target[exp.a] = targetQ;

            qNetwork.train(state, target);
            totalUpdates++;
        }
    }

    private static float[] stateToFloat(int[] state) {
        float[] result = new float[STATE_SIZE];
        for (int i = 0; i < STATE_SIZE; i++) {
            result[i] = state[i] / 3.0f;
        }
        return result;
    }

    public static float Q(int[] s, int a) {
        float[] state = stateToFloat(s);
        float[] qValues = qNetwork.predict(state);
        return qValues[a];
    }

    public static Action e_greedy(float epsilon, int[] state) {
        if (random.nextFloat() < epsilon) {
            return Action.get(random.nextInt(ACTION_SIZE));
        } else {
            return greedy(state);
        }
    }

    public static Action greedy(int[] state) {
        float[] stateFloat = stateToFloat(state);
        float[] qValues = qNetwork.predict(stateFloat);

        int maxAction = 0;
        float maxQ = qValues[0];

        for (int a = 1; a < ACTION_SIZE; a++) {
            if (qValues[a] > maxQ) {
                maxQ = qValues[a];
                maxAction = a;
            }
        }

        return Action.get(maxAction);
    }

    public static float[] getQValues(int[] state) {
        float[] stateFloat = stateToFloat(state);
        return qNetwork.predict(stateFloat);
    }

    private static void updateTarget() {
        targetNetwork.copyWeightsFrom(qNetwork);
    }

    private static float max(float[] array) {
        float max = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] > max) {
                max = array[i];
            }
        }
        return max;
    }

    public static String qualities() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Deep Q-Network Info ===\n");
        sb.append("Buffer size: ").append(replayBuffer.size())
          .append(" / ").append(BUFFER_SIZE).append("\n");
        sb.append("Target updates: ").append(updateCounter)
          .append(" / ").append(UPDATE_TARGET_FREQUENCY).append("\n");
        sb.append("Total training steps: ").append(totalUpdates).append("\n");
        sb.append("Learning rate: ").append(LEARNING_RATE).append("\n");
        sb.append("Discount factor: ").append(DISCOUNT_FACTOR).append("\n\n");
        
        sb.append("=== Sample Q-values ===\n");
        int[][] sampleStates = {
            {0, 0, 0}, {0, 1, 0}, {0, 2, 0},
            {1, 0, 1}, {1, 1, 1}, {1, 2, 1},
            {2, 0, 2}, {2, 1, 2}, {2, 2, 2}
        };
        
        for (int[] state : sampleStates) {
            float[] qVals = getQValues(state);
            sb.append(String.format("[%d,%d,%d]: [%.3f, %.3f, %.3f]\n", 
                state[0], state[1], state[2], qVals[0], qVals[1], qVals[2]));
        }
        
        if (!replayBuffer.isEmpty()) {
            sb.append("\n=== Recent Experiences ===\n");
            int start = Math.max(0, replayBuffer.size() - 5);
            for (int i = start; i < replayBuffer.size(); i++) {
                Experience exp = replayBuffer.get(i);
                sb.append(String.format("s:[%d,%d,%d] a:%d r:%.2f sp:[%d,%d,%d] term:%s\n",
                    exp.s[0], exp.s[1], exp.s[2], exp.a, exp.r,
                    exp.sp[0], exp.sp[1], exp.sp[2], exp.terminal));
            }
        }
        
        return sb.toString();
    }

    public static void reset() {
        initializeNetworks();
        replayBuffer.clear();
        totalUpdates = 0;
        updateCounter = 0;
    }

    public static String getStats() {
        StringBuilder sb = new StringBuilder();
        sb.append("Experiences: ").append(replayBuffer.size()).append("\n");
        sb.append("Updates: ").append(totalUpdates).append("\n");
        sb.append("Next target sync: ").append(UPDATE_TARGET_FREQUENCY - updateCounter);
        return sb.toString();
    }
}
