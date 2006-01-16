/**
 * 
 */
package de.mutantenzoo.gcu.units;

import java.io.Serializable;
import java.text.DecimalFormat;

/**
 * @author MKlemm
 *
 */
public interface UnitSystem extends Serializable {	
	
	
	public static final MetricUnitSystem METRIC = new MetricUnitSystem();
	public static final ImperialUnitSystem IMPERIAL = new ImperialUnitSystem();
	
	public static final String DEVELOPMENT_NOTION_GEAR_INCHES = "GearInches";
	public static final String DEVELOPMENT_NOTION_DEVELOPMENT = "Development";
	
	double computeWheelDiameter(double wheelCirc);
	double computeWheelCirc(double wheelDiameter);
	double computeDevelopment(double trans, double wheelCirc);
	double computeSpeed(double length, double cadence);
	
	double translate(double otherValue);
	
	String getWheelDiameterUnit();
	DecimalFormat getWheelDiameterFormat();
	
	String getWheelCircUnit();
	DecimalFormat getWheelCircFormat();
	
	String getDevelopmentUnit();
	DecimalFormat getDevelopmentFormat();
	
	double getMaxDevelopment();
	
	String getSpeedUnit();
	DecimalFormat getSpeedFormat();
	
	String getRearCenterUnit();
	DecimalFormat getRearCenterFormat();
	
	String getPartPitchUnit();
	DecimalFormat getPartPitchFormat();
	
	String getChainlineUnit();
	DecimalFormat getChainlineFormat();
	
	String getCadenceUnit();
	DecimalFormat getCadenceFormat();
	
	double computeCadence(double length, double speed);
	
	double getFramelineOverlength();
	float getDefaultPartWidth();
	float getDefaultChainWidth();
	float getNarrowLineWidth();
	float getMediumLineWidth();
	float getWideLineWidth();
	float[] getDashDotLine();
	
	double getMeasureLineOverlength();
	float getArrowTipLength();
	float getArrowTipWidth();
	
	double getSmallMeasureLineDistance();
	double getLargeMeasureLineDistance();
	double getDefaultChainPitch();
	String getDevelopmentNotion();
	double getRequiredMargin();
	double getMeasureFontSize();
	double translateDevelopment(double otherValue);
	double getDevelopmentSteps();
}
