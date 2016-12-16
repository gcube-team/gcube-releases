package org.gcube.application.aquamaps.aquamapsservice.impl.util.isconfig;

import java.util.ArrayList;
import java.util.List;


public class VODescriptor {
	
	private String scope;
	
	private List<GeoServerDescriptor> geoServers=new ArrayList<GeoServerDescriptor>();
	private DataSourceDescriptor geoNetwork;
	
	private DBDescriptor internalDB;
	private DBDescriptor geoDb;
	private DBDescriptor publisherDB;

	public VODescriptor(String scope, List<GeoServerDescriptor> geoServers,
			DataSourceDescriptor geoNetwork, DBDescriptor internalDB,
			DBDescriptor geoDb, DBDescriptor publisherDB) {
		super();
		this.scope = scope;
		this.geoServers = geoServers;
		this.geoNetwork = geoNetwork;
		this.internalDB = internalDB;
		this.geoDb = geoDb;
		this.publisherDB = publisherDB;
	}

	


	/**
	 * @return the scope
	 */
	public String getScope() {
		return scope;
	}




	/**
	 * @return the geoServers
	 */
	public List<GeoServerDescriptor> getGeoServers() {
		return geoServers;
	}



	/**
	 * @return the geoNetwork
	 */
	public DataSourceDescriptor getGeoNetwork() {
		return geoNetwork;
	}



	/**
	 * @return the internalDB
	 */
	public DBDescriptor getInternalDB() {
		return internalDB;
	}



	/**
	 * @return the geoDb
	 */
	public DBDescriptor getGeoDb() {
		return geoDb;
	}



	/**
	 * @return the publisherDB
	 */
	public DBDescriptor getPublisherDB() {
		return publisherDB;
	}





	public GeoServerDescriptor getGeoServerByEntryPoint(String entryPoint) throws ParameterNotFoundException{
		for(GeoServerDescriptor desc:geoServers)
			if(desc.getEntryPoint().equals(entryPoint))return desc;
		throw new ParameterNotFoundException();
	}




	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("VODescriptor [scope=");
		builder.append(scope);
		builder.append(", geoServers=");
		builder.append(geoServers);
		builder.append(", geoNetwork=");
		builder.append(geoNetwork);
		builder.append(", internalDB=");
		builder.append(internalDB);
		builder.append(", geoDb=");
		builder.append(geoDb);
		builder.append(", publisherDB=");
		builder.append(publisherDB);
		builder.append("]");
		return builder.toString();
	}




	
	
}
