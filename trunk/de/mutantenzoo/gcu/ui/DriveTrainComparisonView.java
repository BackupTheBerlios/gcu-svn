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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.TreeMap;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import de.mutantenzoo.gcu.io.DriveTrainCSVWriter;
import de.mutantenzoo.gcu.io.DriveTrainEncoder;
import de.mutantenzoo.gcu.io.DriveTrainHTMLWriter;
import de.mutantenzoo.gcu.io.DriveTrainPNGWriter;
import de.mutantenzoo.gcu.model.ChainlineStatus;
import de.mutantenzoo.gcu.model.DriveTrain;
import de.mutantenzoo.gcu.units.UnitSystem;

/**
 * Comparison View Component
 * @author MKlemm
 *
 */
public class DriveTrainComparisonView extends JScrollPane implements Printable, Zoomable, GearView {


	/**
	 * Generated SUID
	 */
	private static final long serialVersionUID = -1444852719712673897L;
	
	private static final int LEFT_MARGIN = 5;
	private static final int TOP_MARGIN = 5;
	private static final int SPACING = 48;
	private static final int MIN_TICK_SPACING = 24;
	private static final double[] SCALE_STEPS = {100,50,25,20,10,5,2,1,0.5,0.25,0.2,0.1};
	
	private static final FontRenderContext frc = new FontRenderContext(null, true, true);
	
	private UnitSystem unitSystem = UnitSystem.METRIC;
	private TreeMap<DriveTrain, TextLayout> driveTrains = new TreeMap<DriveTrain,TextLayout>();
	private ChainlineStatus chainlineStatus = ChainlineStatus.ANY;
	
	private transient int maxNameWidth = 0;
	private double zoomFactor = 1.0;
	
	/**
	 * Default Constructor, pulls up all child components
	 */
	public DriveTrainComparisonView() {
		super();
		setPreferredSize(new Dimension(600,400));
		setFont(new Font("Verdana", Font.PLAIN, 11));
		setViewportView(new GearChart(this));
		getViewport().getView().setFont(getFont());
		setColumnHeaderView(new Rule(this));
		setRowHeaderView(new DriveTrainTitles(this));
		getRowHeader().setViewSize(new Dimension(getNameViewportWidth(), getViewHeight()));
		addComponentListener(new ComponentListener() {
			public void componentResized(ComponentEvent e) {
				sizeChanged();				
			}

			public void componentMoved(ComponentEvent e) {
				// do nothing
				
			}

			public void componentShown(ComponentEvent e) {
				// do nothing
				
			}

			public void componentHidden(ComponentEvent e) {
				// do nothing
				
			}});
		setCorner(UPPER_LEADING_CORNER, new StretchControl(this));
		sizeChanged();
	}
	
	/**
	 * Adds a DriveTrain to the list of 
	 * DriveTrains that are visible in this view
	 * @param model A DriveTrain to add.
	 */
	public void addModel(DriveTrain model) {
		TextLayout tl = new TextLayout(model.getName(), getFont(), frc);
		if(tl.getBounds().getWidth() > maxNameWidth) {
			maxNameWidth = (int)(tl.getAdvance()+0.5f);
			sizeChanged();
		}
		driveTrains.put(model, tl);		
	}
	
	/**
	 * Removes a DriveTrain from
	 * this view.
	 * @param model The DriveTrain to remove
	 */
	public void removeModel(DriveTrain model) {
		driveTrains.remove(model);
		maxNameWidth = 0;
		for(TextLayout tl : driveTrains.values()) {
			if(tl != null && tl.getBounds().getWidth() > maxNameWidth) {
				maxNameWidth = (int)(tl.getAdvance()+0.5f);
			}
		}
	}
	
	/**
	 * Removes all DriveTrains from
	 * this view
	 */
	public void removeAllModels() {
		driveTrains.clear();
		maxNameWidth = 0;
	}

	/**
	 * Handles the "Export" user action.
	 * Prompts the user for a file
	 * name and type and then exports
	 * this view to the specified file.
	 */
	public void export() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileFilter(null);
		fileChooser.addChoosableFileFilter(SuffixFileFilter.PNG);
		fileChooser.addChoosableFileFilter(SuffixFileFilter.CSV);
		fileChooser.addChoosableFileFilter(SuffixFileFilter.HTML);
		int retVal = fileChooser.showSaveDialog(this);
		if (retVal == JFileChooser.APPROVE_OPTION) {
			File selectedFile = ((SuffixFileFilter)fileChooser.getFileFilter()).makeAcceptable(fileChooser.getSelectedFile());
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
				if(fileChooser.getFileFilter() == SuffixFileFilter.PNG) {
					exportPNG(selectedFile);
				} else if(fileChooser.getFileFilter() == SuffixFileFilter.CSV){
					ps = new PrintStream(new FileOutputStream(selectedFile));
					DriveTrainCSVWriter.writeCSV(ps, driveTrains.keySet());
					ps.close();
				} else {
					DriveTrainHTMLWriter.writeComparisonHTML(selectedFile, this);
				}
			} catch (IOException e) {
				JOptionPane.showMessageDialog(this, Messages.format(
						"Main.15", e.getLocalizedMessage()), //$NON-NLS-1$ 
						Messages.getString("Main.16"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$

			}
		}


	}
	
	/**
	 * Utility method to export
	 * this view to a PNG graphics file
	 * @param selectedFile The file to export to.
	 * @throws IOException If file operations fail.
	 */
	private void exportPNG(File selectedFile) throws IOException {
		DriveTrainPNGWriter pngWriter = new DriveTrainPNGWriter(getRowHeader().getViewSize().width + getViewport().getViewSize().width, getColumnHeader().getViewSize().height + getViewport().getViewSize().height);
		pngWriter.setBackgroundColor(Color.WHITE);
		paintFull(pngWriter.getGraphics());
		FileOutputStream out = new FileOutputStream(selectedFile);
		pngWriter.save(out);
		out.close();
	}
	
	/**
	 * Paints this component as in paint(), but
	 * with all of the viewport visible and no
	 * zoom slider control.
	 * @param g The graphics context to draw to.
	 */
	private void paintFull(Graphics2D g) {
		AffineTransform origTransform = g.getTransform();
		g.translate(getRowHeader().getViewSize().getWidth(), 0);
		getColumnHeader().getView().paint(g);
		g.setTransform(origTransform);
		g.translate(0, getColumnHeader().getViewSize().getHeight());
		getRowHeader().getView().paint(g);
		g.setTransform(origTransform);
		g.translate(getRowHeader().getViewSize().getWidth(), getColumnHeader().getViewSize().getHeight());
		getViewport().getView().paint(g);
		g.setTransform(origTransform);
	}
	
	/**
	 * Shows the user a printing dialog and
	 * prints this view.
	 */
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

	/*
	 *  (non-Javadoc)
	 * @see java.awt.print.Printable#print(java.awt.Graphics, java.awt.print.PageFormat, int)
	 */
	public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
		if(pageIndex == 0) {
			Graphics2D g = (Graphics2D)graphics;
			g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
			AffineTransform origTransform = g.getTransform();
			g.translate(pageFormat.getImageableX(), pageFormat.getImageableY()+40);
			Dimension origSize = getSize();
			setSize((int)pageFormat.getImageableWidth(), (int)pageFormat.getImageableHeight());
			paintFull(g);
			setSize(origSize);
			g.setTransform(origTransform);
			g.drawRect((int)pageFormat.getImageableX()+1, (int)pageFormat.getImageableY()+1, (int)pageFormat.getImageableWidth()-1, (int)pageFormat.getImageableHeight()-1);
			return Printable.PAGE_EXISTS;
		} else {
			return Printable.NO_SUCH_PAGE;
		}
	}

	/**
	 * Handles the "View All Gears" user action
	 */
	public void viewAllGears() {
		chainlineStatus = ChainlineStatus.ANY;
		repaint();
	}

	/**
	 * Handles the "View Usable Gears" user action
	 */
	public void viewOKGears() {
		chainlineStatus = ChainlineStatus.USABLE;
		repaint();
	}

	/**
	 * Handles the "View Good Gears" user action.
	 */
	public void viewGoodGears() {
		chainlineStatus = ChainlineStatus.GOOD;
		repaint();
	}

	/**
	 * Sets the unit system
	 * @param unitSystem The unit system to set.
	 */
	public void setUnitSystem(UnitSystem unitSystem) {
		this.unitSystem = unitSystem;
		repaint();
	}

	/**
	 * Handles the "Save As" user action
	 */
	public boolean saveAs() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileFilter(SuffixFileFilter.XML);
		int retVal = fileChooser.showSaveDialog(this);
		if (retVal == JFileChooser.APPROVE_OPTION) {
			File selectedFile = ((SuffixFileFilter)fileChooser.getFileFilter()).makeAcceptable(fileChooser.getSelectedFile());
			
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

	/**
	 * @return Returns the chainlineStatus.
	 */
	ChainlineStatus getChainlineStatus() {
		return chainlineStatus;
	}

	/**
	 * @param chainlineStatus The chainlineStatus to set.
	 */
	void setChainlineStatus(ChainlineStatus chainlineStatus) {
		this.chainlineStatus = chainlineStatus;
	}

	/**
	 * @return Returns the driveTrains.
	 */
	TreeMap<DriveTrain, TextLayout> getDriveTrains() {
		return driveTrains;
	}

	/**
	 * @return Returns the unitSystem.
	 */
	UnitSystem getUnitSystem() {
		return unitSystem;
	}

	/**
	 * @return Returns the zoomFactor.
	 */
	double getZoomFactor() {
		return zoomFactor;
	}

	public void setZoomFactor(double zoomFactor) {
		this.zoomFactor = zoomFactor;
		sizeChanged();
		repaint();
	}
	
	/**
	 * @return Returns the SPACING.
	 */
	int getSpacing() {
		return SPACING;
	}

	public int getTopMargin() {
		return TOP_MARGIN;
	}

	public int getGearViewportWidth() {
		return getWidth() - getNameViewportWidth();
	}

	public int getNameViewportWidth() {
		return maxNameWidth + 2 * LEFT_MARGIN;
	}
	
	public int map(double value) {
		double fact = (getBounds().getWidth() - getNameViewportWidth() - LEFT_MARGIN) / getUnitSystem().getMaxDevelopment();
		return (int)(value * fact * zoomFactor);
	}

	/**
	 * 
	 * @return The height of the ruler (scale)
	 */
	int getRulerViewHeight() {
		return 40;
	}
	
	/**
	 * 
	 * @return The total width of the main gear view
	 */
	int getViewWidth() {
		return map(unitSystem.getMaxDevelopment());
	}
	
	/**
	 * @return The total height of the main gear view
	 */
	int getViewHeight() {
		return TOP_MARGIN + SPACING * (1+driveTrains.size());
	}
	
	/**
	 * Gets the distance between the ticks on the ruler, this
	 * depends on the overall size of the component and the zoom
	 * factor.
	 * @return The distance between the ticks on the ruler
	 */
	double getTickStep() {
		for(int n=SCALE_STEPS.length-1; n>=0; n--) {
			int step = map(SCALE_STEPS[n]);
			if(step > MIN_TICK_SPACING) {
				return SCALE_STEPS[n];
			}
		}
		return 1;
	}

	/**
	 * Performs the steps 
	 * to rearrange the GUI when the size
	 * of this view has changed.
	 */
	private void sizeChanged() {
		getViewport().getView().setPreferredSize(new Dimension(getViewWidth(), getViewHeight()));
		getColumnHeader().getView().setPreferredSize(new Dimension(getViewWidth(), getRulerViewHeight()));
		((JComponent)getViewport().getView()).revalidate();
		((JComponent)getColumnHeader().getView()).revalidate();
	}

}
