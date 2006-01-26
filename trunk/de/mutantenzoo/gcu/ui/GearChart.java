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
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.util.TreeSet;

import javax.swing.JComponent;
import javax.swing.Scrollable;

import de.mutantenzoo.gcu.model.DriveTrain;
import de.mutantenzoo.gcu.model.Gear;

/**
 * @author MKlemm
 *
 */
public class GearChart extends JComponent implements Scrollable {

	/**
	 * Generated SUID
	 */
	private static final long serialVersionUID = -1444852719712673897L;
	private static final GeneralPath triangle = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
	private static final AffineTransform ROTATE90_TRANSFORM = new AffineTransform(0,-1,1,0,0,0);
	
	private Dimension preferredScrollableViewportSize = new Dimension();
	private DriveTrainComparisonView parent;
	private int textSpacing;
	
	/**
	 * Default Constructor
	 */
	public GearChart(DriveTrainComparisonView parent) {
		super();
		this.parent = parent;
		triangle.moveTo(0,0);
		triangle.lineTo(-5f,5f);
		triangle.lineTo(5f,5f);
		triangle.closePath();
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	@Override
	protected void paintComponent(Graphics g1) {
		Graphics2D g = (Graphics2D)g1;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		prepareTransform(g);
		TextLayout tl = new TextLayout("WW:WW", getFont(), new FontRenderContext(new AffineTransform(), true, true));
		textSpacing = (int)(tl.getBounds().getHeight() + 1);
		drawAxes(g);
		drawDriveTrains(g);
	}

	private void drawAxes(Graphics2D g) {
		Stroke origStroke = g.getStroke();
		Color origColor = g.getColor();
		g.setColor(Color.GRAY);
		g.setStroke(new BasicStroke(0.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f, new float[]{2f,2f}, 2f));
		double step = parent.getTickStep();
		for(double n=0; n<parent.getUnitSystem().getMaxDevelopment(); n += step ) {
			Line2D tick = new Line2D.Double(parent.map(n), 0, parent.map(n), getHeight()-parent.getTopMargin());
			g.draw(tick);
		}
		g.setStroke(origStroke);
		g.setColor(origColor);
	}

	
	private void drawDriveTrains(Graphics2D g) {
		int y = -parent.getSpacing();
		for(DriveTrain model : parent.getDriveTrains().keySet()) {
			drawGears(g, y+=parent.getSpacing(), model);
		}
	}
	
	private void drawGears(Graphics2D g, int y, DriveTrain model) {
		Line2D axis = new Line2D.Double(0, y, parent.map(parent.getUnitSystem().getMaxDevelopment()), y);
		Stroke origStroke = g.getStroke();
		g.setStroke(new BasicStroke(0.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f, new float[]{2f,2f}, 2f));
		g.setColor(Color.BLUE);
		g.draw(axis);
		g.setColor(Color.BLACK);
		g.setStroke(origStroke);
		TreeSet<Distributor.Item> set = new TreeSet<Distributor.Item>();
		for(int n=0; n<model.getGearCount(); n++) {
			Gear gear = model.getGear(n);
			if(parent.getChainlineStatus().has(gear.getChainlineStatus())) {
				set.add(new Distributor.Item(gear.getChainwheel().getSize()+":"+gear.getSprocket().getSize(), development(gear), false));
				drawGear(g, y, gear);
			}
		}
		Distributor d = new Distributor(textSpacing, set);
		for(int n=0; n<d.size(); n++) {
			Distributor.Item item = d.get(n);
			drawGearLabel(g, item, y);
		}
	}

	private void drawGear(Graphics2D g, int y, Gear gear) {
		g.setColor(GearRenderer.getColorFromChainlineStatus(gear.getChainlineStatus()));
		int devel = development(gear);
		AffineTransform origTransform = g.getTransform();
		g.translate(devel, y+4);
		g.draw(triangle);
		g.fill(triangle);
		Line2D line = new Line2D.Float(0, -4, 0, 0);
		g.setColor(Color.BLACK);
		g.draw(line);
		g.setTransform(origTransform);
	}

	private void drawGearLabel(Graphics2D g, Distributor.Item item, int y) {
		AffineTransform origTransform = g.getTransform();
		g.translate(item.exactValue, y+4);
		g.setColor(Color.BLACK);
		TextLayout tl = new TextLayout(item.label, getFont(), g.getFontRenderContext());
		g.transform(ROTATE90_TRANSFORM);
		tl.draw(g, -tl.getAdvance()-6f, (float)(tl.getBounds().getHeight()) / 2f);
		g.setTransform(origTransform);
	}

	private int development(Gear gear) {
		if(gear.getParent().getUnitSystem().equals(parent.getUnitSystem())) {
			return parent.map(gear.getDevelopment().getValue());
		} else {
			return parent.map(parent.getUnitSystem().translateDevelopment(gear.getDevelopment().getValue()));
		}
	}

	private void prepareTransform(Graphics2D g) {
		g.translate(0, parent.getTopMargin());
	}
	

	public Dimension getPreferredScrollableViewportSize() {
		preferredScrollableViewportSize.height = getPreferredSize().height;
		preferredScrollableViewportSize.width = parent.getGearViewportWidth();
		return preferredScrollableViewportSize;
	}

	
	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
		return 1;
	}

	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
		return parent.map(parent.getTickStep());
	}

	public boolean getScrollableTracksViewportWidth() {
		return false;
	}

	public boolean getScrollableTracksViewportHeight() {
		return true;
	}
	
	
	/**
	 * @return Returns the preferredSize.
	 */
	/*
	@Override
	public Dimension getPreferredSize() {
		PREFERRED_SIZE.width = getWidth();
		PREFERRED_SIZE.height = getHeight();
		return PREFERRED_SIZE;
	}
	
	@Override
	public Dimension getSize() {
		return getPreferredSize();
	}
	
	@Override
	public int getWidth() {
		return parent.getViewWidth();
	}
	
	@Override
	public int getHeight() {
		return parent.getViewHeight();
	}

	@Override
	public Rectangle getBounds() {
		return new Rectangle(getLocation(), PREFERRED_SIZE);
	}
	*/
}
