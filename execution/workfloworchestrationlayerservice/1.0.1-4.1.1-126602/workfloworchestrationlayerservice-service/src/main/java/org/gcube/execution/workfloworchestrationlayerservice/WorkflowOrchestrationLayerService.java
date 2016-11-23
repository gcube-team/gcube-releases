package org.gcube.execution.workfloworchestrationlayerservice;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;

import org.gcube.common.core.contexts.GCUBEServiceContext;
import org.gcube.common.core.porttypes.GCUBEPortType;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.execution.workfloworchestrationlayerservice.stubs.JdlResource;
import org.gcube.execution.workfloworchestrationlayerservice.stubs.WOLParams;
import org.gcube.execution.workfloworchestrationlayerservice.stubs.WOLResource;
import org.gcube.execution.workfloworchestrationlayerservice.utils.WorkflowOrchestrationLayer;
import org.gcube.execution.workfloworchestrationlayerservice.wrappers.JDLAdaptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkflowOrchestrationLayerService extends GCUBEPortType {
	/**
	 * The Logger used by this class
	 */
	private static Logger logger = LoggerFactory.getLogger(WorkflowOrchestrationLayerService.class);
	
	/** The directory that will contain the temporary files needed for each plan */
	public static File tempDir = null;
	
	
	@Override
	protected GCUBEServiceContext getServiceContext() {
		return WOLServiceContext.getContext();
	}

	/** Initializes the temp directory, should be executed after the initialization of the service */
	private void initializeTempDir()
	{
		if(tempDir==null)
		{
			tempDir = new File(WOLServiceContext.getContext().getPersistenceRoot().getAbsolutePath()+File.separator+"tempFiles"+File.separator);
			tempDir.mkdirs();
		}
	}
	
	/** handles WOL gJDL plans and creates a PE2NG plan with calls to the Workflow Engine Service functions */
	public String adaptWOL(WOLParams wp)
	{
		initializeTempDir();
		String jdlDescription = wp.getJdlDescription();
		logger.info("JDL Description is: "+jdlDescription);
		HashMap<String, byte[]> wrs = new HashMap<String, byte[]>();
		HashMap<String, String> jdlrs = new HashMap<String, String>();
		for(WOLResource wr : wp.getWolResources())
			wrs.put(wr.getResourceKey(), wr.getInMessageBytePayload());
		for(JdlResource jdlr : wp.getJdlResources())
			jdlrs.put(jdlr.getResourceKey(), jdlr.getInMessageStringPayload());
		try {
			String scope = ScopeProvider.instance.get();
			scope = "/gcube/devNext";
			String resourceFile = WorkflowOrchestrationLayer.transform(jdlDescription,wrs,jdlrs,wp.getConfig(),scope);
			if(resourceFile==null)
			{
				logger.info("Execution aborted...");
				return null;
			}
			logger.info("Resource file is: "+resourceFile);
			JDLAdaptor adaptor = new JDLAdaptor();
			String[] newArgs = {resourceFile};
			return adaptor.execute(newArgs);
		} catch (FileNotFoundException e) {
			logger.info("Error while tranforming",e);
		}
		return null;
	}
}
