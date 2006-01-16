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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.JLabel;



/**
 * @author MKlemm
 *
 */
public class SpeedInput extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JRadioButton speedButton = null;
	private JRadioButton pedallingFreqButton = null;
	private JTextField speedTextField = null;
	private JTextField cadenceTextField = null;
	private JLabel speedUnitLabel = null;

	public SpeedInput() {
		initialize();
	}

	/**
	 * This metho			this.add(getPedallingFreqButton(), null);
d initializes this
	 * 
	 */
	private void initialize() {
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		this.setLayout(new GridBagLayout());
		
		this.add(getSpeedButton(), gridBagConstraints);
		this.add(getSpeedTextField(), gridBagConstraints);
		gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
		this.add(getSpeedUnitLabel(), gridBagConstraints);
		
		gridBagConstraints.gridwidth = 1;
		this.add(getPedallingFreqButton(), gridBagConstraints);
		this.add(getCadenceTextField(), gridBagConstraints);
		gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
		this.add(new JLabel("1/min"), gridBagConstraints);
	}

	/**
	 * This method initializes speedButton	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getSpeedButton() {
		if (speedButton == null) {
			speedButton = new JRadioButton();
			speedButton.setText("Speed");
		}
		return speedButton;
	}

	/**
	 * This method initializes pedallingFreqButton	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getPedallingFreqButton() {
		if (pedallingFreqButton == null) {
			pedallingFreqButton = new JRadioButton();
			pedallingFreqButton.setText("Pedalling Frequency");
		}
		return pedallingFreqButton;
	}

	/**
	 * This method initializes speedTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getSpeedTextField() {
		if (speedTextField == null) {
			speedTextField = new JTextField();
			speedTextField.setColumns(2);
		}
		return speedTextField;
	}

	/**
	 * This method initializes cadenceTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getCadenceTextField() {
		if (cadenceTextField == null) {
			cadenceTextField = new JTextField();
			cadenceTextField.setColumns(3);
		}
		return cadenceTextField;
	}

	/**
	 * This method initializes the speedUnitLabel	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JLabel getSpeedUnitLabel() {
		if (speedUnitLabel == null) {
			speedUnitLabel = new JLabel();
			speedUnitLabel.setText("km/h");
		}
		return speedUnitLabel;
	}

}