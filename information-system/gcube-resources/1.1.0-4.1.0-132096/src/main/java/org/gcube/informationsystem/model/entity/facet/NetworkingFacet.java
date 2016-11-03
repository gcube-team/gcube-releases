/**
 * 
 */
package org.gcube.informationsystem.model.entity.facet;

import org.gcube.informationsystem.impl.entity.facet.NetworkingFacetImpl;
import org.gcube.informationsystem.model.annotations.ISProperty;
import org.gcube.informationsystem.model.entity.Facet;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 * https://wiki.gcube-system.org/gcube/Facet_Based_Resource_Model#Network_Address_Facet
 */
@JsonDeserialize(as=NetworkingFacetImpl.class)
public interface NetworkingFacet extends Facet {
	
	public static final String NAME = "NetworkingFacet"; // NetworkingFacet.class.getSimpleName();
	public static final String DESCRIPTION = "Networking information";
	public static final String VERSION = "1.0.0";
	
	public static final String IP_ADDRESS = "IPAddress";
	
	public static final String IP_ADDRESS_PATTERN = "'/^(?>(?>([a-f0-9]{1,4})(?>:(?1)){7}|(?!(?:.*[a-f0-9](?>:|$)){8,})((?1)(?>:(?1)){0,6})?::(?2)?)|(?>(?>(?1)(?>:(?1)){5}:|(?!(?:.*[a-f0-9]:){6,})(?3)?::(?>((?1)(?>:(?1)){0,4}):)?)?(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9]?[0-9])(?>\\.(?4)){3}))$/iD'";
	
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
