package org.gcube.portlet.user.my_vres.client;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.gcube.portlet.user.my_vres.shared.VRE;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface MyVREsServiceAsync {

	void getUserVREs(
			AsyncCallback<LinkedHashMap<String, ArrayList<VRE>>> callback);

	void getSiteLandingPagePath(AsyncCallback<String> callback);

}
