/**
 * 
 */
package org.gcube.portlets.user.td.mapwidget.client;

import java.util.ArrayList;

import org.gcube.portlets.user.td.gwtservice.client.rpc.TDGWTServiceAsync;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTIsLockedException;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTSessionExpiredException;
import org.gcube.portlets.user.td.gwtservice.shared.map.MapCreationSession;
import org.gcube.portlets.user.td.gwtservice.shared.tr.resources.InternalURITD;
import org.gcube.portlets.user.td.gwtservice.shared.tr.resources.ResourceTD;
import org.gcube.portlets.user.td.gwtservice.shared.tr.resources.ResourceTDDescriptor;
import org.gcube.portlets.user.td.gwtservice.shared.tr.resources.ResourceTDType;
import org.gcube.portlets.user.td.gwtservice.shared.tr.resources.StringResourceTD;
import org.gcube.portlets.user.td.gwtservice.shared.tr.resources.TableResourceTD;
import org.gcube.portlets.user.td.gwtservice.shared.uriresolver.UriResolverSession;
import org.gcube.portlets.user.td.monitorwidget.client.MonitorDialog;
import org.gcube.portlets.user.td.monitorwidget.client.MonitorDialogListener;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.SessionExpiredEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.SessionExpiredType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.OperationResult;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.uriresolver.ApplicationType;
import org.gcube.portlets.user.td.wizardwidget.client.WizardCard;
import org.gcube.portlets.user.td.wizardwidget.client.util.UtilsGXT3;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.FramedPanel;
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
public class MapWidgetOperationInProgressCard extends WizardCard implements
		MonitorDialogListener {

	protected MapWidgetOperationInProgressCard thisCard;
	protected MapCreationSession mapCreationSession;
	protected TRId newTrId;
	protected HtmlLayoutContainer resultField;

	public MapWidgetOperationInProgressCard(
			final MapCreationSession mapCreationSession) {
		super("Operation In Progress", "");

		this.mapCreationSession = mapCreationSession;
		thisCard = this;

		VBoxLayoutContainer operationInProgressPanel = new VBoxLayoutContainer();
		operationInProgressPanel.setVBoxLayoutAlign(VBoxLayoutAlign.CENTER);

		final FlexTable description = new FlexTable();
		// FlexCellFormatter cellFormatter = description.getFlexCellFormatter();
		description.setCellSpacing(10);
		description.setCellPadding(4);
		description.setBorderWidth(0);

		// display:block;vertical-align:text-top;
		description.setHTML(0, 0,
				"<span style=\"font-weight:bold;\";>Name: </span>");
		description.setText(0, 1, mapCreationSession.getName());
		description.setHTML(1, 0,
				"<span style=\"font-weight:bold;\";>Abstract: </span>");
		description.setText(1, 1, mapCreationSession.getMetaAbstract());

		description.setHTML(2, 0,
				"<span style=\"font-weight:bold;\";>Purpose: </span>");
		description.setText(2, 1, mapCreationSession.getMetaPurpose());

		FramedPanel summary = new FramedPanel();
		summary.setHeadingText("Map Creation");
		summary.setWidth(400);
		summary.add(description);
		operationInProgressPanel.add(summary, new BoxLayoutData(new Margins(20,
				5, 10, 5)));

		resultField = new HtmlLayoutContainer("<div></div>");

		operationInProgressPanel.add(resultField, new BoxLayoutData(
				new Margins(10, 5, 10, 5)));

		setCenterWidget(operationInProgressPanel, new MarginData(0));
		resultField.setVisible(false);

	}

	public void mapCreation() {
		TDGWTServiceAsync.INSTANCE.startMapCreation(mapCreationSession,
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
							if (caught instanceof TDGWTIsLockedException) {
								Log.error(caught.getLocalizedMessage());
								showErrorAndHide("Error Locked",
										caught.getLocalizedMessage(), "",
										caught);
							} else {
								showErrorAndHide("Error",
										"An error occured in map creation.",
										caught.getLocalizedMessage(), caught);

							}
						}
					}
				});
	}

	@Override
	public void setup() {
		getWizardWindow().setEnableBackButton(false);
		setBackButtonVisible(false);
		getWizardWindow().setEnableNextButton(false);
		getWizardWindow().setNextButtonToFinish();
		mapCreation();
	}

	//
	protected void openMonitorDialog(String taskId) {
		MonitorDialog monitorDialog = new MonitorDialog(taskId, getEventBus());
		monitorDialog.addProgressDialogListener(this);
		monitorDialog.setBackgroundBtnEnabled(false);
		monitorDialog.show();
	}

	@Override
	public void operationComplete(OperationResult operationResult) {
		newTrId = operationResult.getTrId();
		SafeHtmlBuilder safeHtmlBuilder = new SafeHtmlBuilder();
		safeHtmlBuilder
				.appendHtmlConstant("<div style='text-align:center;font-size:large;font-weight:bold; color:#009900;'>Operation Completed</div>");
		resultField.setHTML(safeHtmlBuilder.toSafeHtml());
		resultField.setVisible(true);

		Command sayComplete = new Command() {
			public void execute() {
				try {
					getWizardWindow().close(false);
					Log.info("fire Complete: " + newTrId);
					openMap();
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
				.appendHtmlConstant("<div style='text-align:center;font-size:large;font-weight:bold;color:red;'>Operation Failed</div>");
		resultField.setHTML(safeHtmlBuilder.toSafeHtml());
		resultField.setVisible(true);
		if (caught instanceof TDGWTSessionExpiredException) {
			getEventBus()
					.fireEvent(
							new SessionExpiredEvent(
									SessionExpiredType.EXPIREDONSERVER));
		} else {
			showErrorAndHide("Error in Export", reason, details, caught);
		}

		forceLayout();
	}

	@Override
	public void operationStopped(OperationResult operationResult,
			String reason, String details) {
		newTrId = operationResult.getTrId();
		SafeHtmlBuilder safeHtmlBuilder = new SafeHtmlBuilder();
		safeHtmlBuilder
				.appendHtmlConstant("<div style='text-align:center;font-size:large;font-weight:bold;color: #FF9900;'>Problems in the Operation</div>");
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
				.appendHtmlConstant("<div style='text-align:center;font-size:large;font-weight:bold;color: #AA00AA;'>Operation Aborted</div>");
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
				.appendHtmlConstant("<div style='text-align:center;font-size:large;font-weight:bold;color: #00AAAA;'>Operation in Background</div>");
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

	protected void openMap() {
		TDGWTServiceAsync.INSTANCE.getResourcesTD(newTrId,
				new AsyncCallback<ArrayList<ResourceTDDescriptor>>() {

					public void onFailure(Throwable caught) {
						if (caught instanceof TDGWTSessionExpiredException) {
							getEventBus()
									.fireEvent(
											new SessionExpiredEvent(
													SessionExpiredType.EXPIREDONSERVER));
						} else {
							if (caught instanceof TDGWTIsLockedException) {
								Log.error(caught.getLocalizedMessage());
								UtilsGXT3.alert("Error Locked",
										caught.getLocalizedMessage());
							} else {
								Log.error("Error Retrieving Resources: "
										+ caught.getLocalizedMessage());
								
								
								UtilsGXT3.alert("Error retrieving resources",
										"Error retrieving resources");
							}
						}

					}

					public void onSuccess(ArrayList<ResourceTDDescriptor> result) {
						Log.debug("loaded " + result.size());
						if (result != null && result.size() > 0) {
							ResourceTDDescriptor resourceTDDescriptor=result.get(0);
							if(resourceTDDescriptor.getResourceType().compareTo(ResourceTDType.MAP)==0){
								requestOpenMap(resourceTDDescriptor);
							}
						}
					}

				});

	}

	protected void requestOpenMap(
			final ResourceTDDescriptor resourceTDDescriptor) {
		ResourceTD resource = resourceTDDescriptor.getResourceTD();
		if (resource instanceof StringResourceTD) {
			StringResourceTD stringResourceTD = (StringResourceTD) resource;
			UriResolverSession uriResolverSession = new UriResolverSession(
					stringResourceTD.getValue(), ApplicationType.GIS);

			TDGWTServiceAsync.INSTANCE.getUriFromResolver(uriResolverSession,
					new AsyncCallback<String>() {

						public void onFailure(Throwable caught) {
							if (caught instanceof TDGWTSessionExpiredException) {
								getEventBus()
										.fireEvent(
												new SessionExpiredEvent(
														SessionExpiredType.EXPIREDONSERVER));
							} else {
								Log.error("Error with uri resolver: "
										+ caught.getLocalizedMessage());

								UtilsGXT3.alert("Error",
										"Error retrieving uri from resolver");

							}
						}

						public void onSuccess(String link) {
							Log.debug("Retrieved link: " + link);
							Window.open(link, resourceTDDescriptor.getName(),
									"");
						}

					});

		} else {
			if (resource instanceof InternalURITD) {

			} else {
				if (resource instanceof TableResourceTD) {

				} else {
					Log.error("Error with resource: no valid resource");
					UtilsGXT3.alert("Error with resource",
							"Error no valid InternalUri");

				}

			}
		}
	}

}
