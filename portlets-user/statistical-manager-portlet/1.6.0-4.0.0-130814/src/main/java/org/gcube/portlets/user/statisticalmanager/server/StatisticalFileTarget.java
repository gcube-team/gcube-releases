package org.gcube.portlets.user.statisticalmanager.server;

import java.io.File;

import javax.servlet.http.HttpSession;

import org.gcube.portlets.widgets.file_dw_import_wizard.client.progress.OperationProgress;
import org.gcube.portlets.widgets.file_dw_import_wizard.client.progress.OperationState;
import org.gcube.portlets.widgets.file_dw_import_wizard.server.file.Target;
import org.gcube.portlets.widgets.file_dw_import_wizard.shared.FileType;

public class StatisticalFileTarget implements Target {

	@Override
	public String getId() {
		return "StatisticalFileTarget";
	}

	@Override
	public void importFile(HttpSession session, File file, String fileName,
			FileType type, File generatedTaxa, File generatedVernacular,
			boolean[] columnToImportMask, OperationProgress operationProgress) {
		session.setAttribute("fileImportPath", file.getAbsolutePath());
		session.setAttribute("typeFile", type.toString());
		if (type == FileType.DARWINCORE) {

			if (generatedTaxa != null) {
				session.setAttribute("generatedTaxaFilePath",
						generatedTaxa.getAbsolutePath());

			} else
				session.setAttribute("generatedTaxaFilePath", null);
			
			if (generatedVernacular != null) {
				session.setAttribute("generatedVernacular",
						generatedVernacular.getAbsolutePath());

			} else
				session.setAttribute("generatedVernacular", null);
		}
		System.out.println("IMPORT SERVER OK, fileName: " + file.getName());
		operationProgress.setState(OperationState.COMPLETED);

	}
}
