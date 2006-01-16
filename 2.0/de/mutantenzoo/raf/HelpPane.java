/**
 * HelpPane.java
 *
 * Created: 22.12.2005 18:20:31
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
package de.mutantenzoo.raf;

import java.awt.Dimension;
import java.io.IOException;

import javax.swing.JEditorPane;
import javax.swing.JScrollPane;

import de.mutantenzoo.gcu.ui.Messages;

/**
 * @author MKlemm
 *
 */
public class HelpPane extends JScrollPane {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1696001687455483558L;

	private JEditorPane editorPane;
	
	/**
	 * @param view
	 */
	public HelpPane(String resourceName) {
		super();
		try {
			editorPane = new JEditorPane(getClass().getResource(resourceName));
		} catch (IOException e) {
			editorPane = new JEditorPane("text/html",Messages.getString("Help.NotAvailable.Message"));
		}
		setViewportView(editorPane);

		setPreferredSize(new Dimension(640,480));
	}



}
