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
package de.mutantenzoo.gcu.ui;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

/**
 * @author MKlemm
 *
 */
public class MainFrameListener implements WindowListener {

	private Main main;
	
	/**
	 * 
	 */
	public MainFrameListener(Main main) {
		this.main = main;
	}

	public void windowOpened(WindowEvent e) {
		// Do nothing		
	}

	public void windowClosing(WindowEvent e) {
		main.exit();		
	}

	public void windowClosed(WindowEvent e) {
		// Do nothing
	}

	public void windowIconified(WindowEvent e) {
		// Do nothing
	}

	public void windowDeiconified(WindowEvent e) {
		// Do nothing
	}

	public void windowActivated(WindowEvent e) {
		// Do nothing
	}

	public void windowDeactivated(WindowEvent e) {
		// Do nothing
	}

}
