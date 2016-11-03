package org.gcube.datatransfer.resolver.gis;

import org.gcube.datatransfer.resolver.gis.entity.ServerParameters;
import org.gcube.datatransfer.resolver.gis.exception.GeonetworkInstanceException;
import org.gcube.spatial.data.geonetwork.LoginLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The Class GeonetworkAccessParameter.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jun 9, 2016
 */
public class GeonetworkAccessParameter implements GeonetworkServiceInterface{


	protected static Logger logger = LoggerFactory.getLogger(GeonetworkAccessParameter.class);

	protected GeonetworkInstance geonetworkInstance;

	protected String scope;

	protected ServerParameters serverParam;

	/**
	 * The Enum GeonetworkLoginLevel.
	 *
	 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
	 * Jun 9, 2016
	 */
	public static enum GeonetworkLoginLevel {
		DEFAULT,
		SCOPE,
		PRIVATE,
		CKAN,
		ADMIN
	}

	/**
	 * Instantiates a new geonetowrk access parameter.
	 *
	 * @param scope the scope
	 * @param serverParam the server param
	 */
	public GeonetworkAccessParameter(String scope, ServerParameters serverParam) {
		this.scope = scope;
		this.serverParam = serverParam;
	}


	/* (non-Javadoc)
	 * @see org.gcube.datatransfer.resolver.gis.GeonetworkServiceInterface#getGeonetworkInstance(boolean, org.gcube.datatransfer.resolver.gis.GeonetowrkAccessParameter.GeonetworkLoginLevel)
	 */
	public GeonetworkInstance getGeonetworkInstance(boolean authenticate, GeonetworkLoginLevel loginLevel) throws GeonetworkInstanceException {
		return instanceGeonetwork(authenticate, loginLevel);
	}

	/**
	 * Instance geonetwork.
	 *
	 * @param authenticate the authenticate
	 * @param loginLevel the login level
	 * @return the geonetwork instance
	 * @throws GeonetworkInstanceException the geonetwork instance exception
	 */
	private GeonetworkInstance instanceGeonetwork(boolean authenticate, GeonetworkLoginLevel loginLevel) throws GeonetworkInstanceException{

		if(scope == null || scope.isEmpty())
			throw new GeonetworkInstanceException("Scope is null");

		if(serverParam.getUrl() == null || serverParam.getUrl().isEmpty())
			throw new GeonetworkInstanceException("Geonetwork url is null or empty");

		LoginLevel level = toLoginLevel(loginLevel);
		if(geonetworkInstance==null)
			geonetworkInstance = new GeonetworkInstance(scope,  authenticate, level);

		return geonetworkInstance;

	}


	/* (non-Javadoc)
	 * @see org.gcube.datatransfer.resolver.gis.GeonetworkServiceInterface#getGeonetworkInstance()
	 */
	@Override
	public GeonetworkInstance getGeonetworkInstance()
		throws Exception {

		if(scope == null || scope.isEmpty())
			throw new GeonetworkInstanceException("Scope is null");

		/*if(serverParam.getUrl() == null || serverParam.getUrl().isEmpty())
			throw new GeonetworkInstanceException("Geonetwork url is null or empty");*/

		if(geonetworkInstance==null)
			geonetworkInstance = new GeonetworkInstance(scope,  false, null);

		return geonetworkInstance;
	}

	/* (non-Javadoc)
	 * @see org.gcube.datatransfer.resolver.gis.GeonetworkServiceInterface#getScope()
	 */
	public String getScope() {
		return scope;
	}


	/**
	 * To login level.
	 *
	 * @param loginLevel the login level
	 * @return the login level
	 */
	public static final LoginLevel toLoginLevel(GeonetworkLoginLevel loginLevel){

		switch (loginLevel) {
		case ADMIN:
			return LoginLevel.ADMIN;
		case CKAN:
			return LoginLevel.CKAN;
		case DEFAULT:
			return LoginLevel.DEFAULT;
		case PRIVATE:
			return LoginLevel.PRIVATE;
		case SCOPE:
			return LoginLevel.SCOPE;
		}
		return null;

	}
}
