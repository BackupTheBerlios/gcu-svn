/**
 * TableAdapter.java
 *
 * Created: 13.12.2005 17:28:12
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

import java.text.DecimalFormat;

import javax.swing.table.AbstractTableModel;

import de.mutantenzoo.gcu.model.Gear;
import de.mutantenzoo.gcu.model.DriveTrain;
import de.mutantenzoo.gcu.model.DriveTrainStyle;
import de.mutantenzoo.raf.View;

/**
 * @author MKlemm
 *
 */
public class TableAdapter extends AbstractTableModel implements View<DriveTrain> {


	/**
	 * Generated serialVersion UID
	 */
	private static final long serialVersionUID = 3058124611712695494L;

	private static final DecimalFormat transFormat = new DecimalFormat("###.##"); //$NON-NLS-1$

	private String[] columnNames = new String[5];
	private DriveTrain model;
	private DriveTrainStyle style;

	private int rowCount;
	private Object[][] cache = null;
	
	/**
	 * 
	 */
	public TableAdapter(DriveTrain model, DriveTrainStyle style) {
		setModel(model);
		setStyle(style);
		update();
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	public int getRowCount() {
		return rowCount;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	public int getColumnCount() {
		return 5;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnName(int)
	 */
	@Override
	public String getColumnName(int columnIndex) {
		return columnNames[columnIndex];
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnClass(int)
	 */
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if(columnIndex < 4) {
			return String.class;
		} else {
			return GearRenderer.Model.class;
		}
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	public Object getValueAt(int rowIndex, int columnIndex) {
		return cache[columnIndex][rowIndex];
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
	}

	/**
	 * @return Returns the style.
	 */
	public DriveTrainStyle getStyle() {
		return style;
	}

	/**
	 * @param style The style to set.
	 */
	public void setStyle(DriveTrainStyle style) {
		this.style = style;
	}

	private void rebuildCache() {
		int gearCount = model.getGearCount();
		cache = new Object[5][gearCount];
		rowCount = 0;
		for(int n=0; n<gearCount; n++) {
			Gear g = model.getGear(n);
			if(style.getGearVisibility().has(g.getChainlineStatus())) {
				cache[0][rowCount] = g.getChainwheel().getSize()+":"+g.getSprocket().getSize();
				cache[1][rowCount] = transFormat.format(g.getTrans());
				cache[2][rowCount] = g.getDevelopment().getStringValue();
				cache[3][rowCount] = g.getSpeed().getStringValue();
				cache[4][rowCount] = new GearRenderer.Model(g.getChainlineStatus(), g.getDevelopment().getValue() / model.getUnitSystem().getMaxDevelopment());
				rowCount++;
			}
		}
		
	}

	public void update() {
		String developmentUnit = " ("+model.getUnitSystem().getDevelopmentUnit()+")"; //$NON-NLS-1$ //$NON-NLS-2$
		String speedUnit = " ("+model.getUnitSystem().getSpeedUnit()+")"; //$NON-NLS-1$ //$NON-NLS-2$

		columnNames[0] = Messages.getString("GearOutput.3");
		columnNames[1] = Messages.getString("GearOutput.2");
		columnNames[2] = Messages.getString(model.getUnitSystem().getDevelopmentNotion())+developmentUnit;
		columnNames[3] = Messages.getString("Speed")+speedUnit;
		columnNames[4] = "";
		
		rebuildCache();
		
	}

}
