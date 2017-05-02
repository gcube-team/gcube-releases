package gr.cite.repo.auth.webapp.inject.modules;

import com.sun.jersey.guice.JerseyServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

public class SimpleServletModule extends JerseyServletModule {

	public SimpleServletModule() {
		super();
	}

	@Override
	protected void configureServlets() {
		serve("/*").with(GuiceContainer.class);
	}
}
