package org.gcube.spatial.data.geonetwork.test;

import it.geosolutions.geonetwork.util.GNInsertConfiguration;
import it.geosolutions.geonetwork.util.GNPriv;
import it.geosolutions.geonetwork.util.GNPrivConfiguration;

import java.io.File;
import java.util.Date;
import java.util.EnumSet;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.spatial.data.geonetwork.GeoNetwork;
import org.gcube.spatial.data.geonetwork.GeoNetworkPublisher;
import org.gcube.spatial.data.geonetwork.LoginLevel;
import org.gcube.spatial.data.geonetwork.iso.GcubeISOMetadata;
import org.gcube.spatial.data.geonetwork.iso.ISOMetadataFactory;
import org.gcube.spatial.data.geonetwork.iso.Thesaurus;
import org.gcube.spatial.data.geonetwork.model.ScopeConfiguration;
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
		ScopeProvider.instance.set("/gcube/devsec/devVRE");
//		ScopeProvider.instance.set("/d4science.research-infrastructures.eu");
		GcubeISOMetadata gMeta=new GcubeISOMetadata();
		gMeta.setAbstractField("This layer is used as a base layer for GIS VIewer widget");
		gMeta.setCreationDate(new Date(System.currentTimeMillis()));
		gMeta.setExtent((DefaultExtent) DefaultExtent.WORLD);
		gMeta.setGeometricObjectType(GeometricObjectType.SURFACE);
		gMeta.setPresentationForm(PresentationForm.IMAGE_DIGITAL);
		gMeta.setPurpose(gMeta.getAbstractField());
		gMeta.setTitle("TrueMarble_"+ScopeProvider.instance.get()+"_test");
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
		
		Long id=publisher.insertMetadata(config,meta);
		System.out.println("Inserted meta with id : "+id);
		
		// setting privileges
//		GNPrivConfiguration privs=new GNPrivConfiguration();
//		ScopeConfiguration scopeConfig=publisher.getConfiguration().getScopeConfiguration();
//		privs.addPrivileges(scopeConfig.getDefaultGroup(), EnumSet.of(GNPriv.VIEW,GNPriv.FEATURED));
//		publisher.setPrivileges(id, privs);
//		System.out.println("Privileges set");
		
	}

}
