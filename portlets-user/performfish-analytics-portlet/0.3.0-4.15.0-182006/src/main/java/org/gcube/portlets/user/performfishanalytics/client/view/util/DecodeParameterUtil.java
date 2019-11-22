package org.gcube.portlets.user.performfishanalytics.client.view.util;



// TODO: Auto-generated Javadoc
/**
 * The Class DecodeParameterUtil.
 */
public class DecodeParameterUtil {
	
	/**
	 * Base 64 decode.
	 *
	 * @param valueToDecode the value to decode
	 * @return the long
	 * @throws Exception the exception
	 */
	public static String base64Decode(String valueToDecode) throws Exception{
		try {
			return b64decode(valueToDecode);
		}catch (Exception e) {
			throw new Exception("Error on decoding the parameter: "+ valueToDecode, e);
		}
	}
	
	private static native String b64decode(String a) /*-{
	  return window.atob(a);
	}-*/;

}
