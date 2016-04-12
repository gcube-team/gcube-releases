/**
 * 
 */
package org.cotrix.gcube.portlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.cotrix.gcube.stubs.CopyUtils;
import org.cotrix.gcube.stubs.News;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.applicationsupportlayer.social.ApplicationNewsManager;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author "Federico De Faveri federico.defaveri@fao.org"
 *
 */
public class NewsServiceServlet extends HttpServlet {

	private static final long serialVersionUID = 4766451165326531972L;
	
	private static final String APPLICATION_ID = "org.cotrix";
	
	private static Logger logger = LoggerFactory.getLogger(NewsServiceServlet.class);
	
	/** 
	 * {@inheritDoc}
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)	throws ServletException, IOException {
		HttpSession session = request.getSession();
		
		String username = (String) session.getAttribute(ScopeHelper.USERNAME_ATTRIBUTE);
		String sessionId = session.getId();
		logger.trace("username: {} sessionId: {}", username, sessionId);

		ASLSession aslSession = SessionManager.getInstance().getASLSession(sessionId, username);
		
		ApplicationNewsManager newsManager = new ApplicationNewsManager(aslSession, APPLICATION_ID);
		
		String json = CopyUtils.toString(request.getInputStream());
		logger.trace("news json: {}", json);
		News news = News.valueOf(json);
		
		newsManager.shareApplicationUpdate(news.getText());
		
		response.setStatus(HttpServletResponse.SC_OK);
		response.getOutputStream().close();
	}
}
