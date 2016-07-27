package org.gcube.resources.federation.fhnmanager.cl.fwsimpl;


import org.gcube.common.scope.api.ScopeProvider;


import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;


/**
 * Created by ggiammat on 7/13/16.
 */

@Provider
public class ScopeTokenSetFilter implements ClientRequestFilter {

  @Override
  public void filter(ClientRequestContext requestContext) throws IOException {
    requestContext.getHeaders().putSingle("gcube-scope", ScopeProvider.instance.get());
  }
}
