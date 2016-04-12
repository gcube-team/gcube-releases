package org.gcube.portlets.user.speciesdiscovery.shared;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class DataSourceCapability implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private ArrayList<SpeciesCapability> listFilters;

	private SpeciesCapability capability;
	
	public DataSourceCapability(SpeciesCapability capability, ArrayList<SpeciesCapability> listFilters) {
		super();
		this.capability = capability;
		this.listFilters = listFilters;

	}

	public DataSourceCapability() {}

	public ArrayList<SpeciesCapability> getListFilters() {
		return listFilters;
	}

	public void setListFilters(ArrayList<SpeciesCapability> listFilters) {
		this.listFilters = listFilters;
	}

	public SpeciesCapability getCapability() {
		return capability;
	}

	public void setCapability(SpeciesCapability capability) {
		this.capability = capability;
	}
	
}
