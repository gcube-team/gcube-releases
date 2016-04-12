package org.gcube.portlets.user.td.replacebyexternalwidget.client;

import org.gcube.portlets.user.td.gwtservice.client.rpc.TDGWTServiceAsync;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTIsLockedException;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTSessionExpiredException;
import org.gcube.portlets.user.td.gwtservice.shared.tr.TabResource;
import org.gcube.portlets.user.td.gwtservice.shared.tr.TableData;
import org.gcube.portlets.user.td.gwtservice.shared.tr.replacebyexternal.ReplaceByExternalSession;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.SessionExpiredEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.SessionExpiredType;
import org.gcube.portlets.user.td.wizardwidget.client.WizardCard;
import org.gcube.portlets.user.td.wizardwidget.client.util.UtilsGXT3;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import com.sencha.gxt.widget.core.client.box.ConfirmMessageBox;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent.DialogHideHandler;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class TabResourcesSelectionCard extends WizardCard {
	protected ReplaceByExternalSession replaceByExternalSession;
	protected TabResourcesSelectionCard thisCard;
	protected TabResourcesSelectionPanel tabResourcesSelectionPanel;
	protected TabResource selectedTabResource = null;

	public TabResourcesSelectionCard(
			final ReplaceByExternalSession replaceByExternalSession) {
		super("Select Tabular Resource for Replace By External", "");
		Log.debug("TabResourcesSelectionCard");
		this.replaceByExternalSession = replaceByExternalSession;
		thisCard = this;

		tabResourcesSelectionPanel = new TabResourcesSelectionPanel(thisCard,
				res);

		tabResourcesSelectionPanel
				.addSelectionHandler(new SelectionHandler<TabResource>() {

					public void onSelection(SelectionEvent<TabResource> event) {
						replaceByExternalSession
								.setExternalTabularResource(tabResourcesSelectionPanel
										.getSelectedItem());
						getWizardWindow().setEnableNextButton(true);
					}

				});

		setContent(tabResourcesSelectionPanel);

	}

	@Override
	public void setup() {
		Log.debug("TabResourcesSelectionCard Call Setup ");
		Command sayNextCard = new Command() {

			public void execute() {
				Log.debug("TabResourcesSelectionCard Call sayNextCard");
				retrieveLastTable();

			}

		};

		getWizardWindow().setNextButtonCommand(sayNextCard);

		Command sayPreviousCard = new Command() {
			public void execute() {
				try {
					getWizardWindow().previousCard();
					getWizardWindow().removeCard(thisCard);
					Log.debug("Remove TabResourcesSelectionCard");
				} catch (Exception e) {
					Log.error("sayPreviousCard :" + e.getLocalizedMessage());
				}
			}
		};

		getWizardWindow().setPreviousButtonCommand(sayPreviousCard);
		getWizardWindow().setEnableNextButton(false);
		getWizardWindow().setEnableBackButton(true);

	}

	protected void retrieveLastTable() {
		getWizardWindow().setEnableNextButton(false);
		getWizardWindow().setEnableBackButton(false);

		TDGWTServiceAsync.INSTANCE.getLastTable(replaceByExternalSession
				.getExternalTabularResource().getTrId(),
				new AsyncCallback<TableData>() {

					@Override
					public void onFailure(Throwable caught) {
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

					@Override
					public void onSuccess(TableData result) {
						Log.debug("Retrieve last table: " + result);
						updateConnectedTRInfo(result);
					}

				});

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
					getWizardWindow().setEnableNextButton(false);
					getWizardWindow().setEnableBackButton(true);
					break;
				case YES:
					callDeleteLastTable();
					break;
				default:
					getWizardWindow().setEnableNextButton(false);
					getWizardWindow().setEnableBackButton(true);
					break;

				}

			}
		});

		// TODO
		/*
		 * GXT 3.0.1 mb.addHideHandler(new HideHandler() { public void
		 * onHide(HideEvent event) { if (mb.getHideButton() ==
		 * mb.getButtonById(PredefinedButton.YES .name())) {
		 * callDeleteLastTable();
		 * 
		 * } else if (mb.getHideButton() == mb
		 * .getButtonById(PredefinedButton.NO.name())) {
		 * getWizardWindow().setEnableNextButton(false);
		 * getWizardWindow().setEnableBackButton(true); } } });
		 */
		mb.setWidth(300);
		mb.show();

	}

	protected void callDeleteLastTable() {
		Log.debug("Delete TR:"
				+ replaceByExternalSession.getExternalTabularResource()
						.getTrId());
		TDGWTServiceAsync.INSTANCE
				.removeTabularResource(replaceByExternalSession
						.getExternalTabularResource().getTrId(),
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
										getWizardWindow().setEnableNextButton(
												false);
										getWizardWindow().setEnableBackButton(
												true);
									} else {

										AlertMessageBox d = new AlertMessageBox(
												"Error",
												"Error on delete TabResource: "
														+ caught.getLocalizedMessage());
										d.addHideHandler(new HideHandler() {

											public void onHide(HideEvent event) {
												getWizardWindow()
														.setEnableNextButton(
																false);
												getWizardWindow()
														.setEnableBackButton(
																true);

											}

										});
										d.show();

									}
								}

							}

							public void onSuccess(Void result) {
								tabResourcesSelectionPanel.gridReload();
								getWizardWindow().setEnableNextButton(false);
								getWizardWindow().setEnableBackButton(true);
							}

						});
	}

	protected void updateConnectedTRInfo(TableData table) {
		TabResource tabResource = replaceByExternalSession
				.getExternalTabularResource();
		tabResource.setTrId(table.getTrId());
		replaceByExternalSession.setExternalTabularResource(tabResource);
		Log.debug("ReplaceByExternalSession: " + replaceByExternalSession);
		retriveCurrentTabularResourceInfo();
	}

	protected void retriveCurrentTabularResourceInfo() {
		TDGWTServiceAsync.INSTANCE.getTabResourceInformation(
				replaceByExternalSession.getTrId(),
				new AsyncCallback<TabResource>() {

					public void onSuccess(TabResource result) {
						Log.info("Retrived TR: " + result.getTrId());
						replaceByExternalSession
								.setCurrentTabularResource(result);
						goNext();
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
								UtilsGXT3.alert("Error Locked",
										caught.getLocalizedMessage());
								getWizardWindow().setEnableNextButton(false);
								getWizardWindow().setEnableBackButton(true);
							} else {
								UtilsGXT3
										.alert("Error",
												"Error retrienving information on current tabular resource: ");
								getWizardWindow().setEnableNextButton(false);
								getWizardWindow().setEnableBackButton(true);
							}
						}
					}

				});
	}

	protected void goNext() {
		try {
			Log.info("NextCard ColumnMappingCard");
			ColumnMappingCard columnSelectionCard = new ColumnMappingCard(
					replaceByExternalSession);
			getWizardWindow().addCard(columnSelectionCard);
			getWizardWindow().nextCard();
		} catch (Throwable e) {
			Log.error("goNext: " + e.getLocalizedMessage());
		}
	}

}
