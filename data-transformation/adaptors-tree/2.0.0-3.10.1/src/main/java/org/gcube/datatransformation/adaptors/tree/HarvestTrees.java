package org.gcube.datatransformation.adaptors.tree;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.Writer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.TransformerException;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.datatransformation.adaptors.common.CustomMarshaller;
import org.gcube.datatransformation.adaptors.common.ElementGenerator;
import org.gcube.datatransformation.adaptors.common.constants.ConstantNames;
import org.gcube.datatransformation.adaptors.common.db.exceptions.SourceIDNotFoundException;
import org.gcube.datatransformation.adaptors.common.db.tools.SourcePropsTools;
import org.gcube.datatransformation.adaptors.common.db.xmlobjects.DBProps;
import org.gcube.datatransformation.adaptors.common.xmlobjects.TreeResource;
import org.gcube.datatransformation.adaptors.tree.impl.TreeResourceFactory;
import org.gcube.rest.commons.filter.IResourceFilter;
import org.gcube.rest.commons.resourceawareservice.constants.ResourceAwareServiceConstants;
import org.gcube.rest.commons.resourcefile.IResourceFileUtils;
import org.gcube.rest.resourceawareservice.ResourceAwareService;
import org.gcube.rest.resourceawareservice.exceptions.ResourceAwareServiceException;
import org.gcube.rest.resourcemanager.discoverer.Discoverer;
import org.gcube.rest.resourcemanager.publisher.ResourcePublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.google.inject.Singleton;

//import com.google.inject.Singleton;

@Path("/")
@Singleton
public class HarvestTrees extends ResourceAwareService<TreeResource>  {

	private static final long serialVersionUID = 1L;
	
	/** The logger. */
	private static final Logger logger = LoggerFactory.getLogger(HarvestTrees.class);
	
	
	private final Discoverer<TreeResource> configDiscoverer;
	private final TreeResourceFactory treeResourceFactory;
	
	private String scope;
	private String hostname;
	private String port;
	
	public HarvestTrees(TreeResourceFactory treeResourceFactory,
			ResourcePublisher<TreeResource> configPublisher,
			Discoverer<TreeResource> configDiscoverer,
			IResourceFilter<TreeResource> resourceFilter,
			IResourceFileUtils<TreeResource> resourceFileUtilsBinary,
			String hostname,
			String port,
			String scope
			)
			throws ResourceAwareServiceException {
		super(treeResourceFactory, configPublisher, resourceFilter, resourceFileUtilsBinary);
		
		this.treeResourceFactory = treeResourceFactory;
		this.configDiscoverer = configDiscoverer;
		this.scope = scope;
		this.hostname = hostname;
		this.port = port;
		
		logger.info("In HarvestTrees constructor");
	}
    
	@GET
	@Path(value = "/HarvestTreeCollection")
	@Produces(MediaType.TEXT_XML + "; " + "charset=UTF-8")
	public Response HarvestTreeCollection(
			@Context Request request,
			@HeaderParam(ResourceAwareServiceConstants.SCOPE_HEADER) final String scopeHeader,
			@QueryParam("treeCollectionID") final String treeCollectionID,
			@QueryParam("treeCollectionName") final String treeCollectionName
			) throws IOException, XMLStreamException{

		
		if((scopeHeader==null) || scopeHeader.isEmpty() || (treeCollectionID==null) || treeCollectionID.isEmpty())
			return Response.status(400).entity("Wrong treeCollectionID or scope parameter.").build(); 
		
//		this.loadResources();
		
//		System.out.println("Requested tree collection with ID: "+treeCollectionID);
		logger.debug("Requested tree collection with ID: "+treeCollectionID);

		StreamingOutput out = new StreamingOutput() {
            @Override
            public void write(OutputStream os) throws IOException, WebApplicationException {
                Writer writer = new BufferedWriter(new OutputStreamWriter(os));
                
        		TCollectionReader treeCollectionReader;
        		
        		if(treeCollectionName == null)
        			treeCollectionReader = new TCollectionReader(treeCollectionID, scopeHeader);
        		else
        			treeCollectionReader = new TCollectionReader(treeCollectionID, scopeHeader, treeCollectionName);
        		
                try {
					treeCollectionReader.readPrintCollections(writer);
				} catch (XMLStreamException e) {
					e.printStackTrace(new PrintStream(os));
				}     
            }
        };
		
        return Response.ok(out).build();

	}

	
	@GET
	@Path(value = "/AvailableResources")
	@Produces(MediaType.APPLICATION_XML + "; " + "charset=UTF-8")
	public Response AvailableResources() throws ParserConfigurationException, TransformerException{
		
//		this.loadResources();
		
		
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
 
		Element rootElement = doc.createElement("Resources");
		for(TreeResource treeResource : this.getAllResources()){ //should always be only one!
			Element resource = doc.createElement("resource");
			for(String treeID : treeResource.getTreeIDs()){
				Element id = doc.createElement("treeID");
				id.appendChild(doc.createTextNode(treeID));
				resource.appendChild(id);
			}
			rootElement.appendChild(resource);
		}
		return Response.status(Response.Status.OK).entity(ElementGenerator.domToXML(rootElement)).build();
	}
	
	
	@GET
	@Path(value = "/ResetResource")
	@Produces(MediaType.TEXT_PLAIN + "; " + "charset=UTF-8")
	public Response ResetResource(
			@HeaderParam(ResourceAwareServiceConstants.SCOPE_HEADER) String scopeHeader
			) throws SourceIDNotFoundException, Exception{
		
//		this.loadResources();
		
		for(TreeResource treeResource : this.getAllResources()){ //should be only one !
			this.destroyResource(treeResource.getResourceID());
			logger.debug("Deleted tree resource with ID: "+ treeResource.getResourceID());
		}
		
		String resourceID = this.createResource("");
		logger.debug("created resource with id: "+ resourceID);
		return Response.status(201).entity("Created successfully the resource").build();
		
	}
	
	
	
	
	@GET
	@Path(value = "/AddTree")
	@Produces(MediaType.TEXT_PLAIN + "; " + "charset=UTF-8")
	public Response AddTree(
			@HeaderParam(ResourceAwareServiceConstants.SCOPE_HEADER) String scopeHeader,
			@FormParam("treeID") final String treeID
			) throws SourceIDNotFoundException, Exception{
		if(treeID == null)
			return Response.status(400).entity("Parameter treeID is missing!").build();
		
		for(TreeResource treeResource : this.getAllResources()){ //should be only one !
			if(!treeResource.getTreeIDs().contains(treeID)){
				treeResource.addTreeID(treeID);
				this.saveResource(treeResource.getResourceID());
			}
		}
		
//		this.loadResources(); //this loads to the local repository
		
		return Response.status(200).build();
	}
	
	@GET
	@Path(value = "/RemoveTree")
	@Produces(MediaType.TEXT_PLAIN + "; " + "charset=UTF-8")
	public Response RemoveTree(
			@HeaderParam(ResourceAwareServiceConstants.SCOPE_HEADER) String scopeHeader,
			@FormParam("treeID") final String treeID
			) throws SourceIDNotFoundException, Exception{
		if(treeID == null)
			return Response.status(400).entity("Parameter treeID is missing!").build();
		
		for(TreeResource treeResource : this.getAllResources()){ //should be only one !
//			if(treeResource.getTreeIDs().contains(treeID))
				treeResource.removeTreeID(treeID);			
				this.saveResource(treeResource.getResourceID());
		}
		
		return Response.status(200).build();
	}
	
	
//	@GET
//	@Path(value = "/CreateNew")
//	@Produces(MediaType.TEXT_PLAIN + "; " + "charset=UTF-8")
//	public Response CreateDBHarvesterConfig(
//			@HeaderParam(ResourceAwareServiceConstants.SCOPE_HEADER) String scopeHeader
//			) throws SourceIDNotFoundException, Exception{
//		
//		
//		
////		for(TreeResource treeResource : this.getAllResources()) //should be only one !
////			if(treeResource.getTreeIDs().contains(treeID))
////				treeResource.removeTreeID(treeID);			
//		
//		return Response.status(200).build();
//	}
	
	
	
	@Override
	public String getResourceClass() {
		return ConstantNames.RESOURCE_CLASS;
	}

	@Override
	public String getResourceNamePref() {
		return ConstantNames.RESOURCE_NAME_PREF_TREE;
	}

	@Override
	public String getScope() {
		return scope;
	}


	
	
	
	
	
	
}
