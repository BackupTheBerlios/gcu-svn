/**
 * 
 */
package de.mutantenzoo.gcu.units;

import static java.lang.Math.*;

import java.text.DecimalFormat;

/**
 * @author MKlemm
 *
 */
class MetricUnitSystem implements UnitSystem {	
	
	/**
	 * Default SerialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	
	private static final DecimalFormat WHEEL_DIAMETER_FORMAT = new DecimalFormat("###.##");
	private static final DecimalFormat WHEEL_CIRC_FORMAT = new DecimalFormat("####.##");
	private static final DecimalFormat DEVELOPMENT_FORMAT = new DecimalFormat("##.###");
	private static final DecimalFormat SPEED_FORMAT = new DecimalFormat("##.##");
	private static final DecimalFormat REAR_CENTER_FORMAT = new DecimalFormat("####");
	private static final DecimalFormat PART_DISTANCE_FORMAT = new DecimalFormat("##.##");
	private static final DecimalFormat CHAINLINE_FORMAT = new DecimalFormat("##");
	private static final DecimalFormat CADENCE_FORMAT = new DecimalFormat("###");
	private static final float[] DASH = {50.0f, 10.0f, 10.0f, 10.0f};
	MetricUnitSystem() {
		// API consumer cannot instantiate
	}

	public double computeWheelDiameter(double wheelCirc) {
		return wheelCirc / PI;
	}

	public double computeWheelCirc(double wheelDiameter) {
		return wheelDiameter * PI;
	}

	public double computeDevelopment(double trans, double wheelCirc) {
		return wheelCirc * trans / 1000.0;
	}

	public double computeSpeed(double length, double cadence) {
		return length * cadence * 0.06;
	}

	public String getWheelDiameterUnit() {
		return "mm";
	}

	public String getWheelCircUnit() {
		return "mm";
	}

	public String getDevelopmentUnit() {
		return "m";
	}

	public String getSpeedUnit() {
		return "km/h";
	}

	public double translate(double otherValue) {
		return otherValue * 25.4;
	}

	public DecimalFormat getWheelDiameterFormat() {
		return WHEEL_DIAMETER_FORMAT;
	}

	public DecimalFormat getWheelCircFormat() {
		return WHEEL_CIRC_FORMAT;
	}

	public DecimalFormat getDevelopmentFormat() {
		return DEVELOPMENT_FORMAT;
	}

	public DecimalFormat getSpeedFormat() {
		return SPEED_FORMAT;
	}

	public double getMaxDevelopment() {
		return 11.0;
	}

	public String getRearCenterUnit() {
		return "mm";
	}

	public DecimalFormat getRearCenterFormat() {
		return REAR_CENTER_FORMAT;
	}

	public String getPartPitchUnit() {
		return "mm";
	}

	public DecimalFormat getPartPitchFormat() {
		return PART_DISTANCE_FORMAT;
	}

	public String getChainlineUnit() {
		return "mm";
	}

	public DecimalFormat getChainlineFormat() {
		return CHAINLINE_FORMAT;
	}

	public double computeCadence(double length, double speed) {
		return speed / length / 0.06;
	}

	public String getCadenceUnit() {
		return "1/min";
	}

	public DecimalFormat getCadenceFormat() {
		return CADENCE_FORMAT;
	}

	public double getFramelineOverlength() {
		return 50.0;
	}

	public float getDefaultPartWidth() {
		return 2.5f;
	}

	public float getDefaultChainWidth() {
		return 5.5f;
	}

	public float getNarrowLineWidth() {
		return 0.5f;
	}

	public float getMediumLineWidth() {
		return 1.0f;
	}

	public float getWideLineWidth() {
		return 1.5f;
	}

	public float[] getDashDotLine() {
		return DASH;
	}

	public double getMeasureLineOverlength() {
		return 2.0;
	}

	public float getArrowTipLength() {
		return 6.0f;
	}

	public float getArrowTipWidth() {
		return 4.0f;
	}

	public double getSmallMeasureLineDistance() {
		return 30.0;
	}

	public double getLargeMeasureLineDistance() {
		return 40.0;
	}

	public double getDefaultChainPitch() {
		return 12.7; // 1/2"
	}
	
	@Override public boolean equals(Object o) {
		return o instanceof MetricUnitSystem;
	}

	public String getDevelopmentNotion() {
		return DEVELOPMENT_NOTION_DEVELOPMENT;
	}

	public double getRequiredMargin() {
		return 34.0;
	}

	public double getMeasureFontSize() {
		return 8.0;
	}

	public double translateDevelopment(double otherValue) {
		return otherValue * 0.0254 * PI;
	}

}
