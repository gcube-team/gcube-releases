package org.gcube.portlets.user.rstudio_wrapper_portlet.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("greet")
public interface RStudioService extends RemoteService {
  String retrieveRStudioSecureURL() throws IllegalArgumentException;
}
