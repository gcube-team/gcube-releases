package utils;

import javax.servlet.ServletContext;

import org.gcube.informationsystem.publisher.ScopedPublisher;
import org.gcube.smartgears.configuration.application.ApplicationConfiguration;
import org.gcube.smartgears.configuration.application.ApplicationExtensions;
import org.gcube.smartgears.configuration.application.ApplicationHandlers;
import org.gcube.smartgears.context.application.ApplicationContext;
import org.gcube.smartgears.context.container.ContainerContext;
import org.gcube.smartgears.provider.DefaultProvider;

public class TestProvider extends DefaultProvider {

	public ApplicationContext context;
	public ApplicationConfiguration configuration;
	public ApplicationHandlers handlers;
	public ApplicationExtensions extensions;
	public ScopedPublisher publisher;
	
	public void use(ScopedPublisher publisher) {
		this.publisher=publisher;
	}
	
	public void use(ApplicationConfiguration configuration) {
		this.configuration=configuration;
	}
	
	public void use(ApplicationHandlers handlers) {
		this.handlers=handlers;
	}
	
	public void use(ApplicationExtensions extensions) {
		this.extensions=extensions;
	}
	
	@Override
	public ScopedPublisher publisherFor(ApplicationContext context) {
		return publisher==null?super.publisherFor(context):publisher;
	}
	
	@Override
	public ApplicationContext contextFor(ContainerContext container,ServletContext application) {
		return context = super.contextFor(container,application);
	}
	
	@Override
	public ApplicationHandlers handlersFor(ApplicationContext context) {
		return handlers==null?super.handlersFor(context):handlers;
	}
	
	@Override
	public ApplicationExtensions extensionsFor(ApplicationContext context) {
		return extensions==null?super.extensionsFor(context):extensions;
	}
}
