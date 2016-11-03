/**
 * 
 */
package gr.cite.gaap.datatransferobjects;

import java.util.UUID;

/**
 * @author vfloros
 *
 */
public class JSTREEToServerToken {
	private String type = null;
	private UUID taxonomyID = null;
	private double[] geographyExtent = null;
	
	public double[] getGeographyExtent() {
		return geographyExtent;
	}
	public void setGeographyExtent(double[] geographyExtent) {
		this.geographyExtent = geographyExtent;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public UUID getTaxonomyID() {
		return taxonomyID;
	}
	public void setTaxonomyID(UUID taxonomyID) {
		this.taxonomyID = taxonomyID;
	}
}
