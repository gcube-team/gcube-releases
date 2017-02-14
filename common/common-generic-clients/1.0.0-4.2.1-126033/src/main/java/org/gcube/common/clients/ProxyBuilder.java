package org.gcube.common.clients;

import org.gcube.common.clients.builders.StatelessBuilderAPI.Builder;

/**
 * Builds proxies for target endpoints.
 * 
 * @author Fabio Simeoni
 *
 * @param <P> the type of built proxies
 */
public interface ProxyBuilder<P> extends Builder<P> {}
