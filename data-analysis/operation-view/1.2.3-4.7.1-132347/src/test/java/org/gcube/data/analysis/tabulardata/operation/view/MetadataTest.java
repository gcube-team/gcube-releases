package org.gcube.data.analysis.tabulardata.operation.view;

import java.util.Date;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.analysis.tabulardata.operation.view.maps.XMLAdapterImpl;
import org.gcube.spatial.data.geonetwork.iso.GcubeISOMetadata;
import org.gcube.spatial.data.geonetwork.iso.Thesaurus;
import org.geotoolkit.metadata.iso.extent.DefaultExtent;
import org.geotoolkit.xml.XML;
import org.junit.Test;
import org.opengis.metadata.citation.PresentationForm;
import org.opengis.metadata.identification.TopicCategory;
import org.opengis.metadata.spatial.GeometricObjectType;
import org.opengis.metadata.spatial.TopologyLevel;

public class MetadataTest {

	@Test
	public void testMeta() throws Exception{
		ScopeProvider.instance.set("/gcube/devsec");
		 GcubeISOMetadata meta=new GcubeISOMetadata();
		 
			meta.setAbstractField("800A");
			meta.setCreationDate(new Date(System.currentTimeMillis()));
			meta.setExtent((DefaultExtent) DefaultExtent.WORLD);
			meta.setGeometricObjectType(GeometricObjectType.SURFACE);
			meta.setPresentationForm(PresentationForm.MAP_DIGITAL);
			meta.setPurpose("800A");
			//		meta.setResolution(0.5d);
			meta.setTitle("800A");
			meta.setTopologyLevel(TopologyLevel.GEOMETRY_ONLY);
			meta.setUser("800A");		


			meta.addCredits("800A");

			Thesaurus generalThesaurus=meta.getConfig().getThesauri().get("General");
			
				meta.addKeyword("800A", generalThesaurus);

			meta.addTopicCategory(TopicCategory.BIOTA);
			
		 System.out.println(new XMLAdapterImpl().adaptXML(XML.marshal(meta.getMetadata())));
	}
	
	
}
