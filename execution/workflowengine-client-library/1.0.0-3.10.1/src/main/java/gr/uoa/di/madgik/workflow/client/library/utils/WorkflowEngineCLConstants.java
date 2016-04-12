package gr.uoa.di.madgik.workflow.client.library.utils;

import static org.gcube.common.clients.stubs.jaxws.GCoreServiceBuilder.*;
import gr.uoa.di.madgik.workflow.client.library.stubs.WorkflowEngineStub;

import java.util.concurrent.TimeUnit;

import org.gcube.common.clients.stubs.jaxws.GCoreService;

import javax.xml.namespace.QName;

public class WorkflowEngineCLConstants {
	
	public static final int DEFAULT_TIMEOUT= (int) TimeUnit.SECONDS.toMillis(600000);

	public static final String NAMESPACE="http://gcube.org/execution/workflowengine";
	public static final String NAME="gcube/execution/workflowengine";

	public static final QName name = new QName("http://gcube.org/execution/workflowengine/service", "WorkflowEngineService");
	public static final String porttypeNS = "http://gcube.org/execution/workflowengine";
	public static final String porttypeLN = "WorkflowEngineServicePortType";
	
	public static String gcubeClass="Execution";
	public static String gcubeName="WorkflowEngineService";
			 
	public static final GCoreService<WorkflowEngineStub> workflowEngine = service().withName(name)
		                                                            .coordinates(gcubeClass,gcubeName)
			                                                            .andInterface(WorkflowEngineStub.class); 
	
}
