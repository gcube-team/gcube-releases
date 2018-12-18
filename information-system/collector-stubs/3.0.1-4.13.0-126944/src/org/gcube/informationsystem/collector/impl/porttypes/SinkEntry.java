
package org.gcube.informationsystem.collector.impl.porttypes;

import java.rmi.RemoteException;
import java.util.Calendar;

import javax.xml.namespace.QName;
import org.oasis.wsrf.properties.ResourceUnknownFaultType;
import org.oasis.wsrf.properties.InvalidResourcePropertyQNameFaultType;
import org.oasis.wsrf.properties.GetResourcePropertyResponse;
import org.oasis.wsrf.servicegroup.ServiceGroupEntry;
import org.globus.wsrf.impl.properties.GetResourcePropertyProvider;

import org.gcube.common.core.contexts.GCUBEServiceContext;
import org.gcube.common.core.porttypes.GCUBEPortType;
import org.gcube.informationsystem.collector.impl.contexts.ICServiceContext;


/**
 * A ServiceGroupEntry implemenation.<br> 
 * It is used as base implementation both of the <em>SinkEntry</em> and of <em>Entry</em> portType.
 *
 * @author Manuele Simi (ISTI-CNR)
 *
 */

public class SinkEntry extends GCUBEPortType implements ServiceGroupEntry {
  
    private GetResourcePropertyProvider getResourcePropertyProvider =  new GetResourcePropertyProvider();
    
    
    // WS-Lifetime Properties 
    protected Calendar terminationTime, currentTime;
    
    /** {@inheritDoc} */
    @Override
    protected GCUBEServiceContext getServiceContext() {
	return ICServiceContext.getContext();
    }
    
    /**
     * Gets WS-Resource property by using the {@link GetResourcePropertyProvider}
     * 
     * @param name the full qualified name of the property
     * @return the resource property
     * @throws RemoteException throws by the GetResourcePropertyProvider
     * @throws InvalidResourcePropertyQNameFaultType throws by the GetResourcePropertyProvider
     * @throws ResourceUnknownFaultType throws by the GetResourcePropertyProvider
     * 
     */
    public GetResourcePropertyResponse getResourceProperty(QName name) 
        throws RemoteException, InvalidResourcePropertyQNameFaultType, ResourceUnknownFaultType {
	
        return getResourcePropertyProvider.getResourceProperty(name);
    }
	

}

