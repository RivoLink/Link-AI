package mg.rivolink.ai.linkai.algorithm;

public final class AlgorithmManager {

    private Algorithm algo = Algorithm.getSaved();

    public Algorithm getAlgorithm(){
        return this.algo;
    }

    public void setAlgorithm(Algorithm algo){
        this.algo = algo;
        Algorithm.save(algo);
    }
}
