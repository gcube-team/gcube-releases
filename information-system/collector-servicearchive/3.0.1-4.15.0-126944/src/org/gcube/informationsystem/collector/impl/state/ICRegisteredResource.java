package org.gcube.informationsystem.collector.impl.state;

import org.globus.wsrf.ResourceLifetime;
import org.globus.wsrf.ResourceProperties;
import org.globus.wsrf.TopicListAccessor;

/**
 * Marker interface for IC registered resources
 *
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public interface ICRegisteredResource extends TopicListAccessor, ResourceLifetime, ResourceProperties {

}
