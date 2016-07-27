package org.gcube.datatransformation.adaptors.db;

import java.util.List;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.gcube.datatransformation.adaptors.common.ElementGenerator;
import org.gcube.datatransformation.adaptors.common.HarversterDBServiceAPI;
import org.gcube.datatransformation.adaptors.common.constants.ConstantNames;
import org.gcube.datatransformation.adaptors.common.db.exceptions.SourceIDNotFoundException;
import org.gcube.datatransformation.adaptors.common.db.is.ISResources;
import org.gcube.datatransformation.adaptors.common.db.tools.SourcePropsTools;
import org.gcube.datatransformation.adaptors.common.db.xmlobjects.DBProps;
import org.gcube.datatransformation.adaptors.common.db.xmlobjects.DBSource;
import org.gcube.datatransformation.adaptors.db.resources.DBPropsFactory;
import org.gcube.datatransformation.adaptors.db.toolbox.GenericTools;
import org.gcube.rest.commons.filter.IResourceFilter;
import org.gcube.rest.commons.resourceawareservice.constants.ResourceAwareServiceConstants;
import org.gcube.rest.commons.resourceawareservice.resources.exceptions.StatefulResourceException;
import org.gcube.rest.commons.resourcefile.IResourceFileUtils;
import org.gcube.rest.resourceawareservice.ResourceAwareService;
import org.gcube.rest.resourceawareservice.exceptions.ResourceAwareServiceException;
import org.gcube.rest.resourcemanager.discoverer.Discoverer;
import org.gcube.rest.resourcemanager.discoverer.exceptions.DiscovererException;
import org.gcube.rest.resourcemanager.publisher.ResourcePublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.google.inject.Singleton;

@Path("/")
@Singleton
public class HarvestDB extends ResourceAwareService<DBProps> implements HarversterDBServiceAPI {
	private static final long serialVersionUID = 1L;
	
	/** The logger. */
	private static final Logger logger = LoggerFactory.getLogger(HarvestDB.class);
	
	
	private final Discoverer<DBProps> dbPropsDiscoverer;
	private final DBPropsFactory dbPropsFactory;
	private final ResourcePublisher<DBProps> dbPropsPublisher;
	
	private String scope;
	final String hostname;
	final String port;
	
	@Override
	public String getScope() {
		return scope;
	}
	
	public void setScope(String scope) {
		if (this.scope != null){
			throw new IllegalStateException("scope already set");
		}
		this.scope = scope;
	}
	
	
	public HarvestDB(
			DBPropsFactory dbPropsFactory,
			ResourcePublisher<DBProps> dbPropsPublisher,
			Discoverer<DBProps> dbPropsDiscoverer,
			IResourceFilter<DBProps> resourceFilter,
			IResourceFileUtils<DBProps> resourceFileUtils,
			String hostname,
			String port
			) throws ResourceAwareServiceException {
		super(dbPropsFactory, dbPropsPublisher, resourceFilter, resourceFileUtils);
		
		this.dbPropsFactory = dbPropsFactory;
		this.dbPropsPublisher = dbPropsPublisher;
		this.dbPropsDiscoverer = dbPropsDiscoverer;
		this.hostname = hostname; // TestPortProvider.getHost();
		this.port = port; // String.valueOf(TestPortProvider.getPort());
		logger.info("In HarvestDB constructor");
	}
	
	

	@Override
	public String getResourceClass() {
		return ConstantNames.RESOURCE_CLASS;
	}

	@Override
	public String getResourceNamePref() {
		return ConstantNames.RESOURCE_NAME_PREF_DB;
	}
	
	
	@GET
	@Path(value = "/AvailableResources")
	@Produces(MediaType.APPLICATION_XML + "; " + "charset=UTF-8")
	public Response AvailableResources() throws ParserConfigurationException, TransformerException{
		
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
 
//		System.out.println("getting available resources:");
		
		Element rootElement = doc.createElement("Resources");
		for(DBProps dbProps : this.getAllResources()){
			Element resource = doc.createElement("resource");
			Element resourceid = doc.createElement("resourceid");
			resourceid.appendChild(doc.createTextNode(dbProps.getResourceID()));
			Element sourcename = doc.createElement("sourcename");
			sourcename.appendChild(doc.createTextNode(dbProps.getSourceName()));
			Element propsname = doc.createElement("propsname");
			propsname.appendChild(doc.createTextNode(dbProps.getPropsName()));
			resource.appendChild(resourceid);
			resource.appendChild(sourcename);
			resource.appendChild(propsname);
			rootElement.appendChild(resource);
			
//			System.out.println("resource:");
//			System.out.println("resourceid: "+resourceid);
//			System.out.println("sourcename: "+sourcename);
//			System.out.println("propsname: "+propsname);
		}
		return Response.status(Response.Status.OK).entity(ElementGenerator.domToXML(rootElement)).build();
	}
	
	@GET
	@Path(value = "/GetResource")
	@Produces(MediaType.APPLICATION_XML + "; " + "charset=UTF-8")
	public Response GetResource(
			@HeaderParam(ResourceAwareServiceConstants.SCOPE_HEADER) String scopeHeader,
			@QueryParam("sourcename") String sourcename,
			@QueryParam("propsname") String propsname
			) throws SourceIDNotFoundException, Exception{
		
		for(DBProps dbProps : this.getAllResources()){
			if(dbProps.getSourceName().equals(sourcename) && dbProps.getPropsName().equals(propsname))
				return Response.status(Response.Status.OK).entity(SourcePropsTools.dbPropsToXML(dbProps)).build();
		}
		return Response.status(Response.Status.NO_CONTENT).entity("").build();
	}
	
	
	@POST
	@Path(value = "/DeleteAllDBHarvesterConfigs")
	@Produces(MediaType.TEXT_PLAIN + "; " + "charset=UTF-8")
	public Response DeleteAllDBHarvesterConfigs(
			@HeaderParam(ResourceAwareServiceConstants.SCOPE_HEADER) String scopeHeader
			) throws SourceIDNotFoundException, Exception{
		
		for(DBProps dbp : this.getAllResources()){
			this.destroyResource(dbp.getResourceID());
			logger.debug("Deleted resource with ID: "+ dbp.getResourceID() +" and name: "+dbp.getPropsName());
		}
		
		return Response.status(201).entity("Deleted all resources").build();
		
	}
	
	@POST
	@Path(value = "/ReplaceDBHarvesterConfig")
	@Produces(MediaType.TEXT_PLAIN + "; " + "charset=UTF-8")
	public Response ReplaceDBHarvesterConfig(
			@HeaderParam(ResourceAwareServiceConstants.SCOPE_HEADER) String scopeHeader,
			@FormParam("dbPropsXML") String dbPropsXML
			) throws SourceIDNotFoundException, Exception{
		
		DBProps dbProps = SourcePropsTools.parseSourceProps(dbPropsXML);
		RemoveDBHarvesterConfig(scopeHeader,dbProps.getPropsName());
		CreateDBHarvesterConfig(scopeHeader, dbPropsXML);
		return Response.status(201).entity("Loaded successfully config file").build();
	}
	
	@POST
	@Path(value = "/CreateDBHarvesterConfig")
	@Produces(MediaType.TEXT_PLAIN + "; " + "charset=UTF-8")
	public Response CreateDBHarvesterConfig(
			@HeaderParam(ResourceAwareServiceConstants.SCOPE_HEADER) String scopeHeader,
			@FormParam("dbPropsXML") final String dbPropsXML
			) throws SourceIDNotFoundException, Exception{
		if(dbPropsXML == null)
			return Response.status(400).entity("Parameter dbPropsXML is missing!").build();
		
		/*
		//create a new one below
		DBProps dbProps = dbPropsFactory.createResource(null, dbPropsXML);
		String validityStr = SourcePropsTools.isValid(dbProps);
		if(!validityStr.equals("valid"))
			return Response.status(500).entity(validityStr).build();
		//now publish it
		dbPropsPublisher.publishResource(dbProps, "HarvesterResource", "DB", scopeHeader);
		*/
		
		boolean exists = false;
		DBProps dbProps = SourcePropsTools.parseSourceProps(dbPropsXML);
		for(DBProps dbp : this.getAllResources()){
			if(dbp.getPropsName().equals(dbProps.getPropsName())){
				exists = true;
				logger.debug("Provided resource with propsname "+ dbProps.getPropsName()+" already exists. Will not create the resource.");
				return Response.status(200).entity("Provided resource with propsname "+ dbProps.getPropsName()+" already exists. Will not create the resource.").build();
			}
		}
		if(!exists){
			String resourceID = this.createResource(dbPropsXML);
			logger.debug("created resource with id: "+ resourceID);
			return Response.status(201).entity("Created successfully config file").build();
		}
		
		return Response.status(200).build();
	}
	
	@POST
	@Path(value = "/RemoveDBHarvesterConfig")
	@Produces(MediaType.TEXT_PLAIN + "; " + "charset=UTF-8")
	public Response RemoveDBHarvesterConfig(
			@HeaderParam(ResourceAwareServiceConstants.SCOPE_HEADER) String scopeHeader,
			@FormParam("propsname") String propsname
//			@FormParam("dbPropsXML") final String dbPropsXML
			) throws StatefulResourceException, DiscovererException{
//		this.scope = scopeHeader;
		if(propsname == null)
			return Response.status(400).entity("Parameter \"propsname\" is missing!").build();
//		DBProps dbProps = SourcePropsTools.parseSourceProps(dbPropsXML);
		for(DBProps dbProps : this.getAllResources())
			if(dbProps.getSourceType().equals(ConstantNames.DBSOURCETYPE) && dbProps.getPropsName().equals(propsname))
				this.destroyResource(dbProps.getResourceID());
		return Response.status(201).entity("Removed successfully config file").build();
	}
	
	
	@GET
	@Path(value = "/HarvestDatabase")
	@Produces(MediaType.TEXT_XML + "; " + "charset=UTF-8")
	public Response HarvestDatabase(
			@HeaderParam(ResourceAwareServiceConstants.SCOPE_HEADER) String scopeHeader,
			@QueryParam("sourcename") String sourcename,
			@QueryParam("propsname") String propsname,
			@QueryParam("recordid") String recordid
			) throws Exception{
		
//		this.scope = scopeHeader;
		
		//get the database connection credentials from the IS for the specified sourcename and scope 
		DBSource dbSource = ISResources.getDBSourceInfo(sourcename, scopeHeader);
		
		//add here code to parse the dbPropsXML and update the harvester binded resource
		//first delete all previous resources for this host
		
//		RIDiscovererISHelper.discoverRunningInstances(serviceName, serviceClass, endpointKey, scope)
//		Set <String> endpoints = new HashSet<String>();
//		endpoints.add("http://"+hostname+":"+port);
		
		DBProps dbProps = null;
//		Map<String, Set<DBProps>> resources = dbPropsDiscoverer.discoverResources(endpoints, DBProps.class, scopeHeader);
//		for(DBProps dbp : resources.values().toArray(new DBProps[0])){
//			logger.debug("Checking DB configuration: "+dbp.getSourceName());
//			if(dbp.getSourceName().equalsIgnoreCase(sourcename)){
//				dbProps = dbp;
//			}
//		}
		
		List <DBProps> resources = this.getAllResources();
		
		if(resources.size()==0){
			return Response.status(404).entity("Could not find any database configuration file stored for database named "+sourcename).build();
		}
		else if(resources.size()==1){ //if it's just one, use this
			dbProps = resources.get(0);
		}
		else if(resources.size()>1){ //if it's more than one, use the one with the provided name
			for(DBProps dbp : resources)
				if(dbp.getPropsName().equalsIgnoreCase(propsname))
					dbProps = dbp;
		}
			
		if(!GenericTools.mergeableProperties(dbSource, dbProps)){
			String response = "Sourcename property mismatch between db credentials file and db configuration file: "+dbSource.getSourceName()+" vs "+dbProps.getSourceName();
			return Response.status(500).entity(response).build();
		}
		String validityStr = SourcePropsTools.isValid(dbProps);
		if(!validityStr.equals("valid"))
			return Response.status(500).entity(validityStr).build();
		
		logger.debug("All properties seem to be finely set, fetching the results from the DB");
		
		DBDataStax dbDataStax = new DBDataStax(dbSource, dbProps, scopeHeader);
		if((recordid!=null)&&(!recordid.isEmpty())) //means that the caller has ordered a specific record
			dbDataStax = filterRootSqlBy(dbDataStax,recordid);
		//by now, if user has ordered a specific field, we have modified accordingly the sql of the root table
		StreamingOutput outputStream = dbDataStax.writeSourceData(hostname+":"+port);
		
		//close writer
//		response.getWriter().close();
		
        
        return Response.ok(outputStream).build();
	}
	
	

	
	/**
	 * Edit root table sql of dbDataStax to return only the recordid specified
	 */
	private DBDataStax filterRootSqlBy(DBDataStax dbDataStax, String recordid){
		for(int i=0;i<dbDataStax.getSourceProps().getTables().size();i++){
			if(dbDataStax.getSourceProps().getTables().get(i).getName().equalsIgnoreCase(dbDataStax.getRootTableName())){
				String tableName = dbDataStax.getSourceProps().getTables().get(i).getName();
				String sql = dbDataStax.getSourceProps().getTables().get(i).getSql();
				String [] keys = SourcePropsTools.getPKeyOfTable(dbDataStax.getSourceProps(), tableName).split(",");
				String [] keysVals = recordid.split(",");
				if(sql.toLowerCase().contains(" where ")){
					for(int k=0;k<keys.length;k++)
						sql += " and "+keys[k]+"='"+keysVals[k]+"'";
					dbDataStax.getSourceProps().getTables().get(i).setSql(sql);
				}
				else{
					sql += " where "+keys[0]+"='"+keysVals[0]+"'";
					for(int k=1;k<keys.length;k++)
						sql += " and "+keys[k]+"='"+keysVals[k]+"'";
					dbDataStax.getSourceProps().getTables().get(i).setSql(sql);
				}
			}
		}
		return dbDataStax;
	}



	
	
	

}
