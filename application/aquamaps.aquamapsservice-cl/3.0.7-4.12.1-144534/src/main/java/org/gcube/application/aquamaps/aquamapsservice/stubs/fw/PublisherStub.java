package org.gcube.application.aquamaps.aquamapsservice.stubs.fw;

import static org.gcube.application.aquamaps.aquamapsservice.stubs.fw.AquaMapsServiceConstants.PUB_portType;
import static org.gcube.application.aquamaps.aquamapsservice.stubs.fw.AquaMapsServiceConstants.PUB_target_namespace;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;

import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.collections.FileArray;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.collections.MapArray;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.elements.GetBulkUpdatesStatusResponseType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.elements.GetJSONSubmittedByFiltersRequestType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.elements.PrepareBulkUpdatesFileRequestType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.elements.RetrieveMapsByCoverageRequestType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.faults.AquaMapsFault;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.gis.LayerInfoType;

@SOAPBinding(parameterStyle=ParameterStyle.BARE)
@WebService(name=PUB_portType,targetNamespace=PUB_target_namespace)
public interface PublisherStub {

	
	public MapArray RetrieveMapsByCoverage(RetrieveMapsByCoverageRequestType request)throws AquaMapsFault;
	
	public FileArray GetFileSetById(String id)throws AquaMapsFault;
	
	public LayerInfoType GetLayerById(String id)throws AquaMapsFault;
	
	public String GetJSONSubmittedByFilters(GetJSONSubmittedByFiltersRequestType request)throws AquaMapsFault;
	
	public String PrepareBulkUpdatesFile(PrepareBulkUpdatesFileRequestType request)throws AquaMapsFault;
	
	public GetBulkUpdatesStatusResponseType GetBulkUpdatesStatus(String request)throws AquaMapsFault;
}
