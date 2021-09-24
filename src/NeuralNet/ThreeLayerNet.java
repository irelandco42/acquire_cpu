package NeuralNet;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

import static NeuralNet.MatrixUtil.apply;
import static NeuralNet.NNMath.*;

public class ThreeLayerNet {

    private final NeuronLayer layer1, layer2, layer3;
    private double[][] outputLayer1;
    private double[][] outputLayer2;
    private double[][] outputLayer3;
    private final double learningRate;

    private double lowDiff = 10000;
    private int highCorrect = 0;

    double[][] testInputs;
    double[][] testOutputs;

    double diff = 0;

    public ThreeLayerNet(NeuronLayer layer1, NeuronLayer layer2, NeuronLayer layer3) {
        this(layer1, layer2, layer3, 0.1);
    }

    public ThreeLayerNet(NeuronLayer layer1, NeuronLayer layer2, NeuronLayer layer3, double learningRate) {
        this.layer1 = layer1;
        this.layer2 = layer2;
        this.layer3 = layer3;
        this.learningRate = learningRate;
    }

    /**
     * Forward propagation
     * <p>
     * Output of neuron = 1 / (1 + e^(-(sum(weight, input)))
     *
     * @param inputs
     */
    public void think(double[][] inputs) {
        outputLayer1 = apply(matrixMultiply(inputs, layer1.weights), layer1.activationFunction); // 4x4
        outputLayer2 = apply(matrixMultiply(outputLayer1, layer2.weights), layer2.activationFunction); // 4x1
        outputLayer3 = apply(matrixMultiply(outputLayer2, layer3.weights), layer3.activationFunction); // 4x1
    }

    public void train(double[][] inputs, double[][] outputs, int numberOfTrainingIterations) {
        for (int i = 0; i < numberOfTrainingIterations; ++i) {
            // pass the training set through the network
            think(inputs); // 4x3

            // adjust weights by error * input * output * (1 - output)

            // calculate the error for layer 3
            // (the difference between the desired output and predicted output for each of the training inputs)
            double[][] errorLayer3 = matrixSubtract(outputs, outputLayer3); // 4x1
            double[][] deltaLayer3 = scalarMultiply(errorLayer3, apply(outputLayer3, layer3.activationFunctionDerivative)); // 4x1

            // calculate the error for layer 2
            // (the difference between the desired output and predicted output for each of the training inputs)
            double[][] errorLayer2 = matrixMultiply(deltaLayer3, matrixTranspose(layer3.weights)); // 4x1
            double[][] deltaLayer2 = scalarMultiply(errorLayer2, apply(outputLayer2, layer2.activationFunctionDerivative)); // 4x1

            // calculate the error for layer 1
            // (by looking at the weights in layer 1, we can determine by how much layer 1 contributed to the error in layer 2)

            double[][] errorLayer1 = matrixMultiply(deltaLayer2, matrixTranspose(layer2.weights)); // 4x4
            double[][] deltaLayer1 = scalarMultiply(errorLayer1, apply(outputLayer1, layer1.activationFunctionDerivative)); // 4x4

            // Calculate how much to adjust the weights by
            // Since we’re dealing with matrices, we handle the division by multiplying the delta output sum with the inputs' transpose!

            double[][] adjustmentLayer1 = matrixMultiply(matrixTranspose(inputs), deltaLayer1); // 4x4
            double[][] adjustmentLayer2 = matrixMultiply(matrixTranspose(outputLayer1), deltaLayer2); // 4x1
            double[][] adjustmentLayer3 = matrixMultiply(matrixTranspose(outputLayer2), deltaLayer3); // 4x1

            adjustmentLayer1 = apply(adjustmentLayer1, (x) -> learningRate * x);
            adjustmentLayer2 = apply(adjustmentLayer2, (x) -> learningRate * x);
            adjustmentLayer3 = apply(adjustmentLayer3, (x) -> learningRate * x);

            // adjust the weights
            this.layer1.adjustWeights(adjustmentLayer1);
            this.layer2.adjustWeights(adjustmentLayer2);
            this.layer3.adjustWeights(adjustmentLayer3);

            // if you only had one layer
            // synaptic_weights += dot(training_set_inputs.T, (training_set_outputs - output) * output * (1 - output))
            // double[][] errorLayer1 = NNMath.matrixSubtract(outputs, outputLayer1);
            // double[][] deltaLayer1 = NNMath.matrixMultiply(errorLayer1, MatrixUtil.apply(outputLayer1, NNMath::sigmoidDerivative));
            // double[][] adjustmentLayer1 = NNMath.matrixMultiply(NNMath.matrixTranspose(inputs), deltaLayer1);

            if(i % 100 == 0){
                diff = 0;
                int yes = 0;

                for (int j = 0; j < testInputs.length; j++) {
                    think(new double[][] {testInputs[j]});
                    String string = "Prediction on data " + getOutput()[0][0];
                    if (testOutputs[j][0] == 1) {
                        string = string + " Y";
                    }
                    System.out.println(string);
                    diff += Math.abs(testOutputs[j][0] - getOutput()[0][0]);
                    if (Math.abs(testOutputs[j][0] - getOutput()[0][0]) < 0.5) yes++;
                }

                if (diff < lowDiff) {
                    lowDiff = diff;
                }
                if (highCorrect < yes) {
                    highCorrect = yes;
                }

                System.out.println("Correct " + yes + "/" + testInputs.length);
                System.out.println("HighCorrect " + highCorrect + "/" + testInputs.length);
                System.out.println("Total difference "  + diff);
                System.out.println("Low difference "  + lowDiff);
                System.out.println();
                System.out.println(" Training iteration " + i + " of " + numberOfTrainingIterations);
            }
            //System.out.println(this);

        }
    }

    public void exportNet() {
        PrintWriter out = null;

        try {
            out = new PrintWriter("Connect4Net.txt");
        } catch (FileNotFoundException e) {

        }

        if (out != null) {
            String string = "";
            for (int i = 0; i < layer1.weights.length; ++i) {
                for (int j = 0; j < layer1.weights[0].length; ++j) {
                    string = string + layer1.weights[i][j] + " ";
                }
            }
            out.println(string);

            string = "";
            for (int i = 0; i < layer2.weights.length; ++i) {
                for (int j = 0; j < layer2.weights[0].length; ++j) {
                    string = string + layer2.weights[i][j] + " ";
                }
            }
            out.println(string);

            string = "";
            for (int i = 0; i < layer3.weights.length; ++i) {
                for (int j = 0; j < layer3.weights[0].length; ++j) {
                    string = string + layer3.weights[i][j] + " ";
                }
            }
            out.println(string);
        }

        out.close();
    }

    public double[][] getOutput() {
        return outputLayer3;
    }

    public void applyTests(double[][] testInputs, double[][] testOutputs) {
        this.testInputs = testInputs;
        this.testOutputs = testOutputs;
    }

    @Override
    public String toString() {
        String result = "Layer 1\n";
        result += layer1.toString();
        result += "Layer 2\n";
        result += layer2.toString();

        if (outputLayer1 != null) {
            result += "Layer 1 output\n";
            result += MatrixUtil.matrixToString(outputLayer1);
        }

        if (outputLayer2 != null) {
            result += "Layer 2 output\n";
            result += MatrixUtil.matrixToString(outputLayer2);
        }

        return result;
    }
}
