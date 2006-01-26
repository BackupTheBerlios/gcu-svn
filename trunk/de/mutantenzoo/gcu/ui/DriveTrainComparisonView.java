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
import javax.swing.filechooser.FileFilter;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import de.mutantenzoo.gcu.io.DriveTrainCSVWriter;
import de.mutantenzoo.gcu.io.DriveTrainEncoder;
import de.mutantenzoo.gcu.io.DriveTrainPNGWriter;
import de.mutantenzoo.gcu.model.ChainlineStatus;
import de.mutantenzoo.gcu.model.DriveTrain;
import de.mutantenzoo.gcu.ui.Distributor.Mapper;
import de.mutantenzoo.gcu.units.UnitSystem;

/**
 * @author MKlemm
 *
 */
public class DriveTrainComparisonView extends JScrollPane implements Printable, Zoomable, GearView, Mapper {


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
	
	private Dimension preferredSize = new Dimension(600,400);
	private UnitSystem unitSystem = UnitSystem.METRIC;
	private TreeMap<DriveTrain, TextLayout> driveTrains = new TreeMap<DriveTrain,TextLayout>();
	private ChainlineStatus chainlineStatus = ChainlineStatus.ALL;
	
	private transient int maxNameWidth = 0;
	private double zoomFactor = 1.0;
	
	/**
	 * Default Constructor
	 */
	public DriveTrainComparisonView() {
		super();
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
	 * 
	 * @param model
	 */
	public void addModel(DriveTrain model) {
		TextLayout tl = new TextLayout(model.getName(), getFont(), frc);
		if(tl.getBounds().getWidth() > maxNameWidth) {
			maxNameWidth = (int)(tl.getAdvance()+0.5f);
			sizeChanged();
		}
		driveTrains.put(model, tl);		
	}
	
	public void removeModel(DriveTrain model) {
		driveTrains.remove(model);
		maxNameWidth = 0;
		for(TextLayout tl : driveTrains.values()) {
			if(tl != null && tl.getBounds().getWidth() > maxNameWidth) {
				maxNameWidth = (int)(tl.getAdvance()+0.5f);
			}
		}
	}
	
	public void removeAllModels() {
		driveTrains.clear();
		maxNameWidth = 0;
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
					DriveTrainPNGWriter.writePNG(ps, this, 1024, (TOP_MARGIN + SPACING * (1+driveTrains.size())));
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

	int getRulerViewHeight() {
		return 40;
	}
	
	int getViewWidth() {
		return map(unitSystem.getMaxDevelopment());
	}
	
	int getViewHeight() {
		return TOP_MARGIN + SPACING * (1+driveTrains.size());
	}
	
	double getTickStep() {
		for(int n=SCALE_STEPS.length-1; n>=0; n--) {
			int step = map(SCALE_STEPS[n]);
			if(step > MIN_TICK_SPACING) {
				return SCALE_STEPS[n];
			}
		}
		return 1;
	}

	public void sizeChanged() {
		getViewport().getView().setPreferredSize(new Dimension(getViewWidth(), getViewHeight()));
		getColumnHeader().getView().setPreferredSize(new Dimension(getViewWidth(), getRulerViewHeight()));
		((JComponent)getViewport().getView()).revalidate();
		((JComponent)getColumnHeader().getView()).revalidate();
	}

}
