package edu.rsatu.automaton.form;

import edu.rsatu.automaton.ModelingFileWriter;
import edu.rsatu.automaton.StochasticAutomaton;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.List;

public class StochasticAutomatonForm {
    private JButton jmpMatrLoadButton;
    private JTextField fstStochasticStateInput;
    private JTable outputTable;
    private JPanel mainPanel;
    private JTextArea jmpMatrixArea;
    private JButton calcButton;

    private double[][] jumpMatrix;

    public StochasticAutomatonForm() {
        jmpMatrLoadButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(mainPanel);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                jumpMatrix = getJumpMatrix(readFile(selectedFile));
                fillJumpMatrixArea();
            }
        });


        calcButton.addActionListener(e -> {
            StochasticAutomaton stochasticAutomaton = new StochasticAutomaton(
                    jumpMatrix, getStochasticState(fstStochasticStateInput.getText())
            );

            fillOutputTable(
                    stochasticAutomaton.getStates(),
                    stochasticAutomaton.getEmpiricalStochasticStates(),
                    stochasticAutomaton.getTheoreticalStochasticStates()
            );

            ModelingFileWriter.write(
                    stochasticAutomaton.getStates(),
                    stochasticAutomaton.getEmpiricalStochasticStates(),
                    stochasticAutomaton.getTheoreticalStochasticStates());
        });
    }

    public static void main(String[] args) {
        JFrame mainFrame = new JFrame("");
        mainFrame.setContentPane(new StochasticAutomatonForm().mainPanel);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setMinimumSize(new Dimension(560, 765));
        mainFrame.setVisible(true);
//        mainFrame.setResizable(false);
        mainFrame.setLocationRelativeTo(null);
    }

    private List<String> readFile(File file) {
        List<String> fileLines = new ArrayList<>();

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNext()) {
                fileLines.add(scanner.nextLine());
            }
        }
        catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        return fileLines;
    }

    private double[][] getJumpMatrix(List<String> fileContent) {
        double[][] jumpMatrix = new double[fileContent.size()][fileContent.size()];

        for (int index1 = 0; index1 < fileContent.size(); ++index1) {
            String[] lineParts = fileContent.get(index1).trim().split(" ");

            if (lineParts.length != jumpMatrix.length) throw new IllegalArgumentException();

            for (int index2 = 0; index2 < lineParts.length; ++index2) {
                if (lineParts[index2].contains("/")) {
                    String[] fractionParts = lineParts[index2].split("/");

                    if (fractionParts.length == 2) {
                        jumpMatrix[index1][index2] = Double.parseDouble(fractionParts[0]) / Double.parseDouble(fractionParts[1]);
                    }
                }
                else {
                    jumpMatrix[index1][index2] = Double.parseDouble(lineParts[index2]);
                }
            }
        }

        return jumpMatrix;
    }

    private double[] getStochasticState(String stochasticStateFieldContent) {
        String[] stochasticStateParts = stochasticStateFieldContent.trim().split(" ");

        double[] stochasticState = new double[stochasticStateParts.length];

        for (int index = 0; index < stochasticStateParts.length; ++index) {
            if (stochasticStateParts[index].contains("/")) {
                String[] fractionParts = stochasticStateParts[index].split("/");

                if (fractionParts.length == 2) {
                    stochasticState[index] = Double.parseDouble(fractionParts[0]) / Double.parseDouble(fractionParts[1]);
                }
            }
            else {
                stochasticState[index]= Double.parseDouble(stochasticStateParts[index]);
            }
        }

        return stochasticState;
    }

    private void fillJumpMatrixArea() {
        jmpMatrixArea.setText("");

        for (double[] jumpVector: jumpMatrix) {
            for (double jumpProb: jumpVector) {
                jmpMatrixArea.append(String.format("%.2f ", jumpProb));
            }

            jmpMatrixArea.append("\n");
        }
    }

    private void fillOutputTable(
            List<Integer> states,
            List<List<Double>> empiricalStochasticStates,
            List<List<Double>> theoreticalStochasticStates
    ) {
        if (states.size() != empiricalStochasticStates.size() &&
                empiricalStochasticStates.size() != theoreticalStochasticStates.size()) throw new IllegalArgumentException();

        DefaultTableModel model = (DefaultTableModel) outputTable.getModel();
        model.setRowCount(0);

        for (int index = 0; index < empiricalStochasticStates.size(); ++index) {
            model.addRow(new Object[]{
                    index, "Z" + states.get(index),
                    formatDoubleList(empiricalStochasticStates.get(index)),
                    formatDoubleList(theoreticalStochasticStates.get(index))
                }
            );
        }
    }

    private String formatDoubleList(List<Double> list) {
        StringBuilder stringBuilder = new StringBuilder();

        for (Double element: list) {
            stringBuilder.append(String.format("%.2f ", element));
        }

        return stringBuilder.toString();
    }

    private void createUIComponents() {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Шаг моделирования t");
        model.addColumn("Состояние Zt");
        model.addColumn("Оценка стохастического состояния по результатам моделирования");
        model.addColumn("Стохастическое состояние по уравнению Колмогорова-Чемпена");

        outputTable = new JTable(model);
    }
}
