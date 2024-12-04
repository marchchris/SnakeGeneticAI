import java.util.*;

public class SnakeGame implements Cloneable {
    public enum Direction { UP, DOWN, LEFT, RIGHT }

    private final int width;
    private final int height;
    private LinkedList<int[]> snake; // The snake's body represented as a list of x, y coordinates
    private int[] food;             // The food's position
    private Direction direction;    // Current direction of movement
    private boolean isGameOver;     // Game over status
    private int score;              // Current score
    private NeuralNetwork brain;
    private int movesLeft;
    private int totalSteps;
    private int stepsBetweenApples;

    private double fitness;

    private double replayFitness = 0.0;
    private int replayScore = 0;

    private int snakeGeneration = 0;

    private boolean isReplay;

    private int snakeBodyParts = 2;

    private LinkedList<int[]> foodHistory;



    public static final int[][] DIRECTIONS = {
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

        this.foodHistory = new LinkedList<>();

        this.brain = brain;
        initializeGame();
    }

    public void replayGame() {
        isReplay = true;

        replayScore = score;
        replayFitness = fitness;

        snake = new LinkedList<>();

        int startX = width / 2;
        int startY = height / 2;

        // Initialize snake with length 3, moving upward by default
        snake.add(new int[] { startX, startY });

        for (int i = 0; i < snakeBodyParts; i++) {
            snake.add(new int[] { startX, startY + i });
        }



        spawnFood();
        isGameOver = false;
        score = 0;
        movesLeft = 200;
        direction = Direction.UP;
        fitness = 0.0;

        totalSteps = 0;
        stepsBetweenApples = 0;

    }

    public void restartGame() {
        initializeGame();
    }

    private void initializeGame() {
        snake = new LinkedList<>();
        foodHistory = new LinkedList<>();

        isReplay = false;

        int startX = width / 2;
        int startY = height / 2;

        // Initialize snake with length 3, moving upward by default
        snake.add(new int[] { startX, startY });

        for (int i = 0; i < snakeBodyParts; i++) {
            snake.add(new int[] { startX, startY + i });
        }



        spawnFood();
        isGameOver = false;
        score = 0;
        replayScore = 0;
        movesLeft = 200;
        direction = Direction.UP;
        fitness = 0.0;

        totalSteps = 0;
        stepsBetweenApples = 0;

    }

    private void spawnFood() {
        if (isReplay) {
            // If it's a replay, take the food from the history and remove the first element
            food = foodHistory.removeFirst();
        } else {
            Random rand = new Random();
            while (true) {
                // Generate a random food position
                food = new int[] { rand.nextInt(width), rand.nextInt(height) };

                // Ensure the food does not spawn on the snake
                boolean onSnake = snake.stream().anyMatch(segment -> segment[0] == food[0] && segment[1] == food[1]);
                if (!onSnake) {
                    break;  // Food is valid, exit the loop
                }
            }

            // Add the new food position to the history
            foodHistory.addLast(food);

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
        if (movesLeft <= 0) {
            isGameOver = true;
        }

        if (isGameOver) return;



        changeDirection(think());

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

            calculateFitness();

            return;
        }

        movesLeft--;
        totalSteps++;
        //this.fitness++;


        // Move the snake
        snake.addFirst(nextHead);
        if (nextHead[0] == food[0] && nextHead[1] == food[1]) {
            score++;
            movesLeft += 100;
            stepsBetweenApples = 0;
            spawnFood(); // Generate new food
        } else {
            snake.removeLast(); // Remove the tail
            stepsBetweenApples++;
//
//            double distanceToFood = getEuclideanDistanceToFood(nextHead);
//            if (distanceToFood <= previousDistanceToFood) {
//                this.fitness++;
//            } else {
//                this.fitness -= 1.5;
//            }
//
//            previousDistanceToFood = distanceToFood;

            //this.fitness++;
        }
    }

    /**
     * Gets the sensor inputs for the snake's head.
     * @return A list of 24 doubles representing distances to walls, body, and food in 8 directions.
     */
    public double[] getSensors() {
        double[] sensors = new double[28];
        int[] head = snake.getFirst();

        // Compute distances to walls, food, and body
        for (int i = 0; i < DIRECTIONS.length; i++) {
            int dx = DIRECTIONS[i][0];
            int dy = DIRECTIONS[i][1];

            sensors[i] = getDistanceToWall(head, dx, dy);
//            sensors[i + (DIRECTIONS.length)] = getDistanceToBody(head, dx, dy);

            sensors[i + (DIRECTIONS.length)] = getDistanceToFood(head, dx, dy);
            sensors[i + (DIRECTIONS.length) * 2] = getDistanceToBody(head, dx, dy);

//            sensors[i + (DIRECTIONS.length)] = getDistanceToBody(head, dx, dy);
        }

//        sensors[16] = (double) (food[0] - head[0]) / (double) width;
//        sensors[17] = (double) (food[1] - head[1]) / (double) height;

        switch (direction) {
            case UP -> {
                sensors[24] = 1.0; // UP
                sensors[25] = 0.0; // DOWN
                sensors[26] = 0.0; // LEFT
                sensors[27] = 0.0; // RIGHT
            }
            case DOWN -> {
                sensors[24] = 0.0; // UP
                sensors[25] = 1.0; // DOWN
                sensors[26] = 0.0; // LEFT
                sensors[27] = 0.0; // RIGHT
            }
            case LEFT -> {
                sensors[24] = 0.0; // UP
                sensors[25] = 0.0; // DOWN
                sensors[26] = 1.0; // LEFT
                sensors[27] = 0.0; // RIGHT
            }
            case RIGHT -> {
                sensors[24] = 0.0; // UP
                sensors[25] = 0.0; // DOWN
                sensors[26] = 0.0; // LEFT
                sensors[27] = 1.0; // RIGHT
            }
        }

//        sensors[20] = (double) food[0] / (double) width;
//        sensors[21] = (double) food[1] / (double) height;

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

    public void calculateFitness() {

        double computedFitness = totalSteps * (Math.pow(score, 2));
        this.fitness = computedFitness;
    }

    public double getFitness() {



        return fitness;

        //return fitness;
    }

    public double getReplayFitness() {
        return replayFitness;
    }

    public NeuralNetwork getBrain() {
        NeuralNetwork returnedBrain = new NeuralNetwork(this.brain.getStructure(), this.brain.getWeights(), this.brain.getBiases());
        return returnedBrain;



    }

    public NeuralNetwork getShallowBrain() {
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

    public LinkedList<int[]> getFoodHistory() {
        return foodHistory;
    }

    public int getSnakeGeneration() {
        return snakeGeneration;
    }

    public void setSnakeGeneration(int snakeGeneration) {
        this.snakeGeneration = snakeGeneration;

    }

    public int getReplayScore() {
        return replayScore;
    }

    @Override
    public SnakeGame clone() throws CloneNotSupportedException {
        return (SnakeGame) super.clone();
    }

    public int getMovesLeft() {
        return movesLeft;
    }
}
