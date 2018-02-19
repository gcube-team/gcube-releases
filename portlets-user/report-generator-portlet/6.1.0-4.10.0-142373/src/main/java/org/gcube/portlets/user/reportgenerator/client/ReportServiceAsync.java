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
import org.gcube.portlets.widgets.exporter.shared.TypeExporter;

import com.google.gwt.user.client.rpc.AsyncCallback;
/**
 *  * Service interface for server Async communication
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 * 
 */
 
public interface ReportServiceAsync {
	/**
	 * return the first ten records of the timeseries having id as param
	 * @param sTS .
	 * @param callback .
	 */
	void getSampleTimeSeries(RepTimeSeries sTS, AsyncCallback<Table> callback);
	/**
	 * 
	 * @param callback .
	 */
	void getUserTemplateNames(AsyncCallback<String[]> callback);
	
	/**
	 * 
	 * @param templateName .
	 * @param templateObjectID .
	 * @param isTemplate says if you're opening a template or a report
	 * @param isImporting says if your importing or youre loading a template in the UI
	 * @param callback .
	 */
	void readModel(String templateName, String templateObjectID, boolean isTemplate, boolean isImporting,  AsyncCallback<Model> callback);
	
	/**
	 * 
	 * @param model .
	 * @param callback .
	 */
	void storeTemplateInSession(Model model, AsyncCallback<Void> callback);
	/**
	 * 
	 * call for the model previously stored in the session 
	 * @param callback .
	 */
	void readTemplateFromSession( AsyncCallback<Model> callback);
	
	void saveReport(Model toSave, String folderid, String newname,	AsyncCallback<Void> callback);
	
	void saveReport(Model toSave, AsyncCallback<Void> callback);
	/**
	 * 
	 * @param currentHost
	 * @param callback
	 */
	void getSessionInfo(String currentHost, AsyncCallback<SessionInfo> callback);
	
	void readImportedModel(String tempPath,	AsyncCallback<Model> callback);
	
	void save(String filePath, String workspaceFolderId, String ItemName, TypeExporter type, boolean overwrite, AsyncCallback<String> callback);
	
	void renewHTTPSession(AsyncCallback<Void> callback);
	
	void listVMEReports(AsyncCallback<ArrayList<VMEReportBean>> callback);
	
	void importVMEReport(String id, String name, VMETypeIdentifier refType,
			AsyncCallback<Model> callback);
	
	void listVMEReportRefByType(VMETypeIdentifier refType,
			AsyncCallback<ArrayList<VMEReportBean>> callback);
	
	void getVMEReportRef2Associate(String id, VMETypeIdentifier refType, AsyncCallback<Model> callback);
	void importVMETemplate(VMETypeIdentifier refType,
			AsyncCallback<Model> callback);
	void exportReportToRSG(VMETypeIdentifier refType, Model toSave,
			AsyncCallback<VmeExportResponse> callback);
	void deleteReportFromRSG(VMETypeIdentifier refType, String idToDelete,
			AsyncCallback<VmeExportResponse> callback);
	void getImageUrlById(String identifier, AsyncCallback<ReportImage> callback);
	void getUploadedImageUrlById(String fileName, String absolutePath,
			AsyncCallback<ReportImage> callback);
}
