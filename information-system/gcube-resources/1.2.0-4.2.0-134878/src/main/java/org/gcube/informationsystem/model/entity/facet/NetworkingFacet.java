/**
 * 
 */
package org.gcube.informationsystem.model.entity.facet;

import org.gcube.informationsystem.impl.entity.facet.NetworkingFacetImpl;
import org.gcube.informationsystem.model.annotations.ISProperty;
import org.gcube.informationsystem.model.entity.Facet;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @author Luca Frosini (ISTI - CNR)
 * https://wiki.gcube-system.org/gcube/Facet_Based_Resource_Model#Network_Address_Facet
 */
@JsonDeserialize(as=NetworkingFacetImpl.class)
public interface NetworkingFacet extends Facet {
	
	public static final String NAME = "NetworkingFacet"; // NetworkingFacet.class.getSimpleName();
	public static final String DESCRIPTION = "Networking information";
	public static final String VERSION = "1.0.0";
	
	public static final String IP_ADDRESS = "IPAddress";
	
	@ISProperty
	public String getHostName();
	
	public void setHostName(String hostName);
	
	@ISProperty
	public String getDomainName();
	
	public void setDomainName(String domainName);
		
	@ISProperty(mandatory=true, nullable=false, name=IP_ADDRESS)
	public String getIPAddress();
	
	public void setIPAddress(String ipAddress);
	
	@ISProperty
	public String getMask();
	
	public void setMask(String mask);
	
	@ISProperty
	public String getBroadcastAddress();
	
	public void setBroadcastAddress(String broadcastAddress);
	
}
