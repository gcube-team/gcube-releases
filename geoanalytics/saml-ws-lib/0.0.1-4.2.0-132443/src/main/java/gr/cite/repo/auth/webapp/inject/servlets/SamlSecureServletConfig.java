package gr.cite.repo.auth.webapp.inject.servlets;

import gr.cite.repo.auth.app.config.SamlSecurityConfiguration;
import gr.cite.repo.auth.app.utils.LocationResolver;
import gr.cite.repo.auth.app.utils.UrlLocationResolver;
import gr.cite.repo.auth.webapp.inject.modules.SamlSecureServletModule;
import gr.cite.repo.auth.webapp.inject.modules.SimpleServletModule;
import io.dropwizard.configuration.ConfigurationFactory;
import io.dropwizard.configuration.ConfigurationSourceProvider;
import io.dropwizard.configuration.FileConfigurationSourceProvider;
import io.dropwizard.jackson.Jackson;

import java.util.List;

import javax.validation.Validation;
import javax.validation.Validator;

import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.util.Modules;

public abstract class SamlSecureServletConfig<T extends SamlSecurityConfiguration> extends GuiceServletContextListener {

	protected abstract Class<T> getClazz();

	protected abstract String getConfiguationFilePath();

	protected abstract List<Module> getModules();

	protected abstract String getName();

	private T configuration;

	@Override
	protected Injector getInjector() {

		Module servletModule = isSecure() ? new SamlSecureServletModule(this.getConfiguration())
				: new SimpleServletModule();

		List<Module> allModules = Lists.newArrayList(servletModule);
		allModules.addAll(this.getModules());

		return Guice.createInjector(Modules.override(defaultModule).with(allModules));
	}

	public T getConfiguration() {
		if (configuration == null) {
			configuration = getConf(getConfiguationFilePath(), this.getClazz());
			configuration.getLoggingFactory().configure(new MetricRegistry(), this.getName());
		}
		return configuration;
	}

	protected Boolean isSecure() {
		return this.getConfiguration().getSecurity() != null;
	}

	private Module defaultModule = new AbstractModule() {
		@Override
		protected void configure() {
			bind(LocationResolver.class).to(UrlLocationResolver.class);
		}
	};

	T getConf(String path, Class<T> klass) {
		ConfigurationSourceProvider provider = new FileConfigurationSourceProvider();

		String propertyPrefix = "dw";
		ObjectMapper objectMapper = Jackson.newObjectMapper();
		Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
		// .byProvider(HibernateValidator.class)
		// .configure()
		// .addValidatedValueHandler(new OptionalValidatedValueUnwrapper())
		// .buildValidatorFactory().getValidator();
		ConfigurationFactory<T> factory = new ConfigurationFactory<T>(klass, validator, objectMapper, propertyPrefix);

		T configuration = null;

		try {
			configuration = factory.build(provider, path);
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}

		return configuration;
	}
}
