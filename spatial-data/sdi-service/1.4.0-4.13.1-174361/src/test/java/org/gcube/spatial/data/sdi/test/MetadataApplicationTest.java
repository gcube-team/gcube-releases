package org.gcube.spatial.data.sdi.test;

import java.io.IOException;
import java.nio.file.Paths;

import javax.xml.transform.TransformerException;

import org.gcube.spatial.data.sdi.LocalConfiguration;
import org.gcube.spatial.data.sdi.engine.TemplateManager;
import org.gcube.spatial.data.sdi.engine.impl.metadata.MetadataTemplateManagerImpl;
import org.gcube.spatial.data.sdi.engine.impl.metadata.TemplateApplicationReport;
import org.gcube.spatial.data.sdi.model.metadata.TemplateInvocationBuilder;
import org.junit.Test;

public class MetadataApplicationTest {

	@Test
	public void apply() throws IOException, TransformerException{
		TokenSetter.set("/gcube/devNext/NextNext");
		MetadataTemplateManagerImpl manager=new MetadataTemplateManagerImpl();
//		LocalConfiguration.init(Paths.get("src/main/webapp/WEB-INF/config.properties").toUri().toURL())
//		.setTemplateConfigurationObject(Paths.get("src/main/webapp/WEB-INF/metadataTemplates").toFile());
		
		manager.init(Paths.get("src/main/webapp/WEB-INF/xmlTemplates").toFile());
		
		
		System.out.println(manager.getAvailableMetadataTemplates());
		
		
		TemplateInvocationBuilder builder=new TemplateInvocationBuilder();
		builder.threddsOnlineResources("localhost", "someFileName.sc", "newCatalog");
		TemplateApplicationReport report=manager.applyMetadataTemplates(Paths.get("src/test/resources/xml/toEnrichMetadata.xml").toFile(), builder.get());
		System.out.println(report);
		
		
		
	}
	
	
}
