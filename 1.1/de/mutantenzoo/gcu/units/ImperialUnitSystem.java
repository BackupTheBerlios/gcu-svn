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
class ImperialUnitSystem implements UnitSystem {	
	
	/**
	 * Default SerialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	
	private static final DecimalFormat WHEEL_DIAMETER_FORMAT = new DecimalFormat("##.##");
	private static final DecimalFormat WHEEL_CIRC_FORMAT = new DecimalFormat("###.##");
	private static final DecimalFormat DEVELOPMENT_FORMAT = new DecimalFormat("###");
	private static final DecimalFormat SPEED_FORMAT = new DecimalFormat("##.##");
	private static final DecimalFormat REAR_CENTER_FORMAT = new DecimalFormat("##.##");
	private static final DecimalFormat PART_DISTANCE_FORMAT = new DecimalFormat("0.###");
	private static final DecimalFormat CHAINLINE_FORMAT = new DecimalFormat("0.###");
	private static final DecimalFormat CADENCE_FORMAT = new DecimalFormat("###");

	private static final double FEET_IN_MILE = 1760.0 * 3.0;
	private static final double INCHES_IN_FOOT = 12.0;
	private static final float[] DASH = {2.0f, 0.4f, 0.4f, 0.4f};

	ImperialUnitSystem() {
		// API consumer cannot instantiate
	}
	
	/* (non-Javadoc)
	 * @see de.mutantenzoo.gcu.ui.units.UnitSystem#computeWheelDiameter(double)
	 */
	public double computeWheelDiameter(double wheelCirc) {
		return wheelCirc / PI;
	}

	/* (non-Javadoc)
	 * @see de.mutantenzoo.gcu.ui.units.UnitSystem#computeWheelCirc(double)
	 */
	public double computeWheelCirc(double wheelDiameter) {
		return wheelDiameter * PI;
	}

	/* (non-Javadoc)
	 * @see de.mutantenzoo.gcu.ui.units.UnitSystem#computeLength(double, double)
	 */
	public double computeDevelopment(double trans, double wheelCirc) {
		return trans * computeWheelDiameter(wheelCirc);
	}

	/* (non-Javadoc)
	 * @see de.mutantenzoo.gcu.ui.units.UnitSystem#computeSpeed(double, double)
	 */
	public double computeSpeed(double gearInches, double cadence) {
		return 60.0 * gearInches * PI * cadence / (FEET_IN_MILE * INCHES_IN_FOOT);
	}

	/* (non-Javadoc)
	 * @see de.mutantenzoo.gcu.ui.units.UnitSystem#getWheelDiameterUnit()
	 */
	public String getWheelDiameterUnit() {
		return "in";
	}

	/* (non-Javadoc)
	 * @see de.mutantenzoo.gcu.ui.units.UnitSystem#getWheelCircUnit()
	 */
	public String getWheelCircUnit() {
		return "in";
	}

	/* (non-Javadoc)
	 * @see de.mutantenzoo.gcu.ui.units.UnitSystem#getLengthUnit()
	 */
	public String getDevelopmentUnit() {
		return "in";
	}

	/* (non-Javadoc)
	 * @see de.mutantenzoo.gcu.ui.units.UnitSystem#getSpeedUnit()
	 */
	public String getSpeedUnit() {
		return "mph";
	}

	public double translate(double otherValue) {
		return otherValue / 25.4;
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
		return 200.0;
	}

	public String getRearCenterUnit() {
		return "in";
	}

	public DecimalFormat getRearCenterFormat() {
		return REAR_CENTER_FORMAT;
	}

	public String getPartPitchUnit() {
		return "in";
	}

	public DecimalFormat getPartPitchFormat() {
		return PART_DISTANCE_FORMAT;
	}

	public String getChainlineUnit() {
		return "in";
	}

	public DecimalFormat getChainlineFormat() {
		return CHAINLINE_FORMAT;
	}

	public double computeCadence(double gearInches, double speed) {
		return speed / (gearInches * PI) / 60.0 * FEET_IN_MILE * INCHES_IN_FOOT;
	}

	public String getCadenceUnit() {
		return "RPM";
	}

	public DecimalFormat getCadenceFormat() {
		return CADENCE_FORMAT;
	}

	public double getFramelineOverlength() {
		return 0;
	}

	public float getDefaultPartWidth() {
		return 0.1f;
	}

	public float getDefaultChainWidth() {
		return 0.09375f; // 3/32"
	}

	public float getNarrowLineWidth() {
		return 1.0f/48.0f;
	}

	public float getMediumLineWidth() {
		return 1.0f/24.0f;
	}

	public float getWideLineWidth() {
		return 1.0f/16.0f;
	}

	public float[] getDashDotLine() {
		return DASH;
	}

	public double getMeasureLineOverlength() {
		return 0.125;
	}

	public float getArrowTipLength() {
		return 0.24f;
	}

	public float getArrowTipWidth() {
		return 0.18f;
	}

	public double getSmallMeasureLineDistance() {
		return 30.0/25.4;
	}

	public double getLargeMeasureLineDistance() {
		return 40.0/25.4;
	}

	public double getDefaultChainPitch() {
		return 0.5;
	}

	@Override public boolean equals(Object o) {
		return o instanceof ImperialUnitSystem;
	}

	public String getDevelopmentNotion() {
		return DEVELOPMENT_NOTION_GEAR_INCHES;
	}

	public double getRequiredMargin() {
		return 34.0/25.4;
	}

	public double getMeasureFontSize() {
		return 8.0/25.4;
	}
}
