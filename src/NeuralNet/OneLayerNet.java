package NeuralNet;

import static NeuralNet.MatrixUtil.apply;
import static NeuralNet.NNMath.*;

public class OneLayerNet {

    private final NeuronLayer layer1;

    private double[][] outputLayer1;

    public OneLayerNet(NeuronLayer layer1) {
        this.layer1 = layer1;
    }

    public void think(double inputs[][]) {
        outputLayer1 = apply(matrixMultiply(inputs, layer1.weights), layer1.activationFunction);
    }

    public void train(double[][] inputs, double[][] outputs, int numberOfTrainingIterations) {
        for (int i = 0; i < numberOfTrainingIterations; i++) {
            think(inputs);

            double[][] errorLayer1 = matrixSubtract(outputs, outputLayer1);

            double[][] deltaLayer1 = scalarMultiply(errorLayer1, apply(outputLayer1, layer1.activationFunctionDerivative));

            double[][] adjustmentLayer1 = matrixMultiply(matrixTranspose(inputs), deltaLayer1);

            this.layer1.adjustWeights(adjustmentLayer1);
        }
    }

    public double[][] getOutput() {
        return outputLayer1;
    }
}
