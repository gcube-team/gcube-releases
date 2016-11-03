package org.gcube.portlets.user.td.columnwidget.client.batch;

import java.util.ArrayList;

import org.gcube.portlets.user.td.columnwidget.client.resources.ResourceBundle;
import org.gcube.portlets.user.td.columnwidget.client.utils.UtilsGXT3;
import org.gcube.portlets.user.td.gwtservice.client.rpc.TDGWTServiceAsync;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTIsFinalException;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTIsLockedException;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTSessionExpiredException;
import org.gcube.portlets.user.td.gwtservice.shared.task.InvocationS;
import org.gcube.portlets.user.td.gwtservice.shared.task.TaskResumeSession;
import org.gcube.portlets.user.td.gwtservice.shared.tr.ConditionCode;
import org.gcube.portlets.user.td.gwtservice.shared.tr.DimensionRow;
import org.gcube.portlets.user.td.gwtservice.shared.tr.batch.ReplaceBatchColumnSession;
import org.gcube.portlets.user.td.gwtservice.shared.tr.batch.ReplaceEntry;
import org.gcube.portlets.user.td.gwtservice.shared.tr.column.mapping.ColumnMappingData;
import org.gcube.portlets.user.td.monitorwidget.client.MonitorDialog;
import org.gcube.portlets.user.td.monitorwidget.client.MonitorDialogListener;
import org.gcube.portlets.user.td.widgetcommonevent.client.CommonMessages;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.ChangeTableRequestEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.SessionExpiredEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.ChangeTableRequestType;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.ChangeTableWhy;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.SessionExpiredType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.OperationResult;
import org.gcube.portlets.user.td.widgetcommonevent.shared.RequestProperties;
import org.gcube.portlets.user.td.widgetcommonevent.shared.RequestPropertiesParameterType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class ReplaceBatchDialog extends Window implements MonitorDialogListener {
	private static final String WIDTH = "650px";
	private static final String HEIGHT = "560px";
	
	protected enum CALLTYPE {
		REPLACEBATH, RESUME;
	}
	private ReplaceBatchMessages msgs;
	private CommonMessages msgsCommon;
	
	
	private TRId trId;
	
	private EventBus eventBus;
	private String taskIdOfInvocationS;
	private CALLTYPE callType;
	private InvocationS invocationS;
	private ConditionCode conditionCode; // For Curation


	
	

	/**
	 * 
	 * @param trId
	 * @param columnLocalId
	 * @param eventBus
	 */
	public ReplaceBatchDialog(TRId trId, String columnLocalId, EventBus eventBus) {
		this.trId = trId;
		this.eventBus = eventBus;
		taskIdOfInvocationS = null;
		initMessages();
		initWindow();
		ReplaceBatchPanel batchRepalcePanel = new ReplaceBatchPanel(this, trId,
				columnLocalId, eventBus);
		add(batchRepalcePanel);
	}

	/**
	 * 
	 * @param trId
	 * @param requestProperties
	 * @param eventBus
	 */
	public ReplaceBatchDialog(TRId trId, RequestProperties requestProperties,
			EventBus eventBus) {
		this.trId = trId;
		this.eventBus = eventBus;
		initMessages();
		Log.debug("ReplaceBatchDialog: " + trId + ", RequestProperties:"
				+ requestProperties);
		
		invocationS = (InvocationS) requestProperties.getMap().get(
				RequestPropertiesParameterType.InvocationS);
		taskIdOfInvocationS = invocationS.getTaskId();

		conditionCode = (ConditionCode) requestProperties.getMap().get(
				RequestPropertiesParameterType.ConditionCode);

		Log.debug("Resume TaskId:" + taskIdOfInvocationS);
		initWindow();

		ReplaceBatchPanel batchRepalcePanel = new ReplaceBatchPanel(this, trId,
				requestProperties, eventBus);
		add(batchRepalcePanel);
	}
	
	protected void initMessages(){
		msgs = GWT.create(ReplaceBatchMessages.class);
		msgsCommon = GWT.create(CommonMessages.class);
	}
	
	/**
	 * 
	 */
	protected void initWindow() {
		setWidth(WIDTH);
		setHeight(HEIGHT);
		setBodyBorder(false);
		setResizable(false);

		setClosable(true);
		setModal(true);
		forceLayoutOnResize = true;
		if (conditionCode == null) {
			setHeadingText(msgs.dialogReplaceBatchHeadingText());
			getHeader().setIcon(ResourceBundle.INSTANCE.replaceBatch());
		} else {
			switch (conditionCode) {
			case AllowedColumnType:
				break;
			case AmbiguousValueOnExternalReference:
				setHeadingText(msgs.dialogResolveAmbiguousValuesHeadingText());
				break;
			case CastValidation:
				break;
			case CodeNamePresence:
				break;
			case DuplicateTupleValidation:
				break;
			case DuplicateValueInColumn:
				break;
			case GenericTupleValidity:
				break;
			case GenericValidity:
				break;
			case MaxOneCodenameForDataLocale:
				break;
			case MissingValueOnExternalReference:
				setHeadingText(msgs.dialogResolveValueOnExternalReferenceHeadingText());
				break;
			case MustContainAtLeastOneDimension:
				break;
			case MustContainAtLeastOneMeasure:
				break;
			case MustHaveDataLocaleMetadataAndAtLeastOneLabel:
				break;
			case OnlyOneCodeColumn:
				break;
			case OnlyOneCodenameColumn:
				break;
			case ValidPeriodFormat:
				break;
			default:
				setHeadingText(msgs.dialogReplaceBatchHeadingText());
				getHeader().setIcon(ResourceBundle.INSTANCE.replaceBatch());
				break;

			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initTools() {
		super.initTools();

		closeBtn.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				close();
			}
		});

	}

	/**
	 * 
	 */
	protected void close() {
		hide();

	}

	/**
	 * 
	 * @param replaceBatchColumnSession
	 */
	protected void startBatchReplace(
			ReplaceBatchColumnSession replaceBatchColumnSession) {
		if (conditionCode == null) {
			callStartReplaceBatchColumn(replaceBatchColumnSession);
		} else {
			switch (conditionCode) {
			case AllowedColumnType:
				break;
			case AmbiguousValueOnExternalReference:
				callResumeOnChangeColumnType(replaceBatchColumnSession);
				break;
			case CastValidation:
				break;
			case CodeNamePresence:
				break;
			case DuplicateTupleValidation:
				break;
			case DuplicateValueInColumn:
				break;
			case GenericTupleValidity:
				break;
			case GenericValidity:
				break;
			case MaxOneCodenameForDataLocale:
				break;
			case MissingValueOnExternalReference:
				callStartReplaceBatchColumn(replaceBatchColumnSession);
				break;
			case MustContainAtLeastOneDimension:
				break;
			case MustContainAtLeastOneMeasure:
				break;
			case MustHaveDataLocaleMetadataAndAtLeastOneLabel:
				break;
			case OnlyOneCodeColumn:
				break;
			case OnlyOneCodenameColumn:
				break;
			case ValidPeriodFormat:
				break;
			default:
				callStartReplaceBatchColumn(replaceBatchColumnSession);
				break;

			}
		}
	}

	/**
	 * 
	 * @param replaceBatchColumnSession
	 */
	protected void callStartReplaceBatchColumn(
			ReplaceBatchColumnSession replaceBatchColumnSession) {
		TDGWTServiceAsync.INSTANCE.startReplaceBatchColumn(
				replaceBatchColumnSession, new AsyncCallback<String>() {

					public void onFailure(Throwable caught) {
						if (caught instanceof TDGWTSessionExpiredException) {
							eventBus.fireEvent(new SessionExpiredEvent(
									SessionExpiredType.EXPIREDONSERVER));
						} else {
							if (caught instanceof TDGWTIsLockedException) {
								Log.error(caught.getLocalizedMessage());
								UtilsGXT3.alert(msgsCommon.errorLocked(),
										caught.getLocalizedMessage());
							} else {
								if (caught instanceof TDGWTIsFinalException) {
									Log.error(caught.getLocalizedMessage());
									UtilsGXT3.alert(msgsCommon.errorFinal(),
											caught.getLocalizedMessage());
								} else {
									Log.error("Start Replace Batch failed:"
											+ caught.getLocalizedMessage());
									UtilsGXT3.alert(msgs.errorOnBatchReplaceHead(),
											msgs.errorOnBatchReplace());
								}
							}
						}
						close();

					}

					public void onSuccess(String taskId) {
						Log.trace("Started batch replace ");
						callReplaceBatchColumnProgressDialog(taskId);

					}

				});
	}

	/**
	 * 
	 */
	// TODO
	protected void callReplaceBatchColumnProgressDialog(String taskId) {
		callType = CALLTYPE.REPLACEBATH;
		openMonitorDialog(taskId);

	}

	/**
	 * 
	 */
	protected void startTaskResume() {
		TaskResumeSession taskResumeSession = new TaskResumeSession(trId,
				taskIdOfInvocationS);
		TDGWTServiceAsync.INSTANCE.startTaskResume(taskResumeSession,
				new AsyncCallback<String>() {

					public void onFailure(Throwable caught) {
						if (caught instanceof TDGWTSessionExpiredException) {
							eventBus.fireEvent(new SessionExpiredEvent(
									SessionExpiredType.EXPIREDONSERVER));
						} else {
							if (caught instanceof TDGWTIsLockedException) {
								Log.error(caught.getLocalizedMessage());
								UtilsGXT3.alert(msgsCommon.errorLocked(),
										caught.getLocalizedMessage());
							} else {
								if (caught instanceof TDGWTIsFinalException) {
									Log.error(caught.getLocalizedMessage());
									UtilsGXT3.alert(msgsCommon.errorFinal(),
											caught.getLocalizedMessage());
								} else {
									Log.error("Task Resume failed:"
											+ caught.getLocalizedMessage());
									UtilsGXT3
											.alert(msgs.errorOnTaskResumeHead(),
													msgs.errorOnTaskResume(taskIdOfInvocationS));
								}
							}
						}
						close();

					}

					public void onSuccess(String taskId) {
						Log.trace("Started task resume");
						callForTaskResume(taskId);

					}

				});
	}

	/**
	 * 
	 * @param mapping
	 */
	protected void startTaskResume(ArrayList<ColumnMappingData> mapping,
			ColumnData column) {
		TaskResumeSession taskResumeSession = new TaskResumeSession(trId,
				taskIdOfInvocationS, mapping, column, invocationS);
		TDGWTServiceAsync.INSTANCE.startTaskResume(taskResumeSession,
				new AsyncCallback<String>() {

					public void onFailure(Throwable caught) {
						if (caught instanceof TDGWTSessionExpiredException) {
							eventBus.fireEvent(new SessionExpiredEvent(
									SessionExpiredType.EXPIREDONSERVER));
						} else {
							if (caught instanceof TDGWTIsLockedException) {
								Log.error(caught.getLocalizedMessage());
								UtilsGXT3.alert(msgsCommon.errorLocked(),
										caught.getLocalizedMessage());
							} else {
								if (caught instanceof TDGWTIsFinalException) {
									Log.error(caught.getLocalizedMessage());
									UtilsGXT3.alert(msgsCommon.errorFinal(),
											caught.getLocalizedMessage());
								} else {
									Log.error("Task Resume failed:"
											+ caught.getLocalizedMessage());
									UtilsGXT3
											.alert(msgs.errorOnTaskResumeHead(),
													msgs.errorOnTaskResume(taskIdOfInvocationS));
								}
							}
						}
						close();

					}

					public void onSuccess(String taskId) {
						Log.trace("Started task resume");
						callForTaskResume(taskId);

					}

				});
	}

	/**
	 * 
	 */
	protected void callForTaskResume(String taskId) {
		callType = CALLTYPE.RESUME;
		openMonitorDialog(taskId);

	}

	/**
	 * 
	 * @param replaceBatchColumnSession
	 */
	private void callResumeOnChangeColumnType(
			ReplaceBatchColumnSession replaceBatchColumnSession) {
		Log.debug("ReplaceBatchColumnSession: "
				+ replaceBatchColumnSession.toString());

		ColumnData col = replaceBatchColumnSession.getColumnData();

		ArrayList<ReplaceEntry> replaceEntryList = replaceBatchColumnSession
				.getReplaceEntryList();
		ArrayList<ColumnMappingData> mapping = new ArrayList<ColumnMappingData>();

		for (ReplaceEntry re : replaceEntryList) {
			DimensionRow sourceArg = new DimensionRow(re.getRowId(),
					re.getValue());
			DimensionRow targetArg = re.getReplacementDimensionRow();
			ColumnMappingData columnMappingData = new ColumnMappingData(
					sourceArg, targetArg);
			mapping.add(columnMappingData);
		}

		startTaskResume(mapping, col);

	}

	
	// /
	protected void openMonitorDialog(String taskId) {
		MonitorDialog monitorDialog = new MonitorDialog(taskId, eventBus);
		monitorDialog.addProgressDialogListener(this);
		monitorDialog.setBackgroundBtnEnabled(false);
		monitorDialog.show();
	}

	@Override
	public void operationComplete(OperationResult operationResult) {
		switch (callType) {
		case REPLACEBATH:
			this.trId = operationResult.getTrId();
			if (taskIdOfInvocationS == null) {
				ChangeTableWhy why = ChangeTableWhy.TABLEUPDATED;
				ChangeTableRequestEvent changeTableRequestEvent = new ChangeTableRequestEvent(
						ChangeTableRequestType.COLUMNREPLACEBATCH, trId, why);
				eventBus.fireEvent(changeTableRequestEvent);
				close();
			} else {
				if (conditionCode == null) {
					startTaskResume();
				} else {
					switch (conditionCode) {
					case AllowedColumnType:
						break;
					case AmbiguousValueOnExternalReference:
						ChangeTableWhy why = ChangeTableWhy.TABLECURATION;
						ChangeTableRequestEvent changeTableRequestEvent = new ChangeTableRequestEvent(
								ChangeTableRequestType.COLUMNREPLACEBATCH,
								trId, why);
						eventBus.fireEvent(changeTableRequestEvent);
						close();
						break;
					case CastValidation:
						break;
					case CodeNamePresence:
						break;
					case DuplicateTupleValidation:
						break;
					case DuplicateValueInColumn:
						break;
					case GenericTupleValidity:
						break;
					case GenericValidity:
						break;
					case MaxOneCodenameForDataLocale:
						break;
					case MissingValueOnExternalReference:
						startTaskResume();
						break;
					case MustContainAtLeastOneDimension:
						break;
					case MustContainAtLeastOneMeasure:
						break;
					case MustHaveDataLocaleMetadataAndAtLeastOneLabel:
						break;
					case OnlyOneCodeColumn:
						break;
					case OnlyOneCodenameColumn:
						break;
					case ValidPeriodFormat:
						break;
					default:
						startTaskResume();
						break;

					}
				}

			}
			break;
		case RESUME:
			ChangeTableWhy why = ChangeTableWhy.TABLECURATION;
			ChangeTableRequestEvent changeTableRequestEvent = new ChangeTableRequestEvent(
					ChangeTableRequestType.COLUMNREPLACEBATCH, operationResult.getTrId(), why);
			eventBus.fireEvent(changeTableRequestEvent);
			hide();
			break;
		default:
			break;

		}

	}

	@Override
	public void operationFailed(Throwable caught, String reason, String details) {
		switch (callType) {
		case REPLACEBATH:
			UtilsGXT3.alert(msgs.errorOnBatchReplaceHead(), reason);
			close();
			break;
		case RESUME:
			UtilsGXT3.alert(msgs.errorOnTaskResumeHead(), reason);
			hide();
			break;
		default:
			break;
		}
	}

	@Override
	public void operationStopped(OperationResult operationResult, String reason, String details) {
		ChangeTableWhy why = ChangeTableWhy.TABLECURATION;
		ChangeTableRequestEvent changeTableRequestEvent = new ChangeTableRequestEvent(
				ChangeTableRequestType.COLUMNREPLACEBATCH, operationResult.getTrId(), why);
		eventBus.fireEvent(changeTableRequestEvent);
		close();

	}

	@Override
	public void operationAborted() {
		close();

	}

	@Override
	public void operationPutInBackground() {
		close();

	}

}
