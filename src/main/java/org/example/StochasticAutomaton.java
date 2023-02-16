package org.example;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

public class StochasticAutomaton {
    public static void main(String[] args) {

        double[] stochasticState = {1./3, 1./3, 1./3};

        double[][] jumpMatrix = {
                {0.5, 0.25, 0.25},
                {0.5, 0, 0.5},
                {2./3, 1./3, 0},
        };

        System.out.println(Arrays.toString(multiplyMatrixByVector(jumpMatrix, stochasticState)));
    }

    private int getNewStateFromStochasticState(double[] stochasticState) {
        double prob = ThreadLocalRandom.current().nextDouble(0, 1);

        System.out.println(prob);

        double intervalBound = .0;

        int index = 0;

        for (; index < stochasticState.length; ++index) {
            intervalBound += stochasticState[index];

            if (prob <= intervalBound) break;
        }

        return index;
    }

    private static double[] multiplyMatrixByVector(double[][] matrix, double[] vector) {
        double[] resultVector = new double[vector.length];

        for (int index1 = 0; index1 < matrix.length; ++ index1) {
            for (int index2 = 0; index2 < matrix.length; ++ index2) {
                resultVector[index1] += matrix[index2][index1] * vector[index2];
            }
        }

        return resultVector;
    }
}