/**
 * StretchControl.java
 *
 * Created: 19.01.2006 14:24:58
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

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * @author MKlemm
 *
 */
public class StretchControl extends JPanel {

	/**
	 * Generated SUID
	 */
	private static final long serialVersionUID = -3188673237727240530L;
	
	private JLabel label = null;
	private JSlider slider = null;
	private Zoomable zoomable;


	/**
	 * 
	 */
	public StretchControl(Zoomable observer) {
		super(new BorderLayout());
		this.zoomable = observer;
		initialize();
		slider.addChangeListener(new ChangeListener(){

			public void stateChanged(ChangeEvent e) {
				zoomable.setZoomFactor(getZoomFactor());
			}});
	}

	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
        label = new JLabel();
        label.setFont(new Font("Verdana", Font.PLAIN, 9));
        label.setText(Messages.getString("Stretch"));
        label.setToolTipText(Messages.getString("StretchDescription"));
        this.add(label, BorderLayout.NORTH);
        this.add(getSlider(), BorderLayout.CENTER);		
	}

	/**
	 * This method initializes slider	
	 * 	
	 * @return javax.swing.JSlider	
	 */
	private JSlider getSlider() {
		if (slider == null) {
			slider = new JSlider(100,400);
			slider.setValue(100);
			slider.setToolTipText(Messages.getString("StretchDescription"));
		}
		return slider;
	}
	
	public double getZoomFactor() {
		return slider.getValue() / 100.0;
	}

}
