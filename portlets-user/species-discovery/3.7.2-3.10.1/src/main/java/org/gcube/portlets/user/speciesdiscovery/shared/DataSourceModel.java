package org.gcube.portlets.user.speciesdiscovery.shared;

import java.util.ArrayList;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */

public class DataSourceModel extends DataSource{

	private static final long serialVersionUID = 1L;
	
	private ArrayList<DataSourceCapability> listCapabilities;
	private DataSourceRepositoryInfo dataSourceRepositoryInfo;
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
	
	public DataSourceModel() {}
	
	public ArrayList<DataSourceCapability> getListCapabilities() {
		return listCapabilities;
	}

	public void setListCapabilities(ArrayList<DataSourceCapability> listCapabilities) {
		this.listCapabilities = listCapabilities;
	}

	public DataSourceRepositoryInfo getDataSourceRepositoryInfo() {
		return dataSourceRepositoryInfo;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DataSourceModel [listCapabilities=");
		builder.append(listCapabilities);
		builder.append(", dataSourceRepositoryInfo=");
		builder.append(dataSourceRepositoryInfo);
		builder.append(", getId()=");
		builder.append(getId());
		builder.append(", getName()=");
		builder.append(getName());
		builder.append(", getDescription()=");
		builder.append(getDescription());
		builder.append("]");
		return builder.toString();
	}
	
	
}
