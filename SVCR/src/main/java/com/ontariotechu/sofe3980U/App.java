package com.ontariotechu.sofe3980U;

import java.io.FileReader;
import java.util.List;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

public class App 
{
    public static void main(String[] args)
    {
        String[] files = {"model_1.csv", "model_2.csv", "model_3.csv"};

        double bestMSE = Double.MAX_VALUE;
        double bestMAE = Double.MAX_VALUE;
        double bestMARE = Double.MAX_VALUE;

        String bestMSEModel = "";
        String bestMAEModel = "";
        String bestMAREModel = "";

        for (String filePath : files) {
            FileReader filereader;
            List<String[]> allData;

            try {
                filereader = new FileReader(filePath);
                CSVReader csvReader = new CSVReaderBuilder(filereader).withSkipLines(1).build();
                allData = csvReader.readAll();
            }
            catch (Exception e) {
                System.out.println("Error reading the CSV file: " + filePath);
                continue;
            }

            double sumMSE = 0;
            double sumMAE = 0;
            double sumMARE = 0;
            double epsilon = 1e-10;
            int n = allData.size();

            for (String[] row : allData) {
                double y_true = Double.parseDouble(row[0]);
                double y_predicted = Double.parseDouble(row[1]);

                double error = y_true - y_predicted;

                sumMSE += error * error;
                sumMAE += Math.abs(error);
                sumMARE += Math.abs(error) / (Math.abs(y_true) + epsilon);
            }

            double MSE = sumMSE / n;
            double MAE = sumMAE / n;
            double MARE = sumMARE / n;

            System.out.println("for " + filePath);
            System.out.println("\tMSE =" + MSE);
            System.out.println("\tMAE =" + MAE);
            System.out.println("\tMARE =" + MARE);

            if (MSE < bestMSE) {
                bestMSE = MSE;
                bestMSEModel = filePath;
            }

            if (MAE < bestMAE) {
                bestMAE = MAE;
                bestMAEModel = filePath;
            }

            if (MARE < bestMARE) {
                bestMARE = MARE;
                bestMAREModel = filePath;
            }
        }

        System.out.println("According to MSE, The best model is " + bestMSEModel);
        System.out.println("According to MAE, The best model is " + bestMAEModel);
        System.out.println("According to MARE, The best model is " + bestMAREModel);
    }
}