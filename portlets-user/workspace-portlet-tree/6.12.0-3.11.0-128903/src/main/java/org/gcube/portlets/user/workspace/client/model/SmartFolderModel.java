package org.gcube.portlets.user.workspace.client.model;

import java.io.Serializable;

import org.gcube.portlets.user.workspace.client.ConstantsExplorer;

import com.extjs.gxt.ui.client.data.BaseModelData;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class SmartFolderModel extends BaseModelData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	SmartFolderModel(){
		
	}

	public SmartFolderModel(String id, String name, String query){
		setIdentifier(id);
		setName(name);
		setQuery(query);
	}
	

	public void setIdentifier(String id) {
		set(ConstantsExplorer.IDENTIFIER,id);
	}
	
	private void setName(String name){
		set(ConstantsExplorer.NAME, name);	
	}
	
	private void setQuery(String query){
		set(ConstantsExplorer.QUERY, query);
	}
	
	public String getIdentifier() {
		return get(ConstantsExplorer.IDENTIFIER);
	}
	
	public String getName(){
		return get(ConstantsExplorer.NAME);	
	}
	
	public String getQuery(){
		return  get(ConstantsExplorer.QUERY);
	}
}
