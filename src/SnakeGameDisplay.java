import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class SnakeGameDisplay extends JPanel {
    private SnakeGame game;
    private final int cellSize = 20;
    private int currentGeneration = 0;
    private int currentActiveGames = 0;
    private double prevTopFitness = 0.0;

    String[] outputLabels = {"UP", "DOWN", "LEFT", "RIGHT"};

    public Color red = new Color(255, 0, 0, 128);
    public Color green = new Color(0, 255, 0, 128);
    public Color white = new Color(255, 255, 255, 128);

    public SnakeGameDisplay(SnakeGame game) {
        this.game = game;
        JFrame frame = new JFrame("Snake Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 1000);
        frame.add(this);
        frame.setVisible(true);
    }

    public void setGame(SnakeGame newGame) {
        this.game = newGame;
        this.currentGeneration = newGame.getSnakeGeneration();
        this.prevTopFitness = newGame.getFitness();
    }

    public void updateActiveGames(int activeGames) {
        currentActiveGames = activeGames;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        setBackground(new Color(30, 30, 30));

        // Draw game border
        g.setColor(Color.WHITE);
        g.drawRect(10, 10, cellSize * game.getWidth(), cellSize * game.getHeight());

        // Draw snake
        g.setColor(Color.GREEN);
        ArrayList<int[]> snake = game.getSnake();
        for (int[] segment : snake) {
            g.fillRect(segment[0] * cellSize + 10, segment[1] * cellSize + 10, cellSize, cellSize);
        }

        // Draw food
        g.setColor(Color.RED);
        int[] food = game.getFood();
        g.fillRect(food[0] * cellSize + 10, food[1] * cellSize + 10, cellSize, cellSize);

        // Draw sensors
        drawSensors(g);

        // Draw HUD
        g.setColor(Color.WHITE);
        g.drawString("Current Score: " + game.getScore(), cellSize * game.getWidth() + 20, 20);
        g.drawString("Moves Left: " + game.getMovesLeft(), cellSize * game.getWidth() + 20, 40);

        g.drawString("Current Displayed Generation: " + game.getSnakeGeneration(), cellSize * game.getWidth() + 20, 60);
        g.drawString("Current Generation Top Score: " + game.getReplayScore(), cellSize * game.getWidth() + 20, 80);
        g.drawString("Generations Top Fitness: " + game.getReplayFitness(), cellSize * game.getWidth() + 20, 100);
        g.drawString("Active Agents: " + currentActiveGames, cellSize * game.getWidth() + 20, 120);

        drawNeuralNetwork(g);
    }

    private double[] applySigmoid(double[] arr) {
        double[] activatedOutput = new double[arr.length];
        for (int i = 0; i < arr.length; i++) {
            activatedOutput[i] = 1 / (1 + Math.exp(-arr[i])); // Sigmoid
        }
        return activatedOutput;
    }

    private void drawNeuralNetwork(Graphics g) {
        int neuronRadius = 8;


        int xStart = cellSize * game.getWidth() + 250;
        int yStart = 50;

        int xWidth = getWidth() - xStart;
        int yHeight = (getHeight() - 50) - yStart;





        NeuralNetwork nn = game.getShallowBrain();
        int[] structure = nn.getStructure();
        double[][][] weights = nn.getWeights();
        double[][] activations = nn.getLayerActivations();



        int ySpacing = 15;
        int xSpacing = xWidth / (structure.length + 1);

        int[][] neuronPosition = new int[structure.length][];


        for (int i = 0; i < structure.length; i++) {
            int neuronCount = structure[i];

            neuronPosition[i] = new int[neuronCount];
            for (int j = 0; j < neuronCount; j++) {
                int layerY = yStart + (yHeight - (neuronCount * (neuronRadius * 2 + ySpacing))) / 2 + j * (neuronRadius * 2 + ySpacing);
                neuronPosition[i][j] = layerY;
            }
        }

        for (int i = 0; i < weights.length; i++) {
            double[][] weightMatrix = weights[i];

            for (int j = 0; j < weightMatrix.length; j++) {
                for (int k = 0; k < weightMatrix[j].length; k++) {
                    int startXPos = xStart + (i + 1) * xSpacing;
                    int startYPos = neuronPosition[i][j];
                    int endXPos = xStart + (i + 2) * xSpacing;
                    int endYPos = neuronPosition[i + 1][k];

                    // Get the weight value
                    double weight = weightMatrix[j][k];

                    // Set the connection color based on the weight value
                    Color connectionColor;
                    if (weight > 0) {
                        int green = (int) Math.min(weight * 255.0, 255); // Increase green for positive weights
                        connectionColor = new Color(0, green, 0); // Green for positive
                    } else if (weight < 0) {
                        int red = (int) Math.min(-weight * 255.0, 255); // Increase red for negative weights
                        connectionColor = new Color(red, 0, 0); // Red for negative
                    } else {
                        connectionColor = new Color(128, 128, 128); // Gray for near-zero
                    }

                    // Set the line color and draw the connection
                    g.setColor(connectionColor);

                    g.drawLine(startXPos, startYPos, endXPos, endYPos);
                }
            }
        }

        int outputLayerIndex = structure.length - 1;
        int highestActivationIndex = -1;
        double highestActivationValue = Double.NEGATIVE_INFINITY;

        for (int j = 0; j < structure[outputLayerIndex]; j++) {
            if (activations[outputLayerIndex][j] > highestActivationValue) {
                highestActivationValue = activations[outputLayerIndex][j];
                highestActivationIndex = j;
            }
        }

        // Prepare Graphics2D for thicker border
        Graphics2D g2d = (Graphics2D) g;
        Stroke originalStroke = g2d.getStroke();
        g2d.setStroke(new BasicStroke(3)); // Set a thicker stroke (3 pixels)

        for (int i = 0; i < structure.length; i++) {
            int neuronCount = structure[i];

            int layerX = xStart + (i + 1) * xSpacing;




            for (int j = 0; j < neuronCount; j++) {
                int layerY = yStart + (yHeight - (neuronCount * (neuronRadius * 2 + ySpacing))) / 2 + j * (neuronRadius * 2 + ySpacing);

                Color activationColor = Color.WHITE;


                double value = activations[i][j];
                activationColor = new Color((int) (value * 255.0), (int) (value * 255.0), (int) (value * 255.0));

                g.setColor(activationColor);
                g.fillOval(layerX - neuronRadius, layerY - neuronRadius, neuronRadius * 2, neuronRadius * 2);

                // Highlight the output neuron with the highest activation value
                if (i == outputLayerIndex && j == highestActivationIndex) {
                    g.setColor(Color.CYAN); // Set the border color to blue
                    g2d.drawOval(layerX - neuronRadius - 3, layerY - neuronRadius - 3, neuronRadius * 2 + 6, neuronRadius * 2 + 6);
                }

                // Label output neurons
                if (i == structure.length - 1) { // Check if this is the output layer
                    g.setColor(Color.WHITE);
                    if (j < outputLabels.length) { // Ensure we don't exceed the number of labels
                        g.drawString(outputLabels[j], layerX + neuronRadius + 5, layerY + 5);
                    }
                }
            }
        }

    }


    /**
     * Draws the sensors for the snake's head.
     */
    private void drawSensors(Graphics g) {
        int[] head = game.getSnake().get(0);
        double[] sensors = game.getSensors();

        int[] headPosition = { (head[0] * cellSize + cellSize / 2) + 10, (head[1] * cellSize + cellSize / 2) + 10 };

        for (int i = 0; i < SnakeGame.DIRECTIONS.length; i++) {
            int dx = SnakeGame.DIRECTIONS[i][0];
            int dy = SnakeGame.DIRECTIONS[i][1];

            int distanceToWall = (int) (sensors[i] * cellSize);

            int endX = (headPosition[0] + dx * distanceToWall * cellSize) - (dx * (cellSize / 2)) ;
            int endY = (headPosition[1] + dy * distanceToWall * cellSize) - (dy * (cellSize / 2));

            boolean detectsFood = sensors[i + (SnakeGame.DIRECTIONS.length)] > 0;
            boolean detectsBody = sensors[i + (SnakeGame.DIRECTIONS.length) * 2] > 0;

            if (detectsFood) {
                g.setColor(red);
            } else if (detectsBody) {
                g.setColor(green);
            } else {
                g.setColor(white);
            }

            g.drawLine(headPosition[0], headPosition[1], endX, endY);

            g.setColor(new Color((int) ((sensors[i]) * 255.0), (int) ((sensors[i]) * 255.0), (int) ((sensors[i]) * 255.0)));
            g.fillOval(endX - 3, endY - 3, 6, 6);

        }
    }


    public void refresh() {
        repaint();
    }
}
