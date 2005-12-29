/**
 * 
 */
package de.mutantenzoo.raf;

import javax.swing.JComponent;


/**
 * @author MKlemm
 *
 */
public abstract class ContentComponent extends JComponent implements ContentEventSource {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final ContentAdapter contentAdapter = new ContentAdapter();
	
	public void addContentChangeListener(ContentChangeListener l) {
		contentAdapter.addContentChangeListener(l);
	}

	protected void fireContentChanged() {
		contentAdapter.fireContentChanged();
	}

	protected void fireStyleChanged() {
		contentAdapter.fireStyleChanged();
	}

}
