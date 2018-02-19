/**
 * 
 */
package org.gcube.portlets.user.td.resourceswidget.client.save;




import org.gcube.portlets.user.td.gwtservice.shared.destination.FileDestination;
import org.gcube.portlets.user.td.gwtservice.shared.destination.WorkspaceDestination;
import org.gcube.portlets.user.td.gwtservice.shared.tr.resources.SaveResourceSession;
import org.gcube.portlets.user.td.widgetcommonevent.client.CommonMessages;
import org.gcube.portlets.user.td.wizardwidget.client.WizardCard;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.sencha.gxt.core.client.util.ToggleGroup;
import com.sencha.gxt.widget.core.client.form.Radio;

/**
 * 
 * @author Giancarlo Panichi
 *
 * 
 */
public class DestinationSelectionCard extends WizardCard {
	private static SaveResourceMessages msgs=GWT.create(SaveResourceMessages.class);
	private final SaveResourceSession saveResourceSession;
	private DestinationSelectionCard thisCard;
	
	private final FileDestination fileDestination = FileDestination.INSTANCE;
	private final WorkspaceDestination workspaceDestination = WorkspaceDestination.INSTANCE;
	private CommonMessages msgsCommon;

	public DestinationSelectionCard(final SaveResourceSession saveResourceSession) {
		super(msgs.destinationSelectionCardHead(), "");
		initMessages();
		thisCard=this;
		this.saveResourceSession = saveResourceSession;
		// Default
		saveResourceSession.setDestination(workspaceDestination);

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
						saveResourceSession.setDestination(workspaceDestination);
					} else {
						if (radio.getName().compareTo(fileDestination.getName()) == 0) {
							saveResourceSession.setDestination(fileDestination);
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

	}
	
	protected void initMessages(){
		msgsCommon=GWT.create(CommonMessages.class);
	}
	

	@Override
	public void setup() {
		Log.debug("Setup Card");
		Command sayNextCard = new Command() {
			public void execute() {
				try {
					String destinationId = saveResourceSession.getDestination().getId();
					if (destinationId == null || destinationId.isEmpty()) {
						Log.error("Export Source Id: " + destinationId);
					} else {
						if (destinationId.compareTo("File") == 0) {
							Log.info("NextCard DownloadFileCard");
							DownloadFileCard downloadFileCard = new DownloadFileCard(
									saveResourceSession);
							getWizardWindow().addCard(downloadFileCard);
							getWizardWindow().nextCard();
						} else {
							if (destinationId.compareTo("Workspace") == 0) {
								Log.info("NextCard WorkspaceSelectionCard");
								WorkSpaceSelectionCard workspaceSelectionCard = new WorkSpaceSelectionCard(
										saveResourceSession);
								getWizardWindow().addCard(
										workspaceSelectionCard);
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
	    
	    setEnableBackButton(false);
		setBackButtonVisible(false);
	    setEnableNextButton(true);
	    
	}
	
	
}
