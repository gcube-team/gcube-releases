
package org.gcube.portlets.user.performfishanalytics.server;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.persistence.EntityManagerFactory;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.gcube.common.portal.PortalContext;
import org.gcube.portlets.user.performfishanalytics.client.PerformFishAnalyticsConstant;
import org.gcube.portlets.user.performfishanalytics.client.PerformFishAnalyticsService;
import org.gcube.portlets.user.performfishanalytics.server.database.EntityManagerFactoryCreator;
import org.gcube.portlets.user.performfishanalytics.server.persistence.GenericPersistenceDaoBuilder;
import org.gcube.portlets.user.performfishanalytics.server.util.ContextUtil;
import org.gcube.portlets.user.performfishanalytics.server.util.DataMinerUtil;
import org.gcube.portlets.user.performfishanalytics.server.util.DatabaseUtil;
import org.gcube.portlets.user.performfishanalytics.server.util.GsonUtil;
import org.gcube.portlets.user.performfishanalytics.server.util.HttpCallerUtil;
import org.gcube.portlets.user.performfishanalytics.server.util.PortalContextInfo;
import org.gcube.portlets.user.performfishanalytics.server.util.ServiceParameters;
import org.gcube.portlets.user.performfishanalytics.server.util.csv.CSVReader;
import org.gcube.portlets.user.performfishanalytics.server.util.csv.CSVWriter;
import org.gcube.portlets.user.performfishanalytics.server.util.dataminer.DMServiceResponse;
import org.gcube.portlets.user.performfishanalytics.server.util.dataminer.DataMinerOutputData;
import org.gcube.portlets.user.performfishanalytics.server.util.tozipview.ZipExtractorUtil;
import org.gcube.portlets.user.performfishanalytics.shared.FileContentType;
import org.gcube.portlets.user.performfishanalytics.shared.KPI;
import org.gcube.portlets.user.performfishanalytics.shared.OutputFile;
import org.gcube.portlets.user.performfishanalytics.shared.Population;
import org.gcube.portlets.user.performfishanalytics.shared.PopulationType;
import org.gcube.portlets.user.performfishanalytics.shared.csv.CSVFile;
import org.gcube.portlets.user.performfishanalytics.shared.csv.CSVRow;
import org.gcube.portlets.user.performfishanalytics.shared.dataminer.DataMinerResponse;
import org.gcube.portlets.user.performfishanalytics.shared.exceptions.SessionExpired;
import org.gcube.portlets.user.performfishanalytics.shared.performfishservice.PerformFishInitParameter;
import org.gcube.portlets.user.performfishanalytics.shared.performfishservice.PerformFishResponse;
import org.gcube.vomanagement.usermanagement.exception.GroupRetrievalFault;
import org.gcube.vomanagement.usermanagement.exception.UserRetrievalFault;
import org.gcube.vomanagement.usermanagement.impl.LiferayRoleManager;
import org.gcube.vomanagement.usermanagement.model.GCubeTeam;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

// TODO: Auto-generated Javadoc
/**
 * The server side implementation of the RPC service.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 *         Jan 16, 2019
 */
@SuppressWarnings("serial")
public class PerformFishAnalyticsServiceImpl extends RemoteServiceServlet
	implements PerformFishAnalyticsService {

	/** The log. */
	protected static Logger log = LoggerFactory.getLogger(PerformFishAnalyticsServiceImpl.class);


	/** The date format. */
	private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy 'at' HH:mm:ss-SSS z");

	/**
	 * Gets the DB factory.
	 *
	 * @return the DB factory
	 * @throws Exception
	 *             the exception
	 */
	private EntityManagerFactory getDBFactory()
		throws Exception {

		if(ContextUtil.isSessionExpired(this.getThreadLocalRequest()))
			throw new SessionExpired("The session is expired");

		//PortalContextInfo pContext = ContextUtil.getPortalContext(this.getThreadLocalRequest());
		EntityManagerFactoryCreator.instanceLocalMode();
		EntityManagerFactory dbFactory = EntityManagerFactoryCreator.getEntityManagerFactory();
		new DatabaseUtil().fillDatabaseIfEmpty(dbFactory, this.getThreadLocalRequest().getServletContext());
		return dbFactory;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.GenericServlet#destroy()
	 */
	@Override
	public void destroy() {
		super.destroy();
		try{
			log.info("Closing DB Factory");
			EntityManagerFactoryCreator.instanceLocalMode();
			EntityManagerFactory dbFactory = EntityManagerFactoryCreator.getEntityManagerFactory();
			dbFactory.close();
//			String dbFolderPath = EntityManagerFactoryCreator.getPersistenceFolderPath();
//			FileUtil.deleteDirectoryRecursion(new File(dbFolderPath).toPath());
			log.info("DB Factory closed correctly");
		}catch(Exception e){
			log.info("Error occurred on closing the DB Factory: ",e);
		}
	}

	/**
	 * Gets the list population type.
	 *
	 * @param populationName
	 *            the population name
	 * @return the list population type
	 * @throws Exception
	 *             the exception
	 */
	@Override
	public List<PopulationType> getListPopulationType(String populationName) throws Exception {
		log.info("Getting PopulationType for populationName: "+populationName);

//		if(ContextUtil.isSessionExpired(this.getThreadLocalRequest()))
//			throw new SessionExpired("The session is expired");

		try{
			EntityManagerFactory dbFactory = getDBFactory();
			GenericPersistenceDaoBuilder<Population> builderPopulation =
				new GenericPersistenceDaoBuilder<Population>(
					dbFactory, Population.class.getSimpleName());
			List<Population> listPopulation = builderPopulation.getPersistenceEntity().getList();
			log.info("List of {} are: {}",Population.class.getSimpleName(),listPopulation);
			List<PopulationType> listPopulationType;
			for (Population population : listPopulation) {
				if (population.getName().equalsIgnoreCase(populationName)) {

					listPopulationType = population.getListPopulationType();
					if(log.isDebugEnabled()){
						for (PopulationType populationType : listPopulationType) {
							log.debug(populationType.toString());
						}
					}

					List<PopulationType> listPopulationTypeDTO = ToAvoidIndirectSerialization.toGWTSerializable(listPopulationType, population, true);
					log.info("Returning "+listPopulationTypeDTO.size()+ " type/s for population name: "+populationName);
					return listPopulationTypeDTO;
				}
			}
			// NO POPULATION TYPES
			log.info("No population type found for population name: "+populationName);
			return new ArrayList<PopulationType>();
		}catch(Exception e){
			log.error("Error on loading types of Population for population name: "+populationName, e);
			throw new Exception("Error on loading types of Population for population name: "+populationName);
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.performfishanalytics.client.PerformFishAnalyticsService#getPopulationTypeWithListKPI(java.lang.String)
	 */
	@Override
	public PopulationType getPopulationTypeWithListKPI(String populationTypeId) throws Exception{
		log.info("Getting PopulationType with its list of KPI for id: "+populationTypeId);

		if(ContextUtil.isSessionExpired(this.getThreadLocalRequest()))
			throw new SessionExpired("The session is expired");

		try{
			EntityManagerFactory dbFactory = getDBFactory();
			GenericPersistenceDaoBuilder<PopulationType> builderPopulationType =
				new GenericPersistenceDaoBuilder<PopulationType>(
					dbFactory, PopulationType.class.getSimpleName());

			Map<String, String> filterMap = new HashMap<String, String>();
			filterMap.put("id",populationTypeId);
			List<PopulationType> listPopType = builderPopulationType.getPersistenceEntity().getList(filterMap, -1, -1);

			if(listPopType==null || listPopType.isEmpty())
				throw new Exception("No population type found for id: "+populationTypeId);

			//BUILDING HIERARCHICAL LIST OF KPI
			PopulationType selectedPopType = listPopType.get(0);
			List<KPI> listGWTKPI = new ArrayList<KPI>(selectedPopType.getListKPI().size());
			for (KPI toKPI : selectedPopType.getListKPI()) {
				KPI gwtKPI = convert(toKPI);
				gwtKPI.setPopulationType(selectedPopType);//I'm setting population type only at first level
				//gwtKPI.setLeaf(toKPI.getListKPI()==null || toKPI.getListKPI().isEmpty());
				listGWTKPI.add(gwtKPI);
			}

			List<PopulationType> listPopulationTypeDTO = ToAvoidIndirectSerialization.toGWTSerializable(listPopType, null, false);
			PopulationType toReturn = listPopulationTypeDTO.get(0);
			toReturn.setListKPI(listGWTKPI);

			if(log.isDebugEnabled()){
				for (KPI kpi : toReturn.getListKPI()) {
					log.debug(kpi.toString());
				}
			}
			log.info("Returning type "+toReturn.getName()+" having list of KPI count: "+toReturn.getListKPI().size());
			return toReturn;
		}catch(Exception e){
			log.error("Error on loading list of KPI for popluation type with id: "+populationTypeId, e);
			throw new Exception("Error on loading list of KPI for popluation type with id: "+populationTypeId);
		}
	}

	/**
	 * Convert.
	 *
	 * @param kpi the kpi
	 * @return the kpi
	 */
	private KPI convert(KPI kpi){
		if(kpi.getListKPI()==null){
			log.trace("LEAF "+kpi);
			return getGWTKPI(kpi, null);
		}

		KPI gwtKPI = getGWTKPI(kpi, null);
		log.trace("Converted: "+gwtKPI);
		for (KPI kpiChild : kpi.getListKPI()) {
			KPI convertedChild = convert(kpiChild);
			if(gwtKPI.getListKPI()==null){
				List<KPI> listKPI = new ArrayList<KPI>();
				gwtKPI.setListKPI(listKPI);
			}
			gwtKPI.getListKPI().add(convertedChild);
		}
		log.trace("Filled children of: "+gwtKPI.getName());
		if(gwtKPI.getListKPI()!=null){
			for (KPI chKPI : gwtKPI.getListKPI()) {
				log.trace("\t"+chKPI);
			}
		}
		return gwtKPI;
	}

	/**
	 * Gets the gwtkpi.
	 *
	 * @param toKPI the to kpi
	 * @param populationType the population type
	 * @return the gwtkpi
	 */
	private KPI getGWTKPI(KPI toKPI, PopulationType populationType){
		KPI gwtKPI = new KPI(toKPI.getId(),toKPI.getCode(),toKPI.getName(),toKPI.getDescription(), null,populationType,toKPI.getDeepIndex());
		gwtKPI.setLeaf(toKPI.getListKPI()==null || toKPI.getListKPI().isEmpty());
		return gwtKPI;
	}



	/**
	 * Check grant to access farm id.
	 *
	 * @param farmID the farm id
	 * @return true, if successful
	 * @throws Exception the exception
	 */
	@Override
	public boolean checkGrantToAccessFarmID(String farmID) throws Exception{

		if(ContextUtil.isSessionExpired(this.getThreadLocalRequest()))
			throw new SessionExpired("The session is expired");

		log.info("Checking the rights to access the farmID {} for current user",farmID);
		if(ContextUtil.isWithinPortal()){
			GCubeUser currentUser = PortalContext.getConfiguration().getCurrentUser(this.getThreadLocalRequest());
			long userId = currentUser.getUserId();
			log.info("User {} has the userId {}",currentUser.getUsername(), userId);
			long farmId = -1;
			try{
				farmId = Long.parseLong(farmID);
			}catch(Exception e){
				log.error("Error parsing the farmID "+farmID+" as long", e);
			}

			log.debug("Parsed FARM_ID as long is: "+farmId);

			if(farmId == -1){
				throw new Exception("Your input farm ID seems to be not valid. Please contact the D4Science support");
			}

			long groupId;
			try {
				groupId = PortalContext.getConfiguration().getCurrentGroupId(this.getThreadLocalRequest());
			} catch (Exception e) {
				log.error("Error getting the group id: ",e);
				throw new Exception("Your input farm ID seems to be not valid. Please contact the D4Science support");
			}
			log.debug("The group id is: "+groupId);

			List<GCubeTeam> teams = new ArrayList<>();
			try {
				teams = new LiferayRoleManager().listTeamsByUserAndGroup(userId, groupId);
			} catch (UserRetrievalFault | GroupRetrievalFault e) {
				log.warn("Error listing the GCubeTeams: ",e);
			}
			for (GCubeTeam team : teams) {
				if (team.getTeamId() == farmId) {
					log.info(GCubeTeam.class.getSimpleName() +" matching the FARM_ID "+farmId+" FOUND!");
					return true;
				}
			}
			log.info(GCubeTeam.class.getSimpleName() +" matching the FARM_ID "+farmId+" NOT FOUND!");
			return false;

		}else{
			//IN TEST MODE NOT CHECKING NOTHING
			log.info("I'm in testing mode, grant the rights to access the farmID {} for current user",farmID);
			return true;
		}
	}


	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.performfishanalytics.client.PerformFishAnalyticsService#validParameters(org.gcube.portlets.user.performfishanalytics.shared.performfishservice.PerformFishInitParameter)
	 */
	@Override
	public PerformFishInitParameter validParameters(PerformFishInitParameter initParams) throws Exception{

		Map<String, String> inputParameters = initParams.getParameters();
		String farmID = inputParameters.get(PerformFishAnalyticsConstant.PERFORM_FISH_FARMID_PARAM);
		boolean grantAccess = checkGrantToAccessFarmID(farmID);
		if(!grantAccess)
			throw new Exception("You have no rights to access to this FARM. You does not belong to it.");

		return initParams;

	}


	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.performfishanalytics.client.PerformFishAnalyticsService#submitRequestToPerformFishService(java.util.Map)
	 */
	@Override
	public PerformFishResponse submitRequestToPerformFishService(Map<String, List<String>> mapParameters) throws Exception{

		log.info("Submitting request with parameters: "+mapParameters);
		ServiceParameters performFishService = null;
		try{
			performFishService = ContextUtil.getPerformFishService(this.getThreadLocalRequest());
		}catch (Exception e) {
			log.error("Error on getting the perform fish service from IS: "+performFishService, e);
			throw new Exception("Error on getting the perform fish service from IS: "+performFishService+" Please contact the suport");
		}

		String serviceURL = performFishService.getUrl() + "/performance";
		log.debug("Calling service: "+serviceURL);
		HttpCallerUtil httpCaller = new HttpCallerUtil(serviceURL, null, null);
		String gCubeToken = ContextUtil.getPortalContext(this.getThreadLocalRequest()).getUserToken();
		//mapParameters.put("gcube-token", Arrays.asList(ContextUtil.getPortalContext(this.getThreadLocalRequest()).getUserToken()));
		String response;
		try {
			Date startTime = getCurrentTimeToDate(System.currentTimeMillis());
			log.debug("The request to perform-fish performed just now {}", dateFormat.format(startTime));
			response = httpCaller.callGet(null, mapParameters, gCubeToken);
			Date endTime = getCurrentTimeToDate(System.currentTimeMillis());
			log.info("The response is: "+response +" with status: "+httpCaller.getStatusCode());
			log.info("The perform-fish response returned just now {}. Response returned in {} "+ TimeUnit.MILLISECONDS.toString(), dateFormat.format(endTime), getDateDiff(startTime, endTime, TimeUnit.MILLISECONDS));

			if(response==null)
				throw new Exception("The response is null");

			Map<String, String> theResponseParams = GsonUtil.toMap(response);
			log.debug("The response was converted into map: "+theResponseParams);

			UUID respSessionID = UUID.randomUUID();
			//ContextUtil.getPerformFishService(UUID.randomUUID());
			return new PerformFishResponse(theResponseParams, respSessionID.toString());
		}
		catch (Exception e) {
			log.error("Error interacting with the service: "+performFishService.getUrl() +" with parameters: "+mapParameters, e);
			throw new Exception("There was an error interacting with the "+ContextUtil.PERFORM_SERVICE+" in this VRE ("
							+ ContextUtil.getPortalContext(this.getThreadLocalRequest()).getCurrentScope()+ ")"
							+ ". Please report this issue at www.d4science.org/contact-us");
		}

	}


	/**
	 * Gets the current time to date.
	 *
	 * @param currentTime the current time
	 * @return the current time to date
	 */
	private Date getCurrentTimeToDate(long currentTime) {
		return new Date(currentTime);
		//return dateFormat.format(resultdate);

	}

	/**
	 * Get a diff between two dates.
	 *
	 * @param date1 the oldest date
	 * @param date2 the newest date
	 * @param timeUnit the unit in which you want the diff
	 * @return the diff value, in the provided unit
	 */
	public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
	    long diffInMillies = date2.getTime() - date1.getTime();
	    return timeUnit.convert(diffInMillies,TimeUnit.MILLISECONDS);
	}


	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.performfishanalytics.client.PerformFishAnalyticsService#callingDataMinerPerformFishCorrelationAnalysis(org.gcube.portlets.user.performfishanalytics.shared.performfishservice.PerformFishResponse, java.util.Map)
	 */
	@Override
	public DataMinerResponse callingDataMinerPerformFishCorrelationAnalysis(PerformFishResponse peformFishReponse, Map<String, List<String>> mapParameters) throws Exception{

		log.info("Validating Perform-Fish service response...");

		String URLToBatchesTable = peformFishReponse.getMapParameters().get(PerformFishAnalyticsConstant.BATCHES_TABLE);

		if(URLToBatchesTable==null || URLToBatchesTable.isEmpty())
			throw new Exception("Something seems "+PerformFishAnalyticsConstant.BATCHES_TABLE+ " is null or emty");

		//Checking that the perform-fish PerformFishAnalyticsConstant.BATCHES_TABLE has at least 1 row
		CSVFile csvFile = readCSVFile(URLToBatchesTable);
		log.info("CSVFile read from {} - {}", URLToBatchesTable, csvFile);
		if(csvFile==null || csvFile.getValueRows() == null || csvFile.getValueRows().size()<PerformFishAnalyticsConstant.CSV_BATCHES_TABLE_MINIMUM_SIZE){
			log.warn("The "+PerformFishAnalyticsConstant.BATCHES_TABLE+" CSV rows are"+csvFile.getValueRows()+". It is less than "+PerformFishAnalyticsConstant.CSV_BATCHES_TABLE_MINIMUM_SIZE);
			throw new Exception("Your request does not produce enough data for the analysis, please change your selection and try again");
		}

		log.info("Calling the DM service with client parameters: "+mapParameters.toString());
		//PortalContextInfo pContext = ContextUtil.getPortalContext(this.getThreadLocalRequest());
		Map<String, List<String>> dmRequestParameters = new HashMap<String, List<String>>();
		dmRequestParameters.put("request", Arrays.asList("Execute"));
		dmRequestParameters.put("service", Arrays.asList("WPS"));
		dmRequestParameters.put("Version", Arrays.asList("1.0.0"));
		//dmRequestParameters.put("gcube-token", Arrays.asList(pContext.getUserToken()));
		dmRequestParameters.put("lang", Arrays.asList("en-US"));
		dmRequestParameters.put("Identifier", Arrays.asList("org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.PERFORMFISH_ANALYSIS"));
		dmRequestParameters.putAll(mapParameters);
		
		return callTheDataMinerPerformFishCorrelationAnalysis(peformFishReponse,dmRequestParameters);
	}
	
	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.performfishanalytics.client.PerformFishAnalyticsService#callingDataMinerPerformFishCorrelationAnalysis(org.gcube.portlets.user.performfishanalytics.shared.performfishservice.PerformFishResponse, java.util.Map)
	 */
	@Override
	public DataMinerResponse callingDataMinerPerformFishAnnualCorrelationAnalysis(PerformFishResponse peformFishReponse, Map<String, List<String>> mapParameters) throws Exception{

		log.info("Calling the DM service with client parameters: "+mapParameters.toString());
		//PortalContextInfo pContext = ContextUtil.getPortalContext(this.getThreadLocalRequest());
		Map<String, List<String>> dmRequestParameters = new HashMap<String, List<String>>();
		dmRequestParameters.put("request", Arrays.asList("Execute"));
		dmRequestParameters.put("service", Arrays.asList("WPS"));
		dmRequestParameters.put("Version", Arrays.asList("1.0.0"));
		//dmRequestParameters.put("gcube-token", Arrays.asList(pContext.getUserToken()));
		dmRequestParameters.put("lang", Arrays.asList("en-US"));
		dmRequestParameters.put("Identifier", Arrays.asList("org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.PERFORMFISH_ANALYSIS_ANNUAL"));
		dmRequestParameters.putAll(mapParameters);
		
		return callTheDataMinerPerformFishCorrelationAnalysis(peformFishReponse,dmRequestParameters);
	}
	
	@Override
	public DataMinerResponse callDMServiceToLoadSynopticTable(PerformFishResponse performFishResponse,
			Map<String, List<String>> mapParameters) throws Exception{
		
		log.info("Validating Perform-Fish service response...");

		String URLToBatchesTable = performFishResponse.getMapParameters().get(PerformFishAnalyticsConstant.BATCHES_TABLE);

		if(URLToBatchesTable==null || URLToBatchesTable.isEmpty())
			throw new Exception("Something seems "+PerformFishAnalyticsConstant.BATCHES_TABLE+ " is null or emty");

		//Checking that the perform-fish PerformFishAnalyticsConstant.BATCHES_TABLE has at least 1 row
//		CSVFile csvFile = readCSVFile(URLToBatchesTable);
//		log.info("CSVFile read from {} - {}", URLToBatchesTable, csvFile);
//		if(csvFile==null || csvFile.getValueRows() == null || csvFile.getValueRows().size()<PerformFishAnalyticsConstant.CSV_BATCHES_TABLE_MINIMUM_SIZE){
//			log.warn("The "+PerformFishAnalyticsConstant.BATCHES_TABLE+" CSV rows are"+csvFile.getValueRows()+". It is less than "+PerformFishAnalyticsConstant.CSV_BATCHES_TABLE_MINIMUM_SIZE);
//			throw new Exception("Your request does not produce enough data for the analysis, please change your selection and try again");
//		}

		log.info("Calling the DM service with client parameters: "+mapParameters.toString());
		//PortalContextInfo pContext = ContextUtil.getPortalContext(this.getThreadLocalRequest());
		Map<String, List<String>> dmRequestParameters = new HashMap<String, List<String>>();
		dmRequestParameters.put("request", Arrays.asList("Execute"));
		dmRequestParameters.put("service", Arrays.asList("WPS"));
		dmRequestParameters.put("Version", Arrays.asList("1.0.0"));
		//dmRequestParameters.put("gcube-token", Arrays.asList(pContext.getUserToken()));
		dmRequestParameters.put("lang", Arrays.asList("en-US"));
		//dmRequestParameters.put("Identifier", Arrays.asList("org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.PERFORMFISH_SYNOPTICTABLE_BATCH"));
		dmRequestParameters.putAll(mapParameters);
		return callTheDataMiner(dmRequestParameters);
	}
	

	@Override
	public DataMinerResponse callDMServiceToLoadSynopticAnnualTable(PerformFishResponse thePerformFishResponse,
			Map<String, List<String>> mapParameters) throws Exception {
		log.info("Validating Perform-Fish service response...");

		log.info("Calling the DM service with client parameters: "+mapParameters.toString());
		//PortalContextInfo pContext = ContextUtil.getPortalContext(this.getThreadLocalRequest());
		Map<String, List<String>> dmRequestParameters = new HashMap<String, List<String>>();
		dmRequestParameters.put("request", Arrays.asList("Execute"));
		dmRequestParameters.put("service", Arrays.asList("WPS"));
		dmRequestParameters.put("Version", Arrays.asList("1.0.0"));
		//dmRequestParameters.put("gcube-token", Arrays.asList(pContext.getUserToken()));
		dmRequestParameters.put("lang", Arrays.asList("en-US"));
		dmRequestParameters.put("Identifier", Arrays.asList("org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.PERFORMFISH_SYNOPTIC_TABLE_FARM"));
		dmRequestParameters.putAll(mapParameters);
		return callTheDataMiner(dmRequestParameters);
	}


	
	/**
	 * Call the data miner perform fish correlation analysis.
	 *
	 * @param peformFishReponse the peform fish reponse
	 * @param mapParameters the map parameters
	 * @return the data miner response
	 * @throws Exception the exception
	 */
	private DataMinerResponse callTheDataMinerPerformFishCorrelationAnalysis(PerformFishResponse peformFishReponse, Map<String, List<String>> dmRequestParameters) throws Exception{
		
		PortalContextInfo pContext = ContextUtil.getPortalContext(this.getThreadLocalRequest());

		ServiceParameters dataMinerService = ContextUtil.getDataMinerService(this.getThreadLocalRequest());
		log.info("Found DM service: "+dataMinerService.getUrl() + " int this scope: "+pContext.getCurrentScope());
//		if(!ContextUtil.isWithinPortal()){
//			dataMinerService = new ServiceParameters("http://dataminer-prototypes.d4science.org/wps/WebProcessingService", null, null, dmRequestParameters);
//			log.info("I'm in TEST MODE replacing it with HARD CODED: "+dataMinerService);
//		}
		dataMinerService.setProperties(dmRequestParameters);
		//Asdding client parameters to DM service request
		DMServiceResponse dmResponse = null;
		String response;
		try{
			response = new HttpCallerUtil(dataMinerService.getUrl(), null, null).performGETRequestWithRetry(dmRequestParameters, pContext.getUserToken(), 5);
			if(response==null){
				log.error("The presponse returned is null");
				throw new Exception("The presponse returned is null");
			}
			dmResponse = DataMinerUtil.parseResult(dataMinerService.getUrl(), response);

		}catch(Exception e){
			throw new Exception("The service did not produce any result. Change your selection and try again.");
		}


		if(dmResponse == null || dmResponse.isWithError())
			throw new Exception("The response returned by DM service contains an Exception Status. (The call is: "+dmResponse.getHttpRequestURL()+"). Please report this issue at www.d4science.org/contact-us");

		try{

			DataMinerOutputData toDMOutputData = null;
			List<DataMinerOutputData> listOut = dmResponse.getListDMOutputData();
			for (DataMinerOutputData dataMinerOutputData : listOut) {
				//I'm using this specific output data of DM
				if(dataMinerOutputData.getFileDescription().toLowerCase().contains("outputcharts")){
					log.info("The output: "+dataMinerOutputData.getFileDescription()+ " with: "+dataMinerOutputData.getMimeType()+" is the candidate to unzip");
					toDMOutputData = dataMinerOutputData;
					break;
				}
			}

			if(toDMOutputData==null || toDMOutputData.getPublicURL()==null)
				throw new Exception("The response returned by DM service does not contain a file to unzip with name: 'outputcharts'. Please report this issue at www.d4science.org/contact-us");

			String theZipFileURL = toDMOutputData.getPublicURL();
			log.info("I'm using the file: "+theZipFileURL);

			FileContentType filter = FileContentType.CSV;
			ZipExtractorUtil zipExt = new ZipExtractorUtil(theZipFileURL, Arrays.asList(filter));
			List<OutputFile> output = zipExt.getOutputFiles();
			log.info("Extracted output of type {} as list {}: ",filter,output);

			output = manageOutputsForPerformFishAnalysis(output);
			log.info("Managed output of type {} as list {}: ",filter,output);

			DataMinerResponse theDMResponse = new DataMinerResponse(peformFishReponse, output);
			log.debug("Returning {}",theDMResponse);
			return theDMResponse;

		}catch(Exception e){
			log.error("There was an error extracting the DataMiner response from your request: ", e);
			throw new Exception("The service did not produce any result. Change your request and try again.");

		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.performfishanalytics.client.PerformFishAnalyticsService#callingDataMinerPerformFishAnalysis(org.gcube.portlets.user.performfishanalytics.shared.performfishservice.PerformFishResponse, java.util.Map)
	 */
	@Override
	public DataMinerResponse callingDataMinerPerformFishAnalysis(Map<String, List<String>> algorithmMapParameters) throws Exception{

		log.info("Calling the DM service with algorithm parameters: "+algorithmMapParameters.toString());

		Map<String, List<String>> dmRequestParameters = new HashMap<String, List<String>>();
		dmRequestParameters.put("request", Arrays.asList("Execute"));
		dmRequestParameters.put("service", Arrays.asList("WPS"));
		dmRequestParameters.put("Version", Arrays.asList("1.0.0"));
		dmRequestParameters.put("lang", Arrays.asList("en-US"));
		dmRequestParameters.put("Identifier", Arrays.asList("org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.PERFORMFISH_ANALYSIS"));
		dmRequestParameters.putAll(algorithmMapParameters);
		
		return callTheDataMiner(dmRequestParameters);
	}
	
	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.performfishanalytics.client.PerformFishAnalyticsService#callingDataMinerPerformFishAnalysis(org.gcube.portlets.user.performfishanalytics.shared.performfishservice.PerformFishResponse, java.util.Map)
	 */
	@Override
	public DataMinerResponse callingDataMinerPerformFishAnnualAnalysis(Map<String, List<String>> algorithmMapParameters) throws Exception{

		log.info("Calling the DM service with algorithm parameters: "+algorithmMapParameters.toString());

		Map<String, List<String>> dmRequestParameters = new HashMap<String, List<String>>();
		dmRequestParameters.put("request", Arrays.asList("Execute"));
		dmRequestParameters.put("service", Arrays.asList("WPS"));
		dmRequestParameters.put("Version", Arrays.asList("1.0.0"));
		dmRequestParameters.put("lang", Arrays.asList("en-US"));
		dmRequestParameters.put("Identifier", Arrays.asList("org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.PERFORMFISH_ANALYSIS_ANNUAL"));
		dmRequestParameters.putAll(algorithmMapParameters);
		
		return callTheDataMiner(dmRequestParameters);
	}
	

	/**
	 * Call the data miner.
	 *
	 * @param dmServiceRequestParameters the dm request parameters
	 * @return the data miner response
	 * @throws Exception the exception
	 */
	private DataMinerResponse callTheDataMiner(Map<String, List<String>> dmServiceRequestParameters) throws Exception {
		
		ServiceParameters dataMinerService = ContextUtil.getDataMinerService(this.getThreadLocalRequest());
		log.info("Found DM service: "+dataMinerService.getUrl() + " int this scope: "+ContextUtil.getPortalContext(this.getThreadLocalRequest()).getCurrentScope());

		dataMinerService.setProperties(dmServiceRequestParameters);
		//Addding client parameters to DM service request

		DMServiceResponse dmResponse = null;
		String response;
		PortalContextInfo pContext = ContextUtil.getPortalContext(this.getThreadLocalRequest());
		try{
			response = new HttpCallerUtil(dataMinerService.getUrl(), null, null).performGETRequestWithRetry(dmServiceRequestParameters, pContext.getUserToken(), 5);
			if(response==null){
				log.error("The presponse returned is null");
				throw new Exception("The presponse returned is null");
			}
			dmResponse = DataMinerUtil.parseResult(dataMinerService.getUrl(), response);

		}catch(Exception e){
			throw new Exception("The service did not produce any result. Change your selection and try again.");
		}

		if(dmResponse == null || dmResponse.isWithError())
			throw new Exception("The response returned by DM service contains an Exception Status. (The call is: "+dmResponse.getHttpRequestURL()+"). Please report this issue at www.d4science.org/contact-us");

		try{

			DataMinerOutputData toDMOutputData = null;
			List<DataMinerOutputData> listOut = dmResponse.getListDMOutputData();
			for (DataMinerOutputData dataMinerOutputData : listOut) {
				//I'm using this specific output data of DM
				if(dataMinerOutputData.getFileDescription().toLowerCase().contains("outputcharts")){
					log.info("The output: "+dataMinerOutputData.getFileDescription()+ " with: "+dataMinerOutputData.getMimeType()+" is the candidate to unzip");
					toDMOutputData = dataMinerOutputData;
					break;
				}
				
				if(dataMinerOutputData.getFileDescription().toLowerCase().contains("outputfile")){
					log.info("The output: "+dataMinerOutputData.getFileDescription()+ " with: "+dataMinerOutputData.getMimeType()+" is the candidate to unzip");
					toDMOutputData = dataMinerOutputData;
					break;
				}
			}

			if(toDMOutputData==null || toDMOutputData.getPublicURL()==null)
				throw new Exception("The response returned by DM service does not contain a file to unzip with name: 'outputcharts' or 'outputfile'. Please report this issue at www.d4science.org/contact-us");

			String theZipFileURL = toDMOutputData.getPublicURL();
			log.info("I'm using the file: "+theZipFileURL);

			FileContentType filter = null;
			ZipExtractorUtil zipExt = new ZipExtractorUtil(theZipFileURL, null);
			List<OutputFile> output = zipExt.getOutputFiles();
			log.info("Extracted output of type {} as list {}: ",filter,output);

			DataMinerResponse theDMResponse = new DataMinerResponse(null, output);
			log.debug("Returning {}",theDMResponse);
			return theDMResponse;

		}catch(Exception e){
			log.error("There was an error extracting the DataMiner response from your request: ", e);
			throw new Exception("The service did not produce any result. Change your request and try again.");

		}
	}


	/**
	 * Manage outputs for perform fish analysis.
	 *
	 * @param output the output
	 * @return the list
	 */
	public List<OutputFile> manageOutputsForPerformFishAnalysis(List<OutputFile> output) {

		List<OutputFile> newOutputFiles = new ArrayList<OutputFile>();
		try {

			Map<String, List<String>> theLegendMap = new HashMap<String, List<String>>();

			//FINDING THE FILE WIHT THE LEGEND
			for (OutputFile outputFile : output) {
				log.trace("outputFile: {}", outputFile.getName());
				if(outputFile.getName().toLowerCase().contains("legend") && outputFile.getDataType().equals(FileContentType.CSV)){
					log.debug("Found legend file: {}", outputFile.getName());
					CSVFile theLegendFile = getCSVFile(outputFile, false);
					List<CSVRow> rows = theLegendFile.getValueRows();
					//CREATING FROM *_legend_* CSV
					//THE LEGEND WITH FIRST VALUE AS KEY AND REMAINING VALUES AS PROPERTIES
					for (CSVRow csvRow : rows) {
						theLegendMap.put(csvRow.getListValues().get(0), csvRow.getListValues().subList(1, csvRow.getListValues().size()));
					}
					break;
				}
			}

			if(theLegendMap.size()>0){
				log.info("Legend created as {}", theLegendMap.toString());
				for (OutputFile outputFile : output) {
					OutputFile theOutputFile = outputFile;
					String toNameLower = outputFile.getName().toLowerCase();
					
					//SKIPPING THE LEGEND
					if(toNameLower.contains("legend")) {
						continue;
					}
					
					if(outputFile.getDataType().equals(FileContentType.CSV)) {
						
						//FINDING THE FILE *index* TO CREATE A NEW CSV REPLACING THE LABELS 'A','B','C', etc. WITH THE NAMES (THE KPI NAMES) CONTAINED IN THE LEGEND
						if(toNameLower.contains("index")){
							CSVFile theCorrelationMatrixIndexCSVFile = getCSVFile(outputFile, true);
							try{
								theOutputFile = createCSVWithLegendValues(theCorrelationMatrixIndexCSVFile, theLegendMap);
							}catch(Exception e){
								log.warn("Error thrown creating the CSV File with legend returning the original output file {}", outputFile);
								theOutputFile = outputFile;
							}
							//break;
						}
					}
					
					//RETURNING ALSO THE file correlation_matrix.csv for applying the COLORS
					newOutputFiles.add(theOutputFile);
				}
				
			}else{
				log.warn("The Legend file not found returning the original output files {}", output);
				return output;
			}

			return newOutputFiles;

		}catch (Exception e) {
			log.warn("Error occured managing the  CSV File returing the original files extracted form .zip {}", output);
			return output;
		}

	}


	/**
	 * Creates the csv with legend values.
	 *
	 * @param theCorrelationFile the the correlation file
	 * @param theLegendMap the the legend map
	 * @return the output file
	 * @throws Exception the exception
	 */
	private OutputFile createCSVWithLegendValues(CSVFile theCorrelationFile, Map<String, List<String>> theLegendMap) throws Exception{

		CSVWriter cswWriter = null;
		try{

			CSVRow headerRow = theCorrelationFile.getHeaderRow();
	    	java.nio.file.Path path = Files.createTempFile("With_Legend_"+theCorrelationFile.getFileName(), ".csv");
	    	log.debug("Created temp file: {}", path.getFileName());
	        File tempFile = path.toFile();
			cswWriter = new CSVWriter(tempFile);

			StringBuilder lineBuilder = new StringBuilder();
			for (String headerValue : headerRow.getListValues()) {
				if(theLegendMap.containsKey(headerValue)){
					List<String> legendValue = theLegendMap.get(headerValue);
					for (String value : legendValue) {
						lineBuilder.append(value);
						lineBuilder.append(",");
					}
				}else{
					lineBuilder.append(headerValue);
					lineBuilder.append(",");
				}
			}
			String headerLine = lineBuilder.toString();
			headerLine = removeLastChar(headerLine);
			log.debug("Writed header line: {}", headerLine);
			cswWriter.writeCSVLine(headerLine);
			for (CSVRow cswRow : theCorrelationFile.getValueRows()) {
				lineBuilder = new StringBuilder();
				for (String csvValue : cswRow.getListValues()) {
					if(theLegendMap.containsKey(csvValue)){
						List<String> legendValue = theLegendMap.get(csvValue);
						for (String value : legendValue) {
							lineBuilder.append(value);
							lineBuilder.append(",");
						}
					}else{
						lineBuilder.append(csvValue);
						lineBuilder.append(",");
					}

				}
				String csvLine = lineBuilder.toString();
				csvLine = removeLastChar(csvLine);
				log.debug("Writed line: {}", csvLine);
				cswWriter.writeCSVLine(csvLine);
			}

			OutputFile output = new OutputFile();
			output.setDataType(FileContentType.CSV);
			output.setName(tempFile.getName());
			output.setServerLocation(tempFile.getAbsolutePath());
			return output;

		}catch(Exception e){
			log.info("Error on creating CSV File with legend: ", e);
			throw new Exception("Error on creating CSV File with legend");

		}finally{
			try{
				if(cswWriter!=null)
					cswWriter.closeWriter();
			}catch(Exception e){
				//silent
			}

		}
	}

	/**
	 * Removes the last char.
	 *
	 * @param str the str
	 * @return the string
	 */
	private static String removeLastChar(String str) {

		if(str==null || str.length()<1)
			return str;

	    return str.substring(0, str.length() - 1);
	}

	/**
	 * Gets the CSV file.
	 *
	 * @param file the file
	 * @param deleteAfter the delete after
	 * @return the CSV file
	 * @throws Exception the exception
	 */
	@Override
	public CSVFile getCSVFile(OutputFile file, boolean deleteAfter) throws Exception{

		File theFile = null;
		try{
			if(file==null || file.getServerLocation()==null || file.getServerLocation().isEmpty()){
				throw new Exception("Invalid image file null");
			}

			theFile = new File(file.getServerLocation());
			CSVReader reader = new CSVReader(theFile);
			CSVFile csvFile = reader.getCsvFile();
			csvFile.setFileName(file.getName());
			return csvFile;
		}catch(Exception e){
			log.error("There was an error extracting getting the CSV file: "+file.getName(), e);

			throw new Exception("There was an error extracting getting the CSV file: "+file.getName()
							+ ". Please report this issue at www.d4science.org/contact-us");

		}finally{
			//delete the file;
			if(theFile!=null && deleteAfter){
				try{
					//Files.deleteIfExists(theFile.toPath());
				}catch(Exception e){
					//silent
				}
			}
		}
	}


	/**
	 * Gets the CSV file.
	 *
	 * @param theFileURL the the file url
	 * @return the CSV file
	 * @throws Exception the exception
	 */
	@Override
	public CSVFile readCSVFile(String theFileURL) throws Exception{
		log.info("Trying to read a csv file from URL: {}",theFileURL);

		File theFile = null;

		if(theFileURL==null){
			throw new Exception("Invalid file URL. It is null");
		}

		URL theURL;
		try{
			theURL = new URL(theFileURL);
			log.debug("URL: {} created correclty",theURL.toString());
		}catch(MalformedURLException e){
			log.error("There input URL "+theFileURL+" is  malformed URL", e);
			throw new Exception("There input URL "+theFileURL+" is  malformed URL"
				+ ". Please report this issue at www.d4science.org/contact-us");
		}

		try{
			theFile = ZipExtractorUtil.createTempFile("CVS_FILE_"+UUID.randomUUID(), ".csv", copyToByteArray(theURL));
			CSVReader reader = new CSVReader(theFile);
			return reader.getCsvFile();
		}catch(Exception e){
			log.error("There was an error getting the CSV file from URL "+theURL.getRef(), e);

			throw new Exception("There was an error getting the CSV file: "+theURL.getRef()
							+ ". Please report this issue at www.d4science.org/contact-us");

		}
	}


	/**
	 * Copy to byte array.
	 *
	 * @param url the url
	 * @return the byte[]
	 * @throws Exception the exception
	 */
	public byte[] copyToByteArray(URL url) throws Exception{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		InputStream is = null;
		try {
		  is = url.openStream ();
		  byte[] byteChunk = new byte[4096]; // Or whatever size you want to read in at a time.
		  int n;

		  while ( (n = is.read(byteChunk)) > 0 ) {
		    baos.write(byteChunk, 0, n);
		  }

		  return baos.toByteArray();
		}
		catch (IOException e) {
		  log.error("Failed while reading bytes from %s: %s", url.toExternalForm(), e.getMessage());
		  throw new Exception("Copy to byte array error");
		}
		finally {
		  if (is != null) {
			  try{
				  is.close();
				  baos.close();
			  }catch(Exception e){
				  //silent
			  }
		  }
		}
	}




	/**
	 * Gets the image file.
	 *
	 * @param file the file
	 * @return the image file
	 * @throws Exception the exception
	 */
	@Override
	public String getImageFile(OutputFile file) throws Exception{

		File theFile = null;
		try{
			if(file==null || file.getServerLocation()==null || file.getServerLocation().isEmpty()){
				throw new Exception("Invalid image file null");
			}

			theFile = new File(file.getServerLocation());
			byte[] imageContent = Files.readAllBytes(theFile.toPath());
	//		System.out.println("<img src='data:image/png;base64," + DatatypeConverter.printBase64Binary(imageInByte) + "'></img>");

			return "<img src='data:image/png;base64," + DatatypeConverter.printBase64Binary(imageContent) + "'></img>";
		}catch(Exception e){
			log.error("There was an error getting the image file: "+file.getName(), e);

			throw new Exception("There was an error getting the image file: "+file.getName()
							+ ". Please report this issue at www.d4science.org/contact-us");

		}finally{
			//delete the file;
			if(theFile!=null){
				try{
					Files.deleteIfExists(theFile.toPath());
				}catch(Exception e){
					//silent
				}
			}
		}
	}

}
