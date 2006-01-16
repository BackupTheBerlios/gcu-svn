/**
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

import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.NORTHWEST;
import static java.awt.GridBagConstraints.REMAINDER;
import static java.awt.GridBagConstraints.VERTICAL;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintStream;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.filechooser.FileFilter;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import de.mutantenzoo.gcu.io.DriveTrainCSVWriter;
import de.mutantenzoo.gcu.io.DriveTrainEncoder;
import de.mutantenzoo.gcu.io.DriveTrainHTMLWriter;
import de.mutantenzoo.gcu.io.DriveTrainPNGWriter;
import de.mutantenzoo.gcu.model.ChainlineStatus;
import de.mutantenzoo.gcu.model.DriveTrain;
import de.mutantenzoo.gcu.model.DriveTrainStyle;
import de.mutantenzoo.gcu.units.UnitSystem;
import de.mutantenzoo.raf.ContentChangeListener;
import de.mutantenzoo.raf.ContentPanel;

/**
 * @author MKlemm
 *
 */
public class DriveTrainPanel extends ContentPanel implements ContentChangeListener, Printable, GearView {


	/**
	 * Generated SUID
	 */
	private static final long serialVersionUID = -851432026795134285L;
	private static final Icon SAVE_ICON = new ImageIcon(DriveTrainPanel.class.getResource("/toolbarButtonGraphics/general/Save16.gif"));
	
	private static final int DEFAULT_CHAINWHEEL_COUNT = 3;
	private static final int DEFAULT_SPROCKET_COUNT = 10;

	private static int instanceCount = 0;
	
	private String name;
	
	private DriveTrain model = new DriveTrain(DEFAULT_CHAINWHEEL_COUNT, DEFAULT_SPROCKET_COUNT);

	private DriveTrainStyle style = new DriveTrainStyle();

	private PartSetTranslationInput chainwheelTranslationInput = new PartSetTranslationInput(model
			.getChainwheels(), Messages.getString("Main.0")); //$NON-NLS-1$

	private PartSetTranslationInput sprocketTranslationInput = new PartSetTranslationInput(model.getSprockets(),
			Messages.getString("Main.1")); //$NON-NLS-1$

	private PartSetGeometryInput chainwheelGeometryInput = new PartSetGeometryInput(model
			.getChainwheels(), Messages.getString("Main.0")); //$NON-NLS-1$

	private PartSetGeometryInput sprocketGeometryInput = new PartSetGeometryInput(model.getSprockets(),
			Messages.getString("Main.1")); //$NON-NLS-1$

	private GeneralTranslationInput generalTranslationInput = new GeneralTranslationInput(model);
	private GeneralGeometryInput generalGeometryInput = new GeneralGeometryInput(model);

	private DriveTrainTable driveTrainOutput = new DriveTrainTable(model, style);
	private DriveTrainDrawing driveTrainDrawing = new DriveTrainDrawing(model, style);

	private ZoomInput zoomInput = new ZoomInput(model, style);


	public DriveTrainPanel() {
		super(new GridBagLayout());
		name= Messages.format("Unnamed", ++instanceCount);
		initComponents();
	}

	public void initComponents() {
		model.reset();
		JTabbedPane tabbedPane = new JTabbedPane();

		getTranslationInput(tabbedPane);
		getGeometryInput(tabbedPane);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = NORTHWEST;
		gbc.gridwidth = 1;
		gbc.gridheight = 2;
		gbc.fill = VERTICAL;
		add(tabbedPane, gbc);
		
		gbc.gridheight = 2;
		gbc.fill = VERTICAL;		
		gbc.gridwidth = 1;
		gbc.gridy = 0;
		gbc.gridx = 1;
		gbc.weightx = 0;
		gbc.weighty = 1.0;
		add(zoomInput, gbc);
		zoomInput.addContentChangeListener(this);
		
		gbc.fill = BOTH;
		gbc.gridwidth = 1;
		gbc.weightx = 0;
		gbc.weighty = 1.0;
		gbc.gridx = 2;
		gbc.gridy = 0;
		driveTrainDrawing.setMinimumSize(driveTrainDrawing.getPreferredSize());
		add(driveTrainDrawing, gbc);

		gbc.anchor = NORTHWEST;
		gbc.gridheight = 1;
		gbc.gridx = 3;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.fill = BOTH;
		gbc.gridwidth = REMAINDER;
		add(driveTrainOutput, gbc);
	}

	/**
	 * @param tabbedPane
	 */
	private void getTranslationInput(JTabbedPane tabbedPane) {
		JPanel translationInput = new JPanel(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridwidth = REMAINDER;
		gbc.fill = BOTH;
		gbc.anchor = NORTHWEST;
		gbc.weighty = 1.0;

		chainwheelTranslationInput.addContentChangeListener(this);
		translationInput.add(chainwheelTranslationInput, gbc);

		sprocketTranslationInput.addContentChangeListener(this);
		translationInput.add(sprocketTranslationInput, gbc);

		generalTranslationInput.addContentChangeListener(this);
		translationInput.add(generalTranslationInput, gbc);

		driveTrainDrawing.addContentChangeListener(this);
		driveTrainOutput.addContentChangeListener(this);


		tabbedPane.addTab(Messages.getString("TransmissionDetails"), null, translationInput, Messages.getString("TransmissionDetailsTip"));
	}
	
	/**
	 * @param tabbedPane
	 */
	private void getGeometryInput(JTabbedPane tabbedPane) {
		JPanel geometryInput = new JPanel(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridwidth = REMAINDER;
		gbc.fill = BOTH;
		gbc.anchor = NORTHWEST;
		gbc.weighty = 1.0;

		chainwheelGeometryInput.addContentChangeListener(this);
		geometryInput.add(chainwheelGeometryInput, gbc);

		sprocketGeometryInput.addContentChangeListener(this);
		geometryInput.add(sprocketGeometryInput, gbc);

		generalGeometryInput.addContentChangeListener(this);
		geometryInput.add(generalGeometryInput, gbc);

		driveTrainDrawing.addContentChangeListener(this);
		driveTrainOutput.addContentChangeListener(this);


		tabbedPane.addTab(Messages.getString("GeometryDetails"), null, geometryInput, Messages.getString("GeometryDetailsTip"));
	}

	public void contentChanged(ChangeEvent e) {
		model.setModified(true);
		update();
		driveTrainOutput.dataChanged();
		fireContentChanged();
	}
	
	public void styleChanged(ChangeEvent e) {
		update();
		fireStyleChanged();
	}

	/**
	 * @return Returns the model.
	 */
	public DriveTrain getModel() {
		return model;
	}

	public void setUnitSystem(UnitSystem unitSystem) {
		model.setUnitSystem(unitSystem);
		update();
		driveTrainOutput.structureChanged();
	}

	/**
	 * @param model
	 *            The model to set.
	 */
	public void setModel(DriveTrain model) {
		this.model = model;
		generalTranslationInput.setModel(model);
		generalGeometryInput.setModel(model);
		chainwheelTranslationInput.setModel(model.getChainwheels());
		chainwheelGeometryInput.setModel(model.getChainwheels());
		sprocketTranslationInput.setModel(model.getSprockets());
		sprocketGeometryInput.setModel(model.getSprockets());
		driveTrainDrawing.setModel(model);
		driveTrainOutput.setModel(model);
		update();
		driveTrainOutput.structureChanged();
	}

	public void update() {
		generalTranslationInput.update();
		generalGeometryInput.update();
		driveTrainDrawing.update();
		driveTrainOutput.update();
		chainwheelTranslationInput.update();
		chainwheelGeometryInput.update();
		sprocketTranslationInput.update();
		sprocketGeometryInput.update();
	}

	/**
	 * Prompts the user for a filename and saves the data to the selected file
	 * 
	 */
	public boolean saveAs() {
		JFileChooser fileChooser = new JFileChooser();
		//FileFilter binaryFormatFilter = Filters.BINARY;
		FileFilter xmlFormatFilter = Filters.XML;
		//fileChooser.addChoosableFileFilter(binaryFormatFilter);
		fileChooser.setFileFilter(xmlFormatFilter);
		if(model.getFile() != null) {
			fileChooser.setSelectedFile(model.getFile());
		} else {
			fileChooser.setSelectedFile(new File(getName()));
		}
		int retVal = fileChooser.showSaveDialog(this);
		if (retVal == JFileChooser.APPROVE_OPTION) {
			//boolean binaryFormatSelected = fileChooser.getFileFilter().getDescription().equals(binaryFormatFilter.getDescription()); 
			File selectedFile = fileChooser.getSelectedFile();
			/* if(binaryFormatSelected) {
				if (!selectedFile.getName().toLowerCase().endsWith(".rrp")) { //$NON-NLS-1$
					selectedFile = new File(selectedFile.getAbsolutePath() + ".rrp"); //$NON-NLS-1$
				}
			} else { */
				if (!selectedFile.getName().toLowerCase().endsWith(".xml")
						&& !selectedFile.getName().toLowerCase().endsWith(".txt") ) { //$NON-NLS-1$
					selectedFile = new File(selectedFile.getAbsolutePath() + ".txt"); //$NON-NLS-1$
				}
			//}
			model.setFile(selectedFile);
			model.setName(null);
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
			return writeFile();
		} else {
			return false;
		}
	}

	private boolean writeFile() {
		boolean success;
		if(model.getFile().getName().toLowerCase().endsWith(".rrp")) {
			success = writeBinaryFile();
		} else {
			success = writeXMLFile();
		}
		if(success) {
			fireContentChanged();
		}
		return success;
	}
	
	/**
	 * Writes data out into file specified in model
	 */
	private boolean writeBinaryFile() {
		File selectedFile = model.getFile();
		try {
			ObjectOutputStream ow = new ObjectOutputStream(
					new FileOutputStream(selectedFile));
			ow.writeObject(model);
			ow.close();
			model.reset();
			return true;
		} catch (IOException iox) {
			JOptionPane.showMessageDialog(this, Messages.format(
					"Main.15", iox.getLocalizedMessage()), //$NON-NLS-1$ 
					Messages.getString("Main.16"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
			return false;
		}

	}

	/**
	 * Writes data out into file specified in model
	 */
	private boolean writeXMLFile() {
		File selectedFile = model.getFile();
		try {
			FileOutputStream ow = new FileOutputStream(selectedFile);
			DriveTrainEncoder.encode(ow, model);
			ow.close();
			model.reset();
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

	boolean saveModified() {
		if (model.isModified()) {
			int selectedOption = JOptionPane.showConfirmDialog(this,
					Messages.format("UnsavedChanges.Message", getName()), Messages
							.getString("UnsavedChanges.Title"),
					JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE);
			switch (selectedOption) {
			case JOptionPane.YES_OPTION:
				return save();
			case JOptionPane.NO_OPTION:
				return true;
			default:
				return false;
			}
		} else {
			return true;
		}
	}

	boolean save() {
		if (model.isModified()) {
			if (model.getFile() == null) {
				return saveAs();
			} else {
				return writeFile();
			}
		} else {
			return true;
		}
	}

	public void viewAllGears() {
		getStyle().setGearVisibility(ChainlineStatus.ALL);
		update();
		getGearBoxOutput().dataChanged();
	}
	
	public void viewOKGears() {
		getStyle().setGearVisibility(ChainlineStatus.USABLE);
		update();
		getGearBoxOutput().dataChanged();
	}
	
	public void viewGoodGears() {
		getStyle().setGearVisibility(ChainlineStatus.GOOD);
		update();
		getGearBoxOutput().dataChanged();
	}
	
	public void export() {
		JFileChooser fileChooser = new JFileChooser();
		FileFilter htmlFilter = Filters.HTML;
		FileFilter csvFilter = Filters.CSV;
		fileChooser.setFileFilter(null);
		fileChooser.addChoosableFileFilter(htmlFilter);
		fileChooser.addChoosableFileFilter(csvFilter);

		int retVal = fileChooser.showSaveDialog(this);
		if (retVal == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fileChooser.getSelectedFile();
			boolean htmlSelected = fileChooser.getFileFilter() == htmlFilter;
			if(htmlSelected) {
				if (!selectedFile.getName().toLowerCase().endsWith(".html")
						&& !selectedFile.getName().toLowerCase().endsWith(".htm") ) { //$NON-NLS-1$
					selectedFile = new File(selectedFile.getAbsolutePath() + ".html"); //$NON-NLS-1$
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
				if(htmlSelected) {
					String imageURL = selectedFile.getAbsolutePath().substring(0, selectedFile.getAbsolutePath().length()-4)+"png";
					File imageFile = new File(imageURL);
					FileOutputStream fos = new FileOutputStream(imageFile);
					DriveTrainPNGWriter.writePNG(fos, driveTrainDrawing, driveTrainDrawing.getSize().width, driveTrainDrawing.getSize().height);
					fos.close();
					DriveTrainHTMLWriter.writeHTML(ps, new TableAdapter(model, style), imageFile.getName());
				} else {
					DriveTrainCSVWriter.writeCSV(ps, model);
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
			g.setFont(new Font("Verdana", Font.BOLD, 20));
			g.drawString(model.getFile().getName(), 0, -20);
			g.setFont(new Font("Verdana", Font.PLAIN, 11));
			g.drawString(" "+Messages.getString("Cadence")+": "+model.getCadence().getStringValue(), 0, -3);
			driveTrainOutput.print(g, pageFormat);
			driveTrainDrawing.print(g, pageFormat);
			g.setTransform(origTransform);
			return Printable.PAGE_EXISTS;
		} else {
			return Printable.NO_SUCH_PAGE;
		}
	}

	public boolean close() {
		if(saveModified()) {
			getParent().remove(this);
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Returns the name of this
	 * component
	 */
	@Override 
	public String getName() {
		if (model.getName() == null) {
			model.setName(name);
		}
		return model.getName();
	}

	/**
	 * @return Returns the driveTrainDrawing.
	 */
	public DriveTrainDrawing getGearBoxDrawing() {
		return driveTrainDrawing;
	}

	/**
	 * @return Returns the driveTrainOutput.
	 */
	public DriveTrainTable getGearBoxOutput() {
		return driveTrainOutput;
	}

	public DriveTrainStyle getStyle() {
		return style;
	}



	public Icon getIcon() {
		if(model.isModified()) {
			return SAVE_ICON;
		} else {
			return null;
		}
	}
}
