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

import java.util.ArrayList;
import java.util.Collection;

import de.mutantenzoo.gcu.units.UnitSystem;
import de.mutantenzoo.raf.Measure;

/**
 * @author MKlemm
 *
 */
public class PartSet extends ArrayList<Part> {
	/**
	 * Generated SUID
	 */
	private static final long serialVersionUID = -5960600583842431652L;

	private Measure partPitch = new Measure(6);
	private Measure chainline = new Measure(65);
	
	private UnitSystem unitSystem = null;
	
	Part largest = null;
	
	/**
	 * @param initialCapacity
	 */
	public PartSet(int initialCapacity) {
		super(initialCapacity);
		setUnitSystem(UnitSystem.METRIC);
	}

	/**
	 * 
	 */
	public PartSet() {
		super();
		setUnitSystem(UnitSystem.METRIC);
	}

	/**
	 * @param c
	 */
	public PartSet(Collection<? extends Part> c) {
		super(c);
	}
	
	public Part createPart(int size) {
		Part part = new Part(this, size);
		add(part);
		return part;
	}
	
	@Override
	public boolean add(Part part) {
		boolean result = super.add(part);
		if(largest == null || largest.getSize() < part.getSize()) {
			largest = part;
		}
		updateChainlines();
		return result;
	}
	
	
	/* (non-Javadoc)
	 * @see java.util.ArrayList#remove(int)
	 */
	@Override
	public Part remove(int index) {
		Part result = super.remove(index);
		updateChainlines();
		return result;
	}

	/* (non-Javadoc)
	 * @see java.util.ArrayList#remove(java.lang.Object)
	 */
	@Override
	public boolean remove(Object o) {
		boolean result = super.remove(o);
		updateChainlines();
		return result;
	}

	public void updateChainlines() {
		double outerChainline = getOuterChainline();
		for(int n=0; n < size(); n++) {
			get(n).setIndex(n);
			get(n).setChainline(outerChainline - partPitch.getValue() * n);
		}
	}

	public double getStackThickness() {
		return partPitch.getValue() * (size()-1);
	}
	
	public double getOuterChainline() {
		return chainline.getValue() + getStackThickness() / 2.0;
	}

	public double getInnerChainline() {
		return chainline.getValue() - getStackThickness() / 2.0;
	}
	
	public Part createPart() {
		return createPart(0);
	}

	/**
	 * @return Returns the partPitch.
	 */
	public Measure getPartPitch() {
		return partPitch;
	}

	/**
	 * @return Returns the chainline.
	 */
	public Measure getChainline() {
		return chainline;
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
		if(this.unitSystem != null && !this.unitSystem.equals(unitSystem)) {
			chainline.setValue(unitSystem.translate(chainline.getValue()));
			partPitch.setValue(unitSystem.translate(partPitch.getValue()));
			updateChainlines();
		}
		this.unitSystem = unitSystem;
		chainline.setFormat(unitSystem.getChainlineFormat());
		chainline.setUnit(unitSystem.getChainlineUnit());
		partPitch.setFormat(unitSystem.getPartPitchFormat());
		partPitch.setUnit(unitSystem.getPartPitchUnit());
	}

	public Part getLargest() {
		return largest;
	}

}
