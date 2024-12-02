import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class SnakeGameDisplay extends JPanel {
    private SnakeGame game;
    private final int cellSize = 20;
    private int currentGeneration = 0;
    private int currentActiveGames = 0;
    private double prevTopFitness = 0.0;


    public SnakeGameDisplay(SnakeGame game) {
        this.game = game;
        JFrame frame = new JFrame("Snake Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 800);
        frame.add(this);
        frame.setVisible(true);
    }

    public void setGame(SnakeGame newGame) {
        this.game = newGame;
    }

    public void updateActiveGames(int activeGames) {
        currentActiveGames = activeGames;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        setBackground(new Color(52, 52, 52));

        // Draw game border
        g.setColor(Color.WHITE);
        g.drawRect(0, 0, cellSize * game.getWidth(), cellSize * game.getHeight());

        // Draw snake
        g.setColor(Color.GREEN);
        ArrayList<int[]> snake = game.getSnake();
        for (int[] segment : snake) {
            g.fillRect(segment[0] * cellSize, segment[1] * cellSize, cellSize, cellSize);
        }

        // Draw food
        g.setColor(Color.RED);
        int[] food = game.getFood();
        g.fillRect(food[0] * cellSize, food[1] * cellSize, cellSize, cellSize);

        // Draw sensors
        drawSensors(g);

        // Draw HUD
        g.setColor(Color.WHITE);
        g.drawString("Score: " + game.getScore(), cellSize * game.getWidth() + 20, 20);
        g.drawString("Current Generation: " + currentGeneration, cellSize * game.getWidth() + 20, 40);
        g.drawString("Active Agents: " + currentActiveGames, cellSize * game.getWidth() + 20, 60);
        g.drawString("Last Generation Top Fitness: " + prevTopFitness, cellSize * game.getWidth() + 20, 80);
    }

    /**
     * Draws the sensors for the snake's head.
     */
    private void drawSensors(Graphics g) {
        int[] head = game.getSnake().get(0);
        double[] sensors = game.getSensors();

        int[] headPosition = { head[0] * cellSize + cellSize / 2, head[1] * cellSize + cellSize / 2 };

        for (int i = 0; i < SnakeGame.DIRECTIONS.length; i++) {
            int dx = SnakeGame.DIRECTIONS[i][0];
            int dy = SnakeGame.DIRECTIONS[i][1];

            int distanceToWall = (int) (sensors[i] * cellSize);

            int endX = (headPosition[0] + dx * distanceToWall * cellSize) - (dx * (cellSize / 2)) ;
            int endY = (headPosition[1] + dy * distanceToWall * cellSize) - (dy * (cellSize / 2));

            boolean detectsFood = sensors[i + (SnakeGame.DIRECTIONS.length)] > 0;
            boolean detectsBody = sensors[i + (SnakeGame.DIRECTIONS.length) * 2] > 0;

//            boolean detectsBody = sensors[i + (SnakeGame.DIRECTIONS.length)] > 0;

//             Set color based on sensor detection
            if (detectsFood) {
                g.setColor(Color.RED);
            } else if (detectsBody) {
                g.setColor(Color.GREEN);
            } else {
                g.setColor(Color.WHITE);
            }

//            if (detectsBody) {
//                g.setColor(Color.GREEN);
//            } else {
//                g.setColor(Color.WHITE);
//            }

            g.drawLine(headPosition[0], headPosition[1], endX, endY);

            g.setColor(new Color((int) ((sensors[i]) * 255.0), (int) ((sensors[i]) * 255.0), (int) ((sensors[i]) * 255.0)));
            g.fillOval(endX - 3, endY - 3, 6, 6);

        }
    }


    public void updateGeneration(int newGen) {
        currentGeneration = newGen;
    }

    public void updatePrevTopFitness(double fitness) {
        prevTopFitness = fitness;
    }

    public void refresh() {
        repaint();
    }
}
