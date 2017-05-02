package gr.cite.geoanalytics.security;

import gr.cite.geoanalytics.dataaccess.entities.principal.Principal;
import gr.cite.geoanalytics.manager.PrincipalManager;
import gr.cite.geoanalytics.manager.UserManager;
import gr.cite.geoanalytics.notifications.NotificationManager;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;

public class GeoanalyticsLogoutHandler extends SimpleUrlLogoutSuccessHandler {
	private static final Logger log = LoggerFactory.getLogger(GeoanalyticsLogoutHandler.class);
	
	@Resource
	private UserManager userManager;
	@Resource
	private NotificationManager notificationManager;
	@Resource 
	private PrincipalManager principalManager;
	
	@Override
	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException  {
		if (authentication != null) {
			Principal principal = null;
			try {
				principal = principalManager.getPrincipalByNameAndActivity(authentication.getName(), null);
				//notificationManager.unregister(principal.getNotificationId());//TODO
			} catch (Exception e) {
				//log.error("Could not unregister notification events for user " + principal.getSystemName()
						//+ " (notification id: " + principal.getNotificationId() + ")", e);
				// new ServletException("Could not unregister notification events for user " + principal.getSystemName()
						//+ " (notification id: " + principal.getNotificationId() + ")");
			}

		}
		setDefaultTargetUrl("/login");
		super.onLogoutSuccess(request, response, authentication);
	}

}
