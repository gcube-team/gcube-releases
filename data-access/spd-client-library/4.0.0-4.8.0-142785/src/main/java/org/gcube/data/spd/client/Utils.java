package org.gcube.data.spd.client;

import javax.ws.rs.core.Response;

public class Utils {

	public static String getLocatorFromResponse(Response response){
		String path = response.getLocation().getPath();
		String[] splitPath = path.split("/");
		String locator = splitPath[splitPath.length-1].isEmpty()?splitPath[splitPath.length-2]:splitPath[splitPath.length-1];
		return locator;
	}
}
