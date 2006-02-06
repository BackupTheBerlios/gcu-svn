/**
 * DriveTrainPNGWriter.java
 *
 * Created: 30.12.2005 11:32:53
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
package de.mutantenzoo.gcu.io;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

/**
 * Utility class to write 
 * Swing components to PNG bitmap
 * images.
 * @author MKlemm
 *
 */
public class DriveTrainPNGWriter {

	private BufferedImage image;
	private Graphics2D g;
	
	/**
	 * Initializes the PNGWriter with a
	 * specific width and height.
	 * @param width The width of the image.
	 * @param height The height of the image.
	 */
	public DriveTrainPNGWriter(int width, int height) {
		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		g = image.createGraphics();
	}
	
	/**
	 * Sets the background color of the image.
	 * The default is "Transparent".
	 * @param color Color of the image background.
	 */
	public void setBackgroundColor(Color color) {
		g.setBackground(color);
		g.clearRect(0, 0, image.getWidth(), image.getHeight());
	}
	
	/**
	 * Gets the graphics context.
	 * Everything that is painted on this
	 * Graphics context will be saved to
	 * the image file.
	 * @return The Graphics context.
	 */
	public Graphics2D getGraphics() {
		return g;
	}
	
	/**
	 * Saves the data to the specified
	 * output stream.
	 * @param out The output stream to save the data to.
	 * @throws IOException if stream operations fail.
	 */
	public void save(OutputStream out) throws IOException {
		ImageIO.write(image, "png", out);
	}
	
	/**
	 * Convenience method to save a 
	 * Swing component as it is shown
	 * on-screen in one step.
	 * @param out OutputStream to write the image data to.
	 * @param view Component to export.
	 * @param width Width of the resulting image. The view will be scaled to fit this.
	 * @param height Height of the resulting image. The view will be scaled to fit this.
	 * @throws IOException If stream operations fail.
	 */
	public static void writePNG(OutputStream out, JComponent view, int width, int height) throws IOException {
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();
		Dimension origSize = view.getSize();
		view.setSize(width, height);
		view.paint(g);
		view.setSize(origSize);
		ImageIO.write(image, "png", out);
	}

}
