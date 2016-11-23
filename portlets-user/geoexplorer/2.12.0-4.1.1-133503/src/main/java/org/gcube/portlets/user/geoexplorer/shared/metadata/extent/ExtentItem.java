package org.gcube.portlets.user.geoexplorer.shared.metadata.extent;

import java.io.Serializable;
import java.util.List;


public class ExtentItem implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6653569765771054903L;
	
	
	/**
     * Returns the spatial and temporal extent for the referring object.
     */
    private String description;

    /**
     * Provides geographic component of the extent of the referring object
     *
     * @return The geographic extent, or an empty set if none.
     *
     */
    private List<GeographicBoundingBoxItem> geographicElement;

    


	/**
     * Constructs an initially empty extent.
     */
    public ExtentItem() {
    }

	public ExtentItem(String description,
			List<GeographicBoundingBoxItem> geographicElement) {
		super();
		this.description = description;
		this.geographicElement = geographicElement;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<GeographicBoundingBoxItem> getGeographicElement() {
		return geographicElement;
	}

	public void setGeographicElement(
			List<GeographicBoundingBoxItem> geographicElement) {
		this.geographicElement = geographicElement;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ExtentItem [description=");
		builder.append(description);
		builder.append(", geographicElement=");
		builder.append(geographicElement);
		builder.append("]");
		return builder.toString();
	}

}
