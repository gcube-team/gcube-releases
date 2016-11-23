package org.gcube.data.analysis.statisticalmanager.experimentspace.computation;

import java.rmi.RemoteException;
import java.util.Map;

import javax.xml.namespace.QName;

import org.gcube.common.core.faults.GCUBEFault;
import org.gcube.common.core.porttypes.GCUBEPortType;
import org.gcube.common.core.types.VOID;
import org.gcube.contentmanagement.graphtools.utils.MathFunctions;
import org.gcube.data.analysis.statisticalmanager.Configuration;
import org.gcube.data.analysis.statisticalmanager.SMOperationStatus;
import org.gcube.data.analysis.statisticalmanager.ServiceContext;
import org.gcube.data.analysis.statisticalmanager.exception.StatisticalManagerException;
import org.gcube.data.analysis.statisticalmanager.persistence.SMPersistenceManager;
import org.gcube.data.analysis.statisticalmanager.stubs.ComputationPortType;
import org.gcube.dataanalysis.ecoengine.interfaces.ComputationalAgent;
import org.gcube_system.namespaces.data.analysis.statisticalmanager.types.SMAbstractResource;
import org.gcube_system.namespaces.data.analysis.statisticalmanager.types.SMOperation;
import org.gcube_system.namespaces.data.analysis.statisticalmanager.types.SMOperationInfo;
import org.globus.wsrf.ResourceException;
import org.oasis.wsrf.lifetime.Destroy;
import org.oasis.wsrf.lifetime.DestroyResponse;
import org.oasis.wsrf.lifetime.ResourceNotDestroyedFaultType;
import org.oasis.wsrf.lifetime.SetTerminationTime;
import org.oasis.wsrf.lifetime.SetTerminationTimeResponse;
import org.oasis.wsrf.lifetime.TerminationTimeChangeRejectedFaultType;
import org.oasis.wsrf.lifetime.UnableToSetTerminationTimeFaultType;
import org.oasis.wsrf.properties.GetMultipleResourcePropertiesResponse;
import org.oasis.wsrf.properties.GetMultipleResourceProperties_Element;
import org.oasis.wsrf.properties.GetResourcePropertyResponse;
import org.oasis.wsrf.properties.InvalidQueryExpressionFaultType;
import org.oasis.wsrf.properties.InvalidResourcePropertyQNameFaultType;
import org.oasis.wsrf.properties.InvalidSetResourcePropertiesRequestContentFaultType;
import org.oasis.wsrf.properties.QueryEvaluationErrorFaultType;
import org.oasis.wsrf.properties.QueryResourcePropertiesResponse;
import org.oasis.wsrf.properties.QueryResourceProperties_Element;
import org.oasis.wsrf.properties.ResourceUnknownFaultType;
import org.oasis.wsrf.properties.SetResourcePropertiesResponse;
import org.oasis.wsrf.properties.SetResourceProperties_Element;
import org.oasis.wsrf.properties.SetResourcePropertyRequestFailedFaultType;
import org.oasis.wsrf.properties.UnableToModifyResourcePropertyFaultType;
import org.oasis.wsrf.properties.UnknownQueryExpressionDialectFaultType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Computation extends GCUBEPortType implements ComputationPortType {


	private static Logger logger = LoggerFactory.getLogger(Computation.class);



	/** {@inheritDoc} */	
	@Override
	protected ServiceContext getServiceContext() {return ServiceContext.getContext();}

	private ComputationResource getResource() throws ResourceException {

		return (ComputationResource) 
				ComputationContext.getContext().getWSHome().find();
	}

	@Override
	public SMOperationInfo getComputationInfo(String computationId)
			throws RemoteException, GCUBEFault {

		SMOperation operation = null;
		try {
			operation = SMPersistenceManager.getOperation(Long.valueOf(computationId));
		} catch (Exception e) {
			logger.error("Unexpected Error ",e);
			throw ServiceContext.getContext().getDefaultException(e).toFault();
		}

		switch (SMOperationStatus.values()[operation.getOperationStatus()]) {
		case PENDING: {
			logger.debug("Computation status pending " + computationId);
			return new SMOperationInfo(String.valueOf(0),
					operation.getOperationStatus());
		}	
		case RUNNING: {

			logger.debug("computation status running " + computationId);
			Map<String, ComputationalAgent> agents = getResource().getComputationalAgents();	
			ComputationalAgent agent = agents.get(computationId);	
			if (agent == null) {
				logger.debug("ComputationalAgent not found wsresource not found"); 
				return new SMOperationInfo(String.valueOf(0),
						operation.getOperationStatus());
			}	else {
				logger.debug("ComputationalAgent Found ");
				return new SMOperationInfo(String.valueOf(MathFunctions.roundDecimal(agent.getStatus(), 2)),
						operation.getOperationStatus());
			}

		}
		case STOPPED: {

			logger.debug("computation status stopped " + computationId);
			return new SMOperationInfo(String.valueOf(0),
					operation.getOperationStatus());
		}

		case COMPLETED: {

			logger.debug("computation status completed " + computationId);
			return new SMOperationInfo(String.valueOf(100),
					operation.getOperationStatus());
		}	
		case FAILED: {

			logger.debug(" computation failed");
			return new SMOperationInfo(String.valueOf(100),
					operation.getOperationStatus());

		}
		default:
			throw new StatisticalManagerException("Computation status not found").asGCUBEFault();
		}
	}

	@Override
	public SMAbstractResource getOutput(String requestComputationOutput)
			throws RemoteException, GCUBEFault {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public VOID remove(String computationId) throws RemoteException,
	GCUBEFault {
		try{
			logger.info("Removing computation ID "+computationId);
			Map<String, ComputationalAgent> agents = getResource().getComputationalAgents();	
			ComputationalAgent agent = agents.get(computationId);	

			if (agent != null) {
				logger.info("******COMPUTATIONAL AGENT (Computation ID : "+computationId+") NOT NULL, CALL SHUT DOWN");
				agent.shutdown();
			}

			logger.debug("******Completed shutdown of Computation ID :"+computationId+", gonna remove it");
			SMPersistenceManager.removeComputation(Long.parseLong(computationId),
					Boolean.parseBoolean(Configuration.getProperty(Configuration.FORCE_COMPUTATION_REMOVAL)));		
			return new VOID();
		}catch(Exception e){
			logger.error("Unexpected Error ",e);
			throw new StatisticalManagerException(e).asGCUBEFault();
		}
	}

	@Override
	public VOID stop(String requestRemoveComputation) throws RemoteException,
	GCUBEFault {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GetMultipleResourcePropertiesResponse getMultipleResourceProperties(
			GetMultipleResourceProperties_Element getMultipleResourcePropertiesRequest)
					throws RemoteException, InvalidResourcePropertyQNameFaultType,
					ResourceUnknownFaultType {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GetResourcePropertyResponse getResourceProperty(
			QName getResourcePropertyRequest) throws RemoteException,
			InvalidResourcePropertyQNameFaultType, ResourceUnknownFaultType {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DestroyResponse destroy(Destroy destroyRequest)
			throws RemoteException,
			org.oasis.wsrf.lifetime.ResourceUnknownFaultType,
			ResourceNotDestroyedFaultType {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public QueryResourcePropertiesResponse queryResourceProperties(
			QueryResourceProperties_Element queryResourcePropertiesRequest)
					throws RemoteException, UnknownQueryExpressionDialectFaultType,
					InvalidResourcePropertyQNameFaultType,
					InvalidQueryExpressionFaultType, QueryEvaluationErrorFaultType,
					ResourceUnknownFaultType {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SetResourcePropertiesResponse setResourceProperties(
			SetResourceProperties_Element setResourcePropertiesRequest)
					throws RemoteException, InvalidResourcePropertyQNameFaultType,
					InvalidSetResourcePropertiesRequestContentFaultType,
					SetResourcePropertyRequestFailedFaultType,
					ResourceUnknownFaultType, UnableToModifyResourcePropertyFaultType {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SetTerminationTimeResponse setTerminationTime(
			SetTerminationTime setTerminationTimeRequest)
					throws RemoteException, UnableToSetTerminationTimeFaultType,
					org.oasis.wsrf.lifetime.ResourceUnknownFaultType,
					TerminationTimeChangeRejectedFaultType {
		// TODO Auto-generated method stub
		return null;
	}



}
