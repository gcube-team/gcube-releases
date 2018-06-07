package org.gcube.portlets.user.questions.client;

import java.util.ArrayList;

import org.gcube.portlets.user.questions.shared.GroupDTO;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("getgroups")
public interface QuestionsService extends RemoteService {
  ArrayList<GroupDTO> getGroups();
 

}
