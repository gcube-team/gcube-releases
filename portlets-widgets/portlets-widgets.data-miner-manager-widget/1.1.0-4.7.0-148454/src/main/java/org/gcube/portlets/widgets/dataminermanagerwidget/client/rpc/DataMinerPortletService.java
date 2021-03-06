package org.gcube.portlets.widgets.dataminermanagerwidget.client.rpc;

import java.util.List;

import org.gcube.data.analysis.dataminermanagercl.shared.data.OutputData;
import org.gcube.data.analysis.dataminermanagercl.shared.data.TableItemSimple;
import org.gcube.data.analysis.dataminermanagercl.shared.data.computations.ComputationData;
import org.gcube.data.analysis.dataminermanagercl.shared.data.computations.ComputationId;
import org.gcube.data.analysis.dataminermanagercl.shared.parameters.Parameter;
import org.gcube.data.analysis.dataminermanagercl.shared.process.ComputationStatus;
import org.gcube.data.analysis.dataminermanagercl.shared.process.Operator;
import org.gcube.data.analysis.dataminermanagercl.shared.process.OperatorsClassification;
import org.gcube.portlets.widgets.dataminermanagerwidget.shared.exception.ServiceException;
import org.gcube.portlets.widgets.dataminermanagerwidget.shared.session.UserInfo;
import org.gcube.portlets.widgets.dataminermanagerwidget.shared.workspace.DataMinerWorkArea;
import org.gcube.portlets.widgets.dataminermanagerwidget.shared.workspace.ItemDescription;
import org.gcube.portlets.widgets.wsexplorer.shared.Item;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("dataminermanagerwidget")
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
