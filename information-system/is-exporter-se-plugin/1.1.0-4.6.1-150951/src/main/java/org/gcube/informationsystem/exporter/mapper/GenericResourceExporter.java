package org.gcube.informationsystem.exporter.mapper;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.gcube.common.resources.gcore.GenericResource;
import org.gcube.common.resources.gcore.GenericResource.Profile;
import org.gcube.informationsystem.impl.embedded.HeaderImpl;
import org.gcube.informationsystem.impl.entity.facet.SimpleFacetImpl;
import org.gcube.informationsystem.impl.entity.facet.SoftwareFacetImpl;
import org.gcube.informationsystem.impl.entity.resource.ConfigurationImpl;
import org.gcube.informationsystem.impl.relation.IsIdentifiedByImpl;
import org.gcube.informationsystem.model.embedded.Header;
import org.gcube.informationsystem.model.entity.Facet;
import org.gcube.informationsystem.model.entity.Resource;
import org.gcube.informationsystem.model.entity.facet.SimpleFacet;
import org.gcube.informationsystem.model.entity.facet.SoftwareFacet;
import org.gcube.informationsystem.model.entity.resource.Configuration;
import org.gcube.informationsystem.model.relation.ConsistsOf;
import org.gcube.informationsystem.model.relation.IsIdentifiedBy;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.resource.ResourceAvailableInAnotherContextException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.resource.ResourceNotFoundException;
import org.json.JSONObject;
import org.json.XML;

public class GenericResourceExporter extends GCoreResourceMapper<GenericResource, Configuration> {
	
	public static final String FIXED_VERSION  = "1.0.0";
	public static final String FULL_BODY  = "FULL_BODY";
	
	public GenericResourceExporter(Boolean filteredReport){
		super(GenericResource.class, Configuration.class, filteredReport);
	}
	
	@Override
	protected Configuration map(GenericResource gr) throws Exception {
		Profile profile = gr.profile();
		UUID uuid = UUID.fromString(gr.id());
		boolean readFromIS = false;
		
		Configuration configuration = null;
		SoftwareFacet softwareFacet = null;
		SimpleFacet simpleFacet = null;
		
		try {
			resourceRegistryClient.exists(rClass, uuid);
			readFromIS = true;
		}catch (ResourceNotFoundException e) {
			readFromIS = false;
		} catch (ResourceAvailableInAnotherContextException e) {
			resourceRegistryPublisher.addResourceToContext(uuid);
			Thread.sleep(100);
			readFromIS = true;
		}
		
		
		if(readFromIS){
			configuration = read(uuid);
			//softwareFacet = (SoftwareFacet) configuration.getIdentificationFacets().get(0);
			
			List<ConsistsOf<? extends Resource, ? extends Facet>> consistsOfs = configuration.getConsistsOf();
			
			for(ConsistsOf<? extends Resource, ? extends Facet> c : consistsOfs){
				Facet target = c.getTarget();
				if(c instanceof IsIdentifiedBy){
					if(target instanceof SoftwareFacet) {
						softwareFacet = (SoftwareFacet) target;
						continue;
					}
				}
				
				if(c instanceof ConsistsOf){
					if(target instanceof SimpleFacet){
						simpleFacet = (SimpleFacet) target;
						continue;
					}
				}
				
			}
		}else{
			configuration = new ConfigurationImpl();
			Header header = new HeaderImpl(uuid);
			configuration.setHeader(header);
		}

		/* ----------------------------------------- */
		if(softwareFacet==null){
			softwareFacet = new SoftwareFacetImpl();
			IsIdentifiedBy<Configuration, SoftwareFacet> identifiedBy = 
					new IsIdentifiedByImpl<Configuration, SoftwareFacet>(configuration, softwareFacet, null);
			configuration.addFacet(identifiedBy);
		}
		
		
		softwareFacet.setGroup(profile.type());
		softwareFacet.setName(profile.name());
		softwareFacet.setVersion(FIXED_VERSION);
		String description = profile.description();
		if(description!=null && description.compareTo("")!=0){
			softwareFacet.setDescription(getStringAsUTF8(description));
		}
		/* ----------------------------------------- */

		/* ----------------------------------------- */
		String xmlBody = profile.bodyAsString();
		JSONObject jsonBody = XML.toJSONObject(getStringAsUTF8(xmlBody));
		Map<String, Object> map = jsonBody.toMap();
		if(simpleFacet==null){
			simpleFacet = new SimpleFacetImpl();
			configuration.addFacet(simpleFacet);
			
			try {
				SimpleFacet testSimpleFacet = new SimpleFacetImpl();
				testSimpleFacet.setAdditionalProperties(map);
				SimpleFacet created = resourceRegistryPublisher.createFacet(SimpleFacet.class, testSimpleFacet);
				resourceRegistryPublisher.deleteFacet(created);
				simpleFacet.setAdditionalProperties(map);
			}catch (Exception e) {
				simpleFacet.setAdditionalProperty(FULL_BODY, getStringAsUTF8(xmlBody));
			}
		}else{
			if(simpleFacet.getAdditionalProperty(FULL_BODY)!=null){
				simpleFacet.setAdditionalProperty(FULL_BODY, getStringAsUTF8(xmlBody));
			}else{
				simpleFacet.setAdditionalProperties(map);
			}
		}
		/* ----------------------------------------- */
		
		return configuration;
	}
	
}
