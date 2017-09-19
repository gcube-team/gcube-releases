package org.gcube.portlets.widgets.exporter.server;

import java.io.File;
import java.io.FileInputStream;

import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;
import org.gcube.portlets.d4sreporting.common.shared.Model;
import org.gcube.portlets.docxgenerator.DocxGenerator;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.portlets.widgets.exporter.client.ReportExporterService;
import org.gcube.portlets.widgets.exporter.shared.OpenXMLConverterException;
import org.gcube.portlets.widgets.exporter.shared.SaveReportFileException;
import org.gcube.portlets.widgets.exporter.shared.SaveReportFileExistException;
import org.gcube.portlets.widgets.exporter.shared.TypeExporter;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class ReportExporterServiceImpl extends RemoteServiceServlet implements
		ReportExporterService  {

	@Override
	public String convert(Model model, boolean instructions, boolean comments,
			TypeExporter type) throws OpenXMLConverterException, IllegalArgumentException {
		

		String hostName = System.getenv("CATALINA_HOME") + "/webapps";
		
		if (model == null) {
			throw new IllegalArgumentException("The report to convert is not valid");
		}
		
		DocxGenerator generator = new DocxGenerator(model,hostName,instructions,comments);
		File file = null;
		try {
			switch (type) {
			case DOCX:
				file = generator.outputTmpFile();
				break;
			case HTML:
				file = generator.outputHTMLTmpFile();
				break;
			case PDF:
				file = generator.outputPDFTmpFile();
				break;
			case XML:
				file = generator.outputXMLTmpFile();
			default:
				break;
			}
		} catch (Exception e) {	
		}
		
		if (file == null)
			throw new OpenXMLConverterException("The report can't be" +
					" converted in a " + type + " file");
		
		String pathfile = file.getAbsolutePath();
		generator = null;
		file = null;
		return pathfile;
				
	}

	@Override
	public String save(String filePath, String workspaceFolderId, String itemName, TypeExporter type,
			boolean overwrite) throws SaveReportFileException, SaveReportFileExistException {
		
		try {
			File file = new File(filePath);
			
			Workspace workspace = HomeLibrary.getUserWorkspace(getASLSession().getUsername());
			WorkspaceFolder folder = (workspaceFolderId != null)?
					(WorkspaceFolder)workspace.getItem(workspaceFolderId):workspace.getRoot();
					
			if (workspace.exists(itemName, folder.getId())) {
				if (overwrite)
					workspace.remove(itemName, folder.getId());
				else
					throw new SaveReportFileException("The item " + itemName + " already exists");
			}
			
			switch (type) {
			case PDF:
				return folder.createExternalPDFFileItem(itemName + "." + type.toString().toLowerCase(),
						"", null, new FileInputStream(file)).getId();
				
			case HTML:
				return folder.createExternalFileItem(itemName + "." + type.toString().toLowerCase(),
						"", "text/html", new FileInputStream(file)).getId();
			case DOCX:
				return folder.createExternalFileItem(itemName + "." + type.toString().toLowerCase(),
						"", "application/msword", new FileInputStream(file)).getId();
				
			case XML:
				return folder.createExternalFileItem(itemName + "." + type.toString().toLowerCase(),
						"", "application/xml", new FileInputStream(file)).getId();
			}
			throw new SaveReportFileException("Unknown file type");
		} catch (ItemAlreadyExistException e) {
			throw new SaveReportFileExistException(e.getMessage());
		} catch (Exception e) {
			throw new SaveReportFileException(e.getMessage());
		}
		
	}
	
	/**
	 * the current ASLSession
	 * @return .
	 */
	private ASLSession getASLSession() {
		String sessionID = this.getThreadLocalRequest().getSession().getId();
		String user = (String) this.getThreadLocalRequest().getSession().getAttribute(ScopeHelper.USERNAME_ATTRIBUTE);

		if (user == null) {
			user = "massimiliano.assante";
			this.getThreadLocalRequest().getSession().setAttribute(ScopeHelper.USERNAME_ATTRIBUTE, user);
			SessionManager.getInstance().getASLSession(sessionID, user).setScope("/gcube/devsec");
		}
		return SessionManager.getInstance().getASLSession(sessionID, user);
	}
	
}
