package org.gcube.datapublishing.sdmx.impl.datasource;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MediaType;

import lombok.extern.slf4j.Slf4j;

import org.gcube.datapublishing.sdmx.api.datasource.GCubeSDMXDatasourceClient;
import org.gcube.datapublishing.sdmx.api.model.GCubeSDMXDatasourceDescriptor;
import org.gcube.datapublishing.sdmx.impl.data.DataDocumentVersion;
import org.gcube.datapublishing.sdmx.impl.data.TimeseriesRegistration;
import org.sdmxsource.sdmx.api.constants.SDMX_ERROR_CODE;
import org.sdmxsource.sdmx.api.exception.SdmxException;
import org.springframework.beans.factory.annotation.Autowired;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;

@Slf4j
public class GCubeSDMXDatasourceClientImpl implements GCubeSDMXDatasourceClient {

	private GCubeSDMXDatasourceDescriptor datasource;

	@Autowired
	public GCubeSDMXDatasourceClientImpl(
			GCubeSDMXDatasourceDescriptor datasource) {
		super();
		this.datasource = datasource;
	}

	@Override
	public void registerTimeseries(String flowAgencyId, String flowId,
			String flowVersion, String providerAgencyId, String providerId,
			String timeseriesId, String timeseriesServiceScope,
			String registryScope) throws Exception {

		// Crete registration bean
		TimeseriesRegistration tsr = new TimeseriesRegistration(
				timeseriesServiceScope, timeseriesId, flowAgencyId, flowId,
				flowVersion, providerAgencyId, providerId, registryScope);

		Client client = Client.create();
		String url = datasource.getPublishInterfaceUrl();
		client.setFollowRedirects(true);
		log.debug("POST " + url + ", Timeseries Registration: " + tsr);
		ClientResponse response = client.resource(url)
				.accept(MediaType.APPLICATION_XML)
				.type(MediaType.APPLICATION_XML)
				.post(ClientResponse.class, tsr);

		log.debug("Datasource returned: " + response);

		if (response.getStatus() != 201) {
			log.error("Received status " + response.getStatus()
					+ " while registering timeseries on sdmx datasource. "
					+ "Received message from server: "
					+ response.getEntity(String.class));
			throw new Exception(
					"Error encountered while registering timeseries. Received status "
							+ response.getStatus() + " from sdmx datasource");
		}

		TimeseriesRegistration receivedTsr = response
				.getEntity(TimeseriesRegistration.class);
		if (receivedTsr.equals(tsr))
			return;
		throw new Exception(
				"Unable to register timeseries. Check sdmx datasource server log.");
	}

	@Override
	public List<TimeseriesRegistration> getTimeseriesRegistrations()
			throws Exception {
		Client client = Client.create();
		client.setFollowRedirects(true);
		ClientResponse response = client
				.resource(datasource.getPublishInterfaceUrl())
				.accept(MediaType.APPLICATION_XML).get(ClientResponse.class);

		if (response.getStatus() != 200) {
			log.error("Received status " + response.getStatus()
					+ " while registering timeseries on sdmx datasource. "
					+ "Received message from server: "
					+ response.getEntity(String.class));
			throw new Exception(
					"Error encountered while registering timeseries. Received status "
							+ response.getStatus() + " from sdmx datasource");
		}

		return response
				.getEntity(new GenericType<ArrayList<TimeseriesRegistration>>() {
				});
	}

	@Override
	public InputStream dataQuery(String flowRef, String key,
			String providerRef, Integer firstNObservations, DataDetail detail,
			DataDocumentVersion documentVersion) throws SdmxException {

		String url;
		try {
			url = getRestServiceUrl(documentVersion);
		} catch (Exception e) {
			throw new SdmxException("Invalid Data document version provided",
					SDMX_ERROR_CODE.SYNTAX_ERROR);
		}

		url += "data/" + flowRef + "/" + key + "/" + providerRef;

		if (detail == null)
			url += "?detail=" + DataDetail.full.toString();
		else
			url += "?detail=" + detail.toString();

		if (firstNObservations != null)
			url += "&firstNObservation=" + firstNObservations;

		Client client = Client.create();
		client.setFollowRedirects(true);
		WebResource resource = client.resource(url);

		String acceptType = MediaType.APPLICATION_XML + ";version="
				+ documentVersion.getVersion();
		log.debug("Accepted SDMX document version: " + documentVersion.getVersion());
		log.debug("GET " + url);
		ClientResponse response = resource.accept(acceptType).get(
				ClientResponse.class);
		log.debug("Datasource returned: " + response);

		return response.getEntityInputStream();
	}

	@Override
	public void removeTimeseriesRegistration(String flowAgencyId,
			String flowId, String flowVersion, String providerAgencyId,
			String providerId) throws Exception {
		Client client = Client.create();

		String url = datasource.getPublishInterfaceUrl();
		url += flowAgencyId;
		url += flowId;
		url += flowVersion;
		url += providerAgencyId;
		url += providerId;

		log.debug("DELETE " + url);
		ClientResponse response = client.resource(url).delete(
				ClientResponse.class);
		log.debug("Datasource returned: " + response);

		if (response.getStatus() == 204)
			return;
		if (response.getStatus() == 404) {
			log.warn("Datasource didn't found a Timeseries Registration with the given coordinates: [flowAgencyId="
					+ flowAgencyId
					+ ", flowId="
					+ flowId
					+ ", flowVersion="
					+ flowVersion
					+ ", providerAgencyId="
					+ providerAgencyId
					+ ", providerId=" + providerId + "]");
			return;
		}
		throw new Exception("Received unmanaged HTTP status code from datasource: " + response.getStatus());
	}

	private String getRestServiceUrl(DataDocumentVersion version)
			throws Exception {
		switch (version) {
		case V1:
			return datasource.getRest_url_V1();
		case V2:
			return datasource.getRest_url_V2();
		case V2_1:
			return datasource.getRest_url_V2_1();
		default:
			throw new Exception(
					"Unable to retrieve REST URL for the given document version: "
							+ version.toString());
		}
	}

}
