package scr;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.SwingUtilities;

public class SimpleDriver extends Controller {
	private FileWriter csvWriter;

	// User controls
	private boolean accelerate;
	private boolean brake;
	private boolean steerLeft;
	private boolean steerRight;

	final int[] gearUp = {5000, 6000, 6000, 6500, 7000, 0};
	final int[] gearDown = {0, 2500, 3000, 3000, 3500, 3500};

	final float steerLock = (float) 0.785398;
	final float steerSensitivityOffset = (float) 80.0;
	final float wheelSensitivityCoeff = 1;

	public SimpleDriver() {
		SwingUtilities.invokeLater(() -> new ContinuousCharReaderUI(this));
		try {
			csvWriter = new FileWriter("driving_data.csv");
			csvWriter.append("angle;speedX;speedY;track_0;track_1;track_2;track_3;track_4;track_5;track_6;track_7;track_8;track_9;track_10;track_11;track_12;track_13;track_14;track_15;track_16;track_17;track_18;trackPos;class\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void reset() {
		System.out.println("Restarting the race!");
	}

	public void shutdown() {
		System.out.println("Bye bye!");
		try {
			csvWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
		if (sensors.getSpeed() > steerSensitivityOffset)
			return (float) (targetAngle
					/ (steerLock * (sensors.getSpeed() - steerSensitivityOffset) * wheelSensitivityCoeff));
		else
			return (targetAngle) / steerLock;
	}

	private float getAccel(SensorModel sensors) {
		return 1.0f; // Constant acceleration at max value
	}

	public Action control(SensorModel sensors) {
		Action action = new Action();
		action.gear = getGear(sensors);

		double steering = 0;
		double accel = 0;

		if (steerRight) {
			steering = -0.3;
		} else if (steerLeft) {
			steering = 0.3;
		}

		if (accelerate) {
			accel = getAccel(sensors);
		}

		if (brake) {
			if (sensors.getSpeed() < 1) {
				action.gear = -1;
				accel = 1.0;
			} else {
				action.brake = 1.0;
			}
		}

		action.steering = steering;
		action.accelerate = accel;

		// Record data
		writeDataToCSV(sensors, accel, steering);

		return action;
	}

	private void writeDataToCSV(SensorModel sensors, double accel, double steering) {
		List<Double> readings = new ArrayList<>();
		readings.add(sensors.getAngleToTrackAxis()); // Angolo
		readings.add(sensors.getSpeed()); // SpeedX
		readings.add(sensors.getLateralSpeed()); // SpeedY
		for (double edgeSensor : sensors.getTrackEdgeSensors()) { // Track sensors
			readings.add(edgeSensor);
		}
		readings.add(sensors.getTrackPosition()); // TrackPos
		int classe = determineClass(accel, steering, brake); // Class
		readings.add((double) classe);

		PrintWriter writer = new PrintWriter(csvWriter);
		for (double value : readings) {
			writer.printf(Locale.US, "%.2f;", value);
		}
		writer.println();
		writer.flush();
	}

	private int determineClass(double accel, double steering, boolean brake) {
		if (brake) {
			if (steering == 0) {
				return 5; // brake
			} else if (steering > 0) {
				return 6; // break, left steer
			} else if (steering < 0) {
				return 7; // break, right steer
			}
		} else if (accel == 1.0) {
			if (steering == 0) {
				return 0; // max accel, no steer
			} else if (steering > 0) {
				return 1; // max accel, left steer
			} else if (steering < 0) {
				return 2; // max accel, right steer
			}
		} else {
			if (steering == 0) {
				return 3; // no accel, no steer
			} else if (steering > 0) {
				return 4; // no accel, left steer
			} else if (steering < 0) {
				return 8; // no accel, right steer
			}
		}
		return -1; // unknown class
	}

	// Methods to set user controls
	public void setAccelerate(boolean accelerate) {
		this.accelerate = accelerate;
	}

	public void setBrake(boolean brake) {
		this.brake = brake;
	}

	public void setSteerLeft(boolean steerLeft) {
		this.steerLeft = steerLeft;
	}

	public void setSteerRight(boolean steerRight) {
		this.steerRight = steerRight;
	}
}
