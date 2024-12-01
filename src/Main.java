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

    public static SnakeGame selectParent(List<SnakeGame> topSnakes) {
        // Calculate the total fitness of all top snakes
        double totalFitness = topSnakes.stream().mapToDouble(SnakeGame::getFitness).sum();

        // Generate a random number between 0 and the total fitness
        Random rand = new Random();
        double randomValue = rand.nextDouble() * totalFitness;

        // Traverse the list and pick the snake whose cumulative fitness surpasses the random value
        double cumulativeFitness = 0.0;
        for (SnakeGame snake : topSnakes) {
            cumulativeFitness += snake.getFitness();
            if (cumulativeFitness >= randomValue) {
                return snake;  // Return the selected parent
            }
        }

        // In case of rounding errors, return the last snake
        return topSnakes.get(topSnakes.size() - 1);
    }


    public static NeuralNetwork crossOverNetworks(NeuralNetwork network1, NeuralNetwork network2, double mutationRate) {
        // Get weights and biases from both parent networks
        double[][][] parent1Weights = network1.getWeights();
        double[][][] parent2Weights = network2.getWeights();
        double[][] parent1Biases = network1.getBiases();
        double[][] parent2Biases = network2.getBiases();

        // Ensure both parents have the same structure
        if (parent1Weights.length != parent2Weights.length || parent1Biases.length != parent2Biases.length) {
            throw new IllegalArgumentException("Parent networks must have the same structure.");
        }

        // Create new arrays for child weights and biases
        double[][][] newWeights = new double[parent1Weights.length][][];
        double[][] newBiases = new double[parent1Biases.length][];

        // Arithmetic crossover with alpha = 0.5
        double alpha = 0.5;

        // Combine weights
        for (int i = 0; i < parent1Weights.length; i++) {
            int rows = parent1Weights[i].length;
            int cols = parent1Weights[i][0].length;

            newWeights[i] = new double[rows][cols];

            for (int j = 0; j < rows; j++) {
                for (int k = 0; k < cols; k++) {
                    // Combine weights using arithmetic crossover
                    newWeights[i][j][k] = alpha * parent1Weights[i][j][k] + (1 - alpha) * parent2Weights[i][j][k];


                    // Apply mutation with the given probability
                    if (Math.random() <= mutationRate) {
                        newWeights[i][j][k] = mutateWeight(newWeights[i][j][k]);
                    }
                }
            }
        }

        // Combine biases
        for (int i = 0; i < parent1Biases.length; i++) {
            int length = parent1Biases[i].length;

            newBiases[i] = new double[length];

            for (int j = 0; j < length; j++) {
                // Combine biases using arithmetic crossover
                newBiases[i][j] = alpha * parent1Biases[i][j] + (1 - alpha) * parent2Biases[i][j];

                // Apply mutation with the given probability
                if (Math.random() <= mutationRate) {
                    newBiases[i][j] = mutateWeight(newBiases[i][j]);
                }
            }
        }

        // Create a new child network and set the weights and biases
        NeuralNetwork childNetwork = new NeuralNetwork(network1.getStructure());
        childNetwork.setWeights(newWeights);
        childNetwork.setBiases(newBiases);

        return childNetwork;
    }



    public static void main(String[] args) throws InterruptedException {

        int gameCount = 500;
        double mutationRate = 0.05;
        int generation = 0;

        SnakeGameManager manager = new SnakeGameManager(gameCount, 20, 20);
        SnakeGame singleGame = manager.getGames().getFirst();
        SnakeGameDisplay displayer = new SnakeGameDisplay(singleGame);

        while (true) {
            while (manager.getGames().stream().anyMatch(game -> !game.isGameOver())) {
                // Check if the current displayed game is over
                if (singleGame.isGameOver()) {
                    // Find a new game that is not over
                    for (SnakeGame game : manager.getGames()) {
                        if (!game.isGameOver()) {
                            singleGame = game;
                            displayer.setGame(singleGame); // Update the display with the new game
                            break;
                        }
                    }
                }

                // Update games and refresh the display
                manager.updateGames();
                int activeGames = manager.getActiveGameCount();
                displayer.updateActiveGames(activeGames);
                displayer.refresh();

                Thread.sleep(100); // Simulate step time
            }

            List<SnakeGame> allSnakes = manager.getGames();
            allSnakes.sort(Comparator.comparingDouble(SnakeGame::getFitness));

            // Get the top 16 snakes
            List<SnakeGame> topSnakes = allSnakes.subList(Math.max(allSnakes.size() - 5, 0), allSnakes.size());
            //List<SnakeGame> topSnakes = allSnakes;

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



            double topFit = 0.0;


            for (SnakeGame snake : allSnakes) {
                double fitness = snake.getFitness();
                if (fitness > topFit) {
                    topFit = fitness;
                }
            }

            manager.setNewBrains(nextGenerationNetworks);
            manager.restartGames();
            displayer.updatePrevTopFitness(topFit);
            displayer.updateGeneration(++generation);
        }
    }
}
