package org.gcube.spatial.data.sdi.proxies;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.gcube.common.clients.Call;
import org.gcube.common.clients.delegates.ProxyDelegate;
import org.gcube.spatial.data.sdi.interfaces.Metadata;
import org.gcube.spatial.data.sdi.model.ServiceConstants;
import org.gcube.spatial.data.sdi.model.metadata.MetadataPublishOptions;
import org.gcube.spatial.data.sdi.model.metadata.MetadataReport;
import org.gcube.spatial.data.sdi.model.metadata.TemplateApplicationRequest;
import org.gcube.spatial.data.sdi.model.metadata.TemplateCollection;
import org.gcube.spatial.data.sdi.model.metadata.TemplateDescriptor;
import org.gcube.spatial.data.sdi.model.metadata.TemplateInvocation;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;

public class DefaultMetadata implements Metadata{

	private final ProxyDelegate<WebTarget> delegate;


	public DefaultMetadata(ProxyDelegate<WebTarget> config){
		this.delegate = config;
	}

	
	
	@Override
	public TemplateCollection getAvailableTemplates() {
		Call<WebTarget, Set<TemplateDescriptor>> call = new Call<WebTarget, Set<TemplateDescriptor>>() {
			@Override
			public Set<TemplateDescriptor> call(WebTarget templates) throws Exception {
				GenericType<Set<TemplateDescriptor>> generic=new GenericType<Set<TemplateDescriptor>>() {					
				};
				return templates.path(ServiceConstants.Metadata.LIST_METHOD).request(MediaType.APPLICATION_JSON).get(generic);
			}
		};
		try {
			return new TemplateCollection(new HashSet<TemplateDescriptor>(delegate.make(call)));			
		}catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public MetadataReport pushMetadata(File toPublish) {
		return pushMetadata(toPublish, new MetadataPublishOptions());
	}

	@Override
	public MetadataReport pushMetadata(final File toPublish, final MetadataPublishOptions options) {
		// upload Meta

		Call<WebTarget,MetadataReport> applyTemplatesCall=null; //needs uploaded id
		Call<WebTarget,MetadataReport> publishCall=null; 		//needs uploaded id
		
		try{
			Call<WebTarget,String> uploadCall=new Call<WebTarget, String>() {

				@Override
				public String call(WebTarget endpoint) throws Exception {
					endpoint.register(MultiPartFeature.class);
					FormDataMultiPart multi=new FormDataMultiPart();
					FileDataBodyPart fileDataBodyPart = new FileDataBodyPart(ServiceConstants.Metadata.UPLOADED_FILE_PARAMETER,
							toPublish,MediaType.APPLICATION_OCTET_STREAM_TYPE);
					multi.bodyPart(fileDataBodyPart);
					Response resp= endpoint.request().post(Entity.entity(multi, multi.getMediaType()));
					checkResponse(resp);
					return resp.readEntity(String.class);
				}
			};


			final String id=delegate.make(uploadCall);

			applyTemplatesCall=new Call<WebTarget, MetadataReport>() {
				@Override
				public MetadataReport call(WebTarget endpoint) throws Exception {
					
					
					Response resp= endpoint.path(id).
							request(MediaType.APPLICATION_JSON).put(Entity.entity(								
									new HashSet<TemplateInvocation>(options.getTemplateInvocations()),MediaType.APPLICATION_JSON));
					checkResponse(resp);
					return resp.readEntity(MetadataReport.class);
				}
			};
			
			publishCall=new Call<WebTarget,MetadataReport>(){
				@Override
				public MetadataReport call(WebTarget endpoint) throws Exception {
					Response resp= endpoint.path(ServiceConstants.Metadata.PUBLISH_METHOD).path(id).path(options.getGeonetworkCategory()).
							queryParam(ServiceConstants.Metadata.VALIDATE_PARAMETER, options.isValidate()).
							queryParam(ServiceConstants.Metadata.PUBLIC_PARAMETER, options.isMakePublic()).
							queryParam(ServiceConstants.Metadata.STYLESHEET_PARAMETER, options.getGeonetworkStyleSheet()).
							request(MediaType.APPLICATION_JSON).get();
					checkResponse(resp);
					return resp.readEntity(MetadataReport.class);
				}
			};			
		}catch(Throwable t){
			throw new RuntimeException("Unable to upload file.",t);
		}


		//APPLY TEMPLATES
		MetadataReport templateReport =null;
		try{
			if(!options.getTemplateInvocations().isEmpty())
				templateReport=delegate.make(applyTemplatesCall);
		}catch(Throwable t){
			throw new RuntimeException("Unable to apply templates",t);
		}

		//PUBLISH

		
		try{
			MetadataReport publishReport=delegate.make(publishCall);
			if(templateReport!=null) publishReport.setAppliedTemplates(templateReport.getAppliedTemplates());
			return publishReport;
		}catch(Throwable t){
			throw new RuntimeException("Unable to publish metadata. ",t);
		}

	}

	
	protected void checkResponse(Response toCheck) throws Exception{
		switch(toCheck.getStatusInfo().getFamily()){		
		case SUCCESSFUL : break;
		default : throw new Exception("Unexpected Response code : "+toCheck.getStatus(),new Exception(toCheck.readEntity(String.class)));
		}
	}
}
