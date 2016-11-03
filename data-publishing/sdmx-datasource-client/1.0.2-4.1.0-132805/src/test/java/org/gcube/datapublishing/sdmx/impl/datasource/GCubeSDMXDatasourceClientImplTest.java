package org.gcube.datapublishing.sdmx.impl.datasource;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.IOUtils;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.datapublishing.sdmx.api.datasource.GCubeSDMXDatasourceClient;
import org.gcube.datapublishing.sdmx.api.datasource.GCubeSDMXDatasourceClient.DataDetail;
import org.gcube.datapublishing.sdmx.impl.data.DataDocumentVersion;
import org.gcube.datapublishing.sdmx.impl.data.TimeseriesRegistration;
import org.gcube.datapublishing.sdmx.impl.model.GCubeSDMXDatasourceDescriptorImpl;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.request.RequestContextListener;

import com.sun.jersey.spi.spring.container.servlet.SpringServlet;
import com.sun.jersey.test.framework.AppDescriptor;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.WebAppDescriptor;

@Slf4j
public class GCubeSDMXDatasourceClientImplTest extends JerseyTest {

	static GCubeSDMXDatasourceClient client;

	@Override
	protected AppDescriptor configure() {
		return new WebAppDescriptor.Builder(
				"org.gcube.datapublishing.sdmx.rest")
				.contextParam("contextConfigLocation",
						"classpath:applicationContext-test.xml")
				.servletClass(SpringServlet.class)
				.contextListenerClass(ContextLoaderListener.class)
				.requestListenerClass(RequestContextListener.class).build();
	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		ScopeProvider.instance.set("/gcube/devsec");
		GCubeSDMXDatasourceDescriptorImpl descriptor = new GCubeSDMXDatasourceDescriptorImpl();
		descriptor.setRest_url_V2_1("http://localhost:9998/");
		descriptor.setPublishInterfaceUrl("http://localhost:9998/registration");
		client = new GCubeSDMXDatasourceClientImpl(descriptor);
	}

	@Test
	public void testRegisterTimeseries() {
		TimeseriesRegistration tsr = generateRandomTimeseriesRegistration();
		try {
			client.registerTimeseries(tsr.getFlowAgencyId(), tsr.getFlowId(),
					tsr.getFlowVersion(), tsr.getProviderAgencyId(),
					tsr.getProviderId(), tsr.getTimeseriesId(),
					tsr.getTimeseriesScope(), tsr.getRegistryScope());
		} catch (Exception e) {
			log.error("Error occurred during Timeseries registration", e);
			Assert.fail();
		}

	}

	@Test
	public void testGetTimeseriesRegistrations() {
		TimeseriesRegistration tsr = generateRandomTimeseriesRegistration();
		try {
			client.registerTimeseries(tsr.getFlowAgencyId(), tsr.getFlowId(),
					tsr.getFlowVersion(), tsr.getProviderAgencyId(),
					tsr.getProviderId(), tsr.getTimeseriesId(),
					tsr.getTimeseriesScope(), tsr.getRegistryScope());
		} catch (Exception e) {
			log.error("Error occurred during Timeseries registration", e);
			Assert.fail();
			return;
		}
		
		List<TimeseriesRegistration> list;
		try {
			list = client.getTimeseriesRegistrations();
		} catch (Exception e) {
			log.error("Error occurred while retrieving registered timeseries",e);
			Assert.fail();
			return;
		}
		Assert.assertNotNull(list);
		Assert.assertTrue(list.contains(tsr));
	}

	@Ignore
	@Test
	public void testDataQuery() {
		String randomString = UUID.randomUUID().toString();
		InputStream is = client.dataQuery("TEST_DATAFLOW", randomString, randomString, null, DataDetail.full, DataDocumentVersion.V2_1);
		String document;
		try {
			document = IOUtils.toString(is);
			log.trace("Received document with dataQuery: " + document);
			Assert.assertFalse(document.isEmpty());
		} catch (IOException e) {
			log.error("Error occurred while reading received document from SDMX datasource",e);
			Assert.fail();
		}
	}
	
	public void testRemoveTimeseriesRegistration(){
		TimeseriesRegistration r = generateRandomTimeseriesRegistration();
		
		try {
			client.registerTimeseries(r.getFlowAgencyId(), r.getFlowId(),
					r.getFlowVersion(), r.getProviderAgencyId(),
					r.getProviderId(), r.getTimeseriesId(),
					r.getTimeseriesScope(), r.getRegistryScope());
		} catch (Exception e) {
			log.error("Error occurred while registering timeseries");
			Assert.fail();
		}
		
		try {
			client.removeTimeseriesRegistration(r.getFlowAgencyId(),
					r.getFlowId(), r.getFlowVersion(), r.getProviderAgencyId(),
					r.getProviderId());
		} catch (Exception e) {
			log.error("Error occurred while removing timeseries registration");
			Assert.fail();
		}
		
		try {
			List<TimeseriesRegistration> list = client
					.getTimeseriesRegistrations();
			Assert.assertFalse(list.contains(r));
		} catch (Exception e) {
			log.error("Error occurred while getting timeseries registrations");
			Assert.fail();
		}
				
	}

	public static TimeseriesRegistration generateRandomTimeseriesRegistration() {
		TimeseriesRegistration tsr = new TimeseriesRegistration();
		String randomString = UUID.randomUUID().toString();
		tsr.setFlowAgencyId(randomString);
		tsr.setFlowId(randomString);
		tsr.setFlowVersion(randomString);
		tsr.setProviderAgencyId(randomString);
		tsr.setProviderId(randomString);
		tsr.setRegistryScope(randomString);
		tsr.setTimeseriesId(randomString);
		tsr.setTimeseriesScope(randomString);
		return tsr;
	}

}
