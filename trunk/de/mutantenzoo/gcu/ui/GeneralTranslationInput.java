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

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.event.ChangeEvent;

import de.mutantenzoo.gcu.model.DriveTrain;
import de.mutantenzoo.raf.ContentChangeListener;
import de.mutantenzoo.raf.ContentPanel;
import de.mutantenzoo.raf.MeasureInput;


/**
 * @author MKlemm
 *
 */
public class GeneralTranslationInput extends ContentPanel implements ContentChangeListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private MeasureInput wheelCircInput;
	private MeasureInput wheelDiameterInput;
	private MeasureInput cadenceInput;
	private DriveTrain model;

	/**
	 * Default Constructor
	 */
	public GeneralTranslationInput(DriveTrain model) {
		super(new GridBagLayout());
		setBorder(BorderFactory.createTitledBorder(Messages.getString("General")));
		
		cadenceInput = new MeasureInput(Messages.getString("Cadence"));
		cadenceInput.createIn(this);
		cadenceInput.addContentChangeListener(this);
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor=gbc.WEST;
		gbc.fill=gbc.NONE;
		gbc.gridwidth=gbc.REMAINDER;
		JSeparator sep = new JSeparator();
		gbc.weightx = 0;
		gbc.fill = gbc.HORIZONTAL;
		add(sep, gbc);
		gbc.weightx = 0;
		gbc.fill = gbc.NONE;
		JLabel wheelSizeLabel = new JLabel(Messages.getString("WheelSize"));
		wheelSizeLabel.setAlignmentX(JLabel.LEFT_ALIGNMENT);
		add(wheelSizeLabel, gbc);

		wheelDiameterInput = new MeasureInput(Messages.getString("WheelDiameter"));
		wheelDiameterInput.createIn(this);
		wheelDiameterInput.addContentChangeListener(this);
		
		wheelCircInput = new MeasureInput(Messages.getString("WheelCircumference"));
		wheelCircInput.createIn(this);
		wheelCircInput.addContentChangeListener(this);
		
		setModel(model);
		update();
	}

		
	public void contentChanged(ChangeEvent e) {
		fireContentChanged();
	}
		
	/**
	 * @return Returns the model.
	 */
	public DriveTrain getModel() {
		return model;
	}

	/**
	 * @param model The model to set.
	 */
	public void setModel(DriveTrain model) {
		this.model = model;
		wheelCircInput.setModel(model.getWheelCirc());
		wheelDiameterInput.setModel(model.getWheelDiameter());
		cadenceInput.setModel(model.getCadence());
	}
	
	public void update() {
		wheelCircInput.update();
		wheelDiameterInput.update();
		cadenceInput.update();
	}


	public void styleChanged(ChangeEvent e) {
		fireStyleChanged();		
	}
}
