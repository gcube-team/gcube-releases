package org.gcube.application.aquamaps.aquamapsservice.stubs.fw;

import static org.gcube.application.aquamaps.aquamapsservice.stubs.fw.AquaMapsServiceConstants.pubService;
import static org.gcube.common.clients.stubs.jaxws.StubFactory.stubFor;

import java.util.Arrays;

import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.fields.SubmittedFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Field;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Map;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.PagedRequestSettings;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.collections.FieldArray;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.collections.MapArray;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.collections.StringArray;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.elements.GetJSONSubmittedByFiltersRequestType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.elements.RetrieveMapsByCoverageRequestType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.faults.AquaMapsFault;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.FieldType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.OrderDirection;
import org.gcube.common.scope.api.ScopeProvider;

public class PublisherTestsFW {

	/**
	 * @param args
	 * @throws AquaMapsFault 
	 */
	public static void main(String[] args) throws AquaMapsFault {
		ScopeProvider.instance.set(TestCommon.SCOPE);
		PublisherStub pub=stubFor(pubService).at(TestCommon.getServiceURI("gcube/application/aquamaps/aquamapsservice/PublisherService"));
		System.out.println(pub.GetJSONSubmittedByFilters(new GetJSONSubmittedByFiltersRequestType(new FieldArray(Arrays.asList(new Field[]{
				new Field(SubmittedFields.sourcehcaf+"",325+"",FieldType.INTEGER)
		})), new PagedRequestSettings(1, 0, SubmittedFields.searchid+"", OrderDirection.DESC))));
		
		MapArray array=pub.RetrieveMapsByCoverage(new RetrieveMapsByCoverageRequestType(new StringArray(Arrays.asList(new String[]{
				"Fis-10407"
		})), null, true, true));
		
		System.out.println("Found "+array.theList().size()+" Maps");
		
		for(Map map:array.theList()){
			System.out.println("***********************MAP*****************");
			System.out.println(map);
			System.out.println(pub.GetFileSetById(map.fileSetId()));
			System.out.println(pub.GetLayerById(map.layerId()));
		}
		
	}

}
