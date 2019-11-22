package org.gcube.portlets.user.td.unionwizardwidget.client;

import org.gcube.portlets.user.td.gwtservice.client.rpc.TDGWTServiceAsync;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTIsLockedException;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTSessionExpiredException;
import org.gcube.portlets.user.td.gwtservice.shared.tr.TabResource;
import org.gcube.portlets.user.td.gwtservice.shared.tr.TableData;
import org.gcube.portlets.user.td.gwtservice.shared.tr.union.UnionSession;
import org.gcube.portlets.user.td.widgetcommonevent.client.CommonMessages;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.SessionExpiredEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.SessionExpiredType;
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
import com.sencha.gxt.widget.core.client.event.DialogHideEvent;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent.DialogHideHandler;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;

public class TabResourcesSelectionCard extends WizardCard {
	private static UnionWizardMessages msgs= GWT.create(UnionWizardMessages.class);
	private CommonMessages msgsCommon;
	private UnionSession unionSession;
	private TabResourcesSelectionCard thisCard;
	private TabResourcesSelectionPanel tabResourcesSelectionPanel;
	//private TabResource selectedTabResource = null;
	

	public TabResourcesSelectionCard(final UnionSession unionSession) {
		super(msgs.tabResourcesSelectionCardHead(), "");
		Log.debug("TabResourcesSelectionCard");
		this.unionSession = unionSession;
		thisCard = this;
		initMessages();
		
		tabResourcesSelectionPanel = new TabResourcesSelectionPanel(thisCard,
				res);

		tabResourcesSelectionPanel
				.addSelectionHandler(new SelectionHandler<TabResource>() {

					public void onSelection(SelectionEvent<TabResource> event) {
						unionSession
								.setUnionTabularResource(tabResourcesSelectionPanel
										.getSelectedItem());
						getWizardWindow().setEnableNextButton(true);
					}

				});

		setContent(tabResourcesSelectionPanel);

	}
	
	protected void initMessages(){
		msgsCommon= GWT.create(CommonMessages.class);
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
		
		setEnableNextButton(false);
		setBackButtonVisible(false);
		setEnableBackButton(false);
		setNextButtonVisible(true);
		
		tabResourcesSelectionPanel.gridDeselectAll();
		
	}

	protected void retrieveLastTable() {
		setEnableNextButton(false);
		setEnableBackButton(false);

		TDGWTServiceAsync.INSTANCE.getLastTable(unionSession
				.getUnionTabularResource().getTrId(),
				new AsyncCallback<TableData>() {

					@Override
					public void onFailure(Throwable caught) {
						Log.debug("Attention",
								"This tabular resource does not have a valid table");
						AlertMessageBox d = new AlertMessageBox(msgsCommon.attention(),
								msgs.attentionThisTabularResourceDoesNotHaveAValidTable());
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
		final ConfirmMessageBox mb = new ConfirmMessageBox(msgs.delete(),
				msgs.woultYouLikeToDeleteThisTabularResourceWithoutTable());
		/*Next in GXT 3.1.1 */
		 
		mb.addDialogHideHandler(new DialogHideHandler() {

			@Override
			public void onDialogHide(DialogHideEvent event) {
				switch (event.getHideButton()) {
				case NO:
					setEnableNextButton(false);
					setEnableBackButton(false);
					break;
				case YES:
					callDeleteLastTable();
					break;
				default:
					break;

				}

			}
		});
		
		
		
		/* GXT 3.0.1
		mb.addHideHandler(new HideHandler() {
			public void onHide(HideEvent event) {
				
				  if (mb.getHideButton() == mb.getButtonById(PredefinedButton.YES
				 
						.name())) {
					callDeleteLastTable();

				} else if (mb.getHideButton() == mb
						.getButtonById(PredefinedButton.NO.name())) {
					getWizardWindow().setEnableNextButton(false);
					getWizardWindow().setEnableBackButton(false);
				}
			
			}
			
		});*/
		mb.setWidth(300);
		mb.show();

	}

	protected void callDeleteLastTable() {
		Log.debug("Delete TR:"
				+ unionSession.getUnionTabularResource().getTrId());
		TDGWTServiceAsync.INSTANCE.removeTabularResource(unionSession
				.getUnionTabularResource().getTrId(),
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
								UtilsGXT3.alert(msgsCommon.errorLocked(),
										caught.getLocalizedMessage());
								setEnableNextButton(false);
								setEnableBackButton(false);
							} else {

								AlertMessageBox d = new AlertMessageBox(
										msgsCommon.error(),
										msgs.errorOnDeleteTabularResourceFixed()
												+ caught.getLocalizedMessage());
								d.addHideHandler(new HideHandler() {

									public void onHide(HideEvent event) {
										setEnableNextButton(
												true);
										setEnableBackButton(
												true);

									}

								});
								d.show();
			

							}
						}

					}

					public void onSuccess(Void result) {
						tabResourcesSelectionPanel.gridReload();
						setEnableNextButton(false);
						setEnableBackButton(false);
					}

				});
	}

	protected void updateConnectedTRInfo(TableData table) {
		TabResource tabResource = unionSession.getUnionTabularResource();
		tabResource.setTrId(table.getTrId());
		unionSession.setUnionTabularResource(tabResource);
		Log.debug("UnionSession: " + unionSession);
		retriveCurrentTabularResourceInfo();
	}

	protected void retriveCurrentTabularResourceInfo() {
		TDGWTServiceAsync.INSTANCE.getTabResourceInformation(
				unionSession.getTrId(), new AsyncCallback<TabResource>() {

					public void onSuccess(TabResource result) {
						Log.info("Retrived TR: " + result.getTrId());
						unionSession.setCurrentTabularResource(result);
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
								UtilsGXT3.alert(msgsCommon.errorLocked(),
										caught.getLocalizedMessage());
								setEnableNextButton(false);
								setEnableBackButton(false);
							} else {
								UtilsGXT3
										.alert(msgsCommon.error(),
												msgs.errorRetrievingInfomationOnTRFixed());
								setEnableNextButton(false);
								setEnableBackButton(false);
							}
						}
					}

				});
	}

	protected void goNext() {
		try {
			Log.info("NextCard ColumnMappingCard");
			ColumnMappingCard columnSelectionCard = new ColumnMappingCard(
					unionSession);
			getWizardWindow().addCard(columnSelectionCard);
			getWizardWindow().nextCard();
		} catch (Throwable e) {
			Log.error("goNext: " + e.getLocalizedMessage());
		}
	}

}
