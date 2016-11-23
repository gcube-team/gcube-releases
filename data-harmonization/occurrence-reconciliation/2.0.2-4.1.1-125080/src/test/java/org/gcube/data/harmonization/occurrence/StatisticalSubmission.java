package org.gcube.data.harmonization.occurrence;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.harmonization.occurrence.impl.ReconciliationImpl;
import org.gcube.data.harmonization.occurrence.impl.model.statistical.StatisticalComputation;

public class StatisticalSubmission {

	/**
	 * @param args
	 */
	
	private static final String scope="/gcube";
	private static final String user="fabio.sinibaldi";
	
	public static void main(String[] args) throws Exception {
		System.out.println("Instantiating module..");
		 URI host = URI.create("http://dbtest.next.research-infrastructures.eu:8888");
		 ScopeProvider.instance.set(scope);
		Reconciliation reconciliation=new ReconciliationImpl(user,host);
		
		StatisticalComputation comp=new StatisticalComputation("OCCURRENCES_INSEAS_ONEARTH", "test on earth", "TRANSDUCERERS");
		Map<String, String> params= new HashMap<String, String>();
		params.put("OccurrencePointsTableName", "processedoccurrences_id_66e0fcb9_e451_4f45_9b5f_742189d550d3");
		params.put("longitudeColumn","decimallongitude");
		params.put("latitudeColumn","decimallatitude");
		params.put("FilterType","IN_THE_WATER");
		System.out.println(reconciliation.submitOperation(comp, params,"Submission test","Just another test"));

	}

}
