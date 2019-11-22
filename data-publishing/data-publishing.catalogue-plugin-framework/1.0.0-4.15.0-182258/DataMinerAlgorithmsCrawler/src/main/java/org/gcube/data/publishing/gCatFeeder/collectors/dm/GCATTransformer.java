package org.gcube.data.publishing.gCatFeeder.collectors.dm;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.gcube.data.publishing.gCatFeeder.collectors.dm.model.InternalAlgorithmDescriptor;
import org.gcube.data.publishing.gCatFeeder.collectors.dm.model.ckan.GCatModel;
import org.gcube.data.publishing.gCatfeeder.collectors.DataTransformer;

public class GCATTransformer implements DataTransformer<GCatModel,InternalAlgorithmDescriptor>{

	@Override
	public Set<GCatModel> transform(Collection<InternalAlgorithmDescriptor> collectedData) {
		HashSet<GCatModel> toReturn=new HashSet<>();
		for(InternalAlgorithmDescriptor desc:collectedData) {
			toReturn.add(desc.asCKANModel());
		}
		return toReturn;
	}
	
}
