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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import javax.swing.Action;
import javax.swing.ImageIcon;
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

import de.mutantenzoo.gcu.io.DriveTrainEncoder;
import de.mutantenzoo.gcu.model.DriveTrain;
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
public class Main implements ContentChangeListener {


	private static String[] startupFiles = null;
	
	private JFrame mainFrame;

	private JTabbedPane mainPanel;

	private ActionContainer actions;

	private DriveTrainComparisonView comparisonView = new DriveTrainComparisonView();

	public Main() {
		initComponents();
		for(int n=0; n<startupFiles.length; n++) {
			loadFile(new File(startupFiles[n]));
		}
	}

	@SuppressWarnings("serial")
	public void initComponents() {
		JFrame.setDefaultLookAndFeelDecorated(true);
		JDialog.setDefaultLookAndFeelDecorated(true);
		mainFrame = new JFrame(Messages.getString("Main.2")); //$NON-NLS-1$
		mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		mainFrame.addWindowListener(new WindowAdapter(){@Override public void windowClosing(WindowEvent e) {exit();}});
		mainFrame.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/bicycle.png")));		actions = new ActionContainer();
		mainFrame.setJMenuBar(actions.getMenuBar());
		mainPanel = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
		comparisonView.addComponentListener(new ComponentListener(){

			public void componentResized(ComponentEvent e) {
				// do nothing
				
			}

			public void componentMoved(ComponentEvent e) {
				// do nothing
				
			}

			public void componentShown(ComponentEvent e) {
				setPanelSelected(false);
			}

			public void componentHidden(ComponentEvent e) {
				setPanelSelected(true);
			}
			});
		
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
		
		fileGroup.add(new LocalizedAction("Save") {
			public void actionPerformed(ActionEvent e) {
				getCurrentPanel().save();
			}});
		
		fileGroup.add(new LocalizedAction("SaveAs") {
			public void actionPerformed(ActionEvent e) {
				getCurrentPanel().saveAs();
			}});

		fileGroup.add(new LocalizedAction("SaveAll") {
			public void actionPerformed(ActionEvent e) {
				saveAll();
			}});

		fileGroup.addSeparator();
		fileGroup.add(new LocalizedAction("Print") {
			public void actionPerformed(ActionEvent e) {
				getCurrentView().print();
			}});

		fileGroup.add(new LocalizedAction("Export") {
			public void actionPerformed(ActionEvent e) {
				getCurrentView().export();
			}});

		fileGroup.add(new LocalizedAction("Close"){
			public void actionPerformed(ActionEvent e) {
				closeCurrent();
			}});

		fileGroup.add(new LocalizedAction("CloseAll"){
			public void actionPerformed(ActionEvent e) {
				closeAll();
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
				getCurrentView().viewAllGears();
			}});
		
		viewGroup.add(new LocalizedAction("Usable"){
			public void actionPerformed(ActionEvent e) {
				getCurrentView().viewOKGears();
			}});
		
		viewGroup.add(new LocalizedAction("Good"){
			public void actionPerformed(ActionEvent e) {
				getCurrentView().viewGoodGears();
			}});

		ActionGroup optionsGroup = actions.getActionGroup("Options");
		optionsGroup.add(new LocalizedAction("Metric"){
			public void actionPerformed(ActionEvent e) {
				getCurrentView().setUnitSystem(UnitSystem.METRIC);
			}});
		optionsGroup.add(new LocalizedAction("Imperial"){
			public void actionPerformed(ActionEvent e) {
				getCurrentView().setUnitSystem(UnitSystem.IMPERIAL);
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
		

		mainFrame.getContentPane().add(toolbarPanel,
				BorderLayout.PAGE_START);
		mainFrame.getContentPane().add(mainPanel, BorderLayout.CENTER);
		newProfile();
		mainFrame.pack();
		mainFrame.setVisible(true);
	}

	protected void saveAll() {
		for(int n=0; n < mainPanel.getTabCount(); n++) {
			((DriveTrainPanel)mainPanel.getComponentAt(n)).save();
		}
		
	}

	private void closeCurrent() {
		if(mainPanel.getTabCount() == 1) {
			setActionsEnabled(false);
		}
		comparisonView.removeModel(getCurrentPanel().getModel());
		getCurrentPanel().close();
		
	}
	
	private void setActionsEnabled(boolean enabled) {
		ActionGroup file = actions.getActionGroup("File");
		file.getAction("Save").setEnabled(enabled);
		file.getAction("SaveAs").setEnabled(enabled);
		file.getAction("SaveAll").setEnabled(enabled);
		file.getAction("Print").setEnabled(enabled);
		file.getAction("Export").setEnabled(enabled);
		file.getAction("Close").setEnabled(enabled);
		file.getAction("CloseAll").setEnabled(enabled);
		
		actions.getMenuBar().getMenu(1).setEnabled(enabled);
		
		ActionGroup view = actions.getActionGroup("View");
		view.getAction("All").setEnabled(enabled);
		view.getAction("Usable").setEnabled(enabled);
		view.getAction("Good").setEnabled(enabled);
		
		ActionGroup options = actions.getActionGroup("Options");
		options.getAction("Metric").setEnabled(enabled);
		options.getAction("Imperial").setEnabled(enabled);
		if(enabled) {
			mainPanel.addTab(Messages.getString("Comparison"), comparisonView);
		} else {
			mainPanel.removeTabAt(0);
		}
	}

	private void setPanelSelected(boolean enabled) {
		ActionGroup file = actions.getActionGroup("File");
		file.getAction("Save").setEnabled(enabled);
		file.getAction("SaveAs").setEnabled(enabled);
		file.getAction("Close").setEnabled(enabled);
	}

	private void closeAll() {
		for(int n=0; n<mainPanel.getTabCount(); n++) {
			Component component = mainPanel.getComponentAt(n);
			if(component instanceof DriveTrainPanel) {
				DriveTrainPanel panel = (DriveTrainPanel)component;
				if(panel.close()) {
					comparisonView.removeModel(panel.getModel());
				}
			}
		}
		if(mainPanel.getTabCount() == 1) {
			setActionsEnabled(false);
		}
	}

	private JMenuItem createStandardMenuItem(String textKey, String iconName, int mnemonicKey, char keyChar, Action action) {
		JMenuItem menuItem = new JMenuItem(action);
		menuItem.setIcon(new ImageIcon(Main.class.getResource("/toolbarButtonGraphics/general/"+iconName+"16.gif")));
		menuItem.setText(Messages.getString(textKey));
		menuItem.setMnemonic(mnemonicKey);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(keyChar, KeyEvent.CTRL_MASK));
		return menuItem;
	}
	

	public static void main(String[] args) throws InterruptedException {
		System.setProperty("swing.aatext","true");
		startupFiles = args;
		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new Main();
			}
		});
	}

	
	private void newProfile() {
		createView();
	}

	/**
	 * Prompts the user for a file to open and opens the specified file
	 * 
	 */
	private void open() {
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

	/**
	 * @param selectedFile
	 */
	private void loadBinaryFile(File selectedFile) {
		try {
			ObjectInputStream or = new ObjectInputStream(
					new FileInputStream(selectedFile));
			DriveTrain newModel = (DriveTrain) or.readObject();
			or.close();
			newModel.setFile(selectedFile);
			newModel.reset();
			createView(newModel);
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
	 * @param newModel
	 */
	private void createView(DriveTrain newModel) {
		DriveTrainPanel panel = new DriveTrainPanel();
		panel.setModel(newModel);
		setActionsEnabled(true);		
		mainPanel.insertTab(panel.getName(), panel.getIcon(), panel, panel.getName(), mainPanel.getTabCount()-1);
		comparisonView.addModel(newModel);
		panel.addContentChangeListener(this);
	}

	private void createView() {
		DriveTrainPanel panel = new DriveTrainPanel();
		setActionsEnabled(true);
		mainPanel.insertTab(panel.getName(), panel.getIcon(), panel, panel.getName(), mainPanel.getTabCount()-1);
		comparisonView.addModel(panel.getModel());
		panel.addContentChangeListener(this);
	}

	/**
	 * @param selectedFile
	 */
	private void loadXMLFile(File selectedFile) {
		try {
			FileInputStream is = new FileInputStream(selectedFile);
			DriveTrain newModel = DriveTrainEncoder.decode(is);
			is.close();
			newModel.setFile(selectedFile);
			newModel.reset();
			createView(newModel);
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

	private GearView getCurrentView() {
		return (GearView)mainPanel.getSelectedComponent();
	}

	private DriveTrainPanel getCurrentPanel() {
		return (DriveTrainPanel)mainPanel.getSelectedComponent();
	}
	
	void exit() {
		// GearBoxWriter.print(model);
		if (saveModified()) {
			mainFrame.dispose();
			System.exit(0);
		}
	}

	private boolean saveModified() {
		for(int n=1; n<mainPanel.getTabCount(); n++) {
			Component component = mainPanel.getComponentAt(n);
			if(component instanceof DriveTrainPanel &&
			    !((DriveTrainPanel)component).saveModified() ) {
				return false;
			}
		}
		return true;
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
	
	private String getWindowTitle() {
		String title = Messages.getString("Main.2");
		String modified = getCurrentPanel().getModel().isModified() ? "*" : "";
		return getCurrentPanel().getName() + modified + " - " + title;
	}

	public void contentChanged(ChangeEvent e) {
		mainFrame.setTitle(getWindowTitle());
		mainPanel.setTitleAt(mainPanel.getSelectedIndex(), getCurrentPanel().getName());
		mainPanel.setIconAt(mainPanel.getSelectedIndex(), getCurrentPanel().getIcon());
	}

	public void styleChanged(ChangeEvent e) {
		// empty operation
	}



}
