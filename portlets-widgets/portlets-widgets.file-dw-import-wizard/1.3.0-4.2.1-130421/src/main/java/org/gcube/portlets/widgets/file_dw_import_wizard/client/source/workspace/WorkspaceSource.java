package org.gcube.portlets.widgets.file_dw_import_wizard.client.source.workspace;

import org.gcube.portlets.widgets.file_dw_import_wizard.client.ImportSession;
import org.gcube.portlets.widgets.file_dw_import_wizard.client.general.WizardCard;
import org.gcube.portlets.widgets.file_dw_import_wizard.client.source.local.Source;

import com.extjs.gxt.ui.client.widget.Component;

public class WorkspaceSource implements Source {
	public static final WorkspaceSource INSTANCE = new WorkspaceSource();

	@Override
	public String getId() {
		return "workspace";

	}

	@Override
	public String getName() {
		return "Workspace source";
	}

	@Override
	public String getDescription() {
		return "Select this source if you want to get the  file from your workspace.";
	}

	@Override
	public Component getPanel(WizardCard card, ImportSession session) {
		return new WorkspaceUploadPanel(card, session);
	}

}
