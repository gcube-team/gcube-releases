package org.gcube.data.analysis.tabulardata.commons.webservice;

import java.util.List;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;

import org.gcube.data.analysis.tabulardata.commons.utils.Constants;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.InternalSecurityException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchTabularResourceException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchTaskException;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.TaskStatus;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.tasks.ResumeOperationRequest;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.tasks.TaskInfo;

@WebService(targetNamespace=Constants.TASK_TNS)
@SOAPBinding(style = Style.DOCUMENT, use=Use.LITERAL)
public interface TaskManager {

	public static final String SERVICE_NAME = "taskmanager";
	
	@SOAPBinding(parameterStyle=ParameterStyle.WRAPPED)
	TaskInfo remove(String identifier) throws NoSuchTaskException, InternalSecurityException;
	
	@SOAPBinding(parameterStyle=ParameterStyle.WRAPPED)
	List<TaskInfo> get(String[] identifiers);
	
	@SOAPBinding(parameterStyle=ParameterStyle.WRAPPED)
	TaskInfo abort(String identifier) throws NoSuchTaskException, InternalSecurityException;

	@SOAPBinding(parameterStyle=ParameterStyle.WRAPPED)
	List<TaskInfo> getTasksByTabularResource(Long tabularResourceId)
			throws NoSuchTabularResourceException, InternalSecurityException;

	@SOAPBinding(parameterStyle=ParameterStyle.WRAPPED)
	List<TaskInfo> getTasksByStatusAndTabularResource(Long tabularResourceId,
			TaskStatus status) throws NoSuchTabularResourceException, InternalSecurityException;

	@SOAPBinding(parameterStyle=ParameterStyle.WRAPPED)
	TaskInfo resubmit(String identifier) throws NoSuchTaskException, InternalSecurityException;
	
	@SOAPBinding(parameterStyle=ParameterStyle.WRAPPED)
	TaskInfo resume(ResumeOperationRequest request) throws NoSuchTaskException, InternalSecurityException;
	

}
