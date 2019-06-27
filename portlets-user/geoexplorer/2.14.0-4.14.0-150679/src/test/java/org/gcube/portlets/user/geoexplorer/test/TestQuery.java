/**
 *
 */
package org.gcube.portlets.user.geoexplorer.test;


import it.geosolutions.geonetwork.util.GNSearchRequest;
import it.geosolutions.geonetwork.util.GNSearchResponse;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.spatial.data.geonetwork.GeoNetwork;
import org.gcube.spatial.data.geonetwork.GeoNetworkAdministration;
import org.gcube.spatial.data.geonetwork.LoginLevel;


/**
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jul 28, 2016
 */
public class TestQuery {

	public static void main(String[] args) throws Exception{
		try{
	        String scope = "/d4science.research-infrastructures.eu/gCubeApps";
	        String title ="oscar";
	        ScopeProvider.instance.set(scope);

	        GeoNetworkAdministration reader=GeoNetwork.get();
	        reader.login(LoginLevel.SCOPE);

	        //Configure search request
	        GNSearchRequest req=new GNSearchRequest();
	        req.addParam(GNSearchRequest.Param.any,title);
	        req.addConfig(GNSearchRequest.Config.similarity, "1");
	        GNSearchResponse resp=reader.query(req);
	        System.out.println("Found N layers: "+resp.getCount());
			}catch(Exception e){
				e.printStackTrace();
			}
	    }
}
