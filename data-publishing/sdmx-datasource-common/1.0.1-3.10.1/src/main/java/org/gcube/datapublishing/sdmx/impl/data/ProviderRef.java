package org.gcube.datapublishing.sdmx.impl.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@Getter
@AllArgsConstructor
public class ProviderRef {
	
	private @NonNull String providerAgencyId;
	
	private @NonNull String providerId;	

}
