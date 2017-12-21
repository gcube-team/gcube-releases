package org.gcube.spatial.data.sdi.test.factories;

import java.nio.file.Paths;

import org.gcube.spatial.data.sdi.engine.TemplateManager;
import org.gcube.spatial.data.sdi.engine.impl.metadata.MetadataTemplateManagerImpl;
import org.glassfish.hk2.api.Factory;

public class MetadataTemplateManagerFactory implements Factory<TemplateManager>{
	
	public MetadataTemplateManagerFactory() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void dispose(TemplateManager arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public TemplateManager provide() {
		MetadataTemplateManagerImpl manager=new MetadataTemplateManagerImpl();		
		manager.init(Paths.get("src/main/webapp/WEB-INF/xmlTemplates").toFile());
		return manager;
	}
}
