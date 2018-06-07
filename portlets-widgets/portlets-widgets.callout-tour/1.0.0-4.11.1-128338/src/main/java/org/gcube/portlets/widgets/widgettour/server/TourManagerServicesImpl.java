package org.gcube.portlets.widgets.widgettour.server;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.portal.TourManager.TourManager;
import org.gcube.portal.TourManager.TourManagerImpl;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;
import org.gcube.portlets.widgets.widgettour.client.TourManagerServices;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.liferay.portal.service.UserLocalServiceUtil;

/**
 * The serlvet that talks with the tour-manager library
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
@SuppressWarnings("serial")
public class TourManagerServicesImpl extends RemoteServiceServlet implements
TourManagerServices {

	// logger
	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(TourManagerServicesImpl.class);

	// dev user
	public static final String userid = "test.user";

	//dev vre
	private static final String vreID = "/gcube/devsec/devVRE";

	// Tour manager
	private TourManager tourManager;

	public TourManagerServicesImpl(){

		tourManager = new TourManagerImpl();

	}

	/**
	 * Online or in development mode?
	 * @return true if you're running into the portal, false if in development
	 */
	private boolean isWithinPortal() {
		try {
			UserLocalServiceUtil.getService();
			return true;
		} 
		catch (Exception ex) {			
			logger.debug("Development Mode ON");
			return false;
		}			
	}

	/**
	 * when packaging test will fail if the user is not set to test.user
	 * @return the current user's username
	 */
	public String getDevelopmentUser() {
		String user = userid;
		//		user = "costantino.perciante";
		return user;
	}

	/**
	 * Get the current ASLSession
	 * @return the session
	 */
	private ASLSession getASLSession() {

		String sessionID = this.getThreadLocalRequest().getSession().getId();
		String user = (String) this.getThreadLocalRequest().getSession().getAttribute(ScopeHelper.USERNAME_ATTRIBUTE);

		if (user == null) {

			logger.warn("USER IS NULL setting " + userid + " and Running OUTSIDE PORTAL");
			user = getDevelopmentUser();
			SessionManager.getInstance().getASLSession(sessionID, user).setScope(vreID);

		}		

		return SessionManager.getInstance().getASLSession(sessionID, user);
	}

	@Override
	public boolean setShowNextTime(String callerIdentifier, int versionNumber,
			boolean show) {

		logger.debug("Portlet with identifier " + callerIdentifier + " and version number " + versionNumber 
				+ " is asking to " + (show ? " show " : " no longer show " ) + "its tour." );

		if(isWithinPortal()){

			// get the current user
			ASLSession session = getASLSession();

			// get his/her username
			String username = session.getUsername();

			tourManager.setShowNextTime(callerIdentifier, versionNumber, show, username);

			// now ask if this tour can be shown and return the answer to the portlet
			try {
				return tourManager.isTourShowable(callerIdentifier, versionNumber, username);
			} catch (Exception e) {
				logger.error("Error occurred while asking if tour is showable!", e);
				return false;
			}

		}else{

			logger.debug("Anyway you are in development mode.");
			return true;

		}

	}

	@Override
	public boolean isTourShowable(String callerIdentifier, int versionNumber) {

		logger.debug("Portlet with identifier " + callerIdentifier + " and version number " + versionNumber 
				+ " is asking if its tour can be shown." );

		if(isWithinPortal()){

			// get the current user
			ASLSession session = getASLSession();

			// get his/her username
			String username = session.getUsername();

			try {
				return tourManager.isTourShowable(callerIdentifier, versionNumber, username);
			} catch (Exception e) {
				logger.error("Error occurred while asking if tour is showable!", e);
				return false;
			}

		}else{

			logger.debug("Anyway you are in development mode. You can show whatever you want");
			return true;
		}
	}

}
