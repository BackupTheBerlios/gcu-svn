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
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.util.HashSet;

import javax.swing.JComponent;

import de.mutantenzoo.gcu.model.ChainlineStatus;
import de.mutantenzoo.gcu.model.DriveTrain;
import de.mutantenzoo.gcu.model.Gear;
import de.mutantenzoo.gcu.units.UnitSystem;

/**
 * @author MKlemm
 *
 */
public class DriveTrainComparisonView extends JComponent implements Printable, GearView {

	/**
	 * Generated SUID
	 */
	private static final long serialVersionUID = -1444852719712673897L;
	private static final Font font = new Font("Verdana", Font.PLAIN, 8);
	private static final double LEFT_MARGIN = 120.0;
	private static final double RIGHT_MARGIN = 5.0;
	private static final double BOTTOM_MARGIN = 40.0;
	private static final double SPACING = 30.0;
	private static final GeneralPath triangle = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
	
	private Dimension preferredSize = new Dimension(600,400);
	private UnitSystem unitSystem = UnitSystem.METRIC;
	private HashSet<DriveTrain> driveTrains = new HashSet<DriveTrain>();
	private ChainlineStatus chainlineStatus = ChainlineStatus.ALL;
	
	/**
	 * Default Constructor
	 */
	public DriveTrainComparisonView() {
		super();
		triangle.moveTo(0,0);
		triangle.lineTo(-5f,5f);
		triangle.lineTo(5f,5f);
		triangle.closePath();
	}
	
	public void addModel(DriveTrain model) {
		driveTrains.add(model);
	}
	
	public void removeModel(DriveTrain model) {
		driveTrains.remove(model);
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
		drawDriveTrains(g);
	}

	private void drawAxes(Graphics2D g) {
		Line2D xAxis = new Line2D.Double(0, 0, map(unitSystem.getMaxDevelopment()), 0);
		g.draw(xAxis);
		Line2D yAxis = new Line2D.Double(0, 0, 0, -SPACING * (1+driveTrains.size()));
		g.draw(yAxis);
		for(double n=0; n<unitSystem.getMaxDevelopment(); n += 1.0 ) {
			Line2D tick = new Line2D.Double(map(n), -5, map(n), 5);
			g.draw(tick);
			TextLayout tl = new TextLayout(unitSystem.getDevelopmentFormat().format(n), font, g.getFontRenderContext());
			tl.draw(g, (float)(map(n)-tl.getBounds().getWidth() / 2.0), (float)(6.0 + tl.getBounds().getHeight()));
		}
	}
	
	private double map(double value) {
		double fact = (getBounds().getWidth() - LEFT_MARGIN - RIGHT_MARGIN) / unitSystem.getMaxDevelopment();
		return value * fact;
	}
	
	private void drawDriveTrains(Graphics2D g) {
		double y = 0;
		for(DriveTrain model : driveTrains) {
			drawGears(g, y-=SPACING, model);
		}
	}
	
	private void drawGears(Graphics2D g, double y, DriveTrain model) {
		TextLayout tl = new TextLayout(model.getName(), font, g.getFontRenderContext());
		
		tl.draw(g, (float)(-tl.getBounds().getWidth()), (float)(y+tl.getBounds().getHeight()+4.0));
		
		Line2D axis = new Line2D.Double(-LEFT_MARGIN+5.0, y, map(unitSystem.getMaxDevelopment()), y);
		Stroke origStroke = g.getStroke();
		g.setStroke(new BasicStroke(0.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f, new float[]{2f,2f}, 2f));
		g.setColor(Color.BLUE);
		g.draw(axis);
		g.setColor(Color.BLACK);
		g.setStroke(origStroke);
		for(int n=0; n<model.getGearCount(); n++) {
			Gear gear = model.getGear(n);
			if(chainlineStatus.has(gear.getChainlineStatus())) {
				drawGear(g, y, gear);
			}
		}
	}

	private void drawGear(Graphics2D g, double y, Gear gear) {
		g.setColor(GearRenderer.getColorFromChainlineStatus(gear.getChainlineStatus()));
		AffineTransform origTransform = g.getTransform();
		g.translate(map(gear.getDevelopment().getValue()), y);
		g.draw(triangle);
		g.setColor(Color.BLACK);
		TextLayout tl = new TextLayout(gear.getChainwheel().getSize()+":"+gear.getSprocket().getSize(), font, g.getFontRenderContext());
		AffineTransform textTransform = AffineTransform.getTranslateInstance(tl.getBounds().getHeight() / 2.0, 6.0+tl.getBounds().getWidth());
		textTransform.concatenate(new AffineTransform(0,-1,1,0,0,0));
		Shape t = tl.getOutline(new AffineTransform(textTransform));
		g.fill(t);
		g.setTransform(origTransform);
	}


	private void prepareTransform(Graphics2D g) {
		g.translate(LEFT_MARGIN, getBounds().getHeight() - BOTTOM_MARGIN);
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

	public void removeAllModels() {
		driveTrains.clear();
	}

	public void export() {
		// TODO implementation
	}
	
	public void print() {
		// TODO implementation
	}

	public void viewAllGears() {
		chainlineStatus = ChainlineStatus.ALL;
		repaint();
	}

	public void viewOKGears() {
		chainlineStatus = ChainlineStatus.USABLE;
	}

	public void viewGoodGears() {
		chainlineStatus = ChainlineStatus.GOOD;
		repaint();
	}

	public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
		return 0;
	}

	public void setUnitSystem(UnitSystem unitSystem) {
		this.unitSystem = unitSystem;
		repaint();
	}
	
	
}
