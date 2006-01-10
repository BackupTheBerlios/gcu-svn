/**
 * 
 */
package de.mutantenzoo.raf.graphics;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import de.mutantenzoo.raf.Quantifiable;

/**
 * @author MKlemm
 *
 */
public class MeasureDrawing {

	private static final AffineTransform ROTATE = new AffineTransform(0,-1.0,1.0,0,0,0);

	private boolean horizontal;
	private Font font = new Font("Verdana", Font.PLAIN, 100);
	private Quantifiable model;
	private double overlength = 2.0;
	private float tipLength = 6.0f;
	private float tipWidth = 4.0f;
	private double fontSize = 4.0;
	
	/**
	 * @param fontSize TODO
	 * 
	 */
	public MeasureDrawing(Quantifiable model, double overlength, float tipLength, float tipWidth, double fontSize, boolean horizontal) {
		this.model = model;
		this.overlength = overlength;
		this.tipLength = tipLength;
		this.tipWidth = tipWidth;
		this.horizontal = horizontal;
		this.fontSize = fontSize;
	}

	public void paint(Graphics2D g, double x1, double y1, double x2, double y2, double dist) {
		if(horizontal) {
			paintH(g, x1, y1, x2, y2, dist);
		} else {
			paintV(g, x1, y1, x2, y2, dist);
		}
	}
	
	private void paintH(Graphics2D g, double x1, double y1, double x2, double y2, double dist) {
		double arrowPos;
		if(dist < 0) {
			arrowPos = Math.min(y1+dist, y2+dist);
		} else {
			arrowPos = Math.max(y1+dist, y2+dist);
		}
		Arrow arrow = new Arrow(tipLength, tipWidth);
		Line2D l1 = new Line2D.Double(x1, y1+(Math.signum(dist)*fontSize/2.0), x1, arrowPos+Math.signum(dist)*fontSize);
		g.draw(l1);
		Line2D l2 = new Line2D.Double(x2, y2+(Math.signum(dist)*fontSize/2.0), x2, arrowPos+Math.signum(dist)*fontSize);
		g.draw(l2);
		arrow.paint(g, x1, arrowPos, x2, arrowPos);
		TextLayout tl = new TextLayout(model.getStringValue(), font, g.getFontRenderContext());
		double fontScaleFactor = fontSize / 100.0;
		AffineTransform trans = AffineTransform.getScaleInstance(fontScaleFactor, fontScaleFactor);
		Shape textShape = tl.getOutline(trans);
		Rectangle2D textBounds = textShape.getBounds();
		if(textBounds.getWidth()+overlength > Math.abs(x2-x1)) {
			trans = AffineTransform.getTranslateInstance(Math.max(x1,x2)+overlength, arrowPos-overlength);
		} else {
			trans = AffineTransform.getTranslateInstance((x1 + (x2-x1) / 2.0) - textBounds.getWidth() / 2.0, arrowPos-overlength);
		}
		g.fill(trans.createTransformedShape(textShape));
	}	

	private void paintV(Graphics2D g, double x1, double y1, double x2, double y2, double dist) {
		double arrowPos;
		if(dist > 0) {
			arrowPos = Math.max(x1+dist, x2+dist);
		} else {
			arrowPos = Math.min(x1+dist, x2+dist);
		}
		Arrow arrow = new Arrow(tipLength, tipWidth);
		Line2D l1 = new Line2D.Double(x1+(fontSize/2.0), y1, arrowPos+Math.signum(dist)*fontSize, y1);
		Line2D l2 = new Line2D.Double(x2+(fontSize/2.0), y2, arrowPos+Math.signum(dist)*fontSize, y2);
		g.draw(l1);
		g.draw(l2);
		arrow.paint(g, arrowPos, y1, arrowPos, y2);
		TextLayout tl = new TextLayout(model.getStringValue(), font, g.getFontRenderContext());
		double fontScaleFactor = fontSize / 100.0;
		AffineTransform trans = AffineTransform.getScaleInstance(fontScaleFactor, fontScaleFactor);
		trans.concatenate(ROTATE);
		Shape textShape = tl.getOutline(trans);
		Rectangle2D textBounds = textShape.getBounds();
		double x = arrowPos-(fontSize / 2.0);
		double y;
		if(textBounds.getHeight()+overlength > Math.abs(y2-y1)) {
			y = y1 - overlength;
		} else {
			y = y1 + (y2-y1) / 2.0 - textBounds.getHeight() / 2.0;
		}
		
		trans = AffineTransform.getTranslateInstance(x,y);		
		g.fill(trans.createTransformedShape(textShape));
	}	
	
}
