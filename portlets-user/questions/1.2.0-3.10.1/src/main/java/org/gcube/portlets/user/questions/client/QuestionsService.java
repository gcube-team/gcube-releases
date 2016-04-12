package org.gcube.portlets.user.questions.client;

import java.util.ArrayList;

import org.gcube.portal.databook.shared.UserInfo;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("greet")
public interface QuestionsService extends RemoteService {
  ArrayList<UserInfo> getManagers();
}
