/**
 * 
 */
package org.gcube.portlets.user.geoexplorer.client.beans;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author ceras
 *
 */
public class GroupItem implements IsSerializable {
	String name;
	String title;
	
	/**
	 * @param name
	 * @param title
	 */
	public GroupItem(String name, String title) {
		super();
		this.name = name;
		this.title = title;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	
	
}
