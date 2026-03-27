package scr;


public interface SensorModel {


	// Base information on the car and the track

	public double getSpeed();

	public double getAngleToTrackAxis();

	public double[] getTrackEdgeSensors();

	public double[] getFocusSensors();// ML

	public double getTrackPosition();

	public int getGear();

	// Base information on other cars

	public double[] getOpponentSensors();

	public int getRacePosition();

	// Additional information

	public double getLateralSpeed();

	public double getCurrentLapTime();

	public double getDamage();

	public double getDistanceFromStartLine();

	public double getDistanceRaced();

	public double getFuelLevel();

	public double getLastLapTime();

	public double getRPM();

	public double[] getWheelSpinVelocity();

	public double getZSpeed();

	public double getZ();

	public String getMessage();

}
