import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class SnakeGameDisplay extends JPanel {
    private final SnakeGame game;
    private final int cellSize = 20;
    private int currentGeneration = 1;

    public SnakeGameDisplay(SnakeGame game) {
        this.game = game;
        JFrame frame = new JFrame("Snake Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 800);
        frame.add(this);
        frame.setVisible(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        setBackground(new Color(52,52,52));

        g.setColor(Color.WHITE);

        g.drawRect(0,0, cellSize * game.getWidth(), cellSize * game.getHeight());

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

        g.setColor(Color.WHITE);
        g.drawString("Score: " + game.getScore(), cellSize * game.getWidth() + 20,20);
        g.drawString("Generation: " + currentGeneration, cellSize * game.getWidth() + 20,40);
    }

    public void updateGeneration(int newGen) {
        currentGeneration = newGen;
    }

    public void refresh() {
        repaint();
    }
}
