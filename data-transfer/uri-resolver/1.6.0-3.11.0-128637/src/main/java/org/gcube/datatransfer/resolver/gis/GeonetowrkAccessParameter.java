package org.gcube.datatransfer.resolver.gis;

import org.gcube.datatransfer.resolver.gis.entity.ServerParameters;
import org.gcube.datatransfer.resolver.gis.exception.GeonetworkInstanceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Apr 29, 2013
 *
 */
public class GeonetowrkAccessParameter implements GeonetworkServiceInterface{


	protected static Logger logger = LoggerFactory.getLogger(GeonetowrkAccessParameter.class);

	protected GeonetworkInstance geonetworkInstance;

	protected String scope;

	protected ServerParameters serverParam;


	/**
	 * @param serverParam
	 */
	public GeonetowrkAccessParameter(String scope, ServerParameters serverParam) {
		this.scope = scope;
		this.serverParam = serverParam;
	}

	/**
	 * @param geonetworkInstance
	 * @return
	 */
	public GeonetworkInstance getGeonetworkInstance(boolean authenticate) throws GeonetworkInstanceException {
		return instanceGeonetwork(authenticate);
	}

	private GeonetworkInstance instanceGeonetwork(boolean authenticate) throws GeonetworkInstanceException{

		if(scope == null || scope.isEmpty())
			throw new GeonetworkInstanceException("Scope is null");

		if(serverParam.getUrl() == null || serverParam.getUrl().isEmpty())
			throw new GeonetworkInstanceException("Geonetwork url is null or empty");

		if(geonetworkInstance==null)
			geonetworkInstance = new GeonetworkInstance(scope, serverParam.getUrl(), serverParam.getUser(), serverParam.getPassword(), authenticate);

		return geonetworkInstance;


	}

	public String getScope() {
		return scope;
	}
}
