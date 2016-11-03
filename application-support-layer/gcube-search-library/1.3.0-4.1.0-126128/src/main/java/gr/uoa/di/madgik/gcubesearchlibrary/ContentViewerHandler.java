package gr.uoa.di.madgik.gcubesearchlibrary;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import gr.uoa.di.madgik.gcubesearchlibrary.exceptions.BadRequestException;
import gr.uoa.di.madgik.gcubesearchlibrary.exceptions.ContentInfoFailureException;
import gr.uoa.di.madgik.gcubesearchlibrary.exceptions.InternalServerErrorException;
import gr.uoa.di.madgik.gcubesearchlibrary.exceptions.NotFoundException;
import gr.uoa.di.madgik.gcubesearchlibrary.exceptions.PropertiesFileRetrievalException;
import gr.uoa.di.madgik.gcubesearchlibrary.exceptions.ServletURLRetrievalException;
import gr.uoa.di.madgik.gcubesearchlibrary.exceptions.UnauthorizedException;
import gr.uoa.di.madgik.gcubesearchlibrary.exceptions.XMLParsingException;
import gr.uoa.di.madgik.gcubesearchlibrary.model.beans.ObjectInfoBean;
import gr.uoa.di.madgik.gcubesearchlibrary.model.beans.ServletURLBean;
import gr.uoa.di.madgik.gcubesearchlibrary.parsers.XMLParser;
import gr.uoa.di.madgik.gcubesearchlibrary.utils.FileUtils;
import gr.uoa.di.madgik.gcubesearchlibrary.utils.PropertiesConstants;
import gr.uoa.di.madgik.gcubesearchlibrary.utils.RequestHandler;
import gr.uoa.di.madgik.gcubesearchlibrary.utils.ServletConstants;

/**
 * Handler for GCube objects
 * 
 * @author Panagiota Koltsida, NKUA
 *
 */
public class ContentViewerHandler {

	/**
	 * Retrieves the given the object's mime type
	 * 
	 * @param documentURI
	 * @param username
	 * @param sessionID
	 * @return
	 * @throws ServletURLRetrievalException
	 * @throws PropertiesFileRetrievalException
	 * @throws UnauthorizedException
	 * @throws NotFoundException
	 * @throws BadRequestException
	 * @throws ContentInfoFailureException
	 * @throws XMLParsingException 
	 */
	public static String getGCubeObjectMimeType(ServletURLBean servletURL, String documentURI, String username, String sessionID) throws ServletURLRetrievalException, PropertiesFileRetrievalException, UnauthorizedException, NotFoundException, BadRequestException, ContentInfoFailureException, XMLParsingException {
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
			String contentServlet = FileUtils.getPropertyValue(PropertiesConstants.ServletPropertiesFileName, PropertiesConstants.CA_PROPERTY_NAME);

			String file = "/" + contentServlet + "/" + ServletConstants.CONTENT_INFO;
			if (sessionID != null) {
				sessionID = sessionID.trim();
				file += ";" + ServletConstants.JSESSION_ID + "=" + sessionID;
			}
			file += "?";
			if (username != null)
				file += ServletConstants.USERNAME + "=" + username + "&";

			file += ServletConstants.DOCUMENT_URI + "=" + documentURI +
					"&" + ServletConstants.FORMAT + "=" + ServletConstants.XML;

			URL url = new URL(protocol, host, new Integer(port), file);
			String response = RequestHandler.submitGetRequest(url, sessionID);
			return XMLParser.parseObjectInfoResponseToGetMime(response);
		} catch (NumberFormatException e) {
			throw new ServletURLRetrievalException(e.getMessage(), e.getCause());
		} catch (IOException e) {
			throw new ServletURLRetrievalException(e.getMessage(), e.getCause());
		} catch (InternalServerErrorException e) {
			throw new ContentInfoFailureException("Failed to get Object's info. An internal server error occurred");
		} 
	}

	public static String getGCubeObjectMimeType(ServletURLBean servletURL, String documentURI) throws ServletURLRetrievalException, PropertiesFileRetrievalException, UnauthorizedException, NotFoundException, BadRequestException, ContentInfoFailureException, XMLParsingException {
		return getGCubeObjectMimeType(servletURL, documentURI, null, null);
	}


	/**
	 * Information for the given object
	 * 
	 * @param servletURL The servlet URL to use or null for the default servlet
	 * @param documentURI The object's URI
	 * @param username 
	 * @param sessionID Current session ID
	 * @return @ObjectInfoBean
	 * @throws ServletURLRetrievalException
	 * @throws PropertiesFileRetrievalException
	 * @throws UnauthorizedException
	 * @throws NotFoundException
	 * @throws BadRequestException
	 * @throws ContentInfoFailureException
	 * @throws XMLParsingException
	 */
	public static ObjectInfoBean getGCubeObjectInfo(ServletURLBean servletURL, String documentURI, String username, String sessionID) throws ServletURLRetrievalException, PropertiesFileRetrievalException, UnauthorizedException, NotFoundException, BadRequestException, ContentInfoFailureException, XMLParsingException {		
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
			String contentServlet = FileUtils.getPropertyValue(PropertiesConstants.ServletPropertiesFileName, PropertiesConstants.CA_PROPERTY_NAME);

			String file = "/" + contentServlet + "/" + ServletConstants.CONTENT_INFO;
			
			if (sessionID != null)
				file += ";" + ServletConstants.JSESSION_ID + "=" + sessionID;
			file += "?";
			if (username != null)
				file += ServletConstants.USERNAME + "=" + username + "&";

			file += ServletConstants.DOCUMENT_URI + "=" + documentURI +
					"&" + ServletConstants.FORMAT + "=" + ServletConstants.XML;

			URL url = new URL(protocol, host, new Integer(port), file);
			String response = RequestHandler.submitGetRequest(url, sessionID);
			return XMLParser.parseObjectInfoResponseToGetObjectInfo(response);
		} catch (NumberFormatException e) {
			throw new ServletURLRetrievalException(e.getMessage(), e.getCause());
		}
		catch (IOException e) {
			throw new ServletURLRetrievalException(e.getMessage(), e.getCause());
		} catch (InternalServerErrorException e) {
			throw new ContentInfoFailureException("Failed to get Object's info. An internal server error occurred");
		}
	}
	
	public static ObjectInfoBean getGCubeObjectInfo(ServletURLBean servletURL, String documentURI) throws ServletURLRetrievalException, PropertiesFileRetrievalException, UnauthorizedException, NotFoundException, BadRequestException, ContentInfoFailureException, XMLParsingException {
		return  getGCubeObjectInfo(servletURL, documentURI, null, null);
	}

	/**
	 * Object's payload as a stream
	 * 
	 * @param servletURL The servlet URL to use or null for the default servlet
	 * @param documentURI The object's URI
	 * @param username
	 * @param sessionID Current session ID
	 * @return Object's payload as a stream
	 */
	public static ByteArrayOutputStream getGcubeObjectContent(ServletURLBean servletURL, String documentURI, String username, String sessionID) { 
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
			String contentServlet = FileUtils.getPropertyValue(PropertiesConstants.ServletPropertiesFileName, PropertiesConstants.CA_PROPERTY_NAME);

			String file = "/" + contentServlet + "/" + ServletConstants.CONTENT_VIEWER;

			if (sessionID != null) {
				sessionID = sessionID.trim();
				file += ";" + ServletConstants.JSESSION_ID + "=" + sessionID;
			}
			file += "?";
			if (username != null)
				file += ServletConstants.USERNAME + "=" + username + "&";

			file +=	ServletConstants.DOCUMENT_URI + "=" + documentURI;

			URL url = new URL(protocol, host, new Integer(port), file);
			return RequestHandler.submitGetRequestByteStream(url, sessionID);
		} catch (Exception e) {
			return null;
		}
	}

	public static ByteArrayOutputStream getGcubeObjectContent(ServletURLBean servletURL, String documentURI) {
		return getGcubeObjectContent(servletURL, documentURI, null, null);
	}

	/**
	 * Object's payload URL
	 * @param servletURL The servlet URL to use or null for the default servlet
	 * @param documentURI The object's URI
	 * @param username
	 * @param sessionID Current session ID
	 * @return The URL
	 * @throws ServletURLRetrievalException
	 * @throws PropertiesFileRetrievalException
	 */
	public static String getGCubeObjectContentURL(ServletURLBean servletURL, String documentURI, String username, String sessionID) throws ServletURLRetrievalException, PropertiesFileRetrievalException {
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
			String contentServlet = FileUtils.getPropertyValue(PropertiesConstants.ServletPropertiesFileName, PropertiesConstants.CA_PROPERTY_NAME);

			String file = "/" + contentServlet + "/" + ServletConstants.CONTENT_VIEWER;

			if (sessionID != null) {
				sessionID = sessionID.trim();
				file += ";" + ServletConstants.JSESSION_ID + "=" + sessionID;
			}
			file += "?";
			if (username != null)
				file += ServletConstants.USERNAME + "=" + username + "&";

			file +=	ServletConstants.DOCUMENT_URI + "=" + documentURI;

			URL url = new URL(protocol, host, new Integer(port), file);
			return url.toString();
		} catch (NumberFormatException e) {
			throw new ServletURLRetrievalException(e.getMessage(), e.getCause());
		} catch (MalformedURLException e) {
			throw new ServletURLRetrievalException(e.getMessage(), e.getCause());
		}
	}

	public static String getGCubeObjectContentURL(ServletURLBean servletURL, String documentURI) throws ServletURLRetrievalException, PropertiesFileRetrievalException {
		return getGCubeObjectContentURL(servletURL, documentURI, null, null);
	}
}
