package edu.rsatu.automaton;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ModelingFileWriter {
    public static void write(
            List<Integer> states,
            List<List<Double>> empiricalStochasticStates,
            List<List<Double>> theoreticalStochasticStates)
    {
        try (FileWriter fileWriter = new FileWriter("output.txt")) {
            for (int index = 0; index < empiricalStochasticStates.size(); ++index) {
                fileWriter
                        .append(String.valueOf(index))
                        .append("\tZ").append(String.valueOf(states.get(index)))
                        .append("\t").append(formatDoubleList(empiricalStochasticStates.get(index)))
                        .append("\t").append(formatDoubleList(theoreticalStochasticStates.get(index)))
                        .append("\n");
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String formatDoubleList(List<Double> list) {
        StringBuilder stringBuilder = new StringBuilder();

        for (Double element: list) {
            stringBuilder.append(String.format("%.2f ", element));
        }

        return stringBuilder.toString();
    }
}
