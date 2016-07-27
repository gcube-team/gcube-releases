package org.gcube.portal.databook.client.util;
/**
 * simply encode base64 strings
 * @author massi
 *
 */
public class Encoder {
	public static native String encode(String toEncode) /*-{
		return btoa(toEncode);
	}-*/;
	
	public static native String decode(String toDecode) /*-{
	return atob(toDecode);
}-*/;
}
