/**
 * 
 */
package org.gcube.portlets.user.td.csvexportwidget.client;



import org.gcube.portlets.user.td.gwtservice.client.rpc.TDGWTServiceAsync;
import org.gcube.portlets.user.td.gwtservice.shared.csv.CSVExportSession;
import org.gcube.portlets.user.td.gwtservice.shared.destination.FileDestination;
import org.gcube.portlets.user.td.gwtservice.shared.destination.WorkspaceDestination;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTIsLockedException;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTSessionExpiredException;
import org.gcube.portlets.user.td.gwtservice.shared.tr.TabResource;
import org.gcube.portlets.user.td.widgetcommonevent.client.CommonMessages;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.SessionExpiredEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.SessionExpiredType;
import org.gcube.portlets.user.td.wizardwidget.client.WizardCard;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.sencha.gxt.core.client.util.ToggleGroup;
import com.sencha.gxt.widget.core.client.form.Radio;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class DestinationSelectionCard extends WizardCard {
	private static CSVExportWizardTDMessages msgs = GWT.create(CSVExportWizardTDMessages.class);
	private CommonMessages msgsCommon;
	
	private final CSVExportSession exportSession;
	private DestinationSelectionCard thisCard;
	
	private final FileDestination fileDestination = FileDestination.INSTANCE;
	private final WorkspaceDestination workspaceDestination = WorkspaceDestination.INSTANCE;

	public DestinationSelectionCard(final CSVExportSession exportSession) {
		super(msgs.destinationSelectionCardHead(), "");
		thisCard=this;
		this.exportSession = exportSession;
		
		initMessages();
		
		// Default
		exportSession.setDestination(workspaceDestination);
		
		
		retrieveTabularResource();
		
	}
	
	protected void initMessages(){
		msgsCommon = GWT.create(CommonMessages.class);
	}
	
	protected void create(){
		VerticalPanel destinationSelectionPanel = new VerticalPanel();
		destinationSelectionPanel.setStylePrimaryName(res.wizardCSS()
				.getImportSelectionSources());

		Radio radioWorkspaceDestination = new Radio();
		radioWorkspaceDestination.setBoxLabel("<p style='display:inline-table;'><b>"
				+ msgsCommon.workspaceDestinationName() + "</b><br>"
				+ msgsCommon.workspaceDestinationDescription() + "</p>");
		radioWorkspaceDestination.setName(workspaceDestination.getName());
		radioWorkspaceDestination.setStylePrimaryName(res.wizardCSS()
				.getImportSelectionSource());
		radioWorkspaceDestination.setValue(true);
		
		
		Radio radioFileDestination = new Radio();
		radioFileDestination.setBoxLabel("<p style='display:inline-table;'><b>"
				+ msgsCommon.fileDestinationName() + "</b><br>"
				+ msgsCommon.fileDestinationDescription() + "</p>");
		radioFileDestination.setName(fileDestination.getName());
		radioFileDestination.setStylePrimaryName(res.wizardCSS()
				.getImportSelectionSource());
		radioFileDestination.disable();
		
		destinationSelectionPanel.add(radioWorkspaceDestination);
		destinationSelectionPanel.add(radioFileDestination);
		

		// we can set name on radios or use toggle group
		ToggleGroup toggle = new ToggleGroup();
		toggle.add(radioWorkspaceDestination);
		toggle.add(radioFileDestination);

		toggle.addValueChangeHandler(new ValueChangeHandler<HasValue<Boolean>>() {

			
			public void onValueChange(ValueChangeEvent<HasValue<Boolean>> event) {
				try {
					ToggleGroup group = (ToggleGroup) event.getSource();
					Radio radio = (Radio) group.getValue();
					Log.info("Source Selected:" + radio.getName());
					if (radio.getName().compareTo(workspaceDestination.getName()) == 0) {
						exportSession.setDestination(workspaceDestination);
					} else {
						if (radio.getName().compareTo(fileDestination.getName()) == 0) {
							exportSession.setDestination(fileDestination);
						} else {

						}

					}

				} catch (Exception e) {
					Log.error("ToggleGroup: onValueChange "
							+ e.getLocalizedMessage());
				}

			}
		});
		
		setContent(destinationSelectionPanel);
		forceLayout();
	}
	
	
	protected void retrieveTabularResource() {
		TDGWTServiceAsync.INSTANCE
				.getTabResourceInformation(new AsyncCallback<TabResource>() {

					public void onSuccess(TabResource result) {
						Log.info("Retrived TR: " + result.getTrId());
						exportSession.setTabResource(result);
						create();
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
								showErrorAndHide(msgsCommon.errorLocked(),
										caught.getLocalizedMessage(), "",
										caught);
							} else {
								showErrorAndHide(
										msgsCommon.error(),
										msgs.errorRetrievingTabularResourceInfo(),
										caught.getLocalizedMessage(), caught);

							}
						}

					}

				});

	}
	
	

	@Override
	public void setup() {
		Log.debug("Setup Card");
		Command sayNextCard = new Command() {
			public void execute() {
				try {
					String destinationId = exportSession.getDestination().getId();
					if (destinationId == null || destinationId.isEmpty()) {
						Log.error("CSV Export Source Id: " + destinationId);
					} else {
						if (destinationId.compareTo("File") == 0) {
							Log.info("NextCard CSVUploadFileCard");
							DownloadFileCard downloadFileCard = new DownloadFileCard(
									exportSession);
							getWizardWindow().addCard(downloadFileCard);
							getWizardWindow().nextCard();
						} else {
							if (destinationId.compareTo("Workspace") == 0) {
								Log.info("NextCard CSVWorkspaceSelectionCard");
								CSVWorkSpaceSelectionCard csvWorkspaceSelectionCard = new CSVWorkSpaceSelectionCard(
										exportSession);
								getWizardWindow().addCard(
										csvWorkspaceSelectionCard);
								getWizardWindow().nextCard();
							} else {
								Log.debug("No destination selected and no card loaded");
							}
						}
					}
				} catch (Exception e) {
					Log.error("sayNextCard :" + e.getLocalizedMessage());
				}
			}
		};

		getWizardWindow().setNextButtonCommand(sayNextCard);
		
		Command sayPreviousCard = new Command() {
			public void execute() {
				try {
					getWizardWindow().previousCard();
					getWizardWindow().removeCard(thisCard);
					Log.info("Remove DestinationSelectionCard");
				} catch (Exception e) {
					Log.error("sayPreviousCard :" + e.getLocalizedMessage());
				}
			}
	    };
	   
	    getWizardWindow().setPreviousButtonCommand(sayPreviousCard);
	    
	    setEnableBackButton(true);
		setEnableNextButton(true);
	    
	}
	
	
}
