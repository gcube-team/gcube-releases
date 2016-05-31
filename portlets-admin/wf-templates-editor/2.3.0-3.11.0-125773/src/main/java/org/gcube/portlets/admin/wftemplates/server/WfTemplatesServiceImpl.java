package org.gcube.portlets.admin.wftemplates.server;

import java.util.ArrayList;
import java.util.Date;

import javax.servlet.http.HttpSession;

import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;
import org.gcube.portlets.admin.wfdocslibrary.server.db.MyDerbyStore;
import org.gcube.portlets.admin.wfdocslibrary.server.db.Store;
import org.gcube.portlets.admin.wfdocslibrary.shared.WfGraph;
import org.gcube.portlets.admin.wfdocslibrary.shared.WfGraphDetails;
import org.gcube.portlets.admin.wfdocslibrary.shared.WfRole;
import org.gcube.portlets.admin.wfdocslibrary.shared.WfRoleDetails;
import org.gcube.portlets.admin.wfdocslibrary.shared.WfTemplate;
import org.gcube.portlets.admin.wftemplates.client.WfTemplatesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
/**
 * <code> WfTemplatesServiceImpl </code>  is the server side implementation of the RPC service.
 *
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 * @version May 2011 (0.1) 
 */
@SuppressWarnings("serial")
public class WfTemplatesServiceImpl extends RemoteServiceServlet implements	WfTemplatesService {
	private static final String HARD_CODED_VO_NAME = "/gcube/devsec";
	private static final Logger log = LoggerFactory.getLogger(WfTemplatesServiceImpl.class);
	/**
	 * the WF DB Store
	 */
	private Store store;
	/**
	 * object serializer
	 */
	private XStream xstream;
	/**
	 * init method
	 */
	public void init() {
		store = new MyDerbyStore();
		xstream = new XStream(new DomDriver());
	}
	
	/**
	 * the ASL Session to get the context
	 * @return
	 */
	private ASLSession getASLSession() {
		HttpSession session = this.getThreadLocalRequest().getSession();
		String username = (String) session.getAttribute(ScopeHelper.USERNAME_ATTRIBUTE);
		if (username == null) {
			username = "test.user";
			SessionManager.getInstance().getASLSession(session.getId(), username).setScope(HARD_CODED_VO_NAME);
			SessionManager.getInstance().getASLSession(session.getId(), username).setUserFullName("Test User FullName");
		}		
		return SessionManager.getInstance().getASLSession(session.getId(), username);
	}
	/**
	 * saveTemplate into db as xml
	 */
	@Override
	public Boolean saveTemplate(String wfName, WfGraph toSave) {
		log.info("Attempting to Save Template ..." + wfName);
		String wfXML = xstream.toXML(toSave);
		log.debug("Serialized Workflow ...\n" + wfXML);
		System.out.println("Serialized Workflow ...\n" + wfXML);
		log.info("Saving template into DB ...");
		boolean result =  store.addWorkflowTemplate(wfName,  getASLSession().getUserFullName(), wfXML);
		log.info("Saving into DB SUCCESSFUL, returning " +  result);		
		return result;
	}
	/**
	 * @return the list of wftemplates in the database with the payload
	 */
	public ArrayList<WfTemplate> getTemplates() {
		ArrayList<WfTemplate> templates = new ArrayList<WfTemplate>();
		for (WfGraphDetails g : store.getAllWorkflowTemplates()) {
			WfGraph graph = (WfGraph) xstream.fromXML(g.getGraph());
			Date date = g.getDateCreated();
			templates.add(new WfTemplate(g.getId(), g.getName(), g.getAuthor(), date, graph)); 
		}		
		return new ArrayList<WfTemplate>(templates);
	}
	/**
	 * @return all the wfroles present in the db
	 */
	@Override
	public ArrayList<WfRoleDetails> getRoleDetails() {
		log.debug("Getting Workflow Roles from DB");
		ArrayList<WfRoleDetails> toReturn = new ArrayList<WfRoleDetails>();
		for (WfRole r : store.getAllRoles()) {
			toReturn.add(new WfRoleDetails(r.getRoleid(), r.getRolename()));
		} 
		return toReturn;
	}

	@Override
	public Boolean deleteTemplate(WfTemplate toDelete) {
		return store.deleteWfTemplate(toDelete.getTemplateid());
	}
}
