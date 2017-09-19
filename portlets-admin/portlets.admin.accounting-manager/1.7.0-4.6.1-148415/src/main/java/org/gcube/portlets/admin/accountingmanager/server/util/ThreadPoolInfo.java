package org.gcube.portlets.admin.accountingmanager.server.util;

import java.io.Serializable;

/**
 * 
  * @author Giancarlo Panichi
 *
 *
 */
public class ThreadPoolInfo implements Serializable {

	private static final long serialVersionUID = 6955469914102879890L;
	private long timeout;

	/**
	 * 
	 * @param timeout time out
	 */
	public ThreadPoolInfo(long timeout) {
		super();
		this.timeout = timeout;
	}

	public long getTimeout() {
		return timeout;
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	@Override
	public String toString() {
		return "ThreadPoolInfo [timeout=" + timeout + "]";
	}

}
