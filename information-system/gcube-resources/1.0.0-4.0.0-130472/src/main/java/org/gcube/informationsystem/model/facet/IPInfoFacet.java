/**
 * 
 */
package org.gcube.informationsystem.model.facet;

import org.gcube.informationsystem.model.annotations.ISProperty;
import org.gcube.informationsystem.model.entity.Facet;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 * 
 */
public interface IPInfoFacet extends Facet {
	
	public static final String NAME = IPInfoFacet.class.getSimpleName();
	public static final String DESCRIPTION = "IP information";
	public static final String VERSION = "1.0.0";
	
	public static final String IP_ADDRESS = "IPAddress";
	
	@ISProperty(name=IP_ADDRESS)
	public String getIPAddress();
	
	public void setIPAddress(String ipAddress);
	
	@ISProperty
	public String getMask();
	
	public void setMask(String mask);
	
	@ISProperty
	public String getBroadcastAddress();
	
	public void setBroadcastAddress(String broadcastAddress);
	
}
