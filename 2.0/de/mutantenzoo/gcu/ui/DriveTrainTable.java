/**
 * DriveTrainTable.java
 *
 * Created: 13.12.2005 16:48:20
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

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.mutantenzoo.gcu.model.DriveTrain;
import de.mutantenzoo.gcu.model.DriveTrainStyle;
import de.mutantenzoo.gcu.model.Gear;
import de.mutantenzoo.raf.ContentAdapter;
import de.mutantenzoo.raf.ContentChangeListener;
import de.mutantenzoo.raf.ContentEventSource;
import de.mutantenzoo.raf.View;

/**
 * @author MKlemm
 *
 */
public class DriveTrainTable extends JScrollPane implements ContentEventSource, View<DriveTrain>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1651071670040914327L;
	private JTable displayTable = null;

	private TableAdapter adapter;

	private ContentAdapter contentAdapter = new ContentAdapter();
	
	/**
	 * Initializes the DriveTrainTable
	 * @param model The model
	 * @param style The style 
	 */
	public DriveTrainTable(DriveTrain model, DriveTrainStyle style) {
		super();
		adapter = new TableAdapter(model, style);
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
        this.setViewportView(getDisplayTable());
	}

	/**
	 * This method initializes displayTable	
	 * 	
	 * @return javax.swing.JTable	
	 */
	private JTable getDisplayTable() {
		if (displayTable == null) {
			TableSorter sorter = new TableSorter(adapter);
			displayTable = new JTable(sorter);
			sorter.setTableHeader(displayTable.getTableHeader());
			displayTable.setDefaultRenderer(GearRenderer.Model.class, new GearRenderer());
	        displayTable.getColumnModel().getColumn(0).setPreferredWidth(20);
	        displayTable.getColumnModel().getColumn(1).setPreferredWidth(34);
	        displayTable.getColumnModel().getColumn(2).setPreferredWidth(40);
	        displayTable.getColumnModel().getColumn(3).setPreferredWidth(40);
	        displayTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	        displayTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

				public void valueChanged(ListSelectionEvent e) {
					if( ! e.getValueIsAdjusting() ) {
						ListSelectionModel lsm = (ListSelectionModel)e.getSource();
						int selectedRow = lsm.getMinSelectionIndex();
						if(lsm.isSelectionEmpty()) {
							adapter.getStyle().setSelectedGear(null);
							contentAdapter.fireStyleChanged();
						} else {
							TableSorter sorter = (TableSorter)displayTable.getModel();
							selectedRow = sorter.modelIndex(selectedRow);
							adapter.getStyle().setSelectedGear(adapter.getModel().getGear(selectedRow).clone());
							contentAdapter.fireStyleChanged();
						}
					}
					
				}
	        	
	        });
		}
		return displayTable;
	}

	public void update() {
		adapter.update();
	}

	public void setSelection(Gear gear) {
		adapter.getStyle().setSelectedGear(gear);
		contentAdapter.fireStyleChanged();
	}
	
	/**
	 * @return Returns the model.
	 */
	public DriveTrain getModel() {
		return adapter.getModel();
	}

	/**
	 * @param model The model to set.
	 */
	public void setModel(DriveTrain model) {
		adapter.setModel(model);
	}

	/**
	 * @return Returns the style.
	 */
	public DriveTrainStyle getStyle() {
		return adapter.getStyle();
	}

	/**
	 * @param style The style to set.
	 */
	public void setStyle(DriveTrainStyle style) {
		adapter.setStyle(style);
	}

	public void addContentChangeListener(ContentChangeListener contentChangeListener) {
		contentAdapter.addContentChangeListener(contentChangeListener);
	}

	public void dataChanged() {
		adapter.fireTableDataChanged();
	}
	
	public void structureChanged() {
		adapter.fireTableStructureChanged();
	}

	public void print(Graphics2D g, PageFormat pageFormat) {
		Dimension origSize = displayTable.getSize();
		displayTable.setSize((int)pageFormat.getImageableWidth(), (int)origSize.getHeight());
		displayTable.doLayout();
		displayTable.getTableHeader().setSize(displayTable.getWidth(),displayTable.getTableHeader().getHeight());
		displayTable.getTableHeader().paint(g);
		g.translate(0,1+displayTable.getTableHeader().getHeight());
		displayTable.paint(g);
		g.translate(0, displayTable.getHeight());
		displayTable.setSize(origSize);
		displayTable.getTableHeader().setSize(origSize.width, displayTable.getTableHeader().getHeight());
	}

}
