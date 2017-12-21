package org.gcube.portlets.widgets.exporter.client;


import org.gcube.portlets.d4sreporting.common.shared.Model;
import org.gcube.portlets.widgets.exporter.shared.TypeExporter;

import com.google.gwt.user.client.rpc.AsyncCallback;


public interface ReportExporterServiceAsync {
	void convert(Model model, boolean instructions, boolean comments, TypeExporter type,
			AsyncCallback<String> callback);
	
	void save(String filePath, String workspaceFolderId, String itemName, TypeExporter type,
			boolean overwrite, AsyncCallback<String> callback);
}
