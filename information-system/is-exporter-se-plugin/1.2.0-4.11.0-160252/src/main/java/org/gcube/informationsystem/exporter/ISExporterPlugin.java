package org.gcube.informationsystem.exporter;

import java.util.Map;

import org.gcube.common.authorization.client.Constants;
import org.gcube.common.authorization.library.AuthorizationEntry;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.informationsystem.exporter.mapper.GenericResourceExporter;
import org.gcube.informationsystem.exporter.mapper.ServiceEndpointExporter;
import org.gcube.vremanagement.executor.plugin.Plugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class ISExporterPlugin extends Plugin<ISExporterPluginDeclaration> {

	/**
	 * Logger
	 */
	private static Logger logger = LoggerFactory.getLogger(ISExporterPlugin.class);
	
	public static final String FILTERED_REPORT = "FILTERED_REPORT";
	
	
	public ISExporterPlugin(ISExporterPluginDeclaration pluginDeclaration) {
		super(pluginDeclaration);
		logger.debug("contructor");
	}
	
	/**{@inheritDoc}*/
	@Override
	public void launch(Map<String, Object> inputs) throws Exception {
		String token = SecurityTokenProvider.instance.get();
		AuthorizationEntry authorizationEntry = Constants.authorizationService().get(token);
		String scope = authorizationEntry.getContext();
		logger.info("Launching {} execution on scope {}", 
				ISExporterPluginDeclaration.NAME, scope);
		
		boolean filteredReport = false;
		
		try {
			filteredReport = (boolean) inputs.getOrDefault(FILTERED_REPORT, false);
		}catch (Exception e) {
			filteredReport = false;
		}
		
		logger.debug("Failure Report are filtered (e.g. Failing UUID are not shown to avoid to produce to much uneeded reports)");
		
		GenericResourceExporter genericResourceExporter = new GenericResourceExporter(filteredReport);
		genericResourceExporter.export();
		
		ServiceEndpointExporter serviceEndpointExporter = new ServiceEndpointExporter(filteredReport);
		serviceEndpointExporter.export();
		
		logger.info("{} execution finished", ISExporterPluginDeclaration.NAME);
	}

	/**{@inheritDoc}*/
	@Override
	protected void onStop() throws Exception {
		logger.debug("onStop()");
		
		Thread.currentThread().interrupt();
	}

}
