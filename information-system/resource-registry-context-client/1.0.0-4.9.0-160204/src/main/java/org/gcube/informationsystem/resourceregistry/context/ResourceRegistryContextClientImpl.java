package org.gcube.informationsystem.resourceregistry.context;

import java.io.StringWriter;
import java.net.MalformedURLException;
import java.util.List;
import java.util.UUID;

import org.gcube.informationsystem.impl.utils.ISMapper;
import org.gcube.informationsystem.model.entity.Context;
import org.gcube.informationsystem.resourceregistry.api.exceptions.ResourceRegistryException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.context.ContextAlreadyPresentException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.context.ContextNotFoundException;
import org.gcube.informationsystem.resourceregistry.api.rest.ContextPath;
import org.gcube.informationsystem.resourceregistry.api.rest.httputils.HTTPCall;
import org.gcube.informationsystem.resourceregistry.api.rest.httputils.HTTPCall.HTTPMETHOD;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class ResourceRegistryContextClientImpl implements ResourceRegistryContextClient {

	private static final Logger logger = LoggerFactory.getLogger(ResourceRegistryContextClientImpl.class);

	public static final String PATH_SEPARATOR = "/";

	protected final String address;
	protected HTTPCall httpCall;

	public ResourceRegistryContextClientImpl(String address) {
		this.address = address;

	}

	private HTTPCall getHTTPCall() throws MalformedURLException {
		if (httpCall == null) {
			httpCall = new HTTPCall(address, ResourceRegistryContextClient.class.getSimpleName());
		}
		return httpCall;
	}


	@Override
	public Context create(Context context) throws ContextAlreadyPresentException, ResourceRegistryException {
		try {
			String contextString = ISMapper.marshal(context);
			String res = create(contextString);
			return ISMapper.unmarshal(Context.class, res);
		} catch (ResourceRegistryException e) {
			// logger.trace("Error Creating {}", facet, e);
			throw e;
		} catch (Exception e) {
			// logger.trace("Error Creating {}", facet, e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public String create(String context) throws ContextAlreadyPresentException, ResourceRegistryException {
		try {
			logger.trace("Going to create: {}", context);
			StringWriter stringWriter = new StringWriter();
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(ContextPath.CONTEXT_PATH_PART);

			HTTPCall httpCall = getHTTPCall();
			String c = httpCall.call(String.class, stringWriter.toString(), HTTPMETHOD.PUT, context);

			logger.trace("{} successfully created", c);
			return c;

		} catch (ResourceRegistryException e) {
			// logger.trace("Error Creating {}", facet, e);
			throw e;
		} catch (Exception e) {
			// logger.trace("Error Creating {}", facet, e);
			throw new RuntimeException(e);
		}
	}
	

	@Override
	public Context read(Context context) throws ContextNotFoundException, ResourceRegistryException {
		return read(context.getHeader().getUUID());
	}

	@Override
	public Context read(UUID uuid) throws ContextNotFoundException, ResourceRegistryException {
		try {
			String res = read(uuid.toString());
			return ISMapper.unmarshal(Context.class, res);
		} catch (ResourceRegistryException e) {
			// logger.trace("Error Creating {}", facet, e);
			throw e;
		} catch (Exception e) {
			// logger.trace("Error Creating {}", facet, e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public String read(String uuid) throws ContextNotFoundException, ResourceRegistryException {
		try {
			logger.trace("Going to read {} with UUID {}", Context.NAME);
			StringWriter stringWriter = new StringWriter();
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(ContextPath.CONTEXT_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(uuid);

			HTTPCall httpCall = getHTTPCall();
			String c = httpCall.call(String.class, stringWriter.toString(), HTTPMETHOD.GET);

			logger.debug("Got {} is {}", Context.NAME, c);
			return c;

		} catch (ResourceRegistryException e) {
			// logger.trace("Error Creating {}", facet, e);
			throw e;
		} catch (Exception e) {
			// logger.trace("Error Creating {}", facet, e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public Context update(Context context) throws ContextAlreadyPresentException, ResourceRegistryException {
		try {
			String contextString = ISMapper.marshal(context);
			String res = update(contextString);
			return ISMapper.unmarshal(Context.class, res);
		} catch (ResourceRegistryException e) {
			// logger.trace("Error Updating {}", facet, e);
			throw e;
		} catch (Exception e) {
			// logger.trace("Error Updating {}", facet, e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public String update(String context) throws ContextAlreadyPresentException, ResourceRegistryException {
		try {
			logger.trace("Going to update: {}", context);
			StringWriter stringWriter = new StringWriter();
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(ContextPath.CONTEXT_PATH_PART);

			HTTPCall httpCall = getHTTPCall();
			String c = httpCall.call(String.class, stringWriter.toString(), HTTPMETHOD.POST, context);

			logger.trace("{} successfully updated", c);
			return c;

		} catch (ResourceRegistryException e) {
			// logger.trace("Error Creating {}", facet, e);
			throw e;
		} catch (Exception e) {
			// logger.trace("Error Creating {}", facet, e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean delete(Context context) throws ContextNotFoundException, ResourceRegistryException {
		return delete(context.getHeader().getUUID());
	}

	@Override
	public boolean delete(UUID uuid) throws ContextNotFoundException, ResourceRegistryException {
		return delete(uuid.toString());
	}

	@Override
	public boolean delete(String uuid) throws ContextNotFoundException, ResourceRegistryException {
		try {
			logger.trace("Going to delete {} with UUID {}", Context.NAME, uuid);
			StringWriter stringWriter = new StringWriter();
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(ContextPath.CONTEXT_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(uuid);

			HTTPCall httpCall = getHTTPCall();
			boolean deleted = httpCall.call(Boolean.class, stringWriter.toString(), HTTPMETHOD.DELETE);

			logger.info("{} with UUID {} {}", Context.NAME, uuid,
					deleted ? " successfully deleted" : "was NOT deleted");
			return deleted;
		} catch (ResourceRegistryException e) {
			// logger.trace("Error Creating {}", facet, e);
			throw e;
		} catch (Exception e) {
			// logger.trace("Error Creating {}", facet, e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<Context> all() throws ResourceRegistryException {
		try {
			logger.trace("Going to read {} with UUID {}", Context.NAME);
			StringWriter stringWriter = new StringWriter();
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(ContextPath.CONTEXT_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(ContextPath.ALL_PATH_PART);

			HTTPCall httpCall = getHTTPCall();
			String all = httpCall.call(String.class, stringWriter.toString(), HTTPMETHOD.GET);

			logger.debug("Got contexts are {}", Context.NAME, all);
			return ISMapper.unmarshalList(Context.class, all);

		} catch (ResourceRegistryException e) {
			// logger.trace("Error Creating {}", facet, e);
			throw e;
		} catch (Exception e) {
			// logger.trace("Error Creating {}", facet, e);
			throw new RuntimeException(e);
		}
	}


}
