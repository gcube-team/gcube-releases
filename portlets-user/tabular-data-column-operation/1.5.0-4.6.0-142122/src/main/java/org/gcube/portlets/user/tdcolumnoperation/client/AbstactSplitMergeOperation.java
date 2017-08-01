/**
 * 
 */
package org.gcube.portlets.user.tdcolumnoperation.client;

import java.util.List;

import org.gcube.portlets.user.td.expressionwidget.client.store.ColumnDataTypeElement;
import org.gcube.portlets.user.td.expressionwidget.client.store.ColumnTypeCodeElement;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTIsFinalException;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTIsLockedException;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTSessionExpiredException;
import org.gcube.portlets.user.td.monitorwidget.client.MonitorDialog;
import org.gcube.portlets.user.td.monitorwidget.client.MonitorDialogListener;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.SessionExpiredEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.SessionExpiredType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnDataType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnTypeCode;
import org.gcube.portlets.user.tdcolumnoperation.client.properties.OperatorPropertiesCombo;
import org.gcube.portlets.user.tdcolumnoperation.client.rpc.TdColumnOperationServiceAsync;
import org.gcube.portlets.user.tdcolumnoperation.client.utils.UtilsGXT3;
import org.gcube.portlets.user.tdcolumnoperation.shared.OperationID;
import org.gcube.portlets.user.tdcolumnoperation.shared.SplitAndMergeColumnSession;
import org.gcube.portlets.user.tdcolumnoperation.shared.TdBaseComboDataBean;
import org.gcube.portlets.user.tdcolumnoperation.shared.TdOperatorComboOperator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.data.client.loader.RpcProxy;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.loader.ListLoadConfig;
import com.sencha.gxt.data.shared.loader.ListLoadConfigBean;
import com.sencha.gxt.data.shared.loader.ListLoadResult;
import com.sencha.gxt.data.shared.loader.ListLoadResultBean;
import com.sencha.gxt.data.shared.loader.ListLoader;
import com.sencha.gxt.data.shared.loader.LoadResultListStoreBinding;
import com.sencha.gxt.widget.core.client.form.ComboBox;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jun 3, 2014
 * 
 */
public abstract class AbstactSplitMergeOperation {

	protected SplitAndMergeColumnSession operationColumnSession;
	protected OperationID operationID;

	protected abstract void initAbstactSplitMergeOperation(TRId trId,
			OperationID operationID, EventBus bus) throws Exception;

	protected abstract void updateComboOperatorStatus(TdOperatorComboOperator operator);

	protected abstract void setListener(MonitorDialogListener listener);
	protected MonitorDialogListener progressListener;
	
	protected TdColumnOperationServiceAsync serviceAsync = TdColumnOperationServiceAsync.Util
			.getInstance();
	protected EventBus eventBus;
	protected ComboBox<TdOperatorComboOperator> comboOperator = null;
	protected TRId trId = null;
	private ListStore<TdOperatorComboOperator> storeCombo;
	


	protected void doOperationSubmit(String fieldValue,
			TdOperatorComboOperator operator, ColumnData firstColumnData,
			ColumnData secColumnData, String firstColumnLabel,
			String secondColumnLabel,
			ColumnTypeCodeElement columnTypeCodeElement,
			ColumnTypeCodeElement columnTypeCodeElement2,
			ColumnDataTypeElement selectedDataType1,
			ColumnDataTypeElement selectedDataType2, boolean deleteSourceColumn) {

		String value = fieldValue;
		if (value != null) {
			operationColumnSession = new SplitAndMergeColumnSession(operationID);
			operationColumnSession.setOperator(operator);
			operationColumnSession.setValue(value);
			operationColumnSession.setFirstColumnData(firstColumnData);
			operationColumnSession.setLabelColumn1(firstColumnLabel);
			operationColumnSession.setLabelColumn2(secondColumnLabel);
			operationColumnSession.setColumnType1(columnTypeCodeElement
					.getCode());

			if (columnTypeCodeElement2 != null)
				operationColumnSession.setColumnType2(columnTypeCodeElement2
						.getCode());

			operationColumnSession.setDataType1(selectedDataType1.getType());

			operationColumnSession.setDeleteSourceColumn(deleteSourceColumn);

			if (selectedDataType2 != null)
				operationColumnSession
						.setDataType2(selectedDataType2.getType());
			if (operationID.equals(OperationID.SPLIT)) {
				callStartSplitAndMergeOperation();
			} else if (operationID.equals(OperationID.MERGE)) {
				operationColumnSession.setSecondColumnData(secColumnData);
				callStartSplitAndMergeOperation();
			}
		} else {
			UtilsGXT3.alert("Attention", "Insert a valid value!");
		}
		// } else {
		// UtilsGXT3.alert("Attention", "Select a column!");
		// }
	}

	private void callStartSplitAndMergeOperation() {

		TdColumnOperationServiceAsync.Util.getInstance().startSplitAndMergeOperation(operationColumnSession,new AsyncCallback<String>() {

							@Override
							public void onFailure(Throwable caught) {
								if (caught instanceof TDGWTSessionExpiredException) {
									eventBus.fireEvent(new SessionExpiredEvent(
											SessionExpiredType.EXPIREDONSERVER));
								} else {
									if (caught instanceof TDGWTIsLockedException) {
										GWT.log(caught.getLocalizedMessage());
										UtilsGXT3.alert("Error Locked",
												caught.getLocalizedMessage());
									} else {
										if (caught instanceof TDGWTIsFinalException) {
											GWT.log(caught
													.getLocalizedMessage());
											UtilsGXT3
													.alert("Error Final",
															caught.getLocalizedMessage());
										} else {
											GWT.log("Error in operation : "
													+ caught.getLocalizedMessage());
											UtilsGXT3.alert("Error in operation :",
											"Sorry an error occurred on starting operation, try again later!");
										}
									}
								}

							}

							@Override
							public void onSuccess(String result) {
								GWT.log("Return task id: "+result);
								if (operationID.equals(OperationID.SPLIT)) {

//									SplitColumnProgressDialog splitColumnProgressDialog = new SplitColumnProgressDialog(
//											eventBus);
//									splitColumnProgressDialog.addProgressDialogListener(progressListener);
//									splitColumnProgressDialog.show();

								} else if (operationID.equals(OperationID.MERGE)) {
//
//									MergeColumnProgressDialog mergePrgDlg = new MergeColumnProgressDialog(
//											eventBus);
//									mergePrgDlg
//											.addProgressDialogListener(progressListener);
//									mergePrgDlg.show();
								}
								
								openMonitorDialog(result);
							}
						});
	}

	/**
	 * 
	 */
	protected void initComboOperatorForOperationId() {

		// Column Data
		OperatorPropertiesCombo propsOperatorCombo = GWT
				.create(OperatorPropertiesCombo.class);
		storeCombo = new ListStore<TdOperatorComboOperator>(
				propsOperatorCombo.id());

		GWT.log("StoreComboOperator created");

		RpcProxy<ListLoadConfig, ListLoadResult<TdOperatorComboOperator>> proxy = new RpcProxy<ListLoadConfig, ListLoadResult<TdOperatorComboOperator>>() {

			public void load(
					ListLoadConfig loadConfig,
					final AsyncCallback<ListLoadResult<TdOperatorComboOperator>> callback) {
				loadDataForOperation(loadConfig, callback);
			}

		};

		final ListLoader<ListLoadConfig, ListLoadResult<TdOperatorComboOperator>> loader = new ListLoader<ListLoadConfig, ListLoadResult<TdOperatorComboOperator>>(
				proxy) {
			@Override
			protected ListLoadConfig newLoadConfig() {
				return (ListLoadConfig) new ListLoadConfigBean();
			}

		};

		loader.addLoadHandler(new LoadResultListStoreBinding<ListLoadConfig, TdOperatorComboOperator, ListLoadResult<TdOperatorComboOperator>>(
				storeCombo));
		GWT.log("LoaderComboOperator created");

		comboOperator = new ComboBox<TdOperatorComboOperator>(storeCombo,
				propsOperatorCombo.label()) {

			protected void onAfterFirstAttach() {
				super.onAfterFirstAttach();
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					public void execute() {
						loader.load();
					}
				});
			}
		};

		GWT.log("Combo Operator created");

		addHandlersForComboOperator(propsOperatorCombo.label());

		comboOperator.setLoader(loader);
		comboOperator.setEmptyText("Select an operator...");
		comboOperator.setWidth(150);
		comboOperator.setTypeAhead(false);
		comboOperator.setEditable(false);
		comboOperator.setTriggerAction(TriggerAction.ALL);
	}

	protected void addHandlersForComboOperator(
			final LabelProvider<TdBaseComboDataBean> labelProvider) {
		comboOperator
				.addSelectionHandler(new SelectionHandler<TdOperatorComboOperator>() {
					public void onSelection(
							SelectionEvent<TdOperatorComboOperator> event) {
						/*
						 * Info.display( "Operator Selected", "You selected " +
						 * (event.getSelectedItem() == null ? "nothing" :
						 * labelProvider.getLabel(event .getSelectedItem()) +
						 * "!")); GWT.log("Operator selected: " +
						 * event.getSelectedItem());
						 */
						TdOperatorComboOperator operator = event
								.getSelectedItem();
						updateComboOperatorStatus(operator);
					}

				});
	}

	private void loadDataForOperation(
			ListLoadConfig loadConfig,
			final AsyncCallback<ListLoadResult<TdOperatorComboOperator>> callback) {

		GWT.log("loadDataForOperation " + operationID);
		serviceAsync.loadOperatorForOperationId(operationID,
				new AsyncCallback<List<TdOperatorComboOperator>>() {

					@Override
					public void onFailure(Throwable caught) {
						if (caught instanceof TDGWTSessionExpiredException) {
							eventBus.fireEvent(new SessionExpiredEvent(
									SessionExpiredType.EXPIREDONSERVER));
						} else {
							if (caught instanceof TDGWTIsLockedException) {
								GWT.log(caught.getLocalizedMessage());
								UtilsGXT3.alert("Error Locked",
										caught.getLocalizedMessage());
							} else {
								if (caught instanceof TDGWTIsFinalException) {
									GWT.log(caught
											.getLocalizedMessage());
									UtilsGXT3
											.alert("Error Final",
													caught.getLocalizedMessage());
								} else {
									GWT.log("Error in operation : "
											+ caught.getLocalizedMessage());
									UtilsGXT3.alert("Error in operation :",
											"Sorry an error occurred on getting operation, try again later!");
								}
							}
						}

						callback.onFailure(caught);

					}

					@Override
					public void onSuccess(List<TdOperatorComboOperator> result) {

						GWT.log("loaded loadDataForOperation has size: "
								+ result.size());
						// setComboStatus(result);
						callback.onSuccess(new ListLoadResultBean<TdOperatorComboOperator>(
								result));

					}
				});
	}

	public ListStore<TdOperatorComboOperator> getStoreCombo() {
		return storeCombo;
	}

	/**
	 * 
	 * @return the first element of store or null if it doesn't exist
	 */
	public TdOperatorComboOperator getDefaultValue() {
		if (storeCombo != null && storeCombo.get(0) != null)
			return storeCombo.get(0);

		return null;
	}
	
	protected void openMonitorDialog(String taskId) {
		MonitorDialog monitorDialog = new MonitorDialog(taskId, eventBus);
		monitorDialog.addProgressDialogListener(progressListener);
		monitorDialog.show();
	}
	
	
	/**
	 * 
	 * @param combo
	 * @param typeCode
	 * 
	 * IF input typeCode doesn't exist set value as ANNOTATION if find
	 */
	protected void setColumnTypeSelectedValue(ComboBox<ColumnTypeCodeElement> combo, ColumnTypeCode typeCode){
		
		for (ColumnTypeCodeElement type  : combo.getStore().getAll()) {
			
			if(type.getCode().equals(typeCode)){
				combo.setValue(type);
				GWT.log("combo ColumnType selected as: "+type);
				return;
			}
		}
		
		//IF ColumnTypeCode doesn't exist SELECT ANNOTATION if find
		for (ColumnTypeCodeElement type  : combo.getStore().getAll()) {
			if(type.getCode().equals(ColumnTypeCode.ANNOTATION)){
				combo.setValue(type);
				GWT.log("combo ColumnType selected as Annotation");
				return;
			}
		}
		
	}
	
	/**
	 * 
	 * @param combo
	 * @param dataType
	 * 
	 * IF input dataType doesn't exist set value as Text if find
	 */
	protected void setDataTypeSelectedValue(ComboBox<ColumnDataTypeElement> combo, ColumnDataType dataType){
		
		for (ColumnDataTypeElement type  : combo.getStore().getAll()) {
			
			if(type.getType().equals(dataType)){
				combo.setValue(type);
				GWT.log("combo DataType selected as: "+type);
				return;
			}
		}
		
		//IF ColumnTypeCode doesn't exist SELECT Text if find
		for (ColumnDataTypeElement type  : combo.getStore().getAll()) {
			if(type.getType().equals(ColumnDataType.Text)){
				combo.setValue(type);
				GWT.log("combo DataType selected as: "+ColumnDataType.Text);
				return;
			}
		}
	}
}
