package org.gcube.portlets.user.workspace.client.model;

import java.io.Serializable;

import com.extjs.gxt.ui.client.data.BaseModelData;


/**
 * The Class SmartFolderModel.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Sep 29, 2016
 */
public class SmartFolderModel extends BaseModelData implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Instantiates a new smart folder model.
	 */
	public SmartFolderModel(){

	}

	/**
	 * Instantiates a new smart folder model.
	 *
	 * @param id the id
	 * @param name the name
	 * @param query the query
	 */
	public SmartFolderModel(String id, String name, String query){
		setIdentifier(id);
		setName(name);
		setQuery(query);
	}


	/**
	 * Sets the identifier.
	 *
	 * @param id the new identifier
	 */
	public void setIdentifier(String id) {
		set(FileModel.IDENTIFIER,id);
	}

	/**
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	private void setName(String name){
		set(FileModel.NAME, name);
	}

	/**
	 * Sets the query.
	 *
	 * @param query the new query
	 */
	private void setQuery(String query){
		set(FileModel.QUERY, query);
	}

	/**
	 * Gets the identifier.
	 *
	 * @return the identifier
	 */
	public String getIdentifier() {
		return get(FileModel.IDENTIFIER);
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName(){
		return get(FileModel.NAME);
	}

	/**
	 * Gets the query.
	 *
	 * @return the query
	 */
	public String getQuery(){
		return  get(FileModel.QUERY);
	}
}
