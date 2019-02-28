package org.gcube.portlets.user.dataminerexecutor.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.gcube.data.analysis.dataminermanagercl.server.dmservice.SClient;
import org.gcube.data.analysis.dataminermanagercl.server.util.ServiceCredentials;
import org.gcube.data.analysis.dataminermanagercl.shared.data.OutputData;
import org.gcube.data.analysis.dataminermanagercl.shared.data.TableItemSimple;
import org.gcube.data.analysis.dataminermanagercl.shared.data.computations.ComputationData;
import org.gcube.data.analysis.dataminermanagercl.shared.data.computations.ComputationId;
import org.gcube.data.analysis.dataminermanagercl.shared.parameters.Parameter;
import org.gcube.data.analysis.dataminermanagercl.shared.process.ComputationStatus;
import org.gcube.data.analysis.dataminermanagercl.shared.process.Operator;
import org.gcube.data.analysis.dataminermanagercl.shared.process.OperatorsClassification;
import org.gcube.data.analysis.dataminermanagercl.shared.workspace.DataMinerWorkArea;
import org.gcube.data.analysis.dataminermanagercl.shared.workspace.ItemDescription;
import org.gcube.data.analysis.dminvocation.model.DataMinerInvocation;
import org.gcube.data.analysis.dminvocation.model.DataMinerParam;
import org.gcube.portlets.user.dataminerexecutor.client.rpc.DataMinerExecutorService;
import org.gcube.portlets.user.dataminerexecutor.server.invocation.InvocationManager;
import org.gcube.portlets.user.dataminerexecutor.server.storage.StorageUtil;
import org.gcube.portlets.user.dataminerexecutor.server.util.DataMinerWorkAreaManager;
import org.gcube.portlets.user.dataminerexecutor.server.util.TableReader;
import org.gcube.portlets.user.dataminerexecutor.shared.exception.ServiceException;
import org.gcube.portlets.user.dataminerexecutor.shared.session.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
@SuppressWarnings("serial")
public class DataMinerExecutorServiceImpl extends RemoteServiceServlet implements DataMinerExecutorService {

	private static Logger logger = LoggerFactory.getLogger(DataMinerExecutorServiceImpl.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init() throws ServletException {
		super.init();
		logger.info("DataMinerExecutor started!");

	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public UserInfo hello() throws ServiceException {
		try {
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(this.getThreadLocalRequest());
			logger.debug("hello()");
			UserInfo userInfo = new UserInfo(serviceCredentials.getUserName(), serviceCredentials.getGroupId(),
					serviceCredentials.getGroupName(), serviceCredentials.getScope(), serviceCredentials.getEmail(),
					serviceCredentials.getFullName());
			logger.debug("UserInfo: " + userInfo);
			return userInfo;
		} catch (ServiceException e) {
			logger.error(e.getLocalizedMessage(), e);
			throw e;
		} catch (Throwable e) {
			logger.error("Hello(): " + e.getLocalizedMessage(), e);
			throw new ServiceException(e.getLocalizedMessage(), e);
		}

	}

	@Override
	public List<OperatorsClassification> getOperatorsClassifications(boolean refresh) throws ServiceException {

		try {
			HttpServletRequest httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);
			SClient smClient = SessionUtil.getSClient(httpRequest, serviceCredentials);
			List<OperatorsClassification> list = smClient.getOperatorsClassifications(refresh);
			return list;
		} catch (ServiceException e) {
			logger.error(e.getLocalizedMessage(), e);
			throw e;
		} catch (Throwable e) {
			logger.error("An error occurred getting the OperatorsClassifications list: " + e.getLocalizedMessage(), e);
			throw new ServiceException(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public List<Parameter> getParameters(Operator operator) throws ServiceException {
		try {
			HttpServletRequest httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);

			SClient smClient = SessionUtil.getSClient(httpRequest, serviceCredentials);

			List<Parameter> list = smClient.getInputParameters(operator);
			return list;
		} catch (ServiceException e) {
			logger.error(e.getLocalizedMessage());
			throw e;
		} catch (Throwable e) {
			logger.error("Error retrieving parameters: " + e.getLocalizedMessage(), e);
			throw new ServiceException(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public Operator getInvocationModel(String invocationModelFileUrl) throws ServiceException {
		try {
			HttpServletRequest httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);

			InvocationManager invocationManager = new InvocationManager();
			DataMinerInvocation dmInvocation = invocationManager.readInvocation(invocationModelFileUrl);
			if (dmInvocation == null || dmInvocation.getOperatorId() == null
					|| dmInvocation.getOperatorId().isEmpty()) {
				logger.error("Invalid Invocation: " + dmInvocation);
				throw new ServiceException("Invalid Invocation, operator id not found!");

			}

			if (dmInvocation.getParameters() == null || dmInvocation.getParameters().getInput() == null
					|| dmInvocation.getParameters().getInput().getListParam() == null
					|| dmInvocation.getParameters().getInput().getListParam().isEmpty()) {
				logger.error("Invalid Input Parameter: " + dmInvocation);
				throw new ServiceException("Invalid Input Parameter!");
			}

			SClient smClient = SessionUtil.getSClient(httpRequest, serviceCredentials);

			Operator operator = smClient.getOperatorById(dmInvocation.getOperatorId(),true);

			if (operator == null) {
				logger.error("Operator not found: " + dmInvocation);
				throw new ServiceException("Invalid Invocation, operator not found!");

			}

			List<Parameter> inputParameters = smClient.getInputParameters(operator);
			List<Parameter> inputParametersFilled = new ArrayList<Parameter>();
			List<DataMinerParam> invocationInputParams = dmInvocation.getParameters().getInput().getListParam();

			if (inputParameters != null && !inputParameters.isEmpty()) {
				for (Parameter p : inputParameters) {
					boolean foundParam = false;
					for (DataMinerParam dmP : invocationInputParams) {
						if (p.getName().compareTo(dmP.getKey()) == 0) {
							foundParam = true;
							p.setValue(dmP.getValue());
							inputParametersFilled.add(p);
							break;
						}
					}
					if (!foundParam) {
						logger.error("Invalid invocation, param " + p.getName() + " is not set in invocation model!");
						throw new ServiceException(
								"Invalid invocation, param " + p.getName() + " is not set in invocation model!");
					}

				}
			} else {
				logger.error("Invalid operator, parameters not found: " + operator);
				throw new ServiceException("Invalid operator, parameters not found!");
			}

			operator.setOperatorParameters(inputParametersFilled);

			return operator;
		} catch (ServiceException e) {
			logger.error(e.getLocalizedMessage());
			throw e;
		} catch (Throwable e) {
			logger.error("Error in getInvocationModel(): " + e.getLocalizedMessage(), e);
			throw new ServiceException(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public ComputationId startComputation(Operator operator) throws ServiceException {
		try {
			HttpServletRequest httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);

			SClient smClient = SessionUtil.getSClient(httpRequest, serviceCredentials);
			logger.debug("StartComputation(): [ operator=" + operator + "]");

			ComputationId computationId = smClient.startComputation(operator);
			return computationId;
		} catch (ServiceException e) {
			logger.error(e.getLocalizedMessage());
			throw e;
		} catch (Throwable e) {
			logger.error("Error in start computation: " + e.getLocalizedMessage(), e);
			throw new ServiceException(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public ComputationId resubmit(ItemDescription itemDescription) throws ServiceException {
		try {
			StorageUtil storageUtil = new StorageUtil();
			HttpServletRequest httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);
			Map<String, String> properties = storageUtil.getProperties(serviceCredentials.getUserName(),
					itemDescription.getId());
			logger.debug("Properties: " + properties);

			SClient smClient = SessionUtil.getSClient(httpRequest, serviceCredentials);

			ComputationId computationId = smClient.resubmitComputation(properties);
			return computationId;
		} catch (ServiceException e) {
			logger.error(e.getLocalizedMessage(), e);
			throw e;
		} catch (Throwable e) {
			logger.error("Error in resubmit computation: " + e.getLocalizedMessage(), e);
			throw new ServiceException(e.getLocalizedMessage());
		}
	}

	@Override
	public ComputationStatus getComputationStatus(ComputationId computationId) throws ServiceException {
		try {
			HttpServletRequest httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);
			SClient smClient = SessionUtil.getSClient(httpRequest, serviceCredentials);
			ComputationStatus computationStatus = smClient.getComputationStatus(computationId);
			return computationStatus;

		} catch (ServiceException e) {
			logger.error(e.getLocalizedMessage(), e);
			throw e;
		} catch (Throwable e) {
			logger.error("Error in getComputationStatus: " + e.getLocalizedMessage(), e);
			throw new ServiceException(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void deleteItem(ItemDescription itemDescription) throws ServiceException {
		try {
			HttpServletRequest httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);
			logger.debug("DeleteItem(): " + itemDescription);
			StorageUtil storageUtil = new StorageUtil();
			storageUtil.deleteItem(serviceCredentials.getUserName(), itemDescription.getId());
			return;

		} catch (ServiceException e) {
			logger.error(e.getLocalizedMessage(), e);
			throw e;
		} catch (Throwable e) {
			logger.error(e.getLocalizedMessage(), e);
			throw new ServiceException(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public TableItemSimple retrieveTableInformation(ItemDescription item) throws ServiceException {
		try {
			HttpServletRequest httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);
			logger.debug("retrieveTableInformation(): " + item);
			TableReader tableReader = new TableReader(serviceCredentials, item);
			TableItemSimple tableItemSimple = tableReader.getTableItemSimple();
			return tableItemSimple;
		} catch (ServiceException e) {
			logger.error(e.getLocalizedMessage(), e);
			throw e;
		} catch (Throwable e) {
			logger.error(e.getLocalizedMessage(), e);
			throw new ServiceException(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public DataMinerWorkArea getDataMinerWorkArea() throws ServiceException {
		try {
			HttpServletRequest httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);
			logger.debug("getDataMinerWorkArea()");
			DataMinerWorkAreaManager dataMinerWorkAreaManager = new DataMinerWorkAreaManager(serviceCredentials);
			return dataMinerWorkAreaManager.getDataMinerWorkArea();

		} catch (ServiceException e) {
			logger.error(e.getLocalizedMessage(), e);
			throw e;
		} catch (Throwable e) {
			logger.error(e.getLocalizedMessage(), e);
			throw new ServiceException(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public String getPublicLink(ItemDescription itemDescription) throws ServiceException {
		try {
			HttpServletRequest httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);
			logger.debug("GetPublicLink(): " + itemDescription);
			StorageUtil storageUtil = new StorageUtil();
			String link = storageUtil.getPublicLink(serviceCredentials.getUserName(), itemDescription.getId());

			return link;
		} catch (ServiceException e) {
			logger.error(e.getLocalizedMessage(), e);
			throw e;
		} catch (Throwable e) {
			logger.error(e.getLocalizedMessage(), e);
			throw new ServiceException(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public String cancelComputation(ComputationId computationId) throws ServiceException {
		try {
			HttpServletRequest httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);
			logger.debug("CancelComputation(): " + computationId);
			SClient smClient = SessionUtil.getSClient(httpRequest, serviceCredentials);
			String result = smClient.cancelComputation(computationId);
			// SessionUtil.putSClient(session, smClient);
			return result;
		} catch (ServiceException e) {
			logger.error(e.getLocalizedMessage(), e);
			throw e;
		} catch (Throwable e) {
			logger.error(e.getLocalizedMessage(), e);
			throw new ServiceException(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public OutputData getOutputDataByComputationId(ComputationId computationId) throws ServiceException {
		try {
			HttpServletRequest httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);
			Log.debug("getOutputDataByComputationId: " + computationId);
			SClient smClient = SessionUtil.getSClient(httpRequest, serviceCredentials);
			OutputData outputData = smClient.getOutputDataByComputationId(computationId);
			Log.debug("OutputData: " + outputData);
			return outputData;
		} catch (ServiceException e) {
			logger.error(e.getLocalizedMessage(), e);
			throw e;
		} catch (Throwable e) {
			logger.error("Error retrieving output by computation id: " + e.getLocalizedMessage(), e);
			throw new ServiceException(e.getLocalizedMessage(), e);
		}

	}

	@Override
	public ComputationData getComputationData(ItemDescription itemDescription) throws ServiceException {
		try {
			HttpServletRequest httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);
			logger.debug("OutputDataByComputationItemt(): " + itemDescription);
			StorageUtil storageUtil = new StorageUtil();
			Map<String, String> properties = storageUtil.getProperties(serviceCredentials.getUserName(),
					itemDescription.getId());

			logger.debug("Properties: " + properties);
			SClient smClient = SessionUtil.getSClient(httpRequest, serviceCredentials);

			ComputationData computationData = smClient.getComputationDataByComputationProperties(properties);
			return computationData;

		} catch (ServiceException e) {
			logger.error(e.getLocalizedMessage(), e);
			throw e;
		} catch (Throwable e) {
			logger.error("Error retrieving output by item: " + e.getLocalizedMessage(), e);
			throw new ServiceException(e.getLocalizedMessage());
		}
	}

	@Override
	public String cancelComputation(ItemDescription itemDescription) throws ServiceException {
		try {
			HttpServletRequest httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);
			logger.debug("CancelComputation(): " + itemDescription);
			String compId = itemDescription.getName();
			if (compId == null) {
				throw new ServiceException("Computation Id not found!");
			} else {
				int lastIndexUnd = compId.lastIndexOf("_");
				if (lastIndexUnd == -1) {
					throw new ServiceException("Invalid Computation Id: " + compId);
				} else {
					compId = compId.substring(lastIndexUnd + 1);
				}
			}
			ComputationId computationId = new ComputationId();
			computationId.setId(compId);
			SClient smClient = SessionUtil.getSClient(httpRequest, serviceCredentials);
			String result = smClient.cancelComputation(computationId);
			return result;
		} catch (ServiceException e) {
			logger.error(e.getLocalizedMessage(), e);
			throw e;
		} catch (Throwable e) {
			logger.error(e.getLocalizedMessage(), e);
			throw new ServiceException(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public ItemDescription getItemDescription(String itemId) throws ServiceException {
		try {
			HttpServletRequest httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest);
			logger.debug("getItemDescription(): [itemId=" + itemId + "]");
			StorageUtil storageUtil = new StorageUtil();
			ItemDescription itemDownloadInfo = storageUtil.getItemDescription(serviceCredentials.getUserName(), itemId);
			logger.debug("ItemDescription info: " + itemDownloadInfo);
			return itemDownloadInfo;
		} catch (ServiceException e) {
			logger.error(e.getLocalizedMessage(), e);
			throw e;
		} catch (Throwable e) {
			logger.error("Error retrieving item description: " + e.getLocalizedMessage(), e);
			throw new ServiceException("Error retrieving item description: " + e.getLocalizedMessage(), e);
		}
	}

}
