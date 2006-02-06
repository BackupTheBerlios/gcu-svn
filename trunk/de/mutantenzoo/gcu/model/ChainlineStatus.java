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

/**
 * Enum that represnts the possible values
 * for the status of a chainline
 * @author mklemm
 *
 */
public enum ChainlineStatus {
	
	/**
	 * The chainline was selected by the user
	 */
	SELECTED(0),
	
	/**
	 * The chainline is "good"
	 */
	GOOD(1),
	
	/**
	 * The chainline is sub-optimal, but OK
	 */
	OK(2),
	
	/**
	 * The chailine is bad (unusable gear combination)
	 */
	BAD(4),
	
	/**
	 * The chainline is either good or OK
	 */
	USABLE(3),
	
	/**
	 * The chainline is in any
	 * of the above conditions
	 */
	ANY(7);
	
	private int value;
	
	/**
	 * ChainlineStatus constants are int values internally.
	 * @param value The int value of the constant
	 */
	private ChainlineStatus(int value) {
		this.value = value;
	}
	
	/**
	 * Gets the int value of a constant
	 * @return The int value
	 */
	private int value() {
		return value;
	}
	
	/**
	 * Checks whether this chainline status includes
	 * the specifed one (Logical AND operation).
	 * @param cs The status to check for
	 * @return "true" if this status includes "cs", "false" otherwise.
	 */
	public boolean has(ChainlineStatus cs) {
		return (value & cs.value()) > 0;
	}
}
