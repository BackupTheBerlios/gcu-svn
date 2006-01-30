/**
 * DriveTrainDrawing.java
 *
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

import static java.lang.Math.max;
import static java.lang.Math.min;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.print.PageFormat;

import javax.swing.event.MouseInputListener;

import de.mutantenzoo.gcu.model.Gear;
import de.mutantenzoo.gcu.model.DriveTrain;
import de.mutantenzoo.gcu.model.DriveTrainStyle;
import de.mutantenzoo.gcu.model.Part;
import de.mutantenzoo.raf.ContentComponent;
import de.mutantenzoo.raf.graphics.MeasureDrawing;

/**
 * @author MKlemm
 *
 */
public class DriveTrainDrawing extends ContentComponent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private DriveTrain model;
	private DriveTrainStyle style;
	private Dimension preferredSize = new Dimension(150,480);
	
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

	/**
	 * 
	 */
	public DriveTrainDrawing(DriveTrain model, DriveTrainStyle style) {
		this.model= model;
		this.style = style;
		PanListener panListener = new PanListener();
		addMouseListener(panListener);
		addMouseMotionListener(panListener);
		addMouseWheelListener(panListener);
		setToolTipText(Messages.getString("DriveTrainDrawing.Tip"));
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.drawRect(0, 0, getBounds().width-1, getBounds().height -1);
		draw(g2);
	}
	
	public void print(Graphics2D g, PageFormat pageFormat) {
		Dimension origSize = getSize();
		AffineTransform origTransform = g.getTransform();
		Shape origClip = g.getClip();
		double aspectRatio = origSize.getWidth() / origSize.getHeight();
		
		setSize((int)(pageFormat.getImageableWidth() * aspectRatio), (int)pageFormat.getImageableWidth());
		g.setClip(0, 0, getSize().height, getSize().width);
		g.translate(pageFormat.getImageableWidth(), 0);
		g.transform(new AffineTransform(0,1,-1,0,0,0));
		draw(g);
		g.setTransform(origTransform);
		g.setClip(origClip);
		setSize(origSize);
	}
	
	protected void draw(Graphics2D g2) {
		
		installTransform(g2);
		
		float[] dash = model.getUnitSystem().getDashDotLine();
		g2.setStroke(new BasicStroke(
				model.getUnitSystem().getMediumLineWidth(),
				BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_MITER,
				1.0f,
				dash, dash[3]));
		
		Line2D frameCenterLine = new Line2D.Double(
				0,
				-model.getUnitSystem().getFramelineOverlength(),
				0,
				model.getRearCenter().getValue()
				+ model.getUnitSystem().getFramelineOverlength());
		
		g2.draw(frameCenterLine);
		
		g2.setStroke(new BasicStroke(model.getUnitSystem().getDefaultPartWidth()));
		for(Part chainwheel : model.getChainwheels()) {
			drawPart(g2, chainwheel, 0);
		}
		
		for(Part sprocket : model.getSprockets()) {
			drawPart(g2, sprocket, model.getRearCenter().getValue());
		}
		
		Gear selectedGear = style.getSelectedGear();
		if( selectedGear != null) {
			g2.setStroke(new BasicStroke(model.getUnitSystem().getDefaultChainWidth()));
			drawGear(g2, selectedGear);
			Line2D frontPortion = new Line2D.Double(
					selectedGear.getChainwheel().getChainline(),
					-selectedGear.getChainwheel().getRadius(),
					selectedGear.getChainwheel().getChainline(),
					0);
			Line2D rearPortion = new Line2D.Double(
					selectedGear.getSprocket().getChainline(),
					model.getRearCenter().getValue()+selectedGear.getSprocket().getRadius(),
					selectedGear.getSprocket().getChainline(),
					model.getRearCenter().getValue());
			g2.draw(frontPortion);
			g2.draw(rearPortion);
		} else {
			g2.setStroke(new BasicStroke(model.getUnitSystem().getMediumLineWidth()));
			for(int n=0; n<model.getGearCount(); n++) {
				Gear gear = model.getGear(n);
				if(gear.isValid() && style.getGearVisibility().has(gear.getChainlineStatus())) {
					drawGear(g2, gear);
				}
			}
		}
		
		g2.setColor(Color.BLUE);
		drawMeasures(g2);
		/*
		g2.drawRect(-50,-50,100,100);
		*/
	}

	/**
	 * @param g2
	 */
	private void drawMeasures(Graphics2D g2) {
		g2.setStroke(new BasicStroke(model.getUnitSystem().getNarrowLineWidth()));

		MeasureDrawing chainwheelChainlineMeasure = new MeasureDrawing(
				model.getChainwheels().getChainline(),
				model.getUnitSystem().getMeasureLineOverlength(),
				model.getUnitSystem().getArrowTipLength(),
				model.getUnitSystem().getArrowTipWidth(),
				model.getUnitSystem().getMeasureFontSize(), true);
		
		MeasureDrawing sprocketChainlineMeasure = new MeasureDrawing(
				model.getSprockets().getChainline(),
				model.getUnitSystem().getMeasureLineOverlength(),
				model.getUnitSystem().getArrowTipLength(),
				model.getUnitSystem().getArrowTipWidth(),
				model.getUnitSystem().getMeasureFontSize(), true);
		
		chainwheelChainlineMeasure.paint(g2,
				0, 0,
				model.getChainwheels().getChainline().getValue(),
				model.getChainwheels().getLargest().getRadius(),
				model.getUnitSystem().getLargeMeasureLineDistance());
		
		sprocketChainlineMeasure.paint(g2,
				0, model.getRearCenter().getValue(),
				model.getSprockets().getChainline().getValue(),
				model.getRearCenter().getValue()-model.getSprockets().getLargest().getRadius(),
				-model.getUnitSystem().getLargeMeasureLineDistance());
		if(model.getChainwheels().size() > 1) {
			MeasureDrawing chainwheelSpacingMeasure = new MeasureDrawing(
					model.getChainwheels().getPartPitch(),
					model.getUnitSystem().getMeasureLineOverlength(),
					model.getUnitSystem().getArrowTipLength(),
					model.getUnitSystem().getArrowTipWidth(),
					model.getUnitSystem().getMeasureFontSize(), true);
			chainwheelSpacingMeasure.paint(g2,
					model.getChainwheels().get(0).getChainline(),
					model.getChainwheels().get(0).getRadius(),
					model.getChainwheels().get(1).getChainline(),
					model.getChainwheels().get(1).getRadius(),
					model.getUnitSystem().getSmallMeasureLineDistance());
		}
		if(model.getSprockets().size() > 1) {
			MeasureDrawing sprocketSpacingMeasure = new MeasureDrawing(
					model.getSprockets().getPartPitch(),
					model.getUnitSystem().getMeasureLineOverlength(),
					model.getUnitSystem().getArrowTipLength(),
					model.getUnitSystem().getArrowTipWidth(),
					model.getUnitSystem().getMeasureFontSize(), true);

			sprocketSpacingMeasure.paint(g2,
					model.getSprockets().get(0).getChainline(),
					model.getRearCenter().getValue()
					-model.getSprockets().get(0).getRadius(),
					model.getSprockets().get(1).getChainline(),
					model.getRearCenter().getValue()
					-model.getSprockets().get(1).getRadius(),
					-model.getUnitSystem().getSmallMeasureLineDistance());
		}
		
		MeasureDrawing rearCenterMeasure = new MeasureDrawing(
				model.getRearCenter(),
				model.getUnitSystem().getMeasureLineOverlength(),
				model.getUnitSystem().getArrowTipLength(),
				model.getUnitSystem().getArrowTipWidth(),
				model.getUnitSystem().getMeasureFontSize(),
				false);

		rearCenterMeasure.paint(g2,
				model.getChainwheels().getOuterChainline(),
				0,
				model.getSprockets().getOuterChainline(),
				model.getRearCenter().getValue(),
				model.getUnitSystem().getSmallMeasureLineDistance());
	}

	/**
	 * @param g2
	 * @param sprocket
	 */
	private void drawPart(Graphics2D g2, Part sprocket, double offset) {
		Line2D partLine = new Line2D.Double(
				sprocket.getChainline(),
				offset-sprocket.getRadius(),
				sprocket.getChainline(),
				offset+sprocket.getRadius()
		);
		g2.draw(partLine);
	}

	/**
	 * @param g2
	 * @param gear
	 */
	private void drawGear(Graphics2D g2, Gear gear) {
		g2.setColor(GearRenderer.getColorFromChainlineStatus(gear.getChainlineStatus()));
		Line2D gearLine = new Line2D.Double(
			gear.getChainwheel().getChainline(),
			0,
			gear.getSprocket().getChainline(),
			model.getRearCenter().getValue()
		);
		g2.draw(gearLine);
	}

	
	/* (non-Javadoc)
	 * @see javax.swing.JComponent#getPreferredSize()
	 */
	@Override
	public Dimension getPreferredSize() {
		return preferredSize;
	}

	void update() {
		repaint();
	}
	
	void installTransform(Graphics2D g) {
		Rectangle r = getBounds();
		double chainwheelRadius = model.getChainwheels().getLargest().getRadius();
		double requiredHeight = 
			chainwheelRadius
			+ model.getRearCenter().getValue()
			+ model.getSprockets().getLargest().getRadius();
		
		double requiredWidth = max(model.getSprockets().getOuterChainline(), model.getChainwheels().getOuterChainline()) + model.getUnitSystem().getRequiredMargin();
		double scaleFactorX = r.width / requiredWidth;
		double scaleFactorY = r.height / requiredHeight;
		double scaleFactor = min(scaleFactorX, scaleFactorY);
		
		// make 5 pixel margin
		g.translate(5.0, 5.0);
		g.scale((r.width-10.0) / r.width, (r.height-10.0) / r.height);
		
		// Handle Zoom + Pan
		g.translate(style.getViewportX(), style.getViewportY());
		g.scale(style.getZoomFactor(), style.getZoomFactor());

		// prepare transformation for drawing
		g.scale(scaleFactor, scaleFactor);
		g.translate(0, chainwheelRadius);
	}
	
	private class PanListener implements MouseInputListener,MouseWheelListener {

		private int xOff=0, yOff=0;

		public void mouseDragged(MouseEvent e) {
			Point p = e.getPoint();
			if((e.getModifiersEx() & MouseEvent.SHIFT_DOWN_MASK ) != 0) {
				// TODO scaling code
			} else {
				style.setViewportX(p.x-xOff);
				style.setViewportY(p.y-yOff);
			}
			repaint();
		}

		public void mousePressed(MouseEvent e) {
			Point p = e.getPoint();
			xOff = p.x - style.getViewportX();
			yOff = p.y - style.getViewportY();
		}

		public void mouseClicked(MouseEvent e) {
			if((e.getModifiersEx() & MouseEvent.BUTTON2_DOWN_MASK) != 0) {
				style.setViewportX(0);
				style.setViewportY(0);
				style.setZoomFactor(100);
				repaint();
			}
		}

		public void mouseWheelMoved(MouseWheelEvent e) {
			style.setZoomFactor( style.getZoomFactor() + e.getWheelRotation() * 0.1);
			fireStyleChanged();
			repaint();
		}		

		public void mouseMoved(MouseEvent e) {
			// noop			
		}

		public void mouseReleased(MouseEvent e) {
			// noop			
		}

		public void mouseEntered(MouseEvent e) {
			// noop			
		}

		public void mouseExited(MouseEvent e) {
			// noop			
		}

	}
	
}
