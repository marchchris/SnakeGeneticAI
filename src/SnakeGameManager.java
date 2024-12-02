import java.util.ArrayList;
import java.util.List;

public class SnakeGameManager {
    private final List<SnakeGame> games;
    private int currentGeneration = 0;

    public SnakeGameManager(int gameCount, int width, int height) {
        NeuralNetwork[] brains = new NeuralNetwork[gameCount];

        for (int i = 0; i < gameCount; i++) {
            brains[i] = new NeuralNetwork(new int[] {28,16,16,4});
        }

        games = new ArrayList<>();
        for (int i = 0; i < gameCount; i++) {
            games.add(new SnakeGame(width, height, brains[i]));
        }

    }

    public void updateGames() {
        for (int i = 0; i < games.size(); i++) {
            if (!games.get(i).isGameOver()) {
                games.get(i).update();
            }
        }
    }

    public int getActiveGameCount() {
        int activeGameCount = 0;
        for (SnakeGame game : games) {
            if (!game.isGameOver()) {
                activeGameCount++;
            }
        }
        return activeGameCount;
    }


    public void restartGames() {
        currentGeneration++;


        for (SnakeGame game : games) {
            game.setSnakeGeneration(currentGeneration);
            game.restartGame();
        }


    }

    public void setNewBrains(NeuralNetwork[] brains) {
        for (int i = 0; i < games.size(); i++) {
            games.get(i).setBrain(brains[i]);
        }
    }

    public List<SnakeGame> getGames() {
        return games;
    }

    public int getCurrentGeneration() {
        return currentGeneration;
    }
}
