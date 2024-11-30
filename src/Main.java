public class Main {
    public static void main(String[] args) throws InterruptedException {
        // Run multiple games
        SnakeGameManager manager = new SnakeGameManager(10, 20, 20);

        SnakeGame singleGame = manager.getGames().getFirst();
        SnakeGameDisplay displayer = new SnakeGameDisplay(singleGame);

        while (manager.getGames().stream().anyMatch(game -> !game.isGameOver())) {
            manager.updateGames();
            displayer.refresh();
            Thread.sleep(100); // Simulate step time
        }

    }
}
