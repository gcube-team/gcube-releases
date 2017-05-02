package org.gcube.datapublishing.sdmx.api.datasource;

import java.io.InputStream;
import java.util.List;

import org.gcube.datapublishing.sdmx.impl.data.DataDocumentVersion;
import org.gcube.datapublishing.sdmx.impl.data.TimeseriesRegistration;
import org.sdmxsource.sdmx.api.exception.SdmxException;

public interface GCubeSDMXDatasourceClient {

	public enum DataDetail {
		full, dataonly, serieskeysonly, nodata,
	}

	public void registerTimeseries(String flowAgencyId, String flowId, String flowVersion, String providerAgencyId, String providerId,
			String timeseriesId, String timeseriesServiceScope, String registryScope)
			throws Exception;
	
	public List<TimeseriesRegistration> getTimeseriesRegistrations() throws Exception;
	
	public void removeTimeseriesRegistration(String flowAgencyId, String flowId, String flowVersion, String providerAgencyId, String providerId) throws Exception;

	public InputStream dataQuery(String flowRef, String key, String providerRef,
			Integer firstNObservations, DataDetail detail, DataDocumentVersion documentVersion) throws SdmxException;

}
