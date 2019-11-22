/**
 *
 */
package org.gcube.portlets.user.geoexplorer.server;

import java.util.ArrayList;
import java.util.List;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.portlets.user.geoexplorer.client.Constants;
import org.gcube.portlets.user.geoexplorer.client.beans.GeoexplorerMetadataStyleInterface;
import org.gcube.portlets.user.geoexplorer.server.service.GeonetworkInstance;


/**
 *
 * @author Francesco Mangiacrapa ISTI-CNR francesco.mangiacrapa@isti.cnr.it
 *
 * @date Apr 16, 2013
 *
 */
public class DefaultGeoExplorerServiceImpl extends GeoExplorerServiceImpl {

	private static final long serialVersionUID = 7965911406156513171L;

	//DEVELOPMENT ENVIRONMENT
//	public static String geoServerUrl="http://geoserver-dev.d4science-ii.research-infrastructures.eu/geoserver";
//	public static String geoNetworkUrl = "http://geoserver-dev.d4science-ii.research-infrastructures.eu/geonetwork";
//	private static final String geoNetworkUser = "admin";
//	private static final String geoNetworkPwd = "admin";

	//PRODUCTION ENVIRONMENT

//	public static String geoServerUrl = "http://geoserver.d4science-ii.research-infrastructures.eu/geoserver";
//	public static String geoNetworkUrl = "http://geoserver.d4science-ii.research-infrastructures.eu/geonetwork";
//
//	public static String geoServerWMSUrl = "http://geoserver.d4science-ii.research-infrastructures.eu/geoserver/wms";
//	public static String geoServerGWCUrl = "http://geoserver.d4science-ii.research-infrastructures.eu/geoserver/gwc/service/wms";
//	public static String urlGroup = "http://geoserver.d4science-ii.research-infrastructures.eu/geoserver/rest/layergroups.xml";


//	protected static final Workspace[] workspaces = {new Workspace("Aqua Maps", "aquamaps"), new Workspace("Environments", "wsenvironments")};

	protected GeoExplorerServiceParameters parameters;



	@Override
	protected GeoExplorerServiceInterface getGeoParameters() throws Exception {

		//TODO TEMPORARY
		String defaultScope= Constants.defaultScope;
		logger.warn("I'm using Default GeoParameters.. "+defaultScope);
		ScopeProvider.instance.set(defaultScope);
		if (parameters == null){
			parameters = new GeoExplorerServiceParameters(new GeonetworkInstance(true,null));
		}
		return parameters;
	}



	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.geoexplorer.server.GeoExplorerServiceImpl#getInternalGeoserver()
	 */
	@Override
	protected List<String> getInternalGeoserver() throws Exception {
		return new ArrayList<String>();
	}



	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.geoexplorer.server.GeoExplorerServiceImpl#getDefaultLayers()
	 */
	@Override
	protected List<String> getDefaultLayersItem() throws Exception {
		return new ArrayList<String>();
	}



	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.geoexplorer.server.GeoExplorerServiceImpl#getBaseLayersItem()
	 */
	@Override
	protected List<String> getBaseLayersItem() throws Exception {
		return new ArrayList<String>();
	}



	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.geoexplorer.server.GeoExplorerServiceImpl#getGeoexplorerStylesToShow()
	 */
	@Override
	protected List<? extends GeoexplorerMetadataStyleInterface> getGeoexplorerStylesToShow(boolean onlyIsDisplay) throws Exception {
		return null;
	}



	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.geoexplorer.server.GeoExplorerServiceImpl#invalidCache(org.gcube.common.scope.impl.ScopeBean)
	 */
	@Override
	protected void invalidCache(ScopeBean scope) {
	}



	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.geoexplorer.server.GeoExplorerServiceImpl#getGisLinkForUUID(java.lang.String)
	 */
	@Override
	protected String getGisLinkForUUID(String uuid) throws Exception {
		return null;
	}

}
