/**
 * 
 */
package org.gcube.portlets.user.td.extractcodelistwidget.client;

import java.util.ArrayList;

import org.gcube.portlets.user.td.gwtservice.client.rpc.TDGWTServiceAsync;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTSessionExpiredException;
import org.gcube.portlets.user.td.gwtservice.shared.extract.ExtractCodelistSession;
import org.gcube.portlets.user.td.gwtservice.shared.extract.ExtractCodelistTargetColumn;
import org.gcube.portlets.user.td.gwtservice.shared.tr.column.type.ChangeColumnTypeSession;
import org.gcube.portlets.user.td.monitorwidget.client.MonitorDialog;
import org.gcube.portlets.user.td.monitorwidget.client.MonitorDialogListener;
import org.gcube.portlets.user.td.widgetcommonevent.client.CommonMessages;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.SessionExpiredEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.SessionExpiredType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.OperationResult;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnDataType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnTypeCode;
import org.gcube.portlets.user.td.wizardwidget.client.WizardCard;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutData;
import com.sencha.gxt.widget.core.client.container.HtmlLayoutContainer;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.VBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VBoxLayoutContainer.VBoxLayoutAlign;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class ExtractCodelistOperationInProgressCard extends WizardCard
		implements MonitorDialogListener {
	private static CommonMessages msgsCommon = GWT.create(CommonMessages.class);
	private ExtractCodelistMessages msgs;

	@SuppressWarnings("unused")
	private ExtractCodelistOperationInProgressCard thisCard;
	private ExtractCodelistSession extractCodelistSession;
	private TRId newTrId;
	private HtmlLayoutContainer resultField;
	private boolean automaticallyAttached;
	private TRId collateralTRId;
	private MonitorDialog monitorDialog;

	public ExtractCodelistOperationInProgressCard(
			final ExtractCodelistSession extractCodelistSession) {
		super(msgsCommon.operationInProgress(), "");

		this.extractCodelistSession = extractCodelistSession;
		thisCard = this;
		automaticallyAttached = false;

		initMessages();

		VBoxLayoutContainer operationInProgressPanel = new VBoxLayoutContainer();
		operationInProgressPanel.setVBoxLayoutAlign(VBoxLayoutAlign.LEFT);

		resultField = new HtmlLayoutContainer("<div style='left:0;'></div>");

		operationInProgressPanel.add(resultField, new BoxLayoutData(
				new Margins(10, 5, 10, 25)));

		setCenterWidget(operationInProgressPanel, new MarginData(0));
		resultField.setVisible(false);

	}

	protected void initMessages() {
		msgs = GWT.create(ExtractCodelistMessages.class);

	}

	public void extractCodelist() {
		TDGWTServiceAsync.INSTANCE.startExtractCodelist(extractCodelistSession,
				new AsyncCallback<String>() {

					public void onSuccess(String taskId) {
						openMonitorDialog(taskId);

					}

					public void onFailure(Throwable caught) {
						if (caught instanceof TDGWTSessionExpiredException) {
							getEventBus()
									.fireEvent(
											new SessionExpiredEvent(
													SessionExpiredType.EXPIREDONSERVER));
						} else {
							showErrorAndHide(msgsCommon.error(),
									msgs.errorInExtractCodelistFixed(),
									caught.getLocalizedMessage(), caught);
							return;
						}
					}
				});
	}

	public void setCollateralTRIdFinal(ArrayList<TRId> collateralIds) {
		if (collateralIds == null || collateralIds.isEmpty()) {
			monitorDialog.hide();
			showErrorAndHide(
					msgsCommon.error(),
					msgs.errorAnErrorOccurredSettingCollateralTableFinalFixed(),
					msgs.errorNoCollateralIdRetrieved(),
					new Throwable(msgs.errorNoCollateralIdRetrieved()));

			return;

		}
		collateralTRId = collateralIds.get(0);
		if (collateralTRId == null) {
			monitorDialog.hide();
			showErrorAndHide(
					msgsCommon.error(),
					msgs.errorAnErrorOccurredSettingCollateralTableFinalFixed(),
					msgs.errorCollateralIdIsNull(),
					new Throwable(msgs.errorCollateralIdIsNull()));

			return;
		}

		TDGWTServiceAsync.INSTANCE.setTabResourceToFinal(collateralTRId,
				new AsyncCallback<Void>() {

					@Override
					public void onSuccess(Void result) {
						retrieveCollateralColumn();

					}

					@Override
					public void onFailure(Throwable caught) {
						if (caught instanceof TDGWTSessionExpiredException) {
							getEventBus()
									.fireEvent(
											new SessionExpiredEvent(
													SessionExpiredType.EXPIREDONSERVER));
						} else {
							showErrorAndHide(
									msgsCommon.error(),
									msgs.errorAnErrorOccurredSettingCollateralTableFinalFixed(),
									caught.getLocalizedMessage(), caught);
						}
					}

				});
	}

	private void retrieveCollateralColumn() {
		TDGWTServiceAsync.INSTANCE.getColumnsForDimension(collateralTRId,
				new AsyncCallback<ArrayList<ColumnData>>() {

					@Override
					public void onFailure(Throwable caught) {
						if (caught instanceof TDGWTSessionExpiredException) {
							getEventBus()
									.fireEvent(
											new SessionExpiredEvent(
													SessionExpiredType.EXPIREDONSERVER));
						} else {
							showErrorAndHide(
									msgsCommon.error(),
									msgs.errorAnErrorOccurredRetrievingColumnOnCollateralTableFixed(),
									caught.getLocalizedMessage(), caught);
						}
					}

					@Override
					public void onSuccess(ArrayList<ColumnData> result) {
						createChangeColumnSession(result);
					}
				});

	}

	private void createChangeColumnSession(ArrayList<ColumnData> refColumns) {
		ColumnData orig = extractCodelistSession.getAttachColumn();
		ColumnTypeCode origColumnTypeCode = ColumnTypeCode
				.getColumnTypeCodeFromId(orig.getTypeCode());
		ColumnDataType origColumnDataType = ColumnDataType
				.getColumnDataTypeFromId(orig.getDataTypeName());

		String labelOfAttachColumn = null;
		ColumnTypeCode typeCodeOfColumnAttach = null;
		for (ExtractCodelistTargetColumn targetCol : extractCodelistSession
				.getTargetColumns()) {
			if (targetCol.getSourceColumn().getColumnId()
					.compareTo(orig.getColumnId()) == 0) {
				if (targetCol.isNewColumn()) {
					labelOfAttachColumn = targetCol.getDefColumn().getLabel();
					typeCodeOfColumnAttach = targetCol.getDefColumn()
							.getColumnType();
				} else {
					labelOfAttachColumn = targetCol.getTargetColumn()
							.getLabel();
					typeCodeOfColumnAttach = ColumnTypeCode
							.getColumnTypeCodeFromId(targetCol
									.getTargetColumn().getTypeCode());
				}
				break;
			}
		}

		ColumnData codelistColumnReference = null;

		if (labelOfAttachColumn == null) {
			showErrorAndHide(
					msgsCommon.error(),
					msgs.errorAnErrorOccurredNoLabelRetrievedForAttachColumnFixed(),
					msgs.errorNoLabelRetrievedForAttachColumn(), new Throwable(
							msgs.errorNoLabelRetrievedForAttachColumn()));
			return;
		} else {
			for (ColumnData refColumn : refColumns) {
				if (refColumn.getLabel().compareTo(labelOfAttachColumn) == 0) {
					if (ColumnTypeCode.getColumnTypeCodeFromId(
							refColumn.getTypeCode()).compareTo(
							typeCodeOfColumnAttach) == 0) {
						codelistColumnReference = refColumn;
						break;
					} else {

					}
				}

			}
		}
		if (codelistColumnReference != null) {
			ColumnTypeCode columnTypeCodeTarget = ColumnTypeCode.DIMENSION;

			ChangeColumnTypeSession changeColumnTypeSession = new ChangeColumnTypeSession(
					orig, origColumnTypeCode, origColumnDataType,
					columnTypeCodeTarget, null, codelistColumnReference);
			callChangeColumnType(changeColumnTypeSession);
		} else {
			showErrorAndHide(msgsCommon.error(),
					msgs.errorAnErrorOccurredNoAttachColumnMatchFixed(),
					msgs.errorNoAttachColumnMatch(),
					new Throwable(msgs.errorNoAttachColumnMatch()));
		}
	}

	private void callChangeColumnType(
			ChangeColumnTypeSession changeColumnTypeSession) {
		TDGWTServiceAsync.INSTANCE.startChangeColumnType(
				changeColumnTypeSession, new AsyncCallback<String>() {
					public void onFailure(Throwable caught) {
						if (caught instanceof TDGWTSessionExpiredException) {
							getEventBus()
									.fireEvent(
											new SessionExpiredEvent(
													SessionExpiredType.EXPIREDONSERVER));
						} else {
							showErrorAndHide(
									msgsCommon.error(),
									msgs.errorAnErrorOccurredOnStartChangeColumnTypeFixed(),
									caught.getLocalizedMessage(), caught);
						}
					}

					public void onSuccess(String taskId) {
						automaticallyAttached = true;
						openMonitorDialog(taskId);

					}

				});

	}

	@Override
	public void setup() {
		getWizardWindow().setEnableBackButton(false);
		setBackButtonVisible(false);
		getWizardWindow().setEnableNextButton(false);
		getWizardWindow().setNextButtonToFinish();
		extractCodelist();
	}

	//
	protected void openMonitorDialog(String taskId) {
		monitorDialog = new MonitorDialog(taskId, getEventBus());
		monitorDialog.addProgressDialogListener(this);
		monitorDialog.setBackgroundBtnEnabled(false);
		monitorDialog.show();
	}

	@Override
	public void operationComplete(OperationResult operationResult) {
		newTrId = operationResult.getTrId();
		if (extractCodelistSession.isAutomaticallyAttach()) {
			if (automaticallyAttached) {
				updateOnComplete();
			} else {
				setCollateralTRIdFinal(operationResult.getCollateralTRIds());
			}
		} else {
			updateOnComplete();
		}
	}

	protected void updateOnComplete() {
		SafeHtmlBuilder safeHtmlBuilder = new SafeHtmlBuilder();
		safeHtmlBuilder
				.appendHtmlConstant("<div style='display:block;margin:auto;left:0;text-align:center;font-size:large;font-weight:bold; color:#009900;'>"
						+ msgsCommon.operationCompleted()
						+ "</div>"
						+ "<div style='display:block;margin:auto; margin-top:30px;left:0;text-align:center;font-size:medium;font-weight:bold; color:black;'>"
						+ msgs.codelistAvailableInResources() + "</div>");
		resultField.setHTML(safeHtmlBuilder.toSafeHtml());
		resultField.setVisible(true);
		Command sayComplete = new Command() {
			public void execute() {
				try {
					getWizardWindow().close(false);
					Log.info("fire Complete: " + newTrId);

					getWizardWindow().fireCompleted(newTrId);

				} catch (Exception e) {
					Log.error("fire Complete :" + e.getLocalizedMessage());
				}
			}
		};

		getWizardWindow().setNextButtonCommand(sayComplete);

		setNextButtonVisible(true);
		getWizardWindow().setEnableNextButton(true);
		forceLayout();
	}

	@Override
	public void operationFailed(Throwable caught, String reason, String details) {
		SafeHtmlBuilder safeHtmlBuilder = new SafeHtmlBuilder();
		safeHtmlBuilder
				.appendHtmlConstant("<div style='display:block;margin:auto;left:0;text-align:center;font-size:large;font-weight:bold;color:red;'>"
						+ msgsCommon.operationFailed() + "</div>");
		resultField.setHTML(safeHtmlBuilder.toSafeHtml());
		resultField.setVisible(true);
		if (caught instanceof TDGWTSessionExpiredException) {
			getEventBus()
					.fireEvent(
							new SessionExpiredEvent(
									SessionExpiredType.EXPIREDONSERVER));
		} else {
			showErrorAndHide(msgsCommon.error(), reason, details, caught);

		}
		forceLayout();
	}

	@Override
	public void operationStopped(OperationResult operationResult,
			String reason, String details) {
		newTrId = operationResult.getTrId();
		SafeHtmlBuilder safeHtmlBuilder = new SafeHtmlBuilder();
		safeHtmlBuilder
				.appendHtmlConstant("<div style='display:block;margin:auto;left:0;text-align:center;font-size:large;font-weight:bold;color: #FF9900;'>"
						+ msgsCommon.operationProblem() + "</div>");
		resultField.setHTML(safeHtmlBuilder.toSafeHtml());
		resultField.setVisible(true);

		Command sayComplete = new Command() {
			public void execute() {
				try {
					getWizardWindow().close(false);
					Log.info("fire Complete: tabular resource "
							+ newTrId.getId());
					Log.info("fire Complete: tableId " + newTrId.getTableId());

					getWizardWindow().fireCompleted(newTrId);

				} catch (Exception e) {
					Log.error("fire Complete :" + e.getLocalizedMessage());
				}
			}
		};

		getWizardWindow().setNextButtonCommand(sayComplete);

		setNextButtonVisible(true);
		getWizardWindow().setEnableNextButton(true);
		forceLayout();
	}

	@Override
	public void operationAborted() {
		SafeHtmlBuilder safeHtmlBuilder = new SafeHtmlBuilder();
		safeHtmlBuilder
				.appendHtmlConstant("<div style='diplay:block;margin:auto;left:0;text-align:center;font-size:large;font-weight:bold;color: #AA00AA;'>"
						+ msgsCommon.operationAborted() + "</div>");
		resultField.setHTML(safeHtmlBuilder.toSafeHtml());
		resultField.setVisible(true);

		Command sayComplete = new Command() {
			public void execute() {
				try {
					getWizardWindow().close(false);
					Log.info("fire Aborted");

					getWizardWindow().fireAborted();

				} catch (Exception e) {
					Log.error("fire Aborted :" + e.getLocalizedMessage());
				}
			}
		};

		getWizardWindow().setNextButtonCommand(sayComplete);

		setNextButtonVisible(true);
		getWizardWindow().setEnableNextButton(true);
		forceLayout();

	}

	@Override
	public void operationPutInBackground() {
		SafeHtmlBuilder safeHtmlBuilder = new SafeHtmlBuilder();
		safeHtmlBuilder
				.appendHtmlConstant("<div style='display:block;margin:auto;left:0;text-align:center;font-size:large;font-weight:bold;color: #00AAAA;'>"
						+ msgsCommon.operationInBackground() + "</div>");
		resultField.setHTML(safeHtmlBuilder.toSafeHtml());
		resultField.setVisible(true);

		Command sayComplete = new Command() {
			public void execute() {
				try {
					getWizardWindow().close(false);
					Log.info("fire Operation In Background");

					getWizardWindow().firePutInBackground();

				} catch (Exception e) {
					Log.error("fire Operation In Background :"
							+ e.getLocalizedMessage());
				}
			}
		};

		getWizardWindow().setNextButtonCommand(sayComplete);

		setNextButtonVisible(true);
		getWizardWindow().setEnableNextButton(true);
		forceLayout();

	}

}
