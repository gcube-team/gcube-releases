package org.gcube.common.clients.gcore.builders;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.gcube.common.clients.builders.SingletonBuilderAPI.Builder;

/**
 * A {@link Builder} for singleton gCore services.
 * @author Fabio Simeoni
 *
 * @param <P> the type of service proxies
 */
public interface SingletonBuilder<P> extends Builder<EndpointReferenceType,P> {}
