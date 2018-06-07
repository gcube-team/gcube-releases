package org.gcube.common.clients.gcore.builders;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.gcube.common.clients.builders.StatefulBuilderAPI.Builder;

/**
 * A {@link Builder} for stateful gCore services.
 * @author Fabio Simeoni
 *
 * @param <P> the type of service proxies
 */
public interface StatefulBuilder<P> extends Builder<EndpointReferenceType,P> {}
