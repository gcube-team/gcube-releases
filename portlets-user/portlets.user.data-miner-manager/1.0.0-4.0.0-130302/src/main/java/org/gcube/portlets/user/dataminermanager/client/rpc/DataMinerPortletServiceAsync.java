package org.gcube.portlets.user.dataminermanager.client.rpc;

import java.util.List;

import org.gcube.portlets.user.dataminermanager.client.bean.ComputationStatus;
import org.gcube.portlets.user.dataminermanager.client.bean.Operator;
import org.gcube.portlets.user.dataminermanager.client.bean.OperatorsClassification;
import org.gcube.portlets.user.dataminermanager.shared.data.OutputData;
import org.gcube.portlets.user.dataminermanager.shared.data.TableItemSimple;
import org.gcube.portlets.user.dataminermanager.shared.data.computations.ComputationData;
import org.gcube.portlets.user.dataminermanager.shared.data.computations.ComputationId;
import org.gcube.portlets.user.dataminermanager.shared.parameters.Parameter;
import org.gcube.portlets.user.dataminermanager.shared.session.UserInfo;
import org.gcube.portlets.user.dataminermanager.shared.workspace.DataMinerWorkArea;
import org.gcube.portlets.user.dataminermanager.shared.workspace.ItemDescription;
import org.gcube.portlets.widgets.wsexplorer.shared.Item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * 
 * @author Giancarlo Panichi email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public interface DataMinerPortletServiceAsync {

	public static DataMinerPortletServiceAsync INSTANCE = (DataMinerPortletServiceAsync) GWT
			.create(DataMinerPortletService.class);

	void hello(AsyncCallback<UserInfo> callback);

	void getOperatorsClassifications(
			AsyncCallback<List<OperatorsClassification>> callback);

	void getParameters(Operator operator,
			AsyncCallback<List<Parameter>> callback);

	void startComputation(Operator op,
			AsyncCallback<ComputationId> asyncCallback);

	void getComputationStatus(ComputationId computationId,
			AsyncCallback<ComputationStatus> asyncCallback);

	void resubmit(ItemDescription itemDescription,
			AsyncCallback<ComputationId> callback);

	void retrieveTableInformation(Item item,
			AsyncCallback<TableItemSimple> callback);

	void getDataMinerWorkArea(AsyncCallback<DataMinerWorkArea> asyncCallback);

	void getPublicLink(ItemDescription itemDescription,
			AsyncCallback<String> callback);

	void cancelComputation(ComputationId computationId,
			AsyncCallback<String> asyncCallback);

	void deleteItem(ItemDescription itemDescription,
			AsyncCallback<Void> callback);

	void getOutputDataByComputationId(ComputationId computationId,
			AsyncCallback<OutputData> callback);

	void getComputationData(ItemDescription itemDescription,
			AsyncCallback<ComputationData> callback);

	void cancelComputation(ItemDescription itemDescription,
			AsyncCallback<String> asyncCallback);

}
