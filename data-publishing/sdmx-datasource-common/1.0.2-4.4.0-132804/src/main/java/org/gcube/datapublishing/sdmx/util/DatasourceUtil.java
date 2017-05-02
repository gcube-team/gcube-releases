package org.gcube.datapublishing.sdmx.util;

import lombok.extern.slf4j.Slf4j;

import org.gcube.datapublishing.sdmx.impl.data.ProviderRef;
import org.sdmxsource.sdmx.api.model.beans.reference.MaintainableRefBean;
import org.sdmxsource.sdmx.util.beans.reference.MaintainableRefBeanImpl;

@Slf4j
public class DatasourceUtil {
	
	public static MaintainableRefBean parseReferenceString(String flowRef)
			throws Exception {
		if (flowRef.isEmpty()) throw new Exception("Empty flowRef");
		String[] parts = flowRef.split(",");

		MaintainableRefBean ref;
		switch (parts.length) {
		case 1:
			ref = new MaintainableRefBeanImpl("all", parts[0], "latest");
			break;
		case 2:
			ref = new MaintainableRefBeanImpl(parts[0], parts[1], "latest");
			break;
		case 3:
			ref = new MaintainableRefBeanImpl(parts[0], parts[1], parts[2]);
			break;
		default:
			throw new Exception("Invalid flowRef");
		}
		log.trace("Generated Maintainable Reference: " + ref);
		return ref;
	}
	
	public static ProviderRef providerReferenceString(String providerRef) throws Exception{
		if (providerRef.isEmpty()) throw new Exception("Empty providerRef");
		String[] parts = providerRef.split(",");
		
		ProviderRef ref = null;
		switch (parts.length) {
		case 1:
			ref = new ProviderRef("all", parts[0]);
			break;
		case 2:
			ref = new ProviderRef(parts[0], parts[1]);
			break;
		default:
			throw new Exception("Invalid flowRef");
		}
		log.trace("Generated Maintainable Reference: " + ref);
		return ref;
	}
}
