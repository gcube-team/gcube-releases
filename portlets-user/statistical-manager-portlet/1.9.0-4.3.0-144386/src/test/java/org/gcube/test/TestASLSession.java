/**
 * 
 */
package org.gcube.test;

import org.gcube.application.framework.core.cache.CachesManager;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class TestASLSession {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		CachesManager.getInstance();
		ASLSession session = SessionManager.getInstance().getASLSession("1", "federico.defaveri");
		session.setScope("/gcube/devsec");
		System.out.println(session.getUsername());
	}

}
