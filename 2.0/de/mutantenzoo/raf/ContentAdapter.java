package de.mutantenzoo.raf;

import java.io.Serializable;
import java.util.LinkedList;

import javax.swing.event.ChangeEvent;

public class ContentAdapter implements ContentEventSource, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private LinkedList<ContentChangeListener> contentChangeListeners = new LinkedList<ContentChangeListener>();

	public void addContentChangeListener(ContentChangeListener l) {
		contentChangeListeners.add(l);
	}

	public void fireContentChanged() {
		ChangeEvent e = new ChangeEvent(this);
		for(ContentChangeListener ccl : contentChangeListeners) {
			ccl.contentChanged(e);
		}
	}
	
	public void fireStyleChanged() {
		ChangeEvent e = new ChangeEvent(this);
		for(ContentChangeListener ccl : contentChangeListeners) {
			ccl.styleChanged(e);
		}
	}

}
