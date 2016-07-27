/****************************************************************************
 *  This software is part of the gCube Project.
 *  Site: http://www.gcube-system.org/
 ****************************************************************************
 * The gCube/gCore software is licensed as Free Open Source software
 * conveying to the EUPL (http://ec.europa.eu/idabc/eupl).
 * The software and documentation is provided by its authors/distributors
 * "as is" and no expressed or
 * implied warranty is given for its use, quality or fitness for a
 * particular case.
 ****************************************************************************
 * Filename: Sweeper.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.resourcemanagement.support.server.sweeper;

import static org.gcube.resources.discovery.icclient.ICFactory.client;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.gcube.common.resources.gcore.HostingNode;
import org.gcube.common.resources.gcore.utils.XPathHelper;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.resourcemanagement.support.server.gcube.queries.QueryLoader;
import org.gcube.resourcemanagement.support.server.gcube.queries.QueryLocation;
import org.gcube.resourcemanagement.support.server.managers.resources.GHNManager;
import org.gcube.resourcemanagement.support.server.managers.resources.RunningInstanceManager;
import org.gcube.resourcemanagement.support.shared.util.SweeperActions;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.impl.QueryTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import com.extjs.gxt.ui.client.data.ModelData;

/**
 * @author Massimiliano Assante (ISTI-CNR)
 *
 */
public class Sweeper {
	//TODO: Make it configurable from a property file
	public static String LIVE_GHN_MAX_MINUTES = "40";

	private static final Logger _log = LoggerFactory.getLogger(Sweeper.class);


	protected ArrayList<String> applyQuery(ScopeBean queryScope, QueryLocation queryPath, QueryLocation returnPath, QueryParameter... params) throws Exception {
		//set the scope
		String currScope = ScopeProvider.instance.get();
		ScopeProvider.instance.set(queryScope.toString());

		QueryTemplate isQuery = new QueryTemplate(QueryLoader.getQuery(queryPath)); 
		DiscoveryClient<String> client = client();		 

		if (params != null && params.length > 0) {
			for (int i = 0; i < params.length; i++) {
				isQuery.addParameter(params[i].name, params[i].value);
			}
		}
		isQuery.addParameter("RESOURCE", QueryLoader.getQuery(returnPath));
		
		List<String> results = client.submit(isQuery);
		ArrayList<String> retval = new ArrayList<String>();

		for (String elem : results) {
			DocumentBuilder docBuilder =  DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Node node = docBuilder.parse(new InputSource(new StringReader(elem))).getDocumentElement();
			XPathHelper helper = new XPathHelper(node);
			// Removes the resources with no ID or empty
			try {
				if (helper.evaluate("//ID").get(0) != null && helper.evaluate("//ID").get(0).trim().length() > 0) {
					retval.add(elem);
					System.out.println(elem);
				} else {
					_log.debug("*** Found an invalid element with no ID");
				}
			} catch (Exception e) {
				_log.debug("[getResourcesByType] found a resource with empty ID");
			}
		}
		ScopeProvider.instance.set(currScope);
		return retval;
	}
	/**
	 * this method used to read the MAXWAIT param from the resourcemanagement.properties files, currently it reads it from the static var defined on top of this class
	 * TODO: Make it configurable again from a property file
	 * @param queryScope
	 * @return the expired ghn list
	 */
	public ArrayList<String> getExpiredGHNs(final ScopeBean queryScope) {
		try {
			return applyQuery(
					queryScope,
					QueryLocation.SWEEPER_EXPIRED_GHN, QueryLocation.RETURN_SWEEPER_EXPIRED_GHN,
					new QueryParameter("MAXWAIT", LIVE_GHN_MAX_MINUTES));
		} catch (Exception e) {
			_log.error(e.getMessage());
			return null;
		}
	}
	public ArrayList<String> getDeadGHNs(ScopeBean queryScope) {
		try {
			return applyQuery(
					queryScope,
					QueryLocation.SWEEPER_DEAD_GHN, QueryLocation.RETURN_SWEEPER_DEAD_GHN);
		} catch (Exception e) {
			_log.error(e.getMessage());
			return null;
		}
	}

	public ArrayList<String> getOrphanRI(ScopeBean queryScope) {
		try {
			return applyQuery(
					queryScope,
					QueryLocation.SWEEPER_ORPHAN_RI, QueryLocation.RETURN_SWEEPER_ORPHAN_RI);
		} catch (Exception e) {
			_log.error(e.getMessage());
			return null;
		}
	}

	@SuppressWarnings("deprecation")
	public Boolean applySweep(ScopeBean queryScope, List<ModelData> elems) {
		//set the scope
		String currScope = ScopeProvider.instance.get();
		ScopeProvider.instance.set(queryScope.toString());
		for (ModelData entry : elems) {
			try {
				System.out.println("Action->"+((Object) entry.get("Actions")).toString());
				SweeperActions action = SweeperActions.valueOf(((Object) entry.get("Actions")).toString());
				String resID = ((Object) entry.get("ID")).toString();

				_log.info("Cleaning up " + resID + " " + action);

				switch(action) {
				case APPLY_GHN_DELETE:
					GHNManager manager = new GHNManager(resID);
					manager.forceDelete(queryScope);					
					break;
				case APPLY_GHN_MOVE_TO_UNREACHABLE:
					GHNManager ghnManager = new GHNManager(resID);
					HostingNode res = (HostingNode) ghnManager.getResource(queryScope);		
					_log.trace("*** Setting HostingNode " + resID + " status to unreachable");
					res.profile().description().status("unreachable");
					HostingNode hn = ghnManager.getRegistryPublisher().update(res);
					_log.trace("*** getRegistryPublisher returned " + hn.toString());
					break;
				case APPLY_RI_DELETE:
					RunningInstanceManager riManager = new RunningInstanceManager(resID);
					_log.trace("*** The running instance " + resID + " will be deleted");
					riManager.forceDelete(queryScope);
					break;
				default:
				}
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		ScopeProvider.instance.set(currScope);
		return true;
	}
}
