package org.gcube.spatial.data.sdi.test;

import java.io.IOException;
import java.nio.file.Paths;

import javax.xml.transform.TransformerException;

import org.gcube.spatial.data.sdi.LocalConfiguration;
import org.gcube.spatial.data.sdi.engine.MetadataTemplateManager;
import org.gcube.spatial.data.sdi.engine.impl.metadata.MetadataTemplateManagerImpl;
import org.gcube.spatial.data.sdi.engine.impl.metadata.TemplateApplicationReport;
import org.gcube.spatial.data.sdi.model.metadata.TemplateInvocationBuilder;
import org.junit.Test;

public class MetadataApplicationTest {

	@Test
	public void apply() throws IOException, TransformerException{
		TokenSetter.set("/gcube/devsec/devVRE");
		MetadataTemplateManagerImpl manager=new MetadataTemplateManagerImpl();
//		LocalConfiguration.init(Paths.get("src/main/webapp/WEB-INF/config.properties").toUri().toURL())
//		.setTemplateConfigurationObject(Paths.get("src/main/webapp/WEB-INF/metadataTemplates").toFile());
		
		manager.init(Paths.get("src/main/webapp/WEB-INF/metadataTemplates").toFile());
		
		
		System.out.println(manager.getAvailableTemplates());
		
		
		TemplateInvocationBuilder builder=new TemplateInvocationBuilder();
		builder.threddsOnlineResources("localhost", "someFileName.sc", "newCatalog");
		TemplateApplicationReport report=manager.applyTemplates(Paths.get("src/test/resources/xml/toEnrichMetadata.xml").toFile(), builder.get());
		System.out.println(report);
	}
	
	
}
