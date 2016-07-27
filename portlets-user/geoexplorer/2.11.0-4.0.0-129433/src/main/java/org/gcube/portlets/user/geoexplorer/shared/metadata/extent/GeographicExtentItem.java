package org.gcube.portlets.user.geoexplorer.shared.metadata.extent;

import java.io.Serializable;

public class GeographicExtentItem implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3940736587363192567L;

	/**
     * Indication of whether the bounding polygon encompasses an area covered by the data
     * (<cite>inclusion</cite>) or an area where data is not present (<cite>exclusion</cite>).
     *
     */
    private Boolean extentTypeCode;
    
	public GeographicExtentItem() {
	}

	public Boolean getExtentTypeCode() {
		return extentTypeCode;
	}

	public void setExtentTypeCode(Boolean extentTypeCode) {
		this.extentTypeCode = extentTypeCode;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GeographicExtentItem [extentTypeCode=");
		builder.append(extentTypeCode);
		builder.append("]");
		return builder.toString();
	}

}
