package org.gcube.portlets.widgets.applicationnews.client;

import org.gcube.portal.databook.shared.ApplicationProfile;
import org.gcube.portlets.widgets.applicationnews.shared.LinkPreview;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("appnews")
public interface ApplicationService extends RemoteService {
	/**
	 * 
	 * @param portletClassName
	 * @param textToShow
	 * @param uriGETparams
	 * @param linkPreview
	 * @return
	 */
	boolean publishAppNews(String portletClassName, final String textToShow, final String uriGETparams, final LinkPreview linkPreview);
	
	ApplicationProfile getApplicationProfile(String portletClassName);
}
