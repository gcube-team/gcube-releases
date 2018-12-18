package org.gcube.vomanagement.usermanagement.exception;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

@SuppressWarnings("serial")
public class UserRetrievalFault extends Exception {
	private static final Log _log = LogFactoryUtil.getLog(UserRetrievalFault.class);

	public UserRetrievalFault(String errorMsg, PortalException e) {
		_log.warn(errorMsg);
	}
	
	public UserRetrievalFault(String errorMsg, String username , PortalException e) {
		_log.warn(errorMsg + ", username=" + username);
	}
	
	public UserRetrievalFault(String errorMsg, long userId , PortalException e) {
		_log.warn(errorMsg + ", userId=" + userId);
	}
}