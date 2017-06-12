package org.gcube.portlets.user.messages.client.view.window;

import com.google.gwt.user.client.Window;

public class WindowOpenUrl {

	public WindowOpenUrl(String url, String name, String features){
		
		Window.open(url, name, features);
	}
}
