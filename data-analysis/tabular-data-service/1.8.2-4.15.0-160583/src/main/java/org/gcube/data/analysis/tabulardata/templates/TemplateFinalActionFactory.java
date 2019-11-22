package org.gcube.data.analysis.tabulardata.templates;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.gcube.data.analysis.tabulardata.commons.templates.model.Template;
import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.service.OperationManagerImpl;
import org.gcube.data.analysis.tabulardata.service.TabularResourceManagerImpl;
import org.gcube.data.analysis.tabulardata.utils.EntityManagerHelper;

@Singleton
public class TemplateFinalActionFactory {
	
	@Inject
	OperationManagerImpl opManager;
	
	@Inject
	EntityManagerHelper emHelper;
	
	@Inject
	TabularResourceManagerImpl trImpl;
	
	@Inject
	CubeManager cubeManager;
	
	public TemplateFinalActionExecutor getExecutor(Template template){
		return new TemplateFinalActionExecutor(opManager, trImpl,  template, cubeManager, emHelper);
	}
	
}
