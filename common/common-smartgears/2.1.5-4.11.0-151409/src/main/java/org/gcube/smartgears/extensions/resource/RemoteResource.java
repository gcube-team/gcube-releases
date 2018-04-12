package org.gcube.smartgears.extensions.resource;

import static org.gcube.smartgears.Constants.*;

import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.smartgears.Constants;
import org.gcube.smartgears.extensions.ApiResource;
import org.gcube.smartgears.extensions.HttpController;

/**
 * An {@link HttpController} for remote management of the application.
 * 
 * @author Fabio Simeoni
 * 
 */
@XmlRootElement(name = remote_management)
public class RemoteResource extends HttpController {

	private static final String default_mapping = Constants.root_mapping+"/*";

	private static final long serialVersionUID = 1L;

	/**
	 * Creates an instance with its {@link ApiResource}s.
	 */
	public RemoteResource() {
		super(remote_management, default_mapping);
		addResources(new FrontPageResource(), new ConfigurationResource(), new ProfileResource(),
				new LifecycleResource());
	}

	@Override
	public String toString() {
		return remote_management;
	}
}
