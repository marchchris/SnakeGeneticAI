import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class Main {
    public static double mutateWeight(double weight) {
        Random rand = new Random();
        double mutationFactor = 1 + (rand.nextDouble() * 0.2 - 0.1); // This gives a value between 0.9 and 1.1
        return weight * mutationFactor;
    }

    // Fitness-proportional selection
    public static SnakeGame selectParent(List<SnakeGame> topSnakes) {
        double totalFitness = topSnakes.stream().mapToDouble(SnakeGame::getFitness).sum();
        Random rand = new Random();
        double selectionPoint = rand.nextDouble() * totalFitness;

        double accumulatedFitness = 0;
        for (SnakeGame snake : topSnakes) {
            accumulatedFitness += snake.getFitness();
            if (accumulatedFitness >= selectionPoint) {
                return snake;
            }
        }
        return topSnakes.get(topSnakes.size() - 1); // fallback to last snake (though this should never be reached)
    }

    public static NeuralNetwork crossOverNetworks(NeuralNetwork network1, NeuralNetwork network2, float mutationRate) {
        double[][][] newWeights = new double[network1.getWeights().length][][];

        for (int i = 0; i < network1.getWeights().length; i++) {
            double[][] crossedWeight2 = MatrixOperations.multiplyByScalar(network2.getWeights()[i], Math.random());
            newWeights[i] = MatrixOperations.add(network1.getWeights()[i], crossedWeight2);
        }

        if (Math.random() < mutationRate) {
            Random rand = new Random();
            int index1 = rand.nextInt(newWeights.length);
            int index2 = rand.nextInt(newWeights[index1].length);
            int index3 = rand.nextInt(newWeights[index1][index2].length);

            double mutatedWeight = mutateWeight(newWeights[index1][index2][index3]);
            newWeights[index1][index2][index3] = mutatedWeight;
        }

        network1.setWeights(newWeights);
        return network1;
    }

    public static void main(String[] args) throws InterruptedException {

        int gameCount = 128;
        float mutationRate = 0.1f;
        int generation = 0;

        SnakeGameManager manager = new SnakeGameManager(gameCount, 20, 20);
        SnakeGame singleGame = manager.getGames().getFirst();
        SnakeGameDisplay displayer = new SnakeGameDisplay(singleGame);

        while (true) {
            while (manager.getGames().stream().anyMatch(game -> !game.isGameOver())) {
                manager.updateGames();
                displayer.refresh();
                Thread.sleep(100); // Simulate step time
            }

            List<SnakeGame> allSnakes = manager.getGames();
            allSnakes.sort(Comparator.comparingDouble(SnakeGame::getFitness));

            // Get the top 16 snakes
            List<SnakeGame> topSnakes = allSnakes.subList(Math.max(allSnakes.size() - 16, 0), allSnakes.size());

            NeuralNetwork[] nextGenerationNetworks = new NeuralNetwork[gameCount];

            // Fitness-proportional selection for breeding
            for (int i = 0; i < gameCount; i++) {
                SnakeGame parent1 = selectParent(topSnakes);
                SnakeGame parent2 = selectParent(topSnakes);

                // Make sure the parents are different
                while (parent1 == parent2) {
                    parent2 = selectParent(topSnakes);
                }

                nextGenerationNetworks[i] = crossOverNetworks(parent1.getBrain(), parent2.getBrain(), mutationRate);
            }

            manager.restartGames();
            displayer.updateGeneration(++generation);
        }
    }
}
