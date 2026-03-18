package com.ontariotechu.sofe3980U;

import java.io.FileReader;
import java.util.List;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

public class App 
{
    public static void main(String[] args)
    {
        String filePath = "model.csv";
        FileReader filereader;
        List<String[]> allData;

        try {
            filereader = new FileReader(filePath);
            CSVReader csvReader = new CSVReaderBuilder(filereader).withSkipLines(1).build();
            allData = csvReader.readAll();
        }
        catch (Exception e) {
            System.out.println("Error reading the CSV file");
            return;
        }

        double epsilon = 1e-15;
        double ceSum = 0.0;
        int n = allData.size();

        int[][] confusionMatrix = new int[5][5];

        for (String[] row : allData) {
            int yTrue = Integer.parseInt(row[0]);

            double[] yPredicted = new double[5];
            for (int i = 0; i < 5; i++) {
                yPredicted[i] = Double.parseDouble(row[i + 1]);
            }

            double correctClassProbability = yPredicted[yTrue - 1];
            if (correctClassProbability < epsilon) {
                correctClassProbability = epsilon;
            }

            ceSum += -Math.log(correctClassProbability);

            int predictedClass = 1;
            double maxProbability = yPredicted[0];

            for (int i = 1; i < 5; i++) {
                if (yPredicted[i] > maxProbability) {
                    maxProbability = yPredicted[i];
                    predictedClass = i + 1;
                }
            }

            confusionMatrix[predictedClass - 1][yTrue - 1]++;
        }

        double CE = ceSum / n;

        System.out.printf("CE =%.7f%n", CE);
        System.out.println("Confusion matrix");

        // Encabezado
        System.out.printf("\t\t%-8s%-8s%-8s%-8s%-8s%n", "y=1", "y=2", "y=3", "y=4", "y=5");

        // Filas
        for (int i = 0; i < 5; i++) {
            System.out.printf("\ty^=%d\t", i + 1);
            for (int j = 0; j < 5; j++) {
                System.out.printf("%-8d", confusionMatrix[i][j]);
            }
            System.out.println();
        }
    }
}