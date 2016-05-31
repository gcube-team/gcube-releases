package org.gcube.portlets.user.geoexplorer.shared.metadata.quality;

import java.io.Serializable;


public class DataQualityItem implements Serializable{
	
	  /**
	 * 
	 */
	private static final long serialVersionUID = 8509472542918221795L;

	/**
     * The specific data to which the data quality information applies.
     */
    private String scope;

//    /**
//     * Quantitative quality information for the data specified by the scope.
//     * Should be provided only if {@linkplain Scope#getLevel scope level} is
//     * {@linkplain org.opengis.metadata.maintenance.ScopeCode#DATASET dataset}.
//     */
//    private Collection<Element> reports;

    /**
     * Non-quantitative quality information about the lineage of the data specified by the scope.
     */
    private LineageItem lineage;

    /**
     * Constructs an initially empty data quality.
     */
    public DataQualityItem() {
    }

	public DataQualityItem(String scope, LineageItem lineage) {
		this.scope = scope;
		this.lineage = lineage;
	}

	public LineageItem getLineage() {
		return lineage;
	}

	public void setLineage(LineageItem lineage) {
		this.lineage = lineage;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DataQualityItem [scope=");
		builder.append(scope);
		builder.append(", lineage=");
		builder.append(lineage);
		builder.append("]");
		return builder.toString();
	}

}
