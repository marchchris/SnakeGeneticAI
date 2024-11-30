import java.util.ArrayList;
import java.util.List;

public class SnakeGameManager {
    private final List<SnakeGame> games;

    public SnakeGameManager(int gameCount, int width, int height) {
        games = new ArrayList<>();
        for (int i = 0; i < gameCount; i++) {
            games.add(new SnakeGame(width, height));
        }
    }

    public void updateGames() {
        for (int i = 0; i < games.size(); i++) {
            if (!games.get(i).isGameOver()) {
                games.get(i).update();
            }
        }
    }

    public List<SnakeGame> getGames() {
        return games;
    }
}
