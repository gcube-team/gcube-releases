package org.gcube.data.analysis.tabulardata.commons.webservice;

import java.util.List;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;

import org.gcube.data.analysis.tabulardata.commons.utils.Constants;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.ExecutionFailedException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.HistoryNotFoundException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.InternalSecurityException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.InvalidInvocationException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchTabularResourceException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.OperationNotFoundException;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.BatchExecuteRequest;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.ExecuteRequest;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationDefinition;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.tasks.TaskInfo;


@WebService(targetNamespace=Constants.OPERATION_TNS)
@SOAPBinding(style = Style.DOCUMENT, use=Use.LITERAL)
public interface OperationManager {
	
	public static final String SERVICE_NAME = "operationmanager";

	@SOAPBinding(parameterStyle=ParameterStyle.WRAPPED)
	List<OperationDefinition> getCapabilities();

	@SOAPBinding(parameterStyle=ParameterStyle.BARE)
	OperationDefinition getOperationDescriptor(long operationId) throws OperationNotFoundException;
		
	/**
	 *  return the taskId
	 * 
	 * @param invocation
	 * @param targetTabularResourceId
	 * @return
	 * @throws NoSuchTabularResourceException
	 * @throws InvalidInvocationException
	 * @throws OperationNotFoundException 
	 * @throws SecurityException 
	 */
	@SOAPBinding(parameterStyle=ParameterStyle.WRAPPED)
	TaskInfo execute(ExecuteRequest request) throws NoSuchTabularResourceException, OperationNotFoundException, InternalSecurityException;
	
	@SOAPBinding(parameterStyle=ParameterStyle.WRAPPED)
	TaskInfo batchExecute(BatchExecuteRequest request) throws NoSuchTabularResourceException, OperationNotFoundException, InternalSecurityException;
	
	@SOAPBinding(parameterStyle=ParameterStyle.WRAPPED)
	TaskInfo rollbackTo(long tabularRessourceId, long historyStepId) throws HistoryNotFoundException, NoSuchTabularResourceException, OperationNotFoundException, InternalSecurityException;

	@SOAPBinding(parameterStyle=ParameterStyle.WRAPPED)
	void executeSynchMetadataOperation(ExecuteRequest request) throws ExecutionFailedException ,  NoSuchTabularResourceException, OperationNotFoundException, InternalSecurityException;

	@SOAPBinding(parameterStyle=ParameterStyle.WRAPPED)
	TaskInfo removeValidations(Long tabularResourceId) throws NoSuchTabularResourceException,
			InternalSecurityException;
		
}
