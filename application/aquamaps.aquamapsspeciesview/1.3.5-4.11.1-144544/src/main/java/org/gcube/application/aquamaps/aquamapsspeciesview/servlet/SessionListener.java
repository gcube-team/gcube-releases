package org.gcube.application.aquamaps.aquamapsspeciesview.servlet;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.gcube.application.aquamaps.aquamapsspeciesview.servlet.db.DBManager;
import org.gcube.application.aquamaps.aquamapsspeciesview.servlet.utils.Utils;
import org.gcube.application.framework.core.session.ASLSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SessionListener implements HttpSessionListener {

	private static final Logger logger = LoggerFactory.getLogger(SessionListener.class);
	
	
	@Override
	public void sessionCreated(HttpSessionEvent arg0) {
		//nop
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent arg0) {
		try{
			ASLSession session=Utils.getSession(arg0.getSession());
			try{
				DBManager.getInstance(session.getScope()).cleanMaps(session.getUsername());
			}catch(Exception e){logger.warn("Unable to clean db data for session : "+session.getUsername(), e);}
		}catch(Exception e ){logger.warn("Unable to get ASL Session for session "+arg0.getSession().getId(),e);}
	}

}
