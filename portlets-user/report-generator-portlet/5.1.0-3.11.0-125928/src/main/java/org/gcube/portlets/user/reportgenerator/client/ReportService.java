package org.gcube.portlets.user.reportgenerator.client;

import java.util.ArrayList;

import org.gcube.portlets.d4sreporting.common.shared.Model;
import org.gcube.portlets.d4sreporting.common.shared.RepTimeSeries;
import org.gcube.portlets.d4sreporting.common.shared.Table;
import org.gcube.portlets.user.reportgenerator.shared.ReportImage;
import org.gcube.portlets.user.reportgenerator.shared.SessionInfo;
import org.gcube.portlets.user.reportgenerator.shared.VMEReportBean;
import org.gcube.portlets.user.reportgenerator.shared.VMETypeIdentifier;
import org.gcube.portlets.user.reportgenerator.shared.VmeExportResponse;
import org.gcube.portlets.widgets.exporter.shared.SaveReportFileException;
import org.gcube.portlets.widgets.exporter.shared.SaveReportFileExistException;
import org.gcube.portlets.widgets.exporter.shared.TypeExporter;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
/**
 * Service interface for server communication
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 */
@RemoteServiceRelativePath("ReportServiceImpl")
public interface ReportService extends RemoteService{
	
	ReportImage getUploadedImageUrlById(String fileName, String absolutePath);
	
	ArrayList<VMEReportBean> listVMEReports();
	
	ArrayList<VMEReportBean> listVMEReportRefByType(VMETypeIdentifier refType);
	
	Model importVMEReport(String id, String name, VMETypeIdentifier refType);
	
	Model getVMEReportRef2Associate(String id, VMETypeIdentifier refType); 
	
	Model importVMETemplate(VMETypeIdentifier refType);

	String save(String filePath, String workspaceFolderId, String ItemName,
			TypeExporter type, boolean overwrite)
			throws SaveReportFileException, SaveReportFileExistException;
	
	ReportImage getImageUrlById(String identifier);
	/**
	 * return the first ten records of the timeseries having id as param
	 * @param sTS .
	 * @return .
	 */
	Table getSampleTimeSeries(RepTimeSeries sTS);
	/**
	 * 
	 * @return .
	 */
	String[] getUserTemplateNames();
	/**
	 * @param templateName .
	 * @param templateObjectID .
	 * @param isTemplate says if you're opening a template or a report
	 * @param isImporting says if your importing or youre loading a template in the UI
	 * @return .
	 */
	Model readModel(String templateName, String templateObjectID, boolean isTemplate, boolean isImporting);
	/**
	 * each portlet instance runs in a scope
	 * each portlet instance is used by a unique username from within the portal
	 * @param currentHost .
	 * @return a SessionInfo bean containing the username  the scope andis opening a workflow document or not
	 */
	SessionInfo getSessionInfo(String currentHost);

	/**
	 * 
	 * @param model .
	 */
	void storeTemplateInSession(Model model);


	/**
	 * 
	 * @return the model previously stored in the session 
	 */
	Model readTemplateFromSession();

	/**
	 * the report model is taken from the session
	 * @param folderid the basket id where to save the report 
	 */
	void saveReport(Model toSave, String folderid, String newname);

	/**
	 * the report model is taken from the session, the id also
	 */
	void saveReport(Model toSave);
	
	/**
	 * @param toSave the report instance to save
	 */
	VmeExportResponse exportReportToRSG(VMETypeIdentifier refType, Model toSave);
	
	VmeExportResponse deleteReportFromRSG(VMETypeIdentifier refType, String idToDelete);
	
	
	Model getWorkflowDocumentFromDocumentLibrary();
	
	void updateWorkflowDocument(Model toSave, boolean update);
	
	void renewLock();
	/**
	 * 
	 * @param tempPath
	 * @return
	 */
	Model readImportedModel(String tempPath);
	
	void renewHTTPSession();
}
