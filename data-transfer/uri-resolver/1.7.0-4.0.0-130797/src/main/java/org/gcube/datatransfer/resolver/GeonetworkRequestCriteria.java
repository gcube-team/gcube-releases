/**
 *
 */
package org.gcube.datatransfer.resolver;


/**
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jun 15, 2016
 */
public class GeonetworkRequestCriteria {

	private String scope;
	private final String FILTER_PUBLIC_IDS = "FILTER_PUBLIC_IDS";
	private boolean valueOfFilterPublicIds;
	private boolean authOnGeonetwork;
	private boolean noAuthOnGeonetwork;

	/**
	 * Instantiates a new geonetwork request criteria.
	 *
	 * @param scope the scope
	 * @param valueOfFilterPublicIds the value of filter public ids
	 * @param noAuthOnGeonetowrk the no auth on geonetowrk
	 */
	GeonetworkRequestCriteria(String scope, boolean valueOfFilterPublicIds, boolean noAuthOnGeonetowrk){
		this.scope = scope;
		this.valueOfFilterPublicIds = valueOfFilterPublicIds;
		this.noAuthOnGeonetwork = noAuthOnGeonetowrk;
	}



	/**
	 * @return the noAuthOnGeonetwork
	 */
	public boolean isNoAuthOnGeonetwork() {

		return noAuthOnGeonetwork;
	}


	/**
	 * @return the scope
	 */
	public String getScope() {

		return scope;
	}


	/**
	 * @return the valueOfFilterPublicIds
	 */
	public boolean isValueOfFilterPublicIds() {

		return valueOfFilterPublicIds;
	}


	/**
	 * @param scope the scope to set
	 */
	public void setScope(String scope) {

		this.scope = scope;
	}


	/**
	 * @param valueOfFilterPublicIds the valueOfFilterPublicIds to set
	 */
	public void setValueOfFilterPublicIds(boolean valueOfFilterPublicIds) {

		this.valueOfFilterPublicIds = valueOfFilterPublicIds;
	}



	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("GeonetworkRequestCriteria [scope=");
		builder.append(scope);
		builder.append(", FILTER_PUBLIC_IDS=");
		builder.append(FILTER_PUBLIC_IDS);
		builder.append(", valueOfFilterPublicIds=");
		builder.append(valueOfFilterPublicIds);
		builder.append(", authOnGeonetwork=");
		builder.append(authOnGeonetwork);
		builder.append("]");
		return builder.toString();
	}

}
