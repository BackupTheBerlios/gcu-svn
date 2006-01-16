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
import java.awt.Stroke;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import de.mutantenzoo.gcu.io.DriveTrainCSVWriter;
import de.mutantenzoo.gcu.io.DriveTrainEncoder;
import de.mutantenzoo.gcu.io.DriveTrainPNGWriter;
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
	private static final Font font = new Font("Verdana", Font.PLAIN, 10);
	private static final double LEFT_MARGIN = 5.0;
	private static final double RIGHT_MARGIN = 5.0;
	private static final double BOTTOM_MARGIN = 40.0;
	private static final double SPACING = 40.0;
	private static final GeneralPath triangle = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
	
	private Dimension preferredSize = new Dimension(600,400);
	private UnitSystem unitSystem = UnitSystem.METRIC;
	private HashMap<DriveTrain, TextLayout> driveTrains = new HashMap<DriveTrain,TextLayout>();
	private ChainlineStatus chainlineStatus = ChainlineStatus.ALL;
	
	private transient double maxNameWidth = 0;

	
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
		driveTrains.put(model, null);
	}
	
	private void prepareText(Graphics2D g) {
		for(DriveTrain d : driveTrains.keySet()) {
			TextLayout tl = new TextLayout(d.getName(), font, g.getFontRenderContext());
			if(tl.getBounds().getWidth() > maxNameWidth) {
				maxNameWidth = tl.getBounds().getWidth();
			}
			driveTrains.put(d, tl);
		}
	}
	
	public void removeModel(DriveTrain model) {
		driveTrains.remove(model);
		maxNameWidth = 0;
		for(TextLayout tl : driveTrains.values()) {
			if(tl != null && tl.getBounds().getWidth() > maxNameWidth) {
				maxNameWidth = tl.getBounds().getWidth();
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	@Override
	protected void paintComponent(Graphics g1) {
		Graphics2D g = (Graphics2D)g1;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		prepareText(g);
		prepareTransform(g);
		drawAxes(g);
		drawDriveTrains(g);
	}

	private void drawAxes(Graphics2D g) {
		Line2D xAxis = new Line2D.Double(0, 0, map(unitSystem.getMaxDevelopment()), 0);
		g.draw(xAxis);
		Line2D yAxis = new Line2D.Double(0, 0, 0, -SPACING * (1+driveTrains.size()));
		g.draw(yAxis);
		for(double n=0; n<unitSystem.getMaxDevelopment(); n += unitSystem.getDevelopmentSteps() ) {
			Stroke origStroke = g.getStroke();
			Color origColor = g.getColor();
			g.setColor(Color.GRAY);
			g.setStroke(new BasicStroke(0.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f, new float[]{2f,2f}, 2f));
			Line2D tick = new Line2D.Double(map(n), 5, map(n), -SPACING * (1+driveTrains.size()));
			g.draw(tick);
			g.setStroke(origStroke);
			g.setColor(origColor);
			TextLayout tl = new TextLayout(unitSystem.getDevelopmentFormat().format(n), font, g.getFontRenderContext());
			tl.draw(g, (float)(map(n)-tl.getBounds().getWidth() / 2.0), (float)(6.0 + tl.getBounds().getHeight()));
		}
	}
	
	private double map(double value) {
		double fact = (getBounds().getWidth() - 2.0*LEFT_MARGIN - maxNameWidth - RIGHT_MARGIN) / unitSystem.getMaxDevelopment();
		return value * fact;
	}
	
	private void drawDriveTrains(Graphics2D g) {
		double y = 0;
		for(DriveTrain model : driveTrains.keySet()) {
			drawGears(g, y-=SPACING, model);
		}
	}
	
	private void drawGears(Graphics2D g, double y, DriveTrain model) {
		TextLayout tl = driveTrains.get(model);
		
		tl.draw(g, (float)(-tl.getBounds().getWidth()-LEFT_MARGIN), (float)(y+tl.getBounds().getHeight()+4.0));
		
		Line2D axis = new Line2D.Double(-maxNameWidth-LEFT_MARGIN, y, map(unitSystem.getMaxDevelopment()), y);
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
		if(gear.getParent().getUnitSystem().equals(unitSystem)) {
			g.translate(map(gear.getDevelopment().getValue()), y);
		} else {
			g.translate(map(unitSystem.translateDevelopment(gear.getDevelopment().getValue())), y);
		}
		g.draw(triangle);
		g.setColor(Color.BLACK);
		TextLayout tl = new TextLayout(gear.getChainwheel().getSize()+":"+gear.getSprocket().getSize(), font, g.getFontRenderContext());
		//AffineTransform textTransform = AffineTransform.getTranslateInstance(tl.getBounds().getHeight() / 2.0, 6.0+tl.getBounds().getWidth());
		g.transform(new AffineTransform(0,-1,1,0,0,0));
		//Shape t = tl.getOutline(new AffineTransform(textTransform));
		tl.draw(g, -(float)(tl.getBounds().getWidth())-6f, (float)(tl.getBounds().getHeight() / 2.0));
		g.setTransform(origTransform);
	}


	private void prepareTransform(Graphics2D g) {
		g.translate(2.0*LEFT_MARGIN + maxNameWidth, getBounds().getHeight() - BOTTOM_MARGIN);
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
		JFileChooser fileChooser = new JFileChooser();
		FileFilter pngFilter = Filters.PNG;
		FileFilter csvFilter = Filters.CSV;
		fileChooser.setFileFilter(null);
		fileChooser.addChoosableFileFilter(pngFilter);
		fileChooser.addChoosableFileFilter(csvFilter);

		int retVal = fileChooser.showSaveDialog(this);
		if (retVal == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fileChooser.getSelectedFile();
			boolean pngSelected = fileChooser.getFileFilter() == pngFilter;
			if(pngSelected) {
				if (!selectedFile.getName().toLowerCase().endsWith(".png")) { //$NON-NLS-1$
					selectedFile = new File(selectedFile.getAbsolutePath() + ".png"); //$NON-NLS-1$
				}
			} else {
				if (!selectedFile.getName().toLowerCase().endsWith(".csv")) { //$NON-NLS-1$
					selectedFile = new File(selectedFile.getAbsolutePath() + ".csv"); //$NON-NLS-1$
				}
			}
			if (selectedFile.exists()) {
				if (selectedFile.canWrite()) {
					int chosen = JOptionPane
							.showConfirmDialog(
									this,
									Messages.getString("Main.11"), Messages.getString("Main.12"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE); //$NON-NLS-1$ //$NON-NLS-2$
					if (chosen == JOptionPane.NO_OPTION) {
						return;
					}
				} else {
					JOptionPane
							.showMessageDialog(
									this,
									Messages.getString("Main.13"), Messages.getString("Main.14"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$ //$NON-NLS-2$
					return;
				}
			}
			PrintStream ps;
			try {
				ps = new PrintStream(new FileOutputStream(selectedFile));
				if(pngSelected) {
					DriveTrainPNGWriter.writePNG(ps, this, 1024, (int)(BOTTOM_MARGIN + SPACING * (1+driveTrains.size())));
				} else {
					DriveTrainCSVWriter.writeCSV(ps, driveTrains.keySet());
				}
				ps.close();
			} catch (IOException e) {
				JOptionPane.showMessageDialog(this, Messages.format(
						"Main.15", e.getLocalizedMessage()), //$NON-NLS-1$ 
						Messages.getString("Main.16"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$

			}
		}


	}
	
	public void print() {
		PrinterJob pj = PrinterJob.getPrinterJob();
		pj.setPrintable(this);
		pj.setJobName(Messages.getString("GCUPrintJob"));
		if(pj.printDialog()) {
			try {
				pj.print();
			} catch (PrinterException e) {
				JOptionPane.showMessageDialog(this, Messages.format(
						"PrinterException", e.getLocalizedMessage()), //$NON-NLS-1$ 
						Messages.getString("Main.16"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$

			}
		}
	}

	public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
		if(pageIndex == 0) {
			Graphics2D g = (Graphics2D)graphics;
			g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
			AffineTransform origTransform = g.getTransform();
			g.translate(pageFormat.getImageableX(), pageFormat.getImageableY()+40);
			Dimension origSize = getSize();
			setSize((int)pageFormat.getImageableWidth(), (int)pageFormat.getImageableHeight());
			paintComponent(g);
			setSize(origSize);
			g.setTransform(origTransform);
			return Printable.PAGE_EXISTS;
		} else {
			return Printable.NO_SUCH_PAGE;
		}
	}

	public void viewAllGears() {
		chainlineStatus = ChainlineStatus.ALL;
		repaint();
	}

	public void viewOKGears() {
		chainlineStatus = ChainlineStatus.USABLE;
		repaint();
	}

	public void viewGoodGears() {
		chainlineStatus = ChainlineStatus.GOOD;
		repaint();
	}

	public void setUnitSystem(UnitSystem unitSystem) {
		this.unitSystem = unitSystem;
		repaint();
	}

	public boolean saveAs() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileFilter(Filters.XML);
		int retVal = fileChooser.showSaveDialog(this);
		if (retVal == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fileChooser.getSelectedFile();
			if (!selectedFile.getName().toLowerCase().endsWith(".xml")
					&& !selectedFile.getName().toLowerCase().endsWith(".txt") ) { //$NON-NLS-1$
				selectedFile = new File(selectedFile.getAbsolutePath() + ".txt"); //$NON-NLS-1$
			}
			if (selectedFile.exists()) {
				if (selectedFile.canWrite()) {
					int chosen = JOptionPane
							.showConfirmDialog(
									this,
									Messages.getString("Main.11"), Messages.getString("Main.12"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE); //$NON-NLS-1$ //$NON-NLS-2$
					if(chosen == JOptionPane.NO_OPTION) {
						return false;
					}
				} else {
					JOptionPane
							.showMessageDialog(
									this,
									Messages.getString("Main.13"), Messages.getString("Main.14"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$ //$NON-NLS-2$
					return false;
				}
			}
			return writeFile(selectedFile);
		} else {
			return false;
		}
	}
	/**
	 * Writes data out into file specified in model
	 */
	private boolean writeFile(File selectedFile) {
		try {
			FileOutputStream ow = new FileOutputStream(selectedFile);
			DriveTrainEncoder.encode(ow, driveTrains.keySet());
			ow.close();
			return true;
		} catch (IOException iox) {
			JOptionPane.showMessageDialog(this, Messages.format(
					"Main.15", iox.getLocalizedMessage()), //$NON-NLS-1$ 
					Messages.getString("Main.16"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
			return false;
		} catch (ParserConfigurationException iox) {
			JOptionPane.showMessageDialog(this, Messages.format(
					"ParserConfigurationException", iox.getLocalizedMessage()), //$NON-NLS-1$ 
					Messages.getString("Main.16"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
			return false;
		} catch (TransformerException iox) {
			JOptionPane.showMessageDialog(this, Messages.format(
					"TransformerException", iox.getLocalizedMessage()), //$NON-NLS-1$ 
					Messages.getString("Main.16"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
			return false;
		}

	}

	public boolean close() {
		return false;
	}


}
