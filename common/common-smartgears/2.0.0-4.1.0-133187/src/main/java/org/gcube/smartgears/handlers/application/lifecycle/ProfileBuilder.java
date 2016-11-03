package org.gcube.smartgears.handlers.application.lifecycle;

import java.net.URI;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import javax.servlet.ServletRegistration;

import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.common.resources.gcore.HostingNode;
import org.gcube.smartgears.configuration.application.ApplicationConfiguration;
import org.gcube.smartgears.configuration.container.ContainerConfiguration;
import org.gcube.smartgears.context.application.ApplicationContext;

public class ProfileBuilder {

	private static List<String> servletExcludes = Arrays.asList("default","jsp");

	private ApplicationContext context;

	public ProfileBuilder(ApplicationContext context) {
		this.context = context;
	}

	public void fill(GCoreEndpoint endpoint) {


		ApplicationConfiguration configuration = context.configuration();
		ContainerConfiguration container = context.container().configuration();


		endpoint.profile()
		.description(configuration.description())
		.serviceName(configuration.name())
		.serviceClass(configuration.serviceClass())
		.version(configuration.version())
		.serviceId(configuration.name() + configuration.serviceClass() + configuration.version())
		.ghnId(context.container().profile(HostingNode.class).id());

		endpoint.profile().newDeploymentData()
		.activationTime(Calendar.getInstance())
		.status((context.lifecycle().state().remoteForm()));

		endpoint.profile().endpoints().clear();

		String baseAddress =  "http://"+container.hostname()+":"+container.port()+context.application().getContextPath();

		String secureBaseAddress= null;
		if (context.configuration().secure())
			secureBaseAddress= "https://"+container.hostname()+":"+container.securePort()+context.application().getContextPath();

		for (ServletRegistration servlet : context.application().getServletRegistrations().values())
			if (!servletExcludes.contains(servlet.getName()))
				for (String mapping : servlet.getMappings()) {
					String address;
					if (secureBaseAddress ==null || context.configuration().excludes().contains(mapping))
						address = baseAddress+(mapping.endsWith("*")?mapping.substring(0,mapping.length()-2):mapping);
					else 
						address = secureBaseAddress+(mapping.endsWith("*")?mapping.substring(0,mapping.length()-2):mapping);
					endpoint.profile().endpoints().add().nameAndAddress(servlet.getName(),URI.create(address));
				}


	}

}
