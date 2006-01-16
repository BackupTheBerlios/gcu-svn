package com.ibm.was.graphics;

import java.awt.*;
import java.awt.image.BufferedImage;

import javax.swing.*;

/**
 * This icon can be built with another icon and will render it with a Rollover
 * effect where the icon is darkened and has its blue intensity increased
 */
public class RolloverIcon extends ImageIcon {
	
	/**
	 * Generated SUID
	 */
	private static final long serialVersionUID = 3757470229899737051L;
	protected ImageIcon fIcon;

	/**
	 * Construct us with the icon we will create paint a rollover icon for
	 */
	public RolloverIcon(Component component, ImageIcon anIcon) {
		super();
		int width = anIcon.getImage().getWidth(null);
		int height = anIcon.getImage().getHeight(null);
		BufferedImage  bufferedImage = new BufferedImage(width ,height, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g2D = (Graphics2D) bufferedImage.getGraphics();
		g2D.setComposite(RolloverComposite.getInstance());		
		anIcon.paintIcon(component, g2D, 0, 0);
		setImage(bufferedImage);
	}
}
