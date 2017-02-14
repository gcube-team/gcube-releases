package org.gcube.portlets.user.geoexplorer.server;

import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.cyberneko.html.parsers.DOMParser;
import org.gcube.portlets.user.geoexplorer.client.Constants;
import org.gcube.portlets.user.geoexplorer.server.service.GeonetworkInstance;
import org.gcube.portlets.user.geoexplorer.server.util.ASLSessionUtil;
import org.gcube.portlets.user.geoexplorer.server.util.HttpCallerUtil;
import org.gcube.portlets.user.geoexplorer.server.util.TagHTML;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.InputSource;



/**
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jun 4, 2013
 *
 */
public class EmbeddedGeonetworkMetadataISO19139View extends HttpServlet {


	/**
	 *
	 */
	private static final long serialVersionUID = -530347807765351778L;
	/**
	 *
	 */
	protected static final String UTF_8 = "UTF-8";
	/**
	 *
	 */

	public static final String HTML = "HTML";
	public static final String BODY = "BODY";
	public static final String APPLICATION_XML = "application/xml";
	public static final String METADATA_SHOW_EMBEDDED = "metadata.show.embedded";
	public static final String CSS_SOURCE_FILE = "genetworkembedded.css";


	protected static final String TEXT_HTML = "text/html";

	protected static final String GEONETWORK_SERVICE_URL = "srv/en";

	private static final String GCUBE_METADATA_ISO = "GCUBE Metadata ISO view";

	public static final String DOCTYPE = TagHTML.DOCTYPE;

	public static Logger logger = Logger.getLogger(EmbeddedGeonetworkMetadataISO19139View.class);

//	private String geonetworkPassword;
//
//	private String geonetworkUser;
//
//	private boolean isLogged = false;
//
//	private String geonetworkUrl;

//	private HttpCallerUtil httpCallUtil;


	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException {

		String metadataUUID = "";
		String scope = "";
		PrintWriter out = null;
		String currTab = "";

		try {
			ASLSessionUtil.getASLSession(request.getSession()); //USED TO SET TOKEN AND/OR SCOPE
			out =  response.getWriter();
			metadataUUID = request.getParameter(Constants.UUID);
			scope = request.getParameter(Constants.SCOPE);
			currTab = request.getParameter(Constants.CURRTAB);


			//IS VALID UUID?
			if(metadataUUID==null || metadataUUID.isEmpty()){
				out.println(errorPage(GCUBE_METADATA_ISO+metadataUUID, Constants.MESSAGE_METADATA_UUID_NOT_FOUND));

			}else{

				logger.trace("found UUID "+metadataUUID);
				response.setContentType(TEXT_HTML);
				response.setCharacterEncoding(UTF_8);
				try {

					GeonetworkInstance geoInstance = GeoExplorerServiceImpl.getGeonetworkInstanceFromSession(request.getSession(),scope);
					logger.trace("GeonetworkInstance is "+geoInstance);

					geoInstance.authenticateOnGeoenetwork(true);

					HttpCallerUtil httpCaller = loginGeonetwork(geoInstance.getGeoNetworkUrl(), geoInstance.getGeoNetworkUser(), geoInstance.getGeoNetworkPwd());

					if(httpCaller==null)
						throw new Exception("Error in geonetwork login");

					String urlMethod = getMetadataShowEmbeddedUrlMethod();
					String query = getMetadataShowEmbeddedQuery(metadataUUID, currTab);

					String responseHtmlString = httpCaller.callPost(urlMethod, query, APPLICATION_XML);

					Document responseDom = optimizeHtmlAndGetDom(responseHtmlString);

					responseDom = addStylesheet(responseDom, CSS_SOURCE_FILE);

					out.println(printXmlDocument(responseDom));

					logoutGeonetwork(httpCaller);

				} catch (Exception e) {
					out.close();
					throw new Exception("Error in layers csw loader ", e);
				}
			}

			 //CLOSE STREAM
			out.close();

		}catch (Exception e) {
			String error = "Sorry an error occurred when creating the metadata with uuid: "+metadataUUID;
			response.setContentType(TEXT_HTML);

			if(out!=null)
				out.println(error);
			else
				throw new ServletException(error);

			logger.error(error, e);

			out.close(); //CLOSE STREAM

		}
	}

	private HttpCallerUtil getHttCallerUtil(String geonetworkUrl){
		return new HttpCallerUtil(geonetworkUrl + "/" + GEONETWORK_SERVICE_URL, "","");
	}

//	public static String getRowMetadataItemByUUID(String metadataUUID, HttpSession httpSession) throws Exception{
//
//		GeonetworkInstance gn = new GeonetworkInstance(true);
//		logger.trace("-- GeonetworkInstance OK");
//
//		return gn.getGeonetworkReader().getByIdAsRawString(metadataUUID);
//
//	}

	private HttpCallerUtil loginGeonetwork(String url, String user, String pwd) {

		logger.info("Login Geonetwork");
		try {
			//System.out.println("\n\nLOGIN WITH "+this.geonetworkUser+this.geonetworkPassword);
			String query = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
							"<request>" +
								"<username>" + user + "</username>" +
								"<password>" + pwd + "</password>" +
							"</request>";

			HttpCallerUtil res = getHttCallerUtil(url);

			String response = res.callPost("xml.user.login", query, "text/xml");

			logger.info(response);

			return res;

		} catch (Exception e) {
			logger.error("Exception when login on geonetwork: ",e);
			return null;
		}
	}

	public boolean logoutGeonetwork(HttpCallerUtil httpCaller) {

		try {
			if(httpCaller==null)
				return false;

			httpCaller.callPost("xml.user.logout", "<?xml version=\"1.0\" encoding=\"UTF-8\"?><requests/>", APPLICATION_XML);
			logger.info("Logout Geonetwork");
			return true;
		} catch (Exception e) {
			logger.error("Exception when logut on geonetwork: ", e);
			return false;
		}

	}

	public String errorPage(String title, String message){
		String errorPage = "";
	    errorPage +="<html>";
	    errorPage +="<head>";
	    errorPage +="<title>Error: "+title+"</title>";
	    errorPage +="</head>";
	    errorPage +="<body>";
	    errorPage +=message;
	    errorPage +="</body>";
	    errorPage +="</html>";
	    return errorPage;
	}

	protected String optimizeHtmlAndAddStylesheet(String html, String cssFileName) throws Exception {

	        DOMParser parser = new DOMParser();
	        parser.parse(new InputSource(new StringReader(html)));

	        Document doc = parser.getDocument();

//	    	System.out.println("FIRST CHILD IS: "+doc.getElementsByTagName("HTML").item(0).getNodeName());

			Node nodeHtml = doc.getElementsByTagName(HTML).item(0); //GET NODE HTML

			String cssFile = cssFileName;

			//CREATE STYLESHEET
			Element link = doc.createElement("link");
			link.setAttribute("type", "text/css");
			link.setAttribute("rel", "stylesheet");
			link.setAttribute("href", cssFile);

			nodeHtml.insertBefore(link, doc.getElementsByTagName(BODY).item(0)); //INSERT BEFOR OF TAG BODY

	        return printXmlDocument(doc);
	}

	protected Document addStylesheet(Document doc, String cssFileName) throws Exception {

//    	System.out.println("FIRST CHILD IS: "+doc.getElementsByTagName("HTML").item(0).getNodeName());

		Node nodeHtml = doc.getElementsByTagName(HTML).item(0); //GET NODE HTML

		String cssFile = cssFileName;

		//CREATE STYLESHEET
		Element link = doc.createElement("link");
		link.setAttribute("type", "text/css");
		link.setAttribute("rel", "stylesheet");
		link.setAttribute("href", cssFile);

		nodeHtml.insertBefore(link, doc.getElementsByTagName(BODY).item(0)); //INSERT BEFOR OF TAG BODY

        return doc;
}

	protected Document optimizeHtmlAndGetDom(String html) throws Exception {

        DOMParser parser = new DOMParser();
        parser.parse(new InputSource(new StringReader(html)));

        return parser.getDocument();
}

	public String domToString(Document doc) throws TransformerException{

		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");

		//initialize StreamResult with File object to save to file
		StreamResult result = new StreamResult(new StringWriter());
		DOMSource source = new DOMSource(doc);
		transformer.transform(source, result);

		return result.getWriter().toString();
	}


	public static String printXmlDocument(Document document) {
	    DOMImplementationLS domImplementationLS = (DOMImplementationLS) document.getImplementation();
	    LSSerializer lsSerializer = domImplementationLS.createLSSerializer();
	    return lsSerializer.writeToString(document);
	}

	public static String getMetadataShowEmbeddedQuery(String uuid, String currTab){

		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
						"<request><uuid>"+uuid+"</uuid>" +
						"<currTab>"+currTab+"</currTab>" +
						"</request>";
	}

	public static String getMetadataShowEmbeddedUrlMethod(){

		return GEONETWORK_SERVICE_URL+"/"+METADATA_SHOW_EMBEDDED;
	}

	public static void main(String[] args) throws Exception {

		String geonet = "http://geoserver-dev.d4science-ii.research-infrastructures.eu/geonetwork";

		EmbeddedGeonetworkMetadataISO19139View emd = new EmbeddedGeonetworkMetadataISO19139View();

//		HttpCallerUtil httpCaller = new HttpCallerUtil(geonet, "","");
//		emd.httpCallUtil = httpCaller;

		emd.loginGeonetwork(geonet, "admin", "admin");

		HashMap<String, String> parameters = new HashMap<String, String>();

		parameters.put("uuid", "83132484-c1ab-41b7-b0d2-8cf631129d37");
//		parameters.put("controls", "false");
		parameters.put("currTab", "simple");

//		String responseString = emd.httpCallUtil.callGet("srv/en/metadata.show.embedded?request=GetCapabilities&service=CSW&acceptVersions=2.0.2&acceptFormats=application/xml",null);

		String urlMethod = getMetadataShowEmbeddedUrlMethod();
		String query = getMetadataShowEmbeddedQuery("83132484-c1ab-41b7-b0d2-8cf631129d37", "simple");

//		String responseString = emd.httpCallUtil.callPost(urlMethod, query, APPLICATION_XML);


//		FileUtils.writeStringToFile(new File("testResponse.html"), responseString);

//		emd.logoutGeonetwork();
//		String responseString = emd.httpCallUtil.callGet("srv/en/metadata.show.embedded", parameters);

//		System.out.println(responseString);



//		Document responseDom = emd.optimizeHtmlAndGetDom(responseString);

//		String responseString = httpCaller.callGet("srv/en/metadata.show.embedded", parameters);

//		responseDom = emd.addStylesheet(responseDom, CSS_SOURCE_FILE);

//		System.out.println(printXmlDocument(responseDom));


//		FileUtils.writeStringToFile(new File("testResponse.html"), printXmlDocument(responseDom),"UTF-8");
	}


}