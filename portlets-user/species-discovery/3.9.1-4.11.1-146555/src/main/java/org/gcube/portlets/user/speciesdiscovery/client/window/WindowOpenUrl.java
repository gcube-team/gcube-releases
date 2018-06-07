package org.gcube.portlets.user.speciesdiscovery.client.window;

import com.google.gwt.user.client.Window;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class WindowOpenUrl {

	public WindowOpenUrl(String url, String name, String features){
		
		Window.open(url, name, features);
	}
}
