package org.gcube.spatial.data.geonetwork.iso.tpl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.gcube.spatial.data.geonetwork.iso.Protocol;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DistributionInfo {

	public static enum DistributionInfoType{
		GeoServer,Thredds
	}
	
	private DistributionInfoType type;
	
	private Collection<Protocol> protocols;
	
	private Collection<OnlineResource> onlines;

	public DistributionInfo(DistributionInfoType type, Collection<OnlineResource> onlines) {
		super();
		this.type = type;
		this.onlines = onlines;
		Set<Protocol> protocols=new HashSet<Protocol>();
		for(OnlineResource res:onlines){
			protocols.add(res.getProtocol());
		}
		this.protocols=protocols;
	}
}
