package gr.uoa.di.madgik.gcubesearchlibrary;

import gr.uoa.di.madgik.gcubesearchlibrary.exceptions.BadRequestException;
import gr.uoa.di.madgik.gcubesearchlibrary.exceptions.CollectionsRetrievalFailureException;
import gr.uoa.di.madgik.gcubesearchlibrary.exceptions.InternalServerErrorException;
import gr.uoa.di.madgik.gcubesearchlibrary.exceptions.NotFoundException;
import gr.uoa.di.madgik.gcubesearchlibrary.exceptions.PropertiesFileRetrievalException;
import gr.uoa.di.madgik.gcubesearchlibrary.exceptions.SearchFailureException;
import gr.uoa.di.madgik.gcubesearchlibrary.exceptions.SearchStatusFailureException;
import gr.uoa.di.madgik.gcubesearchlibrary.exceptions.ServletURLRetrievalException;
import gr.uoa.di.madgik.gcubesearchlibrary.exceptions.UnauthorizedException;
import gr.uoa.di.madgik.gcubesearchlibrary.exceptions.XMLParsingException;
import gr.uoa.di.madgik.gcubesearchlibrary.model.beans.CollectionBean;
import gr.uoa.di.madgik.gcubesearchlibrary.model.beans.CriterionBean;
import gr.uoa.di.madgik.gcubesearchlibrary.model.beans.ResultBean;
import gr.uoa.di.madgik.gcubesearchlibrary.model.beans.SearchStatusBean;
import gr.uoa.di.madgik.gcubesearchlibrary.model.beans.ServletURLBean;
import gr.uoa.di.madgik.gcubesearchlibrary.parsers.XMLParser;
import gr.uoa.di.madgik.gcubesearchlibrary.utils.FileUtils;
import gr.uoa.di.madgik.gcubesearchlibrary.utils.PropertiesConstants;
import gr.uoa.di.madgik.gcubesearchlibrary.utils.RequestHandler;
import gr.uoa.di.madgik.gcubesearchlibrary.utils.ServletConstants;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;

/**
 * SearchHandler class for submitting queries to the gCube search system
 * Interacts with the Application Support HTTP servlet
 * 
 * @author Panagiota Koltsida, NKUA
 *
 */
public class SearchHandler {

	/**
	 * A hashmap with the collections split in groups
	 * 
	 * @param servletURL The servlet URL to use or null for the default
	 * @param username 
	 * @param sessionID The current session ID
	 * @return
	 * @throws PropertiesFileRetrievalException 
	 * @throws ServletURLRetrievalException 
	 * @throws BadRequestException 
	 * @throws NotFoundException 
	 * @throws UnauthorizedException 
	 * @throws XMLParsingException 
	 * @throws CollectionsRetrievalFailureException 
	 */
	public static HashMap<String, List<CollectionBean>> getCollections(ServletURLBean servletURL, String username, String sessionID) throws PropertiesFileRetrievalException, ServletURLRetrievalException, UnauthorizedException, NotFoundException, BadRequestException, XMLParsingException, CollectionsRetrievalFailureException {

		//System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");
		//Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());

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
			String irServlet = FileUtils.getPropertyValue(PropertiesConstants.ServletPropertiesFileName, PropertiesConstants.IR_PROPERTY_NAME);
			String file = "/" + irServlet + "/" + ServletConstants.GET_COLLECTIONS +
					";" + ServletConstants.JSESSION_ID + "=" + sessionID +
					"?" + ServletConstants.FORMAT + "=" + ServletConstants.XML +
					"&" + ServletConstants.USERNAME + "=" + username;

			URL url = new URL(protocol, host, new Integer(port), file);
			String response = RequestHandler.submitGetRequest(url, sessionID);
			return XMLParser.parseCollectionsResponse(response);
		} catch (NumberFormatException e) {
			throw new ServletURLRetrievalException(e.getMessage(), e.getCause());
		} catch (IOException e) {
			throw new ServletURLRetrievalException(e.getMessage(), e.getCause());
		} catch (InternalServerErrorException e) {
			throw new CollectionsRetrievalFailureException("Failed to retrieve the available collections. An internal server error occurred");
		}

	}

	/**
	 * The searchable fields for the selected collections
	 * 
	 * @param servletURL The servlet URL to use or null for the default
	 * @param username 
	 * @param selectedCollectionsIDs A list with the selected collections' IDs
	 * @param sessionID The current session ID
	 * @return An object that contains all the needed information
	 * @throws PropertiesFileRetrievalException 
	 * @throws BadRequestException 
	 * @throws NotFoundException 
	 * @throws UnauthorizedException 
	 * @throws XMLParsingException 
	 * @throws ServletURLRetrievalException 
	 * @throws SearchStatusFailureException 
	 */
	public static SearchStatusBean getSearchStatus(ServletURLBean servletURL, List<String> selectedCollectionsIDs, String username, String sessionID) throws PropertiesFileRetrievalException, UnauthorizedException, NotFoundException, BadRequestException, XMLParsingException, ServletURLRetrievalException, SearchStatusFailureException {
		sessionID = sessionID.trim();
		if (selectedCollectionsIDs != null) {
			StringBuilder sb = new StringBuilder();
			// {%22SelectedCollections%22:[%220a0574d0-f400-11dd-9a38-9b05ac676cca%22]} 
			sb.append("{\"SelectedCollections\":[");
			for (String collectionID : selectedCollectionsIDs) {
				sb.append("\"");
				sb.append(collectionID);
				sb.append("\",");
			}
			sb.deleteCharAt(sb.length()-1);
			sb.append("]}");

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
				String irServlet = FileUtils.getPropertyValue(PropertiesConstants.ServletPropertiesFileName, PropertiesConstants.IR_PROPERTY_NAME);
				String file = "/" + irServlet + "/" + ServletConstants.COLLECTIONS_INFO + ";" + ServletConstants.JSESSION_ID + "=" + sessionID +
						"?" + ServletConstants.FORMAT + "=" + ServletConstants.XML + "&" + ServletConstants.SELECTED_COLLECTIONS + "=" + sb.toString() +
						"&" + ServletConstants.USERNAME + "=" + username;

				URL url = new URL(protocol, host, new Integer(port), file);
				String response = RequestHandler.submitGetRequest(url, sessionID);
				return XMLParser.parseCollectionInfoResponse(response);
			} catch (NumberFormatException e) {
				throw new ServletURLRetrievalException(e.getMessage(), e.getCause());
			} catch (IOException e) {
				throw new ServletURLRetrievalException(e.getMessage(), e.getCause());
			} catch (InternalServerErrorException e) {
				throw new SearchStatusFailureException("Failed to get the search status for the selected collections. An internal server error occurred");
			}
		}
		return null;
	}

	/**
	 * Submits a fulltext search query
	 * 
	 * @param servletURL The servlet URL to use or null for the default
	 * @param searchTerm The search terms
	 * @param selectedCollectionsIDs A list with the selected collections' IDs
	 * @param username The user's username
	 * @param sessionID The current session ID
	 * @return A List of @ResultBean
	 * @throws SearchFailureException 
	 * @throws ServletURLRetrievalException 
	 * @throws XMLParsingException 
	 * @throws PropertiesFileRetrievalException 
	 * @throws BadRequestException 
	 * @throws NotFoundException 
	 * @throws UnauthorizedException 
	 */
	public static List<ResultBean> submitFullTextSearch(ServletURLBean servletURL, String searchTerm, List<String> selectedCollectionsIDs, String username, String sessionID, Integer numOfResults) throws SearchFailureException, ServletURLRetrievalException, XMLParsingException, PropertiesFileRetrievalException, UnauthorizedException, NotFoundException, BadRequestException {
		sessionID = sessionID.trim();
		if (selectedCollectionsIDs != null) {
			StringBuilder sb = new StringBuilder();
			// {%22SelectedCollections%22:[%220a0574d0-f400-11dd-9a38-9b05ac676cca%22]} 
			sb.append("{\"SelectedCollections\":[");
			for (String collectionID : selectedCollectionsIDs) {
				sb.append("\"");
				sb.append(collectionID);
				sb.append("\",");
			}
			sb.deleteCharAt(sb.length()-1);
			sb.append("]}");

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
				String irServlet = FileUtils.getPropertyValue(PropertiesConstants.ServletPropertiesFileName, PropertiesConstants.IR_PROPERTY_NAME);
				String file = "/" + irServlet + "/" + ServletConstants.SEARCH + ";" + ServletConstants.JSESSION_ID + "=" + sessionID +
						//String file = "/" + irServlet + "/" + ServletConstants.SEARCH +
						"?" + ServletConstants.FORMAT + "=" + ServletConstants.XML + 
						"&" + ServletConstants.SEARCH_TYPE + "=" + ServletConstants.SIMPLE_SEARCH + 
						"&" + ServletConstants.SEARCH_TERMS + "=" + URLEncoder.encode(searchTerm, "UTF-8") + 
						"&" + ServletConstants.SELECTED_COLLECTIONS + "=" + sb.toString() +
						"&" + ServletConstants.USERNAME + "=" + username + 
						"&" + ServletConstants.ALL_FIELDS;
				

				String resNum = null;
				if (numOfResults != null) {
					file += "&" + ServletConstants.RESULTS_NUMBER + "=" + numOfResults.toString();
				}
				else if ((resNum = FileUtils.getPropertyValue(PropertiesConstants.ServletPropertiesFileName, PropertiesConstants.NUM_OF_RESULTS)) != null) {
					file += "&" + ServletConstants.RESULTS_NUMBER + "=" + resNum;
				}

				URL url = new URL(protocol, host, new Integer(port), file);
				//long start = System.currentTimeMillis();
				String response = RequestHandler.submitGetRequest(url, sessionID);
				//long end = System.currentTimeMillis();
				//System.out.println("Got response in -> " + (end -start)/1000 + " seconds");
				return XMLParser.parseSearchResults(response);
			} catch (NumberFormatException e) {
				throw new ServletURLRetrievalException(e.getMessage(), e.getCause());
			} catch (IOException e) {
				throw new ServletURLRetrievalException(e.getMessage(), e.getCause());
			} catch (InternalServerErrorException e) {
				throw new SearchFailureException("Search submission failed. An internal server error occurred");
			}
		}
		return null;
	}

	public static List<ResultBean> submitSimpleSearchForTesting(ServletURLBean servletURL, String searchTerm, Integer numOfResults) throws SearchFailureException, ServletURLRetrievalException, XMLParsingException, PropertiesFileRetrievalException, UnauthorizedException, NotFoundException, BadRequestException {

		// This is for testing reasons only. It submits a search in the FARM Fao Country maps
		StringBuilder sb = new StringBuilder();
		// {%22SelectedCollections%22:[%220a0574d0-f400-11dd-9a38-9b05ac676cca%22]} 
		sb.append("{\"SelectedCollections\":[");
		sb.append("\"");
		sb.append("c5b83790-f35f-11dd-9a37-9b05ac676cca");
		sb.append("\"");
		sb.append("]}");

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
			String irServlet = FileUtils.getPropertyValue(PropertiesConstants.ServletPropertiesFileName, PropertiesConstants.IR_PROPERTY_NAME);
			String file = "/" + irServlet + "/" + ServletConstants.SEARCH +
					//String file = "/" + irServlet + "/" + ServletConstants.SEARCH +
					"?" + ServletConstants.FORMAT + "=" + ServletConstants.XML + 
					"&" + ServletConstants.SEARCH_TYPE + "=" + ServletConstants.SIMPLE_SEARCH + 
					"&" + ServletConstants.SEARCH_TERMS + "=" + searchTerm + 
					"&" + ServletConstants.SELECTED_COLLECTIONS + "=" + sb.toString();

			String resNum = null;
			if (numOfResults != null) {
				file += "&" + ServletConstants.RESULTS_NUMBER + "=" + numOfResults.toString();
			}
			else if ((resNum = FileUtils.getPropertyValue(PropertiesConstants.ServletPropertiesFileName, PropertiesConstants.NUM_OF_RESULTS)) != null) {
				file += "&" + ServletConstants.RESULTS_NUMBER + "=" + resNum;
			}

			URL url = new URL(protocol, host, new Integer(port), file);
			String response = RequestHandler.submitGetRequest(url, null);
			return XMLParser.parseSearchResults(response);
		} catch (NumberFormatException e) {
			throw new ServletURLRetrievalException(e.getMessage(), e.getCause());
		} catch (IOException e) {
			throw new ServletURLRetrievalException(e.getMessage(), e.getCause());
		} catch (InternalServerErrorException e) {
			throw new SearchFailureException("Search submission failed. An internal server error occurred");
		}
	}

	/**
	 * Submits a generic search query
	 * This type of search does not require to select any collections and is working with anonymous access
	 * 
	 * @param servletURL
	 * @param searchTerm
	 * @param numOfResults
	 * @return
	 * @throws SearchFailureException
	 * @throws ServletURLRetrievalException
	 * @throws XMLParsingException
	 * @throws PropertiesFileRetrievalException
	 * @throws UnauthorizedException
	 * @throws NotFoundException
	 * @throws BadRequestException
	 */
	public static List<ResultBean> submitGenericSearch(ServletURLBean servletURL, String searchTerm, Integer numOfResults) throws SearchFailureException, ServletURLRetrievalException, XMLParsingException, PropertiesFileRetrievalException, UnauthorizedException, NotFoundException, BadRequestException {
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
			String irServlet = FileUtils.getPropertyValue(PropertiesConstants.ServletPropertiesFileName, PropertiesConstants.IR_PROPERTY_NAME);
			String file = "/" + irServlet + "/" + ServletConstants.GENERIC_SEARCH +
					"?" + ServletConstants.FORMAT + "=" + ServletConstants.XML + 
					"&" + ServletConstants.SEARCH_TERMS + "=" + URLEncoder.encode(searchTerm, "UTF-8") +
					"&" + ServletConstants.ALL_FIELDS;

			String resNum = null;
			if (numOfResults != null) {
				file += "&" + ServletConstants.RESULTS_NUMBER + "=" + numOfResults.toString();
			}
			else if ((resNum = FileUtils.getPropertyValue(PropertiesConstants.ServletPropertiesFileName, PropertiesConstants.NUM_OF_RESULTS)) != null) {
				file += "&" + ServletConstants.RESULTS_NUMBER + "=" + resNum;
			}

			URL url = new URL(protocol, host, new Integer(port), file);
			String response = RequestHandler.submitGetRequest(url, null);
			return XMLParser.parseSearchResults(response);
		} catch (NumberFormatException e) {
			throw new ServletURLRetrievalException(e.getMessage(), e.getCause());
		} catch (IOException e) {
			throw new ServletURLRetrievalException(e.getMessage(), e.getCause());
		} catch (InternalServerErrorException e) {
			throw new SearchFailureException("Search submission failed. An internal server error occurred");
		}
	}

	/**
	 * Submits an advanced search
	 * 
	 * @param servletURL The servlet URL to use or null for the default
	 * @param criteria A list with the criteria to used in search
	 * @param selectedCollectionsIDs A list with the selected collections' IDs
	 * @param username The user's username
	 * @param sessionID The current session ID
	 * @param numOfResults The maximum number of results to return
	 * @return A list of @ResultBean
	 * @throws SearchFailureException 
	 * @throws ServletURLRetrievalException 
	 * @throws PropertiesFileRetrievalException 
	 * @throws BadRequestException 
	 * @throws NotFoundException 
	 * @throws UnauthorizedException 
	 * @throws XMLParsingException 
	 */
	public static List<ResultBean> submitAdvancedSearch(ServletURLBean servletURL, List<CriterionBean> criteria, List<String> selectedCollectionsIDs, String username, String sessionID, Integer numOfResults) throws SearchFailureException, ServletURLRetrievalException, PropertiesFileRetrievalException, UnauthorizedException, NotFoundException, BadRequestException, XMLParsingException {
		sessionID = sessionID.trim();
		if (selectedCollectionsIDs != null) {

			//[{"id":"0e6b455e-f4da-42ea-8008-91effa0da553","value":"image"} ,{"id":"0e6b455e-f4da-42ea-8008-91effa0da553","value":"satellite"}]

			StringBuilder collectionsSb = new StringBuilder();
			collectionsSb.append("{\"SelectedCollections\":[");
			for (String collectionID : selectedCollectionsIDs) {
				collectionsSb.append("\"");
				collectionsSb.append(collectionID);
				collectionsSb.append("\",");
			}
			collectionsSb.deleteCharAt(collectionsSb.length()-1);
			collectionsSb.append("]}");

			StringBuilder criteriaSb = new StringBuilder();
			criteriaSb.append("[");
			for (CriterionBean c : criteria) {
				criteriaSb.append("{\"id\":\"");
				criteriaSb.append(c.getId());
				criteriaSb.append("\",\"value\":\"");
				criteriaSb.append(c.getValue());
				criteriaSb.append("\"},");
			}
			criteriaSb.deleteCharAt(criteriaSb.length()-1);
			criteriaSb.append("]");

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
				String irServlet = FileUtils.getPropertyValue(PropertiesConstants.ServletPropertiesFileName, PropertiesConstants.IR_PROPERTY_NAME);
				String file = "/" + irServlet + "/" + ServletConstants.SEARCH + ";" + ServletConstants.JSESSION_ID + "=" + sessionID +
						"?" + ServletConstants.FORMAT + "=" + ServletConstants.XML + 
						"&" + ServletConstants.SEARCH_TYPE + "=" + ServletConstants.ADVANCED_SEARCH + 
						"&" + ServletConstants.CRITERIA + "=" + URLEncoder.encode(criteriaSb.toString(), "UTF-8") + 
						"&" + ServletConstants.SELECTED_COLLECTIONS + "=" + collectionsSb.toString() +
						"&" + ServletConstants.LANGUAGE + "=" + ServletConstants.ENGLISH +
						"&" + ServletConstants.USERNAME + "=" + username +
						"&" + ServletConstants.ALL_FIELDS;
				

				String resNum = null;
				if (numOfResults != null) {
					file += "&" + ServletConstants.RESULTS_NUMBER + "=" + numOfResults.toString();
				}
				else if ((resNum = FileUtils.getPropertyValue(PropertiesConstants.ServletPropertiesFileName, PropertiesConstants.NUM_OF_RESULTS)) != null) {
					file += "&" + ServletConstants.RESULTS_NUMBER + "=" + resNum;
				}
				URL url = new URL(protocol, host, new Integer(port), file);
				String response = RequestHandler.submitGetRequest(url, sessionID);
				return XMLParser.parseSearchResults(response);
			} catch (NumberFormatException e) {
				throw new ServletURLRetrievalException(e.getMessage(), e.getCause());
			} catch (IOException e) {
				throw new ServletURLRetrievalException(e.getMessage(), e.getCause());
			} catch (InternalServerErrorException e) {
				throw new SearchFailureException("Search submission failed. An internal server error occurred");
			}
		}
		return null;
	}
}
