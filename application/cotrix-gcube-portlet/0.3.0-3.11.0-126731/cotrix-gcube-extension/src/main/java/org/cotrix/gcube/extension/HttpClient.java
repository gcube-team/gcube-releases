/**
 * 
 */
package org.cotrix.gcube.extension;

import java.net.URL;

/**
 * @author "Federico De Faveri federico.defaveri@fao.org"
 *
 */
public interface HttpClient {

	String get(URL url, String cookie);

	String post(URL url, String cookie, String content);

}
