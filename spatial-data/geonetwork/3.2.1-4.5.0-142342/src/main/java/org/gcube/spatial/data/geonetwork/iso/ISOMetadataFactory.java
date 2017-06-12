package org.gcube.spatial.data.geonetwork.iso;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.gcube.portlets.user.uriresolvermanager.UriResolverManager;
import org.gcube.portlets.user.uriresolvermanager.exception.IllegalArgumentException;
import org.gcube.portlets.user.uriresolvermanager.exception.UriResolverMapException;
import org.gcube.spatial.data.geonetwork.utils.ScopeUtils;
import org.geotoolkit.metadata.iso.DefaultIdentifier;
import org.geotoolkit.metadata.iso.DefaultMetadata;
import org.geotoolkit.metadata.iso.citation.DefaultAddress;
import org.geotoolkit.metadata.iso.citation.DefaultCitation;
import org.geotoolkit.metadata.iso.citation.DefaultCitationDate;
import org.geotoolkit.metadata.iso.citation.DefaultContact;
import org.geotoolkit.metadata.iso.citation.DefaultOnlineResource;
import org.geotoolkit.metadata.iso.citation.DefaultResponsibleParty;
import org.geotoolkit.metadata.iso.constraint.DefaultLegalConstraints;
import org.geotoolkit.metadata.iso.distribution.DefaultDigitalTransferOptions;
import org.geotoolkit.metadata.iso.distribution.DefaultDistribution;
import org.geotoolkit.metadata.iso.distribution.DefaultFormat;
import org.geotoolkit.metadata.iso.identification.DefaultBrowseGraphic;
import org.geotoolkit.metadata.iso.identification.DefaultDataIdentification;
import org.geotoolkit.metadata.iso.identification.DefaultKeywords;
import org.geotoolkit.metadata.iso.identification.DefaultResolution;
import org.geotoolkit.metadata.iso.maintenance.DefaultMaintenanceInformation;
import org.geotoolkit.metadata.iso.spatial.DefaultGeometricObjects;
import org.geotoolkit.metadata.iso.spatial.DefaultVectorSpatialRepresentation;
import org.geotoolkit.util.DefaultInternationalString;
import org.opengis.metadata.Metadata;
import org.opengis.metadata.citation.DateType;
import org.opengis.metadata.citation.Role;
import org.opengis.metadata.constraint.Restriction;
import org.opengis.metadata.identification.CharacterSet;
import org.opengis.metadata.maintenance.MaintenanceFrequency;
import org.opengis.metadata.maintenance.ScopeCode;

public class ISOMetadataFactory {


	public static Metadata generateMeta(GcubeISOMetadata gcubeMeta) throws URISyntaxException{
	
			String metadataIdentifier=UUID.randomUUID().toString();
		
		
		
		
			//*************** Responsible Party : author
			DefaultResponsibleParty author=new DefaultResponsibleParty();
			author.setIndividualName(gcubeMeta.getUser());
			author.setOrganisationName(new DefaultInternationalString(gcubeMeta.getConfig().getProjectName()));
			author.setRole(Role.AUTHOR);
	
			//*************** Responsible Party : provider -> iMarine
	
			DefaultResponsibleParty distributor=new DefaultResponsibleParty();
			distributor.setIndividualName(gcubeMeta.getConfig().getDistributorIndividualName());
			distributor.setOrganisationName(new DefaultInternationalString(gcubeMeta.getConfig().getProjectName()));
			distributor.setRole(Role.DISTRIBUTOR);
			DefaultContact distributorContact=new DefaultContact();
			DefaultAddress distributorAddress=new DefaultAddress();
			distributorAddress.getElectronicMailAddresses().add(gcubeMeta.getConfig().getDistributorEMail());			
			distributorContact.setAddress(distributorAddress);
			DefaultOnlineResource distributorOnline=new DefaultOnlineResource (new URI(gcubeMeta.getConfig().getDistributorSite()));
			distributorOnline.setName(gcubeMeta.getConfig().getProjectName()+" site.");
			distributorOnline.setProtocol(gcubeMeta.getConfig().getHttpProtocolDeclaration());
			distributorContact.setOnlineResource(distributorOnline);
			distributor.setContactInfo(distributorContact);
	
			DefaultResponsibleParty provider=new DefaultResponsibleParty();
			provider.setIndividualName(gcubeMeta.getConfig().getProviderIndividualName());
			provider.setOrganisationName(new DefaultInternationalString(gcubeMeta.getConfig().getProjectName()));
			provider.setRole(Role.RESOURCE_PROVIDER);
			DefaultContact providerContact=new DefaultContact();
			DefaultAddress providerAddress=new DefaultAddress();
			providerAddress.getElectronicMailAddresses().add(gcubeMeta.getConfig().getProviderEMail());			
			providerContact.setAddress(providerAddress);
			DefaultOnlineResource providerOnline=new DefaultOnlineResource (new URI(gcubeMeta.getConfig().getProviderSite()));
			providerOnline.setName(gcubeMeta.getConfig().getProjectName()+" site.");
			providerOnline.setProtocol(gcubeMeta.getConfig().getHttpProtocolDeclaration());
			providerContact.setOnlineResource(providerOnline);
			provider.setContactInfo(providerContact);
	
	
			//*************** Identification	
			DefaultDataIdentification ident=new DefaultDataIdentification();
			ident.getLanguages().add(Locale.ENGLISH);			
	
			DefaultCitation citation=new DefaultCitation();
			citation.setTitle(new DefaultInternationalString(gcubeMeta.getTitle()));		
	
			citation.getDates().add(new DefaultCitationDate(gcubeMeta.getCreationDate(), DateType.CREATION));
	
	
			citation.getPresentationForms().add(gcubeMeta.getPresentationForm());
			citation.setIdentifiers(Collections.singleton(new DefaultIdentifier(metadataIdentifier)));
	
			ident.setCitation(citation);
			ident.setAbstract(new DefaultInternationalString(gcubeMeta.getAbstractField()));
			ident.setPurpose(new DefaultInternationalString(gcubeMeta.getPurpose()));
			ident.getCredits().addAll(gcubeMeta.getCredits());
			
			
			
			
			ident.getResourceMaintenances().add(new DefaultMaintenanceInformation(MaintenanceFrequency.AS_NEEDED));
	
	
			for(Entry<Thesaurus,HashSet<String>> entry:gcubeMeta.getDescriptiveKeywords().entrySet()){
				DefaultKeywords keywords=new DefaultKeywords();
				for(String key:entry.getValue())keywords.getKeywords().add(new DefaultInternationalString(key));
				keywords.setType(entry.getKey().getType());
	
				DefaultCitation thesaurus=new DefaultCitation();
				thesaurus.setTitle(new DefaultInternationalString(entry.getKey().getTitle()));					
				thesaurus.getDates().add(new DefaultCitationDate(entry.getKey().getCitationDate(), DateType.CREATION));
				if(entry.getKey().isAuthored()){
					thesaurus.setOtherCitationDetails(new DefaultInternationalString(entry.getKey().getCitationDescription()));
					DefaultResponsibleParty thesaurusParty=new DefaultResponsibleParty();
					thesaurusParty.setIndividualName(entry.getKey().getTitle());
					thesaurusParty.setOrganisationName(new DefaultInternationalString(entry.getKey().getCitationOrganization()));
					thesaurusParty.setRole(Role.POINT_OF_CONTACT);
					DefaultContact thesaurusContact=new DefaultContact();
					thesaurusContact.setOnlineResource(getOnline(entry.getKey().getTitle(),entry.getKey().getCitationUri()));
					thesaurusParty.setContactInfo(thesaurusContact);
					thesaurus.getCitedResponsibleParties().add(thesaurusParty);
				}
				keywords.setThesaurusName(thesaurus);
				ident.getDescriptiveKeywords().add(keywords);
			}
	
	
	
			
			
	
			ident.getTopicCategories().addAll(gcubeMeta.getTopicCategories());
	
	
			ident.getExtents().add(gcubeMeta.getExtent());
	
			//Spatial Rapresentation Info
			DefaultGeometricObjects geoObjs=new DefaultGeometricObjects();
			geoObjs.setGeometricObjectType(gcubeMeta.getGeometricObjectType());
			geoObjs.setGeometricObjectCount(gcubeMeta.getGeometryCount());
	
			DefaultVectorSpatialRepresentation spatial=new DefaultVectorSpatialRepresentation();
			spatial.setTopologyLevel(gcubeMeta.getTopologyLevel());
			spatial.getGeometricObjects().add(geoObjs);

			
			
			DefaultResolution layerResolution=new DefaultResolution();
			layerResolution.setDistance(gcubeMeta.getResolution());
			ident.getSpatialResolutions().add(layerResolution);
			
			
	
			// DistributionInfo
			
			//TODO this part should be inserted by publishing logic
			

	
			for(String uri:gcubeMeta.getGraphicOverviewsURI()){
				DefaultBrowseGraphic graph=new DefaultBrowseGraphic(new URI(uri));				
				ident.getGraphicOverviews().add(graph);
			}
			//MetadataConstraints
	
			DefaultLegalConstraints constraints=new DefaultLegalConstraints();
			constraints.getUseLimitations().add(new DefaultInternationalString(gcubeMeta.getConfig().getLicense()));
			constraints.getAccessConstraints().add(Restriction.LICENSE);
			constraints.getUseConstraints().add(Restriction.LICENSE);		
			constraints.getOtherConstraints().add(new DefaultInternationalString("other restrictions"));
	

	
	
			//*************** The Meta Object
			DefaultMetadata meta=new DefaultMetadata(author, new Date(System.currentTimeMillis()), ident);
			meta.setCharacterSet(CharacterSet.UTF_8);
			meta.getContacts().add(distributor);
			meta.getContacts().add(provider);
			meta.getSpatialRepresentationInfo().add(spatial);
//			meta.setDistributionInfo(distribution);
			meta.getMetadataConstraints().add(constraints);
//			meta.getDataQualityInfo().add(processQuality);
			meta.setLanguage(Locale.ENGLISH);
			meta.getHierarchyLevels().add(ScopeCode.DATASET);
			meta.setFileIdentifier(metadataIdentifier);

			return meta;
		}

	
	
	public static DefaultDistribution getDistributionByLayer(String layerName, String geoserverUrl, String style, String bbox, EnvironmentConfiguration config) throws URISyntaxException{
		DefaultDistribution distribution=new DefaultDistribution();
		distribution.getTransferOptions().add(getTransferOptionsByLayer(layerName, geoserverUrl, style, bbox, config));
		
		DefaultFormat format1 = new DefaultFormat();
		format1.setName(new DefaultInternationalString(Protocol.WMS.getName()));
		format1.setVersion(new DefaultInternationalString(Protocol.WMS.getVersion()));			
		DefaultFormat format2 = new DefaultFormat();
		format2.setName(new DefaultInternationalString(Protocol.WFS.getName()));
		format2.setVersion(new DefaultInternationalString(Protocol.WFS.getVersion()));
		DefaultFormat format3 = new DefaultFormat();
		format3.setName(new DefaultInternationalString(Protocol.WCS.getName()));
		format3.setVersion(new DefaultInternationalString(Protocol.WCS.getVersion()));
		
		DefaultFormat format4 = new DefaultFormat();
		format4.setName(new DefaultInternationalString(Protocol.HTTP.getName()));
		format4.setVersion(new DefaultInternationalString(Protocol.HTTP.getVersion()));


		distribution.setDistributionFormats(new ArrayList<DefaultFormat>(Arrays.asList(format1, format2,format3, format4)));
		return distribution;
	}
	

	public static DefaultDigitalTransferOptions getTransferOptionsByLayer(String layerName, String geoserverUrl, String style, String bbox, EnvironmentConfiguration config ) throws URISyntaxException{
		DefaultDigitalTransferOptions transferOptions=new DefaultDigitalTransferOptions();			
		
//		transferOptions.getOnLines().add(getOnline(Protocol.WMS.getName()+" link to layer.",config.getWmsProtocolDeclaration(),StringEscapeUtils.escapeXml10(getWmsUrl(geoserverUrl, layerName, style, bbox, config.getDefaultCRS()))));
//		transferOptions.getOnLines().add(getOnline(Protocol.WFS.getName()+" link to layer.",config.getWfsProtocolDeclaration(),StringEscapeUtils.escapeXml10(getWfsUrl(geoserverUrl,layerName))));
//		transferOptions.getOnLines().add(getOnline(Protocol.WCS.getName()+" link to layer.",config.getWcsProtocolDeclaration(),StringEscapeUtils.escapeXml10(getWcsUrl(geoserverUrl,layerName,bbox))));
		
		
		
		
		return transferOptions;
	}
	
	public static DefaultOnlineResource getOnline(String name,String uriString) throws URISyntaxException{
		return getOnline(name,Protocol.getByURI(uriString).getDeclaration(),Protocol.getByURI(uriString).getName()+" link to resource.",uriString);
	}
	
	public static DefaultOnlineResource getOnline(String name,String protocolDeclaration,String uriString) throws URISyntaxException{
		return getOnline(name,protocolDeclaration,Protocol.getByURI(uriString).getName()+" link to resource.",uriString);
	}
	
	public static DefaultOnlineResource getOnline(String name,String protocol, String description, String uriString) throws URISyntaxException{
		URI uri=new URI(uriString);
		DefaultOnlineResource resource=new DefaultOnlineResource(uri);		
		resource.setName(name);
		resource.setProtocol(protocol);
		resource.setDescription(new DefaultInternationalString(description));
		return resource;
	}

	
	
	
	public static String getWmsUrl(String geoServerUrl, String layerName,String style, String bbox,String CRS) {		
		if(bbox==null) bbox=BoundingBox.WORLD_EXTENT.toString();
      return geoServerUrl + 
      		"/wms?service=wms&version=1.1.0" 
      		+ "&request=GetMap&layers=" + layerName 
      		+ "&styles=" + (style == null ? "" : style) 
      		+ "&bbox=" + bbox + "&width=676&height=330" +
      		"&srs=EPSG:4326&crs="+CRS+"&format=application/openlayers";
		
		
    }
	public static String getWfsUrl(String geoServerUrl, String layerName) {		
        return geoServerUrl + 
        		"/ows?service=wfs&version=1.0.0" 
        		+ "&request=GetFeature&typeName=" + layerName 
        		+"&format=json";
    }
	public static String getWcsUrl(String geoServerUrl, String layerName,String bbox) {
		if(bbox==null) bbox=BoundingBox.WORLD_EXTENT.toString();
		return geoServerUrl + "/wcs?service=wcs&version=1.0.0" + "&request=GetCoverage&coverage=" + 
				layerName + "&CRS=EPSG:4326" + "&bbox=" + bbox + "&width=676&height=330&format=geotiff"; 
    }
	

	public static String getGisLinkByUUID(String uuid) throws UriResolverMapException, IllegalArgumentException{
		Map<String,String> params=new HashMap<String,String>();
		params.put("scope", ScopeUtils.getCurrentScope());
		params.put("gis-UUID", uuid);
		UriResolverManager resolver = new UriResolverManager("GIS");
		return resolver.getLink(params, true);
	}
}
