package de.mutantenzoo.raf;

import javax.swing.JMenu;
import javax.swing.JToolBar;

import de.mutantenzoo.gcu.ui.Messages;

public class ActionGroup {

	private String name;
	private JToolBar toolBar = null;
	private JMenu menu = null;
	
	public ActionGroup(String name) {
		this.name = name;
	}

	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return Returns the toolBar.
	 */
	public JToolBar getToolBar() {
		if(toolBar == null) {
			toolBar = new JToolBar(name);
		}
		return toolBar;
	}
	
	public JMenu getMenu() {
		if(menu == null) {
			menu = new JMenu(name);
			menu.setText(Messages.getString(name));
		}
		return menu;
	}

	public void add(LocalizedAction localizedAction) {
		localizedAction.setGroup(this);
		Button button = new Button(localizedAction);
		MenuItem menuItem = new MenuItem(localizedAction);
		getMenu().add(menuItem);
		getToolBar().add(button);
	}
	
	public void addSeparator() {
		getMenu().addSeparator();
		getToolBar().addSeparator();
	}

}
