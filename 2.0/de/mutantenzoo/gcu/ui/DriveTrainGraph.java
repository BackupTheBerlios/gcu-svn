/**
 * DriveTrainGraph.java
 *
 * Created: 09.01.2006 17:54:29
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
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.TextLayout;
import java.awt.geom.Line2D;

import javax.swing.JComponent;

import de.mutantenzoo.gcu.model.DriveTrain;
import de.mutantenzoo.gcu.model.Gear;

/**
 * @author MKlemm
 *
 */
public class DriveTrainGraph extends JComponent {

	/**
	 * Generated SUID 
	 */
	private static final long serialVersionUID = 5301441247402795157L;
	
	private static final Font font = new Font("Verdana", Font.PLAIN, 8);

	private DriveTrain model;
	
	private Dimension preferredSize = new Dimension(600,400);
	
	/**
	 * Default constructor 
	 */
	public DriveTrainGraph(DriveTrain model) {
		setModel(model);
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

	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	@Override
	protected void paintComponent(Graphics g1) {
		Graphics2D g = (Graphics2D)g1;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		prepareTransform(g);
		drawAxes(g);
		drawGears(g);
	}

	private void drawGears(Graphics2D g) {
		for(int n=0; n<model.getGearCount(); n++) {
			drawGear(g, model.getGear(n));
		}
	}

	private void drawGear(Graphics2D g, Gear gear) {
		g.setColor(GearRenderer.getColorFromChainlineStatus(gear.getChainlineStatus()));
		Line2D l = new Line2D.Double(gear.getDevelopment().getValue(), 0, gear.getDevelopment().getValue(), 20.0);
		g.draw(l);		
	}

	private void drawAxes(Graphics2D g) {
		Line2D xAxis = new Line2D.Double(0, 0, model.getUnitSystem().getMaxDevelopment(), 0);
		g.draw(xAxis);
		for(double n=0; n<model.getUnitSystem().getMaxDevelopment(); n += model.getUnitSystem().getMaxDevelopment()/10.0 ) {
			Line2D tick = new Line2D.Double(n, -10, n, 10);
			g.draw(tick);
			TextLayout tl = new TextLayout(Double.toString(n), font, g.getFontRenderContext());
			
			tl.draw(g, (float)n, -20f);
		}
	}

	private void prepareTransform(Graphics2D g) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @return Returns the preferredSize.
	 */
	@Override
	public Dimension getPreferredSize() {
		return preferredSize;
	}

	/**
	 * @param preferredSize The preferredSize to set.
	 */
	@Override
	public void setPreferredSize(Dimension preferredSize) {
		this.preferredSize = preferredSize;
	}

	
}
