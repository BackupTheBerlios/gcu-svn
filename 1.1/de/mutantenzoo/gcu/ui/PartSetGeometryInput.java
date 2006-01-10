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

import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.event.ChangeEvent;

import de.mutantenzoo.gcu.model.PartSet;
import de.mutantenzoo.raf.ContentChangeListener;
import de.mutantenzoo.raf.ContentPanel;
import de.mutantenzoo.raf.MeasureInput;

public class PartSetGeometryInput extends ContentPanel implements ContentChangeListener {

	private PartSet model;
	
	private MeasureInput chainlineInput;
	private MeasureInput partDistanceInput;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PartSetGeometryInput(PartSet model, String title) {
		super(new GridBagLayout());
		setBorder(BorderFactory.createTitledBorder(title));

		chainlineInput = new MeasureInput(Messages.getString("Chainline"), model.getChainline());
		chainlineInput.createIn(this);
		chainlineInput.addContentChangeListener(this);
		
		partDistanceInput = new MeasureInput(Messages.getString("PartDistance"), model.getPartPitch());
		partDistanceInput.createIn(this);
		partDistanceInput.addContentChangeListener(this);
		
		
		setModel(model);
		update();
	}

	/**
	 * @return Returns the model.
	 */
	public PartSet getModel() {
		return model;
	}

	/**
	 * @param model The model to set.
	 */
	public void setModel(PartSet model) {
		this.model = model;
		chainlineInput.setModel(model.getChainline());
		partDistanceInput.setModel(model.getPartPitch());
	}
	
	public void update() {
		chainlineInput.update();
		partDistanceInput.update();
	}

	public void contentChanged(ChangeEvent e) {
		model.updateChainlines();
		fireContentChanged();
	}

	public void styleChanged(ChangeEvent e) {
		fireStyleChanged();
	}
	
	

}
