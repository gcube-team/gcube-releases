package org.gcube.portlets.admin.wfroleseditor.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import java.util.ArrayList;


import org.gcube.portlets.admin.wfdocslibrary.shared.WfRole;
import org.gcube.portlets.admin.wfdocslibrary.shared.WfRoleDetails;


@RemoteServiceRelativePath("WfRolesService")
public interface WfRolesService extends RemoteService {
	
  WfRole addRole(WfRole wfRole);
  Boolean deleteRole(String id); 
  ArrayList<WfRoleDetails> deleteRoles(ArrayList<String> ids);
  ArrayList<WfRoleDetails> getRoleDetails();
  WfRole getRole(String id);
  WfRole updateRole(WfRole wfRole);
}
