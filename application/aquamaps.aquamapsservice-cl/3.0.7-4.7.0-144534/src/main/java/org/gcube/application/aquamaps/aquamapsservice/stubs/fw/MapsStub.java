package org.gcube.application.aquamaps.aquamapsservice.stubs.fw;

import static org.gcube.application.aquamaps.aquamapsservice.stubs.fw.AquaMapsServiceConstants.AQ_portType;
import static org.gcube.application.aquamaps.aquamapsservice.stubs.fw.AquaMapsServiceConstants.AQ_target_namespace;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;

import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.AquaMap;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Job;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Submitted;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.collections.FieldArray;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.collections.StringArray;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.elements.CalculateEnvelopeFromCellSelectionRequestType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.elements.CalculateEnvelopeRequestType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.elements.GetPhylogenyRequestType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.elements.GetSpeciesByFiltersRequestType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.elements.GetSpeciesEnvelopeRequestType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.faults.AquaMapsFault;
import org.gcube.common.clients.stubs.jaxws.JAXWSUtils.Empty;



@SOAPBinding(parameterStyle=ParameterStyle.BARE)
@WebService(name=AQ_portType,targetNamespace=AQ_target_namespace)
public interface MapsStub {

	public Empty markSaved(StringArray toMarkIds) throws AquaMapsFault;
	
	public int deleteSubmitted(StringArray todeleteIds) throws AquaMapsFault;
	
	public FieldArray calculateEnvelope(CalculateEnvelopeRequestType request) throws AquaMapsFault;
	
	public FieldArray calculateEnvelopefromCellSelection(CalculateEnvelopeFromCellSelectionRequestType request)throws AquaMapsFault;
	
	public AquaMap getObject(int id) throws AquaMapsFault;
	
	public String submitJob(Job id) throws AquaMapsFault;
	
	public FieldArray getSpeciesEnvelop(GetSpeciesEnvelopeRequestType request) throws AquaMapsFault;
	
	public String getSpeciesByFilters(GetSpeciesByFiltersRequestType request) throws AquaMapsFault;
	
	public String getSpeciesByFiltersASCSV(GetSpeciesByFiltersRequestType request) throws AquaMapsFault;
	
	public String getPhylogeny(GetPhylogenyRequestType request) throws AquaMapsFault;
	
	public Submitted loadSubmittedById(int id)throws AquaMapsFault;
}
