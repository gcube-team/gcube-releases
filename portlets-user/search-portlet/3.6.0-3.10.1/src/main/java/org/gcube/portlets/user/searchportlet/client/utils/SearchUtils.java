package org.gcube.portlets.user.searchportlet.client.utils;

import org.gcube.portlets.user.searchportlet.client.SearchPortletG;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class SearchUtils {

	private static String stackTrace = null;
	
	protected static String getStackTraceAsString(Throwable caught) {
	
		AsyncCallback<String> stackTraceCallback = new AsyncCallback<String>() {

			public void onFailure(Throwable caught) {
				
				
			}

			public void onSuccess(String result) {
				stackTrace = result;
				
			}
			
		};SearchPortletG.searchService.stackTraceAsString(caught, stackTraceCallback);
		return stackTrace;
	}
}
