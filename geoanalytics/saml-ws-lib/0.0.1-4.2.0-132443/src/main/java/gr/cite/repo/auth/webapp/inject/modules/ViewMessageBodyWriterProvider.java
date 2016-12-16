package gr.cite.repo.auth.webapp.inject.modules;

import io.dropwizard.views.ViewMessageBodyWriter;
import io.dropwizard.views.ViewRenderer;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

import com.codahale.metrics.MetricRegistry;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.sun.jersey.spi.service.ServiceFinder;

@Singleton
@Produces(MediaType.WILDCARD)
@Provider
class ViewMessageBodyWriterProvider extends ViewMessageBodyWriter{

	@Inject
	public ViewMessageBodyWriterProvider() {
		super(new MetricRegistry(), ServiceFinder.find(ViewRenderer.class));
	}
}