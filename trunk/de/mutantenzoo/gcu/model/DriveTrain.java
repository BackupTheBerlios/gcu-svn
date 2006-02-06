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
import java.util.UUID;

import de.mutantenzoo.gcu.units.UnitSystem;
import de.mutantenzoo.raf.FileName;
import de.mutantenzoo.raf.Measure;
import de.mutantenzoo.raf.Quantifiable;

/**
 * Central application data model.
 * Represents all aspects of a drivetrain that
 * can be handled by the application.
 * Doesn't include presentation specific properties, however (e.g.
 * doesn't include information about sorting of gears or
 * which chainline statuses should be shown).
 * A main principle of GCUs implementation is that as
 * little data as possible is being cached or stored in
 * internal structures. Because all the properties of a drivetrain
 * are retrievable through very basic and computationally cheap
 * mathematical operations from a small set of primitive data,
 * the property getters usually just implement
 * these operations instead of doing all computations
 * in a single place and storing the results in instance fields.
 * This means that on every getter call the return value will be
 * newly calculated. Because of the simpleness of the underlying
 * mathematical rules, this is more efficient than keeping a large
 * set of intermediately stored data up-to-date when
 * an input property changes.
 * @author MKlemm
 * 
 */
public class DriveTrain implements Serializable, Comparable<DriveTrain> {


	/**
	 * Generated SUID
	 */
	private static final long serialVersionUID = 6846924453453949891L;

	private PartSet chainwheels;

	private PartSet sprockets;

	private Measure wheelCirc = new Measure(2100.0);
	private Measure cadence = new Measure(95);
	private UnitSystem unitSystem = null;
	
	private Measure rearCenter = new Measure(420.0);
	private Measure maxGoodDragAngle = new Measure(1.5);
	private Measure maxOKDragAngle = new Measure(3.0);
	private Gear lightweightGear = new Gear();
	
	private WheelDiameter wheelDiameter = new WheelDiameter();

	private boolean unitSystemChanged = false;
	private String name;

	private final transient UUID uuid;
	private transient File file = null;
	private transient boolean modified = false;
	
	/**
	 * Contsructor, initializes a drive train with
	 * a predefined number of front and rear sprockets,
	 * this is used as a convenience for the initial view, so
	 * the user doesn't need to add all parts, but can 
	 * just enter values into existing table rows... 
	 */
	public DriveTrain(int initialChainwheelCount, int initialSprocketCount) {
		uuid = UUID.randomUUID();
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

	/**
	 * Default constructor, initializes
	 * a DriveTrain with a metric unit system
	 * and no gears.
	 */
	public DriveTrain() {
		uuid = UUID.randomUUID();
		chainwheels = new PartSet();
		sprockets = new PartSet();
		setUnitSystem(UnitSystem.METRIC);
	}


	/**
	 * @return Returns the name.
	 */
	public String getName() {
		if(name != null) {
			return name;
		} else if(file != null) {
			return FileName.strip(file.getName());
		} else {
			return null;
		}
	}

	/**
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
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

	/**
	 * Gets a specific gear.
	 * Gears are sorted by
	 * Chainwheel size and rear sprocket size
	 * rather than translation ratios.
	 * @param index The index of the gear to retrieve.
	 * @return A Gear object containing the characteristics
	 * of the gear specified by <i>index</i>. For
	 * performance and data consistency reasons,
	 * the returned Gear instance is re-used for every
	 * invocation of getGear(). Thus, it doesn't make
	 * sense for the client to cache Gear objects somewhere.
	 * They are to be used for immediate display or value
	 * retrieval only.
	 */
	public Gear getGear(int index) {
		int ci = index / sprockets.size();
		int si = index % sprockets.size();
		return getGear(
				chainwheels.get(ci),
				sprockets.get(si)
		);
	}
	
	/**
	 * Gets the number of gears of this drivetrain
	 * @return The number of gears of this drivetrain
	 */
	public int getGearCount() {
		return chainwheels.size() * sprockets.size();
	}
	
	/**
	 * Gets a specific gear identified by 
	 * the front and rear sprockets it
	 * connects.
	 * @param chainwheel The chainwheel of the gear
	 * @param sprocket The rear sprocket of the gear
	 * @return A Gear object containing the characteristics
	 * of the gear specified by <i>index</i>. For
	 * performance and data consistency reasons,
	 * the returned Gear instance is re-used for every
	 * invocation of getGear(). Thus, it doesn't make
	 * sense for the client to cache Gear objects somewhere.
	 * They are to be used for immediate display or value
	 * retrieval only.
	 */
	public Gear getGear(Part chainwheel, Part sprocket) {
		lightweightGear.setState(this, chainwheel, sprocket);
		return lightweightGear;
	}
	
	
	/**
	 * Gets the wheel cicumference
	 * @return The wheel circumference.
	 */
	public Measure getWheelCirc() {
		return wheelCirc;
	}
	
	
	/**
	 * Gets the wheel diameter
	 * (directly dependent on the wheel circumference)
	 * @return The wheel diameter
	 */
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
		maxOKDragAngle.setUnit("\u00B0");
		maxOKDragAngle.setFormat(unitSystem.getPartPitchFormat());
		maxGoodDragAngle.setUnit("\u00B0");
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

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return obj != null && ((DriveTrain)obj).uuid.equals(uuid);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return uuid.hashCode();
	}

	/**
	 * Implements @see java.lang.Comparable@compareTo(java.lang.Object)
	 * @param o The object to compare to
	 * @return @see java.lang.Comparable@compareTo(java.lang.Object)
	 */
	public int compareTo(DriveTrain o) {
		return uuid.toString().compareTo(o.uuid.toString());
	}
	
	/**
	 * Utility class to wrap the value of the
	 * wheel circumference and expose its value
	 * converted to a diameter.
	 * @author mklemm
	 *
	 */
	private class WheelDiameter implements Quantifiable, Serializable {

		/**
		 * Generated SUID
		 */
		private static final long serialVersionUID = -119523948608991855L;

		/*
		 *  (non-Javadoc)
		 * @see de.mutantenzoo.raf.Quantifiable#getValue()
		 */
		public double getValue() {
			return unitSystem.computeWheelDiameter(wheelCirc.getValue());
		}

		/*
		 *  (non-Javadoc)
		 * @see de.mutantenzoo.raf.Quantifiable#setValue(double)
		 */
		public void setValue(double value) {
			wheelCirc.setValue(unitSystem.computeWheelCirc(value));
		}

		/*
		 *  (non-Javadoc)
		 * @see de.mutantenzoo.raf.Quantifiable#getStringValue()
		 */
		public String getStringValue() {
			return unitSystem.getWheelDiameterFormat().format(getValue());
		}

		/*
		 *  (non-Javadoc)
		 * @see de.mutantenzoo.raf.Quantifiable#setStringValue(java.lang.String)
		 */
		public void setStringValue(String value) throws ParseException {
			setValue(unitSystem.getWheelDiameterFormat().parse(value).doubleValue());
		}

		/*
		 *  (non-Javadoc)
		 * @see de.mutantenzoo.raf.Quantifiable#getUnit()
		 */
		public String getUnit() {
			return unitSystem.getWheelDiameterUnit();
		}
				
	}

}