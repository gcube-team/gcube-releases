package org.gcube.portlets.admin.wfroleseditor.server;

import java.util.ArrayList;

import org.gcube.portlets.admin.wfdocslibrary.server.db.MyDerbyStore;
import org.gcube.portlets.admin.wfdocslibrary.server.db.Store;
import org.gcube.portlets.admin.wfdocslibrary.shared.WfRole;
import org.gcube.portlets.admin.wfdocslibrary.shared.WfRoleDetails;
import org.gcube.portlets.admin.wfroleseditor.client.WfRolesService;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * <code> WfRolesServiceImpl </code> class is the service impl of the webapp
 *
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 * @version April 2011 (0.1) 
 */

@SuppressWarnings("serial")
public class WfRolesServiceImpl extends RemoteServiceServlet implements  WfRolesService {

	private Store store;

	public WfRolesServiceImpl() {
		initRoles();
	}

	private void initRoles() {
		store = new MyDerbyStore();
	}

	public WfRole addRole(WfRole wfRole) {
		return store.add(wfRole);
	}

	public WfRole updateRole(WfRole wfRole) {
		return  store.updateRole(wfRole);
	}

	public Boolean deleteRole(String id) {
		return store.deleteRole(id);
	}

	public ArrayList<WfRoleDetails> deleteRoles(ArrayList<String> ids) {
		ArrayList<WfRoleDetails> toReturn = new ArrayList<WfRoleDetails>();
		for (WfRole r : store.deleteRoles(ids)) {
			toReturn.add(new WfRoleDetails(r.getRoleid(), r.getRolename()));
		} 
		return toReturn;
	}

	public ArrayList<WfRoleDetails> getRoleDetails() {
		ArrayList<WfRoleDetails> toReturn = new ArrayList<WfRoleDetails>();
		for (WfRole r : store.getAllRoles()) {
			toReturn.add(new WfRoleDetails(r.getRoleid(), r.getRolename()));
		} 
		return toReturn;
	}

	public WfRole getRole(String id) {
		return  store.getRole(id);
	}
}
