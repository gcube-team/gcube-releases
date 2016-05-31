package org.gcube.spatial.data.geonetwork.test;

import java.io.File;
import java.util.Date;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.spatial.data.geonetwork.GeoNetwork;
import org.gcube.spatial.data.geonetwork.GeoNetworkPublisher;
import org.gcube.spatial.data.geonetwork.LoginLevel;
import org.gcube.spatial.data.geonetwork.iso.GcubeISOMetadata;
import org.gcube.spatial.data.geonetwork.iso.ISOMetadataFactory;
import org.gcube.spatial.data.geonetwork.iso.Thesaurus;
import org.geotoolkit.metadata.iso.DefaultMetadata;
import org.geotoolkit.metadata.iso.extent.DefaultExtent;
import org.geotoolkit.xml.XML;
import org.opengis.metadata.Metadata;
import org.opengis.metadata.citation.PresentationForm;
import org.opengis.metadata.identification.TopicCategory;
import org.opengis.metadata.spatial.GeometricObjectType;

public class TrueMarbleMeta {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		ScopeProvider.instance.set("/gcube");
//		ScopeProvider.instance.set("/d4science.research-infrastructures.eu");
		GcubeISOMetadata gMeta=new GcubeISOMetadata();
		gMeta.setAbstractField("This layer is used as a base layer for GIS VIewer widget");
		gMeta.setCreationDate(new Date(System.currentTimeMillis()));
		gMeta.setExtent((DefaultExtent) DefaultExtent.WORLD);
		gMeta.setGeometricObjectType(GeometricObjectType.SURFACE);
		gMeta.setPresentationForm(PresentationForm.IMAGE_DIGITAL);
		gMeta.setPurpose(gMeta.getAbstractField());
		gMeta.setTitle("TrueMarble");
		gMeta.setUser("fabio.sinibaldi");
		Thesaurus general=gMeta.getConfig().getThesauri().get("General");
		gMeta.addKeyword("True Marble", general);
		gMeta.addTopicCategory(TopicCategory.ENVIRONMENT);
		Metadata meta=gMeta.getMetadata();
		
		((DefaultMetadata)meta).setDistributionInfo(ISOMetadataFactory.getDistributionByLayer("TrueMarble.16km.2700x1350", "http://geoserver-dev.d4science.org/geoserver", "raster", "-180.0,-90.0,180.0,90.0", gMeta.getConfig()));
		XML.marshal(meta, new File("TrueMarble.xml"));
		
		GeoNetworkPublisher publisher=GeoNetwork.get();
		publisher.login(LoginLevel.DEFAULT);
//		System.out.println("Inserted meta with id : "+publisher.insertMetadata(meta));
	}

}
