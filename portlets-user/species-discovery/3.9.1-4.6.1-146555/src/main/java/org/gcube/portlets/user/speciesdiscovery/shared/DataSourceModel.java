package org.gcube.portlets.user.speciesdiscovery.shared;

import java.io.Serializable;
import java.util.ArrayList;

import com.google.gwt.user.client.rpc.IsSerializable;


/**
 * The Class DataSourceModel.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jan 10, 2017
 */
public class DataSourceModel extends DataSource implements IsSerializable, Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = -8314335099130160226L;
	private ArrayList<DataSourceCapability> listCapabilities;
	private DataSourceRepositoryInfo dataSourceRepositoryInfo;

	/**
	 * Instantiates a new data source model.
	 */
	public DataSourceModel() {}

	/**
	 * Instantiates a new data source model.
	 *
	 * @param id the id
	 * @param name the name
	 * @param description the description
	 * @param listCapabilities the list capabilities
	 * @param dsInfo the ds info
	 */
	public DataSourceModel(String id, String name, String description, ArrayList<DataSourceCapability> listCapabilities, DataSourceRepositoryInfo dsInfo) {
		super(id,name,description);
		setListCapabilities(listCapabilities);
		setDataSourceRepositoryInfo(dsInfo);
	}

	/**
	 * Sets the data source repository info.
	 *
	 * @param dsInfo the new data source repository info
	 */
	private void setDataSourceRepositoryInfo(DataSourceRepositoryInfo dsInfo) {
		this.dataSourceRepositoryInfo = dsInfo;

	}

	//Used in Data Source advanced option to create the check list
	/**
	 * Instantiates a new data source model.
	 *
	 * @param id the id
	 * @param name the name
	 */
	public DataSourceModel(String id, String name){
		super(id,name);
	}



	/**
	 * Gets the list capabilities.
	 *
	 * @return the list capabilities
	 */
	public ArrayList<DataSourceCapability> getListCapabilities() {
		return listCapabilities;
	}

	/**
	 * Sets the list capabilities.
	 *
	 * @param listCapabilities the new list capabilities
	 */
	public void setListCapabilities(ArrayList<DataSourceCapability> listCapabilities) {
		this.listCapabilities = listCapabilities;
	}

	/**
	 * Gets the data source repository info.
	 *
	 * @return the data source repository info
	 */
	public DataSourceRepositoryInfo getDataSourceRepositoryInfo() {
		return dataSourceRepositoryInfo;
	}
}
