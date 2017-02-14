package org.gcube.data.analysis.statisticalmanager.experimentspace;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.gcube.common.core.contexts.GCUBEServiceContext.Status;
import org.gcube.common.core.faults.GCUBEFault;
import org.gcube.common.core.porttypes.GCUBEPortType;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.types.VOID;
import org.gcube.data.access.queueManager.model.RequestItem;
import org.gcube.data.analysis.statisticalmanager.Configuration;
import org.gcube.data.analysis.statisticalmanager.SMOperationStatus;
import org.gcube.data.analysis.statisticalmanager.ServiceContext;
import org.gcube.data.analysis.statisticalmanager.exception.StatisticalManagerException;
import org.gcube.data.analysis.statisticalmanager.persistence.HibernateManager;
import org.gcube.data.analysis.statisticalmanager.persistence.SMPersistenceManager;
import org.gcube.data.analysis.statisticalmanager.persistence.ServiceQueueManager;
import org.gcube.data.analysis.statisticalmanager.persistence.algorithms.AlgorithmCategoryDescriptor;
import org.gcube.data.analysis.statisticalmanager.persistence.algorithms.AlgorithmDescriptor;
import org.gcube.data.analysis.statisticalmanager.persistence.algorithms.AlgorithmManager;
import org.gcube.data.analysis.statisticalmanager.stubs.ComputationFactoryPortType;
import org.gcube.data.analysis.statisticalmanager.stubs.SMAlgorithmsRequest;
import org.gcube.data.analysis.statisticalmanager.stubs.SMComputationConfig;
import org.gcube.data.analysis.statisticalmanager.stubs.SMComputationRequest;
import org.gcube.data.analysis.statisticalmanager.stubs.SMComputations;
import org.gcube.data.analysis.statisticalmanager.stubs.SMComputationsRequest;
import org.gcube.data.analysis.statisticalmanager.stubs.SMListGroupedAlgorithms;
import org.gcube.data.analysis.statisticalmanager.stubs.SMOutput;
import org.gcube.data.analysis.statisticalmanager.stubs.SMParameter;
import org.gcube.data.analysis.statisticalmanager.stubs.SMParameters;
import org.gcube.data.analysis.statisticalmanager.util.ScopeUtils;
import org.gcube.data.analysis.statisticalmanager.util.ScopeUtils.ScopeBean;
import org.gcube.dataanalysis.ecoengine.datatypes.OutputTable;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveType;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.PrimitiveTypes;
import org.gcube_system.namespaces.data.analysis.statisticalmanager.types.SMComputation;
import org.gcube_system.namespaces.data.analysis.statisticalmanager.types.SMEntries;
import org.gcube_system.namespaces.data.analysis.statisticalmanager.types.SMEntry;
import org.gcube_system.namespaces.data.analysis.statisticalmanager.types.SMInputEntry;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ComputationFactory extends GCUBEPortType implements ComputationFactoryPortType {

	private static Logger logger = LoggerFactory.getLogger(ComputationFactory.class);

	private static final String keyFRString = "keyFactoryResource";
	private static ComputationFactoryResource resource;

	/** {@inheritDoc} */
	@Override
	protected ServiceContext getServiceContext() {
		return ServiceContext.getContext();
	}

	public static ComputationFactoryResource getFactoryResource() {
		return resource;
	}

	@Override
	protected void onInitialisation() throws Exception {

		if (resource == null){ 

			logger.info("Initialising the factory state...");
			new Thread() {

				@Override
				public void run() {
					int attempts = 0;
					boolean created = false;
					while (attempts++ < 10) {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e1) {
							logger.error("Failed to sleep in between factory creation");
							ServiceContext.getContext().setStatus(Status.FAILED);
							break;
						}
						try {
							for (GCUBEScope scope : ServiceContext.getContext().getStartScopes()) {

								logger.debug("Creating the singleton factory resource within the scope "+ scope);

								ScopeUtils.setAuthorizationSettings(new ScopeBean(scope.toString(), null));
								resource = (ComputationFactoryResource) ComputationFactoryContext
										.getContext().getWSHome().create(ComputationFactoryContext
												.getContext().makeKey(keyFRString));
							}
							created = true;
							break;
						} catch (Exception e) {
							logger.error("Failed to create the resource", e);
						}
					}
					if (!created)
						ServiceContext.getContext().setStatus(Status.FAILED);
				}

			}.start();
		}
	}

	@Override
	public SMOutput getAlgorithmOutputs(String algorithm)throws RemoteException, GCUBEFault {
		StatisticalType outputs = null;
		try {
			logger.debug("get Output for "+algorithm);
			outputs = AlgorithmManager.getAlgorithmByName(algorithm).getOutput();
			if (outputs != null) {
				SMOutput output = new SMOutput();
				output.setDescription(outputs.getDescription());
				output.setName(outputs.getName());

				if (outputs instanceof OutputTable) {
					logger.debug("table");
					output.setSize(1);
					output.setType(output.getName());
				}

				if (outputs instanceof PrimitiveType) {
					PrimitiveType primitiveObject = (PrimitiveType) outputs;
					if (primitiveObject.getType() == PrimitiveTypes.MAP) {
						Map<String, StatisticalType> map = (Map<String, StatisticalType>) primitiveObject
								.getContent();
						logger.debug("map");

						output.setSize(map.entrySet().size());
						output.setType(PrimitiveTypes.MAP.name());
					} else if (primitiveObject.getType() == PrimitiveTypes.IMAGES) {
						output.setType(PrimitiveTypes.IMAGES.name());
						output.setSize(1);
						logger.debug("image");


					} else if (primitiveObject.getType() == PrimitiveTypes.FILE) {
						output.setSize(1);

						output.setType(PrimitiveTypes.FILE.name());
						logger.debug("file");

					} else {
						if (primitiveObject.getType() == PrimitiveTypes.STRING)
							output.setSize(1);
						logger.debug("string");

						output.setType(PrimitiveTypes.STRING.name());
					}
				}
				return output;

			}
		} catch (Exception e) {
			logger.debug("Parameters unknown",e);
			throw new StatisticalManagerException("Parameters unknown for this computation", e).asGCUBEFault();
		}

		return null;
	}

	@Override
	public SMParameters getAlgorithmParameters(String algorithm)throws RemoteException, GCUBEFault {

		Collection<StatisticalType> parameters = null;
		try {
			parameters = AlgorithmManager.getAlgorithmByName(algorithm).getParameters().values();
		} catch (Exception e) {
			throw new StatisticalManagerException("Unknown parameters",e).asGCUBEFault();			
		}

		logger.debug("------------- parameters retrieved");
		ArrayList<SMParameter> listParameters = new ArrayList<SMParameter>();
		for (StatisticalType param : parameters) {
			SMParameter smParameter = FactoryComputationParameter.createParameter(param);
			if (smParameter != null)	listParameters.add(smParameter);
		}

		return new SMParameters(listParameters.toArray(new SMParameter[listParameters.size()]));
	}

	@Override
	public SMListGroupedAlgorithms getAlgorithmsUser(SMAlgorithmsRequest request) throws RemoteException, GCUBEFault {

		logger.debug("Called get user perspective");

		try {

			Map<AlgorithmCategory, AlgorithmCategoryDescriptor> map = AlgorithmManager.getAvailableAlgorithms(request.getParameters());

			return AlgorithmManager.groupByUserPerspective(map);

		} catch (Exception e) {
			logger.error("Get features error ", e);
			throw ServiceContext.getContext().getDefaultException(e).toFault();
		}
	}

	@Override
	public SMListGroupedAlgorithms getAlgorithms(SMAlgorithmsRequest request)
			throws RemoteException, GCUBEFault {

		logger.debug("Called get all algorithm");

		try {
			Map<AlgorithmCategory, AlgorithmCategoryDescriptor> map = AlgorithmManager.getAvailableAlgorithms(request.getParameters());

			return AlgorithmManager.asGroup(map);
			// return ServiceUtil.getGroupedAlgorithm(request.getParameters(),
			// map);
		} catch (Exception e) {
			logger.error("Get features error ", e);
			throw ServiceContext.getContext().getDefaultException(e).toFault();
		}
	}

	@Override
	public SMComputations getComputations(SMComputationsRequest request)throws RemoteException, GCUBEFault {

		logger.trace("List computations request by user " + request.getUser());
		try {
			List<SMComputation> toReturn=null;

			if (request.getParameters() == null) 
				toReturn= SMPersistenceManager.getComputations(request.getUser(), null, null);

			else for(Entry<AlgorithmCategory,AlgorithmCategoryDescriptor> entry:AlgorithmManager.getAvailableAlgorithms(request.getParameters()).entrySet()){
				for(AlgorithmDescriptor algo:entry.getValue().getAlgorithms().values())
					toReturn.addAll(SMPersistenceManager.getComputations(request.getUser(), algo.getName(), algo.getCategory().name()));
			}

			return new SMComputations(toReturn.toArray(new SMComputation[toReturn.size()]));

		} catch (Exception e) {
			logger.error("Get Computations error ", e);
			throw ServiceContext.getContext().getDefaultException(e).toFault();
		}

	}

	@Override
	public String executeComputation(SMComputationRequest requestComputation)
			throws RemoteException, GCUBEFault {
		try{
			return executeComputation(requestComputation, null);
		}catch(Exception e){
			logger.error("Unable to execute",e);
			throw new StatisticalManagerException("Unable to execute", e).asGCUBEFault();
		}
	}

	private String executeComputation(SMComputationRequest requestComputation,String computationId) throws Exception {
		logger.debug("Execute computation : "+computationId+" under scope "+ScopeUtils.getCurrentScope());
		String category = AlgorithmManager.getAlgorithmByName(requestComputation.getConfig().getAlgorithm()).getCategory().name();

		if (computationId == null) {
			long id = SMPersistenceManager.addComputation(requestComputation, category);
			computationId = String.valueOf(id);
		} else {
			SMPersistenceManager.setOperationStatus(Long.parseLong(computationId), "", "",SMOperationStatus.PENDING);
		}

		Map<String, Serializable> parameters = new HashMap<String, Serializable>();
		parameters.put(Configuration.getProperty(Configuration.JMS_MESSAGE_REQUEST),requestComputation);
		parameters.put(Configuration.getProperty(Configuration.JMS_MESSAGE_COMPUTATION_ID),computationId);
		parameters.put(Configuration.getProperty(Configuration.JMS_MESSAGE_SCOPE),ScopeUtils.getCurrentScope());
		parameters.put(Configuration.getProperty(Configuration.JMS_MESSAGE_TOKEN),ScopeUtils.getToken());
		ServiceQueueManager.sendItem(new RequestItem("CallScript", null, parameters));

		return String.valueOf(computationId);

	}

	@Override
	public SMComputation getComputation(String computationId)
			throws RemoteException, GCUBEFault {
		HibernateManager mng=null;
		Session session = null;
		try {
			mng=HibernateManager.get();
			session=mng.getSessionFactory().openSession();
			Query query = session.createQuery("select computation from SMComputation computation "
					+ "where computation.operationId = :name");

			query.setParameter("name", Long.valueOf(computationId));

			@SuppressWarnings("unchecked")
			List<Object> objects = query.list();

			SMComputation computation = (SMComputation) objects.get(0);

			Query queryParameters = session.createQuery("select parameter from SMEntry parameter "
					+ "where parameter.computationId = :operationId");
			queryParameters.setParameter("operationId",	computation.getOperationId());

			@SuppressWarnings("unchecked")
			List<Object> parameters = queryParameters.list();
			computation.setParameters(parameters.toArray(new SMEntry[parameters.size()]));

			return (SMComputation) objects.get(0);
		}catch(StatisticalManagerException e){
			throw e.asGCUBEFault();
		}catch(Exception e){
			throw new StatisticalManagerException("Unexpected exception",e).asGCUBEFault();
		} finally {
			if(session!=null)mng.closeSession(session);
		}
	}

	@Override
	public VOID removeComputation(String computationId) throws RemoteException,GCUBEFault {

		logger.debug("Remove computation" + computationId + " from factory");
		try{
			SMPersistenceManager.removeComputation(Long.parseLong(computationId),
					Boolean.parseBoolean(Configuration.getProperty(Configuration.FORCE_COMPUTATION_REMOVAL)));
			return new VOID();
		}catch(StatisticalManagerException e ){
			throw e.asGCUBEFault();
		}catch(Exception e){
			throw new StatisticalManagerException("Unexpected Exception",e).asGCUBEFault();
		}
	}

	@Override
	public String resubmitComputation(String computationId)
			throws RemoteException, GCUBEFault {

		//		GCUBEScope scope = ServiceContext.getContext().getScope();
		//		if (scope.isInfrastructure())
		//			throw ServiceContext.getContext()
		//			.getDefaultException(new StatisticalManagerException())
		//			.toFault();

		try {
			SMComputation computation = (SMComputation) SMPersistenceManager.getOperation(Long.parseLong(computationId));
			List<SMEntry> parameters = new ArrayList<SMEntry>();

			HibernateManager mng=HibernateManager.get();
			Session session = mng.getSessionFactory().openSession();
			try {
				Query queryParameters = session.createQuery("select parameter from SMEntry parameter "
						+ "where parameter.computationId = :computationId");
				queryParameters.setParameter("computationId",computation.getOperationId());

				parameters.addAll(queryParameters.list());

			} finally {
				mng.closeSession(session);
			}

			List<SMInputEntry> entries = new ArrayList<SMInputEntry>();
			for (SMEntry parameter : parameters) 
				entries.add(new SMInputEntry(parameter.getKey(), parameter.getValue()));

			SMEntries inputEntries = new SMEntries(entries.toArray(new SMInputEntry[entries.size()]));
			SMComputationConfig computationConfig = new SMComputationConfig(computation.getAlgorithm(), inputEntries);
			SMComputationRequest request = new SMComputationRequest(computationConfig, null, null, computation.getPortalLogin());

			logger.debug("Re execute "+computationId+" under scope "+ScopeUtils.getCurrentScope());
			
			executeComputation(request,computationId);

			return computationId;

		} catch (Exception e) {
			throw ServiceContext.getContext().getDefaultException(e).toFault();
		}
	}



}
