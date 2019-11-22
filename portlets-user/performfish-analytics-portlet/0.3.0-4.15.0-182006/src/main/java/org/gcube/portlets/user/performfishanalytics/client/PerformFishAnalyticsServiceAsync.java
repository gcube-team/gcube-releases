package org.gcube.portlets.user.performfishanalytics.client;

import java.util.List;
import java.util.Map;

import org.gcube.portlets.user.performfishanalytics.shared.OutputFile;
import org.gcube.portlets.user.performfishanalytics.shared.PopulationType;
import org.gcube.portlets.user.performfishanalytics.shared.csv.CSVFile;
import org.gcube.portlets.user.performfishanalytics.shared.dataminer.DataMinerResponse;
import org.gcube.portlets.user.performfishanalytics.shared.performfishservice.PerformFishInitParameter;
import org.gcube.portlets.user.performfishanalytics.shared.performfishservice.PerformFishResponse;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface PerformFishAnalyticsServiceAsync
{

    /**
     * Utility class to get the RPC Async interface from client-side code
     */
    public static final class Util
    {
        private static PerformFishAnalyticsServiceAsync instance;

        public static final PerformFishAnalyticsServiceAsync getInstance()
        {
            if ( instance == null )
            {
                instance = (PerformFishAnalyticsServiceAsync) GWT.create( PerformFishAnalyticsService.class );
            }
            return instance;
        }

        private Util()
        {
            // Utility class should not be instantiated
        }
    }


	void getListPopulationType(
		String populationName, AsyncCallback<List<PopulationType>> callback);


	void getPopulationTypeWithListKPI(
		String populationTypeId, AsyncCallback<PopulationType> callback);


	void validParameters(
		PerformFishInitParameter initParams,
		AsyncCallback<PerformFishInitParameter> callback);


	void submitRequestToPerformFishService(
		Map<String, List<String>> mapParameters,
		AsyncCallback<PerformFishResponse> callback);


	void callingDataMinerPerformFishCorrelationAnalysis(
		PerformFishResponse result, Map<String, List<String>> mapParameters,
		AsyncCallback<DataMinerResponse> callback);


	void getImageFile(OutputFile file, AsyncCallback<String> callback);


	void readCSVFile(String URL, AsyncCallback<CSVFile> callback);

	void getCSVFile(
		OutputFile file, boolean deleteAfter, AsyncCallback<CSVFile> callback);


	void callingDataMinerPerformFishAnalysis(
		Map<String, List<String>> mapParameters,
		AsyncCallback<DataMinerResponse> callback);


	void checkGrantToAccessFarmID(String farmID, AsyncCallback<Boolean> callback);


	void callingDataMinerPerformFishAnnualAnalysis(Map<String, List<String>> algorithmMapParameters,
			AsyncCallback<DataMinerResponse> callback);


	void callingDataMinerPerformFishAnnualCorrelationAnalysis(PerformFishResponse peformFishReponse,
			Map<String, List<String>> mapParameters, AsyncCallback<DataMinerResponse> callback);


	void callDMServiceToLoadSynopticTable(PerformFishResponse performFishResponse,
			Map<String, List<String>> mapParameters, AsyncCallback<DataMinerResponse> asyncCallback);


	void callDMServiceToLoadSynopticAnnualTable(PerformFishResponse thePerformFishResponse,
			Map<String, List<String>> mapParameters, AsyncCallback<DataMinerResponse> asyncCallback);
}
