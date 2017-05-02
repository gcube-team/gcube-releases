package org.gcube.data.transfer.model;

import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Synchronized;
import lombok.ToString;

import org.gcube.data.transfer.model.options.DirectTransferOptions;
import org.gcube.data.transfer.model.options.HttpDownloadOptions;
import org.gcube.data.transfer.model.options.TransferOptions;


@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class TransferCapabilities {

	@XmlID
	private String nodeId;
	@XmlElement
	private String hostName;
	@XmlElement
	private Integer port;
	
	@XmlElementWrapper
	@XmlElementRefs({
			@XmlElementRef(type = DirectTransferOptions.class),
			@XmlElementRef(type = HttpDownloadOptions.class),
		})
	private Set<TransferOptions> availableMeans;
	
	@XmlElementWrapper
	private Set<PluginDescription> availablePlugins;
	
	@XmlElementWrapper
	private Set<String> availablePersistenceIds;

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
				
		if(availableMeans!=null){
			for(TransferOptions opt:availableMeans)
				result=prime*result+((opt==null)?0:opt.hashCode());
		}
		
		if(availablePlugins!=null){
			for(PluginDescription opt:availablePlugins)
				result=prime*result+((opt==null)?0:opt.hashCode());
		}
		
		result = prime * result
				+ ((nodeId == null) ? 0 : nodeId.hashCode());
		result = prime * result
				+ ((hostName == null) ? 0 : hostName.hashCode());
		result = prime * result
				+ ((port == null) ? 0 : port.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TransferCapabilities other = (TransferCapabilities) obj;
		
		
		//Check capabilities
		if ((availableMeans == null || availableMeans.isEmpty()))
				if(other.availableMeans!=null&& (!other.availableMeans.isEmpty()))
					return false;
		else if(availableMeans!=null && (!availableMeans.isEmpty()))
				if(other.availableMeans==null || other.availableMeans.isEmpty())
			return false;
		else if(availableMeans.size()!=other.availableMeans.size())
			return false;
		else for(TransferOptions opt:availableMeans)
			if(!other.availableMeans.contains(opt))
				return false;
		
		
		//Check plugins
		if ((availablePlugins == null || availablePlugins.isEmpty()))
			if(other.availablePlugins!=null&& (!other.availablePlugins.isEmpty()))
				return false;
	else if(availablePlugins!=null && (!availablePlugins.isEmpty()))
			if(other.availablePlugins==null || other.availablePlugins.isEmpty())
		return false;
	else if(availablePlugins.size()!=other.availablePlugins.size())
		return false;
	else for(PluginDescription opt:availablePlugins)
		if(!other.availablePlugins.contains(opt))
			return false;
		
		
		
		if (nodeId == null) {
			if (other.nodeId != null)
				return false;
		} else if (!nodeId.equals(other.nodeId))
			return false;
		
		if (hostName == null) {
			if (other.hostName != null)
				return false;
		} else if (!hostName.equals(other.hostName))
			return false;
		
		if (port == null) {
			if (other.port != null)
				return false;
		} else if (!port.equals(other.port))
			return false;
		
		
		return true;
	}

	
}
