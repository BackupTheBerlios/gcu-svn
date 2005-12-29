/**
 * 
 */
package de.mutantenzoo.raf;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;

/**
 * @author MKlemm
 *
 */
public class Button extends JButton {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private boolean textVisible = false;
	private boolean iconVisible = true;
	
	/**
	 * 
	 */
	protected Button() {
		super();
	}

	/**
	 * @param a
	 */
	protected Button(LocalizedAction a) {
		super(a);
		setLocalizedActionProperties(a);
	}

	/**
	 * @param icon
	 */
	protected Button(Icon icon) {
		super(icon);
	}

	/**
	 * @param text
	 * @param icon
	 */
	protected Button(String text, Icon icon) {
		super(text, icon);
	}

	/**
	 * @param text
	 */
	protected Button(String text) {
		super(text);
	}

	/* (non-Javadoc)
	 * @see javax.swing.AbstractButton#setAction(javax.swing.Action)
	 */
	public void setAction(LocalizedAction a) {
		super.setAction(a);
		setLocalizedActionProperties(a);
	}
	
	private void setLocalizedActionProperties(LocalizedAction a) {
		setName(a.getName());
		setText(a.getText());
		if(iconVisible) {
			setIcon(a.getIconBundle().getLargeIcon(this));
			setRolloverIcon(a.getIconBundle().getLargeRolloverIcon(this));
		} else {
			setIcon(null);
			setRolloverIcon(null);
		}
		if(textVisible) {
			setText(a.getName());
		} else {
			setBorder(BorderFactory.createEmptyBorder());
			setText(null);
		}
	}
	
}
