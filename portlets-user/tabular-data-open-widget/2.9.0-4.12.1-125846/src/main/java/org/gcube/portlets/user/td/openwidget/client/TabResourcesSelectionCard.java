/**
 * 
 */
package org.gcube.portlets.user.td.openwidget.client;

import org.gcube.portlets.user.td.gwtservice.client.rpc.TDGWTServiceAsync;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTIsLockedException;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTSessionExpiredException;
import org.gcube.portlets.user.td.gwtservice.shared.tr.TabResource;
import org.gcube.portlets.user.td.gwtservice.shared.tr.TableData;
import org.gcube.portlets.user.td.gwtservice.shared.tr.open.TDOpenSession;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.RibbonEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.SessionExpiredEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.RibbonType;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.SessionExpiredType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.TabResourceType;
import org.gcube.portlets.user.td.wizardwidget.client.WizardCard;
import org.gcube.portlets.user.td.wizardwidget.client.util.UtilsGXT3;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import com.sencha.gxt.widget.core.client.box.ConfirmMessageBox;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent.DialogHideHandler;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class TabResourcesSelectionCard extends WizardCard {

	private static TDOpenMessages msgsTDOpen = GWT.create(TDOpenMessages.class);
	protected TabResourcesSelectionCard thisCard;
	protected TDOpenSession tdOpenSession;
	protected TabResourcesSelectionPanel tabResourcesSelectionPanel;
	protected TRId trId;
	protected TabResource selectedTabResource = null;

	
	
	public TabResourcesSelectionCard(TRId trId,
			final TDOpenSession tdOpenSession) {
		super(msgsTDOpen.tabResourcesSelectionCardSelectLabel(), "");
		Log.debug("TabResourcesSelectionCard");
		this.tdOpenSession = tdOpenSession;
		thisCard = this;
		this.trId = trId;

		tabResourcesSelectionPanel = new TabResourcesSelectionPanel(thisCard,
				res);

		tabResourcesSelectionPanel
				.addSelectionHandler(new SelectionHandler<TabResource>() {

					public void onSelection(SelectionEvent<TabResource> event) {
						tdOpenSession
								.setSelectedTabResource(tabResourcesSelectionPanel
										.getSelectedItem());
						getWizardWindow().setEnableNextButton(true);
					}

				});

		setCenterWidget(tabResourcesSelectionPanel, new MarginData(0));

	}

	@Override
	public void setup() {
		Command sayFinish = new Command() {

			public void execute() {
				ckeckIsLocked();

			}

		};

		getWizardWindow().setNextButtonCommand(sayFinish);
		// getWizardWindow().setFinishCommand(sayFinish);
		setBackButtonVisible(false);

	}

	protected void ckeckIsLocked() {

		if (tdOpenSession.getSelectedTabResource().isLocked()) {
			Log.debug(
					"Attention",
					"This tabular resource is locked");
			AlertMessageBox d = new AlertMessageBox("Attention",
					"This tabular resource is locked, click background task to monitor it!");
			d.addHideHandler(new HideHandler() {

				public void onHide(HideEvent event) {

				}
			});
			d.show();
		} else {
			retrieveLastTable();
		}
	}

	public TDOpenSession getTdOpenSession() {
		return tdOpenSession;
	}

	public void setTdOpenSession(TDOpenSession tdOpenSession) {
		this.tdOpenSession = tdOpenSession;
	}

	public void retrieveLastTable() {
		final TRId selectedTRId = tdOpenSession.getSelectedTabResource()
				.getTrId();
		TDGWTServiceAsync.INSTANCE.getLastTable(selectedTRId,
				new AsyncCallback<TableData>() {

					@Override
					public void onFailure(Throwable caught) {
						if (caught instanceof TDGWTSessionExpiredException) {
							getEventBus()
									.fireEvent(
											new SessionExpiredEvent(
													SessionExpiredType.EXPIREDONSERVER));
						} else {
							retrievedLastTableNull(selectedTRId);
						}
					}

					@Override
					public void onSuccess(TableData result) {
						Log.debug("Retrieve last table: " + result);
						updateTDOpenSessionInfo(result);
					}

				});

	}

	protected void retrievedLastTableNull(TRId trId) {
		if (trId.getTabResourceType().compareTo(TabResourceType.FLOW) == 0) {
			Log.debug(
					"Attention",
					"This tabular resource has type flow and it does not have a valid table, no data entered in the flow");
			AlertMessageBox d = new AlertMessageBox("Attention",
					"No data entered in the flow");
			d.addHideHandler(new HideHandler() {

				public void onHide(HideEvent event) {

				}
			});
			d.show();
		} else {
			Log.debug("Attention",
					"This tabular resource does not have a valid table");
			AlertMessageBox d = new AlertMessageBox("Attention",
					"This tabular resource does not have a valid table");
			d.addHideHandler(new HideHandler() {

				public void onHide(HideEvent event) {
					deleteTRWithLastTableNull();

				}
			});
			d.show();
		}
	}

	protected void deleteTRWithLastTableNull() {
		final ConfirmMessageBox mb = new ConfirmMessageBox("Delete",
				"Would you like to delete this tabular resource without table?");
		// Next in GXT 3.1.1

		mb.addDialogHideHandler(new DialogHideHandler() {

			@Override
			public void onDialogHide(DialogHideEvent event) {
				switch (event.getHideButton()) {
				case NO:

					break;
				case YES:
					callDeleteLastTable();
					break;
				default:
					break;
				}

			}
		});
		// TODO
		/*
		 * GXT 3.0.1 mb.addHideHandler(new HideHandler() { public void
		 * onHide(HideEvent event) { if (mb.getHideButton() ==
		 * mb.getButtonById(PredefinedButton.YES .name())) {
		 * callDeleteLastTable(); } else if (mb.getHideButton() == mb
		 * .getButtonById(PredefinedButton.NO.name())) { // perform NO action }
		 * } });
		 */
		mb.setWidth(300);
		mb.show();

	}

	protected void callDeleteLastTable() {
		Log.debug("Call Delete TR:"
				+ tdOpenSession.getSelectedTabResource().getTrId());
		TDGWTServiceAsync.INSTANCE.removeTabularResource(tdOpenSession
				.getSelectedTabResource().getTrId(), new AsyncCallback<Void>() {

			public void onFailure(Throwable caught) {
				if (caught instanceof TDGWTSessionExpiredException) {
					getEventBus().fireEvent(
							new SessionExpiredEvent(
									SessionExpiredType.EXPIREDONSERVER));
				} else {
					if (caught instanceof TDGWTIsLockedException) {
						Log.error(caught.getLocalizedMessage());
						UtilsGXT3.alert("Error Locked",
								caught.getLocalizedMessage());
					} else {
						Log.error("Error on delete TabResource: "
								+ caught.getLocalizedMessage());
						UtilsGXT3.alert(
								"Error",
								"Error on delete TabResource: "
										+ caught.getLocalizedMessage());
					}

				}

			}

			public void onSuccess(Void result) {
				Log.debug("Remove tabular resource success");
				Log.debug("Check current tr for close: " + trId);
				if (trId != null
						&& trId.getId() != null
						&& trId.getId().compareTo(
								tdOpenSession.getSelectedTabResource()
										.getTrId().getId()) == 0) {
					Log.debug("Fire Close Event on current TR");
					getEventBus().fireEvent(new RibbonEvent(RibbonType.CLOSE));
				} else {
					Log.debug("No tr opened");
				}
				tabResourcesSelectionPanel.gridReload();
			}

		});
	}

	protected void updateTDOpenSessionInfo(TableData table) {
		TabResource tabResource = tdOpenSession.getSelectedTabResource();
		tabResource.setTrId(table.getTrId());
		tdOpenSession.setSelectedTabResource(tabResource);
		Log.debug("TdOpenSession: " + tdOpenSession);
		setTabularResource();
	}

	protected void setTabularResource() {
		TDGWTServiceAsync.INSTANCE.setTabResource(
				tdOpenSession.getSelectedTabResource(),
				new AsyncCallback<Void>() {
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
								Log.error("Error on set TabResource: "
										+ caught.getLocalizedMessage());
								UtilsGXT3.alert(
										"Error",
										"Error on set TabResource: "
												+ caught.getLocalizedMessage());
							}

						}

					}

					public void onSuccess(Void result) {
						getWizardWindow().fireCompleted(
								tdOpenSession.getSelectedTabResource()
										.getTrId());
						getWizardWindow().close(false);
						Log.info("OpenTD Tabular Resource selected :"
								+ tdOpenSession.getSelectedTabResource());
					}

				});

	}

}
