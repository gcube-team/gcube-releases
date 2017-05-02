package org.gcube.common.geoserverinterface.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkspaceRest implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6114472831120210793L;
	/**
	 * @uml.property  name="name"
	 */
	private String name = "";
	/**
	 * @uml.property  name="stores"
	 * @uml.associationEnd  qualifier="typeStore:java.lang.String java.util.ArrayList"
	 */
	private Map<String, ArrayList<String>> stores = new HashMap<String, ArrayList<String>>();
	
	public WorkspaceRest() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return
	 * @uml.property  name="name"
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 * @uml.property  name="name"
	 */
	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<String> getStores(String typeStore) {
		return stores.get(typeStore);
	}

	public void setStores(String typeStore, List<String> list) {
		this.stores.put(typeStore, (ArrayList<String>) list);
	}
}
