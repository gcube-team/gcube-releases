package org.gcube.portlets.admin.wfdocslibrary.server.db;


import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.admin.wfdocslibrary.shared.ActionChange;
import org.gcube.portlets.admin.wfdocslibrary.shared.LogAction;
import org.gcube.portlets.admin.wfdocslibrary.shared.UserComment;
import org.gcube.portlets.admin.wfdocslibrary.shared.WfGraphDetails;
import org.gcube.portlets.admin.wfdocslibrary.shared.WfRole;

/**
 * <code> Store </code> class is the interface of the store
 *
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 * @version June 2011 (0.2) 
 */
public interface Store {
	//WF ROLES
	WfRole add(WfRole wfRole);
	WfRole updateRole(WfRole wfRole);
	Boolean deleteRole(String roleid);
	ArrayList<WfRole> deleteRoles(List<String> ids);
	ArrayList<WfRole> getAllRoles();
	WfRole getRole(String id);
	
	//WF TEMPLATES
	Boolean addWorkflowTemplate(String wfName, String wfAuthor, String wfXML);
	ArrayList<WfGraphDetails> getAllWorkflowTemplates();
	ArrayList<WfGraphDetails> getAllWorkflows();
	Boolean deleteWfTemplate(String wfTemplateid);
	WfGraphDetails getWfTemplateById(String id);
	
	//WF REPORT
	String addWorkflowReport(String wfReportid, String wfReportName, String status, String wfAuthor, String wfXML) ;
	WfGraphDetails getWorkflowById(String id);
	ArrayList<UserComment> getCommentsByWorkflowId(String workflowid);
	Boolean addWorkflowComment(String workflowid, String author, String comment);
	ArrayList<LogAction> getLogActionsByWorkflowId(String workflowid);
	Boolean addWorkflowLogAction(String workflowid, String author, String actiontype);
	ArrayList<ActionChange> getChangesByActionId(String actionid);
	Boolean addWorkflowActionChange(String actionid, String author, String sectionChangeType, int sectionId, String componentType, int componentId, String previousContent);
	
	
	
	Boolean deleteWorkflowReport(String wfReportid);
	/**
	 * updates just the xml representing the workflow
	 * @param workflowid
	 * @param wfXML
	 * @return
	 */
	Boolean updateWorkflowGraph(String workflowid, String wfXML);
	/**
	 * updates the workflow status and the xml representing the workflow
	 * @param workflowid
	 * @param wfXML
	 * @return
	 */
	Boolean updateWorkflowStatusAndGraph(String workflowid, String newStatus, String wfXML);

}
