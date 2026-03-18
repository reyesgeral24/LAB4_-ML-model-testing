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

        double bestBCE = Double.MAX_VALUE;
        double bestAccuracy = -1.0;
        double bestPrecision = -1.0;
        double bestRecall = -1.0;
        double bestF1 = -1.0;
        double bestAUC = -1.0;

        String bestBCEModel = "";
        String bestAccuracyModel = "";
        String bestPrecisionModel = "";
        String bestRecallModel = "";
        String bestF1Model = "";
        String bestAUCModel = "";

        for (String filePath : files) {
            FileReader filereader;
            List<String[]> allData;

            try {
                filereader = new FileReader(filePath);
                CSVReader csvReader = new CSVReaderBuilder(filereader).withSkipLines(1).build();
                allData = csvReader.readAll();
            } catch (Exception e) {
                System.out.println("Error reading the CSV file: " + filePath);
                continue;
            }

            double epsilon = 1e-15;
            int n = allData.size();

            double bceSum = 0.0;

            int TP = 0;
            int FP = 0;
            int TN = 0;
            int FN = 0;

            int nPositive = 0;
            int nNegative = 0;

            // Main pass: BCE + confusion matrix
            for (String[] row : allData) {
                int yTrue = Integer.parseInt(row[0]);
                double yPred = Double.parseDouble(row[1]);

                // avoid log(0)
                if (yPred < epsilon) {
                    yPred = epsilon;
                }
                if (yPred > 1.0 - epsilon) {
                    yPred = 1.0 - epsilon;
                }

                // Standard BCE
                bceSum += yTrue * Math.log(yPred) + (1 - yTrue) * Math.log(1 - yPred);

                int yBinary = (yPred >= 0.5) ? 1 : 0;

                if (yTrue == 1) {
                    nPositive++;
                    if (yBinary == 1) {
                        TP++;
                    } else {
                        FN++;
                    }
                } else {
                    nNegative++;
                    if (yBinary == 1) {
                        FP++;
                    } else {
                        TN++;
                    }
                }
            }

            double BCE = -bceSum / n;
            double Accuracy = (double) (TP + TN) / (TP + TN + FP + FN);
            double Precision = (TP + FP == 0) ? 0.0 : (double) TP / (TP + FP);
            double Recall = (TP + FN == 0) ? 0.0 : (double) TP / (TP + FN);
            double F1 = (Precision + Recall == 0) ? 0.0 : 2.0 * Precision * Recall / (Precision + Recall);

            // ROC + AUC
            double[] x = new double[101]; // FPR
            double[] y = new double[101]; // TPR

            for (int i = 0; i <= 100; i++) {
                double th = i / 100.0;

                int rocTP = 0;
                int rocFP = 0;

                for (String[] row : allData) {
                    int yTrue = Integer.parseInt(row[0]);
                    double yPred = Double.parseDouble(row[1]);

                    if (yTrue == 1 && yPred >= th) {
                        rocTP++;
                    }
                    if (yTrue == 0 && yPred >= th) {
                        rocFP++;
                    }
                }

                double TPR = (nPositive == 0) ? 0.0 : (double) rocTP / nPositive;
                double FPR = (nNegative == 0) ? 0.0 : (double) rocFP / nNegative;

                y[i] = TPR;
                x[i] = FPR;
            }

            double auc = 0.0;
            for (int i = 1; i <= 100; i++) {
                auc += (y[i - 1] + y[i]) * Math.abs(x[i - 1] - x[i]) / 2.0;
            }

            // Output
            System.out.println("for " + filePath);
            System.out.printf("\tBCE =%.7f%n", BCE);
            System.out.println("\tConfusion matrix");
            System.out.printf("\t\t\t%-8s%-8s%n", "y=1", "y=0");
            System.out.printf("\t\ty^=1\t%-8d%-8d%n", TP, FP);
            System.out.printf("\t\ty^=0\t%-8d%-8d%n", FN, TN);
            System.out.printf("\tAccuracy =%.4f%n", Accuracy);
            System.out.printf("\tPrecision =%.7f%n", Precision);
            System.out.printf("\tRecall =%.8f%n", Recall);
            System.out.printf("\tf1 score =%.7f%n", F1);
            System.out.printf("\tauc roc =%.8f%n", auc);

            // Best model tracking
            if (BCE < bestBCE) {
                bestBCE = BCE;
                bestBCEModel = filePath;
            }
            if (Accuracy > bestAccuracy) {
                bestAccuracy = Accuracy;
                bestAccuracyModel = filePath;
            }
            if (Precision > bestPrecision) {
                bestPrecision = Precision;
                bestPrecisionModel = filePath;
            }
            if (Recall > bestRecall) {
                bestRecall = Recall;
                bestRecallModel = filePath;
            }
            if (F1 > bestF1) {
                bestF1 = F1;
                bestF1Model = filePath;
            }
            if (auc > bestAUC) {
                bestAUC = auc;
                bestAUCModel = filePath;
            }
        }

        System.out.println("According to BCE, The best model is " + bestBCEModel);
        System.out.println("According to Accuracy, The best model is " + bestAccuracyModel);
        System.out.println("According to Precision, The best model is " + bestPrecisionModel);
        System.out.println("According to Recall, The best model is " + bestRecallModel);
        System.out.println("According to F1 score, The best model is " + bestF1Model);
        System.out.println("According to AUC ROC, The best model is " + bestAUCModel);
    }
}