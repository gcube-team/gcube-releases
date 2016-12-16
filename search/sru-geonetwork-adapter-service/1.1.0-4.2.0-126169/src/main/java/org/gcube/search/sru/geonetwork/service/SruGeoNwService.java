package org.gcube.search.sru.geonetwork.service;

import java.io.IOException;
import java.io.StringWriter;

import it.geosolutions.geonetwork.GNClient;
import it.geosolutions.geonetwork.exception.GNLibException;
import it.geosolutions.geonetwork.exception.GNServerException;
import it.geosolutions.geonetwork.util.GNSearchRequest;
import it.geosolutions.geonetwork.util.GNSearchResponse;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.gcube.rest.commons.filter.IResourceFilter;
import org.gcube.rest.commons.resourceawareservice.constants.ResourceAwareServiceConstants;
import org.gcube.rest.commons.resourceawareservice.resources.exceptions.StatefulResourceException;
import org.gcube.rest.commons.resourcefile.IResourceFileUtils;
import org.gcube.rest.resourceawareservice.ResourceAwareService;
import org.gcube.rest.resourceawareservice.exceptions.ResourceAwareServiceException;
import org.gcube.rest.resourcemanager.discoverer.Discoverer;
import org.gcube.rest.resourcemanager.publisher.ResourcePublisher;
import org.gcube.search.sru.geonetwork.commons.api.SruGeoNwResourceFactory;
import org.gcube.search.sru.geonetwork.commons.api.SruGeoNwServiceAPI;
import org.gcube.search.sru.geonetwork.commons.constants.Constants;
import org.gcube.search.sru.geonetwork.commons.resources.SruGeoNwResource;
import org.gcube.search.sru.geonetwork.service.exceptions.CqlException;
import org.gcube.search.sru.geonetwork.service.exceptions.GeonetworkAccessException;
import org.gcube.search.sru.geonetwork.service.exceptions.NotSupportedException;
import org.gcube.search.sru.geonetwork.service.parsers.CqlParser;
import org.gcube.search.sru.geonetwork.service.responses.Explain;
import org.gcube.search.sru.geonetwork.service.responses.SearchRetrieve;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

import com.google.inject.Singleton;

@Path("/")
@Singleton
public class SruGeoNwService extends ResourceAwareService<SruGeoNwResource> implements SruGeoNwServiceAPI {
	
//	private final Discoverer<SruGeoNwResource> dbPropsDiscoverer;
//	private final ResourceFactory dbPropsFactory;
//	private final ResourcePublisher<SruGeoNwResource> dbPropsPublisher;
	
	private String scope;
	private String hostname;
	private String port;
	
	private GNClient client;
	private String geonetworkUrl;
	private String username;
	private String password;
	
	private static final long serialVersionUID = 1L;
	
	private DocumentBuilder simpleDocBuilder;
	
	/** The logger. */
	private static final Logger logger = LoggerFactory.getLogger(SruGeoNwService.class);

	
	public SruGeoNwService(
			SruGeoNwResourceFactory geoNwResource,
			ResourcePublisher<SruGeoNwResource> geoNwResourcePublisher,
			Discoverer<SruGeoNwResource> geoNwResourceDiscoverer,
			IResourceFilter<SruGeoNwResource> resourceFilter,
			IResourceFileUtils<SruGeoNwResource> resourceFileUtils,
			String hostname,
			String port) throws ResourceAwareServiceException {
		super(geoNwResource, geoNwResourcePublisher, resourceFilter, resourceFileUtils);
		this.hostname = hostname;
		this.port = port;
		try {
			geoNwResource.createResource("", ""); //with null parameters, as it generates it from the default properties file. 
		} catch (StatefulResourceException e) {
			logger.debug("Could not create the geonetwork resources file from the default properties file. Should now create a resource manually");
		} 
	}
	
	
	private void setGNClient() throws GeonetworkAccessException{
		if(this.client!=null)
			return;
		for(SruGeoNwResource res: this.getAllResources()){
			if((res.getUrl()!=null) && (!res.getUrl().isEmpty())){
				logger.debug("Initializing client of geonetwork: "+res.getUrl());
				this.client = new GNClient(res.getUrl());
				this.geonetworkUrl = res.getUrl();
				this.username = res.getUsername();
				this.password = res.getPassword();
				boolean logged = client.login(res.getUsername(), res.getPassword());
				if(!logged)
					logger.debug("Could not log in. Will operate only on it's public resources.");
				break; //means that we connect at the first available.(this can be changed later on to connect to the one specified by id)
			}
		}
		try{
			simpleDocBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		}
		catch(ParserConfigurationException e){
			logger.error("Could not instantiate the document builder");
		}
	}

	private int getTotalNumOfRepoRecords() throws GeonetworkAccessException{
		GNSearchRequest searchRequest = new GNSearchRequest();
		try{
			if(client==null)
				logger.debug("Client is not initiated properly");
			GNSearchResponse searchResponse = client.search(searchRequest);
			if(searchResponse==null)
				logger.debug("Could not search Geonetwork for maximum records");
			return searchResponse.getCount();
		}catch(GNLibException | GNServerException ex){
			throw new GeonetworkAccessException("Could not connect to geonetwork to retrieve the max number of metadata records.");
		}
	}
	
	@GET
	@Path(value = "/printAllResources")
	@Produces(MediaType.APPLICATION_XML + "; " + "charset=UTF-8")
	public Response printAllResources(){
		StringBuilder sb = new StringBuilder(1000);
		sb.append("<resources>");
		for(SruGeoNwResource res : this.getAllResources()){
			sb.append("<resource>");
			sb.append("<id>"+res.getResourceID()+"</id>");
			sb.append("<url>"+res.getUrl()+"</url>");
			sb.append("<username>"+res.getUsername()+"</username>");
			sb.append("<password>"+res.getPassword()+"</password>");
			sb.append("</resource>");
		}
		sb.append("</resources>");
		return Response.status(Response.Status.OK).entity(sb.toString()).build();
	}
	
	
	@POST
	@Path(value = "/CreateResource")
	@Produces(MediaType.TEXT_PLAIN + "; " + "charset=UTF-8")
	public Response createResource(
			@HeaderParam(ResourceAwareServiceConstants.SCOPE_HEADER) String scopeHeader,
			@FormParam("resourceXML") String resourceXML,
			@FormParam("url") final String url,
			@FormParam("username") final String username,
			@FormParam("password") final String password
			) throws  Exception{

		if((resourceXML!=null)&&(!resourceXML.isEmpty())){
			String resourceID = this.createResource(resourceXML);
			logger.debug("created resource with id: "+ resourceID);
			return Response.status(Response.Status.OK).entity("Created successfully config file").build();			
		}
		else if((url!=null)&&(!url.isEmpty())){
			resourceXML = "<SruGeoNwResource><url>"+url+"</url><username>"+username+"</username><password>"+password+"</password></SruGeoNwResource>";
			String resourceID = this.createResource(resourceXML);
			logger.debug("created resource with id: "+ resourceID);
			return Response.status(Response.Status.OK).entity("Created successfully config file").build();	
		}
		else{
			return Response.status(Response.Status.BAD_REQUEST).entity("Should provide either a 'resourceXML' parameter or a {url,username,password} parameter set").build();
		}
		
	}
	
	
	@POST
	@Path(value = "/RemoveResource")
	@Produces(MediaType.TEXT_PLAIN + "; " + "charset=UTF-8")
	public Response removeResource(
			@HeaderParam(ResourceAwareServiceConstants.SCOPE_HEADER) String scopeHeader,
			@FormParam("id") final String id
			) throws  Exception{

		boolean removed = this.destroyResource(id);
//		this.removeResource(scopeHeader, id);
		if(removed){
			logger.debug("removed resource with id: "+ id);
			return Response.status(Response.Status.OK).entity("Successfully removed config file with id: "+id).build();
		}
		else{
			logger.debug("Could not remove resource with id: "+ id);
			return Response.status(Response.Status.NOT_MODIFIED).entity("Could not remove config file with id: "+id).build();
		}
			
		
	}
	
	
	
	@GET
	@Path(value = "/sru")
	@Produces(MediaType.APPLICATION_XML + "; " + "charset=UTF-8")
	public Response get( 
			@HeaderParam(ResourceAwareServiceConstants.SCOPE_HEADER) String scope,
			@QueryParam("resourceID") String resourceID,
			@QueryParam("operation") String operation,
			@QueryParam("version") Float version,
			@QueryParam("recordPacking") String recordPacking,
			@QueryParam("query") String query, 
			@QueryParam("maximumRecords") Integer maximumRecords, 
			@QueryParam("recordSchema") String recordSchema)
	{
		return post(scope, resourceID, operation, version, recordPacking, query, maximumRecords, recordSchema);
	}
	
	
	
	@POST
	@Path(value = "/sru")
	@Produces(MediaType.APPLICATION_XML + "; " + "charset=UTF-8")
	public Response post( 
			@HeaderParam(ResourceAwareServiceConstants.SCOPE_HEADER) String scope,
			@FormParam("resourceID") String resourceID,
			@FormParam("operation") String operation,
			@FormParam("version") Float version,
			@FormParam("recordPacking") String recordPacking,
			@FormParam("query") String query, 
			@FormParam("maximumRecords") Integer maximumRecords, 
			@FormParam("recordSchema") String recordSchema)
	{
		
		try{
			setGNClient();
		}catch(GeonetworkAccessException ex){
			logger.debug("",ex);
			//TODO: put here an erroneous response
		}
		if("searchRetrieve".equalsIgnoreCase(operation)){
			try {
				
				if((query==null)||(query.isEmpty()))
					return Response.status(Response.Status.BAD_REQUEST).entity("Parameter 'query' was also expected.").build();
				if(version==null)
					version = Float.valueOf("1.1");
				//connect to geonetwork and get data for the query
				GNClient client = new GNClient(geonetworkUrl);
				// Perform a login into GN
				boolean logged = client.login(username, password);
				if (!logged)
					logger.debug("Could not log in. Will operate on public resources.");
				CqlParser parser = new CqlParser();
			    GNSearchRequest searchRequest = parser.getRequestByCqlQuery(query);
			    // do the search!
			    GNSearchResponse searchResponse = client.search(searchRequest);
			    
			    logger.debug("Got "+searchResponse.getCount() +" results from geonetwork");
			    
			    //create response
			    SearchRetrieve sr = new SearchRetrieve(version, recordPacking, hostname, operation, "", "", String.valueOf(searchResponse.getCount()));
			    int numToProcess = searchResponse.getCount();
			    if((maximumRecords!=null)&&maximumRecords<searchResponse.getCount())
			    	numToProcess = maximumRecords;
			    
			    Document doc = simpleDocBuilder.newDocument();
			    
			    Element searchRetrieveResponse = doc.createElementNS("zs", "searchRetrieveResponse");
			    searchRetrieveResponse.setAttribute("xmlns:zs", sr.xmlnsZr);
			    Element v = doc.createElementNS("zs", "version");
			    v.appendChild(doc.createTextNode(String.valueOf(version)));
			    searchRetrieveResponse.appendChild(v);
			    Element totalnumofrecs = doc.createElementNS("zs", "numberOfRecords");
			    totalnumofrecs.appendChild(doc.createTextNode(String.valueOf(searchResponse.getCount())));
			    searchRetrieveResponse.appendChild(totalnumofrecs);
			    
			    Element records = doc.createElementNS("zs", "records");
			    
				for(int i=0;i<numToProcess;i++){
					Element record = doc.createElementNS("zs", "record");
					
					Element recSchema = doc.createElementNS("zs", "recordSchema");
					recSchema.appendChild(doc.createTextNode(sr.dcSchemaIdentifier));
					record.appendChild(recSchema);
					
					Element recPacking = doc.createElementNS("zs", "recordPacking");
					recPacking.appendChild(doc.createTextNode(sr.getRecordPacking()));
					record.appendChild(recPacking);
					
					Element recData = doc.createElementNS("zs", "recordData");
					
					//should be noted that the client.get(searchResponse.getMetadata(i).getId()) makes a call on the geonetwork to get the actual data (bad design of client)
					Element rec = sr.transformRecord(client.get(searchResponse.getMetadata(i).getId()), simpleDocBuilder);
				    Node imported = recData.getOwnerDocument().importNode(rec, true);
				    recData.appendChild(imported);
					record.appendChild(recData);
					
					Element recPos = doc.createElementNS("zs", "recordPosition");
					recPos.appendChild(doc.createTextNode(String.valueOf(i+1)));
					record.appendChild(recPos);
					
					records.appendChild(record);
				}
				searchRetrieveResponse.appendChild(records);
				//write response to the output
				LSSerializer serializer =  ((DOMImplementationLS)searchRetrieveResponse.getOwnerDocument().getImplementation()).createLSSerializer();
				serializer.getDomConfig().setParameter("xml-declaration", false); //by default its true, so set it to false to get String without xml-declaration
				return Response.status(Response.Status.OK).entity(serializer.writeToString(searchRetrieveResponse)).build();
			} catch (NotSupportedException | CqlException | GNLibException | IOException | GNServerException e) {
				e.printStackTrace();
			}
			
		}
		
		//(Float version, String recordPacking, String host, String port,String basePath, String sruName, int numOfRecords)
		else if("explain".equalsIgnoreCase(operation)||operation==null){ //case of "explain"
			//connect to geonetwork and get data for the query
			client = new GNClient(geonetworkUrl);
			GNSearchRequest searchRequest = new GNSearchRequest();
			// set not searching at other bridged geonetworks 
		    searchRequest.addConfig(GNSearchRequest.Config.remote, "off");
			Explain explain;
			try{
				explain = new Explain(version, recordPacking, hostname, port, "/", "", getTotalNumOfRepoRecords());
				return Response.status(Response.Status.OK).entity(explain.getExplainResponse()).build();
			}catch (NotSupportedException | GeonetworkAccessException ex){
				logger.debug("",ex);
			}
		}
		
		return Response.status(Response.Status.NO_CONTENT).entity("Call ended without returning any content").build();
	}

	@Override
	public String getResourceClass() {
		return Constants.RESOURCE_CLASS;
	}

	@Override
	public String getResourceNamePref() {
		return Constants.RESOURCE_NAME_PREF;
	}

	@Override
	public String getScope() {
		return scope;
	}
	
	public void setScope(String scope) {
		this.scope = scope;
	}
	
}