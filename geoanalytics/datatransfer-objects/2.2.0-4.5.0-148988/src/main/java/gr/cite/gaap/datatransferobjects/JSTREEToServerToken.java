/**
 * 
 */
package gr.cite.gaap.datatransferobjects;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author vfloros
 *
 */
public class JSTREEToServerToken {
	private static Logger logger = LoggerFactory.getLogger(JSTREEToServerToken.class);
	private String type = null;
	private UUID geocodeSystemID = null;
	private double[] geographyExtent = null;
	private String tenantName = "";
	
	
	public JSTREEToServerToken() {
		super();
		logger.trace("Initialized default contructor for JSTREEToServerToken");
	}
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
	public UUID getGeocodeSystemID() {
		return geocodeSystemID;
	}
	public void setGeocodeSystemID(UUID taxonomyID) {
		this.geocodeSystemID = taxonomyID;
	}
	public String getTenantName() {
		return tenantName;
	}
	public void setTenantName(String tenantName) {
		this.tenantName = tenantName;
	}
}
