package org.gcube.portlets.user.tokengenerator.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("tokenservice")
public interface TokenService extends RemoteService {
  String getServiceToken();
}
