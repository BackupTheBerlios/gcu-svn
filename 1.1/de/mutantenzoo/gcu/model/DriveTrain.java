/**
 * Created: 13.12.2005 08:36:01
 *
 * $Id$
 *
 * Copyright 2005 MKlemm
 *
 * This file is part of GCU.
 *
 * GCU is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * Foobar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GCU; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package de.mutantenzoo.gcu.model;

import java.io.File;
import java.io.Serializable;
import java.text.ParseException;

import de.mutantenzoo.gcu.units.UnitSystem;
import de.mutantenzoo.raf.Measure;
import de.mutantenzoo.raf.Quantifiable;

/**
 * @author MKlemm
 * 
 */
public class DriveTrain implements Serializable {

	/**
	 * Generated SUID
	 */
	private static final long serialVersionUID = 1L;

	private PartSet chainwheels;

	private PartSet sprockets;

	private Measure wheelCirc = new Measure(2100.0);
	private Measure cadence = new Measure(95);
	private UnitSystem unitSystem = null;
	
	private Measure rearCenter = new Measure(600.0);
	private Measure maxGoodDragAngle = new Measure(1.5);
	private Measure maxOKDragAngle = new Measure(3.0);
	private Gear lightweightGear = new Gear();
	
	private transient File file = null;
	private transient boolean modified = false;
	private WheelDiameter wheelDiameter = new WheelDiameter();

	private boolean unitSystemChanged = false;
	

	/**
	 * 
	 */
	public DriveTrain(int initialChainwheelCount, int initialSprocketCount) {
		chainwheels = new PartSet(initialChainwheelCount);
		for(int n=0; n<initialChainwheelCount; n++) {
			chainwheels.createPart(0);
		}
		
		sprockets = new PartSet(initialSprocketCount);
		for(int n=0; n<initialSprocketCount; n++) {
			sprockets.createPart(0);
		}
		
		setUnitSystem(UnitSystem.METRIC);
	}

	public DriveTrain() {
		chainwheels = new PartSet();
		sprockets = new PartSet();
		setUnitSystem(UnitSystem.METRIC);
	}


	/**
	 * @return Returns the chainwheels.
	 */
	public PartSet getChainwheels() {
		return chainwheels;
	}

	/**
	 * @return Returns the sprockets.
	 */
	public PartSet getSprockets() {
		return sprockets;
	}

	public Gear getGear(int index) {
		int ci = index / sprockets.size();
		int si = index % sprockets.size();
		return getGear(
				chainwheels.get(ci),
				sprockets.get(si)
		);
	}
	
	public int getGearCount() {
		return chainwheels.size() * sprockets.size();
	}
	
	public Gear getGear(Part chainwheel, Part sprocket) {
		lightweightGear.setState(this, chainwheel, sprocket);
		return lightweightGear;
	}
	
	
	
	public Measure getWheelCirc() {
		return wheelCirc;
	}
	
	
	public WheelDiameter getWheelDiameter() {
		return wheelDiameter;
	}

	/**
	 * @return Returns the cadence in RPM.
	 */
	public Measure getCadence() {
		return cadence;
	}

	/**
	 * @return Returns the unitSystem.
	 */
	public UnitSystem getUnitSystem() {
		return unitSystem;
	}

	/**
	 * @param unitSystem The unitSystem to set.
	 */
	public void setUnitSystem(UnitSystem unitSystem) {
		modified = this.unitSystem == null || !this.unitSystem.equals(unitSystem);
		if(modified && this.unitSystem != null) {
			wheelCirc.setValue(unitSystem.translate(wheelCirc.getValue()));
			rearCenter.setValue(unitSystem.translate(rearCenter.getValue()));
		}
		this.unitSystem = unitSystem;
		wheelCirc.setUnit(unitSystem.getWheelCircFormat(), unitSystem.getWheelCircUnit());
		wheelCirc.setFormat(unitSystem.getWheelCircFormat());
		rearCenter.setUnit(unitSystem.getRearCenterFormat(), unitSystem.getRearCenterUnit());
		rearCenter.setFormat(unitSystem.getRearCenterFormat());
		chainwheels.setUnitSystem(unitSystem);
		sprockets.setUnitSystem(unitSystem);
		cadence.setUnit(unitSystem.getCadenceUnit());
		cadence.setFormat(unitSystem.getCadenceFormat());
		maxOKDragAngle.setUnit("°");
		maxOKDragAngle.setFormat(unitSystem.getPartPitchFormat());
		maxGoodDragAngle.setUnit("°");
		maxGoodDragAngle.setFormat(unitSystem.getPartPitchFormat());
		unitSystemChanged  = true;
	}

	/**
	 * @return Returns the fileName.
	 */
	public File getFile() {
		return file;
	}

	/**
	 * @param fileName The fileName to set.
	 */
	public void setFile(File file) {
		this.file = file;
	}


	/**
	 * @return Returns the rearCenter.
	 */
	public Measure getRearCenter() {
		return rearCenter;
	}

	/**
	 * @return Returns the maxGoodDragAngle.
	 */
	public Measure getMaxGoodDragAngle() {
		return maxGoodDragAngle;
	}

	/**
	 * @return Returns the maxOKDragAngle.
	 */
	public Measure getMaxOKDragAngle() {
		return maxOKDragAngle;
	}
	
	private class WheelDiameter implements Quantifiable, Serializable {

		/**
		 * Generated SUID
		 */
		private static final long serialVersionUID = -119523948608991855L;

		public double getValue() {
			return unitSystem.computeWheelDiameter(wheelCirc.getValue());
		}

		public void setValue(double value) {
			wheelCirc.setValue(unitSystem.computeWheelCirc(value));
		}

		public String getStringValue() {
			return unitSystem.getWheelDiameterFormat().format(getValue());
		}

		public void setStringValue(String value) throws ParseException {
			setValue(unitSystem.getWheelDiameterFormat().parse(value).doubleValue());
		}

		public String getUnit() {
			return unitSystem.getWheelDiameterUnit();
		}
				
	}
	
	
	
	/**
	 * @return Returns the modified.
	 */
	public boolean isModified() {
		return modified;
	}

	/**
	 * @param modified The modified to set.
	 */
	public void setModified(boolean modified) {
		this.modified = modified;
	}

	public void reset() {
		modified = false;
	}

	public boolean unitSystemHasChanged() {
		boolean c = unitSystemChanged;
		unitSystemChanged = false;
		return c;
	}

}