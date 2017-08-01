package org.gcube.portlets.widgets.sessionchecker.client;

import org.gcube.portlets.widgets.sessionchecker.shared.SessionInfoBean;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("checksession")
public interface SessionCheckerService extends RemoteService {
  SessionInfoBean checkSession();
}
