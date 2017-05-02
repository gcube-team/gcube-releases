package gr.cite.clustermanager.model;

import java.util.Objects;

public class GosDefinition {

	private String gosEndpoint;
	private String gosIdentifier;
	private String geoserverEndpoint;
	private String datastoreName;
	private String geoserverWorkspace;

	public GosDefinition(String gosIdentifier, String gosEndpoint, String geoserverEndpoint, String datastoreName, String geoserverWorkspace) {
		this.gosIdentifier = gosIdentifier;
		this.gosEndpoint = gosEndpoint;
		this.geoserverEndpoint = geoserverEndpoint;
		this.datastoreName = datastoreName;
		this.geoserverWorkspace = geoserverWorkspace;
	}

	public String getGosEndpoint() {
		return gosEndpoint;
	}

	public String getGosIdentifier() {
		return gosIdentifier;
	}

	public String getGeoserverEndpoint() {
		return geoserverEndpoint;
	}
	
	public String getDatastoreName() {
		return datastoreName;
	}
	
	public String getGeoserverWorkspace() {
		return geoserverWorkspace;
	}
	

	@Override
	public boolean equals(Object other) {
		if (other == this)
			return true;
		if (!(other instanceof GosDefinition))
			return false;
		GosDefinition otherGos = (GosDefinition) other;
		return Objects.equals(gosIdentifier, otherGos.gosIdentifier);
	}

	@Override
	public int hashCode() {
		return Objects.hash(gosIdentifier);
	}

	@Override
	public String toString(){
		return "GOS [id: "+gosIdentifier+" , endpoint: "+gosEndpoint+" , geoserverUrl: "+geoserverEndpoint+" ]";
	}
	
}
