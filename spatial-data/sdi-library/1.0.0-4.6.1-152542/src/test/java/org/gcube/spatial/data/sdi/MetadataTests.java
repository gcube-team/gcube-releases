package org.gcube.spatial.data.sdi;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;

import org.gcube.spatial.data.sdi.interfaces.Metadata;
import org.gcube.spatial.data.sdi.model.metadata.MetadataPublishOptions;
import org.gcube.spatial.data.sdi.model.metadata.TemplateInvocationBuilder;
import org.gcube.spatial.data.sdi.plugins.SDIAbstractPlugin;
import org.junit.Before;
import org.junit.Test;

public class MetadataTests {

	@Before
	public void setScope(){
		TokenSetter.set("/gcube/devsec/devVRE");
	}
	
	@Test
	public void getAvailableTemplatesTest() throws IllegalArgumentException, URISyntaxException{
		
		Metadata meta=SDIAbstractPlugin.metadata().at(new URI("http://sdi-d-d4s.d4science.org/sdi-service/gcube/service")).build();
		System.out.println(meta.getAvailableTemplates());
	}

	
	@Test
	public void pushMetadata() throws IllegalArgumentException, URISyntaxException{
		File toPubilsh=Paths.get("src/test/resources/xml/toEnrichMeta.xml").toFile();
		Metadata meta=SDIAbstractPlugin.metadata().at(new URI("http://sdi-d-d4s.d4science.org/sdi-service/gcube/service")).build();
		System.out.println(meta.pushMetadata(toPubilsh));
		
		MetadataPublishOptions opts=new MetadataPublishOptions(new TemplateInvocationBuilder().threddsOnlineResources("my_hostname", "some_dataset.nc", "myPersonalCatalog").get());
		opts.setGeonetworkCategory("service");
		System.out.println(meta.pushMetadata(toPubilsh, opts));
	}
	
	
	
}
