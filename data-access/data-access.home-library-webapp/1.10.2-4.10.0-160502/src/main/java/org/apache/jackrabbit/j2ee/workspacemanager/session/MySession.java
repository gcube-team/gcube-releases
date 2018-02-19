package org.apache.jackrabbit.j2ee.workspacemanager.session;

import javax.jcr.Session;

import org.gcube.common.homelibary.model.util.Cleanable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A wrapper for JCR session object.
 */
public class MySession implements Cleanable{

	private static Logger logger = LoggerFactory.getLogger(MySession.class);
	private String user;
	private Session session;

	public MySession(String user, Session session) {
		this.user = user;
		this.session = session;
	}

	/**
	 * @return the user
	 */
	public String getUser() {
		return user;
	}

	/**
	 * @return the session
	 */
	public Session getSession() {
		return session;
	}

	@Override
	public void releaseResources() {
		destroyJCRSession();
	}

	@Override
	protected void finalize() throws Throwable {
		destroyJCRSession();
	}

	/**
	 * Explicitly logout (remove locks etc)
	 */
	private void destroyJCRSession(){
		if(session != null){
			try{
				// explicitly destroy this jcr session
				logger.debug("Releasing session resources for session with id " + session.toString());
				session.logout();
				session = null;
			}catch(Exception e){
				logger.warn("Failed to release session resources for session with id " + session.toString(), e);
			}
		}
	}


}
