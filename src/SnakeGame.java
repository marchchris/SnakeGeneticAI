import java.util.ArrayList;
import java.util.Arrays;
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
    private NeuralNetwork brain;

    private int fitness;

    private static final int[][] DIRECTIONS = {
            { 0, -1 },  // N
            { 1, 0 },   // E
            { 0, 1 },   // S
            { -1, 0 },  // W
            { 1, -1 },  // NE
            { 1, 1 },   // SE
            { -1, 1 },  // SW
            { -1, -1 }  // NW
    };


    public SnakeGame(int width, int height, NeuralNetwork brain) {
        this.width = width;
        this.height = height;
        this.fitness = 0;

        this.brain = brain;
        initializeGame();
    }

    public void restartGame() {
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
        spawnFood();
        isGameOver = false;
        score = 0;

        direction = Direction.UP;
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

    private Direction think() {
        double[] inputs = getSensors();

        double[] output = brain.forward(inputs);

        //System.out.println(Arrays.toString(output));
        // 0 UP
        // 1 DOWN
        // 2 LEFT
        // 3 RIGHT

        int maxIndex = 0;
        double maxValue = output[0];


        for (int i = 1; i < output.length; i++) {
            if (output[i] > maxValue) {
                maxValue = output[i];
                maxIndex = i;
            }
        }


        return switch (maxIndex) {
            // UP
            case 0 -> Direction.UP;

            // Down
            case 1 -> Direction.DOWN;

            // LEFT
            case 2 -> Direction.LEFT;

            // RIGHT
            case 3 -> Direction.RIGHT;
            default -> throw new IllegalStateException("Unexpected value: " + maxIndex);
        };
    }

    public void update() {
        if (isGameOver) return;

        Direction dir = think();

        changeDirection(dir);

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

        fitness++;

        // Move the snake
        snake.addFirst(nextHead);
        if (nextHead[0] == food[0] && nextHead[1] == food[1]) {
            score++;
            fitness += 100;
            spawnFood(); // Generate new food
        } else {
            snake.removeLast(); // Remove the tail
        }
    }

    /**
     * Gets the sensor inputs for the snake's head.
     * @return A list of 24 doubles representing distances to walls, body, and food in 8 directions.
     */
    public double[] getSensors() {
        double[] sensors = new double[24];
        int[] head = snake.getFirst();

        for (int i = 0; i < DIRECTIONS.length; i++) {
            int dx = DIRECTIONS[i][0];
            int dy = DIRECTIONS[i][1];

            // Wall distance
            sensors[i * 3] = getDistanceToWall(head, dx, dy);

            // Body distance
            sensors[i * 3 + 1] = getDistanceToBody(head, dx, dy);

            // Food distance
            sensors[i * 3 + 2] = getDistanceToFood(head, dx, dy);
        }

        return sensors;
    }

    private double getDistanceToWall(int[] head, int dx, int dy) {
        int x = head[0];
        int y = head[1];
        int distance = 0;

        while (x >= 0 && x < width && y >= 0 && y < height) {
            x += dx;
            y += dy;
            distance++;
        }

        return (double) distance / (double) width;
    }

    private double getDistanceToBody(int[] head, int dx, int dy) {
        int x = head[0];
        int y = head[1];
        int distance = 0;

        while (x >= 0 && x < width && y >= 0 && y < height) {
            x += dx;
            y += dy;
            distance++;

            for (int[] segment : snake) {
                if (segment[0] == x && segment[1] == y) {
                    return 1.0;
                }
            }
        }

        return 0.0;
    }

    private double getDistanceToFood(int[] head, int dx, int dy) {
        int x = head[0];
        int y = head[1];
        int distance = 0;

        while (x >= 0 && x < width && y >= 0 && y < height) {
            x += dx;
            y += dy;
            distance++;

            if (x == food[0] && y == food[1]) {
                return 1.0;
            }
        }

        return 0.0;
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    public int getFitness() {
        return fitness;
    }

    public NeuralNetwork getBrain() {
        return brain;
    }

    public void setBrain(NeuralNetwork brain) {
        this.brain = brain;
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
