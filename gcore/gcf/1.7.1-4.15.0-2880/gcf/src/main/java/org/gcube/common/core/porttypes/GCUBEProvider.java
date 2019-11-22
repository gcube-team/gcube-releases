 package org.gcube.common.core.porttypes;

import java.rmi.RemoteException;

import javax.xml.namespace.QName;

import org.gcube.common.core.utils.logging.GCUBELog;
import org.globus.wsrf.impl.lifetime.DestroyProvider;
import org.globus.wsrf.impl.lifetime.SetTerminationTimeProvider;
import org.globus.wsrf.impl.properties.GetMultipleResourcePropertiesProvider;
import org.globus.wsrf.impl.properties.GetResourcePropertyProvider;
import org.globus.wsrf.impl.properties.QueryResourcePropertiesProvider;
import org.globus.wsrf.impl.properties.SetResourcePropertiesProvider;
import org.oasis.wsrf.lifetime.Destroy;
import org.oasis.wsrf.lifetime.DestroyResponse;
import org.oasis.wsrf.lifetime.ResourceNotDestroyedFaultType;
import org.oasis.wsrf.lifetime.ResourceUnknownFaultType;
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
import org.oasis.wsrf.properties.SetResourcePropertiesResponse;
import org.oasis.wsrf.properties.SetResourceProperties_Element;
import org.oasis.wsrf.properties.SetResourcePropertyRequestFailedFaultType;
import org.oasis.wsrf.properties.UnableToModifyResourcePropertyFaultType;
import org.oasis.wsrf.properties.UnknownQueryExpressionDialectFaultType;


/**
 * An operation provider which wraps the GLOBUS providers which are default for gCube services
 * (destroy, termination time, get/set/query resource properties). 
 * 
 * @author Andrea Manzi (CNR), Fabio Simeoni (University of Strathclyde)
 *
 */
public class GCUBEProvider {

	/**
	 * Class logger.
	 */
	static final GCUBELog logger = new GCUBELog(GCUBEProvider.class);
	
	/**
	 * Wrapped GLOBUS operation provider.
	 */
	protected DestroyProvider destroyProvider = new DestroyProvider();
	/**
	 * Wrapped GLOBUS operation provider.
	 */
	protected SetTerminationTimeProvider terminationTimeProvider = new SetTerminationTimeProvider();
	/**
	 * Wrapped GLOBUS operation provider.
	 */
	protected GetResourcePropertyProvider getRPProvider = new GetResourcePropertyProvider();
	/**
	 * Wrapped GLOBUS operation provider.
	 */
	protected GetMultipleResourcePropertiesProvider getMultipleRPProvider = new GetMultipleResourcePropertiesProvider();
	/**
	 * Wrapped GLOBUS operation provider.
	 */
	protected SetResourcePropertiesProvider setRPProvider = new SetResourcePropertiesProvider();
	/**
	 * Wrapped GLOBUS operation provider.
	 */
	protected QueryResourcePropertiesProvider queryRPProvider = new QueryResourcePropertiesProvider();
		
	
	/**
	 * Dispatches the invocation to the corresponding GLOBUS provider.
	 * @param arg0 the provider's input,as per WSRF specifications.
	 * @return the provider's output,as per WSRF specifications.
	 * @throws RemoteException
	 * @throws ResourceNotDestroyedFaultType
	 * @throws ResourceUnknownFaultType
	 */
	public DestroyResponse destroy(Destroy arg0) throws RemoteException,
			ResourceNotDestroyedFaultType, ResourceUnknownFaultType {
		return destroyProvider.destroy(arg0);
	}

	/**
	 * Dispatches the invocation to the corresponding GLOBUS provider.
	 * @param arg0 the provider's input,as per WSRF specifications.
	 * @return the provider's output,as per WSRF specifications.
	 * @throws RemoteException
	 * @throws ResourceNotDestroyedFaultType
	 * @throws ResourceUnknownFaultType
	 */
	public GetMultipleResourcePropertiesResponse getMultipleResourceProperties(
			GetMultipleResourceProperties_Element arg0) throws RemoteException,
			InvalidResourcePropertyQNameFaultType,
			org.oasis.wsrf.properties.ResourceUnknownFaultType {
		return getMultipleRPProvider.getMultipleResourceProperties(arg0);
	}

	/**
	 * Dispatches the invocation to the corresponding GLOBUS provider.
	 * @param arg0 the provider's input,as per WSRF specifications.
	 * @return the provider's output,as per WSRF specifications.
	 * @throws RemoteException
	 * @throws ResourceNotDestroyedFaultType
	 * @throws ResourceUnknownFaultType
	 */
	public GetResourcePropertyResponse getResourceProperty(QName arg0)
			throws RemoteException, InvalidResourcePropertyQNameFaultType,
			org.oasis.wsrf.properties.ResourceUnknownFaultType {
		return getRPProvider.getResourceProperty(arg0);
	}

	/**
	 * Dispatches the invocation to the corresponding GLOBUS provider.
	 * @param arg0 the provider's input,as per WSRF specifications.
	 * @return the provider's output,as per WSRF specifications.
	 * @throws RemoteException
	 * @throws ResourceNotDestroyedFaultType
	 * @throws ResourceUnknownFaultType
	 */
	public QueryResourcePropertiesResponse queryResourceProperties(
			QueryResourceProperties_Element arg0) throws RemoteException,
			InvalidResourcePropertyQNameFaultType,
			org.oasis.wsrf.properties.ResourceUnknownFaultType,
			InvalidQueryExpressionFaultType, QueryEvaluationErrorFaultType,
			UnknownQueryExpressionDialectFaultType {
		return queryRPProvider.queryResourceProperties(arg0);
	}

	/**
	 * Dispatches the invocation to the corresponding GLOBUS provider.
	 * @param arg0 the provider's input,as per WSRF specifications.
	 * @return the provider's output,as per WSRF specifications.
	 * @throws RemoteException
	 * @throws ResourceNotDestroyedFaultType
	 * @throws ResourceUnknownFaultType
	 */
	public SetResourcePropertiesResponse setResourceProperties(
			SetResourceProperties_Element arg0) throws RemoteException,
			SetResourcePropertyRequestFailedFaultType,
			InvalidResourcePropertyQNameFaultType,
			UnableToModifyResourcePropertyFaultType,
			org.oasis.wsrf.properties.ResourceUnknownFaultType,
			InvalidSetResourcePropertiesRequestContentFaultType {
		return setRPProvider.setResourceProperties(arg0);
	}

	/**
	 * Dispatches the invocation to the corresponding GLOBUS provider.
	 * @param arg0 the provider's input,as per WSRF specifications.
	 * @return the provider's output,as per WSRF specifications.
	 * @throws RemoteException
	 * @throws ResourceNotDestroyedFaultType
	 * @throws ResourceUnknownFaultType
	 */
	public SetTerminationTimeResponse setTerminationTime(SetTerminationTime arg0)
			throws RemoteException, UnableToSetTerminationTimeFaultType,
			ResourceUnknownFaultType, TerminationTimeChangeRejectedFaultType {
		return terminationTimeProvider.setTerminationTime(arg0);
	}
}
