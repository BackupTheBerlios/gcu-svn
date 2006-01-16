package de.mutantenzoo.raf.graphics;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;

public	class Arrow {
	
	public static final int LEFT=-1;
	public static final int RIGHT=1;
	
	private GeneralPath tip;
	private double tipLength;
	private double tipWidth;
	
	public Arrow(float tipLength, float tipWidth) {
		tip = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
		tip.moveTo(-tipLength, -tipWidth/2.0f);
		tip.lineTo(-tipLength, tipWidth/2.0f);
		tip.lineTo(0,0);
		tip.closePath();
		this.tipLength = tipLength;
		this.tipWidth = tipWidth;
	}
	
	public void paint(Graphics2D g, double x1, double y1, double x2, double y2) {
		double lineLength = getLineLength(x1,y1,x2,y2);
		double tipSize = getLineLength(0,0, tipLength, tipWidth/2.0);
		if( lineLength < 2.0 * tipSize) {
			paintSmall(g, x1, y1, x2, y2);
		} else {
			paintLarge(g, x1, y1, x2, y2);
		}
	}
	
	public void paintLarge(Graphics2D g, double x1, double y1, double x2, double y2) {
		Line2D line = new Line2D.Double(x1, y1, x2, y2);
		g.draw(line);
		AffineTransform transform = g.getTransform();
		g.translate(x2,y2);
		double dX = x2-x1;
		double dY = y2-y1;
		g.rotate(Math.atan2(dY,dX));
		g.fill(tip);
		g.setTransform(transform);
		g.translate(x1,y1);
		g.rotate(Math.atan2(dY,dX)+Math.PI);
		g.fill(tip);
		g.setTransform(transform);
	}
	
	public void paintSmall(Graphics2D g, double x1, double y1, double x2, double y2) {
		AffineTransform transform = g.getTransform();
		g.translate(x2,y2);
		double dX = x2-x1;
		double dY = y2-y1;
		g.rotate(Math.atan2(dY,dX)+Math.PI);
		g.fill(tip);
		g.setTransform(transform);
		g.translate(x1,y1);
		g.rotate(Math.atan2(dY,dX));
		g.fill(tip);
		g.setTransform(transform);
	}
	
	private static double getLineLength(double x1, double y1, double x2, double y2) {
		double dX = x2-x1;
		double dY = y2-y1;
		return Math.sqrt(dX*dX + dY*dY);
	}
}


