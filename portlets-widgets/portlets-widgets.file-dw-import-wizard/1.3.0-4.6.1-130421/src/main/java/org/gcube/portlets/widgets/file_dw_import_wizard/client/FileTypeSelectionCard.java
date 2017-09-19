/**
 * 
 */
package org.gcube.portlets.widgets.file_dw_import_wizard.client;

import org.gcube.portlets.widgets.file_dw_import_wizard.client.general.WizardCard;
import org.gcube.portlets.widgets.file_dw_import_wizard.client.rpc.ImportService;
import org.gcube.portlets.widgets.file_dw_import_wizard.client.util.WizardResources;
import org.gcube.portlets.widgets.file_dw_import_wizard.shared.FileType;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.SimpleRadioButton;
import com.google.gwt.user.client.ui.VerticalPanel;

public class FileTypeSelectionCard extends WizardCard {

	protected ImportSession importSession;
	protected VerticalPanel sourceSelectionPanel = new VerticalPanel();

	public FileTypeSelectionCard(final ImportSession importSession) {
		super("File Type selection", "Step 1 of 4");

		this.importSession = importSession;
		importSession.setType("GENERAL");
		sourceSelectionPanel.setSpacing(4);
		sourceSelectionPanel.setWidth("100%");
		sourceSelectionPanel.setHeight("100%");
		getTypePanel();

		// HorizontalPanel sourcePanel = getTypePanel();
		// sourceSelectionPanel.add(sourcePanel);

		// importSession.setSource(source);

		setContent(sourceSelectionPanel);

	}

	protected void getTypePanel() {
		HorizontalPanel sourcePanelgen = new HorizontalPanel();
		sourcePanelgen.setSpacing(2);
		final SimpleRadioButton genericTypeRadioButton = new SimpleRadioButton(
				"typegr");
		final SimpleRadioButton darwingCoreTypeRadioButton = new SimpleRadioButton(
				"typedw");
		genericTypeRadioButton.setValue(true);
		genericTypeRadioButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				
				if (genericTypeRadioButton.getValue())
				{
					importSession.setType("GENERAL");
					darwingCoreTypeRadioButton.setValue(false);
					ImportService.Utility.getInstance().updateFileType
					(importSession.id, FileType.GENERAL, new AsyncCallback<Void>() {

						@Override
						public void onFailure(Throwable caught) {
 							
						}

						@Override
						public void onSuccess(Void result) {
 							
						}
					});
				}
				
}

		});

		darwingCoreTypeRadioButton.setValue(false);
		darwingCoreTypeRadioButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				
				if (darwingCoreTypeRadioButton.getValue())
				{
					importSession.setType("DARWINCORE");
					genericTypeRadioButton.setValue(false);
					ImportService.Utility.getInstance().updateFileType
					(importSession.id, FileType.DARWINCORE, new AsyncCallback<Void>() {

						@Override
						public void onFailure(Throwable caught) {
 							
						}

						@Override
						public void onSuccess(Void result) {
 							
						}
					});
				}
				
			}

		});

		HTML sourceDescription = new HTML();
		StringBuilder description = new StringBuilder();
		description.append("<b>");
		description.append("Generic File");
		description.append("</b><br><p>");
		// description.append("test");
		description.append("</p>");

		sourceDescription.setHTML(description.toString());
//		sourceDescription.addClickHandler(new ClickHandler() {
//
//			@Override
//			public void onClick(ClickEvent event) {
//				if (genericTypeRadioButton.getValue()) {
//					darwingCoreTypeRadioButton.setValue(false);
//				}
//				//
//			}
//		});
		sourceDescription.setStylePrimaryName(WizardResources.INSTANCE
				.wizardCss().getSourceSelectionHover());
		sourcePanelgen.add(sourceDescription);

		sourcePanelgen.add(genericTypeRadioButton);
		sourceSelectionPanel.add(sourcePanelgen);

		sourceSelectionPanel.setSpacing(5);
		HorizontalPanel sourcePanelDw = new HorizontalPanel();

		HTML sourceDescriptionDW = new HTML();
		StringBuilder descriptionDw = new StringBuilder();
		descriptionDw.append("<b>");
		descriptionDw.append("Darwin Core Archive");
		descriptionDw.append("</b><br><p>");
		// description.append("test");
		descriptionDw.append("</p>");
		sourceDescriptionDW.setHTML(descriptionDw.toString());
//		sourceDescriptionDW.addClickHandler(new ClickHandler() {
//
//			@Override
//			public void onClick(ClickEvent event) {
//				if (darwingCoreTypeRadioButton.getValue()) {
//					genericTypeRadioButton.setValue(false);
//					
//				}
//			}
//		});
		sourceDescriptionDW.setStylePrimaryName(WizardResources.INSTANCE
				.wizardCss().getSourceSelectionHover());
		sourcePanelDw.add(sourceDescriptionDW);
		sourcePanelDw.add(darwingCoreTypeRadioButton);
		sourceSelectionPanel.add(sourcePanelDw);

	}

}
