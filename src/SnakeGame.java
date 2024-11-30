import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

public class SnakeGame {
    public enum Direction { UP, DOWN, LEFT, RIGHT }

    private final int width;
    private final int height;
    private LinkedList<int[]> snake; // The snake's body represented as a list of x, y coordinates
    private int[] food;             // The food's position
    private Direction direction;    // Current direction of movement
    private boolean isGameOver;     // Game over status
    private int score;              // Current score

    public SnakeGame(int width, int height) {
        this.width = width;
        this.height = height;
        initializeGame();
    }

    private void initializeGame() {
        snake = new LinkedList<>();
        int startX = width / 2;
        int startY = height / 2;

        // Initialize snake with length 3, moving upward by default
        snake.add(new int[] { startX, startY });
        snake.add(new int[] { startX, startY + 1 });
        snake.add(new int[] { startX, startY + 2 });
        direction = Direction.RIGHT;
        spawnFood();
        isGameOver = false;
        score = 0;
    }

    private void spawnFood() {
        Random rand = new Random();
        while (true) {
            food = new int[] { rand.nextInt(width), rand.nextInt(height) };
            boolean onSnake = snake.stream().anyMatch(segment -> segment[0] == food[0] && segment[1] == food[1]);
            if (!onSnake) break;
        }
    }

    public void changeDirection(Direction newDirection) {
        // Prevent reversing direction
        if ((direction == Direction.UP && newDirection != Direction.DOWN) ||
                (direction == Direction.DOWN && newDirection != Direction.UP) ||
                (direction == Direction.LEFT && newDirection != Direction.RIGHT) ||
                (direction == Direction.RIGHT && newDirection != Direction.LEFT)) {
            direction = newDirection;
        }
    }

    public void update() {
        if (isGameOver) return;


        // Compute next head position
        int[] currentHead = snake.getFirst();
        int[] nextHead = new int[] { currentHead[0], currentHead[1] };
        switch (direction) {
            case UP -> nextHead[1]--;
            case DOWN -> nextHead[1]++;
            case LEFT -> nextHead[0]--;
            case RIGHT -> nextHead[0]++;
        }

        // Check for collisions
        if (nextHead[0] < 0 || nextHead[0] >= width || nextHead[1] < 0 || nextHead[1] >= height ||
                snake.stream().anyMatch(segment -> segment[0] == nextHead[0] && segment[1] == nextHead[1])) {
            isGameOver = true;
            return;
        }

        // Move the snake
        snake.addFirst(nextHead);
        if (nextHead[0] == food[0] && nextHead[1] == food[1]) {
            score++;
            spawnFood(); // Generate new food
        } else {
            snake.removeLast(); // Remove the tail
        }
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    public int getScore() {
        return score;
    }

    public ArrayList<int[]> getSnake() {
        return new ArrayList<>(snake);
    }

    public int[] getFood() {
        return food;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
