/**
 * 
 */
package org.gcube.portlets.user.td.codelistmappingimportwidget.client;

import org.gcube.portlets.user.td.gwtservice.shared.codelisthelper.CodelistMappingSession;
import org.gcube.portlets.user.td.gwtservice.shared.source.FileSource;
import org.gcube.portlets.user.td.gwtservice.shared.source.SourceType;
import org.gcube.portlets.user.td.gwtservice.shared.source.UrlSource;
import org.gcube.portlets.user.td.gwtservice.shared.source.WorkspaceSource;
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
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class SourceSelectionCard extends WizardCard {
	private static final CodelistMappingMessages msgs = GWT
			.create(CodelistMappingMessages.class);
	private final CodelistMappingSession codelistMappingSession;
	private final UrlSource urlSource = UrlSource.INSTANCE;
	private final FileSource fileSource = FileSource.INSTANCE;
	private final WorkspaceSource workspaceSource = WorkspaceSource.INSTANCE;
	private CommonMessages msgsCommon;

	public SourceSelectionCard(
			final CodelistMappingSession codelistMappingSession) {
		super(msgs.sourceSelectionCardHead(), "");
		this.codelistMappingSession = codelistMappingSession;
		initMessages();
		create();
		
	}

	protected void initMessages() {
		msgsCommon = GWT.create(CommonMessages.class);
	}
	
	protected void create(){

		// Default
		codelistMappingSession.setSource(urlSource);

		VerticalPanel sourceSelectionPanel = new VerticalPanel();
		sourceSelectionPanel.setStylePrimaryName(res.wizardCSS()
				.getImportSelectionSources());

		Radio radioUrlSource = new Radio();
		radioUrlSource.setBoxLabel("<p style='display:inline-table;'><b>"
				+ msgsCommon.urlSourceName() + "</b><br>"
				+ msgsCommon.urlSourceDescription() + "</p>");
		radioUrlSource.setName(urlSource.getName());
		radioUrlSource.setStylePrimaryName(res.wizardCSS()
				.getImportSelectionSource());
		radioUrlSource.setValue(true);

		Radio radioFileSource = new Radio();
		radioFileSource.setBoxLabel("<p style='display:inline-table;'><b>"
				+ msgsCommon.fileSourceName() + "</b><br>"
				+ msgsCommon.fileSourceDescription() + "</p>");
		radioFileSource.setName(fileSource.getName());
		radioFileSource.setStylePrimaryName(res.wizardCSS()
				.getImportSelectionSource());

		Radio radioWorkspaceSource = new Radio();
		radioWorkspaceSource.setBoxLabel("<p style='display:inline-table;'><b>"
				+ msgsCommon.workspaceSourceName() + "</b><br>"
				+ msgsCommon.workspaceSourceDescription() + "</p>");
		radioWorkspaceSource.setName(workspaceSource.getName());
		radioWorkspaceSource.setStylePrimaryName(res.wizardCSS()
				.getImportSelectionSource());
		// radioWorkspaceSource.disable();

		sourceSelectionPanel.add(radioUrlSource);
		sourceSelectionPanel.add(radioFileSource);
		sourceSelectionPanel.add(radioWorkspaceSource);

		// we can set name on radios or use toggle group
		ToggleGroup toggle = new ToggleGroup();
		toggle.add(radioUrlSource);
		toggle.add(radioWorkspaceSource);
		toggle.add(radioFileSource);

		toggle.addValueChangeHandler(new ValueChangeHandler<HasValue<Boolean>>() {

			public void onValueChange(ValueChangeEvent<HasValue<Boolean>> event) {
				try {
					ToggleGroup group = (ToggleGroup) event.getSource();
					Radio radio = (Radio) group.getValue();
					Log.info("Source Selected:" + radio.getName());
					if (radio.getName().compareTo(urlSource.getName()) == 0) {
						codelistMappingSession.setSource(urlSource);
					} else {

						if (radio.getName()
								.compareTo(workspaceSource.getName()) == 0) {
							codelistMappingSession.setSource(workspaceSource);
						} else {
							if (radio.getName().compareTo(fileSource.getName()) == 0) {
								codelistMappingSession.setSource(fileSource);
							} else {

							}

						}
					}

				} catch (Exception e) {
					Log.error("ToggleGroup: onValueChange "
							+ e.getLocalizedMessage());
				}

			}
		});

		setContent(sourceSelectionPanel);

	}
	
	
	@Override
	public void setup() {
		Log.debug("Setup Card");
		Command sayNextCard = new Command() {
			public void execute() {
				try {
					String sourceId = codelistMappingSession.getSource()
							.getId();
					if (sourceId == null || sourceId.isEmpty()) {
						Log.error("Codelist Mapping Import Source Id: "
								+ sourceId);
					} else {
						if (sourceId.compareTo(SourceType.FILE.toString()) == 0) {
							Log.info("NextCard CodelistMappingUploadFileCard");
							CodelistMappingUploadFileCard codelistMappingUploadFileCard = new CodelistMappingUploadFileCard(
									codelistMappingSession);
							getWizardWindow().addCard(
									codelistMappingUploadFileCard);
							getWizardWindow().nextCard();
						} else {
							if (sourceId.compareTo(SourceType.WORKSPACE
									.toString()) == 0) {
								Log.info("NextCard CSVWorkspaceSelectionCard");
								CodelistMappingWorkSpaceSelectionCard codelistMappingWorkspaceSelectionCard = new CodelistMappingWorkSpaceSelectionCard(
										codelistMappingSession);
								getWizardWindow().addCard(
										codelistMappingWorkspaceSelectionCard);
								getWizardWindow().nextCard();
							} else {
								if (sourceId.compareTo(SourceType.URL
										.toString()) == 0) {
									Log.info("NextCard CodelistMappingURLSelectionCard");
									CodelistMappingUrlSelectionCard codelistMappingUrlSelectionCard = new CodelistMappingUrlSelectionCard(
											codelistMappingSession);
									getWizardWindow().addCard(
											codelistMappingUrlSelectionCard);
									getWizardWindow().nextCard();
								} else {
									Log.debug("No source selected and no card loaded");
								}
							}
						}
					}
				} catch (Exception e) {
					Log.error("sayNextCard :" + e.getLocalizedMessage());
				}
			}
		};

		getWizardWindow().setNextButtonCommand(sayNextCard);

		setEnableNextButton(true);
		setNextButtonVisible(true);
		setEnableBackButton(false);
		setBackButtonVisible(false);

	}

}
