package org.gcube.spatial.data.geonetwork.test;

import java.io.File;
import java.util.Date;

import org.gcube.spatial.data.geonetwork.GeoNetwork;
import org.gcube.spatial.data.geonetwork.GeoNetworkPublisher;
import org.gcube.spatial.data.geonetwork.LoginLevel;
import org.gcube.spatial.data.geonetwork.iso.GcubeISOMetadata;
import org.gcube.spatial.data.geonetwork.iso.ISOMetadataFactory;
import org.gcube.spatial.data.geonetwork.iso.Thesaurus;
import org.gcube.spatial.data.geonetwork.utils.ScopeUtils;
import org.geotoolkit.metadata.iso.DefaultMetadata;
import org.geotoolkit.metadata.iso.extent.DefaultExtent;
import org.geotoolkit.xml.XML;
import org.opengis.metadata.Metadata;
import org.opengis.metadata.citation.PresentationForm;
import org.opengis.metadata.identification.TopicCategory;
import org.opengis.metadata.spatial.GeometricObjectType;

import it.geosolutions.geonetwork.util.GNInsertConfiguration;

public class TrueMarbleMeta {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		TokenSetter.set("/gcube/devNext/NextNext");
//		TokenSetter.set("/d4science.research-infrastructures.eu");
		GcubeISOMetadata gMeta=new GcubeISOMetadata();
		gMeta.setAbstractField("This layer is used as a base layer for GIS VIewer widget");
		gMeta.setCreationDate(new Date(System.currentTimeMillis()));
		gMeta.setExtent((DefaultExtent) DefaultExtent.WORLD);
		gMeta.setGeometricObjectType(GeometricObjectType.SURFACE);
		gMeta.setPresentationForm(PresentationForm.IMAGE_DIGITAL);
		gMeta.setPurpose(gMeta.getAbstractField());
		gMeta.setTitle("TrueMarble_"+ScopeUtils.getCurrentScope()+"_test");
		gMeta.setUser("fabio.sinibaldi");
		Thesaurus general=gMeta.getConfig().getThesauri().get("General");
		gMeta.addKeyword("True Marble", general);
		gMeta.addTopicCategory(TopicCategory.ENVIRONMENT);
		Metadata meta=gMeta.getMetadata();
		
		((DefaultMetadata)meta).setDistributionInfo(ISOMetadataFactory.getDistributionByLayer("TrueMarble.16km.2700x1350", "http://geoserver-dev.d4science.org/geoserver", "raster", "-180.0,-90.0,180.0,90.0", gMeta.getConfig()));
		XML.marshal(meta, new File("TrueMarble.xml"));
		
		GeoNetworkPublisher publisher=GeoNetwork.get();
		publisher.login(LoginLevel.PRIVATE);
		GNInsertConfiguration config=publisher.getCurrentUserConfiguration("dataset", "_none_");
		
		Long id=publisher.insertAndPromoteMetadata(config,meta);
		System.out.println("Inserted meta with id : "+id);
		
		
	}

}
