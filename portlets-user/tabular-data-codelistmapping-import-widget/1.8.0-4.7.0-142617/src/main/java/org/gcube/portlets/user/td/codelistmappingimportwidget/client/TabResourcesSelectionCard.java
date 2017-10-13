package org.gcube.portlets.user.td.codelistmappingimportwidget.client;

import org.gcube.portlets.user.td.gwtservice.client.rpc.TDGWTServiceAsync;
import org.gcube.portlets.user.td.gwtservice.shared.codelisthelper.CodelistMappingSession;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTSessionExpiredException;
import org.gcube.portlets.user.td.gwtservice.shared.tr.TabResource;
import org.gcube.portlets.user.td.gwtservice.shared.tr.TableData;
import org.gcube.portlets.user.td.widgetcommonevent.client.CommonMessages;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.SessionExpiredEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.SessionExpiredType;
import org.gcube.portlets.user.td.wizardwidget.client.WizardCard;

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
//Next in GXT 3.1.1
//import com.sencha.gxt.widget.core.client.event.DialogHideEvent;
//import com.sencha.gxt.widget.core.client.event.DialogHideEvent.DialogHideHandler;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class TabResourcesSelectionCard extends WizardCard {
	private static CodelistMappingMessages msgs = GWT
			.create(CodelistMappingMessages.class);
	private CodelistMappingSession codelistMappingSession;
	private TabResourcesSelectionCard thisCard;
	private TabResourcesSelectionPanel tabResourcesSelectionPanel;
	private CommonMessages msgsCommon;

	public TabResourcesSelectionCard(
			final CodelistMappingSession codelistMappingSession) {
		super(msgs.tabResourcesSelectionCardHead(), "");
		Log.debug("TabResourcesSelectionCard");
		this.codelistMappingSession = codelistMappingSession;
		thisCard = this;
		initMessages();
		create();
	}

	protected void initMessages() {
		msgsCommon = GWT.create(CommonMessages.class);
	}

	protected void create() {
		tabResourcesSelectionPanel = new TabResourcesSelectionPanel(thisCard,
				res);

		tabResourcesSelectionPanel
				.addSelectionHandler(new SelectionHandler<TabResource>() {

					public void onSelection(SelectionEvent<TabResource> event) {
						codelistMappingSession
								.setConnectedTR(tabResourcesSelectionPanel
										.getSelectedItem());
						setEnableNextButton(true);
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
		setEnableNextButton(false);
		setEnableBackButton(false);

	}

	protected void retrieveLastTable() {
		setEnableNextButton(false);
		setEnableBackButton(false);

		TDGWTServiceAsync.INSTANCE.getLastTable(codelistMappingSession
				.getConnectedTR().getTrId(), new AsyncCallback<TableData>() {

			@Override
			public void onFailure(Throwable caught) {
				if (caught instanceof TDGWTSessionExpiredException) {
					getEventBus().fireEvent(
							new SessionExpiredEvent(
									SessionExpiredType.EXPIREDONSERVER));
				} else {
					Log.debug("Attention",
							"This tabular resource does not have a valid table");
					AlertMessageBox d = new AlertMessageBox(
							msgsCommon.attention(),
							msgs.attentionThisTabularResourceDoesNotHaveAValidTable());
					d.addHideHandler(new HideHandler() {

						public void onHide(HideEvent event) {
							deleteTRWithLastTableNull();

						}
					});
					d.show();

				}
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
				msgs.wouldYouLikeToDeleteThisTabularResourceWithoutTable());

		mb.addDialogHideHandler(new DialogHideHandler() {

			@Override
			public void onDialogHide(DialogHideEvent event) {
				switch (event.getHideButton()) {
				case NO:
					setEnableNextButton(true);
					setEnableBackButton(true);
					break;
				case YES:
					callDeleteLastTable();
					break;
				default:
					setEnableNextButton(true);
					setEnableBackButton(true);
					break;

				}

			}
		});
		mb.setWidth(300);
		mb.show();

	}

	protected void callDeleteLastTable() {
		Log.debug("Delete TR:"
				+ codelistMappingSession.getConnectedTR().getTrId());
		TDGWTServiceAsync.INSTANCE.removeTabularResource(codelistMappingSession
				.getConnectedTR().getTrId(), new AsyncCallback<Void>() {

			public void onFailure(Throwable caught) {
				AlertMessageBox d = new AlertMessageBox(msgsCommon.error(),
						msgs.errorOnDeleteTabularResourceFixed()
								+ caught.getLocalizedMessage());
				d.addHideHandler(new HideHandler() {

					public void onHide(HideEvent event) {
						setEnableNextButton(true);
						setEnableBackButton(true);

					}
				});
				d.show();

			}

			public void onSuccess(Void result) {
				tabResourcesSelectionPanel.gridReload();
				setEnableNextButton(false);
				setEnableBackButton(true);
			}

		});
	}

	protected void updateConnectedTRInfo(TableData table) {
		TabResource tabResource = codelistMappingSession.getConnectedTR();
		tabResource.setTrId(table.getTrId());
		codelistMappingSession.setConnectedTR(tabResource);
		Log.debug("CodelistMappingSession: " + codelistMappingSession);
		goNext();
	}

	protected void goNext() {
		try {
			Log.info("NextCard ColumnSelectionCard");
			ColumnSelectionCard columnSelectionCard = new ColumnSelectionCard(
					codelistMappingSession);
			getWizardWindow().addCard(columnSelectionCard);
			getWizardWindow().nextCard();
		} catch (Throwable e) {
			Log.error("goNext: " + e.getLocalizedMessage());
		}
	}

}
