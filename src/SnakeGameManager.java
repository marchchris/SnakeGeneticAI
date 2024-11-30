import java.util.ArrayList;
import java.util.List;

public class SnakeGameManager {
    private final List<SnakeGame> games;

    public SnakeGameManager(int gameCount, int width, int height) {
        NeuralNetwork[] brains = new NeuralNetwork[gameCount];

        for (int i = 0; i < gameCount; i++) {
            brains[i] = new NeuralNetwork(new int[] {24,16,12,4});
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

    public void restartGames() {
        for (int i = 0; i < games.size(); i++) {
            games.get(i).restartGame();
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
}
