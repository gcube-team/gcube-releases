package org.gcube.portlets.user.speciesdiscovery.shared;

import java.io.Serializable;
import java.util.ArrayList;

import com.google.gwt.user.client.rpc.IsSerializable;



/**
 * The Class DataSourceCapability.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jan 10, 2017
 */
public class DataSourceCapability implements IsSerializable, Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = 844266531985842984L;
	private ArrayList<SpeciesCapability> listFilters;
	private SpeciesCapability capability;

	/**
	 * Instantiates a new data source capability.
	 */
	public DataSourceCapability() {}

	/**
	 * Instantiates a new data source capability.
	 *
	 * @param capability the capability
	 * @param listFilters the list filters
	 */
	public DataSourceCapability(SpeciesCapability capability, ArrayList<SpeciesCapability> listFilters) {
		super();
		this.capability = capability;
		this.listFilters = listFilters;

	}

	/**
	 * Gets the list filters.
	 *
	 * @return the list filters
	 */
	public ArrayList<SpeciesCapability> getListFilters() {
		return listFilters;
	}

	/**
	 * Sets the list filters.
	 *
	 * @param listFilters the new list filters
	 */
	public void setListFilters(ArrayList<SpeciesCapability> listFilters) {
		this.listFilters = listFilters;
	}

	/**
	 * Gets the capability.
	 *
	 * @return the capability
	 */
	public SpeciesCapability getCapability() {
		return capability;
	}

	/**
	 * Sets the capability.
	 *
	 * @param capability the new capability
	 */
	public void setCapability(SpeciesCapability capability) {
		this.capability = capability;
	}

}
