package de.mutantenzoo.gcu.ui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.font.TextLayout;
import java.awt.geom.Line2D;

import javax.swing.JComponent;

/**
 * @author MKlemm
 *
 */
class Rule extends JComponent {

	/**
	 * 
	 */
	private final DriveTrainComparisonView parent;

	/**
	 * @param view
	 */
	Rule(DriveTrainComparisonView view) {
		parent = view;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 632737643579445202L;
	
	final Rectangle bounds = new Rectangle();
	
	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	@Override
	protected void paintComponent(Graphics g1) {
		Graphics2D g = (Graphics2D)g1;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		double step = parent.getTickStep();
		for(double t=0; t < parent.getUnitSystem().getMaxDevelopment(); t+=step) {
			int tickLength = getTickLength(t);
			TextLayout tl = new TextLayout(parent.getUnitSystem().getDevelopmentFormat().format(t), parent.getFont(), g.getFontRenderContext());
			if(t == 0) {
				tl.draw(g, 0, getHeight() - tickLength - 4);
			} else if(t == parent.getUnitSystem().getMaxDevelopment()) {
				tl.draw(g, parent.map(t) - tl.getAdvance(), getHeight() - tickLength - 4);
			} else {
				tl.draw(g, parent.map(t) - tl.getAdvance() / 2f, getHeight() - tickLength - 4);
			}
			Line2D line = new Line2D.Float(parent.map(t), getHeight()-tickLength, parent.map(t), getHeight());
			g.draw(line);
		}
		// System.out.println();
	}

	private int getTickLength(double t) {
		String nStr = Integer.toString((int)(t*100.0+0.5));
		// System.out.print(","+nStr);
		if(nStr.endsWith("00")) {
			return 16;
		} else if(nStr.endsWith("0")) {
			return 10;
		} else {
			return 4;
		}
	}
	
	/*
	@Override
	public Dimension getPreferredSize() {
		return getBounds().getSize();
	}
	
	@Override
	public Dimension getSize() {
		return getPreferredSize();
	}
	
	@Override
	public Rectangle getBounds() {
		bounds.setLocation(super.getLocation());
		bounds.width = getWidth();
		bounds.height = getHeight();
		return bounds;
	}
	
	@Override
	public int getWidth() {
		return parent.getViewWidth();
	}
	
	@Override
	public int getHeight() {
		return parent.getRulerViewHeight();
	}
	*/
}