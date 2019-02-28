package org.gcube.spatial.data.geonetwork.test;

import java.io.File;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.UUID;

import org.gcube.spatial.data.geonetwork.GeoNetworkPublisher;
import org.gcube.spatial.data.geonetwork.LoginLevel;
import org.gcube.spatial.data.geonetwork.iso.BoundingBox;
import org.gcube.spatial.data.geonetwork.iso.tpl.ISOMetadataByTemplate;
import org.gcube.spatial.data.geonetwork.iso.tpl.Keyword;
import org.gcube.spatial.data.geonetwork.iso.tpl.MetadataDescriptor;
import org.gcube.spatial.data.geonetwork.iso.tpl.ResponsibleParty;
import org.gcube.spatial.data.geonetwork.iso.tpl.ResponsibleParty.Contact;

import it.geosolutions.geonetwork.util.GNInsertConfiguration;

public class TrueMarbleMeta {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		TokenSetter.set("/gcube/devsec");
//		TokenSetter.set("/d4science.research-infrastructures.eu");
		
//		GcubeISOMetadata gMeta=new GcubeISOMetadata();
//		gMeta.setAbstractField("This layer is used as a base layer for GIS VIewer widget");
//		gMeta.setCreationDate(new Date(System.currentTimeMillis()));
//		gMeta.setExtent((DefaultExtent) DefaultExtent.WORLD);
//		gMeta.setGeometricObjectType(GeometricObjectType.SURFACE);
//		gMeta.setPresentationForm(PresentationForm.IMAGE_DIGITAL);
//		gMeta.setPurpose(gMeta.getAbstractField());
//		gMeta.setTitle("TrueMarble_"+ScopeUtils.getCurrentScope()+"_test");
//		gMeta.setUser("fabio.sinibaldi");
//		Thesaurus general=gMeta.getConfig().getThesauri().get("General");
//		gMeta.addKeyword("True Marble", general);
//		gMeta.addTopicCategory(TopicCategory.ENVIRONMENT);
		
		
	
		MetadataDescriptor desc=new MetadataDescriptor();
		desc.setResponsibleParties(Arrays.asList(
				new ResponsibleParty("The scope ","the infra",ResponsibleParty.Roles.DISTRIBUTOR,new Contact("my.mail@this.place.com","www.mipiacitu.com")),
				new ResponsibleParty("Io","me stesso",ResponsibleParty.Roles.POINT_OF_CONTACT,new Contact("point.of.contact.maiol@place.com","www.mipiacitu.com")),
				new ResponsibleParty("Io","me stesso",ResponsibleParty.Roles.AUTHOR,new Contact("point.of.contact.maiol@place.com","www.mipiacitu.com"))
				));
		
		
		
		
		desc.setAbstractField("My Abstract Field");
		
		desc.setCredit("Fatto io");		
		
		desc.setCreationTime(new GregorianCalendar().getTime());
		desc.setGeometricObjectCount(1000l);
		desc.setKeywords(Arrays.asList(Keyword.getInspireTheme(Keyword.Themes.SEA_REGIONS)));
		
		desc.setPublicationTime(desc.getCreationTime());
		desc.setPurpose("Just for fun");
		desc.setTitle("Il mio bel titolone");
		desc.setUUIDIdentifier(UUID.randomUUID().toString());
		desc.setBoundingBox(BoundingBox.WORLD_EXTENT);
		desc.setGeoServerDistributionInfo("http://geoserver.d4science.org/geoserver", "ws","wmpa", "speciesProb", "EPSG:4326");
		desc.setSpatialResolution(0.5d);
		desc.setTopicCategory("environment");
		desc.setLineageStatement("I made with my own hands");
		System.out.println(desc.getBoundingBox());
		String metaPath=ISOMetadataByTemplate.createXML(desc);
		System.out.println("Going to Publish ----->> "+metaPath);
		
		GeoNetworkPublisher publisher=TestConfiguration.getClient();
		publisher.login(LoginLevel.SCOPE);
		GNInsertConfiguration config=publisher.getCurrentUserConfiguration("dataset", "_none_");
		long id=publisher.insertMetadata(config, new File(metaPath));
		System.out.println("PUBLISHED WITH ID : "+id);
		
		
//		Metadata meta=gMeta.getMetadata();
//		
//		((DefaultMetadata)meta).setDistributionInfo(ISOMetadataFactory.getDistributionByLayer("TrueMarble.16km.2700x1350", "http://geoserver-dev.d4science.org/geoserver", "raster", "-180.0,-90.0,180.0,90.0", gMeta.getConfig()));
//		XML.marshal(meta, new File("TrueMarble.xml"));
		
		
		
//		GeoNetworkPublisher publisher=TestConfiguration.getClient();
//		publisher.login(LoginLevel.SCOPE);
//		GNInsertConfiguration config=publisher.getCurrentUserConfiguration("dataset", "_none_");
//		
//		//Long id=publisher.insertMetadata(config,new File("/tmp/GEO_1069334659927122420.xml"));
//		Long id=publisher.insertMetadata(config,meta);
//		System.out.println("Inserted meta with id : "+id);
		
		
	}

}
