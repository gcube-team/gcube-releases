package org.gcube.portlets.user.dataminermanager.client.rpc;

import java.util.List;

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
import org.gcube.portlets.widgets.wsexplorer.shared.Item;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("dataminerman")
public interface DataMinerPortletService extends RemoteService {

	public UserInfo hello() throws ServiceException;

	public List<OperatorsClassification> getOperatorsClassifications()
			throws ServiceException;

	public List<Parameter> getParameters(Operator operator) throws ServiceException;

	public ComputationId startComputation(Operator op) throws ServiceException;

	public ComputationStatus getComputationStatus(ComputationId computationId)
			throws ServiceException;

	public ComputationId resubmit(ItemDescription itemDescription) throws ServiceException;

	
	public TableItemSimple retrieveTableInformation(Item item)
			throws ServiceException;

	public DataMinerWorkArea getDataMinerWorkArea() throws ServiceException;

	public String getPublicLink(ItemDescription itemDescription)
			throws ServiceException;

	public String cancelComputation(ComputationId computationId) throws ServiceException;

	public void deleteItem(ItemDescription itemDescription)
			throws ServiceException;
	
	public OutputData getOutputDataByComputationId(ComputationId computationId) throws ServiceException;
	
	public ComputationData getComputationData(ItemDescription itemDescription) throws ServiceException;

	public String cancelComputation(ItemDescription itemDescription) throws ServiceException;
	
}
