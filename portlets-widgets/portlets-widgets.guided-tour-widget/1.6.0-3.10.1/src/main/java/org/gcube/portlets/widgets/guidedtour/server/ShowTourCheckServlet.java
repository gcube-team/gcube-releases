package org.gcube.portlets.widgets.guidedtour.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.gcube.portal.custom.communitymanager.OrganizationsUtil;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.portal.model.User;
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portal.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.security.permission.PermissionThreadLocal;
import com.liferay.portal.service.UserLocalServiceUtil;

/**
 * Servlet implementation class ShowTourCheckServlet
 */

@SuppressWarnings("serial")
public class ShowTourCheckServlet extends HttpServlet {
	private static final Logger _log = LoggerFactory.getLogger(ShowTourCheckServlet.class);

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String toReturn = "";
		String className = request.getParameter("className");

		User currUser;
		String username = null;
		boolean show = false;
		if (request.getSession().getAttribute(ScopeHelper.USERNAME_ATTRIBUTE) == null) { //if running into eclipse always shows the popup	
			show = true; 
		}
		else {
			username = (String) request.getSession().getAttribute(ScopeHelper.USERNAME_ATTRIBUTE);
			String attrToCheck = TourServiceImpl.getUniqueIdentifier(className);
			try {
				
				long companyId = OrganizationsUtil.getCompany().getCompanyId();
				_log.trace("Setting Thread Permission");
				User user = UserLocalServiceUtil.getUserByScreenName(companyId, ScopeHelper.getAdministratorUsername());
				PermissionChecker permissionChecker = PermissionCheckerFactoryUtil.create(user, false);
				PermissionThreadLocal.setPermissionChecker(permissionChecker);
				_log.trace("Setting Permission ok!");

				currUser = OrganizationsUtil.validateUser(username);
				show = currUser.getExpandoBridge().getAttribute(attrToCheck) != null ? true: false;

				_log.trace("Setting Thread Permission back to regular");
				user = UserLocalServiceUtil.getUserByScreenName(companyId, username);
				permissionChecker = PermissionCheckerFactoryUtil.create(user, false);
				PermissionThreadLocal.setPermissionChecker(permissionChecker);
				_log.trace("Setting Permission ok! returning ...");
			} catch (Exception e) { 
				e.printStackTrace();
			}
		}
		System.out.println("CheckServlet: user=" + request.getSession().getAttribute(ScopeHelper.USERNAME_ATTRIBUTE) + " toShow="+show);
		toReturn += show;
		response.setContentType("text/plain");  
		response.setCharacterEncoding("UTF-8"); 
		response.getWriter().write(toReturn); 
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub

	}
}