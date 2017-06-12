package org.gcube.informationsystem.exporter.mapper;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.Profile;
import org.gcube.common.resources.gcore.ServiceEndpoint.Property;
import org.gcube.common.resources.gcore.ServiceEndpoint.Runtime;
import org.gcube.common.resources.gcore.common.Platform;
import org.gcube.common.resources.gcore.utils.Group;
import org.gcube.informationsystem.impl.embedded.HeaderImpl;
import org.gcube.informationsystem.impl.embedded.ValueSchemaImpl;
import org.gcube.informationsystem.impl.entity.facet.AccessPointFacetImpl;
import org.gcube.informationsystem.impl.entity.facet.NetworkingFacetImpl;
import org.gcube.informationsystem.impl.entity.facet.ServiceStateFacetImpl;
import org.gcube.informationsystem.impl.entity.facet.SoftwareFacetImpl;
import org.gcube.informationsystem.impl.entity.resource.EServiceImpl;
import org.gcube.informationsystem.impl.relation.ConsistsOfImpl;
import org.gcube.informationsystem.impl.relation.IsIdentifiedByImpl;
import org.gcube.informationsystem.model.embedded.Header;
import org.gcube.informationsystem.model.embedded.ValueSchema;
import org.gcube.informationsystem.model.entity.Facet;
import org.gcube.informationsystem.model.entity.Resource;
import org.gcube.informationsystem.model.entity.facet.AccessPointFacet;
import org.gcube.informationsystem.model.entity.facet.NetworkingFacet;
import org.gcube.informationsystem.model.entity.facet.ServiceStateFacet;
import org.gcube.informationsystem.model.entity.facet.SoftwareFacet;
import org.gcube.informationsystem.model.entity.resource.EService;
import org.gcube.informationsystem.model.relation.ConsistsOf;
import org.gcube.informationsystem.model.relation.IsIdentifiedBy;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.resource.ResourceAvailableInAnotherContextException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.resource.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceEndpointExporter extends GCoreResourceMapper<ServiceEndpoint, EService> {
	
	private static Logger logger = LoggerFactory.getLogger(ServiceEndpointExporter.class);
	
	public static final String FIXED_VERSION  = "1.0.0";
	
	public static final String PLATFORM = "PLATFORM";
	public static final String POSITION = "POSITION";
	
	public static final String GHN_ID = "ghnID";
	
	public static final String USERNAME = "USERNAME";
	public static final String PASSWORD = "PASSWORD";
	public static final String USERNAME_PASSWORD_SCHEMA_STRING;
	//public static final URI USERNAME_PASSWORD_SCHEMA;
	
	public static final String NAME = "NAME";
	public static final String ENCRYPTED = "ENCRYPTED";
	public static final String VALUE = "VALUE";
	public static final String PROPERTY_SCHEMA_STRING;
	public static final URI PROPERTY_SCHEMA;
	
	public static final String NOT_URI_ENDPOINT = "NOT_URI_ENDPOINT";
	
	static {
		USERNAME_PASSWORD_SCHEMA_STRING = String.format("%s:%s", USERNAME, PASSWORD);
		//USERNAME_PASSWORD_SCHEMA = URI.create(USERNAME_PASSWORD_SCHEMA_STRING);
		PROPERTY_SCHEMA_STRING = String.format("%s:%s:%s", NAME, ENCRYPTED, VALUE);
		PROPERTY_SCHEMA = URI.create(PROPERTY_SCHEMA_STRING);
	}
	
	public ServiceEndpointExporter(boolean filteredReport){
		super(ServiceEndpoint.class, EService.class, filteredReport);
	}
	
	@Override
	protected EService map(ServiceEndpoint gr) throws Exception  {
		Profile profile = gr.profile();
		UUID uuid = UUID.fromString(gr.id());
		boolean readFromIS = false;
		
		EService eService = null;
		AccessPointFacet[] accessPointFacets = null;
		SoftwareFacet softwareFacet = null;
		SoftwareFacet platformSoftwareFacet = null;
		ServiceStateFacet serviceStateFacet = null;
		NetworkingFacet networkingFacet = null;
		
		try {
			resourceRegistryClient.exists(rClass, uuid);
			readFromIS = true;
		}catch (ResourceNotFoundException e) {
			readFromIS = false;
		}catch (ResourceAvailableInAnotherContextException e) {
			resourceRegistryPublisher.addResourceToContext(uuid);
			Thread.sleep(100);
			readFromIS = true;
		}
		
		Group<AccessPoint> accessPoints = profile.accessPoints();
		accessPointFacets = new AccessPointFacet[accessPoints.size()];
		
		if(readFromIS){
			eService = read(uuid);
			//softwareFacet = (SoftwareFacet) eService.getIdentificationFacets().get(0);
			
			List<ConsistsOf<? extends Resource, ? extends Facet>> consistsOfs = eService.getConsistsOf();
			
			for(ConsistsOf<? extends Resource, ? extends Facet> c : consistsOfs){
				Facet target = c.getTarget();
				if(c instanceof IsIdentifiedBy){
					if(target instanceof SoftwareFacet) {
						softwareFacet = (SoftwareFacet) target;
						continue;
					}
				} else {
					if(target instanceof AccessPointFacet){
						try {
							Object positionObject = c.getAdditionalProperty(POSITION);
							Integer position = Integer.valueOf(positionObject.toString());
							if(position!=null){
								accessPointFacets[position] = (AccessPointFacet) target;
							}
						}catch (Exception e) {
							// Position is used on relation to match the AccessPoint on ServiceEndpoint
							logger.error("No POSITION found", e);
						}
						continue;
					}
					
					if(target instanceof SoftwareFacet){
						SoftwareFacet targetSoftwareFacet = (SoftwareFacet) target;
						if(targetSoftwareFacet.getGroup().compareTo(PLATFORM)==0){
							platformSoftwareFacet = targetSoftwareFacet;
						}
						continue;
					}
					
					if(target instanceof ServiceStateFacet){
						serviceStateFacet = (ServiceStateFacet) target;
						continue;
					}
					
					if(target instanceof NetworkingFacet){
						networkingFacet = (NetworkingFacet) target;
						continue;
					}
					
				}

			}
		}else{
			eService = new EServiceImpl();
			Header header = new HeaderImpl(uuid);
			eService.setHeader(header);
		}
		
		/* ----------------------------------------- */
		if(softwareFacet==null){
			softwareFacet = new SoftwareFacetImpl();
			IsIdentifiedBy<EService, SoftwareFacet> identifiedBy = 
					new IsIdentifiedByImpl<EService, SoftwareFacet>(eService, softwareFacet, null);
			eService.addFacet(identifiedBy);
		}
		
		softwareFacet.setGroup(profile.category());
		softwareFacet.setName(profile.name());
		softwareFacet.setVersion(FIXED_VERSION);
		String description = profile.description();
		if(description!=null && description.compareTo("")!=0){
			softwareFacet.setDescription(getStringAsUTF8(description));
		}
		/* ----------------------------------------- */
		
		
		/* ----------------------------------------- */
		Platform platform = profile.platform();

		if(platformSoftwareFacet==null){
			platformSoftwareFacet = new SoftwareFacetImpl();
			eService.addFacet(platformSoftwareFacet);
		}
		
		platformSoftwareFacet.setGroup(PLATFORM);
		platformSoftwareFacet.setName(platform.name());
		String platformVersion = String.format("%d.%d.%d-%d", 
				platform.version(), platform.minorVersion(), 
				platform.revisionVersion(), platform.buildVersion());
		softwareFacet.setVersion(platformVersion);
		/* ----------------------------------------- */
		
		
		/* ----------------------------------------- */
		Runtime runTime = profile.runtime();
		
		if(serviceStateFacet==null){
			serviceStateFacet = new ServiceStateFacetImpl();
			eService.addFacet(serviceStateFacet);
		}
		serviceStateFacet.setValue(runTime.status());
		
		if(networkingFacet==null){
			networkingFacet = new NetworkingFacetImpl();
			eService.addFacet(networkingFacet);
		}
		networkingFacet.setHostName(runTime.hostedOn());
		String ghnID = runTime.ghnId();
		if(ghnID!=null && ghnID.compareTo("")!=0){
			networkingFacet.setAdditionalProperty(GHN_ID, ghnID);
		}
		
		/* ----------------------------------------- */
		
		
		/* ----------------------------------------- */
		int i=0;
		for(AccessPoint accessPoint : accessPoints){
			if(accessPointFacets[i] == null){
				accessPointFacets[i] = new AccessPointFacetImpl();
				ConsistsOf<EService, AccessPointFacet> consistsOf = new ConsistsOfImpl<EService, AccessPointFacet>(eService, accessPointFacets[i], null);
				consistsOf.setAdditionalProperty(POSITION, i);
				eService.addFacet(consistsOf);
			}
			
			accessPointFacets[i].setEntryName(accessPoint.name());
			String address = accessPoint.address();
			if(address!=null && address.compareTo("")!=0){
				try {
					URI uri = URI.create(address);
					accessPointFacets[i].setEndpoint(uri);
				}catch (IllegalArgumentException e) {
					accessPointFacets[i].setAdditionalProperty(NOT_URI_ENDPOINT, address);
				}
			}
			
			String accessPointDescription = accessPoint.description();
			if(accessPointDescription!=null && accessPointDescription.compareTo("")!=0){
				accessPointFacets[i].setDescription(getStringAsUTF8(accessPointDescription));
			}
			
			/* ---------- */ 
			ValueSchema authorization = new ValueSchemaImpl();
			
			String value = USERNAME_PASSWORD_SCHEMA_STRING;
			String schema = USERNAME_PASSWORD_SCHEMA_STRING;
			
			boolean replaceColon = false;
			
			try {
				value = value.replace(USERNAME, accessPoint.username());
			}catch (NullPointerException e) {
				value = value.replace(USERNAME, "");
				schema  = schema.replace(USERNAME, "");
				replaceColon = true;
			}
			
			try {
				value = value.replace(PASSWORD, accessPoint.password());
			}catch (NullPointerException e) {
				value = value.replace(PASSWORD, "");
				schema  = schema.replace(PASSWORD, "");
				replaceColon = true;
			}
			
			if(replaceColon){
				value = value.replace(":", "");
				schema  = schema.replace(":", "");
			}
			
			if(value.compareTo("")!=0){
				authorization.setValue(value);
				authorization.setSchema(URI.create(schema));
					
				accessPointFacets[i].setAuthorization(authorization);
			}
			/* ----- */
			
					
			List<ValueSchema> properties = new ArrayList<>();
			
			for(Property property : accessPoint.properties()){
				ValueSchema valueSchema = new ValueSchemaImpl();
				String propertyValue = PROPERTY_SCHEMA.toString();
				propertyValue = propertyValue.replace(NAME, property.name());
				propertyValue = propertyValue.replace(ENCRYPTED, Boolean.toString(property.isEncrypted()));
				propertyValue = propertyValue.replace(VALUE, property.value());
				
				valueSchema.setValue(propertyValue);
				valueSchema.setSchema(PROPERTY_SCHEMA);
				
				properties.add(valueSchema);
				
			}
			accessPointFacets[i].setProperties(properties);
			
			i++;
		}
		/* ----------------------------------------- */
		
		
		return eService; 
	}

}
