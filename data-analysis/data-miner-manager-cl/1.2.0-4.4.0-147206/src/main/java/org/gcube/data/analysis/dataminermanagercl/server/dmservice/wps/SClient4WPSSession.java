package org.gcube.data.analysis.dataminermanagercl.server.dmservice.wps;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.zip.GZIPInputStream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.opengis.ows.x11.ExceptionReportDocument;
import net.opengis.ows.x11.OperationDocument.Operation;
import net.opengis.wps.x100.CapabilitiesDocument;
import net.opengis.wps.x100.ExecuteDocument;
import net.opengis.wps.x100.ExecuteResponseDocument;
import net.opengis.wps.x100.ProcessBriefType;
import net.opengis.wps.x100.ProcessDescriptionType;
import net.opengis.wps.x100.ProcessDescriptionsDocument;

import org.apache.commons.codec.binary.Base64;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.n52.wps.client.ClientCapabiltiesRequest;
import org.n52.wps.client.WPSClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * 
 * @author Giancarlo Panichi email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class SClient4WPSSession implements Serializable {

	private static final long serialVersionUID = -1387670579312851370L;
	private static Logger logger = LoggerFactory
			.getLogger(SClient4WPSSession.class);
	private static final String OGC_OWS_URI = "http://www.opengeospatial.net/ows";
	private static String SUPPORTED_VERSION = "1.0.0";

	// private static StatWPSClientSession session;
	private HashMap<String, CapabilitiesDocument> loggedServices;
	private XmlOptions options = null;

	// a Map of <url, all available process descriptions>
	public HashMap<String, ProcessDescriptionsDocument> processDescriptions;
	private String user;
	private String password;

	/**
	 * Initializes a WPS client session.
	 *
	 */
	public SClient4WPSSession(String user, String password) {
		super();
		logger.debug("Create SClient4WPSSession: [user=" + user
				+ ", password=" + password + "]");
		this.user = user;
		this.password = password;
		options = new XmlOptions();
		options.setLoadStripWhitespace();
		options.setLoadTrimTextBuffer();
		loggedServices = new HashMap<String, CapabilitiesDocument>();
		processDescriptions = new HashMap<String, ProcessDescriptionsDocument>();
	}

	/**
	 * Connects to a WPS and retrieves Capabilities plus puts all available
	 * Descriptions into cache.
	 * 
	 * @param url
	 *            the entry point for the service. This is used as id for
	 *            further identification of the service.
	 * @return true, if connect succeeded, false else.
	 * @throws WPSClientException
	 */

	public boolean connect(String url) throws WPSClientException {
		logger.info("CONNECT: " + url);
		logger.debug("LoggedSevices: " + loggedServices.keySet());
		if (loggedServices.containsKey(url)) {
			logger.debug("Service already registered: " + url);
			return false;
		}
		logger.debug("Service not registered");
		CapabilitiesDocument capsDoc = retrieveCapsViaGET(url);
		if (capsDoc != null) {
			logger.debug("Adding caps to logged services " + url);
			loggedServices.put(url, capsDoc);
			logger.debug("Logged Services key: " + loggedServices.keySet());
		} else {
			logger.error("CapsDoc is null!");
		}

		ProcessDescriptionsDocument processDescs = describeAllProcesses(url);
		if (processDescs != null && capsDoc != null) {
			logger.debug("Adding processes descriptions to logged services "
					+ url);
			processDescriptions.put(url, processDescs);
			logger.debug("ProcessDescriptions key: "
					+ processDescriptions.keySet());
			return true;

		} else {
			logger.error("ProcessDescs  is null!");
		}

		logger.warn("retrieving caps failed, caps are null");
		return false;
	}

	/**
	 * Connects to a WPS and retrieves Capabilities plus puts all available
	 * Descriptions into cache.
	 * 
	 * @param url
	 *            the entry point for the service. This is used as id for
	 *            further identification of the service.
	 * @return true, if connect succeeded, false else.
	 * @throws WPSClientException
	 */
	public boolean connectForMonitoring(String url) throws WPSClientException {
		logger.debug("CONNECT");
		if (loggedServices.containsKey(url)) {
			logger.debug("Service already registered: " + url);
			return false;
		}

		logger.warn("retrieving caps failed, caps are null");
		return false;
	}

	/**
	 * removes a service from the session
	 * 
	 * @param url
	 */
	public void disconnect(String url) {
		/*
		 * if (loggedServices.containsKey(url)) { loggedServices.remove(url);
		 * processDescriptions.remove(url);
		 * logger.debug("service removed successfully: " + url); }
		 */
	}

	/**
	 * returns the serverIDs of all loggedServices
	 * 
	 * @return
	 */
	public List<String> getLoggedServices() {
		if (loggedServices != null && loggedServices.keySet() != null) {
			return new ArrayList<String>(loggedServices.keySet());
		} else {
			return new ArrayList<String>();
		}
	}

	/**
	 * informs you if the descriptions for the specified service is already in
	 * the session. in normal case it should return true :)
	 * 
	 * @param serverID
	 * @return success
	 */
	public boolean descriptionsAvailableInCache(String serverID) {
		return processDescriptions.containsKey(serverID);
	}

	/**
	 * returns the cached processdescriptions of a service.
	 * 
	 * @param serverID
	 * @return success
	 * @throws IOException
	 */
	private ProcessDescriptionsDocument getProcessDescriptionsFromCache(
			String wpsUrl) throws IOException {
		if (!descriptionsAvailableInCache(wpsUrl)) {
			try {
				connect(wpsUrl);
			} catch (WPSClientException e) {
				throw new IOException("Could not initialize WPS " + wpsUrl);
			}
		}
		return processDescriptions.get(wpsUrl);
	}

	/**
	 * return the processDescription for a specific process from Cache.
	 * 
	 * @param serverID
	 * @param processID
	 * @return a ProcessDescription for a specific process from Cache.
	 * @throws IOException
	 */
	public ProcessDescriptionType getProcessDescription(String serverID,
			String processID) throws IOException {
		ProcessDescriptionType[] processes = getProcessDescriptionsFromCache(
				serverID).getProcessDescriptions().getProcessDescriptionArray();
		for (ProcessDescriptionType process : processes) {
			if (process.getIdentifier().getStringValue().equals(processID)) {
				return process;
			}
		}
		return null;
	}

	/**
	 * Delivers all ProcessDescriptions from a WPS
	 * 
	 * @param wpsUrl
	 *            the URL of the WPS
	 * @return An Array of ProcessDescriptions
	 * @throws IOException
	 */
	public ProcessDescriptionType[] getAllProcessDescriptions(String wpsUrl)
			throws IOException {
		return getProcessDescriptionsFromCache(wpsUrl).getProcessDescriptions()
				.getProcessDescriptionArray();
	}

	/**
	 * looks up, if the service exists already in session.
	 */
	public boolean serviceAlreadyRegistered(String serverID) {
		return loggedServices.containsKey(serverID);
	}

	/**
	 * provides you the cached capabilities for a specified service.
	 * 
	 * @param url
	 * @return
	 */
	public CapabilitiesDocument getWPSCaps(String url) {
		return loggedServices.get(url);
	}

	/**
	 * retrieves all current available ProcessDescriptions of a WPS. Mention: to
	 * get the current list of all processes, which will be requested, the
	 * cached capabilities will be used. Please keep that in mind. the retrieved
	 * descriptions will not be cached, so only transient information!
	 * 
	 * @param url
	 * @return
	 * @throws WPSClientException
	 */
	public ProcessDescriptionsDocument describeAllProcesses(String url)
			throws WPSClientException {
		CapabilitiesDocument doc = loggedServices.get(url);
		if (doc == null) {
			logger.warn("serviceCaps are null, perhaps server does not exist");
			return null;
		}
		ProcessBriefType[] processes = doc.getCapabilities()
				.getProcessOfferings().getProcessArray();
		String[] processIDs = new String[processes.length];
		for (int i = 0; i < processIDs.length; i++) {
			processIDs[i] = processes[i].getIdentifier().getStringValue();
		}
		return describeProcess(processIDs, url);

	}

	/**
	 * retrieves the desired description for a service. the retrieved
	 * information will not be held in cache!
	 * 
	 * @param processIDs
	 *            one or more processIDs
	 * @param serverID
	 * @throws WPSClientException
	 */
	public ProcessDescriptionsDocument describeProcess(String[] processIDs,
			String serverID) throws WPSClientException {

		CapabilitiesDocument caps = this.loggedServices.get(serverID);
		Operation[] operations = caps.getCapabilities().getOperationsMetadata()
				.getOperationArray();
		String url = null;
		for (Operation operation : operations) {
			if (operation.getName().equals("DescribeProcess")) {
				url = operation.getDCPArray()[0].getHTTP().getGetArray()[0]
						.getHref();
			}
		}
		if (url == null) {
			throw new WPSClientException(
					"Missing DescribeOperation in Capabilities");
		}
		return retrieveDescriptionViaGET(processIDs, url);
	}

	/**
	 * Executes a process at a WPS
	 * 
	 * @param url
	 *            url of server not the entry additionally defined in the caps.
	 * @param execute
	 *            Execute document
	 * @return either an ExecuteResponseDocument or an InputStream if asked for
	 *         RawData or an Exception Report
	 */
	private Object execute(String serverID, ExecuteDocument execute,
			boolean rawData) throws WPSClientException {
		CapabilitiesDocument caps = loggedServices.get(serverID);
		Operation[] operations = caps.getCapabilities().getOperationsMetadata()
				.getOperationArray();
		String url = null;
		for (Operation operation : operations) {
			if (operation.getName().equals("Execute")) {
				url = operation.getDCPArray()[0].getHTTP().getPostArray()[0]
						.getHref();
			}
		}
		if (url == null) {
			throw new WPSClientException(
					"Caps does not contain any information about the entry point for process execution");
		}
		execute.getExecute().setVersion(SUPPORTED_VERSION);
		return retrieveExecuteResponseViaPOST(url, execute, rawData);
	}

	/**
	 * Executes a process at a WPS
	 * 
	 * @param url
	 *            url of server not the entry additionally defined in the caps.
	 * @param execute
	 *            Execute document
	 * @return either an ExecuteResponseDocument or an InputStream if asked for
	 *         RawData or an Exception Report
	 */
	public Object execute(String serverID, ExecuteDocument execute)
			throws WPSClientException {
		if (execute.getExecute().isSetResponseForm() == true
				&& execute.getExecute().isSetResponseForm() == true
				&& execute.getExecute().getResponseForm().isSetRawDataOutput() == true) {
			return execute(serverID, execute, true);
		} else {
			return execute(serverID, execute, false);
		}

	}

	private CapabilitiesDocument retrieveCapsViaGET(String url)
			throws WPSClientException {
		logger.debug("retrieveCapsViaGET: " + url);
		ClientCapabiltiesRequest req = new ClientCapabiltiesRequest();
		url = req.getRequest(url);

		try {
			String authString = user + ":" + password;
			logger.debug("auth string: " + authString);
			byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
			String encoded = new String(authEncBytes);
			logger.debug("Base64 encoded auth string: " + encoded);

			URL urlObj = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) urlObj
					.openConnection();
			connection.setRequestMethod("GET");
			connection.setDoOutput(true);
			connection.setRequestProperty("Authorization", "Basic " + encoded);
			InputStream is = connection.getInputStream();
			Document doc = checkInputStream(is);
			CapabilitiesDocument capabilitiesDocument = CapabilitiesDocument.Factory
					.parse(doc, options);
			return capabilitiesDocument;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			throw new WPSClientException(
					"Capabilities URL seems to be unvalid: " + url, e);
		} catch (IOException e) {
			e.printStackTrace();
			throw new WPSClientException(
					"Error occured while retrieving capabilities from url: "
							+ url, e);
		} catch (XmlException e) {
			e.printStackTrace();
			throw new WPSClientException("Error occured while parsing XML", e);
		}
	}

	private ProcessDescriptionsDocument retrieveDescriptionViaGET(
			String[] processIDs, String url) throws WPSClientException {
		try {
			logger.debug("RetrieveDescription GET: " + processIDs + " url:"
					+ url);

			Path tempFile = Files.createTempFile("WPSProcessDescriptions",
					"txt");

			List<String> lines = new ArrayList<>();
			lines.add("<wps:ProcessDescriptions xmlns:wps=\"http://www.opengis.net/wps/1.0.0\" "
					+ "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
					+ "xmlns:ows=\"http://www.opengis.net/ows/1.1\" "
					+ "xsi:schemaLocation=\"http://www.opengis.net/wps/1.0.0 "
					+ "http://schemas.opengis.net/wps/1.0.0/wpsDescribeProcess_response.xsd\" "
					+ "xml:lang=\"en-US\" "
					+ "service=\"WPS\" version=\"1.0.0\">");
			Files.write(tempFile, lines, Charset.defaultCharset(),
					StandardOpenOption.APPEND);

			for (String processId : processIDs) {
				String[] process = { processId };
				DClientDescribeProcessRequest req = new DClientDescribeProcessRequest();
				req.setIdentifier(process);
				String requestURL = req.getRequest(url);

				String authString = user + ":" + password;
				//logger.debug("auth string: " + authString);
				byte[] authEncBytes = Base64
						.encodeBase64(authString.getBytes());
				String encoded = new String(authEncBytes);
				//logger.debug("Base64 encoded auth string: " + encoded);

				URL urlObj = new URL(requestURL);
				HttpURLConnection connection = (HttpURLConnection) urlObj
						.openConnection();
				connection.setRequestMethod("GET");
				connection.setDoOutput(true);
				connection.setRequestProperty("Authorization", "Basic "
						+ encoded);
				InputStream is = connection.getInputStream();
				lines=retrievesSingleDescription(is);
				Files.write(tempFile, lines, Charset.defaultCharset(),
						StandardOpenOption.APPEND);
			}
			lines = new ArrayList<>();
			lines.add("</wps:ProcessDescriptions>");
			Files.write(tempFile, lines, Charset.defaultCharset(),
					StandardOpenOption.APPEND);

			logger.debug(tempFile.toString());
			Document doc=null;
			try (InputStream inputStream = Files.newInputStream(tempFile,
						StandardOpenOption.READ)){
				doc = checkInputStream(inputStream);
				
			} 
			ProcessDescriptionsDocument processDescriptionsDocument = ProcessDescriptionsDocument.Factory.parse(doc, options);
			
			Files.delete(tempFile);
			return processDescriptionsDocument;
			
			
		} catch (MalformedURLException e) {
			logger.error("URL seems not to be valid");
			e.printStackTrace();
			throw new WPSClientException("URL seems not to be valid", e);
		} catch (IOException e) {
			logger.error("Error occured while receiving data");
			e.printStackTrace();
			throw new WPSClientException("Error occured while receiving data",
					e);
		} catch (XmlException e) {
			logger.error("Error occured while parsing ProcessDescription document");
			e.printStackTrace();
			throw new WPSClientException(
					"Error occured while parsing ProcessDescription document",
					e);
		} catch (Throwable e) {
			logger.error(e.getLocalizedMessage());
			e.printStackTrace();
			throw new WPSClientException(e.getLocalizedMessage(),
					new Exception(e));
		}
	}

	protected List<String> retrievesSingleDescription(InputStream is) throws  WPSClientException {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(is));

			List<String> lines = new ArrayList<>();
			String line = null;
			boolean elementProcessDescriptionsFound = false;
			boolean elementProcessDescriptionsClosureFound = false;

			while ((line = br.readLine()) != null) {
				if (elementProcessDescriptionsFound) {
					if (elementProcessDescriptionsClosureFound) {
						if (line.contains("</wps:ProcessDescriptions>")) {
							break;
						} else {
							lines.add(line);
						}
					} else {
						int closeIndex = line.indexOf(">");
						if (closeIndex != -1) {
							elementProcessDescriptionsClosureFound = true;
							if (closeIndex == line.length() - 1) {

							} else {

							}
						}
					}
				} else {
					if (line.contains("<wps:ProcessDescriptions")) {
						elementProcessDescriptionsFound = true;
						int closeIndex = line.indexOf(">");
						if (closeIndex != -1) {
							elementProcessDescriptionsClosureFound = true;
							if (closeIndex == line.length() - 1) {

							} else {

							}
						}
					} else {

					}
				}
			}
			return lines;
		} catch (Throwable e) {
			logger.error(e.getLocalizedMessage());
			e.printStackTrace();
			throw new WPSClientException(e.getLocalizedMessage(),
					new Exception(e));
		}
	}

	private InputStream retrieveDataViaPOST(XmlObject obj, String urlString)
			throws WPSClientException {
		try {
			logger.debug("RetrieveDataViaPost(): " + urlString);
			String authString = user + ":" + password;
			logger.debug("auth string: " + authString);
			byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
			String encoded = new String(authEncBytes);
			logger.debug("Base64 encoded auth string: " + encoded);

			URL url = new URL(urlString);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Authorization", "Basic " + encoded);
			conn.setRequestProperty("Accept-Encoding", "gzip");
			conn.setRequestProperty("Content-Type", "text/xml");
			conn.setDoOutput(true);
			obj.save(conn.getOutputStream());
			InputStream input = null;
			String encoding = conn.getContentEncoding();
			if (encoding != null && encoding.equalsIgnoreCase("gzip")) {
				input = new GZIPInputStream(conn.getInputStream());
			} else {
				input = conn.getInputStream();
			}
			return input;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			throw new WPSClientException("URL seems to be unvalid", e);
		} catch (IOException e) {
			e.printStackTrace();
			throw new WPSClientException("Error while transmission", e);
		}
	}

	private Document checkInputStream(InputStream is) throws WPSClientException {
		try {
			DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
			fac.setNamespaceAware(true);
			Document doc = fac.newDocumentBuilder().parse(is);
			logger.debug("Document: " + doc);
			if (doc == null) {
				logger.error("Document is null");
				throw new WPSClientException(
						"Error in check input stream: Document is null");
			}

			if (getFirstElementNode(doc.getFirstChild()).getLocalName().equals(
					"ExceptionReport")
					&& getFirstElementNode(doc.getFirstChild())
							.getNamespaceURI().equals(OGC_OWS_URI)) {
				try {
					ExceptionReportDocument exceptionDoc = ExceptionReportDocument.Factory
							.parse(doc);
					logger.debug(exceptionDoc.xmlText(options));
					throw new WPSClientException(
							"Error occured while executing query", exceptionDoc);
				} catch (XmlException e) {
					throw new WPSClientException(
							"Error while parsing ExceptionReport retrieved from server",
							e);
				}
			} else {
				logger.debug("No Exception Report");
			}
			return doc;
		} catch (SAXException e) {
			logger.error("Error while parsing input: "
					+ e.getLocalizedMessage());
			e.printStackTrace();
			throw new WPSClientException("Error while parsing input", e);
		} catch (IOException e) {
			logger.error("Error occured while transfer: "
					+ e.getLocalizedMessage());
			e.printStackTrace();
			throw new WPSClientException("Error occured while transfer", e);
		} catch (ParserConfigurationException e) {
			logger.error("Error occured, parser is not correctly configured: "
					+ e.getLocalizedMessage());
			e.printStackTrace();
			throw new WPSClientException(
					"Error occured, parser is not correctly configured", e);
		} catch (WPSClientException e) {
			throw e;
		}
	}

	private Node getFirstElementNode(Node node) {
		if (node == null) {
			return null;
		}
		if (node.getNodeType() == Node.ELEMENT_NODE) {
			return node;
		} else {
			return getFirstElementNode(node.getNextSibling());
		}

	}

	/**
	 * either an ExecuteResponseDocument or an InputStream if asked for RawData
	 * or an Exception Report
	 * 
	 * @param url
	 * @param doc
	 * @param rawData
	 * @return
	 * @throws WPSClientException
	 */
	private Object retrieveExecuteResponseViaPOST(String url,
			ExecuteDocument doc, boolean rawData) throws WPSClientException {
		InputStream is = retrieveDataViaPOST(doc, url);
		if (rawData) {
			return is;
		}
		Document documentObj = checkInputStream(is);
		ExceptionReportDocument erDoc = null;
		try {

			return ExecuteResponseDocument.Factory.parse(documentObj);
		} catch (XmlException e) {
			try {
				erDoc = ExceptionReportDocument.Factory.parse(documentObj);
			} catch (XmlException e1) {
				throw new WPSClientException(
						"Error occured while parsing executeResponse", e);
			}
			return erDoc;
		}
	}

	public String[] getProcessNames(String url) throws IOException {
		ProcessDescriptionType[] processes = getProcessDescriptionsFromCache(
				url).getProcessDescriptions().getProcessDescriptionArray();
		String[] processNames = new String[processes.length];
		for (int i = 0; i < processNames.length; i++) {
			processNames[i] = processes[i].getIdentifier().getStringValue();
		}
		return processNames;
	}

	/**
	 * Executes a process at a WPS
	 * 
	 * @param url
	 *            url of server not the entry additionally defined in the caps.
	 * @param executeAsGETString
	 *            KVP Execute request
	 * @return either an ExecuteResponseDocument or an InputStream if asked for
	 *         RawData or an Exception Report
	 */
	public Object executeViaGET(String urlString, String executeAsGETString)
			throws WPSClientException {
		urlString = urlString + executeAsGETString;
		logger.debug("ExecuteViaGet() Url: " + urlString);
		try {
			// TODO
			String authString = user + ":" + password;
			logger.debug("auth string: " + authString);
			byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
			String encoded = new String(authEncBytes);
			logger.debug("Base64 encoded auth string: " + encoded);

			URL url = new URL(urlString);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Authorization", "Basic " + encoded);
			conn.setDoOutput(true);
			InputStream is = conn.getInputStream();

			if (executeAsGETString.toUpperCase().contains("RAWDATA")) {
				logger.debug("ExecuteAsGETString as RAWDATA");
				return is;
			}
			Document doc = checkInputStream(is);
			ExceptionReportDocument erDoc = null;
			logger.debug("ExecuteAsGETString as Document");

			try {
				return ExecuteResponseDocument.Factory.parse(doc);
			} catch (XmlException e) {
				e.printStackTrace();
				try {
					erDoc = ExceptionReportDocument.Factory.parse(doc);
				} catch (XmlException e1) {
					e1.printStackTrace();
					throw new WPSClientException(
							"Error occured while parsing executeResponse", e);

				}
				throw new WPSClientException(
						"Error occured while parsing executeResponse", erDoc);
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
			throw new WPSClientException(
					"Capabilities URL seems to be unvalid: " + urlString, e);
		} catch (IOException e) {
			e.printStackTrace();
			throw new WPSClientException(
					"Error occured while retrieving capabilities from url: "
							+ urlString, e);
		}

	}

	public String cancelComputation(String url, String computationId)
			throws WPSClientException {

		try {
			String authString = user + ":" + password;
			logger.debug("auth string: " + authString);
			byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
			String encoded = new String(authEncBytes);
			logger.debug("Base64 encoded auth string: " + encoded);

			url += "?id=" + computationId;
			URL urlObj = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) urlObj
					.openConnection();
			connection.setRequestMethod("GET");
			connection.setDoOutput(true);
			connection.setRequestProperty("Authorization", "Basic " + encoded);
			String responseMessage = connection.getResponseMessage();
			return responseMessage;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			throw new WPSClientException(
					"Capabilities URL seems to be unvalid: " + url, e);
		} catch (IOException e) {
			e.printStackTrace();
			throw new WPSClientException(
					"Error occured while retrieving capabilities from url: "
							+ url, e);
		}
	}

}
