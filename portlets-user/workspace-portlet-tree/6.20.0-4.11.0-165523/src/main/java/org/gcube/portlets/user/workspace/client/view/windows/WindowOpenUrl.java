package org.gcube.portlets.user.workspace.client.view.windows;

import com.google.gwt.user.client.Window;

public class WindowOpenUrl {

	public WindowOpenUrl(String url, String name, String features){
		
		Window.open(url, name, features);
	}
}
