/**
 * 
 */
package org.gcube.portlets.user.tdwx.server.session;

import java.util.Arrays;

/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class TDSessionList {
	
	protected TDSession[] sessions;
	
	public TDSessionList()
	{
		sessions = new TDSession[1];
	}
	
	public void set(int id, TDSession session)
	{
		if (id>=sessions.length) ensureSize(id+1);
		sessions[id] = session;
	}
	
	protected void ensureSize(int max)
	{
		int newSize = (int) Math.max(max, sessions.length*1.1);
		sessions = Arrays.copyOf(sessions, newSize);
	}
	
	public TDSession get(int id)
	{
		if (id>=sessions.length) return null;
		return sessions[id];
	}
	
	public boolean exists(int id)
	{
		return id > 0 && id < sessions.length; 
	}

}
