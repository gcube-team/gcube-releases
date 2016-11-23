package org.gcube.portlets.user.speciesdiscovery.shared;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */

public class DataSourceModel extends DataSource implements Serializable{


	private static final long serialVersionUID = 7399231525793036218L;
	
	private ArrayList<DataSourceCapability> listCapabilities;
	private DataSourceRepositoryInfo dataSourceRepositoryInfo;
	
	public DataSourceModel() {}
	/**
	 * 
	 * @param id
	 * @param name
	 * @param description
	 * @param listCapabilities
	 * @param dsInfo 
	 */
	public DataSourceModel(String id, String name, String description, ArrayList<DataSourceCapability> listCapabilities, DataSourceRepositoryInfo dsInfo) {
		super(id,name,description);
		setListCapabilities(listCapabilities);
		setDataSourceRepositoryInfo(dsInfo);
	}
	
	private void setDataSourceRepositoryInfo(DataSourceRepositoryInfo dsInfo) {
		this.dataSourceRepositoryInfo = dsInfo;
		
	}

	//Used in Data Source advanced option to create the check list
	public DataSourceModel(String id, String name){
		super(id,name);
	}
	

	
	public ArrayList<DataSourceCapability> getListCapabilities() {
		return listCapabilities;
	}

	public void setListCapabilities(ArrayList<DataSourceCapability> listCapabilities) {
		this.listCapabilities = listCapabilities;
	}

	public DataSourceRepositoryInfo getDataSourceRepositoryInfo() {
		return dataSourceRepositoryInfo;
	}
}
