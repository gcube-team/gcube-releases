package org.gcube.datapublishing.sdmx.impl.model;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.datapublishing.sdmx.api.model.GCubeSDMXDatasourceDescriptor;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;

@Slf4j
public class GCubeSDMXDatasourceDescriptorIS implements
		GCubeSDMXDatasourceDescriptor {

	enum DatasourceInterfaceCode {
		RESTV1, RESTV2, RESTV2_1, PUBLISH,
	}
	
	private GCubeSDMXDatasourceDescriptor delegate;

	private boolean empty = true;

	public String getRest_url_V2_1() {
		if (empty) delegate = retrieveDescriptor();
		return delegate.getRest_url_V2_1();
	}

	public String getRest_url_V2() {
		if (empty) delegate = retrieveDescriptor();
		return delegate.getRest_url_V2();
	}

	public String getRest_url_V1() {
		if (empty) delegate = retrieveDescriptor();
		return delegate.getRest_url_V1();
	}

	public String getPublishInterfaceUrl() {
		if (empty) delegate = retrieveDescriptor();
		return delegate.getPublishInterfaceUrl();
	}

	/**
	 * Retrieve Datasource endpoints data from IS
	 * @return The datasource descriptor
	 */
	private GCubeSDMXDatasourceDescriptor retrieveDescriptor() {
		SimpleQuery query = queryFor(ServiceEndpoint.class);

		String scope = ScopeProvider.instance.get();

		if (scope == null || scope.isEmpty()) {
			log.error("No scope provided, unable to retrieve SDMX datasource");
			return null;
		}

		query.addCondition("$resource/Profile/Category/text() eq 'SDMX'");
		query.addCondition("$resource/Profile/Name/text() eq 'SDMXDatasource'");

		DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);
		List<ServiceEndpoint> resources = client.submit(query);

		if (resources.size() > 1 || resources.isEmpty()) {
			log.error("Invalid number of SDMX datasource resources found");
			return null;
		}

		GCubeSDMXDatasourceDescriptorImpl descriptor = new GCubeSDMXDatasourceDescriptorImpl(); 

		for (AccessPoint ap : resources.get(0).profile().accessPoints()) {
			log.trace("Retrieved " + ap.name() + ": " + ap.address());
			if (ap.name().equals(DatasourceInterfaceCode.RESTV1.toString()))
				descriptor.setRest_url_V1(ap.address());
			if (ap.name().equals(DatasourceInterfaceCode.RESTV2.toString()))
				descriptor.setRest_url_V2(ap.address());
			if (ap.name().equals(DatasourceInterfaceCode.RESTV2_1.toString()))
				descriptor.setRest_url_V2_1(ap.address());
			if (ap.name().equals(DatasourceInterfaceCode.PUBLISH.toString()))
				descriptor.setPublishInterfaceUrl(ap.address());
		}
		empty = false;
		log.debug("SDMX datasource resource retrieved from IS: " + descriptor);
		return descriptor;
	}

}
