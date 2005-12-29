/**
 * 
 */
package de.mutantenzoo.raf;

import java.awt.event.InputEvent;

import javax.swing.KeyStroke;

import de.mutantenzoo.gcu.ui.Messages;

/**
 * @author MKlemm
 *
 */
public abstract class LocalizedAction extends javax.swing.AbstractAction {

	public static final String ICON = "Icon";
	public static final String CATEGORY = "Category";
	
	private String name;

	public LocalizedAction(String name) {
		this.name = name;
	}

	void setGroup(ActionGroup group) {
		String category = group.getName();
		putValue(CATEGORY, category);
		putValue(NAME, Messages.getString(category, name, NAME));
		putValue(ICON, new IconBundle(Messages.getString(category, name, ICON, name)));
		putValue(SHORT_DESCRIPTION, Messages.getString(category, name, SHORT_DESCRIPTION, getText()));
		putValue(LONG_DESCRIPTION, Messages.getString(category, name, LONG_DESCRIPTION));
		String acceleratorKeyDef = Messages.getString(category, name, ACCELERATOR_KEY);
		if(acceleratorKeyDef != null) {
			KeyStroke accelerator;
			if(acceleratorKeyDef.length() == 1) {
				accelerator = KeyStroke.getKeyStroke(acceleratorKeyDef.charAt(0), InputEvent.CTRL_MASK);
			} else {
				accelerator = KeyStroke.getKeyStroke(acceleratorKeyDef);
			}
			putValue(ACCELERATOR_KEY, accelerator);
		}
		String mnemonicKeyDef = Messages.getString(category, name, MNEMONIC_KEY);
		if(mnemonicKeyDef != null) {
			putValue(MNEMONIC_KEY, new Integer(mnemonicKeyDef.charAt(0)));
		}
		putValue(ACTION_COMMAND_KEY, Messages.getString(category, name, ACTION_COMMAND_KEY));
	}

	/**
	 * @return Returns the iconBundle.
	 */
	public IconBundle getIconBundle() {
		return (IconBundle)getValue(ICON);
	}

	/**
	 * @param iconBundle The iconBundle to set.
	 */
	public void setIconBundle(IconBundle iconBundle) {
		putValue(ICON, iconBundle);
	}

	/**
	 * @return Returns the label.
	 */
	public String getText() {
		return (String)getValue(NAME);
	}
	
	public String getName() {
		return name;
	}

	public String getCategory() {
		return (String)getValue(CATEGORY);
	}
	/**
	 * @param label The label to set.
	 */
	public void setName(String name) {
		putValue(NAME, Messages.getString(name));
	}

	/**
	 * @return Returns the longDescription.
	 */
	public String getLongDescription() {
		return (String)getValue(LONG_DESCRIPTION);
	}

	/**
	 * @param longDescription The longDescription to set.
	 */
	public void setLongDescription(String longDescription) {
		putValue(LONG_DESCRIPTION, Messages.getString(longDescription));
	}

	/**
	 * @return Returns the shortDescription.
	 */
	public String getShortDescription() {
		return (String)getValue(SHORT_DESCRIPTION);
	}

	/**
	 * @param shortDescription The shortDescription to set.
	 */
	public void setShortDescription(String shortDescription) {
		putValue(SHORT_DESCRIPTION, Messages.getString(shortDescription));
	}
	
	public void setMnemonic(int mnemonic) {
		putValue(MNEMONIC_KEY, mnemonic);
	}
}
