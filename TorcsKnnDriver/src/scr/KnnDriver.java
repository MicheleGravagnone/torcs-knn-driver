package scr;

import knn.NearestNeighbor;
import knn.Sample;

public class KnnDriver extends Controller {
    private final NearestNeighbor knn;

    final int[] gearUp = {5000, 6000, 6000, 6500, 7000, 0};
    final int[] gearDown = {0, 2500, 3000, 3000, 3500, 3500};

    public KnnDriver() {
        knn = new NearestNeighbor("driving_data.csv");
    }

    @Override
    public void reset() {
        System.out.println("Restarting the race!");
    }

    @Override
    public void shutdown() {
        System.out.println("Bye bye!");
    }

    private int getGear(SensorModel sensors) {
        int gear = sensors.getGear();
        double rpm = sensors.getRPM();

        if (gear < 1)
            return 1;
        if (gear < 6 && rpm >= gearUp[gear - 1])
            return gear + 1;
        else if (gear > 1 && rpm <= gearDown[gear - 1])
            return gear - 1;
        else
            return gear;
    }

    private float getSteer(SensorModel sensors) {
        float targetAngle = (float) (sensors.getAngleToTrackAxis() - sensors.getTrackPosition() * 0.5);
        if (sensors.getSpeed() > 80)
            return (float) (targetAngle / (0.785398 * (sensors.getSpeed() - 80) * 1));
        else
            return targetAngle / 0.785398f;
    }

    private float getAccel(SensorModel sensors) {
        return 1.0f;
    }

    @Override
    public Action control(SensorModel sensors) {
        Action action = new Action();
        action.gear = getGear(sensors);

        // Prepara un Sample per la previsione
        Sample sample = new Sample(
                sensors.getAngleToTrackAxis(),
                sensors.getSpeed(),
                sensors.getLateralSpeed(),
                sensors.getTrackEdgeSensors(),
                sensors.getTrackPosition()
        );

        // Normalizza le caratteristiche usando i min e max dai dati di addestramento
        double[] features = sample.getFeatures();
        for (int i = 0; i < features.length; i++) {
            features[i] = (features[i] - knn.getFeatureMin()[i]) / (knn.getFeatureMax()[i] - knn.getFeatureMin()[i]);
        }

        // Classifica il Sample e determina l'azione da intraprendere
        int predictedClass = knn.classify(sample, 3);

        double steering = 0;
        double accel = 0;
        double brake = 0;

        switch (predictedClass) {
            case 0:
                steering = 0;
                accel = 1.0;
                brake = 0.0;
                break;
            case 1:
                steering = 0.3;
                accel = 1.0;
                brake = 0.0;
                break;
            case 2:
                steering = -0.3;
                accel = 1.0;
                brake = 0.0;
                break;
            case 3:
                steering = 0.0;
                accel = 0.0;
                brake = 0.0;
                break;
            case 4:
                steering = 0.3;
                accel = 0.0;
                brake = 0.0;
                break;
            case 5:
                steering = 0.0;
                accel = 0.0;
                brake = 1.0;
                break;
            case 6:
                steering = 0.3;
                accel = 0.0;
                brake = 1.0;
                break;
            case 7:
                steering = -0.3;
                accel = 0.0;
                brake = 1.0;
                break;
            case 8:
                steering = -0.3;
                accel = 0.0;
                brake = 0.0;
                break;
            default:
                System.err.println("Classe sconosciuta: " + predictedClass);
                steering = 0;
                accel = 0.0;
                brake = 0.0;
                break;
        }

        action.steering = steering;
        action.accelerate = accel;
        action.brake = brake;

        return action;
    }
}