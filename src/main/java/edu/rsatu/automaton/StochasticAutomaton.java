package edu.rsatu.automaton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class StochasticAutomaton {
    private final List<List<Double>> empiricalStochasticStates;

    private final List<List<Double>> theoreticalStochasticStates;

    private final List<Integer> states;

    public StochasticAutomaton(double[][] jumpMatrix, double[] firstStochasticState, int iterationNum) {
        if (jumpMatrix.length == 0) throw new IllegalArgumentException();
        if (jumpMatrix.length != firstStochasticState.length) throw new IllegalArgumentException();
        if (jumpMatrix[0].length != jumpMatrix.length) throw new IllegalArgumentException();

        empiricalStochasticStates = new ArrayList<>();
        theoreticalStochasticStates = new ArrayList<>();
        states = new ArrayList<>();

        modeling(jumpMatrix, firstStochasticState, iterationNum);
    }

    public List<List<Double>> getEmpiricalStochasticStates() {
        return empiricalStochasticStates;
    }

    public List<List<Double>> getTheoreticalStochasticStates() {
        return theoreticalStochasticStates;
    }

    public List<Integer> getStates() {
        return states;
    }

    private void modeling(double[][] jumpMatrix, double[] firstStochasticState, int iterationNum) {
        int currentState = getNewState(firstStochasticState);

        System.out.println("Начальное состояние: " + currentState);

        double[] curTheoreticalStochasticState = firstStochasticState.clone(), curEmpiricalStochasticState;

        int[] stateJumpNumbers = new int[jumpMatrix.length];

        for (int step = 1; step <= iterationNum; ++step) {
            currentState = getNewState(jumpMatrix[currentState]);

            ++stateJumpNumbers[currentState];

            curEmpiricalStochasticState = getNewEmpiricalStochasticState(stateJumpNumbers, step);
            curTheoreticalStochasticState = getNewTheoreticalStochasticState(jumpMatrix, curTheoreticalStochasticState);

            states.add(currentState);
            empiricalStochasticStates.add(Arrays.stream(curEmpiricalStochasticState).boxed().collect(Collectors.toList()));
            theoreticalStochasticStates.add(Arrays.stream(curTheoreticalStochasticState).boxed().collect(Collectors.toList()));
        }
    }

    private boolean isStochasticStatesEqual(List<Double> stochasticState1, List<Double> stochasticState2) {
//        System.out.println("Empirical: " + stochasticState1 + ", theoretical: " + stochasticState2);

        for (int index = 0; index < stochasticState1.size(); ++index) {
            if (Math.abs(stochasticState1.get(index) - stochasticState2.get(index)) > 0.01) {
                return false;
            }
        }

        return true;
    }

    private int getNewState(double[] stochasticState) {
        double prob = ThreadLocalRandom.current().nextDouble(0, 1);

        double intervalBound = .0;

        int index = 0;

//        System.out.println("stochasticState.length: " + stochasticState.length);

        for (; index < stochasticState.length; ++index) {
            intervalBound += stochasticState[index];

            if (prob <= intervalBound) break;
        }

        return index;
    }

    private double[] getNewEmpiricalStochasticState(int[] stateJumpNumbers, int currentStep) {
        return Arrays.stream(stateJumpNumbers)
                .mapToDouble(stateJumpNumber -> (double) stateJumpNumber / currentStep)
                .toArray();
    }

    private double[] getNewTheoreticalStochasticState(double[][] jumpMatrix, double[] curTheoreticalStochasticState) {
        double[] resultVector = new double[curTheoreticalStochasticState.length];

        for (int index1 = 0; index1 < jumpMatrix.length; ++ index1) {
            for (int index2 = 0; index2 < jumpMatrix.length; ++ index2) {
                resultVector[index1] += jumpMatrix[index2][index1] * curTheoreticalStochasticState[index2];
            }
        }

        return resultVector;
    }
}