package org.gcube.portal.threadlocalexec;

import java.io.IOException;

import javax.servlet.ServletException;

import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;
import org.gcube.common.authorization.library.provider.AuthorizationProvider;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.scope.api.ScopeProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 
 * @author Massimiliano Assante, CNR ISTI
 * @author Lucio Lelii, CNR ISTI
 *
 */
public class SmartGearsPortalValve extends ValveBase  {
	private static final Logger _log = LoggerFactory.getLogger(SmartGearsPortalValve.class);

	@Override
	public void invoke(Request req, Response resp) throws IOException,	ServletException {
		SecurityTokenProvider.instance.reset();
		ScopeProvider.instance.reset();
		AuthorizationProvider.instance.reset();
		getNext().invoke(req, resp);
		//_log.trace("SmartGearsPortalValve SecurityTokenProvider and AuthorizationProvider reset OK");
	}
}

