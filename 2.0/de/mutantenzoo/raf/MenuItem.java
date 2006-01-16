/**
 * 
 */
package de.mutantenzoo.raf;

import javax.swing.Icon;
import javax.swing.JMenuItem;

/**
 * @author MKlemm
 *
 */
public class MenuItem extends JMenuItem {

	/**
	 * 
	 */
	private static final long serialVersionUID = 350251333522248490L;
	
	private boolean iconVisible = true;

	/**
	 * Default constructor
	 */
	public MenuItem() {
		super();
	}

	/**
	 * @param icon
	 */
	public MenuItem(Icon icon) {
		super(icon);
	}

	/**
	 * @param text
	 */
	public MenuItem(String text) {
		super(text);
	}

	/**
	 * @param a
	 */
	public MenuItem(LocalizedAction a) {
		super(a);
		setLocalizedActionProperties(a);
	}

	/**
	 * @param text
	 * @param icon
	 */
	public MenuItem(String text, Icon icon) {
		super(text, icon);
	}

	/**
	 * @param text
	 * @param mnemonic
	 */
	public MenuItem(String text, int mnemonic) {
		super(text, mnemonic);
	}

	/**
	 * sets a LocalizedAction for this MenuItem
	 * @param a
	 */
	public void setAction(LocalizedAction a) {
		super.setAction(a);
		setLocalizedActionProperties(a);
	}
	
	private void setLocalizedActionProperties(LocalizedAction a) {
		setName(a.getName());
		setText(a.getText());
		if(iconVisible) {
			setIcon(a.getIconBundle().getSmallIcon(this));
			setRolloverIcon(a.getIconBundle().getSmallRolloverIcon(this));
		} else {
			setIcon(null);
			setRolloverIcon(null);
		}
	}

}
