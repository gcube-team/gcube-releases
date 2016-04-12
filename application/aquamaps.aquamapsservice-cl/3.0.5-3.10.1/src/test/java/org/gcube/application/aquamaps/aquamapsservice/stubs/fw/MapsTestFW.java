package org.gcube.application.aquamaps.aquamapsservice.stubs.fw;

import static org.gcube.application.aquamaps.aquamapsservice.stubs.fw.AquaMapsServiceConstants.aqService;
import static org.gcube.common.clients.stubs.jaxws.StubFactory.stubFor;

import java.util.Arrays;

import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.fields.SpeciesOccursumFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Filter;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.PagedRequestSettings;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.collections.FilterArray;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.elements.GetSpeciesByFiltersRequestType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.elements.GetSpeciesEnvelopeRequestType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.faults.AquaMapsFault;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.FieldType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.FilterType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.OrderDirection;
import org.gcube.common.scope.api.ScopeProvider;

public class MapsTestFW {

	/**
	 * @param args
	 * @throws AquaMapsFault 
	 */
	public static void main(String[] args) throws AquaMapsFault {
		ScopeProvider.instance.set(TestCommon.SCOPE);
		MapsStub stub=stubFor(aqService).at(TestCommon.getServiceURI("gcube/application/aquamaps/aquamapsservice/AquaMapsService"));
		
		int subId=528206;
		int defHspen=125;
		
		
		System.out.println(stub.loadSubmittedById(subId));
		System.out.println(stub.getSpeciesByFilters(new GetSpeciesByFiltersRequestType(new FilterArray(Arrays.asList(new Filter[]{
				new Filter(FilterType.begins+"",SpeciesOccursumFields.familycolumn+"","Gad",FieldType.STRING+"")
		})), null, new PagedRequestSettings(10, 0, SpeciesOccursumFields.speciesid+"", OrderDirection.DESC), defHspen)));
		System.out.println(stub.getSpeciesEnvelop(new GetSpeciesEnvelopeRequestType("AFD-Pul-46", defHspen)));
		System.out.println(stub.getObject(subId));
		
	}

}
