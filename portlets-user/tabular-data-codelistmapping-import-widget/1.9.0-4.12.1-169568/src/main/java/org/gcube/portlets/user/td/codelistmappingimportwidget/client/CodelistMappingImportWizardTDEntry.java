package org.gcube.portlets.user.td.codelistmappingimportwidget.client;

import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.web.bindery.event.shared.SimpleEventBus;

/**
 * 
 * @author Giancarlo Panichi
 *   
 *
 */
public class CodelistMappingImportWizardTDEntry implements EntryPoint {

	private CodelistMappingMessages msgs;

	public void onModuleLoad() {
		initMessages();
		TRId trId = new TRId();
		SimpleEventBus eventBus = new SimpleEventBus();
		CodelistMappingImportWizardTD importWizard = new CodelistMappingImportWizardTD(
				trId, msgs.codelistMappingImportWizardHead(), eventBus);
		Log.info(importWizard.getId());
	}

	private void initMessages() {
		msgs = GWT.create(CodelistMappingMessages.class);
	}

}
