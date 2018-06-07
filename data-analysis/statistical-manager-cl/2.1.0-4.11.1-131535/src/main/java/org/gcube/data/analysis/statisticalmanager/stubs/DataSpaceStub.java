package org.gcube.data.analysis.statisticalmanager.stubs;

import static org.gcube.data.analysis.statisticalmanager.stubs.SMConstants.dataspace_portType;
import static org.gcube.data.analysis.statisticalmanager.stubs.SMConstants.dataspace_target_namespace;

import java.rmi.RemoteException;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;

import org.gcube.data.analysis.statisticalmanager.stubs.faults.StatisticalManagerFault;
import org.gcube.data.analysis.statisticalmanager.stubs.types.SMCreateTableFromCSVRequest;
import org.gcube.data.analysis.statisticalmanager.stubs.types.SMCreateTableFromDataStreamRequest;
import org.gcube.data.analysis.statisticalmanager.stubs.types.SMCreatedTablesRequest;
import org.gcube.data.analysis.statisticalmanager.stubs.types.SMFiles;
import org.gcube.data.analysis.statisticalmanager.stubs.types.SMGetFilesRequest;
import org.gcube.data.analysis.statisticalmanager.stubs.types.SMImporters;
import org.gcube.data.analysis.statisticalmanager.stubs.types.SMImportersRequest;
import org.gcube.data.analysis.statisticalmanager.stubs.types.SMResources;
import org.gcube.data.analysis.statisticalmanager.stubs.types.SMTables;
import org.gcube.data.analysis.statisticalmanager.stubs.types.SMimportDwcaFileRequest;
import org.gcube.data.analysis.statisticalmanager.stubs.types.SMimportFileRequest;
import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.SMImport;

@WebService(name=dataspace_portType,targetNamespace=dataspace_target_namespace)
public interface DataSpaceStub {

	@SOAPBinding(parameterStyle=ParameterStyle.BARE) 
	   public long createTableFromDataStream(SMCreateTableFromDataStreamRequest request) throws RemoteException,StatisticalManagerFault;
	@SOAPBinding(parameterStyle=ParameterStyle.BARE)     
	public long importFromFile(SMimportFileRequest request) throws RemoteException,StatisticalManagerFault;
	
	@SOAPBinding(parameterStyle=ParameterStyle.BARE) 
	public long importFromDwcaFile(SMimportDwcaFileRequest request)throws RemoteException,StatisticalManagerFault;
	
	@SOAPBinding(parameterStyle=ParameterStyle.BARE)     
	public long createTableFromCSV(SMCreateTableFromCSVRequest request) throws RemoteException,StatisticalManagerFault;
	
	@SOAPBinding(parameterStyle=ParameterStyle.BARE)     
	public String exportTable(String request) throws RemoteException,StatisticalManagerFault;
	
	@SOAPBinding(parameterStyle=ParameterStyle.BARE)     
	public SMTables getTables(SMCreatedTablesRequest request) throws RemoteException,StatisticalManagerFault;
	
	@SOAPBinding(parameterStyle=ParameterStyle.BARE)     
	public SMFiles getFiles(SMGetFilesRequest request) throws RemoteException,StatisticalManagerFault;
	
	@SOAPBinding(parameterStyle=ParameterStyle.BARE)    
	public SMResources getResources(SMCreatedTablesRequest request) throws RemoteException,StatisticalManagerFault;
	
	@SOAPBinding(parameterStyle=ParameterStyle.BARE)     
	public String getDBParameters(String request) throws RemoteException,StatisticalManagerFault;
	
	@SOAPBinding(parameterStyle=ParameterStyle.BARE)   
	public  SMImporters getImporters(SMImportersRequest request) throws RemoteException,StatisticalManagerFault;
	
	@SOAPBinding(parameterStyle=ParameterStyle.BARE)    
	public SMImport getImporter(String request) throws RemoteException,StatisticalManagerFault;
	
	@SOAPBinding(parameterStyle=ParameterStyle.BARE)    
	public void  removeImporter(String request) throws RemoteException,StatisticalManagerFault;
	
	@SOAPBinding(parameterStyle=ParameterStyle.BARE)    
	public void removeTable(String request) throws RemoteException,StatisticalManagerFault;

	
}
