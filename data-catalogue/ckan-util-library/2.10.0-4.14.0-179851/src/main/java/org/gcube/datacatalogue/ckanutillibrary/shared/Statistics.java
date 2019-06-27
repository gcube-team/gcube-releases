package org.gcube.datacatalogue.ckanutillibrary.shared;

import java.io.Serializable;

/**
 * This bean offers the following statistics:
 * <ul>
 * <li> number of items of the catalogue
 * <li> number of organizations of the catalogue
 * <li> number of groups of the catalogue
 * <li> number of types of the catalogue
 * </ul>
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class Statistics implements Serializable{
	
	private static final long serialVersionUID = 2871906712366452266L;
	private long numTypes;
	private long numOrganizations;
	private long numGroups;
	private long numItems;

	public long getNumTypes() {
		return numTypes;
	}
	public void setNumTypes(long numTypes) {
		this.numTypes = numTypes;
	}
	public long getNumOrganizations() {
		return numOrganizations;
	}
	public void setNumOrganizations(long numOrganizations) {
		this.numOrganizations = numOrganizations;
	}
	public long getNumGroups() {
		return numGroups;
	}
	public void setNumGroups(long numGroups) {
		this.numGroups = numGroups;
	}
	public long getNumItems() {
		return numItems;
	}
	public void setNumItems(long numItems) {
		this.numItems = numItems;
	}
	@Override
	public String toString() {
		return "Statistics [numTypes=" + numTypes + ", numOrganizations="
				+ numOrganizations + ", numGroups=" + numGroups + ", numItems="
				+ numItems + "]";
	}

}
