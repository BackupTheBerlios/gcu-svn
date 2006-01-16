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

import java.io.Serializable;
import java.text.ParseException;

import de.mutantenzoo.raf.Measure;
import de.mutantenzoo.raf.Quantifiable;

import static java.lang.Math.*;

/**
 * @author MKlemm
 *
 */
public class Gear implements Cloneable, Comparable<Gear>, Serializable {

	/**
	 * Generated SUID
	 */
	private static final long serialVersionUID = -5211561364024935718L;
	
	private DriveTrain parent;
	private Part chainwheel;
	private Part sprocket;
	
	private Measure development;
	private Speed speed = new Speed();
	private double trans;
	
	Gear() {
		// default constructor default visibility
	}
	
	/**
	 * Constructor
	 * @param index
	 * @param size
	 * @param index2
	 * @param size2
	 * @param circ
	 */
	public Gear(
			DriveTrain parent,
			Part chainwheel,
			Part sprocket
	) {
		setState(parent, chainwheel, sprocket);
	}

	void setState(DriveTrain parent, Part chainwheel, Part sprocket) {
		this.parent = parent;
		this.chainwheel = chainwheel;
		this.sprocket = sprocket;
		
		
		if(sprocket.getSize() == 0) {
			trans = 0.0;
		} else {
			trans = chainwheel.getSize() / (double)sprocket.getSize();
		}
		development = new Measure(parent.getUnitSystem().computeDevelopment(trans, parent.getWheelCirc().getValue()),
				parent.getUnitSystem().getDevelopmentFormat(),
				parent.getUnitSystem().getDevelopmentUnit() );
	}
	/**
	 * @return Returns the chainWheel.
	 */
	public Part getChainwheel() {
		return chainwheel;
	}


	/**
	 * @return Returns the development.
	 */
	public Measure getDevelopment() {
		return development;
	}



	/**
	 * @return Returns the sprocketIndex.
	 */
	public Part getSprocket() {
		return sprocket;
	}


	/**
	 * @return Returns the trans.
	 */
	public double getTrans() {
		return trans;
	}


	/**
	 * @return Returns the deviationStatus.
	 */
	public ChainlineStatus getChainlineStatus() {
		double dragAngle = abs( getDragAngle() );
		if(dragAngle <= parent.getMaxGoodDragAngle().getValue()) {
			return ChainlineStatus.GOOD;
		} else if(dragAngle <= parent.getMaxOKDragAngle().getValue()) {
			return ChainlineStatus.OK;
		} else {
			return ChainlineStatus.BAD;
		}
	}


	public int compareTo(Gear o) {
		if(this.getTrans() < o.getTrans()) {
			return -1;
		} else if(this.getTrans() > o.getTrans()) {
			return 1;
		} else {
			return 0;		
		}
		/*
		if( this.length < o.length ) {
			return -1;
		} else if(this.length > o.length) {
			return 1;
		} else {
			return 0;
		}
		*/
	}

	public Speed getSpeed() {
		return speed;
	}
	
	public double getDragAngle() {
		return (atan2(parent.getRearCenter().getValue(), chainwheel.getChainline()-sprocket.getChainline()) - PI/2.0) * 180.0 / PI;
	}


	/**
	 * @return Returns the parent.
	 */
	public DriveTrain getParent() {
		return parent;
	}
	
	public class Speed implements Quantifiable,Serializable {

		/**
		 * generated SUID
		 */
		private static final long serialVersionUID = -1321610333922703745L;

		public double getValue() {
			return parent.getUnitSystem().computeSpeed(development.getValue(), parent.getCadence().getValue());
		}

		public void setValue(double value) {
			parent.getCadence().setValue(parent.getUnitSystem().computeCadence(development.getValue(), value));
		}

		public String getStringValue() {
			return parent.getUnitSystem().getSpeedFormat().format(getValue());
		}

		public void setStringValue(String value) throws ParseException {
			setValue(parent.getUnitSystem().getSpeedFormat().parse(value).doubleValue());
		}

		public String getUnit() {
			return parent.getUnitSystem().getSpeedUnit();
		}		
	}

	public boolean isValid() {
		return chainwheel.getSize() > 0 && sprocket.getSize() > 0;
	}
	
	@Override public Gear clone() {
		return new Gear(parent, chainwheel, sprocket);
	}
}
