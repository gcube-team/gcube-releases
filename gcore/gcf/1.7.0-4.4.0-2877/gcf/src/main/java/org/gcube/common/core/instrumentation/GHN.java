package org.gcube.common.core.instrumentation;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import javax.management.MBeanNotificationInfo;
import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;

import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.scope.GCUBEScope;

public class GHN extends NotificationBroadcasterSupport implements GHNMBean {

	protected AtomicInteger seqNum = new AtomicInteger(0);
	
	public String getUptime() {
		return GHNContext.getContext().getUptime();
	}

	public void restart() throws Exception {
		new Thread() {public void run() {
			try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}GHNContext.getContext().restart();}}.start();
	}

	public String addScope(String s) throws Exception {
		Set<GCUBEScope> scopes =GHNContext.getContext().addScope(GCUBEScope.getScope(s));
		notifyAddedScope(GCUBEScope.getScope(s));
		return (scopes.size()>0?scopes.iterator().next():"no scope")+" was added to the GHN";
	}
	
	public String removeScope(String s) throws Exception {
		GCUBEScope scope = GCUBEScope.getScope(s);
		Set<GCUBEScope> scopes =GHNContext.getContext().removeScope(scope);
		return (scopes.size()>0?scopes.iterator().next():"no scope")+" was removed from the Running Instance";
	}

	 @Override 
	 public MBeanNotificationInfo[] getNotificationInfo() {
	     
	      String[] types = new String[]{
	         GHNNotification.ADDEDSCOPE
	      };
	      String name = GHNNotification.class.getName();
	      String description = "GHN Scope added";
	      MBeanNotificationInfo info = new MBeanNotificationInfo(types, name, description);

	      return (new MBeanNotificationInfo[]{ info });
	   }

	 public void notifyAddedScope(GCUBEScope scope) {
	   Notification event = new GHNNotification(GHNNotification.ADDEDSCOPE,this, "Scope added: " + scope.toString()
	            , seqNum.getAndAdd(1));
	      sendNotification(event);
	   } 
}
