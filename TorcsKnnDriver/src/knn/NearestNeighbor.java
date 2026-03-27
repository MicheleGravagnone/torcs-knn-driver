package knn;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NearestNeighbor {

    private List<Sample> trainingData;
    private KDTree kdtree;
    private int[] classCounts;
    private double[] featureMin;
    private double[] featureMax;

    private String firstLineOfTheFile;

    public NearestNeighbor(String filename) {
        this.trainingData = new ArrayList<>();
        this.kdtree = null;
        this.classCounts = new int[9];
        this.firstLineOfTheFile = "angle,speedX,speedY,track_0,track_1,track_2,track_3,track_4,track_5,track_6,track_7," +
                "track_8,track_9,track_10,track_11,track_12,track_13,track_14,track_15,track_16,track_17,track_18,trackPos,class";
        readAndNormalizeData(filename);
        this.kdtree = new KDTree(trainingData);
    }

    private void readAndNormalizeData(String filename) {
        readPointsFromCSV(filename);

        int featureLength = trainingData.get(0).features.length;
        featureMin = new double[featureLength];
        featureMax = new double[featureLength];

        for (int i = 0; i < featureLength; i++) {
            featureMin[i] = Double.MAX_VALUE;
            featureMax[i] = Double.MIN_VALUE;
        }

        for (Sample sample : trainingData) {
            for (int i = 0; i < featureLength; i++) {
                if (sample.features[i] < featureMin[i]) featureMin[i] = sample.features[i];
                if (sample.features[i] > featureMax[i]) featureMax[i] = sample.features[i];
            }
        }

        for (Sample sample : trainingData) {
            for (int i = 0; i < featureLength; i++) {
                sample.features[i] = (sample.features[i] - featureMin[i]) / (featureMax[i] - featureMin[i]);
            }
        }
    }

    private void readPointsFromCSV(String filename) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line;
            boolean isFirstLine = true;
            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                trainingData.add(new Sample(line));
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.kdtree = new KDTree(trainingData);
    }

    public List<Sample> findKNearestNeighbors(Sample testPoint, int k) {
        return kdtree.kNearestNeighbors(testPoint, k);
    }

    public int classify(Sample testPoint, int k) {
        List<Sample> kNearestNeighbors = findKNearestNeighbors(testPoint, k);
        for (int i = 0; i < classCounts.length; i++) {
            classCounts[i] = 0;
        }
        for (Sample neighbor : kNearestNeighbors) {
            classCounts[neighbor.cls]++;
        }

        int maxCount = -1;
        int predictedClass = -1;
        for (int i = 0; i < classCounts.length; i++) {
            if (classCounts[i] > maxCount) {
                maxCount = classCounts[i];
                predictedClass = i;
            }
        }

        return predictedClass;
    }

    public double[] getFeatureMin() {
        return featureMin;
    }

    public double[] getFeatureMax() {
        return featureMax;
    }

    public List<Sample> getTrainingData() {
        return trainingData;
    }
}