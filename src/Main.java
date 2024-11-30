import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Main {

    public NeuralNetwork crossOverNetworks(NeuralNetwork network1, NeuralNetwork network2) {
        double[][][] newWeights = new double[network1.getWeights().length][][];

        for (int i = 0; i < network1.getWeights().length; i++) {
            double[][] crossedWeight2 = MatrixOperations.getWeights()[i]
        }
    }

    public static void main(String[] args) throws InterruptedException {
        // Run multiple games


        int gameCount = 32;

        NeuralNetwork[] networks = new NeuralNetwork[gameCount];

        for (int i = 0; i < gameCount; i++) {
            networks[i] = new NeuralNetwork(new int[] {24,12,8,4});
        }

        SnakeGameManager manager = new SnakeGameManager(gameCount, 20, 20, networks);

        SnakeGame singleGame = manager.getGames().getFirst();
        SnakeGameDisplay displayer = new SnakeGameDisplay(singleGame);

        while (manager.getGames().stream().anyMatch(game -> !game.isGameOver())) {
            manager.updateGames();
            displayer.refresh();
            Thread.sleep(100); // Simulate step time
        }

        List<SnakeGame> allSnakes = manager.getGames();
        allSnakes.sort(Comparator.comparingDouble(SnakeGame::getFitness));

        List<SnakeGame> topFiveSnakes = allSnakes.subList(Math.max(allSnakes.size() - 5, 0), allSnakes.size());

        for (int i = 0; i < gameCount; i++) {

        }

    }
}
