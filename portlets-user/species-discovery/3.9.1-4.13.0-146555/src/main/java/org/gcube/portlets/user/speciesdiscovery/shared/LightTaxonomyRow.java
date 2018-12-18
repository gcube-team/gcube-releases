/**
 * 
 */
package org.gcube.portlets.user.speciesdiscovery.shared;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class LightTaxonomyRow implements FetchingElement, Serializable, Comparable<LightTaxonomyRow> {


	/**
	 * 
	 */
	private static final long serialVersionUID = -3303399965998066897L;
	
	public final static String ID_FIELD = "id";
	public final static String DATAPROVIDER_NAME = "dataProviderName";
	public static final String DATAPROVIDER_ID = "dataProviderId";
	public final static String RANK = "rank";
	public static final String NAME = "name";
	public static final String STATUSREFID = "statusRefId";
	public static final String STATUSNAME = "statusName";
	public final static String PARENT_ID = "parentID";
	public final static String IS_PARENT = "isParent";
	public final static String SERVICE_ID_FIELD = "serviceIdField";
	public final static String STATUS_REMARKS = "statusRemarks";

	public static final String BASE_TAXON_VALUE = "baseTaxonValue";

	protected String name;
	protected String serviceIdField;
	protected String dataProviderId;
	protected String dataProviderName;
	protected String rank;
	private String statusRefId;
	private String statusName;
	
	protected String baseTaxonValue;
	protected String statusRemarks;
	
	private boolean isParent = false;

	protected List<LightTaxonomyRow> parents;
	
	//USED FOR PARENT
	protected String parentID;
	protected int id;
	protected int parentIndex = -1;

	protected LightTaxonomyRow() {
		
		
	}

	/**
	 * @param id
	 */
	public LightTaxonomyRow(int id) {
		this.id = id;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * @return the id
	 */
	public String getIdToString() {
		return id+"";
	}
	
	public void setBaseTaxonValue(String taxonomyValue) {
		this.baseTaxonValue = taxonomyValue;

	}

	public String getBaseTaxonValue() {
		return baseTaxonValue;
	}

	/**
	 * @return the dataProviderId
	 */
	public String getDataProviderId() {
		return dataProviderId;
	}

	/**
	 * @param dataProviderId
	 *            the dataProviderId to set
	 */
	public void setDataProviderId(String dataProviderId) {
		this.dataProviderId = dataProviderId;
	}

	/**
	 * @return the dataProviderName
	 */
	public String getDataProviderName() {
		return dataProviderName;
	}

	/**
	 * @param dataProviderName
	 *            the dataProviderName to set
	 */
	public void setDataProviderName(String dataProviderName) {
		this.dataProviderName = dataProviderName;
	}

	public String getRank() {
		return rank;
	}

	public void setRank(String rank) {
		this.rank = rank;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setStatusRefId(String statusRefId) {
		this.statusRefId = statusRefId;
		
	}

	public void setStatusName(String statusName) {
		this.statusName = statusName;
		
	}

	public void setParent(List<LightTaxonomyRow> parent) {
		this.parents = parent;
	}

	public List<LightTaxonomyRow> getParents() {
		Collections.sort(parents);
		return parents;
	}

	public String getStatusRefId() {
		return statusRefId;
	}

	public String getStatusName() {
		return statusName;
	}

	public String getTaxonId() {
		return ""+id;
	}

	public String getParentID() {
		return parentID;
	}

	public void setParentID(String parentID) {
		this.parentID = parentID;
	}

	public String getServiceId() {
		return serviceIdField;
	}

	public void setServiceId(String serviceIdField) {
		this.serviceIdField = serviceIdField;
	}

	public String getStatusRemarks() {
		return statusRemarks;
	}

	public void setStatusRemarks(String statusRemarks) {
		this.statusRemarks = statusRemarks;
	}

	public boolean isParent() {
		return isParent;
	}

	public void setParent(boolean isParent) {
		this.isParent = isParent;
	}

	public int getParentIndex() {
		return parentIndex;
	}

	public void setParentIndex(int parentIndex) {
		this.parentIndex = parentIndex;
	}

	/* (non-Javadoc)
	 * Comparable on insertion order
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(LightTaxonomyRow o) {
		return parentIndex - o.getParentIndex();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MinimalTaxonomyRow [id=");
		builder.append(id);
		builder.append(", name=");
		builder.append(name);
		builder.append(", serviceIdField=");
		builder.append(serviceIdField);
		builder.append(", dataProviderId=");
		builder.append(dataProviderId);
		builder.append(", dataProviderName=");
		builder.append(dataProviderName);
		builder.append(", rank=");
		builder.append(rank);
		builder.append(", statusRefId=");
		builder.append(statusRefId);
		builder.append(", statusName=");
		builder.append(statusName);
		builder.append(", baseTaxonValue=");
		builder.append(baseTaxonValue);
		builder.append(", statusRemarks=");
		builder.append(statusRemarks);
		builder.append(", isParent=");
		builder.append(isParent);
		builder.append(", parents=");
		builder.append(parents);
		builder.append(", parentID=");
		builder.append(parentID);
		builder.append(", parentIndex=");
		builder.append(parentIndex);
		builder.append("]");
		return builder.toString();
	}
	
}
