package org.gcube.datatransfer.resolver.gis;

import org.gcube.datatransfer.resolver.gis.exception.GeonetworkInstanceException;
import org.gcube.spatial.data.geonetwork.LoginLevel;
import org.gcube.spatial.data.geonetwork.model.Account.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The Class GeonetworkAccessParameter.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * 
 * May 20, 2019
 */
public class GeonetworkAccessParameter implements GeonetworkServiceInterface{


	protected static Logger logger = LoggerFactory.getLogger(GeonetworkAccessParameter.class);

	protected GeonetworkInstance geonetworkInstance;

	protected String scope;

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
	 * The Enum AccountType.
	 *
	 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
	 * May 15, 2017
	 */
	public static enum AccountType{
		CKAN,
		SCOPE
	}


	/**
	 * Instantiates a new geonetwork access parameter.
	 *
	 * @param scope the scope
	 */
	public GeonetworkAccessParameter(String scope) {
		this.scope = scope;
	}
	

	/* (non-Javadoc)
	 * @see org.gcube.datatransfer.resolver.gis.GeonetworkServiceInterface#getGeonetworkInstance(boolean, org.gcube.datatransfer.resolver.gis.GeonetworkAccessParameter.GeonetworkLoginLevel, org.gcube.datatransfer.resolver.gis.GeonetworkAccessParameter.AccountType)
	 */
	public GeonetworkInstance getGeonetworkInstance(boolean authenticate, GeonetworkLoginLevel loginLevel, AccountType accType) throws GeonetworkInstanceException{
		
		if(geonetworkInstance==null) {

			if(scope == null || scope.isEmpty())
				throw new GeonetworkInstanceException("Scope is null");
	
			LoginLevel level = loginLevel!=null?toLoginLevel(loginLevel):null;
			Type type = accType!=null?toType(accType):null;
			geonetworkInstance = new GeonetworkInstance(scope,  authenticate, level, type);
		}
		
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


		if(loginLevel==null)
			return null;

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
		default:
			logger.info("Returning null converting "+loginLevel+" "+LoginLevel.class.getName() +" a new level has been added?");
			return null;
		}

	}

	/**
	 * To type.
	 *
	 * @param accType the acc type
	 * @return the type
	 */
	private static Type toType(AccountType accType) {

		if(accType==null)
			return null;

		switch (accType) {
		case CKAN:
			return Type.CKAN;
		case SCOPE:
			return Type.SCOPE;
		default:
			logger.info("Returning null converting "+accType+" "+Type.class.getName() +" a new type has been added?");
			return null;
		}
	}

}
