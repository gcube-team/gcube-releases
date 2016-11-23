package org.gcube.portlets.user.dataminermanager.server;

import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;

import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.portlets.user.dataminermanager.client.rpc.DataMinerPortletService;
import org.gcube.portlets.user.dataminermanager.server.dmservice.SClient;
import org.gcube.portlets.user.dataminermanager.server.storage.StorageUtil;
import org.gcube.portlets.user.dataminermanager.server.util.DataMinerWorkAreaManager;
import org.gcube.portlets.user.dataminermanager.server.util.TableReader;
import org.gcube.portlets.user.dataminermanager.shared.data.OutputData;
import org.gcube.portlets.user.dataminermanager.shared.data.TableItemSimple;
import org.gcube.portlets.user.dataminermanager.shared.data.computations.ComputationData;
import org.gcube.portlets.user.dataminermanager.shared.data.computations.ComputationId;
import org.gcube.portlets.user.dataminermanager.shared.exception.ServiceException;
import org.gcube.portlets.user.dataminermanager.shared.parameters.Parameter;
import org.gcube.portlets.user.dataminermanager.shared.process.ComputationStatus;
import org.gcube.portlets.user.dataminermanager.shared.process.Operator;
import org.gcube.portlets.user.dataminermanager.shared.process.OperatorsClassification;
import org.gcube.portlets.user.dataminermanager.shared.session.UserInfo;
import org.gcube.portlets.user.dataminermanager.shared.workspace.DataMinerWorkArea;
import org.gcube.portlets.user.dataminermanager.shared.workspace.ItemDescription;
//import org.gcube.portlets.user.tdw.server.datasource.DataSourceFactoryRegistry;
//import org.gcube.portlets.widgets.file_dw_import_wizard.server.file.TargetRegistry;
import org.gcube.portlets.widgets.wsexplorer.shared.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * 
 * @author Giancarlo Panichi email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
@SuppressWarnings("serial")
public class DataMinerManagerServiceImpl extends RemoteServiceServlet implements
		DataMinerPortletService {

	private static Logger logger = LoggerFactory
			.getLogger(DataMinerManagerServiceImpl.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init() throws ServletException {
		super.init();
		logger.info("DataMinerManager started!");

	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public UserInfo hello() throws ServiceException {
		try {
			HttpSession session = this.getThreadLocalRequest().getSession();
			ASLSession aslSession = SessionUtil.getASLSession(session);
			String token = SessionUtil.getToken(aslSession);
			UserInfo userInfo = new UserInfo(aslSession.getUsername(),
					aslSession.getGroupId(), aslSession.getGroupName(),
					aslSession.getScope(), aslSession.getScopeName(),
					aslSession.getUserEmailAddress(),
					aslSession.getUserFullName());
			logger.debug("UserInfo: " + userInfo);
			logger.debug("UserToken: " + token);
			return userInfo;
		} catch (ServiceException e) {
			e.printStackTrace();
			throw e;
		} catch (Throwable e) {
			e.printStackTrace();
			logger.error("Hello(): " + e.getLocalizedMessage(), e);
			throw new ServiceException(e.getLocalizedMessage(), e);
		}

	}

	@Override
	public List<OperatorsClassification> getOperatorsClassifications()
			throws ServiceException {

		try {
			HttpSession session = this.getThreadLocalRequest().getSession();
			ASLSession aslSession = SessionUtil.getASLSession(session);
			SClient smClient = SessionUtil.getSClient(aslSession, session);
			List<OperatorsClassification> list = smClient
					.getOperatorsClassifications();
			return list;
		} catch (ServiceException e) {
			logger.error(e.getLocalizedMessage());
			throw e;
		} catch (Throwable e) {
			logger.error(
					"An error occurred getting the OperatorsClassifications list: "
							+ e.getLocalizedMessage(), e);
			e.printStackTrace();
			throw new ServiceException(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public List<Parameter> getParameters(Operator operator)
			throws ServiceException {
		try {
			HttpSession session = this.getThreadLocalRequest().getSession();
			ASLSession aslSession = SessionUtil.getASLSession(session);

			SClient smClient = SessionUtil.getSClient(aslSession, session);
			List<Parameter> list = smClient.getInputParameters(operator);
			return list;
		} catch (ServiceException e) {
			logger.error(e.getLocalizedMessage());
			throw e;
		} catch (Throwable e) {
			logger.error("Error retrieving parameters: "
					+ e.getLocalizedMessage());
			e.printStackTrace();
			throw new ServiceException(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public ComputationId startComputation(Operator operator)
			throws ServiceException {
		try {
			HttpSession session = this.getThreadLocalRequest().getSession();
			ASLSession aslSession = SessionUtil.getASLSession(session);
			logger.debug("StartComputation(): [ operator=" + operator + "]");
			SClient smClient = SessionUtil.getSClient(aslSession, session);
			ComputationId computationId = smClient.startComputation(operator);
			return computationId;
		} catch (ServiceException e) {
			logger.error(e.getLocalizedMessage());
			throw e;
		} catch (Throwable e) {
			logger.error("Error in start computation: "
					+ e.getLocalizedMessage());
			e.printStackTrace();
			throw new ServiceException(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public ComputationId resubmit(ItemDescription itemDescription)
			throws ServiceException {
		try {
			HttpSession session = this.getThreadLocalRequest().getSession();
			ASLSession aslSession = SessionUtil.getASLSession(session);
			Map<String, String> properties = StorageUtil.getProperties(
					aslSession.getUsername(), itemDescription.getId());
			logger.debug("Properties: " + properties);
			SClient smClient = SessionUtil.getSClient(aslSession, session);
			ComputationId computationId = smClient
					.resubmitComputation(properties);
			return computationId;
		} catch (ServiceException e) {
			logger.error(e.getLocalizedMessage());
			throw e;
		} catch (Throwable e) {
			logger.error("Error in resubmit computation: "
					+ e.getLocalizedMessage());
			e.printStackTrace();
			throw new ServiceException(e.getLocalizedMessage());
		}
	}

	@Override
	public ComputationStatus getComputationStatus(ComputationId computationId)
			throws ServiceException {
		try {
			HttpSession session = this.getThreadLocalRequest().getSession();
			ASLSession aslSession = SessionUtil.getASLSession(session);
			SClient smClient = SessionUtil.getSClient(aslSession, session);
			ComputationStatus computationStatus = smClient
					.getComputationStatus(computationId);
			return computationStatus;

		} catch (ServiceException e) {
			logger.error(e.getLocalizedMessage());
			throw e;
		} catch (Throwable e) {
			logger.error("Error in getComputationStatus: "
					+ e.getLocalizedMessage());
			e.printStackTrace();
			throw new ServiceException(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void deleteItem(ItemDescription itemDescription)
			throws ServiceException {
		try {
			HttpSession session = this.getThreadLocalRequest().getSession();
			ASLSession aslSession = SessionUtil.getASLSession(session);
			String token = SessionUtil.getToken(aslSession);
			logger.debug("UserToken: " + token);
			logger.debug("DeleteItem(): " + itemDescription);
			StorageUtil.deleteItem(aslSession.getUsername(),
					itemDescription.getId());
			return;

		} catch (ServiceException e) {
			logger.error(e.getLocalizedMessage());
			throw e;
		} catch (Throwable e) {
			logger.error(e.getLocalizedMessage(), e);
			e.printStackTrace();
			throw new ServiceException(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public TableItemSimple retrieveTableInformation(Item item)
			throws ServiceException {
		try {
			HttpSession session = this.getThreadLocalRequest().getSession();
			ASLSession aslSession = SessionUtil.getASLSession(session);
			String token = SessionUtil.getToken(aslSession);
			logger.debug("UserToken: " + token);
			logger.debug("retrieveTableInformation(): " + item);
			TableReader tableReader = new TableReader(aslSession, item);
			TableItemSimple tableItemSimple = tableReader.getTableItemSimple();
			return tableItemSimple;
		} catch (ServiceException e) {
			logger.error(e.getLocalizedMessage());
			throw e;
		} catch (Throwable e) {
			logger.error(e.getLocalizedMessage(), e);
			e.printStackTrace();
			throw new ServiceException(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public DataMinerWorkArea getDataMinerWorkArea() throws ServiceException {
		try {
			HttpSession session = this.getThreadLocalRequest().getSession();
			ASLSession aslSession = SessionUtil.getASLSession(session);
			String token = SessionUtil.getToken(aslSession);
			logger.debug("UserToken: " + token);
			logger.debug("getDataMinerWorkArea()");
			DataMinerWorkAreaManager dataMinerWorkAreaManager = new DataMinerWorkAreaManager(
					aslSession);
			return dataMinerWorkAreaManager.getDataMinerWorkArea();

		} catch (ServiceException e) {
			logger.error(e.getLocalizedMessage());
			throw e;
		} catch (Throwable e) {
			logger.error(e.getLocalizedMessage(), e);
			e.printStackTrace();
			throw new ServiceException(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public String getPublicLink(ItemDescription itemDescription)
			throws ServiceException {
		try {
			HttpSession session = this.getThreadLocalRequest().getSession();
			ASLSession aslSession = SessionUtil.getASLSession(session);
			String token = SessionUtil.getToken(aslSession);
			logger.debug("UserToken: " + token);
			logger.debug("GetPublicLink(): " + itemDescription);
			String link = StorageUtil.getPublicLink(aslSession.getUsername(),
					itemDescription.getId());

			return link;
		} catch (ServiceException e) {
			logger.error(e.getLocalizedMessage());
			throw e;
		} catch (Throwable e) {
			logger.error(e.getLocalizedMessage(), e);
			e.printStackTrace();
			throw new ServiceException(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public String cancelComputation(ComputationId computationId)
			throws ServiceException {
		try {
			HttpSession session = this.getThreadLocalRequest().getSession();
			ASLSession aslSession = SessionUtil.getASLSession(session);
			logger.debug("CancelComputation(): " + computationId);
			SClient smClient = SessionUtil.getSClient(aslSession, session);
			String result = smClient.cancelComputation(computationId);
			// SessionUtil.putSClient(session, smClient);
			return result;
		} catch (ServiceException e) {
			logger.error(e.getLocalizedMessage());
			throw e;
		} catch (Throwable e) {
			logger.error(e.getLocalizedMessage(), e);
			e.printStackTrace();
			throw new ServiceException(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public OutputData getOutputDataByComputationId(ComputationId computationId)
			throws ServiceException {
		try {
			Log.debug("getOutputDataByComputationId: " + computationId);
			HttpSession session = this.getThreadLocalRequest().getSession();
			ASLSession aslSession = SessionUtil.getASLSession(session);
			SClient smClient = SessionUtil.getSClient(aslSession, session);
			OutputData outputData = smClient
					.getOutputDataByComputationId(computationId);
			Log.debug("OutputData: " + outputData);
			return outputData;
		} catch (ServiceException e) {
			logger.error(e.getLocalizedMessage());
			throw e;
		} catch (Throwable e) {
			logger.error("Error retrieving output by computation id: "
					+ e.getLocalizedMessage());
			e.printStackTrace();
			throw new ServiceException(e.getLocalizedMessage(), e);
		}

	}

	@Override
	public ComputationData getComputationData(ItemDescription itemDescription)
			throws ServiceException {
		try {
			HttpSession session = this.getThreadLocalRequest().getSession();
			ASLSession aslSession = SessionUtil.getASLSession(session);
			logger.debug("OutputDataByComputationItemt(): " + itemDescription);
			Map<String, String> properties = StorageUtil.getProperties(
					aslSession.getUsername(), itemDescription.getId());
			
			
			logger.debug("Properties: " + properties);
			SClient smClient = SessionUtil.getSClient(aslSession, session);
			ComputationData computationData = smClient
					.getComputationDataByComputationProperties(properties);
			return computationData;

		} catch (ServiceException e) {
			logger.error(e.getLocalizedMessage());
			throw e;
		} catch (Throwable e) {
			logger.error("Error retrieving output by item: "
					+ e.getLocalizedMessage());
			e.printStackTrace();
			throw new ServiceException(e.getLocalizedMessage());
		}
	}

	@Override
	public String cancelComputation(ItemDescription itemDescription)
			throws ServiceException {
		try {
			HttpSession session = this.getThreadLocalRequest().getSession();
			ASLSession aslSession = SessionUtil.getASLSession(session);
			logger.debug("CancelComputation(): " + itemDescription);
			String compId = itemDescription.getName();
			if (compId == null) {
				throw new ServiceException("Computation Id not found!");
			} else {
				int lastIndexUnd = compId.lastIndexOf("_");
				if (lastIndexUnd == -1) {
					throw new ServiceException("Invalid Computation Id: "
							+ compId);
				} else {
					compId = compId.substring(lastIndexUnd + 1);
				}
			}
			ComputationId computationId = new ComputationId();
			computationId.setId(compId);
			SClient smClient = SessionUtil.getSClient(aslSession, session);
			String result = smClient.cancelComputation(computationId);
			return result;
		} catch (ServiceException e) {
			logger.error(e.getLocalizedMessage());
			throw e;
		} catch (Throwable e) {
			logger.error(e.getLocalizedMessage(), e);
			e.printStackTrace();
			throw new ServiceException(e.getLocalizedMessage(), e);
		}
	}

}
