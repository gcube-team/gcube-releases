package org.gcube.application.aquamaps.aquamapsportlet.servlet.listeners;

import java.util.List;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.gcube.application.aquamaps.aquamapsportlet.client.constants.Tags;
import org.gcube.application.aquamaps.aquamapsportlet.servlet.db.DBManager;
import org.gcube.application.aquamaps.aquamapsportlet.servlet.utils.Utils;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SessionListener implements HttpSessionListener {

	private static final long serialVersionUID = -873906383637717415L;
	private static final Logger logger = LoggerFactory.getLogger(SessionListener.class);

	
	public void sessionCreated(HttpSessionEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void sessionDestroyed(HttpSessionEvent arg0) {
		try {
			logger.trace("Session destroyed, deleting related data");			
			ASLSession session = Utils.getSession(arg0.getSession());
			DBManager.getInstance(session.getScope()).removeSession(session.getUsername());
			List<String> fetchedBaskets=(List<String>) session.getAttribute(Tags.lastFetchedBasket);
			logger.trace("Found "+fetchedBaskets.size()+" fetched Basket to Delete");
			for(String basketId:fetchedBaskets)
				DBManager.getInstance(session.getScope()).deleteFetched(basketId);
			logger.trace("Done");
		} catch (Exception e) {
			String user =arg0.getSession().getAttribute(ScopeHelper.USERNAME_ATTRIBUTE).toString();
			logger.error("Unable to destroy session ID : "+arg0.getSession().getId()+" user : "+user);
		}
	}
}
