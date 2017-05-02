package org.gcube.portlets.widgets.dataminermanagerwidget.client;

import java.util.List;

import org.gcube.data.analysis.dataminermanagercl.shared.data.OutputData;
import org.gcube.data.analysis.dataminermanagercl.shared.data.computations.ComputationData;
import org.gcube.data.analysis.dataminermanagercl.shared.data.computations.ComputationId;
import org.gcube.data.analysis.dataminermanagercl.shared.process.OperatorsClassification;
import org.gcube.portlets.widgets.dataminermanagerwidget.client.common.EventBusProvider;
import org.gcube.portlets.widgets.dataminermanagerwidget.client.events.CancelComputationExecutionRequestEvent;
import org.gcube.portlets.widgets.dataminermanagerwidget.client.events.CancelExecutionFromComputationsRequestEvent;
import org.gcube.portlets.widgets.dataminermanagerwidget.client.events.ComputationDataEvent;
import org.gcube.portlets.widgets.dataminermanagerwidget.client.events.ComputationDataRequestEvent;
import org.gcube.portlets.widgets.dataminermanagerwidget.client.events.DataMinerWorkAreaEvent;
import org.gcube.portlets.widgets.dataminermanagerwidget.client.events.DataMinerWorkAreaRequestEvent;
import org.gcube.portlets.widgets.dataminermanagerwidget.client.events.DeleteItemRequestEvent;
import org.gcube.portlets.widgets.dataminermanagerwidget.client.events.ExternalExecutionRequestEvent;
import org.gcube.portlets.widgets.dataminermanagerwidget.client.events.MenuEvent;
import org.gcube.portlets.widgets.dataminermanagerwidget.client.events.MenuSwitchEvent;
import org.gcube.portlets.widgets.dataminermanagerwidget.client.events.OperatorsClassificationEvent;
import org.gcube.portlets.widgets.dataminermanagerwidget.client.events.OperatorsClassificationRequestEvent;
import org.gcube.portlets.widgets.dataminermanagerwidget.client.events.OutputDataEvent;
import org.gcube.portlets.widgets.dataminermanagerwidget.client.events.OutputDataRequestEvent;
import org.gcube.portlets.widgets.dataminermanagerwidget.client.events.RefreshDataMinerWorkAreaEvent;
import org.gcube.portlets.widgets.dataminermanagerwidget.client.events.ResubmitComputationExecutionEvent;
import org.gcube.portlets.widgets.dataminermanagerwidget.client.events.ResubmitComputationExecutionRequestEvent;
import org.gcube.portlets.widgets.dataminermanagerwidget.client.events.SessionExpiredEvent;
import org.gcube.portlets.widgets.dataminermanagerwidget.client.events.StartComputationExecutionRequestEvent;
import org.gcube.portlets.widgets.dataminermanagerwidget.client.events.TabularResourceInfoEvent;
import org.gcube.portlets.widgets.dataminermanagerwidget.client.events.TabularResourceInfoRequestEvent;
import org.gcube.portlets.widgets.dataminermanagerwidget.client.monitor.StatusMonitor;
import org.gcube.portlets.widgets.dataminermanagerwidget.client.rpc.DataMinerPortletServiceAsync;
import org.gcube.portlets.widgets.dataminermanagerwidget.client.tr.TabularResourceData;
import org.gcube.portlets.widgets.dataminermanagerwidget.client.type.DataMinerWorkAreaElementType;
import org.gcube.portlets.widgets.dataminermanagerwidget.client.type.DataMinerWorkAreaEventType;
import org.gcube.portlets.widgets.dataminermanagerwidget.client.type.MenuType;
import org.gcube.portlets.widgets.dataminermanagerwidget.client.util.InfoMessageBox;
import org.gcube.portlets.widgets.dataminermanagerwidget.client.util.UtilsGXT3;
import org.gcube.portlets.widgets.dataminermanagerwidget.shared.Constants;
import org.gcube.portlets.widgets.dataminermanagerwidget.shared.exception.SessionExpiredServiceException;
import org.gcube.portlets.widgets.dataminermanagerwidget.shared.session.UserInfo;
import org.gcube.portlets.widgets.dataminermanagerwidget.shared.workspace.DataMinerWorkArea;
import org.gcube.portlets.widgets.dataminermanagerwidget.shared.workspace.ItemDescription;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;

/**
 * 
 * @author Giancarlo Panichi email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class DataMinerManagerController {
	private UserInfo userInfo;
	private DataMinerWorkArea dataMinerWorkArea;
	private List<OperatorsClassification> operatorsClassifications;
	private MenuType currentVisualization;
	// private String operatorId;
	private TabularResourceData tabularResourceData;

	public DataMinerManagerController() {
		init();
	}

	public UserInfo getUserInfo() {
		return userInfo;
	}

	// public String getOperatorId(){
	// return operatorId;
	// }

	public DataMinerWorkArea getDataMinerWorkArea() {
		return dataMinerWorkArea;
	}

	public void setTabularResourceData(TabularResourceData tabularResourceData) {
		this.tabularResourceData = tabularResourceData;
	}

	private void init() {
		currentVisualization = MenuType.EXPERIMENT;
		restoreUISession();
		bind();
		callHello();
		checkSession();
	}

	private void checkSession() {
		// if you do not need to something when the session expire
		// CheckSession.getInstance().startPolling();
	}

	private void sessionExpiredShow() {
		// CheckSession.showLogoutDialog();
	}

	/*
	 * private void sessionExpiredShowDelayed() { Timer timeoutTimer = new
	 * Timer() { public void run() { sessionExpiredShow();
	 * 
	 * } }; int TIMEOUT = 3; // 3 second timeout
	 * 
	 * timeoutTimer.schedule(TIMEOUT * 1000); // timeout is in milliseconds
	 * 
	 * }
	 */

	private void bind() {

		EventBusProvider.INSTANCE.addHandler(SessionExpiredEvent.TYPE,
				new SessionExpiredEvent.SessionExpiredEventHandler() {

					@Override
					public void onChange(SessionExpiredEvent event) {
						Log.debug("Catch SessionExpiredEvent");
						sessionExpiredShow();

					}
				});

		EventBusProvider.INSTANCE.addHandler(MenuEvent.TYPE,
				new MenuEvent.MenuEventHandler() {

					@Override
					public void onSelect(MenuEvent event) {
						Log.debug("Catch MenuEvent:" + event);
						manageMenuEvent(event);

					}
				});

		EventBusProvider.INSTANCE
				.addHandler(
						StartComputationExecutionRequestEvent.TYPE,
						new StartComputationExecutionRequestEvent.StartComputationExecutionRequestEventHandler() {

							@Override
							public void onStart(
									StartComputationExecutionRequestEvent event) {
								Log.debug("Catch StartComputationExecutionRequestEvent In Controller: "
										+ event);
								ExternalExecutionRequestEvent ev=new ExternalExecutionRequestEvent(event.getOp());
								EventBusProvider.INSTANCE.fireEvent(ev);
								//startComputationRequest(event);

							}
						});

		EventBusProvider.INSTANCE
				.addHandler(
						CancelComputationExecutionRequestEvent.TYPE,
						new CancelComputationExecutionRequestEvent.CancelComputationExecutionRequestEventHandler() {

							@Override
							public void onCancel(
									CancelComputationExecutionRequestEvent event) {
								Log.debug("Catch CancelComputationRequestEvent: "
										+ event);
								cancelComputationRequest(event);

							}
						});

		EventBusProvider.INSTANCE
				.addHandler(
						CancelExecutionFromComputationsRequestEvent.TYPE,
						new CancelExecutionFromComputationsRequestEvent.CancelExecutionFromComputationsRequestEventHandler() {

							@Override
							public void onCancel(
									CancelExecutionFromComputationsRequestEvent event) {
								Log.debug("Catch CancelExecutionFromComputationsRequestEvent: "
										+ event);
								cancelExecutionFromComputationsRequest(event);

							}

						});

		EventBusProvider.INSTANCE
				.addHandler(
						ResubmitComputationExecutionRequestEvent.TYPE,
						new ResubmitComputationExecutionRequestEvent.ResubmitComputationExecutionRequestEventHandler() {

							@Override
							public void onResubmit(
									ResubmitComputationExecutionRequestEvent event) {
								Log.debug("Catch ResubmitComputationExecutionRequestEvent: "
										+ event);
								resubmitComputationRequest(event);

							}

						});

		EventBusProvider.INSTANCE.addHandler(OutputDataRequestEvent.TYPE,
				new OutputDataRequestEvent.OutputDataRequestEventHandler() {

					@Override
					public void onOutputRequest(OutputDataRequestEvent event) {
						Log.debug("Catch OutputDataRequestEvent: " + event);
						manageOutputDataRequestEvent(event);

					}

				});

		EventBusProvider.INSTANCE
				.addHandler(
						ComputationDataRequestEvent.TYPE,
						new ComputationDataRequestEvent.ComputationDataRequestEventHandler() {

							@Override
							public void onComputationDataRequest(
									ComputationDataRequestEvent event) {
								Log.debug("Catch ComputationDataRequestEvent: "
										+ event);
								manageComputationDataRequestEvent(event);

							}

						});

		EventBusProvider.INSTANCE
				.addHandler(
						OperatorsClassificationRequestEvent.TYPE,
						new OperatorsClassificationRequestEvent.OperatorsClassificationRequestEventHandler() {

							@Override
							public void onRequest(
									OperatorsClassificationRequestEvent event) {
								Log.debug("Catch OperatorsClassificationRequestEvent: "
										+ event);
								operatorsClassificationRequest(event);

							}

						});

		EventBusProvider.INSTANCE.addHandler(DeleteItemRequestEvent.TYPE,
				new DeleteItemRequestEvent.DeleteItemRequestEventHandler() {

					@Override
					public void onDeleteRequest(DeleteItemRequestEvent event) {
						Log.debug("Catch DeleteItemRequestEvent: " + event);
						deleteItemRequest(event);

					}

				});

		EventBusProvider.INSTANCE
				.addHandler(
						DataMinerWorkAreaRequestEvent.TYPE,
						new DataMinerWorkAreaRequestEvent.DataMinerWorkAreaRequestEventHandler() {

							@Override
							public void onRequest(
									DataMinerWorkAreaRequestEvent event) {
								Log.debug("Catch DataMinerWorkAreaRequestEvent: "
										+ event);
								retrieveDataMinerWorkArea(event);

							}

						});

		EventBusProvider.INSTANCE
				.addHandler(
						TabularResourceInfoRequestEvent.TYPE,
						new TabularResourceInfoRequestEvent.TabularResourceInfoRequestEventHandler() {

							@Override
							public void onRequest(
									TabularResourceInfoRequestEvent event) {
								Log.debug("Catch TabularResourceInfoRequestEvent: "
										+ event);
								TabularResourceInfoEvent ev = new TabularResourceInfoEvent(
										tabularResourceData);
								EventBusProvider.INSTANCE.fireEvent(ev);

							}

						});

	}

	private void restoreUISession() {
		// checkLocale();
		// operatorId = com.google.gwt.user.client.Window.Location
		// .getParameter(Constants.DATA_MINER_OPERATOR_ID);
	}

	private void callHello() {

		DataMinerPortletServiceAsync.INSTANCE
				.hello(new AsyncCallback<UserInfo>() {

					@Override
					public void onFailure(Throwable caught) {
						if (caught instanceof SessionExpiredServiceException) {
							UtilsGXT3.alert("Error", "Expired Session!");
							EventBusProvider.INSTANCE
									.fireEvent(new SessionExpiredEvent());
						} else {
							UtilsGXT3.alert(
									"Error",
									"No user found: "
											+ caught.getLocalizedMessage());
						}
					}

					@Override
					public void onSuccess(UserInfo result) {
						userInfo = result;
						Log.info("Hello: " + userInfo.getUsername());
					}

				});

	}

	private void manageMenuEvent(MenuEvent event) {
		Log.debug("CurrentVisualization=" + currentVisualization);
		if (event == null
				|| event.getMenuType() == null
				|| (currentVisualization == null && event.getMenuType()
						.compareTo(MenuType.HOME) == 0)
				|| (currentVisualization != null && event.getMenuType()
						.compareTo(currentVisualization) == 0)) {

			return;
		}
		currentVisualization = event.getMenuType();
		MenuSwitchEvent menuSwitchEvent = new MenuSwitchEvent(
				event.getMenuType());
		EventBusProvider.INSTANCE.fireEvent(menuSwitchEvent);
	}

	/*
	private void startComputationRequest(
			final StartComputationExecutionRequestEvent event) {
		DataMinerPortletServiceAsync.INSTANCE.startComputation(event.getOp(),
				new AsyncCallback<ComputationId>() {

					@Override
					public void onFailure(Throwable caught) {
						if (caught instanceof SessionExpiredServiceException) {
							UtilsGXT3.alert("Error", "Expired Session!");
							EventBusProvider.INSTANCE
									.fireEvent(new SessionExpiredEvent());
						} else {
							UtilsGXT3.alert("Error",
									"Failed start computation "
											+ event.getOp().getName() + "! "
											+ caught.getLocalizedMessage());
							caught.printStackTrace();
						}
					}

					@Override
					public void onSuccess(ComputationId computationId) {
						if (computationId == null)
							UtilsGXT3.alert("Error",
									"Failed start computation "
											+ event.getOp().getName()
											+ ", the computation id is null!");
						else {
							startComputation(computationId,
									event.getComputationStatusPanelIndex());
						}
					}
				});

	}

	private void startComputation(ComputationId computationId,
			int computationStatusPanelIndex) {
		StartComputationExecutionEvent event = new StartComputationExecutionEvent(
				computationId, computationStatusPanelIndex);
		EventBusProvider.INSTANCE.fireEvent(event);
	}*/

	private void cancelExecutionFromComputationsRequest(
			CancelExecutionFromComputationsRequestEvent event) {
		final ItemDescription itemDescription = event.getItemDescription();
		DataMinerPortletServiceAsync.INSTANCE.cancelComputation(
				itemDescription, new AsyncCallback<String>() {

					@Override
					public void onFailure(Throwable caught) {
						if (caught instanceof SessionExpiredServiceException) {
							UtilsGXT3.alert("Error", "Expired Session!");
							EventBusProvider.INSTANCE
									.fireEvent(new SessionExpiredEvent());
						} else {
							Log.error("Error in cancenExecutionFromComputations:"
									+ caught.getLocalizedMessage());
							UtilsGXT3.alert("Error",
									"Error in cancel computation "
											+ itemDescription.getName() + ": "
											+ caught.getLocalizedMessage());
						}

					}

					@Override
					public void onSuccess(String result) {
						Log.debug("Computation Cancelled!");
						final InfoMessageBox d = new InfoMessageBox("Info",
								"Computation cancellation request has been accepted!");
						d.addHideHandler(new HideHandler() {

							public void onHide(HideEvent event) {
								fireRefreshDataMinerWorkAreaEvent(DataMinerWorkAreaElementType.Computations);
							}
						});
						d.show();

					}
				});

	}

	private void cancelComputationRequest(
			CancelComputationExecutionRequestEvent event) {
		final ComputationId computationId = event.getComputationId();
		DataMinerPortletServiceAsync.INSTANCE.cancelComputation(computationId,
				new AsyncCallback<String>() {

					@Override
					public void onFailure(Throwable caught) {
						if (caught instanceof SessionExpiredServiceException) {
							UtilsGXT3.alert("Error", "Expired Session!");
							EventBusProvider.INSTANCE
									.fireEvent(new SessionExpiredEvent());
						} else {
							UtilsGXT3.alert("Error",
									"Error in cancel computation "
											+ computationId.getId() + ": "
											+ caught.getLocalizedMessage());
						}

					}

					@Override
					public void onSuccess(String result) {
						Log.debug("Computation Cancelled!");

					}
				});
	}

	private void resubmitComputationRequest(
			final ResubmitComputationExecutionRequestEvent event) {
		currentVisualization = MenuType.EXPERIMENT;
		MenuSwitchEvent menuSwitchEvent = new MenuSwitchEvent(
				MenuType.EXPERIMENT);
		EventBusProvider.INSTANCE.fireEvent(menuSwitchEvent);

		DataMinerPortletServiceAsync.INSTANCE.resubmit(
				event.getItemDescription(), new AsyncCallback<ComputationId>() {

					@Override
					public void onFailure(Throwable caught) {
						if (caught instanceof SessionExpiredServiceException) {
							UtilsGXT3.alert("Error", "Expired Session!");
							EventBusProvider.INSTANCE
									.fireEvent(new SessionExpiredEvent());
						} else {
							UtilsGXT3.alert(
									"Error",
									"Failed to resubmit computation: "
											+ caught.getMessage());
						}
					}

					@Override
					public void onSuccess(ComputationId result) {
						if (result == null)
							UtilsGXT3
									.alert("Error",
											"Failed to resubmit computation, the computation id is null!");
						else {
							resubmitComputation(result);
						}

					}
				});
	}

	private void resubmitComputation(ComputationId computationId) {
		ResubmitComputationExecutionEvent event = new ResubmitComputationExecutionEvent(
				computationId);
		EventBusProvider.INSTANCE.fireEvent(event);
	}

	private void operatorsClassificationRequest(
			OperatorsClassificationRequestEvent event) {
		if (operatorsClassifications == null) {
			getOperatorsClassifications(event);
		} else {
			if (event.getOperatorsClassificationRequestType() == null) {
				return;
			}
			switch (event.getOperatorsClassificationRequestType()) {
			case ByName:
				getOperatorsClassificationByName(event);
				break;
			case Default:
				getOperatorsClassificationDefault(event);
				break;
			default:
				break;

			}

		}

	}

	private void getOperatorsClassifications(
			final OperatorsClassificationRequestEvent event) {
		DataMinerPortletServiceAsync.INSTANCE
				.getOperatorsClassifications(new AsyncCallback<List<OperatorsClassification>>() {

					@Override
					public void onSuccess(List<OperatorsClassification> result) {
						operatorsClassifications = result;
						operatorsClassificationRequest(event);
					}

					@Override
					public void onFailure(Throwable caught) {
						if (caught instanceof SessionExpiredServiceException) {
							UtilsGXT3.alert("Error", "Expired Session");
							EventBusProvider.INSTANCE
									.fireEvent(new SessionExpiredEvent());
						} else {
							UtilsGXT3.alert("Error",
									"Error retrieving operators classification! "
											+ caught.getLocalizedMessage());
							Log.error("Error retrieving operators classification: "
									+ caught.getLocalizedMessage());
							caught.printStackTrace();
						}
					}
				});
	}

	private void getOperatorsClassificationDefault(
			OperatorsClassificationRequestEvent event) {
		OperatorsClassification find = null;
		for (OperatorsClassification oc : operatorsClassifications) {
			if (oc.getName().equals(Constants.UserClassificationName)) {
				find = oc;
				break;
			}
		}

		OperatorsClassificationEvent ocEvent;
		// TODO
		// if(event.isOperatorId()){
		// ocEvent = new OperatorsClassificationEvent(
		// find, operatorId);
		// } else {
		ocEvent = new OperatorsClassificationEvent(find);
		// }
		EventBusProvider.INSTANCE.fireEvent(ocEvent);
	}

	private void getOperatorsClassificationByName(
			OperatorsClassificationRequestEvent event) {
		OperatorsClassification find = null;
		for (OperatorsClassification oc : operatorsClassifications) {
			if (oc.getName().equals(event.getClassificationName())) {
				find = oc;
				break;
			}
		}

		if (find == null) {
			for (OperatorsClassification oc : operatorsClassifications) {
				if (oc.getName().equals(Constants.UserClassificationName)) {
					find = oc;
					break;
				}
			}
		}

		OperatorsClassificationEvent ocEvent;
		// TODO
		// if(event.isOperatorId()){
		// ocEvent = new OperatorsClassificationEvent(
		// event.getClassificationName(), find, operatorId);
		// } else {
		ocEvent = new OperatorsClassificationEvent(
				event.getClassificationName(), find);
		// }
		EventBusProvider.INSTANCE.fireEvent(ocEvent);
	}

	private void retrieveDataMinerWorkArea(
			final DataMinerWorkAreaRequestEvent event) {
		final StatusMonitor monitor = new StatusMonitor();
		DataMinerPortletServiceAsync.INSTANCE
				.getDataMinerWorkArea(new AsyncCallback<DataMinerWorkArea>() {

					@Override
					public void onFailure(Throwable caught) {
						monitor.hide();
						if (caught instanceof SessionExpiredServiceException) {
							UtilsGXT3.alert("Error", "Expired Session");
							EventBusProvider.INSTANCE
									.fireEvent(new SessionExpiredEvent());
						} else {
							UtilsGXT3.alert("Error",
									"Error retrieving DataMiner work area info: "
											+ caught.getLocalizedMessage());
						}
					}

					@Override
					public void onSuccess(DataMinerWorkArea result) {
						monitor.hide();
						Log.debug("DataMinerWorkArea: " + result);
						fireDataMinerWorkAreaEventRetrieved(event, result);

					}

				});

	}

	private void fireDataMinerWorkAreaEventRetrieved(
			DataMinerWorkAreaRequestEvent event, DataMinerWorkArea result) {
		dataMinerWorkArea = result;
		DataMinerWorkAreaEvent dataMinerWorkAreaEvent = new DataMinerWorkAreaEvent(
				DataMinerWorkAreaEventType.OPEN,
				event.getDataMinerWorkAreaRegionType(), result);
		EventBusProvider.INSTANCE.fireEvent(dataMinerWorkAreaEvent);
	}

	private void deleteItemRequest(final DeleteItemRequestEvent event) {
		final StatusMonitor monitor = new StatusMonitor();
		DataMinerPortletServiceAsync.INSTANCE.deleteItem(
				event.getItemDescription(), new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						monitor.hide();
						if (caught instanceof SessionExpiredServiceException) {
							UtilsGXT3.alert("Error", "Expired Session");
							EventBusProvider.INSTANCE
									.fireEvent(new SessionExpiredEvent());
						} else {
							UtilsGXT3.alert("Error",
									"Error deleting item on workspace: "
											+ caught.getLocalizedMessage());
						}
					}

					@Override
					public void onSuccess(Void result) {
						monitor.hide();
						Log.debug("Deleted Item!");
						fireRefreshDataMinerWorkAreaEvent(event
								.getDataMinerWorkAreaElementType());

					}

				});

	}

	private void fireRefreshDataMinerWorkAreaEvent(
			DataMinerWorkAreaElementType dataMinerWorkAreaElementType) {
		RefreshDataMinerWorkAreaEvent refreshEvent = new RefreshDataMinerWorkAreaEvent(
				dataMinerWorkAreaElementType);
		EventBusProvider.INSTANCE.fireEvent(refreshEvent);

	}

	private void manageOutputDataRequestEvent(OutputDataRequestEvent event) {
		if (event == null) {
			UtilsGXT3.alert("Error", "Invalid output request!");
		} else {
			if (event.getComputationId() == null
					|| event.getComputationId().getId() == null
					|| event.getComputationId().getId().isEmpty()) {
				UtilsGXT3.alert(
						"Error",
						"Invalid output request, computation id: "
								+ event.getComputationId());
			} else {
				final StatusMonitor monitor = new StatusMonitor();
				DataMinerPortletServiceAsync.INSTANCE
						.getOutputDataByComputationId(event.getComputationId(),
								new AsyncCallback<OutputData>() {
									@Override
									public void onSuccess(OutputData outputData) {
										monitor.hide();
										fireOutputDataEvent(outputData);
									}

									@Override
									public void onFailure(Throwable caught) {
										monitor.hide();
										Log.error("Error in getResourceByComputationId: "
												+ caught.getLocalizedMessage());
										UtilsGXT3
												.alert("Error",
														"Impossible to retrieve output info. "
																+ caught.getLocalizedMessage());

									}
								});
			}
		}
	}

	private void fireOutputDataEvent(OutputData outputData) {
		OutputDataEvent event = new OutputDataEvent(outputData);
		EventBusProvider.INSTANCE.fireEvent(event);

	}

	private void manageComputationDataRequestEvent(
			ComputationDataRequestEvent event) {
		if (event == null) {
			UtilsGXT3.alert("Error", "Invalid computation info request!");
		} else {
			if (event.getItemDescription() == null
					|| event.getItemDescription().getId() == null
					|| event.getItemDescription().getId().isEmpty()) {
				UtilsGXT3.alert("Error",
						"Invalid computation info request, item description: "
								+ event.getItemDescription());
			} else {
				final StatusMonitor monitor = new StatusMonitor();
				DataMinerPortletServiceAsync.INSTANCE.getComputationData(
						event.getItemDescription(),
						new AsyncCallback<ComputationData>() {
							@Override
							public void onSuccess(
									ComputationData computationData) {
								monitor.hide();
								fireComputationDataEvent(computationData);
							}

							@Override
							public void onFailure(Throwable caught) {
								monitor.hide();
								Log.error("Error in getComputationData: "
										+ caught.getLocalizedMessage());
								caught.printStackTrace();
								UtilsGXT3.alert("Error",
										"Impossible to retrieve computation info. "
												+ caught.getLocalizedMessage());

							}
						});
			}
		}
	}

	private void fireComputationDataEvent(ComputationData computationData) {
		ComputationDataEvent event = new ComputationDataEvent(computationData);
		EventBusProvider.INSTANCE.fireEvent(event);

	}

}