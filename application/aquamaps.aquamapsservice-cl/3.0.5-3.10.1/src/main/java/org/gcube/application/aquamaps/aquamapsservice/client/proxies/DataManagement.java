package org.gcube.application.aquamaps.aquamapsservice.client.proxies;

import java.io.File;
import java.rmi.RemoteException;
import java.util.List;

import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Analysis;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Resource;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.environments.EnvironmentalExecutionReportItem;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.environments.SourceGenerationRequest;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.CustomQueryDescriptorStubs;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Field;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.PagedRequestSettings;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.ExportOperation;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.ResourceType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.SystemTable;

public interface DataManagement {

	public String getJSONView(PagedRequestSettings settings, String tableName, List<Field> filters)throws RemoteException,Exception;
//	public String getJSONSPECGroupGenreationRequests(PagedRequestSettings settings) throws RemoteException,Exception;
	
	
	public Integer generateMaps(String author,boolean enableGIS,Integer hspecId,List<Field> speciesFilter,boolean forceRegeneration)throws RemoteException,Exception;

	public String submitRequest(SourceGenerationRequest request)throws RemoteException,Exception;
	public EnvironmentalExecutionReportItem getReport(List<String> reportIds) throws RemoteException,Exception;
//	public SourceGenerationRequest getRequest(String id) throws RemoteException,Exception;
	public String removeRequest(String id, boolean deleteData,boolean  deleteJobs)throws RemoteException,Exception;
//	public void editRequest(SourceGenerationRequest requestDetails) throws RemoteException,Exception;
	
	public List<Field> getDefaultSources()throws RemoteException,Exception;
	public Resource updateResource(Resource toUpdate)throws RemoteException,Exception;
	public void deleteResource(int resourceId)throws RemoteException,Exception;
	public Resource loadResource(int resId)throws RemoteException,Exception;
	
	
	public CustomQueryDescriptorStubs setCustomQuery(String userId,String queryString)throws RemoteException,Exception;
	public String viewCustomQuery(String userId,PagedRequestSettings settings)throws RemoteException,Exception;
	
	public Integer asyncImportResource(File toImport, String userId,ResourceType type,String encoding, Boolean[] fieldsMask, boolean hasHeader,char delimiter) throws RemoteException,Exception;
	public Resource syncImportResource(File toImport, String userId,ResourceType type,String encoding, Boolean[] fieldsMask, boolean hasHeader,char delimiter) throws RemoteException,Exception;
	
	/**
	 * Returns the csv File if the operation in case of TRANSFER operation, null otherwise.
	 * 
	 * @param table
	 * @param basketId
	 * @param user
	 * @param toSaveName
	 * @param operationType
	 * @return
	 * @throws RemoteException
	 * @throws Exception
	 */
	public File exportTableAsCSV(String table,String basketId,String user,String toSaveName,ExportOperation operationType) throws RemoteException,Exception;
	
	public String analyzeTables(Analysis request)throws RemoteException,Exception;
//	public String getJsonSubmittedAnalysis(PagedRequestSettings settings)throws RemoteException,Exception;
	public File loadAnalysisResults(String id)throws RemoteException,Exception;
	public String resubmitGeneration(String id) throws RemoteException,Exception;
	
	
	public void deleteAnalysis(String id)throws RemoteException,Exception;
	public String getSystemTableName(SystemTable table)throws RemoteException,Exception;
	
}
