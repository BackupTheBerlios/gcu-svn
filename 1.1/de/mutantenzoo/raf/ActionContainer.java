/**
 * 
 */
package de.mutantenzoo.raf;

import java.util.LinkedHashMap;

import javax.swing.JMenuBar;

/**
 * @author MKlemm
 *
 */
public class ActionContainer {

	private JMenuBar menuBar = null;
	private LinkedHashMap<String,ActionGroup> actionGroups = new LinkedHashMap<String, ActionGroup>();
	
	
	public ActionGroup getActionGroup(String groupName) {
		ActionGroup group = actionGroups.get(groupName);
		if(group == null) {
			group = new ActionGroup(groupName);
			add(group);
		}
		return group;
	}

	public JMenuBar getMenuBar() {
		if(menuBar == null) {
			menuBar = new JMenuBar();
		}
		return menuBar;
	}
	
	public void add(ActionGroup actionGroup) {
		actionGroups.put(actionGroup.getName(), actionGroup);
		menuBar.add(actionGroup.getMenu());
	}
}
