package org.gcube.spatial.data.geonetwork.iso.tpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.gcube.portlets.user.uriresolvermanager.exception.IllegalArgumentException;
import org.gcube.portlets.user.uriresolvermanager.exception.UriResolverMapException;
import org.gcube.spatial.data.geonetwork.iso.BoundingBox;
import org.gcube.spatial.data.geonetwork.iso.ISOMetadataFactory;
import org.gcube.spatial.data.geonetwork.iso.tpl.DistributionInfo.DistributionInfoType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@ToString
@NoArgsConstructor
public class MetadataDescriptor {

	private String UUIDIdentifier;
	private Collection<ResponsibleParty> responsibleParties;
	
	private Date creationTime;
	
	private Long geometricObjectCount;
	
	private Date publicationTime;
	
	private String title;
	private String abstractField;
	private String purpose;
	private String credit;
	
	private Collection<Keyword> keywords;
	
	private DistributionInfo distributionInfo;
	
	private BoundingBox boundingBox;
	
	private Double spatialResolution;
	private String topicCategory;
	
	private String guidelinesConformityExplanation;
	private Boolean guidelinesConformityPass;
	
	private String lineageStatement;
	
	
	public void setGeoServerDistributionInfo(String geoServerUrl,String layerName, String workspace, String style, String CRS) throws UriResolverMapException, IllegalArgumentException{
		List<OnlineResource> resources=new ArrayList<OnlineResource>();
		String bbox=this.getBoundingBox().toString();
		String wmsUrl=ISOMetadataFactory.getWmsUrl(geoServerUrl, layerName, workspace, style, bbox, CRS);
		String wcsUrl=ISOMetadataFactory.getWcsUrl(geoServerUrl, layerName, workspace, bbox);
		String wfsUrl=ISOMetadataFactory.getWfsUrl(geoServerUrl, layerName, workspace);
		String gisViewerUrl=ISOMetadataFactory.getGisLinkByUUID(UUIDIdentifier);
		
		resources.add(new OnlineResource(wmsUrl, "WMS Link"));
		resources.add(new OnlineResource(wcsUrl, "WCS Link"));
		resources.add(new OnlineResource(wfsUrl, "WFS Link"));
		resources.add(new OnlineResource(gisViewerUrl, "GISViewer Link"));
		distributionInfo=new DistributionInfo(DistributionInfoType.GeoServer, resources);
	}
}
