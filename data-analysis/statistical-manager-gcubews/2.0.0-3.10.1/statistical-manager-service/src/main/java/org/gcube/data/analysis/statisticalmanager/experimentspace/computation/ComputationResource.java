package org.gcube.data.analysis.statisticalmanager.experimentspace.computation;

import java.io.File;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.gcube.accounting.datamodel.UsageRecord.OperationResult;
import org.gcube.accounting.datamodel.usagerecords.JobUsageRecord;
import org.gcube.accounting.persistence.AccountingPersistence;
import org.gcube.accounting.persistence.AccountingPersistenceFactory;
import org.gcube.common.core.state.GCUBEWSResource;
import org.gcube.common.encryption.StringEncrypter;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.Property;
import org.gcube.common.resources.gcore.utils.Group;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.analysis.statisticalmanager.Configuration;
import org.gcube.data.analysis.statisticalmanager.SMOperationStatus;
import org.gcube.data.analysis.statisticalmanager.experimentspace.AlgorithmCategory;
import org.gcube.data.analysis.statisticalmanager.experimentspace.ComputationFactory;
import org.gcube.data.analysis.statisticalmanager.persistence.DataBaseManager;
import org.gcube.data.analysis.statisticalmanager.persistence.RemoteStorage;
import org.gcube.data.analysis.statisticalmanager.persistence.RuntimeResourceManager;
import org.gcube.data.analysis.statisticalmanager.persistence.SMPersistenceManager;
import org.gcube.data.analysis.statisticalmanager.persistence.algorithms.AlgorithmManager;
import org.gcube.data.analysis.statisticalmanager.stubs.SMComputationConfig;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.configuration.INFRASTRUCTURE;
import org.gcube.dataanalysis.ecoengine.datatypes.DatabaseType;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveType;
import org.gcube.dataanalysis.ecoengine.datatypes.ServiceType;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.DatabaseParameters;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.PrimitiveTypes;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.ServiceParameters;
import org.gcube.dataanalysis.ecoengine.interfaces.ComputationalAgent;
import org.gcube.dataanalysis.ecoengine.processing.factories.ClusterersFactory;
import org.gcube.dataanalysis.ecoengine.processing.factories.EvaluatorsFactory;
import org.gcube.dataanalysis.ecoengine.processing.factories.GeneratorsFactory;
import org.gcube.dataanalysis.ecoengine.processing.factories.ModelersFactory;
import org.gcube.dataanalysis.ecoengine.processing.factories.TransducerersFactory;
import org.gcube.documentstore.exception.InvalidValueException;
import org.gcube_system.namespaces.data.analysis.statisticalmanager.types.ComputationalAgentClass;
import org.gcube_system.namespaces.data.analysis.statisticalmanager.types.SMComputation;
import org.gcube_system.namespaces.data.analysis.statisticalmanager.types.SMFile;
import org.gcube_system.namespaces.data.analysis.statisticalmanager.types.SMInputEntry;
import org.gcube_system.namespaces.data.analysis.statisticalmanager.types.SMResource;
import org.globus.wsrf.ResourceProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ComputationResource extends GCUBEWSResource {

	private static Logger logger = LoggerFactory.getLogger(ComputationResource.class);

	private static final String NAME_RP_NAME = "computation";
	private volatile String userLogin;
	AccountingPersistence raFactory = null;

	private static ConcurrentHashMap<String, ComputationalAgent> runningCAgents = new ConcurrentHashMap<String, ComputationalAgent>();



	public void cleanResourcesComputational(final INFRASTRUCTURE compInf,
			final long computationId) {

		switch (compInf) {
		case LOCAL:
			logger.debug("---------- Clean up local resources");
			ComputationFactory.getFactoryResource().cleanLocalComputation();
			// ComputationFactory.getFactoryResource().
			// cleanLocalResourcesComputational(String.valueOf(computationId));
			break;
		case D4SCIENCE:
			logger.debug("--------- Clen up D4Science resources");
			ComputationFactory.getFactoryResource().cleanD4ScienceComputation();
		default:
			ComputationFactory.getFactoryResource().cleanLocalComputation();
			logger.debug("---------- Clean up local resources");
			break;
		}

	}

	private synchronized void addComputationalAgent(String key,
			ComputationalAgent agent) {

		runningCAgents.put(key, agent);
		ResourceProperty property = getResourcePropertySet().get(NAME_RP_NAME);
		property.add(key);
	}

	private synchronized void removeComputationalAgent(String key) {

		runningCAgents.remove(key);
		ResourceProperty property = getResourcePropertySet().get(NAME_RP_NAME);
		property.clear();
		for (Entry<String, ComputationalAgent> entry : runningCAgents
				.entrySet()) {
			property.add(entry.getKey());
		}

	}

	private void setComputationOutput(long computationId,ComputationalAgent agent) throws Exception {

		SMComputation computation = (SMComputation) SMPersistenceManager.getOperation(computationId);
		StatisticalType output = agent.getOutput();
		BuilderComputationOutput builder = new BuilderComputationOutput(userLogin, computation);
		SMResource resource = builder.serialize(output);
		logger.debug("created SMResource output");
		logger.debug("save resource in db.......");
		SMPersistenceManager.addCreatedResource(computationId, resource);
	}

	/** {@inheritDoc} */
	@Override
	public void initialise(Object... args) throws Exception {		
		userLogin = (String) args[0];
		logger.debug(String.format("------ initialize Service Resource, current Scope %s, user %s ",
				ScopeProvider.instance.get(),userLogin));
		
		try {
			raFactory = AccountingPersistenceFactory.getPersistence();
		} catch (Exception e) {
			logger.error("error in  initialization" + e.getMessage());
		}

	}

	/** {@inheritDoc} */
	@Override
	protected String[] getPropertyNames() {
		return new String[] { NAME_RP_NAME };
	}

	public ConcurrentHashMap<String, ComputationalAgent> getComputationalAgents() {
		return runningCAgents;
	}

	private AlgorithmConfiguration setUserParameters(AlgorithmConfiguration algoConfig, SMComputationConfig requestConfig)throws Exception {

		Map<String, StatisticalType> parameters = AlgorithmManager.getAlgorithmByName(requestConfig.getAlgorithm()).getParameters();

		// Set user algorithms parameters
		for (SMInputEntry parameter : requestConfig.getParameters().getList()) {
			logger.debug("Set Parameter user key " + parameter.getKey()	+ " value " + parameter.getValue());
			String value = parameter.getValue();

			StatisticalType typeParam = parameters.get(parameter.getKey());
			if ((typeParam instanceof PrimitiveType)
					&& (((PrimitiveType) typeParam).getType() == PrimitiveTypes.FILE)) {

				SMFile file = SMPersistenceManager.getFile(parameter.getValue());
				logger.debug("Parameter is a file with id "	+ parameter.getValue());
				logger.debug("File with name" + file.getName());
				logger.debug("File with url" + file.getUrl());
				logger.debug("File with remote name" + file.getRemoteName());

				File tmpFile = new RemoteStorage().importByUri(file.getUrl());

				logger.debug("----------- File created "+ tmpFile.getAbsolutePath());

				value = tmpFile.getAbsolutePath();
			}

			algoConfig.setParam(parameter.getKey(), value);
		}
		return algoConfig;
	}

	private void setServiceParameters(SMComputationConfig computationConfig,
			AlgorithmConfiguration algoConfig, Collection<StatisticalType> parameters)
					throws SMParametersSettingException,Exception {

		logger.debug("Parameter retrieved " + parameters.size());

		String rrEndPoint = null;
		String rrUser = null;
		String rrPassword = null;
		String rrDBName = null;
		String rrDBDriver = null;
		String rrDBDialect = null;
		logger.debug("---------Inside setServiceParameter  scope is "+ ScopeProvider.instance.get());
		// algoConfig.setGcubeScope(scope);
		for (StatisticalType parameter : parameters) {
			if (parameter.getClass() == DatabaseType.class) {
				DatabaseType dbType = (DatabaseType) parameter;
				if (dbType.getDatabaseParameter() == DatabaseParameters.REMOTEDATABASERRNAME) {
					try {
						Group<AccessPoint> accessPoints = RuntimeResourceManager.getRRAccessPoint(dbType.getName());
						for (AccessPoint accessPoint : accessPoints) {
							rrEndPoint = accessPoint.address();
							logger.debug("RR EndPoint " + rrEndPoint);
							rrUser = accessPoint.username();
							logger.debug("RR User " + rrUser);
							rrPassword = StringEncrypter.getEncrypter().decrypt(accessPoint.password());
							logger.debug("RR Password " + rrPassword);

							Map<String, Property> properties = accessPoint.propertyMap();

							Property property = properties.get(Configuration.getProperty(Configuration.SERVICE_PARAMETER_DATABASE_NAME));
							rrDBName = property.name();
							logger.debug("RR DB Name " + rrDBName);
							rrDBDialect = properties.get(Configuration.getProperty(Configuration.SERVICE_PARAMETER_DIALECT)).name();
							logger.debug("RR DB Dialect " + rrDBDialect);
							rrDBDriver = properties.get(Configuration.getProperty(Configuration.SERVICE_PARAMETER_DRIVER)).name();
							logger.debug("RR DB Driver " + rrDBDriver);

						}

					} catch (Exception e) {
						logger.error("RR " + dbType.getName() + " NOT FOUND OR Invalid", e);
					}
				}

			}
		}

		for (StatisticalType parameter : parameters) {

			logger.debug("Parameter retrieved " + parameter.getClass());
			if (parameter instanceof DatabaseType) {
				DataBaseManager dbMng=DataBaseManager.get();
				switch (((DatabaseType) parameter).getDatabaseParameter()) {
				case DATABASEURL:
					algoConfig.setParam(parameter.getName(),dbMng.getUrlDB());
					break;
				case DATABASEPASSWORD:
					algoConfig.setParam(parameter.getName(),dbMng.getPassword());
					break;
				case DATABASEUSERNAME:
					algoConfig.setParam(parameter.getName(),dbMng.getUsername());
					break;
				case DATABASEDRIVER:
					algoConfig.setParam(parameter.getName(),
							(rrDBDriver != null) ? rrDBDriver : dbMng.getDriver());
					break;
				case DATABASEDIALECT:
					if (rrDBDialect != null)algoConfig.setParam(parameter.getName(), rrDBDialect);

				case REMOTEDATABASEURL:
					algoConfig.setParam(parameter.getName(), rrEndPoint);
					break;

				case REMOTEDATABASEUSERNAME:
					algoConfig.setParam(parameter.getName(), rrUser);
					break;

				case REMOTEDATABASEPASSWORD:
					algoConfig.setParam(parameter.getName(), rrPassword);
					break;

				default:
					break;
				}
			}

			if (parameter instanceof ServiceType) {
				if (((ServiceType) parameter).getServiceParameter() == ServiceParameters.RANDOMSTRING) {
					String id = "ID_"+ UUID.randomUUID().toString().replace("-", "_");
					if (parameter.getDefaultValue() != null) id = parameter.getDefaultValue() + id;

					logger.debug("Param service name:" + parameter.getName()+ " value :" + id.toLowerCase());
					algoConfig.setParam(parameter.getName(), id.toLowerCase());
				}

				if (((ServiceType) parameter).getServiceParameter() == ServiceParameters.USERNAME) {
					logger.debug("Param service name:" + parameter.getName()
							+ " value :" + getID().getValue());
					algoConfig
					.setParam(parameter.getName(), getID().getValue());
				}

				// add code in order to obtaine generic runtime resource
				if (((ServiceType) parameter).getServiceParameter() == ServiceParameters.INFRA) {
					try {
						logger.debug("------ INFRA parameter ");						
						for (Map.Entry<String, String> entry : RuntimeResourceManager.getServiceEndpointAsMap(parameter.getName()).entrySet())
							algoConfig.setParam(entry.getKey(), entry.getValue());

					} catch (Exception e) {
						logger.error("INFRA RR " + parameter.getName()+ " NOT FOUND", e);
					}
				}

			}

			if ((parameter instanceof PrimitiveType)&& ((PrimitiveType) parameter).getType() == PrimitiveTypes.CONSTANT) {
				algoConfig.setParam(parameter.getName(),parameter.getDefaultValue());
				logger.debug("Param primitive name constant : "	+ parameter.getName() + " value : "+ parameter.getDefaultValue());
			}
		}
	}


	public void executeComputation(SMComputationConfig computationConfig,
			final long computationId) throws SMResourcesNotAvailableException {

		AlgorithmConfiguration algoConfig = new AlgorithmConfiguration();
		String algorithm = null;
		try {
			logger.debug(" ------------- Computation request: ");
			String configPath = Configuration.getConfigPath();
			algorithm = computationConfig.getAlgorithm();
			logger.debug(" ------------- Algorithm request" + algorithm);

			algoConfig.setConfigPath(configPath);
			algoConfig.setAgent(algorithm);
			algoConfig.setModel(algorithm);
			algoConfig.setPersistencePath(configPath);


			// algoConfig.setGcubeScope("/gcube");
			logger.debug("Set user parameters init");
			setUserParameters(algoConfig, computationConfig);

			logger.debug("Set service paramter init");
			Collection<StatisticalType> parameters = AlgorithmManager.getAlgorithmByName(computationConfig.getAlgorithm()).getParameters().values();
			setServiceParameters(computationConfig, algoConfig, parameters);
		} catch (Exception e) {
			logger.error("Set service parameter error in execute computation :", e);
			try {
				SMPersistenceManager.setOperationStatus(computationId, "Algorithm Execution Error",e.getMessage(),SMOperationStatus.FAILED);

			} catch (Exception e1) {logger.error("Set status failed error in excute computation"+ " set parameters :", e1);
			}
			return;
		}

		try {
			logger.debug("Init computation");
			initComputation(computationConfig, algorithm, algoConfig,
					computationId);
		} catch (SMComputationalAgentInitializationException e) {
			logger.error("Init computation failed", e);
			try {				
				SMPersistenceManager.setOperationStatus(computationId, "INIT Failed","Unable to initialize Computational Agent",SMOperationStatus.FAILED);
			} catch (Exception e1) {
				logger.error("Set status failed error in init computation :",
						e1);
			}
		}
	}

	private JobUsageRecord getAccountingRecord(long jobId, String name) {
		
		JobUsageRecord usageRecord = new JobUsageRecord();
		try {
			usageRecord.setConsumerId(userLogin);
			usageRecord.setOperationResult(OperationResult.SUCCESS);
			usageRecord.setCreationTime(GregorianCalendar.getInstance());
			
			usageRecord.setJobId(jobId+"");
			usageRecord.setJobName(name);
			usageRecord.setScope(ScopeProvider.instance.get());
			usageRecord.setJobQualifier(usageRecord.getJobId());
			return usageRecord;

		} catch (InvalidValueException e) {
			logger.error(" ------ You SHOULD NOT SEE THIS MESSAGE. Error Creating a test Usage Record", e);
			throw new RuntimeException(e);
		}
	}
	
	private void startComputation(final long computationId,
			final INFRASTRUCTURE infra, final ComputationalAgent agent,
			final JobUsageRecord ur)
					throws SMComputationalAgentInitializationException {

		addComputationalAgent(String.valueOf(computationId), agent);
		final String currentScope=ScopeProvider.instance.get();
		new Thread() {
			@Override
			public void run() {
				try {					
					logger.debug("StartComputation Thread, scope is "+ScopeProvider.instance.get()+", setting to "+currentScope);
					ScopeProvider.instance.set(currentScope);
					SMPersistenceManager.setOperationStatus(computationId, "",
							"", SMOperationStatus.RUNNING);
					ur.setJobStartTime(GregorianCalendar.getInstance());
					
					agent.init();
					agent.compute();

					setComputationOutput(computationId, agent);
					
					ur.setJobEndTime(GregorianCalendar.getInstance());
					ur.setOperationResult(OperationResult.SUCCESS);

				} catch (Exception e) {
					logger.error("Compute action failed", e);
					try {


						SMPersistenceManager.setOperationStatus(computationId, "Algorithm Execution Error",e.toString(),
								SMOperationStatus.FAILED);
						ur.setOperationResult(OperationResult.FAILED);

					} catch (Exception e1) {
						logger.error("Error save status FAILED computation "
								+ computationId, e1);
					}

				} finally {
					cleanResourcesComputational(infra, computationId);
					removeComputationalAgent(String.valueOf(computationId));
					try {
						ur.setJobEndTime(GregorianCalendar.getInstance());
					} catch (InvalidValueException e) {
						logger.warn("Unexpected error setting job end time",e);
					}
					try{
						raFactory.account(ur);
					}catch(Exception e){
						logger.warn("Unable to account record "+ur,e);
					}
				}
			}
		}.start();
	}

	private List<? extends ComputationalAgent> getComputationalAgentsAvailable(
			SMComputationConfig computationConfig,
			AlgorithmConfiguration algoConfig)
					throws SMComputationalAgentInitializationException {

		ComputationalAgentClass cac = null;
		try {
			cac = ComputationalAgentClass.fromString(AlgorithmManager.getAlgorithmByName(computationConfig.getAlgorithm()).getCategory().name());
			switch (AlgorithmCategory.valueOf(cac.getValue())) {
			case DISTRIBUTIONS:
				return GeneratorsFactory.getGenerators(algoConfig);
			case EVALUATORS:
				return EvaluatorsFactory.getEvaluators(algoConfig);
			case CLUSTERERS:
				return ClusterersFactory.getClusterers(algoConfig);
			case MODELS:
				return ModelersFactory.getModelers(algoConfig);
			case TRANSDUCERS:
				return TransducerersFactory.getTransducerers(algoConfig);
			default:
				logger.error("Computational agent category not found ");
				throw new Exception();
			}
		} catch (Exception e) {
			logger.error("Computational agent list not found", e);
			throw new SMComputationalAgentInitializationException(
					"Algorithm requested filed : "
							+ ((cac != null) ? cac.getValue()
									: "computation agent not found"));
		}

	}

	private void initComputation(SMComputationConfig computationConfig,
			String algorithm, AlgorithmConfiguration algoConfig,
			long computationId)
					throws SMComputationalAgentInitializationException,
					SMResourcesNotAvailableException {

		logger.debug("INIT Computation "+computationId +" "+computationConfig.getAlgorithm()+", scope is "+ScopeProvider.instance.get());		
		algoConfig.setGcubeScope(ScopeProvider.instance.get());
		
		// TODO Ecological engine library throws an exception
		// if algoConfig has numberOfResources null so set temporarily 1 as
		// numberOfResources

		final JobUsageRecord ur = getAccountingRecord(computationId,
				computationConfig.getAlgorithm());		
		   try {
				ur.setJobStartTime(GregorianCalendar.getInstance());
//				ur.setVmsUsed(1);
			} catch (InvalidValueException e1) {
				logger.error(" Accounting error: ",e1);
			}
		
		algoConfig.setNumberOfResources(1);

		List<? extends ComputationalAgent> agents = getComputationalAgentsAvailable(computationConfig, algoConfig);

		for (ComputationalAgent agent : agents) {

			logger.debug("INFRA IS "+agent.getInfrastructure());

			try{
				SMPersistenceManager.setComputationalInfrastructure(computationId, agent.getInfrastructure());
				switch (agent.getInfrastructure()) {
				case D4SCIENCE: {
					if (!ComputationFactory.getFactoryResource().setD4ScienceComputation())
						throw new SMResourcesNotAvailableException("No D4science resources available");

					List<StatisticalType> parameters = agent.getInputParameters();
					setServiceParameters(computationConfig, algoConfig,	parameters);
					break;
				}

				case LOCAL: {
					int resources = ComputationFactory.getFactoryResource().getLocalResourcesNeeded(computationConfig.getAlgorithm(),
							AlgorithmManager.getAlgorithmByName(computationConfig.getAlgorithm()).getCategory().name());
					logger.debug("---------> Resources needed :" + resources);

					if (!ComputationFactory.getFactoryResource().getLocalComputation())
						throw new SMResourcesNotAvailableException("Local Resources not available");

					algoConfig.setNumberOfResources(resources);
					break;
				}
				default: {
					int resources = ComputationFactory.getFactoryResource().getLocalResourcesNeeded(computationConfig.getAlgorithm(),
							AlgorithmManager.getAlgorithmByName(computationConfig.getAlgorithm()).getCategory().name());
					logger.debug("---------> Resources needed :" + resources);

					if (!ComputationFactory.getFactoryResource().getLocalComputation())
						throw new SMResourcesNotAvailableException("Local Resources not available");

					logger.debug("Set number of resources ");
					algoConfig.setNumberOfResources(resources);

					break;

				}
				}
				agent.setConfiguration(algoConfig);

				startComputation(computationId, agent.getInfrastructure(),agent,ur);
				logger.debug("Computation started in "+agent.getInfrastructure());
			}catch(SMResourcesNotAvailableException e){
				throw e;
			}catch(Exception e){
				logger.error("Failed computation start",e);
				cleanResourcesComputational(agent.getInfrastructure(), computationId);
				throw new SMComputationalAgentInitializationException("Computation initialization failed, no agent class started : "+e.getMessage());
			}

		}

		

	}

}
