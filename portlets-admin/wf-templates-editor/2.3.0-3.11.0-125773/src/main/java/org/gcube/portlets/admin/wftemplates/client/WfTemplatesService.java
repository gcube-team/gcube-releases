package org.gcube.portlets.admin.wftemplates.client;


import java.util.ArrayList;

import org.gcube.portlets.admin.wfdocslibrary.shared.WfGraph;
import org.gcube.portlets.admin.wfdocslibrary.shared.WfRoleDetails;
import org.gcube.portlets.admin.wfdocslibrary.shared.WfTemplate;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("wftemplates")
public interface WfTemplatesService extends RemoteService {

	ArrayList<WfTemplate> getTemplates();

	ArrayList<WfRoleDetails> getRoleDetails();

	Boolean saveTemplate(String wfName, WfGraph toSave);
	
	Boolean deleteTemplate(WfTemplate toDelete);
}
