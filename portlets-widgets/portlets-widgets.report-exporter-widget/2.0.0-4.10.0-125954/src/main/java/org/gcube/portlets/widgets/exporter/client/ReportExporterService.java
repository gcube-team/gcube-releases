package org.gcube.portlets.widgets.exporter.client;


import org.gcube.portlets.d4sreporting.common.shared.Model;
import org.gcube.portlets.widgets.exporter.shared.OpenXMLConverterException;
import org.gcube.portlets.widgets.exporter.shared.SaveReportFileException;
import org.gcube.portlets.widgets.exporter.shared.SaveReportFileExistException;
import org.gcube.portlets.widgets.exporter.shared.TypeExporter;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("convert")
public interface ReportExporterService extends RemoteService {
	
	String convert(Model model, boolean instructions, boolean comments,
			TypeExporter type) throws OpenXMLConverterException, IllegalArgumentException;
	
	String save(String filePath, String workspaceFolderId, String ItemName,
			TypeExporter type, boolean overwrite)
			throws SaveReportFileException, SaveReportFileExistException;
}
