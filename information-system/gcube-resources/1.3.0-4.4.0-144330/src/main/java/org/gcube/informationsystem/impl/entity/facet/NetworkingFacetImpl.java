/**
 * 
 */
package org.gcube.informationsystem.impl.entity.facet;

import org.gcube.informationsystem.impl.entity.FacetImpl;
import org.gcube.informationsystem.model.entity.facet.NetworkingFacet;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@JsonTypeName(value=NetworkingFacet.NAME)
public class NetworkingFacetImpl extends FacetImpl implements NetworkingFacet {
	
	/**
	 * Generated Serial version UID
	 */
	private static final long serialVersionUID = 4634990116645322204L;
	
	protected String hostName;
	protected String domainName;
	protected String ipAddress;
	protected String mask;
	protected String broadcastAddress;

	/**
	 * @return the hostName
	 */
	@Override
	public String getHostName() {
		return hostName;
	}

	/**
	 * @param hostName the hostName to set
	 */
	@Override
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	/**
	 * @return the domainName
	 */
	@Override
	public String getDomainName() {
		return domainName;
	}

	/**
	 * @param domainName the domainName to set
	 */
	@Override
	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

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
