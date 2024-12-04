import java.util.Arrays;
import java.util.Random;

public class NeuralNetwork {

    private int[] layers;              // Structure of the network
    private double[][][] weights;      // Weights for each layer
    private double[][] biases;         // Biases for each layer
    private double[][] layerActivations; // Activated values for each layer

    private final Random random = new Random();

    // Constructor to initialize the neural network structure
    public NeuralNetwork(int[] layers) {
        this.layers = layers;
        layerActivations = new double[layers.length][];

        initializeNetwork();
    }

    public NeuralNetwork(int[] layers, double[][][] weights, double[][] biases) {
        this.layers = layers;
        this.weights = weights;
        this.biases = biases;
        layerActivations = new double[layers.length][];

        for (int i = 0; i < layers.length; i++) {
            layerActivations[i] = new double[layers[i]]; // Ensure each layer's activations are initialized
        }
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
                    weights[i][j][k] = Math.random() * 2.0 - 1.0;
                }
            }

            for (int j = 0; j < layers[i + 1]; j++) {
                biases[i][j] = Math.random() * 2.0 - 1.0;
            }
        }

        for (int i = 0; i < layers.length; i++) {
            layerActivations[i] = new double[layers[i]]; // Ensure each layer's activations are initialized
        }
    }

    // Perform a forward pass through the network
    public double[] forward(double[] input) {
        double[] currentInput = input;
        layerActivations[0] = input; // Store input layer activations

        for (int i = 0; i < weights.length; i++) {
            double[][] weightMatrix = weights[i];
            double[] layerOutput = MatrixOperations.multiply(new double[][] { currentInput }, weightMatrix)[0];
            layerOutput = addBias(layerOutput, biases[i]);

//            // Apply activation function
            if (i == weights.length - 1) {
                layerActivations[i + 1] = applySigmoid(layerOutput);
                currentInput = applySigmoid(layerOutput); // Sigmoid for output layer
            } else {
                currentInput = applyReLU(layerOutput); // ReLU for hidden layers
                layerActivations[i + 1] = applySigmoid(layerOutput);

            }

//            currentInput = applySigmoid(layerOutput); // Sigmoid for output layer

            //layerActivations[i + 1] = currentInput; // Store activated output for the current layer
        }



        return currentInput;
    }


    // Apply ReLU activation function
    private double[] applyReLU(double[] layerOutput) {
        double[] activatedOutput = new double[layerOutput.length];
        for (int i = 0; i < layerOutput.length; i++) {
            activatedOutput[i] = Math.max(0, layerOutput[i]); // ReLU
        }
        return activatedOutput;
    }

    // Apply Sigmoid activation function
    private double[] applySigmoid(double[] layerOutput) {
        double[] activatedOutput = new double[layerOutput.length];
        for (int i = 0; i < layerOutput.length; i++) {
            activatedOutput[i] = 1 / (1 + Math.exp(-layerOutput[i])); // Sigmoid
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

    // Getters and setters
    public double[][][] getWeights() {
        return weights.clone();
    }

    public double[][] getBiases() {
        return biases.clone();
    }

    public void setBiases(double[][] biases) {
        this.biases = biases.clone();
    }

    public void setWeights(double[][][] weights) {
        this.weights = weights.clone();
    }

    public int[] getStructure() {
        return layers.clone();
    }

    // New method to get activations
    public double[][] getLayerActivations() {


        return layerActivations.clone();
    }
}
