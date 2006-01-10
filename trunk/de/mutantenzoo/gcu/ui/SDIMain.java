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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.DefaultEditorKit;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import de.mutantenzoo.gcu.io.DriveTrainCSVWriter;
import de.mutantenzoo.gcu.io.DriveTrainEncoder;
import de.mutantenzoo.gcu.io.DriveTrainHTMLWriter;
import de.mutantenzoo.gcu.model.ChainlineStatus;
import de.mutantenzoo.gcu.model.DriveTrain;
import de.mutantenzoo.gcu.model.DriveTrainStyle;
import de.mutantenzoo.gcu.units.UnitSystem;
import de.mutantenzoo.raf.ActionContainer;
import de.mutantenzoo.raf.ActionGroup;
import de.mutantenzoo.raf.ContentChangeListener;
import de.mutantenzoo.raf.HelpPane;
import de.mutantenzoo.raf.LocalizedAction;

/**
 * @author MKlemm
 *
 */
public class SDIMain implements ContentChangeListener, Printable {


	private static final GridBagConstraints gbc = new GridBagConstraints();
	private static String startupFile = null;
	private JFrame mainFrame;
	private static final int DEFAULT_CHAINWHEEL_COUNT = 3;
	private static final int DEFAULT_SPROCKET_COUNT = 10;

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

	private JButton recomputeButton = new JButton();
	private JPanel mainPanel;

	private DriveTrainTable driveTrainOutput = new DriveTrainTable(model, style);

	private DriveTrainDrawing driveTrainDrawing = new DriveTrainDrawing(model, style);
	private ZoomInput zoomInput = new ZoomInput(model, style);
	private ActionContainer actions;

	public SDIMain() {
		initComponents();
		if(startupFile != null) {
			loadFile(new File(startupFile));
		}
	}

	@SuppressWarnings("serial")
	public void initComponents() {
		JFrame.setDefaultLookAndFeelDecorated(true);
		JDialog.setDefaultLookAndFeelDecorated(true);
		model.reset();
		mainFrame = new JFrame(Messages.getString("Main.2")); //$NON-NLS-1$
		mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		mainFrame.addWindowListener(new WindowAdapter(){@Override public void windowClosing(WindowEvent e) {exit();}});
		mainFrame.getRootPane().setDefaultButton(recomputeButton);
		mainFrame.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/bicycle.png")));		actions = new ActionContainer();
		mainFrame.setJMenuBar(actions.getMenuBar());
		mainPanel = new JPanel(new GridBagLayout());
		JTabbedPane tabbedPane = new JTabbedPane();

		getTranslationInput(tabbedPane);
		getGeometryInput(tabbedPane);
		
		gbc.anchor = NORTHWEST;
		gbc.gridwidth = 1;
		gbc.gridheight = 2;
		gbc.fill = VERTICAL;
		mainPanel.add(tabbedPane, gbc);
		
		ActionGroup fileGroup = actions.getActionGroup("File");
		fileGroup.add(new LocalizedAction("New"){
			public void actionPerformed(ActionEvent e) {
				newProfile();
			}});
		
		fileGroup.addSeparator();
		
		fileGroup.add(new LocalizedAction("Open") {
			public void actionPerformed(ActionEvent e) {
				open();
			}});
		
		fileGroup.add(new LocalizedAction("Save") {	public void actionPerformed(ActionEvent e) {save();}});
		
		fileGroup.add(new LocalizedAction("SaveAs") {
			public void actionPerformed(ActionEvent e) {
				saveAs();
			}});

		fileGroup.addSeparator();
		fileGroup.add(new LocalizedAction("Print") {
			public void actionPerformed(ActionEvent e) {
				print();
			}});

		fileGroup.add(new LocalizedAction("Export") {
			public void actionPerformed(ActionEvent e) {
				export();
			}});
		
		fileGroup.addSeparator();
		fileGroup.add(new LocalizedAction("Exit"){
			public void actionPerformed(ActionEvent e) {
				exit();
			}});

		
		ActionGroup editGroup = actions.getActionGroup("Edit");
		JMenu editMenu = editGroup.getMenu();
		editMenu.setMnemonic(KeyEvent.VK_E);
		
		editMenu.add(createStandardMenuItem("Edit.Cut", "Cut", KeyEvent.VK_T, 'X', new DefaultEditorKit.CutAction()));
		editMenu.add(createStandardMenuItem("Edit.Copy", "Copy", KeyEvent.VK_C, 'C', new DefaultEditorKit.CopyAction()));
		editMenu.add(createStandardMenuItem("Edit.Paste", "Paste", KeyEvent.VK_P, 'V', new DefaultEditorKit.PasteAction()));
		
		
		ActionGroup viewGroup = actions.getActionGroup("View");
		viewGroup.add(new LocalizedAction("All") {
			public void actionPerformed(ActionEvent e) {
				viewAllGears();
			}});
		
		viewGroup.add(new LocalizedAction("Usable"){
			public void actionPerformed(ActionEvent e) {
				viewOKGears();
			}});
		
		viewGroup.add(new LocalizedAction("Good"){
			public void actionPerformed(ActionEvent e) {
				viewGoodGears();
			}});

		ActionGroup optionsGroup = actions.getActionGroup("Options");
		optionsGroup.add(new LocalizedAction("Metric"){
			public void actionPerformed(ActionEvent e) {
				setUnitSystem(UnitSystem.METRIC);
			}});
		optionsGroup.add(new LocalizedAction("Imperial"){
			public void actionPerformed(ActionEvent e) {
				setUnitSystem(UnitSystem.IMPERIAL);
			}});

		ActionGroup helpGroup = actions.getActionGroup("Help");
		helpGroup.add(new LocalizedAction("Help"){
			public void actionPerformed(ActionEvent e) {
				help();
			}});
		
		helpGroup.addSeparator();
		helpGroup.add(new LocalizedAction("About"){
			public void actionPerformed(ActionEvent e) {
				about();
			}});

		JPanel toolbarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		toolbarPanel.add(fileGroup.getToolBar());
		toolbarPanel.add(viewGroup.getToolBar());
		
		gbc.gridheight = 2;
		gbc.fill = VERTICAL;		
		gbc.gridwidth = 1;
		gbc.gridy = 0;
		gbc.gridx = 1;
		gbc.weightx = 0;
		gbc.weighty = 1.0;
		mainPanel.add(zoomInput, gbc);
		zoomInput.setMinimumSize(new Dimension(driveTrainDrawing.getPreferredSize().width, zoomInput.getPreferredSize().height));
		zoomInput.addContentChangeListener(this);
		
		gbc.fill = BOTH;
		gbc.gridwidth = 1;
		gbc.weightx = 0;
		gbc.weighty = 1.0;
		gbc.gridx = 2;
		gbc.gridy = 0;
		driveTrainDrawing.setMinimumSize(driveTrainDrawing.getPreferredSize());
		mainPanel.add(driveTrainDrawing, gbc);

		gbc.anchor = NORTHWEST;
		gbc.gridheight = 1;
		gbc.gridx = 3;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.fill = BOTH;
		gbc.gridwidth = REMAINDER;
		mainPanel.add(driveTrainOutput, gbc);

		mainFrame.getContentPane().add(toolbarPanel,
				BorderLayout.PAGE_START);
		mainFrame.getContentPane().add(mainPanel, BorderLayout.CENTER);
		mainFrame.pack();
		mainFrame.setVisible(true);
	}

	private JMenuItem createStandardMenuItem(String textKey, String iconName, int mnemonicKey, char keyChar, Action action) {
		JMenuItem menuItem = new JMenuItem(action);
		menuItem.setIcon(new ImageIcon(SDIMain.class.getResource("/toolbarButtonGraphics/general/"+iconName+"16.gif")));
		menuItem.setText(Messages.getString(textKey));
		menuItem.setMnemonic(mnemonicKey);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(keyChar, KeyEvent.CTRL_MASK));
		return menuItem;
	}
	/**
	 * @param tabbedPane
	 */
	private void getTranslationInput(JTabbedPane tabbedPane) {
		JPanel translationInput = new JPanel(new GridBagLayout());

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
	}
	
	public void styleChanged(ChangeEvent e) {
		update();
	}

	public static void main(String[] args) throws InterruptedException {
		System.setProperty("swing.aatext","true");
		if(args.length > 0) {
			startupFile = args[0];
		}
		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new SDIMain();
			}
		});
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
		mainFrame.setTitle(getWindowTitle());
		generalTranslationInput.update();
		generalGeometryInput.update();
		driveTrainDrawing.update();
		driveTrainOutput.update();
		chainwheelTranslationInput.update();
		chainwheelGeometryInput.update();
		sprocketTranslationInput.update();
		sprocketGeometryInput.update();
	}

	public JFrame getMainFrame() {
		return mainFrame;
	}

	private void newProfile() {
		if (saveModified()) {
			setModel(new DriveTrain(DEFAULT_CHAINWHEEL_COUNT, DEFAULT_SPROCKET_COUNT));
		}
	}

	/**
	 * Prompts the user for a file to open and opens the specified file
	 * 
	 */
	private void open() {
		if (saveModified()) {
			JFileChooser fileChooser = new JFileChooser();
			FileFilter binaryFormatFilter = new BinaryFormatFileFilter();
			FileFilter xmlFormatFilter = new XMLFormatFileFilter();
			fileChooser.addChoosableFileFilter(binaryFormatFilter);
			fileChooser.addChoosableFileFilter(xmlFormatFilter);
			int retVal = fileChooser.showOpenDialog(mainFrame.getRootPane());
			if (retVal == JFileChooser.APPROVE_OPTION) {
				File selectedFile = fileChooser.getSelectedFile();
				loadFile(selectedFile);
			}
		}
	}

	/**
	 * @param selectedFile
	 */
	private void loadBinaryFile(File selectedFile) {
		try {
			ObjectInputStream or = new ObjectInputStream(
					new FileInputStream(selectedFile));
			DriveTrain newModel = (DriveTrain) or.readObject();
			newModel.setFile(selectedFile);
			newModel.reset();
			setModel(newModel);
			or.close();
		} catch (IOException iox) {
			JOptionPane
					.showMessageDialog(
							mainFrame,
							Messages
									.format(
											"Main.19", iox.getLocalizedMessage()), Messages.getString("Main.20"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$ //$NON-NLS-2$
		} catch (ClassNotFoundException ex) {
			JOptionPane
					.showMessageDialog(
							mainFrame,
							Messages.getString("Main.21"), Messages.getString("Main.22"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	/**
	 * @param selectedFile
	 */
	private void loadXMLFile(File selectedFile) {
		try {
			FileInputStream is = new FileInputStream(selectedFile);
			DriveTrain newModel = DriveTrainEncoder.decode(is);
			newModel.setFile(selectedFile);
			newModel.reset();
			setModel(newModel);
			is.close();
		} catch (IOException iox) {
			JOptionPane
					.showMessageDialog(
							mainFrame,
							Messages
									.format(
											"Main.19", iox.getLocalizedMessage()), Messages.getString("Main.20"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$ //$NON-NLS-2$
		} catch (Exception ex) {
			JOptionPane
					.showMessageDialog(
							mainFrame,
							Messages.getString("XMLError"), Messages.getString("Main.22"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	/**
	 * loads Data from a file in
	 * any supported format
	 * @param selectedFile
	 */
	private void loadFile(File selectedFile) {
		if(selectedFile.getName().toLowerCase().endsWith(".rrp")) {
			loadBinaryFile(selectedFile);
		} else {
			loadXMLFile(selectedFile);
		}
	}

	/**
	 * Prompts the user for a filename and saves the data to the selected file
	 * 
	 */
	private void saveAs() {
		JFileChooser fileChooser = new JFileChooser();
		FileFilter binaryFormatFilter = new BinaryFormatFileFilter();
		FileFilter xmlFormatFilter = new XMLFormatFileFilter();
		fileChooser.addChoosableFileFilter(binaryFormatFilter);
		fileChooser.addChoosableFileFilter(xmlFormatFilter);

		int retVal = fileChooser.showSaveDialog(mainFrame.getRootPane());
		if (retVal == JFileChooser.APPROVE_OPTION) {
			boolean binaryFormatSelected = fileChooser.getFileFilter().getDescription().equals(binaryFormatFilter.getDescription()); 
			File selectedFile = fileChooser.getSelectedFile();
			if(binaryFormatSelected) {
				if (!selectedFile.getName().toLowerCase().endsWith(".rrp")) { //$NON-NLS-1$
					selectedFile = new File(selectedFile.getAbsolutePath() + ".rrp"); //$NON-NLS-1$
				}
			} else {
				if (!selectedFile.getName().toLowerCase().endsWith(".xml")
						&& !selectedFile.getName().toLowerCase().endsWith(".txt") ) { //$NON-NLS-1$
					selectedFile = new File(selectedFile.getAbsolutePath() + ".txt"); //$NON-NLS-1$
				}
			}
			model.setFile(selectedFile);
			if (selectedFile.exists()) {
				if (selectedFile.canWrite()) {
					int chosen = JOptionPane
							.showConfirmDialog(
									mainFrame,
									Messages.getString("Main.11"), Messages.getString("Main.12"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE); //$NON-NLS-1$ //$NON-NLS-2$
					if (chosen == JOptionPane.NO_OPTION) {
						return;
					}
				} else {
					JOptionPane
							.showMessageDialog(
									mainFrame,
									Messages.getString("Main.13"), Messages.getString("Main.14"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$ //$NON-NLS-2$
					return;
				}
			}
			writeFile();
		}

	}

	private void writeFile() {
		if(model.getFile().getName().toLowerCase().endsWith(".rrp")) {
			writeBinaryFile();
		} else {
			writeXMLFile();
		}
	}
	
	/**
	 * Writes data out into file specified in model
	 */
	private void writeBinaryFile() {
		File selectedFile = model.getFile();
		try {
			ObjectOutputStream ow = new ObjectOutputStream(
					new FileOutputStream(selectedFile));
			ow.writeObject(model);
			ow.close();
			model.reset();
			mainFrame.setTitle(getWindowTitle());
		} catch (IOException iox) {
			JOptionPane.showMessageDialog(mainFrame, Messages.format(
					"Main.15", iox.getLocalizedMessage()), //$NON-NLS-1$ 
					Messages.getString("Main.16"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
		}

	}

	/**
	 * Writes data out into file specified in model
	 */
	private void writeXMLFile() {
		File selectedFile = model.getFile();
		try {
			FileOutputStream ow = new FileOutputStream(selectedFile);
			DriveTrainEncoder.encode(ow, model);
			ow.close();
			model.reset();
			mainFrame.setTitle(getWindowTitle());
		} catch (IOException iox) {
			JOptionPane.showMessageDialog(mainFrame, Messages.format(
					"Main.15", iox.getLocalizedMessage()), //$NON-NLS-1$ 
					Messages.getString("Main.16"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
		} catch (ParserConfigurationException iox) {
			JOptionPane.showMessageDialog(mainFrame, Messages.format(
					"ParserConfigurationException", iox.getLocalizedMessage()), //$NON-NLS-1$ 
					Messages.getString("Main.16"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
		} catch (TransformerException iox) {
			JOptionPane.showMessageDialog(mainFrame, Messages.format(
					"TransformerException", iox.getLocalizedMessage()), //$NON-NLS-1$ 
					Messages.getString("Main.16"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
		}

	}

	void exit() {
		// GearBoxWriter.print(model);
		if (saveModified()) {
			mainFrame.dispose();
			System.exit(0);
		}
	}

	private boolean saveModified() {
		if (model.isModified()) {
			int selectedOption = JOptionPane.showConfirmDialog(mainFrame,
					Messages.getString("UnsavedChanges.Message"), Messages
							.getString("UnsavedChanges.Title"),
					JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE);
			switch (selectedOption) {
			case JOptionPane.YES_OPTION:
				save();
				return true;
			case JOptionPane.NO_OPTION:
				return true;
			default:
				return false;
			}
		} else {
			return true;
		}
	}

	private void save() {
		if (model.isModified()) {
			if (model.getFile() == null) {
				saveAs();
			} else {
				writeFile();
			}
		}
	}

	void about() {
		HelpPane helpPane = new HelpPane(Messages.getString("Help.License.Name"));
		helpPane.setPreferredSize(new Dimension(500,300));
		JPanel panel = new JPanel(new BorderLayout());
		JLabel label = new JLabel(Messages.getString("Help.About"));
		label.setPreferredSize(new Dimension(500,250));
		label.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		panel.add(label, BorderLayout.NORTH);
		panel.add(helpPane, BorderLayout.CENTER);
		JOptionPane.showMessageDialog(mainFrame,
				panel,
				Messages.getString("Help.About.ShortDescription"),
				JOptionPane.INFORMATION_MESSAGE);
	}

	void help() {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setContentPane(new HelpPane(Messages.getString("Help.URL")));
		frame.setTitle(Messages.getString("Help.Title"));
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/toolbarButtonGraphics/general/Help24.gif")));
		frame.pack();
		frame.setVisible(true);
	}
	
	private void viewAllGears() {
		getStyle().setGearVisibility(ChainlineStatus.ALL);
		update();
		getGearBoxOutput().dataChanged();
	}
	
	private void viewOKGears() {
		getStyle().setGearVisibility(ChainlineStatus.USABLE);
		update();
		getGearBoxOutput().dataChanged();
	}
	
	private void viewGoodGears() {
		getStyle().setGearVisibility(ChainlineStatus.GOOD);
		update();
		getGearBoxOutput().dataChanged();
	}
	
	private void export() {
		JFileChooser fileChooser = new JFileChooser();
		FileFilter htmlFilter = new HTMLFileFilter();
		FileFilter csvFilter = new CSVFileFilter();
		fileChooser.setFileFilter(null);
		fileChooser.addChoosableFileFilter(htmlFilter);
		fileChooser.addChoosableFileFilter(csvFilter);

		int retVal = fileChooser.showSaveDialog(mainFrame.getRootPane());
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
									mainFrame,
									Messages.getString("Main.11"), Messages.getString("Main.12"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE); //$NON-NLS-1$ //$NON-NLS-2$
					if (chosen == JOptionPane.NO_OPTION) {
						return;
					}
				} else {
					JOptionPane
							.showMessageDialog(
									mainFrame,
									Messages.getString("Main.13"), Messages.getString("Main.14"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$ //$NON-NLS-2$
					return;
				}
			}
			PrintStream ps;
			try {
				ps = new PrintStream(new FileOutputStream(selectedFile));
				if(htmlSelected) {
					DriveTrainHTMLWriter.writeHTML(ps, new TableAdapter(model, style));
				} else {
					DriveTrainCSVWriter.writeCSV(ps, model);
				}
				ps.close();
			} catch (FileNotFoundException e) {
				JOptionPane.showMessageDialog(mainFrame, Messages.format(
						"Main.15", e.getLocalizedMessage()), //$NON-NLS-1$ 
						Messages.getString("Main.16"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$

			}
		}


	}
	
	private void print() {
		PrinterJob pj = PrinterJob.getPrinterJob();
		pj.setPrintable(this);
		pj.setJobName(Messages.getString("GCUPrintJob"));
		if(pj.printDialog()) {
			try {
				pj.print();
			} catch (PrinterException e) {
				JOptionPane.showMessageDialog(mainFrame, Messages.format(
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


	private String getWindowTitle() {
		String title = Messages.getString("Main.2");
		String fileName;
		if (model.getFile() == null) {
			fileName = Messages.getString("Unnamed");
		} else {
			fileName = model.getFile().getName();
		}
		String modified = model.isModified() ? "*" : "";
		return fileName + modified + " - " + title;
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

	private final class XMLFormatFileFilter extends FileFilter {
		@Override
		public boolean accept(File file) {
			return file.getName().toLowerCase().endsWith(".xml") //$NON-NLS-1$
			|| file.getName().toLowerCase().endsWith(".txt") //$NON-NLS-1$
			|| file.isDirectory(); 
		}

		@Override
		public String getDescription() {
			return Messages.getString("XMLFileFormat"); //$NON-NLS-1$
		}
	}

	private final class BinaryFormatFileFilter extends FileFilter {
		@Override
		public boolean accept(File file) {
			return file.getName().toLowerCase().endsWith(".rrp") || file.isDirectory(); //$NON-NLS-1$
		}

		@Override
		public String getDescription() {
			return Messages.getString("Main.18"); //$NON-NLS-1$
		}
	}

	/**
	 * @author MKlemm
	 *
	 */
	public class HTMLFileFilter extends FileFilter {
		@Override
		public boolean accept(File file) {
			return file.getName().toLowerCase().endsWith(".htm") //$NON-NLS-1$
			|| file.getName().toLowerCase().endsWith(".html") //$NON-NLS-1$
			|| file.isDirectory(); 
		}

		@Override
		public String getDescription() {
			return Messages.getString("HTMLFileFormat"); //$NON-NLS-1$
		}
	}

	/**
	 * @author MKlemm
	 *
	 */
	public class CSVFileFilter extends FileFilter {
		@Override
		public boolean accept(File file) {
			return file.getName().toLowerCase().endsWith(".csv") //$NON-NLS-1$
			|| file.isDirectory(); 
		}

		@Override
		public String getDescription() {
			return Messages.getString("CSVFileFormat"); //$NON-NLS-1$
		}
	}



}
