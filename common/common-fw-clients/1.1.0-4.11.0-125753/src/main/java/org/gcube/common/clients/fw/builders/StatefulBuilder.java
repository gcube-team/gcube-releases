package org.gcube.common.clients.fw.builders;

import javax.xml.ws.EndpointReference;

import org.gcube.common.clients.builders.StatefulBuilderAPI.Builder;

/**
 * A {@link Builder} for stateful gCore services.
 * 
 * @author Fabio Simeoni
 *
 * @param <P> the type of service proxies
 */
public interface StatefulBuilder<P> extends Builder<EndpointReference,P> {}
