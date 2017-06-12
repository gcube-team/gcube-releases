package org.gcube.common.clients.fw.builders;

import javax.xml.ws.EndpointReference;

import org.gcube.common.clients.builders.SingletonBuilderAPI.Builder;

/**
 * A {@link Builder} for singleton gCore services.
 * 
 * 
 * @author Fabio Simeoni
 *
 * @param <P> the type of service proxies
 */
public interface SingletonBuilder<P> extends Builder<EndpointReference,P> {}
