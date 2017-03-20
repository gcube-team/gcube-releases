package org.gcube.execution;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.gcube.rest.commons.resourceawareservice.resources.RunInstance;
import org.gcube.rest.resourcemanager.discovery.InformationCollector;
import org.gcube.rest.resourcemanager.is.discovery.ISInformationCollector;
import org.gcube.rest.resourcemanager.is.publisher.is.PublisherISimpl;
import org.gcube.rest.resourcemanager.publisher.ResourcePublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;

@Path("/")
public class ExecutionEngineService {

	private static final Logger logger = LoggerFactory.getLogger(ExecutionEngineService.class);

	boolean initialized = false;

	public ExecutionEngineService() throws Exception {
		this.initialize();
	}

	synchronized void initialize() throws Exception {
		Injector injector = Guice.createInjector(
//				new JpaPersistModule("myapp-db"),
				new AbstractModule() {

			@Override
			protected void configure() {
				// bind(ResourceModelDao.class);
				// bind(RunInstanceModelDao.class);
				// bind(SerInstanceModelDao.class);
				// bind(HostNodeModelDao.class);
				bind(InformationCollector.class).to(ISInformationCollector.class);
				 bind(new TypeLiteral<ResourcePublisher<RunInstance>>(){})
				 .to(new TypeLiteral<PublisherISimpl<RunInstance>>(){});
			}
		});
//		ApplicationInitializer ai = injector.getInstance(ApplicationInitializer.class);

		if (!initialized) {
			System.out.println("Initializing execution engine service context...");
			ServiceContext sc = injector.getInstance(ServiceContext.class);
			System.out.println("Initializing execution engine service context...OK");
			initialized = true;
		}
	}

	@GET
	@Path("/about")
	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
	public Response about() {
		String msg = "{\"msg\" : \"hello WS world\" }";
		Response.Status status = Response.Status.OK;

		return Response.status(status).entity(msg).build();
	}
}
