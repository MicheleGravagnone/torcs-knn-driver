package knn;

public class Sample {
    double[] features;
    int cls;

    /*
    Constructor to initialize the Sample with a given set of features and class label.
    This is typically used when building the dataset.
    */
    public Sample(double[] features, int cls) {
        this.features = features;
        this.cls = cls;
    }

    // Nuovo costruttore per creare un Sample specifico per il problema di TORCS
    public Sample(double angle, double speedX, double speedY, double[] edgeSensors, double trackPosition, int cls) {
        this.features = new double[edgeSensors.length + 4];
        this.features[0] = angle;
        this.features[1] = speedX;
        this.features[2] = speedY;
        System.arraycopy(edgeSensors, 0, this.features, 3, edgeSensors.length);
        this.features[features.length - 1] = trackPosition;
        this.cls = cls;
    }

    public Sample(double angle, double speedX, double speedY, double[] edgeSensors, double trackPosition) {
        this(angle, speedX, speedY, edgeSensors, trackPosition, -1);
    }

    /*
    Constructor to initialize the Sample with a given set of features without a class label.
    This is used when classifying a new sample.
    */
    public Sample(double[] features) {
        this.features = features;
        this.cls = -1; // Default class value
    }

    /*
    Constructor to initialize the Sample from a CSV line.
    Assumes the last value in the CSV is the class label.
    */
    public Sample(String line) {
        String[] parts = line.split(";");
        int n = parts.length;
        features = new double[n - 1];
        for (int i = 0; i < n - 1; i++) {
            features[i] = Double.parseDouble(parts[i].trim()); // Modifica qui
        }
        this.cls = (int) Double.parseDouble(parts[n - 1].trim());
    }

    public double[] getFeatures() {
        return features;
    }
    /*
    Method to calculate the Euclidean distance between this sample and another sample.
    */
    public double distance(Sample other) {
        double sum = 0;
        for (int i = 0; i < this.features.length; i++) {
            sum += Math.pow(this.features[i] - other.features[i], 2);
        }
        return Math.sqrt(sum);
    }
}
