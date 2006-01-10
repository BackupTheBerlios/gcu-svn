/**
 * 
 */
package de.mutantenzoo.raf;

import java.util.EventListener;

import javax.swing.event.ChangeEvent;

/**
 * @author MKlemm
 *
 */
public interface ContentChangeListener extends EventListener {
	void contentChanged(ChangeEvent e);

	void styleChanged(ChangeEvent e);
}
