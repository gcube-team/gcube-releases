package org.gcube.vomanagement.usermanagement.exception;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

@SuppressWarnings("serial")
public class GroupRetrievalFault extends Exception {
	private static final Log _log = LogFactoryUtil.getLog(GroupRetrievalFault.class);
	public GroupRetrievalFault(String errorMsg, long groupId){
		_log.error(errorMsg + ", groupId=" + groupId);
	}

	public GroupRetrievalFault(String errorMsg, long groupId , PortalException e){
		_log.error(errorMsg + ", groupId=" + groupId);
	}
	
	public GroupRetrievalFault(String errorMsg, long groupId , SystemException e){
		_log.error(errorMsg + ", groupId=" + groupId);
	}
}
