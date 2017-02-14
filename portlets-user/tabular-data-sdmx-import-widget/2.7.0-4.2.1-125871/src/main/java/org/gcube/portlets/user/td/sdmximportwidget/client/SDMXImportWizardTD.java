package org.gcube.portlets.user.td.sdmximportwidget.client;

import org.gcube.portlets.user.td.gwtservice.shared.document.CodelistDocument;
import org.gcube.portlets.user.td.gwtservice.shared.sdmx.SDMXImportSession;
import org.gcube.portlets.user.td.gwtservice.shared.source.SDMXRegistrySource;
import org.gcube.portlets.user.td.wizardwidget.client.WizardWindow;

import com.google.web.bindery.event.shared.EventBus;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class SDMXImportWizardTD extends WizardWindow {

	private SDMXImportSession importSession;

	/**
	 * The id of the {@link CSVTarget} to use.
	 * 
	 * @param targetId
	 */

	public SDMXImportWizardTD(String title, EventBus eventBus) {
		super(title, eventBus);
		setWidth(550);
		setHeight(520);

		importSession = new SDMXImportSession();

		// For multiple sources
		/*
		 * SDMXDocumentSelectionCard sdmxdocumentSelection= new
		 * SDMXDocumentSelectionCard(importSession);
		 * addCard(sdmxdocumentSelection);
		 * 
		 * SourceSelectionCard sourceSelection= new
		 * SourceSelectionCard(importSession); addCard(sourceSelection);
		 */

		final SDMXRegistrySource sdmxRegistrySource = SDMXRegistrySource.INSTANCE;
		importSession.setSource(sdmxRegistrySource);

		final CodelistDocument codelist = CodelistDocument.INSTANCE;
		importSession.setSDMXDocument(codelist);
		
		SDMXCodelistSelectionCard sdmxCodelistSelectionCard = new SDMXCodelistSelectionCard(
				importSession);
		addCard(sdmxCodelistSelectionCard);
		sdmxCodelistSelectionCard.setup();
		
	}

}