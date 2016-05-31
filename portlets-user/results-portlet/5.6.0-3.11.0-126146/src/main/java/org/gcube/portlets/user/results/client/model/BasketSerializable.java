package org.gcube.portlets.user.results.client.model;

import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * 
 * @author massi
 * the class which goes in the session
 *
 */
public class BasketSerializable implements IsSerializable {

	private String id;
	private String name;
	private String path;
	private List<BasketModelItem> items;
	
//	*************	
	
	public BasketSerializable() {
		super();
	}	
	
	public BasketSerializable(String id, String name, String path, List<BasketModelItem> items) {
		super();
		this.id = id;
		this.name = name;
		this.path = path;
		this.items = items;
	}
	
//*************


	public List<BasketModelItem> getItems() {
		return items;
	}
	public void setItems(List<BasketModelItem> items) {
		this.items = items;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * the id of the basket in the user workspace
	 * @return id of the basket in the user workspace
	 */
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	} 		
	
	
}
