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
 * Presentation specific application state.
 * Contains information abou which and how
 * the gears are to be shown to the user.
 * @author MKlemm
 *
 */
public class DriveTrainStyle {
	
	private ChainlineStatus gearVisibility = ChainlineStatus.ANY;
	private double zoomFactor = 1.0;
	private Gear selectedGear = null;
	private int viewportX = 0;
	private int viewportY = 0;
	
	/**
	 * @return Returns the gearVisibility.
	 */
	public ChainlineStatus getGearVisibility() {
		return gearVisibility;
	}

	/**
	 * Sets the ChainlineStatus that gears should have
	 * that should be displayed. Gears with a status worse
	 * than the specified one will be hidden by the GUI
	 * @param gearVisibility The gearVisibility to set.
	 */
	public void setGearVisibility(ChainlineStatus gearVisibility) {
		this.gearVisibility = gearVisibility;
	}

	/**
	 * Gets the Zoom factor of the graphical drivetrain representation
	 * @return Returns the zoomFactor.
	 */
	public double getZoomFactor() {
		return zoomFactor;
	}

	/**
	 * Sets the zoom factor of the graphical drivetrain representation
	 * @param zoomFactor The zoomFactor to set.
	 */
	public void setZoomFactor(double zoomFactor) {
		this.zoomFactor = zoomFactor;
	}

	/**
	 * Gets the gear that was selected by the user from the table view.
	 * @return Returns the selectedGear.
	 */
	public Gear getSelectedGear() {
		return selectedGear;
	}

	/**
	 * Sets the gear that was selected by the user from the table view.
	 * @param selectedGear The selectedGear to set.
	 */
	public void setSelectedGear(Gear selectedGear) {
		this.selectedGear = selectedGear;
	}

	/**
	 * Gets the X position of the viewport of the
	 * drivetrain drawing
	 * @return Returns the viewportX.
	 */
	public int getViewportX() {
		return viewportX;
	}

	/**
	 * Sets the X position of the viewport of the
	 * drivetrain drawing
	 * @param viewportX The viewportX to set.
	 */
	public void setViewportX(int viewportX) {
		this.viewportX = viewportX;
	}

	/**
	 * Gets the Y position of the viewport of the
	 * drivetrain drawing
	 * @return Returns the viewportY.
	 */
	public int getViewportY() {
		return viewportY;
	}

	/**
	 * Sets the Y position of the viewport of the
	 * drivetrain drawing
	 * @param viewportY The viewportY to set.
	 */
	public void setViewportY(int viewportY) {
		this.viewportY = viewportY;
	}
	
	
}
