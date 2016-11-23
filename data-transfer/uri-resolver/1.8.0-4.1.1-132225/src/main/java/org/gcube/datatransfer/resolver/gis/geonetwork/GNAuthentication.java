/**
 *
 */
package org.gcube.datatransfer.resolver.gis.geonetwork;

import org.apache.commons.httpclient.HttpStatus;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The Class GNAuthentication.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Apr 4, 2016
 */
public class GNAuthentication {

	private static Logger logger = LoggerFactory.getLogger(GNAuthentication.class);
	public static final String XML_USER_LOGOUT = "xml.user.logout";
	private static final String SRV_EN_XML_USER_LOGIN = "/srv/en/xml.user.login";

	/**
	 * Perform a GN login.<br/>
	 * GN auth is carried out via a JSESSIONID cookie returned by a successful login
	 * call.<br/>
	 *
	 * <ul>
	 * <li>Url: <tt>http://<i>server</i>:<i>port</i>/geonetwork/srv/en/xml.user.login</tt></li>
	 * <li>Mime-type: <tt>application/xml</tt></li>
	 * <li>Post request: <pre>{@code
	 *   <?xml version="1.0" encoding="UTF-8"?>
	 *   <request>
	 *       <username>user</username>
	 *       <password>pwd</password>
	 *   </request>
	 * }</pre></li>
	 * </ul>
	 *
	 * @param connection the connection
	 * @param serviceURL the service url
	 * @param username the username
	 * @param password the password
	 * @return true, if successful
	 */
    public static boolean login(HTTPCallsUtils connection, String serviceURL, String username, String password) {
        Element request = new Element("request");
        request.addContent(new Element("username").setText(username));
        request.addContent(new Element("password").setText(password));

        XMLOutputter outputter = new XMLOutputter(Format.getCompactFormat());
        String xml = outputter.outputString(request);

        logger.trace("Authentication on Geonetwork: "+xml);

        String loginURL = serviceURL+SRV_EN_XML_USER_LOGIN;
        connection.postXml(loginURL, xml);

        return connection.getLastHttpStatus() == HttpStatus.SC_OK;
    }


    /**
     * Logout.
     *
     * @param connection the connection
     * @param serviceURL the service url
     * @return true, if successful
     */
    public static boolean logout(HTTPCallsUtils connection, String serviceURL) {
        Element request = new Element("request");

        XMLOutputter outputter = new XMLOutputter(Format.getCompactFormat());
        String xml = outputter.outputString(request);

        String logOut = serviceURL+XML_USER_LOGOUT;
        connection.postXml(logOut, xml);

        return connection.getLastHttpStatus() == HttpStatus.SC_OK;
    }
}