/**
 * 
 */
package org.gcube.informationsystem.impl.facet;

import org.gcube.informationsystem.impl.entity.FacetImpl;
import org.gcube.informationsystem.model.facet.IPInfoFacet;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 */
public class IPInfoFacetImpl extends FacetImpl implements IPInfoFacet {
	
	protected String ipAddress;
	
	protected String mask;
	
	protected String broadcastAddress;

	@Override
	public String getIPAddress() {
		return this.ipAddress;
	}

	@Override
	public void setIPAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	@Override
	public String getMask() {
		return this.mask;
	}

	@Override
	public void setMask(String mask) {
		this.mask = mask;
	}

	@Override
	public String getBroadcastAddress() {
		return this.broadcastAddress;
	}

	@Override
	public void setBroadcastAddress(String broadcastAddress) {
		this.broadcastAddress = broadcastAddress;
	}

}
