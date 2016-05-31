package org.gcube.portlets.admin.wfroleseditor.client;

import java.util.ArrayList;

import org.gcube.portlets.admin.wfdocslibrary.shared.WfRole;
import org.gcube.portlets.admin.wfdocslibrary.shared.WfRoleDetails;

import com.google.gwt.user.client.rpc.AsyncCallback;


public interface WfRolesServiceAsync {

  public void addRole(WfRole wfRole, AsyncCallback<WfRole> callback);
  public void deleteRole(String id, AsyncCallback<Boolean> callback);
  public void deleteRoles(ArrayList<String> ids, AsyncCallback<ArrayList<WfRoleDetails>> callback);
  public void getRoleDetails(AsyncCallback<ArrayList<WfRoleDetails>> callback);
  public void getRole(String id, AsyncCallback<WfRole> callback);
  public void updateRole(WfRole wfRole, AsyncCallback<WfRole> callback);
}

