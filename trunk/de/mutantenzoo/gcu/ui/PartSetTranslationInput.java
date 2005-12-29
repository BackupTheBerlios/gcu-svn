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

import static java.awt.GridBagConstraints.*;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;

import de.mutantenzoo.gcu.model.PartSet;
import de.mutantenzoo.raf.ContentChangeListener;
import de.mutantenzoo.raf.ContentPanel;

public class PartSetTranslationInput extends ContentPanel implements ContentChangeListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final ImageIcon addIcon = new ImageIcon(PartSetTranslationInput.class.getResource("/toolbarButtonGraphics/general/Add16.gif")); //$NON-NLS-1$
	private static final ImageIcon removeIcon = new ImageIcon(PartSetTranslationInput.class.getResource("/toolbarButtonGraphics/general/Delete16.gif")); //$NON-NLS-1$

	private static final GridBagConstraints gbc = new GridBagConstraints();
	
	private PartSet model = null;
	private JTable partTable;
	private PartTableModel tableModel = new PartTableModel();
	
	public PartSetTranslationInput(PartSet model, String title) {
		super(new GridBagLayout());
		this.model = model;
		setBorder(BorderFactory.createTitledBorder(title));
		
		partTable = new JTable(tableModel);
		partTable.setDefaultRenderer(Icon.class, new IconRenderer());
		partTable.setRowSelectionAllowed(false);
		partTable.getColumnModel().getColumn(0).setPreferredWidth(removeIcon.getIconWidth());
		partTable.getColumnModel().getColumn(1).setPreferredWidth(removeIcon.getIconWidth()+8);
		partTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		partTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

			public void valueChanged(ListSelectionEvent e) {
				if(!e.getValueIsAdjusting()
					&& partTable.getSelectedRow() >= 0
					&& partTable.getSelectedColumn() == 0
					&& partTable.getSelectedRow() < getModel().size()) {
					removePart(partTable.getSelectedRow());
				}
			}
		
		});
		gbc.gridwidth = REMAINDER;
		gbc.anchor = NORTHWEST;
		gbc.fill = NONE;
		gbc.weighty = 0;
		add(partTable.getTableHeader(), gbc);
		gbc.weighty = 0;
		add(partTable, gbc);
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
	}
	
	
	public void update() {
		tableModel.fireTableDataChanged();
	}
		
	public void removePart(int index) {
		model.remove(index);
		fireContentChanged();
	}
		
	public void contentChanged(ChangeEvent e) {
		fireContentChanged();
	}

	public void styleChanged(ChangeEvent e) {
		fireStyleChanged();		
	}

	private class PartTableModel extends AbstractTableModel {

		/**
		 * 
		 */
		private static final long serialVersionUID = 2884130720683351671L;
	
		public int getColumnCount() {
			return 3;
		}

		public int getRowCount() {
			return model.size()+1;
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			if(columnIndex == 2) {
				if(rowIndex < model.size()) {
					int partSize = model.get(rowIndex).getSize();
					if(partSize > 0) {
						return partSize;
					} else {
						return null;
					}
				} else {
					return null;
				}
			} else if(columnIndex == 1) {
				if(rowIndex == model.size()) {
					return null;
				} else {
					return rowIndex +1;
				}
			} else {
				if(rowIndex == model.size()) {
					return addIcon;
				} else {
					return removeIcon;
				}
			}
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
		 */
		@Override
		public Class<?> getColumnClass(int columnIndex) {
			if(columnIndex == 0) {
				return Icon.class;
			} else {
				return Integer.class;
			}
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
		 */
		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return columnIndex > 1;
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.AbstractTableModel#setValueAt(java.lang.Object, int, int)
		 */
		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			int size;
			if(aValue == null) {
				size = 0;
			} else {
				size = ((Integer)aValue).intValue();
			}
			
			if(rowIndex < model.size()) {
				model.get(rowIndex).setSize(size);
			} else {
				model.createPart(size);
			}
			fireContentChanged();
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
		 */
		@Override
		public String getColumnName(int column) {
			switch(column) {
			case 1: return Messages.getString("NumberAbbrev");
			case 2: return Messages.getString("ToothCount");
			default: return "";
			}
		}	
		
		
	}
	
	private class IconRenderer implements TableCellRenderer {

		private JLabel label = null;
		
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			Icon icon = (Icon)value;
			if(label == null) {
				label = new JLabel(icon);
			} else {
				label.setIcon(icon);
			}
			return label;
		}
		
	}
	
}
