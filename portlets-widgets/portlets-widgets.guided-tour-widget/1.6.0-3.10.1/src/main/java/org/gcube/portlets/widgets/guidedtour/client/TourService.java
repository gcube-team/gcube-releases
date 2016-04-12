package org.gcube.portlets.widgets.guidedtour.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.  
 * @author Massimiliano Assante ISTI-CNR
 * 
 * @version 1.0 Feb 1st 2012
 *
 */
@RemoteServiceRelativePath("quicktourServlet")
public interface TourService extends RemoteService {
	Boolean showTour(String portletUniqueId);
	
	void setNotShowItAgain(String portletUniqueId);
}
