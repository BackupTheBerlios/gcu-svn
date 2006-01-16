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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import de.mutantenzoo.gcu.model.ChainlineStatus;

public class GearRenderer extends JComponent implements TableCellRenderer {

	private double fillRatio=0.5;
	private Color color = Color.BLUE;
	private Color selectedColor = null;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public GearRenderer() {
		super();
	}

	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	@Override
	protected void paintComponent(Graphics g) {
		Dimension d = getSize();
		g.setColor(color);
		int xOff = (int) (d.width * fillRatio);
		g.fillRect(0, 0, xOff, d.height );
		if(selectedColor != null) {
			g.setColor(selectedColor);
			g.fillRect(xOff, 0, d.width, d.height);
		}
	}

	/**
	 * @return Returns the color.
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * @param color The color to set.
	 */
	public void setColor(Color color) {
		this.color = color;
	}

	/**
	 * @return Returns the fillRatio.
	 */
	public double getFillRatio() {
		return fillRatio;
	}

	/**
	 * @param fillRatio The fillRatio to set.
	 */
	public void setFillRatio(double fillRatio) {
		this.fillRatio = fillRatio;
	}

	/* (non-Javadoc)
	 * @see javax.swing.JComponent#getPreferredSize()
	 */
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(300,10);
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		if (value != null) {
			Model model = (Model)value;
			Rectangle rect = table.getCellRect(row, column, false);
			setSize(rect.width, rect.height);
			setFillRatio(model.fillRatio);
			setColor(getColorFromChainlineStatus(model.status));
			if(isSelected) {
				selectedColor = table.getSelectionBackground();
			} else {
				selectedColor = null;
			}
			return this;
		} else {
			return null;
		}
	}
	
	public static class Model {
		public ChainlineStatus status;
		public double fillRatio;
		public Model(ChainlineStatus status, double fillRatio) {
			this.status = status;
			this.fillRatio = fillRatio;
		}
		
		@Override public String toString() {
			Color c = getColorFromChainlineStatus(status);
			return "<div style=\"width: "+(int)(fillRatio*100.0)+"%; height: 100%; background-color: rgb("+c.getRed()+","+c.getGreen()+","+c.getBlue()+");\" />";
		}
	}

	public static Color getColorFromChainlineStatus(ChainlineStatus chainlineStatus) {
		switch(chainlineStatus) {
		case GOOD: return Color.GREEN;
		case OK: return Color.YELLOW;
		case BAD: return Color.RED;
		default: return Color.BLUE;
		}
	}
}
