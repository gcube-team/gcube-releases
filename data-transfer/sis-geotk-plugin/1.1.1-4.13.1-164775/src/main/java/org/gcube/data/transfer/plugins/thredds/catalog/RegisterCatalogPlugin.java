package org.gcube.data.transfer.plugins.thredds.catalog;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.gcube.data.transfer.model.ExecutionReport;
import org.gcube.data.transfer.model.ExecutionReport.ExecutionReportFlag;
import org.gcube.data.transfer.model.PluginInvocation;
import org.gcube.data.transfer.plugin.AbstractPlugin;
import org.gcube.data.transfer.plugin.fails.PluginCleanupException;
import org.gcube.data.transfer.plugin.fails.PluginExecutionException;
import org.gcube.data.transfer.plugins.thredds.ThreddsInstanceManager;
import org.gcube.data.transfer.plugins.thredds.XMLCatalogHandler;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public class RegisterCatalogPlugin extends AbstractPlugin{

	private ThreddsInstanceManager instanceManager;
	
	public RegisterCatalogPlugin(PluginInvocation invocation, ThreddsInstanceManager instanceManager) {
		super(invocation);
		this.instanceManager=instanceManager;
	}

	@Override
	public void cleanup() throws PluginCleanupException {
		// TODO Auto-generated method stub

	}

	@Override
	public ExecutionReport run() throws PluginExecutionException {
		try {		
			Map<String,String> params=invocation.getParameters();
			String catalogFile=params.get(RegisterCatalogPluginFactory.CATALOG_FILE);
			if(catalogFile.contains(" ")) throw new PluginExecutionException("Invalid catalog filename "+catalogFile);
			String catalogReference=params.get(RegisterCatalogPluginFactory.CATALOG_REFERENCE);
			log.trace("Registering {} as {}",catalogFile,catalogReference);
			
			XMLCatalogHandler handler=instanceManager.mainCatalogHandler();
			handler.registerCatalog(new File(catalogFile),catalogReference);
			handler.close();
			instanceManager.clearCache();
			return new ExecutionReport(invocation,"Registered catalog entry "+catalogReference,ExecutionReportFlag.SUCCESS);
		}catch(Throwable t) {
			log.error("Unable to register catalog.",t);
			throw new PluginExecutionException("Unable to register catalog.",t);
		}
	}
	
}
