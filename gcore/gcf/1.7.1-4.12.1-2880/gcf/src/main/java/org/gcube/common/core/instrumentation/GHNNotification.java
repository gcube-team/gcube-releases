package org.gcube.common.core.instrumentation;

import javax.management.Notification;

public class GHNNotification extends Notification{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String ADDEDSCOPE = "org.gcube.ghn.scope.add";
	
	 public GHNNotification(String arg0, Object source, long arg2) {
		super(ADDEDSCOPE, source, arg2);
		setTimeStamp(System.currentTimeMillis());
	}
	 public GHNNotification(String type,Object source , String  message, long arg2) {
			super(ADDEDSCOPE,source,arg2 ,message);
			setTimeStamp(System.currentTimeMillis());
	}

}
