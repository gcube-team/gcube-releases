/**
 * 
 */
package org.gcube.portlets.user.td.csvimportwidget.client;

import org.gcube.portlets.user.td.widgetcommonevent.client.CommonMessages;
import org.gcube.portlets.user.td.wizardwidget.client.WizardCard;
import org.gcube.portlets.user.td.gwtservice.shared.csv.CSVImportSession;
import org.gcube.portlets.user.td.gwtservice.shared.source.FileSource;
import org.gcube.portlets.user.td.gwtservice.shared.source.WorkspaceSource;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.sencha.gxt.core.client.util.ToggleGroup;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.form.Radio;

/**
 * 
 * 
 *        
 * 
 */
public class SourceSelectionCard extends WizardCard {
	
	private static CSVImportWizardTDMessages msgs= GWT.create(CSVImportWizardTDMessages.class);
	
	private final FileSource fileSource = FileSource.INSTANCE;
	private final WorkspaceSource workspaceSource = WorkspaceSource.INSTANCE;

	private final CSVImportSession importSession;

	private CommonMessages msgsCommon;
	
	

	public SourceSelectionCard(final CSVImportSession importSession) {
		super(msgs.csvSourceSelection(), "");
		initMessages();
		this.importSession = importSession;
		// Default
		importSession.setSource(fileSource);

		VerticalPanel sourceSelectionPanel = new VerticalPanel();
		sourceSelectionPanel.setStylePrimaryName(res.wizardCSS()
				.getImportSelectionSources());

		Radio radioFileSource = new Radio();
		radioFileSource.setBoxLabel("<p style='display:inline-table;'><b>"
				+ msgsCommon.fileSourceName() + "</b><br>"
				+ msgsCommon.fileSourceDescription() + "</p>");
		radioFileSource.setName(fileSource.getName());
		radioFileSource.setStylePrimaryName(res.wizardCSS()
				.getImportSelectionSource());
		radioFileSource.setValue(true);

		Radio radioWorkspaceSource = new Radio();
		radioWorkspaceSource.setBoxLabel("<p style='display:inline-table;'><b>"
				+ msgsCommon.workspaceSourceName() + "</b><br>"
				+ msgsCommon.workspaceSourceDescription() + "</p>");
		radioWorkspaceSource.setName(workspaceSource.getName());
		radioWorkspaceSource.setStylePrimaryName(res.wizardCSS()
				.getImportSelectionSource());
		// radioWorkspaceSource.disable();

		sourceSelectionPanel.add(radioFileSource);
		sourceSelectionPanel.add(radioWorkspaceSource);

		// we can set name on radios or use toggle group
		ToggleGroup toggle = new ToggleGroup();
		toggle.add(radioWorkspaceSource);
		toggle.add(radioFileSource);

		toggle.addValueChangeHandler(new ValueChangeHandler<HasValue<Boolean>>() {

			
			public void onValueChange(ValueChangeEvent<HasValue<Boolean>> event) {
				try {
					ToggleGroup group = (ToggleGroup) event.getSource();
					Radio radio = (Radio) group.getValue();
					Log.info("Source Selected:" + radio.getName());
					if (radio.getName().compareTo(workspaceSource.getName()) == 0) {
						importSession.setSource(workspaceSource);
					} else {
						if (radio.getName().compareTo(fileSource.getName()) == 0) {
							importSession.setSource(fileSource);
						} else {

						}

					}

				} catch (Exception e) {
					Log.error("ToggleGroup: onValueChange "
							+ e.getLocalizedMessage());
				}

			}
		});

		setCenterWidget(sourceSelectionPanel, new MarginData(0));

	}
	
	protected void initMessages(){
		msgsCommon = GWT.create(CommonMessages.class);
	}

	@Override
	public void setup() {
		Log.debug("Setup Card");
		Command sayNextCard = new Command() {
			public void execute() {
				try {
					String sourceId = importSession.getSource().getId();
					if (sourceId == null || sourceId.isEmpty()) {
						Log.error("CSV Import Source Id: " + sourceId);
					} else {
						if (sourceId.compareTo("File") == 0) {
							Log.info("NextCard CSVUploadFileCard");
							CSVUploadFileCard csvUploadFileCard = new CSVUploadFileCard(
									importSession);
							getWizardWindow().addCard(csvUploadFileCard);
							getWizardWindow().nextCard();
						} else {
							if (sourceId.compareTo("Workspace") == 0) {
								Log.info("NextCard CSVWorkspaceSelectionCard");
								CSVWorkSpaceSelectionCard csvWorkspaceSelectionCard = new CSVWorkSpaceSelectionCard(
										importSession);
								getWizardWindow().addCard(
										csvWorkspaceSelectionCard);
								getWizardWindow().nextCard();
							} else {
								Log.debug("No source selected and no card loaded");
							}
						}
					}
				} catch (Exception e) {
					Log.error("sayNextCard :" + e.getLocalizedMessage());
					e.printStackTrace();
				}
			}
		};
		
		getWizardWindow().setNextButtonCommand(sayNextCard);
		setNextButtonVisible(true);
		setBackButtonVisible(false);
		
	}

}
