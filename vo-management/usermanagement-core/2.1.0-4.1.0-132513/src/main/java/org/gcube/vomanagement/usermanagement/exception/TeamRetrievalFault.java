package org.gcube.vomanagement.usermanagement.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;



@SuppressWarnings("serial")
public class TeamRetrievalFault extends Exception{
	private static final Logger _log = LoggerFactory.getLogger(TeamRetrievalFault.class);
	public TeamRetrievalFault(String errorMsg) {
		_log.error(errorMsg);
	}
	/**
	 * 
	 */
	public TeamRetrievalFault(String errorMsg, SystemException e){
		_log.error(errorMsg + e);
	}

	public TeamRetrievalFault(String errorMsg, PortalException e){
		_log.error(errorMsg);
		e.printStackTrace();
	}
	
	public TeamRetrievalFault(String errorMsg, String roleId , PortalException e){
		_log.error(errorMsg + roleId);
		e.printStackTrace();
	}
}