package org.gcube.dataanalysis.ecoengine.processing;

import java.util.HashMap;
import java.util.List;

import org.gcube.contentmanagement.graphtools.utils.HttpRequest;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.ALG_PROPS;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.configuration.INFRASTRUCTURE;
import org.gcube.dataanalysis.ecoengine.connectors.RemoteGenerationManager;
import org.gcube.dataanalysis.ecoengine.connectors.RemoteHspecInputObject;
import org.gcube.dataanalysis.ecoengine.connectors.RemoteHspecOutputObject;
import org.gcube.dataanalysis.ecoengine.connectors.livemonitor.ResourceLoad;
import org.gcube.dataanalysis.ecoengine.connectors.livemonitor.Resources;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.interfaces.GenericAlgorithm;

//deprecated
public class RainyCloudGenerator {

	AlgorithmConfiguration config;
	private boolean interruptProcessing;
	RemoteGenerationManager remoteGenerationManager;
	RemoteHspecInputObject rhio;

	public RainyCloudGenerator(AlgorithmConfiguration config) {
		setConfiguration(config);
		init();
	}

	public RainyCloudGenerator() {
	}

	public float getStatus() {
		RemoteHspecOutputObject oo = remoteGenerationManager.retrieveCompleteStatus();

		// if (oo.status.equals("DONE")||oo.status.equals("ERROR"))
		if (oo.status.equals("DONE")) {
			shutdown();
			return 100f;
		} else {
			float remoteStatus = (float) remoteGenerationManager.retrieveCompletion();
			return (remoteStatus == 100) ? 99 : remoteStatus;
		}
	}

	public void init() {
		AnalysisLogger.setLogger(config.getConfigPath() + AlgorithmConfiguration.defaultLoggerFile);
		interruptProcessing = false;
		rhio = new RemoteHspecInputObject();
		rhio.userName = config.getParam("ServiceUserName");
		rhio.environment = config.getParam("RemoteEnvironment");
		rhio.configuration = config.getGeneralProperties();

		rhio.generativeModel = config.getModel();

		String jdbcUrl = config.getParam("DatabaseURL");
		String userName = config.getParam("DatabaseUserName");
		String password = config.getParam("DatabasePassword");
		jdbcUrl += ";username=" + userName + ";password=" + password;

		// jdbc:sqlserver://localhost;user=MyUserName;password=*****;
		rhio.hcafTableName.tableName = config.getParam("CsquarecodesTable");
		rhio.hcafTableName.jdbcUrl = jdbcUrl;

		rhio.hspecDestinationTableName.tableName = config.getParam("DistributionTable");
		rhio.hspecDestinationTableName.jdbcUrl = jdbcUrl;

		rhio.hspenTableName.tableName = config.getParam("EnvelopeTable");
		rhio.hspenTableName.jdbcUrl = jdbcUrl;

		rhio.occurrenceCellsTable.tableName = "maxminlat_" + config.getParam("EnvelopeTable");
		rhio.occurrenceCellsTable.jdbcUrl = jdbcUrl;
		rhio.nWorkers = config.getNumberOfResources();

		if (config.getModel().contains("2050"))
			rhio.is2050 = true;
		else
			rhio.is2050 = false;

		if (config.getModel().contains("NATIVE"))
			rhio.isNativeGeneration = true;
		else
			rhio.isNativeGeneration = false;

		// create and call the remote generator
		remoteGenerationManager = new RemoteGenerationManager(config.getParam("RemoteCalculator"));
	}

	

	public void setConfiguration(AlgorithmConfiguration config) {
		this.config = config;
	}

	public void shutdown() {
		interruptProcessing = true;
	}

	public String getResourceLoad() {
		String returnString = "[]";

		try {
			RemoteHspecOutputObject rhoo = remoteGenerationManager.retrieveCompleteStatus();
			if (rhoo.metrics.throughput.size() > 1) {
				ResourceLoad rs = new ResourceLoad(rhoo.metrics.throughput.get(0), rhoo.metrics.throughput.get(1));
				returnString = rs.toString();
			}

		} catch (Exception e) {
		}
		return returnString;
	}

	public String getResources() {
		Resources res = new Resources();
		try {
			RemoteHspecOutputObject rhoo = remoteGenerationManager.retrieveCompleteStatus();
			res.list = rhoo.metrics.load;
		} catch (Exception e) {
		}
		if ((res != null) && (res.list != null))
			return HttpRequest.toJSon(res.list).replace("resId", "resID");
		else
			return "[]";
	}

	public String getLoad() {
		RemoteHspecOutputObject rhoo = remoteGenerationManager.retrieveCompleteStatus();
		String returnString = "[]";
		if ((rhoo.metrics.throughput != null) && (rhoo.metrics.throughput.size() > 1)) {
			ResourceLoad rs = new ResourceLoad(rhoo.metrics.throughput.get(0), rhoo.metrics.throughput.get(1));
			returnString = rs.toString();
		}
		return returnString;
	}

	
	public void generate() throws Exception {

		try {
			remoteGenerationManager.submitJob(rhio);
		} catch (Exception e) {
			e.printStackTrace();
		}

		AnalysisLogger.getLogger().trace("REMOTE PROCESSING STARTED");
		boolean finish = false;

		while (!finish && !interruptProcessing) {
			float status = getStatus();
			// AnalysisLogger.getLogger().trace("Status "+status);
			if (status == 100)
				finish = true;
			Thread.sleep(500);
		}

		AnalysisLogger.getLogger().trace("REMOTE PROCESSING ENDED");
	}

	
	public ALG_PROPS[] getSupportedAlgorithms() {
		ALG_PROPS[] p = { ALG_PROPS.SPECIES_VS_CSQUARE_REMOTE_FROM_DATABASE };
		return p;
	}

	
	public INFRASTRUCTURE getInfrastructure() {
		return INFRASTRUCTURE.D4SCIENCE;
	}

	/*
	public HashMap<String, VarCouple> getInputParameters() {
		HashMap<String, VarCouple> parameters = new HashMap<String, VarCouple>();
		parameters.put("RemoteEnvironment", new VarCouple(VARTYPE.INFRA,""));
		parameters.put("RemoteCalculator", new VarCouple(VARTYPE.INFRA,""));
		parameters.put("ServiceUserName", new VarCouple(VARTYPE.SERVICE,""));
		
		parameters.put("CsquarecodesTable", new VarCouple(VARTYPE.STRING,""));
		parameters.put("DatabaseURL", new VarCouple(VARTYPE.DATABASEURL,""));
		parameters.put("DatabaseUserName", new VarCouple(VARTYPE.DATABASEUSERNAME,""));
		parameters.put("DatabasePassword", new VarCouple(VARTYPE.DATABASEPASSWORD,""));
		parameters.put("DistributionTable", new VarCouple(VARTYPE.RANDOM,"hspec_rem_"));
		parameters.put("EnvelopeTable", new VarCouple(VARTYPE.STRING,""));
		
		return parameters;
	}
	
	
	public VARTYPE getContentType() {
		return VARTYPE.HSPEC;
	}

	
	public Object getContent() {
		return config.getParam("DistributionTable");
	}
*/
}
