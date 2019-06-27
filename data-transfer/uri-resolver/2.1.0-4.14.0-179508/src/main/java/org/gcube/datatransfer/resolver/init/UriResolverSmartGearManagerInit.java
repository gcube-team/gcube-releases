/**
 *
 */
package org.gcube.datatransfer.resolver.init;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.common.scope.impl.ScopeBean.Type;
import org.gcube.datatransfer.resolver.caches.LoadingGeonetworkInstanceCache;
import org.gcube.datatransfer.resolver.caches.LoadingGisViewerApplicationURLCache;
import org.gcube.datatransfer.resolver.caches.LoadingMapOfScopeCache;
import org.gcube.datatransfer.resolver.gis.property.ApplicationProfilePropertyReader;
import org.gcube.datatransfer.resolver.gis.property.PropertyFileNotFoundException;
import org.gcube.smartgears.ApplicationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The Class UriResolverSmartGearManagerInit.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Nov 15, 2018
 */
public class UriResolverSmartGearManagerInit implements ApplicationManager {

	private static Logger log = LoggerFactory.getLogger(UriResolverSmartGearManagerInit.class);

	public static final String GIS_VIEWER_GENERIC_RESOURCE_GCUBE_APPS_PROPERTIES_FILENAME = "gisviewerappgenericresource.properties";
	public static final String GEO_EXPLORER_GENERIC_RESOURCE_GCUBE_APPS_PROPERTIES_FILENAME = "geoexplorerappgenericresource.properties";
	public static final String PARTHENOS_VRE_PROPERTIES_FILENAME = "parthenosvre.properties";

	protected static final String SECONDARY_TYPE = "SECONDARY_TYPE";
	protected static final String APP_ID = "APP_ID";
	protected static final String VRE_NAME = "VRE_NAME";

	public static final String ENV_SCOPE = "SCOPE"; //Environment Variable

	private static String rootContextScope = null;

	private static ApplicationProfilePropertyReader gisViewerProfile;
	private static ApplicationProfilePropertyReader geoExplorerProfile;
	private static String parthenosVREName;

	/* (non-Javadoc)
	 * @see org.gcube.smartgears.ApplicationManager#onInit()
	 */
	@Override
	public void onInit() {
		log.info("init called with scope: "+ScopeProvider.instance.get());
		try {

			boolean initRootContextPerformed = false;

			if(rootContextScope==null){
				log.debug("The rootContextScope is null, getting it from ScopeProvider");
				String scope = ScopeProvider.instance.get();
				ScopeBean theScopeBean = new ScopeBean(scope);
				log.debug("The ScopeBean is: "+theScopeBean.toString());
				if(theScopeBean.is(Type.INFRASTRUCTURE)){
					rootContextScope = theScopeBean.name();
					rootContextScope = rootContextScope.startsWith("/")?rootContextScope:"/"+rootContextScope;
					log.info("The rootContextScope has value: "+rootContextScope);
					//THE ROOT SCOPE has been initialized
					initRootContextPerformed = true;
				}
			}

			//JUST ONCE AND TO BE SURE WITH THE ROOT SCOPE INITIALIZED
			if(initRootContextPerformed && (gisViewerProfile==null || geoExplorerProfile==null)){
				log.info("init Profiles...");
				gisViewerProfile = loadApplicationProfile(UriResolverServletContextListener.getServletContext(), GIS_VIEWER_GENERIC_RESOURCE_GCUBE_APPS_PROPERTIES_FILENAME);
				geoExplorerProfile = loadApplicationProfile(UriResolverServletContextListener.getServletContext(), GEO_EXPLORER_GENERIC_RESOURCE_GCUBE_APPS_PROPERTIES_FILENAME);
				parthenosVREName = loadPartheosVREName(UriResolverServletContextListener.getServletContext(), PARTHENOS_VRE_PROPERTIES_FILENAME);
		        log.info("GisViewerProfile [ID: "+gisViewerProfile.getAppId() + ", Generic Resource Type: "+gisViewerProfile.getGenericResource()+"] loaded from "+GIS_VIEWER_GENERIC_RESOURCE_GCUBE_APPS_PROPERTIES_FILENAME);
		        log.info("GeoExplorerProfile [ID: "+geoExplorerProfile. getAppId() + ", Generic Resource Type: "+geoExplorerProfile.getGenericResource()+"] loaded from "+GEO_EXPLORER_GENERIC_RESOURCE_GCUBE_APPS_PROPERTIES_FILENAME);
		        log.info("PARTHENOS "+VRE_NAME+" is '"+parthenosVREName+ "' loaded from "+PARTHENOS_VRE_PROPERTIES_FILENAME);
			}

			//JUST ONCE AND TO BE SURE WITH THE ROOT SCOPE INITIALIZED
			if(initRootContextPerformed && gisViewerProfile!=null && geoExplorerProfile!=null){
		        log.info("Pre-Loading caches... using rootContextScope: "+rootContextScope);

		        //init the caches
		        new LoadingGeonetworkInstanceCache();
		        new LoadingGisViewerApplicationURLCache();
		        new LoadingMapOfScopeCache();
			}
		}
		catch (Exception e) {
			//
			log.error(e.getMessage(), e);
		}

    }


	/* (non-Javadoc)
	 * @see org.gcube.smartgears.ApplicationManager#onShutdown()
	 */
	@Override
	public void onShutdown() {

		// TODO Auto-generated method stub

	}


    /**
     * Gets the currency.
     *
     * @param context the context
     * @param propertyFileName the property file name
     * @return the currency
     */
    private static ApplicationProfilePropertyReader loadApplicationProfile(ServletContext context, String propertyFileName) {

        String contextPath = "/WEB-INF/property/"+propertyFileName;
        String realPath = context.getRealPath(contextPath);
        try {
			return new ApplicationProfilePropertyReader(new FileInputStream(new File(realPath)));

        } catch (PropertyFileNotFoundException | FileNotFoundException ex) {
        	log.error("PropertyFileNotFoundException: "+contextPath, ex);
        }

        return null;
    }


    /**
     * Load partheos vre name.
     *
     * @param context the context
     * @param propertyFileName the property file name
     */
    private static String loadPartheosVREName(ServletContext context, String propertyFileName) {

        String contextPath = "/WEB-INF/property/"+propertyFileName;
        String realPath = context.getRealPath(contextPath);
        try {
            Properties props = new Properties();
            props.load(new FileInputStream(new File(realPath)));
			parthenosVREName = props.getProperty(VRE_NAME);
			return parthenosVREName;

        } catch (FileNotFoundException ex) {
        	log.error("PropertyFileNotFoundException: "+contextPath, ex);
        }catch (IOException e) {
        	log.error("Error on loading property from: "+contextPath, e);
		}

        return null;

    }


	/**
	 * Load scope from environment.
	 *
	 * @return the scope read from Environment Variable
	 * @throws ServletException the servlet exception
	 */
	public static String loadScopeFromEnvironment() throws ServletException{

		log.info("Reading Environment Variable "+ENV_SCOPE);
		String scopeFromEnv = System.getenv(ENV_SCOPE);

		if(scopeFromEnv == null || scopeFromEnv.isEmpty())
			throw new ServletException(UriResolverServletContextListener.class.getName() +" cannot read scope from Environment Variable: "+ENV_SCOPE+", It is null or empty");

		log.info("Read scope: "+scopeFromEnv+" from Environment Variable: "+ENV_SCOPE);
		return scopeFromEnv;
	}


	/**
	 * Gets the gis viewer profile.
	 *
	 * @return the gis viewer profile
	 */
	public static ApplicationProfilePropertyReader getGisViewerProfile() {

		return gisViewerProfile;
	}


	/**
	 * Gets the geo explorer profile.
	 *
	 * @return the geoExplorerProfile
	 */
	public static ApplicationProfilePropertyReader getGeoExplorerProfile() {

		return geoExplorerProfile;
	}


	/**
	 * Gets the parthenos vre name.
	 *
	 * @return the parthenosVREName
	 */
	public static String getParthenosVREName() {

		return parthenosVREName;
	}


	/**
	 * Gets the root context scope.
	 *
	 * @return the root context scope
	 */
	public static String getRootContextScope() {

		return rootContextScope;
	}
	
	public static void setRootContextScope(String rootContextScope) {
		UriResolverSmartGearManagerInit.rootContextScope = rootContextScope;
	}
}
