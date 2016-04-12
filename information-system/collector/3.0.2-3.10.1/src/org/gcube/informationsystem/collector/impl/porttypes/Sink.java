package org.gcube.informationsystem.collector.impl.porttypes;

import java.util.Calendar;

import org.globus.wsrf.ResourceLifetime;
import org.globus.wsrf.ResourceProperties;
import org.globus.wsrf.ResourceProperty;
import org.globus.wsrf.ResourcePropertySet;
import org.globus.wsrf.impl.ReflectionResourceProperty;
import org.globus.wsrf.impl.SimpleResourcePropertyMetaData;
import org.globus.wsrf.impl.SimpleResourcePropertySet;

import org.gcube.common.core.contexts.GCUBEServiceContext;
import org.gcube.common.core.porttypes.GCUBEPortType;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.informationsystem.collector.impl.contexts.ICServiceContext;
import org.gcube.informationsystem.collector.impl.state.AggregatorRegisteredResource;

/**
 * <em>Sink</em> PortType's implementation class. It's the registration PortType
 * for all the Aggregator Sources.
 * 
 * @author Manuele Simi (ISTI-CNR)
 * 
 */
public class Sink extends GCUBEPortType implements ResourceLifetime, ResourceProperties {

    // WS-Lifetime Properties
    protected Calendar terminationTime, currentTime;

    private final GCUBELog logger = new GCUBELog(Sink.class);

    private ResourcePropertySet propSet;

    /** {@inheritDoc} */
    @Override
    protected GCUBEServiceContext getServiceContext() {
	return ICServiceContext.getContext();
    }
    
    /**
     * Initializes a new Sink PortType by creating its
     * {@link SimpleResourcePropertySet}
     * 
     */
    public Sink() {
	this.propSet = new SimpleResourcePropertySet(AggregatorRegisteredResource.RP_SET);
	ResourceProperty prop = null;
	try {
	    // ResourceLifeTime properties
	    prop = new ReflectionResourceProperty(SimpleResourcePropertyMetaData.TERMINATION_TIME, this);
	    this.propSet.add(prop);
	    prop = new ReflectionResourceProperty( SimpleResourcePropertyMetaData.CURRENT_TIME, this);
	    this.propSet.add(prop);
	    this.propSet.add(prop);
	} catch (Exception e) {
	    logger.error("An error occurred during the Sink resource creation", e);
	}
    }

    /**
     * 
     * {@inheritDoc}
     */
    public Calendar getCurrentTime() {
	return Calendar.getInstance();
    }

    /**
     * 
     * {@inheritDoc}
     */
    public Calendar getTerminationTime() {
	return this.terminationTime;
    }

    /**
     * 
     * {@inheritDoc}
     */
    public void setTerminationTime(Calendar time) {
	this.terminationTime = time;

    }

    /**
     * 
     * {@inheritDoc}
     */
    public ResourcePropertySet getResourcePropertySet() {
	return this.propSet;
    }

}
