package org.gcube.portlets.widgets.applicationnews.client;

import org.gcube.portal.databook.shared.ApplicationProfile;
import org.gcube.portlets.widgets.applicationnews.shared.LinkPreview;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>ApplicationService</code>.
 */
public interface ApplicationServiceAsync {
	void getApplicationProfile(String portletClassName,
			AsyncCallback<ApplicationProfile> callback);

	void publishAppNews(String portletClassName, String textToShow,
			String uriGETparams, LinkPreview linkPreview,
			AsyncCallback<Boolean> callback);
}
