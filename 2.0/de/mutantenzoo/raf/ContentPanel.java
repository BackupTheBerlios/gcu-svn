/**
 * 
 */
package de.mutantenzoo.raf;

import java.awt.LayoutManager;

import javax.swing.JPanel;


/**
 * @author MKlemm
 *
 */
public class ContentPanel extends JPanel implements ContentEventSource {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final ContentAdapter contentAdapter = new ContentAdapter();
	/**
	 * @param layout
	 * @param isDoubleBuffered
	 */
	public ContentPanel(LayoutManager layout, boolean isDoubleBuffered) {
		super(layout, isDoubleBuffered);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param layout
	 */
	public ContentPanel(LayoutManager layout) {
		super(layout);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param isDoubleBuffered
	 */
	public ContentPanel(boolean isDoubleBuffered) {
		super(isDoubleBuffered);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	public ContentPanel() {
		super();
		// TODO Auto-generated constructor stub
	}

	public void addContentChangeListener(ContentChangeListener contentChangeListener) {
		contentAdapter.addContentChangeListener(contentChangeListener);		
	}

	protected void fireContentChanged() {
		contentAdapter.fireContentChanged();
	}

	protected void fireStyleChanged() {
		contentAdapter.fireStyleChanged();
	}
}
