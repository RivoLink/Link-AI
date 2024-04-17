package mg.rivolink.ai.linkai.agents.qlearning;

import java.util.Arrays;
import java.util.Random;

import mg.rivolink.ai.linkai.agents.Agent.Action;

public class QLearn implements RL {

    public static final float[][] Q = new float[64][3];

    static final int[][] Q_target = {
        {1,0,0},{1,0,0},{1,0,0},{1,0,0},{1,0,0},{0,1,0},{0,1,0},{0,1,0},
        {0,1,0},{0,1,0},{0,1,0},{0,1,0},{0,1,0},{0,1,0},{0,1,0},{0,1,0},
        {1,0,0},{1,0,0},{1,0,0},{1,0,0},{0,0,1},{1,0,0},{1,0,0},{1,0,0},
        {0,0,1},{0,1,0},{0,1,0},{0,1,0},{0,0,1},{0,1,0},{0,1,0},{0,1,0},
        {1,0,0},{1,0,0},{1,0,0},{1,0,0},{0,0,1},{1,0,0},{1,0,0},{1,0,0},
        {0,0,1},{0,0,1},{0,1,0},{0,1,0},{0,0,1},{0,0,1},{0,1,0},{0,1,0},
        {1,0,0},{1,0,0},{1,0,0},{1,0,0},{0,0,1},{1,0,0},{1,0,0},{1,0,0},
        {0,0,1},{0,0,1},{0,1,0},{0,1,0},{0,0,1},{0,0,1},{0,1,0},{0,1,0},
    };

    public static boolean useRL = false;

    public static boolean stopLearning;

    public static void update(int[] s,int a,int r,int[] sp){
        int st = index(s);
        int stp1 = index(sp);
        int ap = greedy(sp).index;
        Q[st][a] = Q[st][a] + 0.1f*(r + 0.9f*Q[stp1][ap] - Q[st][a]);
    }

    public static float Q(int[] s,int a){
        return Q[index(s)][a];
    }

    private static int index(int[] state){
        return (state[0]<<4)|(state[1]<<2)|state[2];
    }

    public static Action e_greedy(float e,int[] state){
        Random r = new Random();
        if(r.nextFloat() < e){
            int i = r.nextInt(3);
            return Action.get(i);
        }
        else {
            return greedy(state);
        }
    }

    public static Action greedy(int[] state){
        int sI = index(state);

        int maxA = 0;
        float maxQ = Q[sI][0];

        for(int aI = 0; aI < 3; aI++){
            if(maxQ < Q[sI][aI]){
                maxA = aI;
                maxQ = Q[sI][aI];
            }
        }

        return Action.get(maxA);
    }

    public static int[] toBits(int n){
        return new int[]{
            (n & 0b110000) >> 4,
            (n & 0b001100) >> 2,
            (n & 0b000011)
        };
    }

    public static String qualities(){
        String q = "";
        for(int s = 0; s < Q.length; s++){
            q += Arrays.toString(toBits(s)).concat(":");
            q += Arrays.toString(Q[s]).concat("\n");
        }
        return q;
    }
}
