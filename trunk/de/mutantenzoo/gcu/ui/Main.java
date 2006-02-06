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
import java.util.Properties;

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
 * Main entry point for the GCU application.
 * COntains the main window and manages the MDI.
 * Directly controls the comparison view,
 * drive train views are mostly self-contained.
 * @author MKlemm
 * 
 */
public class Main implements ContentChangeListener, Runnable {


	private String[] startupFiles;
	
	private JFrame mainFrame = null;

	private JTabbedPane mainPanel = null;

	private ActionContainer actions = null;

	private ImageIcon comparisonIcon = null;

	private DriveTrainComparisonView comparisonView = null;
	
	/**
	 * The main application entry point.
	 * All work is delgated to an instance of "Main",
	 * Main.run() is invoked asynchronosly using the
	 * SwingUtils.invokeLater() method.
	 * @param args An array of profile filenames that will be opened on application startup.
	 * @throws InterruptedException if the asynchronos invocation of Main.run() is interrupted.
	 */
	public static void main(String[] args) throws InterruptedException {
		System.setProperty("swing.aatext","true");
		Main main = new Main(args);
		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(main);
	}

	///////////////////////////////////////////////////////
	//[start] General initialization code
	
	/**
	 * Constructor for the Main class,
	 * initializes an empty Main object and
	 * stores the array of file names to
	 * load on startup.
	 * Since Main is a Runnable class,
	 * the actual initialization work is done in
	 * @see run().
	 * @param startupFiles An array of file names to load on app
	 * startup.
	 */
	public Main(String[] startupFiles) {
		this.startupFiles = startupFiles;
	}
	
	/**
	 * Pulls up the GUI and loads the initial
	 * files specified in the constructor, or
	 * creates an empty profile, if no startup
	 * files have been specified.
	 */
	public void run() {
		initComponents();
		for(int n=0; n<startupFiles.length; n++) {
			loadFile(new File(startupFiles[n]));
		}
		if(startupFiles.length == 0) {
			newProfile();
		}
		mainFrame.pack();
		mainFrame.setVisible(true);
	}

	/**
	 * Creates all the GUI child components,
	 * menus, and toolbars, and wires up the event
	 * handlers.
	 */
	@SuppressWarnings("serial")
	private void initComponents() {
		JFrame.setDefaultLookAndFeelDecorated(true);
		JDialog.setDefaultLookAndFeelDecorated(true);
		mainFrame = new JFrame(Messages.getString("Main.2")); //$NON-NLS-1$
		mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		mainFrame.addWindowListener(new WindowAdapter(){@Override public void windowClosing(WindowEvent e) {exit(); }});
		mainFrame.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/bicycle.png")));		actions = new ActionContainer();
		mainFrame.setJMenuBar(actions.getMenuBar());
		mainPanel = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
		comparisonView = new DriveTrainComparisonView();
		comparisonView.addComponentListener(new TabListener(false));
		
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
				getCurrentView().saveAs();
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
	}

	/**
	 * Creates a standard "Edit"-Menu entry
	 * @param textKey A key of the localized resource entry that will be used as the label of the action button and menu.
	 * @param iconName The name of an icon to load from the application's resource base.
	 * @param mnemonicKey A mnemonic keystroke code to use as the menu mnemonic.
	 * @param keyChar A character that will be used as the keyboard accelerator for the action.
	 * @param action An Action object for the menu item.
	 * @return a menu item that represents the defined standard edit action.
	 */
	private JMenuItem createStandardMenuItem(String textKey, String iconName, int mnemonicKey, char keyChar, Action action) {
		JMenuItem menuItem = new JMenuItem(action);
		menuItem.setIcon(new ImageIcon(Main.class.getResource("/toolbarButtonGraphics/general/"+iconName+"16.gif")));
		menuItem.setText(Messages.getString(textKey));
		menuItem.setMnemonic(mnemonicKey);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(keyChar, KeyEvent.CTRL_MASK));
		return menuItem;
	}
	
	//[end]

	///////////////////////////////////////////////////////
	//[start] User action handlers

	/**
	 * Handles the "Save All" User action.
	 * Saves all open profiles to their
	 * respective files, and prompts for
	 * a file name to save previously
	 * unsaved profiles to.
	 */
	private void saveAll() {
		for(int n=0; n < mainPanel.getTabCount()-1; n++) {
			mainPanel.setSelectedIndex(n);
			((DriveTrainPanel)mainPanel.getComponentAt(n)).save();
		}
		
	}

	/**
	 * Handles the "Close Current Profile" user action.
	 * Closes the current tab view and prompts for save
	 * if there are unsaved changes.
	 */
	private void closeCurrent() {
		if(mainPanel.getTabCount() == 1) {
			setProfileOpened(false);
		}
		comparisonView.removeModel(getCurrentPanel().getModel());
		getCurrentPanel().close();		
	}

	/**
	 * Handles the "Close All" user action.
	 * Closes all profile tabs and prompt
	 * for save of unsaved changes.
	 */
	private void closeAll() {
		int skippedTabs = 1; 
		while(mainPanel.getTabCount() > skippedTabs) {
			GearView view = (GearView)mainPanel.getComponentAt(skippedTabs-1);
			if(view.close()) {
				comparisonView.removeModel(((DriveTrainPanel)view).getModel());
			} else {
				skippedTabs++;
			}
		}
		if(mainPanel.getTabCount() == 1) {
			setProfileOpened(false);
		}
	}

	/**
	 * Handles the "New Profile" user action.
	 * Creates a new tab with a new empty profile in it.
	 */
	private void newProfile() {
		createView();
	}

	/**
	 * Handles the "Open Profile" user action.
	 * Prompts the user for a file to open and opens the specified file
	 */
	private void open() {
		JFileChooser fileChooser = new JFileChooser();
		/* binary support removed */
		//FileFilter binaryFormatFilter = SuffixFileFilter.BINARY;
		fileChooser.setMultiSelectionEnabled(true);
		//fileChooser.addChoosableFileFilter(binaryFormatFilter);
		fileChooser.setFileFilter(SuffixFileFilter.XML);
		int retVal = fileChooser.showOpenDialog(mainFrame.getRootPane());
		if (retVal == JFileChooser.APPROVE_OPTION) {
			File[] selectedFiles = fileChooser.getSelectedFiles();
			for(int n=0; n<selectedFiles.length; n++) {
				File selectedFile = selectedFiles[n];
				loadFile(selectedFile);
			}
		}
	}

	/**
	 * Handles the "Help Contents" user action.
	 * Shows a windows which contains the HTML
	 * on-line help document.
	 */
	private void help() {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setContentPane(new HelpPane(Messages.getString("Help.URL")));
		frame.setTitle(Messages.getString("Help.Title"));
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/toolbarButtonGraphics/general/Help24.gif")));
		frame.pack();
		frame.setVisible(true);
	}

	/**
	 * Handles the "About" user action.
	 * Shows a dialog with version information,
	 * copyright and GPL.
	 */
	private void about() {
		Properties versionProperties = new Properties();
		try {
			versionProperties.load(getClass().getResourceAsStream("/build.number"));
		} catch (IOException e) {
			versionProperties.setProperty("build.number","?");
			versionProperties.setProperty("build.version","?");
		}
		HelpPane helpPane = new HelpPane(Messages.getString("Help.License.Name"));
		helpPane.setPreferredSize(new Dimension(500,300));
		JPanel panel = new JPanel(new BorderLayout());
		JLabel label = new JLabel(Messages.format("Help.About", versionProperties.getProperty("build.version"), versionProperties.getProperty("build.number")));
		label.setPreferredSize(new Dimension(500,250));
		label.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		panel.add(label, BorderLayout.NORTH);
		panel.add(helpPane, BorderLayout.CENTER);
		JOptionPane.showMessageDialog(mainFrame,
				panel,
				Messages.getString("Help.About.ShortDescription"),
				JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * Handles the "Exit" user action, including
	 * the "Close Window" action.
	 * Propmpts for to save unsaved changes.
	 */
	private void exit() {
		if (saveModified()) {
			mainFrame.dispose();
			System.exit(0);
		}
	}

	///////////////////////////////////////////////////////
	// Support methods for user action handling

	/**
	 * Utility method that loads Data from a file in
	 * any supported format.
	 * Uses loadBinaryFile() and loadXMLFile() internally
	 * after deciding which file type should be loaded.
	 * @param selectedFile The file to load.
	 */
	private void loadFile(File selectedFile) {
		if(selectedFile.getName().toLowerCase().endsWith(".rrp")) {
			loadBinaryFile(selectedFile);
		} else {
			loadXMLFile(selectedFile);
		}
	}

	/**
	 * Utility method to load a file in the
	 * GCU 1.x "*.rrp" format.
	 * @param selectedFile The file to load
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
	 * Utility method to load a file in XML format.
	 * @param selectedFile The file to open.
	 */
	private void loadXMLFile(File selectedFile) {
		try {
			FileInputStream is = new FileInputStream(selectedFile);
			DriveTrain[] newModels = DriveTrainEncoder.decode(is);
			is.close();
			for(int n=0; n<newModels.length; n++) {
				DriveTrain newModel = newModels[n];
				if(newModels.length==1) {
					newModel.setFile(selectedFile);
				}
				newModel.reset();
				createView(newModel);
			}
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
	 * Utility method that prompts the user
	 * to save all modifications.
	 * @return A flag that indicates if the save
	 * operation has succeeded. Returns "true" if
	 * either all modified documents have been saved
	 * successfully, or the user has chosen not to save
	 * one or more changed profiles, and to proceed anyway.
	 * Returns "false" if either one or more profiles could
	 * not be saved, or the user has chosen to cancel the
	 * whole operation.
	 */
	private boolean saveModified() {
		for(int n=0; n<mainPanel.getTabCount(); n++) {
			Component component = mainPanel.getComponentAt(n);
			if(component instanceof DriveTrainPanel &&
			    !((DriveTrainPanel)component).saveModified() ) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Utility method to create a new tab
	 * with the specified profile in it.
	 * Used among others by open().
	 * @param newModel The model that should be displyed in the new tab.
	 */
	private void createView(DriveTrain newModel) {
		DriveTrainPanel panel = new DriveTrainPanel();
		panel.setModel(newModel);
		setProfileOpened(true);		
		mainPanel.insertTab(panel.getName(), panel.getIcon(), panel, panel.getName(), mainPanel.getTabCount()-1);
		comparisonView.addModel(newModel);
		panel.addContentChangeListener(this);
		panel.addComponentListener(new TabListener(true));
		mainPanel.setSelectedComponent(panel);
	}

	/**
	 * Utility method to create a new tab
	 * with an empty profile in it.
	 * Used among others by newProfile().
	 */
	private void createView() {
		DriveTrainPanel panel = new DriveTrainPanel();
		setProfileOpened(true);
		mainPanel.insertTab(panel.getName(), panel.getIcon(), panel, panel.getName(), mainPanel.getTabCount()-1);
		comparisonView.addModel(panel.getModel());
		panel.addContentChangeListener(this);
		panel.addComponentListener(new TabListener(true));
		mainPanel.setSelectedComponent(panel);
	}

	///////////////////////////////////////////////////////
	// Convenience field accessor methods

	/**
	 * Gets the currently selected GearView
	 * @return The currently selected GearView
	 */
	private GearView getCurrentView() {
		return (GearView)mainPanel.getSelectedComponent();
	}

	/**
	 * Gets the icon for the comparison view tab,
	 * loads it if it isn't already loaded.
	 * @return The icon for the comparison view tab.
	 */
	private ImageIcon getComparisonIcon() {
		if(comparisonIcon == null) {
			comparisonIcon = new ImageIcon(Main.class.getResource("/toolbarButtonGraphics/general/Find16.gif"));
		}
		return comparisonIcon;
	}

	/**
	 * Gets the currently selected DriveTrainPanel
	 * @return the currently selected DriveTrainPanel
	 */
	private DriveTrainPanel getCurrentPanel() {
		return (DriveTrainPanel)mainPanel.getSelectedComponent();
	}
	
	/**
	 * Gets the main window title dependent on
	 * the current profile, its modification
	 * status, and its name.
	 * @return The main window title.
	 */
	private String getWindowTitle() {
		String title = Messages.getString("Main.2");
		if(getCurrentView() instanceof DriveTrainPanel) {
			String modified = getCurrentPanel().getModel().isModified() ? "*" : "";
			return getCurrentPanel().getName() + modified + " - " + title;
		} else {
			return title;
		}
	}

	///////////////////////////////////////////////////////
	// GUI state management support methods

	/**
	 * Manages the enabled/disabled state of the toolbar and menu
	 * actions for the two conditions where either
	 * there is a currently open profile, or there isn't
	 * (the main window is empty).
	 * @param enabled true if there is a currently
	 * open profile, false if there isn't
	 */
	private void setProfileOpened(boolean enabled) {
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
			mainPanel.addTab(Messages.getString("Comparison"), getComparisonIcon(), comparisonView);
		} else {
			mainPanel.removeTabAt(0);
			mainPanel.setSelectedIndex(-1);
		}
		contentChanged(null);
	}

	/**
	 * Manages the enabled/disabled state of the
	 * toolbar and menu actions for the two
	 * conditions that a profile panel is selected
	 * or the comparison view is selected instead.
	 * @param enabled true if a profile panel is
	 * currently selected and visible, false if
	 * the comparison view is currently selected and
	 * visible.
	 */
	private void setPanelSelected(boolean enabled) {
		ActionGroup file = actions.getActionGroup("File");
		file.getAction("Save").setEnabled(enabled);
		file.getAction("Close").setEnabled(enabled);
	}

	///////////////////////////////////////////////////////
	// Implementation of the ContentChangeListener interface

	/**
	 * Implements ContentChangeListener.contentChanged().
	 * Updates the window title.
	 */
	public void contentChanged(ChangeEvent e) {
		mainFrame.setTitle(getWindowTitle());
		if(mainPanel.getSelectedIndex() > -1 && (getCurrentView() instanceof DriveTrainPanel)) {
			mainPanel.setTitleAt(mainPanel.getSelectedIndex(), getCurrentPanel().getName());
			mainPanel.setIconAt(mainPanel.getSelectedIndex(), getCurrentPanel().getIcon());
		}
	}

	/**
	 * Implements ContentChangeListener.styleChanged().
	 * Empty operation.
	 */
	public void styleChanged(ChangeEvent e) {
		// empty operation
	}

	///////////////////////////////////////////////////////
	// Nested listener classes

	/**
	 * Listener implementation for tab
	 * selection change events that are
	 * available in Swing as component hide/show
	 * events.
	 * This is wired up to the profile tab 
	 * views, but not to the comparison view, to
	 * allow to set the correct state of the
	 * menu and toolbar actions and to update
	 * the main window title accordingly.
	 * @author mklemm
	 *
	 */
	private final class TabListener implements ComponentListener {
		
		boolean panel;
		
		TabListener(boolean panel) {
			this.panel = panel;
		}
		
		/**
		 * Not handled
		 */
		public void componentResized(ComponentEvent e) {
			// do nothing			
		}

		/**
		 * Not handled
		 */
		public void componentMoved(ComponentEvent e) {
			// do nothing			
		}

		/**
		 * Handles the selection of a
		 * profile tab.
		 * Sets all actions to "enabled"
		 * that are needed for profile
		 * tab control.
		 */
		public void componentShown(ComponentEvent e) {
			setPanelSelected(panel);
			contentChanged(null);
		}

		/**
		 * Handles the de-selection
		 * of a profile tab.
		 * Disables all actions
		 * that are only applicable for
		 * profile tabs, and leaves only those 
		 * actions enabled that are used for the
		 * comparison view.
		 */
		public void componentHidden(ComponentEvent e) {
			setPanelSelected(!panel);
			contentChanged(null);
		}
	}
	
	
}
