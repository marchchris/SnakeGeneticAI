import java.util.Random;

public class NeuralNetwork {

    private final int[] layers;              // Structure of the network
    private double[][][] weights;           // Weights for each layer
    private double[][] biases;              // Biases for each layer
    private final Random random = new Random();

    // Constructor to initialize the neural network structure
    public NeuralNetwork(int[] layers) {
        this.layers = layers;
        initializeNetwork();
    }

    // Initialize weights and biases with random values
    private void initializeNetwork() {
        weights = new double[layers.length - 1][][];
        biases = new double[layers.length - 1][];

        for (int i = 0; i < layers.length - 1; i++) {
            // Initialize weights with dimensions [current layer neurons][next layer neurons]
            weights[i] = new double[layers[i]][layers[i + 1]];
            biases[i] = new double[layers[i + 1]];

            for (int j = 0; j < layers[i]; j++) {
                for (int k = 0; k < layers[i + 1]; k++) {
                    weights[i][j][k] = random.nextGaussian(); // Random normal distribution
                }
            }

            for (int j = 0; j < layers[i + 1]; j++) {
                biases[i][j] = random.nextGaussian(); // Random normal distribution
            }
        }
    }

    // Perform a forward pass through the network
    public double[] forward(double[] input) {
        double[] currentInput = input;

        for (int i = 0; i < weights.length; i++) {
            double[][] weightMatrix = weights[i];
            double[] layerOutput = MatrixOperations.multiply(new double[][] { currentInput }, weightMatrix)[0];
            layerOutput = addBias(layerOutput, biases[i]);
            currentInput = applyActivation(layerOutput);
        }

        return currentInput;
    }

    // Apply an activation function (ReLU)
    private double[] applyActivation(double[] layerOutput) {
        double[] activatedOutput = new double[layerOutput.length];
        for (int i = 0; i < layerOutput.length; i++) {
            activatedOutput[i] = Math.max(0, layerOutput[i]); // ReLU
        }
        return activatedOutput;
    }

    // Add biases to the layer output
    private double[] addBias(double[] layerOutput, double[] bias) {
        double[] outputWithBias = new double[layerOutput.length];
        for (int i = 0; i < layerOutput.length; i++) {
            outputWithBias[i] = layerOutput[i] + bias[i];
        }
        return outputWithBias;
    }


}
