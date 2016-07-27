package org.gcube.spatial.data.gis.meta;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.gcube.spatial.data.gis.URIUtils;
import org.geotoolkit.metadata.iso.DefaultMetadata;
import org.geotoolkit.metadata.iso.citation.DefaultCitation;
import org.geotoolkit.metadata.iso.citation.DefaultCitationDate;
import org.geotoolkit.metadata.iso.citation.DefaultOnlineResource;
import org.geotoolkit.metadata.iso.distribution.DefaultDigitalTransferOptions;
import org.geotoolkit.metadata.iso.distribution.DefaultDistribution;
import org.geotoolkit.metadata.iso.distribution.DefaultFormat;
import org.geotoolkit.metadata.iso.identification.DefaultBrowseGraphic;
import org.geotoolkit.metadata.iso.identification.DefaultDataIdentification;
import org.geotoolkit.util.DefaultInternationalString;
import org.opengis.metadata.Metadata;
import org.opengis.metadata.citation.CitationDate;
import org.opengis.metadata.citation.DateType;
import org.opengis.metadata.identification.DataIdentification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MetadataEnricher {
	final static Logger logger= LoggerFactory.getLogger(MetadataEnricher.class);
	
	private Metadata toEnrich;
	private DefaultMetadata enriched;
	private ArrayList<String> messages=new ArrayList<String>();
	
	
	private String uuid;
	
	private DefaultDataIdentification ident=null;
	
	public MetadataEnricher(Metadata toEnrich,boolean generateUUID) {
		this.toEnrich=toEnrich;
		this.enriched=castMeta(toEnrich);		
		if(generateUUID){
			uuid=UUID.randomUUID().toString();
			enriched.setFileIdentifier(uuid);
		}else {
			uuid=enriched.getFileIdentifier();
		}
		
		if(enriched.getIdentificationInfo().isEmpty()){
			ident=new DefaultDataIdentification();
			enriched.getIdentificationInfo().add(ident);			
		}else ident=new DefaultDataIdentification((DataIdentification)enriched.getIdentificationInfo().iterator().next());
		
	}
	
	public String getMetadataIdentifier(){
		return uuid;
	}
	
	
	public void addPreview(String previewURL){
		try{
			ident.getGraphicOverviews().add(new DefaultBrowseGraphic(new URI(previewURL)));
		}catch(URISyntaxException e){
			String msg="Unable to set preview, passed URI is "+previewURL+", cause : "+e.getMessage();
			logger.warn(msg);
			messages.add(msg);			
		}
	}
	
	public void setdistributionURIs(List<String> uris,String layerName){
		DefaultDistribution distribution=new DefaultDistribution();
		
		DefaultDigitalTransferOptions transferOptions=new DefaultDigitalTransferOptions();
		for(String uriString:uris)
			try{
				URI uri=new URI(uriString);
				DefaultOnlineResource resource=new DefaultOnlineResource(uri);
				String protocol=URIUtils.getProtocol(uriString);
				resource.setName(layerName);
				resource.setProtocol(protocol);
				transferOptions.getOnLines().add(resource);
				
			}catch(URISyntaxException e){
				String msg="Unable to set transfer option, passed URI is "+uriString+", cause : "+e.getMessage();
				logger.warn(msg);
				messages.add(msg);				
			}
		
		//******* Assuming all formats available
		DefaultFormat format1 = new DefaultFormat();
		format1.setName(new DefaultInternationalString("WMS"));
		format1.setVersion(new DefaultInternationalString("1.1.0"));			
		DefaultFormat format2 = new DefaultFormat();
		format2.setName(new DefaultInternationalString("WFS"));
		format2.setVersion(new DefaultInternationalString("1.1.0"));
		DefaultFormat format3 = new DefaultFormat();
		format3.setName(new DefaultInternationalString("WCS"));
		format3.setVersion(new DefaultInternationalString("1.0.0"));
		
		//*** GIS-RESOLVER-LINK is HTTP
		DefaultFormat format4 = new DefaultFormat();
		format4.setName(new DefaultInternationalString("HTTP"));
		format4.setVersion(new DefaultInternationalString("1.1.0"));		
		
		distribution.setDistributionFormats(new ArrayList<DefaultFormat>(Arrays.asList(format1, format2, format3,format4)));
		
		distribution.getTransferOptions().add(transferOptions);
		enriched.setDistributionInfo(distribution);
	}
	
	public void addDate(Date toAdd,DateType type){
		DefaultCitationDate publishDate=new DefaultCitationDate(toAdd, type);
		if(ident.getCitation()==null){		
			DefaultCitation citation=new DefaultCitation();
			citation.getDates().add(publishDate);			
		}else ((Collection<CitationDate>)ident.getCitation().getDates()).add((CitationDate)publishDate);
		
	}
	
	
	
	public DefaultMetadata getEnriched() {
		return enriched;
	}
	public Metadata getToEnrich() {
		return toEnrich;
	}
	public ArrayList<String> getMessages() {
		return messages;
	}
	
	private static final DefaultMetadata castMeta(Metadata meta){
		if(meta.getClass().isAssignableFrom(DefaultMetadata.class))
			return (DefaultMetadata)meta;
		else return new DefaultMetadata(meta);		
	}
}
