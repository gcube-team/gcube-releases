package org.gcube.portlets.user.speciesdiscovery.client.rpc;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;


@RemoteServiceRelativePath("gisinfo")
public interface GISInfoService extends RemoteService {

	public String getGisLinkByLayerName(String layername) throws Exception;
	
}
