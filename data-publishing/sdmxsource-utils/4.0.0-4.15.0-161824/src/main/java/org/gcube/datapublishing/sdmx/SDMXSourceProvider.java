package org.gcube.datapublishing.sdmx;

import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;

import org.sdmxsource.sdmx.api.manager.output.StructureWriterManager;
import org.sdmxsource.sdmx.api.manager.parse.StructureParsingManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SDMXSourceProvider {
	
	private ApplicationContext context = null;

	private StructureWriterManager structureWriterManager;
	private StructureParsingManager structureParsingManager;

	@Produces @Default
	public StructureParsingManager getStructureParsingManager() {
		if (structureParsingManager == null) {
			initApplicationContext();
			structureParsingManager = context.getBean(StructureParsingManager.class);
		}
		return structureParsingManager;

	}

	
	@Produces @Default
	public StructureWriterManager getStructureWriterManager() {
		if (structureWriterManager == null) {
			initApplicationContext();
			structureWriterManager = context.getBean(StructureWriterManager.class);
		}
		return structureWriterManager;
	}

	public void initApplicationContext() {
		if (context == null)
			context = new ClassPathXmlApplicationContext("sdmxsource-context.xml");
	}
	

}
