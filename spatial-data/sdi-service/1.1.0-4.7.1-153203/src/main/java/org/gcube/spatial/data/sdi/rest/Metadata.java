package org.gcube.spatial.data.sdi.rest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.gcube.smartgears.annotations.ManagedBy;
import org.gcube.spatial.data.geonetwork.GeoNetworkPublisher;
import org.gcube.spatial.data.geonetwork.LoginLevel;
import org.gcube.spatial.data.geonetwork.utils.ScopeUtils;
import org.gcube.spatial.data.sdi.SDIServiceManager;
import org.gcube.spatial.data.sdi.engine.GeoNetworkProvider;
import org.gcube.spatial.data.sdi.engine.MetadataTemplateManager;
import org.gcube.spatial.data.sdi.engine.TemporaryPersistence;
import org.gcube.spatial.data.sdi.engine.impl.metadata.MetadataHandler;
import org.gcube.spatial.data.sdi.engine.impl.metadata.TemplateApplicationReport;
import org.gcube.spatial.data.sdi.model.ServiceConstants;
import org.gcube.spatial.data.sdi.model.metadata.MetadataReport;
import org.gcube.spatial.data.sdi.model.metadata.TemplateCollection;
import org.gcube.spatial.data.sdi.model.metadata.TemplateDescriptor;
import org.gcube.spatial.data.sdi.model.metadata.TemplateInvocation;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import it.geosolutions.geonetwork.util.GNInsertConfiguration;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Path(ServiceConstants.Metadata.INTERFACE)
@ManagedBy(SDIServiceManager.class)
public class Metadata {

	@Inject
	MetadataTemplateManager templateManager;
	@Inject
	GeoNetworkProvider geonetwork;

	@Inject
	TemporaryPersistence persistence;

	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public String uploadMetadata(@FormDataParam(ServiceConstants.Metadata.UPLOADED_FILE_PARAMETER) InputStream uploadedMeta,
			@FormDataParam(ServiceConstants.Metadata.UPLOADED_FILE_PARAMETER) FormDataContentDisposition uploadedMetaDetails){
		try {
			log.debug("Receiving metadata upload... size {} ",uploadedMetaDetails.getSize());
			return persistence.store(uploadedMeta);
		} catch (IOException e) {
			log.error("Unable to store file. ",e);
			throw new WebApplicationException("Unable to store file locally. Cause : "+e.getMessage(),Status.INTERNAL_SERVER_ERROR);
		}		
	}

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{uploadedId}")
	public MetadataReport applyTemplates(Collection<TemplateInvocation> templateInvocations, @PathParam("uploadedId") String uploadedId){
		log.debug("Checking uploaded id {} ",uploadedId);
		File uploaded=null;
		try {
			uploaded=persistence.getById(uploadedId);
		} catch (FileNotFoundException e) {
			log.debug("Unable to ge uploaded with ID {}. Cause : ",uploadedId,e);
			throw new WebApplicationException("Invalid upload id "+uploadedId);
		}
		MetadataReport toReturn=new MetadataReport();
		Set<TemplateInvocation> metadataEnrichments=new HashSet<>(templateInvocations);
		
		if(metadataEnrichments!=null && !metadataEnrichments.isEmpty()){
			try{
				log.debug("Applying invocations...");
				TemplateApplicationReport report=templateManager.applyTemplates(uploaded, metadataEnrichments);
				toReturn.setAppliedTemplates(report.getAppliedTemplates());
				persistence.update(uploadedId, new FileInputStream(new File(report.getGeneratedFilePath())));				
			}catch(Throwable e){
				log.debug("Unable to apply templates. ",e);
				throw new WebApplicationException("Unable to apply templates. Cause : "+e.getMessage(),Status.INTERNAL_SERVER_ERROR);
			}
		}
		return toReturn;
	}


	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/publish/{uploadedId}/{gnCategory}")
	public MetadataReport publishMetadata(@QueryParam(ServiceConstants.Metadata.VALIDATE_PARAMETER) @DefaultValue("true") Boolean validate,
			@QueryParam(ServiceConstants.Metadata.PUBLIC_PARAMETER) @DefaultValue("false") Boolean makePublic,
			@QueryParam(ServiceConstants.Metadata.STYLESHEET_PARAMETER) @DefaultValue("_none_") String styleSheet,
			@PathParam("gnCategory") String category, @PathParam("uploadedId") String uploadedId){
		try{
			log.info("PUBLISHING METADATA. UPLOADED ID {}. Scope is {} ",uploadedId,ScopeUtils.getCurrentScope());
			MetadataReport toReturn=new MetadataReport();
			File toPublish=persistence.getById(uploadedId);
			log.debug("Publishing metadata.. ");
			GeoNetworkPublisher publisher=geonetwork.getGeoNetwork();
			publisher.login(LoginLevel.DEFAULT);
			log.debug("Sending to {} ",publisher.getConfiguration());
			GNInsertConfiguration config=publisher.getCurrentUserConfiguration(category, styleSheet);
			config.setValidate(validate);
			long id = publisher.insertMetadata(config, toPublish);
			
			String uuid=new MetadataHandler(toPublish).getUUID();
			toReturn.setPublishedID(id);
			toReturn.setPublishedUUID(uuid);
			return toReturn;
		} catch (FileNotFoundException e) {
			log.debug("Unable to ge uploaded with ID {}. Cause : ",uploadedId,e);
			throw new WebApplicationException("Invalid upload id "+uploadedId);		
		}catch(Throwable e ){
			log.debug("Unexpected error while publishing {} . Cause : ",uploadedId,e);
			throw new WebApplicationException("Unabel to publish metadata. Cause "+e.getMessage(),Status.INTERNAL_SERVER_ERROR);
		}
	}



	@GET
	@Path("/list")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<TemplateDescriptor> getList(){
		log.debug("Received LIST method");
		TemplateCollection coll= templateManager.getAvailableTemplates();
		log.debug("Gonna respond with {} ",coll);
		return coll.getAvailableTemplates();
	}





}
