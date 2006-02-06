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

/**
 * Represents a front or rear sprocket.
 * @author MKlemm
 *
 */
public class Part implements Serializable {
	
	
	/**
	 * Generated SUID
	 */	
	private static final long serialVersionUID = 4954679568571530415L;
	
	private PartSet parent;
	private int size;
	private int index;
	private double chainline;

	/**
	 * @return Returns the size.
	 */
	public int getSize() {
		return size;
	}

	/**
	 * @param size The size to set.
	 */
	public void setSize(int size) {
		this.size = size;
		if( parent.largest == null || parent.largest.getSize() < size) {
			parent.largest = this;
		}
	}

	/**
	 * @param size
	 */
	public Part(PartSet parent, int size) {
		this.parent = parent;
		this.size = size;
	}

	/**
	 * @return Returns the chainline.
	 */
	public double getChainline() {
		return chainline;
	}

	public void setChainline(double chainline) {
		this.chainline = chainline;
	}

	public double getCirc() {
		return size * parent.getUnitSystem().getDefaultChainPitch();
	}
	
	public double getRadius() {
		return getCirc() / Math.PI / 2.0;
	}
	/**
	 * @return Returns the parent.
	 */
	public PartSet getParent() {
		return parent;
	}

	/**
	 * @return Returns the index.
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * @param index The index to set.
	 */
	public void setIndex(int index) {
		this.index = index;
	}
}
