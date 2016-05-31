package gr.uoa.di.madgik.gcubesearchlibrary;

import gr.uoa.di.madgik.gcubesearchlibrary.exceptions.BadRequestException;
import gr.uoa.di.madgik.gcubesearchlibrary.exceptions.InternalServerErrorException;
import gr.uoa.di.madgik.gcubesearchlibrary.exceptions.NotFoundException;
import gr.uoa.di.madgik.gcubesearchlibrary.exceptions.PropertiesFileRetrievalException;
import gr.uoa.di.madgik.gcubesearchlibrary.exceptions.ServletURLRetrievalException;
import gr.uoa.di.madgik.gcubesearchlibrary.exceptions.SetVREFailureException;
import gr.uoa.di.madgik.gcubesearchlibrary.exceptions.SignOutFailureException;
import gr.uoa.di.madgik.gcubesearchlibrary.exceptions.UnauthorizedException;
import gr.uoa.di.madgik.gcubesearchlibrary.exceptions.VresListingFailureException;
import gr.uoa.di.madgik.gcubesearchlibrary.model.beans.ServletURLBean;
import gr.uoa.di.madgik.gcubesearchlibrary.parsers.XMLParser;
import gr.uoa.di.madgik.gcubesearchlibrary.utils.FileUtils;
import gr.uoa.di.madgik.gcubesearchlibrary.utils.PropertiesConstants;
import gr.uoa.di.madgik.gcubesearchlibrary.utils.RequestHandler;
import gr.uoa.di.madgik.gcubesearchlibrary.utils.ServletConstants;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;


/**
 * Login to the gCube system through ASL HTTP servlet
 * 
 * @author Panagiota Koltsida, NKUA
 *
 */
public class LoginHandler {

	/**
	 * Sign in to the gCube system
	 * 
	 * @param username User's username
	 * @param pass User's password
	 * @return The session ID
	 * @throws ServletURLRetrievalException 
	 * @throws PropertiesFileRetrievalException 
	 * @throws UnauthorizedException
	 * @throws NotFoundException
	 */
	public static String signIn(ServletURLBean servletURL, String username, String pass) throws ServletURLRetrievalException, UnauthorizedException, NotFoundException, PropertiesFileRetrievalException {
	//	System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");
	//	Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());

		String request = null;

		try {
			request = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8");
			request += "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(pass, "UTF-8");
		} catch (UnsupportedEncodingException e) {

		}
		try {
			String protocol = null;
			String host = null;
			String port = null;
			if (servletURL != null) {
				protocol = servletURL.getProtocol();
				host = servletURL.getHost();
				port = servletURL.getPort();
			}
			else {
				protocol = FileUtils.getPropertyValue(PropertiesConstants.ServletPropertiesFileName, PropertiesConstants.PROTOCOL);
				host = FileUtils.getPropertyValue(PropertiesConstants.ServletPropertiesFileName, PropertiesConstants.HOST);
				port = FileUtils.getPropertyValue(PropertiesConstants.ServletPropertiesFileName, PropertiesConstants.PORT);		
			}
			String loginServlet = FileUtils.getPropertyValue(PropertiesConstants.ServletPropertiesFileName, PropertiesConstants.LOGIN_PROPERTY_NAME);
			String file = "/" + loginServlet + "/" + ServletConstants.LOGIN;
			URL url = new URL(protocol, host, new Integer(port), file);

			return  XMLParser.parseSignInResponse(RequestHandler.submitPostRequest(url, request));
		} catch (NumberFormatException e) {
			throw new ServletURLRetrievalException(e.getMessage(), e.getCause());
		} catch (MalformedURLException e) {
			throw new ServletURLRetrievalException(e.getMessage(), e.getCause());
		} catch (IOException e) {
			throw new ServletURLRetrievalException(e.getMessage(), e.getCause());
		}
	}

	/**
	 * Terminates the current session
	 * 
	 * @param servletURL The servlet URL to use or null for the default
	 * @param sessionID The current session ID
	 * @throws SignOutFailureException
	 */
	public static void signOut(ServletURLBean servletURL, String sessionID) throws SignOutFailureException {
		try {
			sessionID = sessionID.trim();
			String protocol = null;
			String host = null;
			String port = null;
			if (servletURL != null) {
				protocol = servletURL.getProtocol();
				host = servletURL.getHost();
				port = servletURL.getPort();
			}
			else {
				protocol = FileUtils.getPropertyValue(PropertiesConstants.ServletPropertiesFileName, PropertiesConstants.PROTOCOL);
				host = FileUtils.getPropertyValue(PropertiesConstants.ServletPropertiesFileName, PropertiesConstants.HOST);
				port = FileUtils.getPropertyValue(PropertiesConstants.ServletPropertiesFileName, PropertiesConstants.PORT);		
			}
			String loginServlet = FileUtils.getPropertyValue(PropertiesConstants.ServletPropertiesFileName, PropertiesConstants.LOGIN_PROPERTY_NAME);
			String file = "/" + loginServlet + "/" + ServletConstants.LOGOUT;
			URL url = new URL(protocol, host, new Integer(port), file);
			RequestHandler.submitGetRequest(url, sessionID);
		} catch (Exception e) {
			throw new SignOutFailureException("SignOut failed for the session ID '" + sessionID + "'");
		}

	}

	/**
	 * Returns the VREs that the current user belongs to
	 * 
	 * @param sessionID The current session ID
	 * @return A list with the VREs
	 * @throws PropertiesFileRetrievalException 
	 * @throws ServletURLRetrievalException 
	 * @throws BadRequestException 
	 * @throws NotFoundException 
	 * @throws UnauthorizedException 
	 * @throws VresListingFailureException 
	 */
	public static List<String> getUserVREs(ServletURLBean servletURL, String sessionID) throws PropertiesFileRetrievalException, ServletURLRetrievalException, UnauthorizedException, NotFoundException, BadRequestException, VresListingFailureException {		
		try {
			sessionID = sessionID.trim();
			String protocol = null;
			String host = null;
			String port = null;
			if (servletURL != null) {
				protocol = servletURL.getProtocol();
				host = servletURL.getHost();
				port = servletURL.getPort();
			}
			else {
				protocol = FileUtils.getPropertyValue(PropertiesConstants.ServletPropertiesFileName, PropertiesConstants.PROTOCOL);
				host = FileUtils.getPropertyValue(PropertiesConstants.ServletPropertiesFileName, PropertiesConstants.HOST);
				port = FileUtils.getPropertyValue(PropertiesConstants.ServletPropertiesFileName, PropertiesConstants.PORT);		
			}
			String loginServlet = FileUtils.getPropertyValue(PropertiesConstants.ServletPropertiesFileName, PropertiesConstants.LOGIN_PROPERTY_NAME);
			String file = "/" + loginServlet + "/" + ServletConstants.LIST_USER_VREs + ";" + ServletConstants.JSESSION_ID + "=" + sessionID;
			URL url = new URL(protocol, host, new Integer(port), file);

			String response = RequestHandler.submitGetRequest(url, sessionID);
			if (response != null)
				return XMLParser.parseVREsResponse(response);
		} catch (NumberFormatException e) {
			throw new ServletURLRetrievalException(e.getMessage(), e.getCause());
		} catch (IOException e) {
			throw new ServletURLRetrievalException(e.getMessage(), e.getCause());			
		} catch (InternalServerErrorException e) {
			throw new VresListingFailureException("Failed to retrieve the available VREs. An internal server error occurred");
		}

		return null;
	}

	/**
	 * Sign in to the given VRE
	 * 
	 * @param vre The VRE to sign in
	 * @param sessionID The current session ID
	 * @throws ServletURLRetrievalException 
	 * @throws BadRequestException 
	 * @throws NotFoundException 
	 * @throws UnauthorizedException 
	 * @throws PropertiesFileRetrievalException 
	 * @throws SetVREFailureException 
	 */
	public static void setVRE(ServletURLBean servletURL, String vre, String sessionID) throws ServletURLRetrievalException, UnauthorizedException, NotFoundException, BadRequestException, PropertiesFileRetrievalException, SetVREFailureException {
		try {
			sessionID = sessionID.trim();
			String protocol = null;
			String host = null;
			String port = null;
			if (servletURL != null) {
				protocol = servletURL.getProtocol();
				host = servletURL.getHost();
				port = servletURL.getPort();
			}
			else {
				protocol = FileUtils.getPropertyValue(PropertiesConstants.ServletPropertiesFileName, PropertiesConstants.PROTOCOL);
				host = FileUtils.getPropertyValue(PropertiesConstants.ServletPropertiesFileName, PropertiesConstants.HOST);
				port = FileUtils.getPropertyValue(PropertiesConstants.ServletPropertiesFileName, PropertiesConstants.PORT);		
			}
			String loginServlet = FileUtils.getPropertyValue(PropertiesConstants.ServletPropertiesFileName, PropertiesConstants.LOGIN_PROPERTY_NAME);
			String file = "/" + loginServlet + "/" + ServletConstants.SET_SCOPE + ";" + ServletConstants.JSESSION_ID + "=" + sessionID +
					"?" + ServletConstants.SCOPE + "=" + vre;

			URL url = new URL(protocol, host, new Integer(port), file);
			RequestHandler.submitGetRequest(url, sessionID);
		} catch (NumberFormatException e) {
			throw new ServletURLRetrievalException(e.getMessage(), e.getCause());
		} catch (IOException e) {
			throw new ServletURLRetrievalException(e.getMessage(), e.getCause());
		} catch (InternalServerErrorException e) {
			throw new SetVREFailureException("Failed to set the selected VRE. An internal server error occurred");
		}

	}
}
