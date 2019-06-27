package org.gcube.portlets.user.performfishanalytics.client;

import java.util.List;
import java.util.Map;

import org.gcube.portlets.user.performfishanalytics.shared.OutputFile;
import org.gcube.portlets.user.performfishanalytics.shared.PopulationType;
import org.gcube.portlets.user.performfishanalytics.shared.csv.CSVFile;
import org.gcube.portlets.user.performfishanalytics.shared.dataminer.DataMinerResponse;
import org.gcube.portlets.user.performfishanalytics.shared.performfishservice.PerformFishInitParameter;
import org.gcube.portlets.user.performfishanalytics.shared.performfishservice.PerformFishResponse;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("performfish")
public interface PerformFishAnalyticsService extends RemoteService {

	/**
	 * @param populationName
	 * @return
	 * @throws Exception
	 */
  List<PopulationType> getListPopulationType(String populationName)
	throws Exception;

	/**
	 * @param populationTypeId
	 * @return
	 * @throws Exception
	 */
	PopulationType getPopulationTypeWithListKPI(String populationTypeId)
		throws Exception;

	/**
	 * @param initParams
	 * @return
	 * @throws Exception
	 */
	PerformFishInitParameter validParameters(
		PerformFishInitParameter initParams)
		throws Exception;

	/**
	 * @param mapParameters
	 * @return
	 * @throws Exception
	 */
	PerformFishResponse submitRequestToPerformFishService(Map<String, List<String>> mapParameters) throws Exception;

	DataMinerResponse callingDataMinerPerformFishCorrelationAnalysis(PerformFishResponse result,
			Map<String, List<String>> mapParameters) throws Exception;

	/**
	 * @param file
	 * @return
	 * @throws Exception
	 */
	String getImageFile(OutputFile file)
		throws Exception;

	/**
	 * @param file
	 * @return
	 * @throws Exception
	 */
	CSVFile readCSVFile(String URL)
		throws Exception;

	/**
	 * @param file
	 * @return
	 * @throws Exception
	 */
	CSVFile getCSVFile(OutputFile file, boolean deleteAfter)
		throws Exception;

	/**
	 * @param mapParameters
	 * @return
	 * @throws Exception
	 */
	DataMinerResponse callingDataMinerPerformFishAnalysis(
		Map<String, List<String>> mapParameters)
		throws Exception;

	/**
	 * @param farmID
	 * @return
	 * @throws Exception
	 */
	boolean checkGrantToAccessFarmID(String farmID)
		throws Exception;

	DataMinerResponse callingDataMinerPerformFishAnnualAnalysis(Map<String, List<String>> algorithmMapParameters)
			throws Exception;

	DataMinerResponse callingDataMinerPerformFishAnnualCorrelationAnalysis(PerformFishResponse peformFishReponse,
			Map<String, List<String>> mapParameters) throws Exception;
}
