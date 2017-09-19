package org.gcube.spatial.data.sdi.test.factories;

import java.nio.file.Paths;

import org.gcube.spatial.data.sdi.engine.MetadataTemplateManager;
import org.gcube.spatial.data.sdi.engine.impl.metadata.MetadataTemplateManagerImpl;
import org.glassfish.hk2.api.Factory;

public class MetadataTemplateManagerFactory implements Factory<MetadataTemplateManager>{
	
	public MetadataTemplateManagerFactory() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void dispose(MetadataTemplateManager arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public MetadataTemplateManager provide() {
		MetadataTemplateManagerImpl manager=new MetadataTemplateManagerImpl();		
		manager.init(Paths.get("src/main/webapp/WEB-INF/metadataTemplates").toFile());
		return manager;
	}
}
