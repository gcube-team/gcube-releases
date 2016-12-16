package org.gcube.application.aquamaps.aquamapsservice.stubs.fw;

import static org.gcube.application.aquamaps.aquamapsservice.stubs.fw.AquaMapsServiceConstants.DM_portType;
import static org.gcube.application.aquamaps.aquamapsservice.stubs.fw.AquaMapsServiceConstants.DM_target_namespace;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;

import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Analysis;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.CustomQueryDescriptorStubs;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Resource;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.collections.FieldArray;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.collections.StringArray;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.elements.ExportTableRequest;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.elements.ExportTableStatusType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.elements.GenerateMapsRequest;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.elements.GetGenerationLiveReportResponseType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.elements.HspecGroupGenerationRequestType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.elements.ImportResourceRequest;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.elements.RemoveHSPECGroupGenerationRequestResponseType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.elements.SetCustomQueryRequest;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.elements.ViewCustomQueryRequestType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.elements.ViewTableRequest;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.faults.AquaMapsFault;
import org.gcube.common.clients.stubs.jaxws.JAXWSUtils.Empty;


@WebService(name=DM_portType,targetNamespace=DM_target_namespace)
@SOAPBinding(parameterStyle=ParameterStyle.BARE)
public interface DataManagementStub {
	
	
	
	// TESTED
	// ************* GENERIC
	public String viewTable(ViewTableRequest request)throws AquaMapsFault;
	
	
	//************** MAPS BATCH
	public Integer generateMaps(GenerateMapsRequest request)throws AquaMapsFault;
	
		
	//************** SOURCE GENERATION
	public String GenerateHSPECGroup(HspecGroupGenerationRequestType request)throws AquaMapsFault;
	
	public RemoveHSPECGroupGenerationRequestResponseType RemoveHSPECGroup(RemoveHSPECGroupGenerationRequestResponseType request)throws AquaMapsFault;
	
	public GetGenerationLiveReportResponseType GetGenerationLiveReportGroup(StringArray id)throws AquaMapsFault;
	
	public Empty EditHSPECGroupDetails(HspecGroupGenerationRequestType request)throws AquaMapsFault;
	
	public String ResubmitGeneration(String id)throws AquaMapsFault;
	
	
	
	//************** SOURCE MANAGEMENT
	
	// TESTED
	public FieldArray GetDefaultSources(Empty empty)throws AquaMapsFault;
	// TESTED
	public Resource EditResource(Resource toEdit)throws AquaMapsFault;
	
	public Empty RemoveResource(int resourceId)throws AquaMapsFault;
	// TESTED
	public Resource getResourceInfo(Resource toEdit)throws AquaMapsFault;
	
	
	
	//************ CUSTOM QUERY 
	// TESTED
	public String ViewCustomQuery(ViewCustomQueryRequestType request)throws AquaMapsFault;
	// TESTED
	public String SetCustomQuery(SetCustomQueryRequest request)throws AquaMapsFault;
	// TESTED
	public CustomQueryDescriptorStubs GetCustomQueryDescriptor(String request)throws AquaMapsFault;
	
	
	
	//*********** IMPORT / EXPORT
	public int ImportResource(ImportResourceRequest request)throws AquaMapsFault;
	// TESTED
	public String ExportTableAsCSV(ExportTableRequest request)throws AquaMapsFault;
	// TESTED
	public ExportTableStatusType GetExportStatus(String request)throws AquaMapsFault;
	
	
	
	//************ ANALYSIS
	public String AnalyzeTables(Analysis request)throws AquaMapsFault;
	
	public String LoadAnalysis(String request)throws AquaMapsFault;
	
	public Empty DeleteAnalysis(String request)throws AquaMapsFault;
}
