package org.gcube.data.transfer.model;

import java.util.Collections;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.transfer.model.options.TransferOptions;
import org.gcube.data.transfer.model.settings.DirectTransferSettings;
import org.gcube.data.transfer.model.settings.HttpDownloadSettings;
import org.gcube.data.transfer.model.settings.TransferSettings;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class TransferRequest{

	
	@XmlID
	private String id;
	@XmlElementRefs({
		@XmlElementRef(type = DirectTransferSettings.class),
		@XmlElementRef(type = HttpDownloadSettings.class),
	})
	private TransferSettings settings;
	
	@XmlElement
	private Destination destinationSettings;
	
	@XmlElementWrapper
	private Set<PluginInvocation> pluginInvocations;
	
	
	public TransferRequest(String id, TransferSettings settings, Destination destinationSettings) {
		super();
		this.id = id;
		this.settings = settings;
		this.destinationSettings = destinationSettings;
		this.pluginInvocations=Collections.emptySet();
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TransferRequest other = (TransferRequest) obj;
		if (destinationSettings == null) {
			if (other.destinationSettings != null)
				return false;
		} else if (!destinationSettings.equals(other.destinationSettings))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		
		
		//Check plugins
				if ((pluginInvocations == null || pluginInvocations.isEmpty()))
					if(other.pluginInvocations!=null&& (!other.pluginInvocations.isEmpty()))
						return false;
			else if(pluginInvocations!=null && (!pluginInvocations.isEmpty()))
					if(other.pluginInvocations==null || other.pluginInvocations.isEmpty())
				return false;
			else if(pluginInvocations.size()!=other.pluginInvocations.size())
				return false;
			else for(PluginInvocation opt:pluginInvocations)
				if(!other.pluginInvocations.contains(opt))
					return false;
		return true;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((destinationSettings == null) ? 0 : destinationSettings.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		if(pluginInvocations!=null){
			for(PluginInvocation opt:pluginInvocations)
				result=prime*result+((opt==null)?0:opt.hashCode());
		}
		result = prime * result + ((settings == null) ? 0 : settings.hashCode());
		return result;
	}
	
	
	
	
}
