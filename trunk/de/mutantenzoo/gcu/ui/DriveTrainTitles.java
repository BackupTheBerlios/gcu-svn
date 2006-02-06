/**
 * DriveTrainComparisonView.java
 *
 * Created: 11.01.2006 16:50:49
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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.font.TextLayout;
import java.awt.geom.Line2D;

import javax.swing.JComponent;

import de.mutantenzoo.gcu.model.DriveTrain;

/**
 * Component that draw the names of the drivetrains
 * in rows left to the comparison view.
 * Component of the @see de.mutantenzoo.gcu.ui.DriveTrainComparisonView
 * @author MKlemm
 *
 */
public class DriveTrainTitles extends JComponent {

	/**
	 * Generated SUID
	 */
	private static final long serialVersionUID = -1444852719712673897L;
	private static final Dimension PREFERRED_SIZE = new Dimension();
	private static final int LEFT_MARGIN = 5;
	
	private DriveTrainComparisonView parent;
	
	
	/**
	 * Constructor
	 * @param parent The parent view of this view
	 */
	public DriveTrainTitles(DriveTrainComparisonView parent) {
		super();
		this.parent = parent;
		setOpaque(true);
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	@Override
	protected void paintComponent(Graphics g1) {
		Graphics2D g = (Graphics2D)g1;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		prepareTransform(g);
		drawDriveTrains(g);
	}

	/**
	 * Draws the names of the drivetrains
	 * @param g Graphics context to draw to
	 */
	private void drawDriveTrains(Graphics2D g) {
		int y = -parent.getSpacing(); 
		for(DriveTrain model : parent.getDriveTrains().keySet()) {
			drawDriveTrainName(g, y+=parent.getSpacing(), model);
		}
	}
	
	/**
	 * Draws the name of a drivetrain
	 * @param g Graphics context to draw to
	 * @param y y-Position of the name
	 * @param model DriveTrain the name of which should be drawn
	 */
	private void drawDriveTrainName(Graphics2D g, int y, DriveTrain model) {
		TextLayout tl = parent.getDriveTrains().get(model);
		tl.draw(g, getWidth()-tl.getAdvance()-LEFT_MARGIN, y+tl.getAscent()+4);
		Line2D axis = new Line2D.Double(LEFT_MARGIN , y, getWidth(), y);
		Stroke origStroke = g.getStroke();
		g.setStroke(new BasicStroke(0.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f, new float[]{2f,2f}, 2f));
		g.setColor(Color.BLUE);
		g.draw(axis);
		g.setColor(Color.BLACK);
		g.setStroke(origStroke);
	}

	/**
	 * Prepares the transformation matrix of the graphics context.
	 * @param g Graphics context to set the transformation for
	 */
	private void prepareTransform(Graphics2D g) {
		g.translate(0, parent.getTopMargin());
	}
	

	/**
	 * @return Returns the preferredSize.
	 */
	@Override
	public Dimension getPreferredSize() {
		PREFERRED_SIZE.width = getWidth();
		PREFERRED_SIZE.height = getHeight();
		return PREFERRED_SIZE;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see java.awt.Component#getSize()
	 */
	@Override
	public Dimension getSize() {
		return getPreferredSize();
	}
	
	/*
	 *  (non-Javadoc)
	 * @see java.awt.Component#getWidth()
	 */
	@Override
	public int getWidth() {
		return parent.getNameViewportWidth();
	}
	
	/*
	 *  (non-Javadoc)
	 * @see java.awt.Component#getHeight()
	 */
	@Override
	public int getHeight() {
		return parent.getViewHeight();
	}

	/*
	 *  (non-Javadoc)
	 * @see java.awt.Component#getBounds()
	 */
	@Override
	public Rectangle getBounds() {
		return new Rectangle(getLocation(), PREFERRED_SIZE);
	}
}
