/**
 *
 */
package org.gcube.portlets.user.speciesdiscovery.server.util;

import org.apache.log4j.Logger;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.workspace.Workspace;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Oct 7, 2013
 *
 */
public class GetWorkspaceUtil {

	protected static Logger logger = Logger.getLogger(GetWorkspaceUtil.class);

	public static Workspace getWorskspace(ASLSession session) throws Exception {

		if(session==null)
			throw new Exception("ASL session is null");

		if(session.getScope()==null)
			throw new Exception("Scope into ASL session is null");

		String scope = session.getScope().toString();
		//logger.trace("Get workspace for scope "+scope);
		//ScopeProvider.instance.set(scope);
		//logger.trace("ScopeProvider instancied for scope "+scope);
		logger.trace("retuning workspace for username "+session.getUsername());
		return HomeLibrary.getUserWorkspace(session.getUsername());
	}
}
