package org.gcube.data.tm.state;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBContext;

import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.informationsystem.publisher.ISPublisher;
import org.gcube.common.core.resources.GCUBEGenericResource;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.security.GCUBESecurityManager;
import org.gcube.common.core.state.GCUBEWSResource;
import org.gcube.common.core.state.GCUBEWSResourceKey;
import org.gcube.common.core.types.DescriptiveProperty;
import org.gcube.common.core.utils.handlers.GCUBEHandler;
import org.gcube.common.core.utils.handlers.GCUBEScheduledHandler.Mode;
import org.gcube.data.tm.Constants;
import org.gcube.data.tm.activationrecord.ActivationRecord;
import org.gcube.data.tm.context.ServiceContext;
import org.gcube.data.tm.context.TReaderContext;
import org.gcube.data.tm.context.TWriterContext;
import org.gcube.data.tm.plugin.DefaultSourceNotifier;
import org.gcube.data.tm.plugin.PluginEnvironment;
import org.gcube.data.tm.plugin.PluginManager;
import org.gcube.data.tm.publishers.ResilientScheduler;
import org.gcube.data.tm.stubs.PluginDescription;
import org.gcube.data.tm.stubs.SourceBinding;
import org.gcube.data.tmf.api.Plugin;
import org.gcube.data.tmf.api.Property;
import org.gcube.data.tmf.api.Source;
import org.gcube.data.tmf.api.SourceBinder;
import org.gcube.data.tmf.api.exceptions.InvalidRequestException;
import org.globus.wsrf.NoSuchResourceException;
import org.globus.wsrf.ResourceException;
import org.globus.wsrf.ResourceProperty;
import org.w3c.dom.Element;

/**
 * The singleton {@link GCUBEWSResource} of the T-Binder service.
 * 
 * @author Fabio Simeoni
 */
public class TBinderResource extends GCUBEWSResource {

	private static ISPublisher publisher;
	private static JAXBContext jaxbContext;

	static {
		try {
			publisher = GHNContext.getImplementation(ISPublisher.class);
			jaxbContext = JAXBContext.newInstance(SourceInfo.class);
		} catch (Throwable e) {
			throw new RuntimeException("cannot initialise T-Binder resource", e);
		}
	}

	/** RP names. */
	private static String[] RPNames = { Constants.BINDER_PLUGIN_RPNAME };

	// available at load time
	Set<String> activations = new HashSet<String>();

	/** {@inheritDoc} */
	protected void initialise(Object... args) throws Exception,IllegalArgumentException {
		setPluginProperty();
	}

	void setPluginProperty() {
		
		ResourceProperty typeRP = this.getResourcePropertySet().get(
				Constants.BINDER_PLUGIN_RPNAME);
		typeRP.clear();
		for (Plugin plugin : new PluginManager().plugins().values()) {// prepare
																		// task
																		// description

			PluginDescription description = new PluginDescription();

			description.setName(plugin.name());
			description.setDescription(plugin.description());

			List<Property> properties = plugin.properties();
			
			
			if (properties != null) {
				List<DescriptiveProperty> descriptiveProperties = new ArrayList<DescriptiveProperty>();
				for (Property prop : plugin.properties()) {
					DescriptiveProperty property = new DescriptiveProperty(
							prop.description(), prop.name(), prop.value());
					descriptiveProperties.add(property);
				}
				description.setProperty(descriptiveProperties
						.toArray(new DescriptiveProperty[0]));
			}
		
			// 'serialise' description as property
			List<String> schemas = plugin.requestSchemas();
				if (schemas!=null) {
				List<String> descriptionschemas = new ArrayList<String>();
				for (String schema : schemas)
					descriptionschemas.add(schema);
				description.setSchema(descriptionschemas.toArray(new String[0]));
	
				typeRP.add(description);
			}
		}
	}

	/** {@inheritDoc} */
	public String[] getPropertyNames() {
		return RPNames;
	}

	/**
	 * Memorises the processing of an {@link ActivationRecord}.
	 * 
	 * @param record
	 *            the record
	 * @return <code>true</code> if the record was never processed before,
	 *         <code>false</code> otherwise
	 */
	public synchronized boolean addActivation(ActivationRecord record) {
		String id = record.getResource().getID();
		if (activations.contains(id))
			return false;
		activations.add(id);
		store();
		return true;
	}

	/**
	 * Indicates whether an activation record was previously processed.
	 * 
	 * @param record
	 *            the record
	 * @return <code>true</code> if the record was never processed before,
	 *                          <code>false</code> otherwise
	 */
	public synchronized boolean knowsActivation(ActivationRecord record) {
		return activations.contains(record.getResource().getID());
	}

	/**
	 * Returns one or more {@link SourceBinding}s from client requests to a
	 * plugin.
	 * 
	 * @param plugin
	 *            the name of the plugin
	 * @param request
	 *            the request
	 * @return the bindings
	 * @throws InvalidRequestException
	 *             if the requests are malformed or unexpected
	 * @throws Exception
	 *             if the operation fails for any other error
	 */
	public List<SourceBinding> bind(String plugin, Element request)
			throws InvalidRequestException, Exception {

		if (plugin == null || request == null)
			throw new InvalidRequestException("plugin or requests are null");

		Plugin pluginContext = getPlugin(plugin);

		SourceBinder binder = pluginContext.binder();

		// dispatch creation to delegate
		List<? extends Source> sources = binder.bind(request);

		if (sources == null || sources.isEmpty())
			throw new Exception("plugin has bound no source");

		// prepare output
		List<SourceBinding> bindings = new ArrayList<SourceBinding>();

		// accumulate errors that may follow
		List<Exception> exceptions = new ArrayList<Exception>();

		for (Source source : sources) {

			boolean newReader = source.reader() != null;
			boolean newWriter = source.writer() != null;

			try {

				// REUSE CASE: does the source already exist?

				try {
					SourceResource resource = (SourceResource) TReaderContext
							.getContext().getLocalHome().find(source.id());

					Source existingSource = resource.source();
					
					//update info
					newReader=existingSource.reader()!=null;
					newWriter=existingSource.writer()!=null;
					
					existingSource.lifecycle().reconfigure(request);
					

					logger.trace("reconfiguring source " + source.id());

					// use existing source in what follows
					source = existingSource;

				}

				// NEW CASE
				catch (NoSuchResourceException tolerate) {

					if (!newReader && !newWriter)
						throw new Exception(
								"plugin has not bound any reader or writer to the source");

					logger.trace("initializing source " + source.id());

					// injecting in source
					source.setEnvironment(new PluginEnvironment());
					source.setNotifier(new DefaultSourceNotifier());

					source.lifecycle().init();
				}

				// build output for this source
				SourceBinding binding = new SourceBinding();
				binding.setSourceID(source.id());

				// create now read or/and write resource now in call scope
				boolean isNewScope = true;

				if (newReader) {

					// does resource already exist in call scope?
					TReaderContext context = TReaderContext.getContext();
					GCUBEWSResourceKey key = context.makeKey(source.id());
					TReaderResource reader = null;
					try {
						reader = (TReaderResource) context.getWSHome()
								.find(key);
						// existed in current scope? then no scope change at all
						isNewScope = false;
					} catch (NoSuchResourceException e) {
						// did not exist? then create and store in new scope
						reader = (TReaderResource) context.getWSHome().create(
								key, source, plugin);
					}

					// we store in all cases, even if source had
					// null-reconfiguration
					reader.store();
					binding.setReaderEndpoint(reader.getEPR());
				}

				// do same for writer, if necessary
				if (newWriter) {

					TWriterContext context = TWriterContext.getContext();
					GCUBEWSResourceKey key = context.makeKey(source.id());
					TWriterResource writer = null;
					try {
						writer = (TWriterResource) context.getWSHome()
								.find(key);
						isNewScope = isNewScope || false;
					} catch (NoSuchResourceException e) {
						writer = (TWriterResource) context.getWSHome().create(
								key, source, plugin);
					}

					// we store in all cases, even if source had
					// null-reconfiguration
					writer.store();
					binding.setWriterEndpoint(writer.getEPR());
				}

				bindings.add(binding);

				// if we've published some resource in a new scope, we need to
				// publish
				// source profile there too.
				if (isNewScope)
					publishProfile(source, getScope(),
							ServiceContext.getContext());

			} catch (Exception e) {// accumulate per-source errors
				exceptions.add(e);
			}

		}

		// we need to have succeeded at least with one source
		if (bindings.size() > 0)
			for (Exception e : exceptions)
				logger.error("cannot not process bind request", e);
		// otherwise throw the first error (cannot be silent if there is no
		// positive outcome at all)
		else
			throw exceptions.get(0);

		return bindings;
	}

	private void publishProfile(final Source source, final GCUBEScope scope,
			final GCUBESecurityManager manager) throws Exception {

		ResilientScheduler scheduler = new ResilientScheduler(1L, Mode.LAZY);
		scheduler.setAttempts(Constants.MAX_SOURCEPROFILE_PUBLICATION_ATTEMPTS);
		scheduler.setDelay(10L);
		scheduler.setName("sourceProfilePublisher");
		scheduler.setScheduled(new GCUBEHandler<Void>() {

			/** {@inheritDoc} */
			@Override
			public void run() throws Exception {
				GCUBEGenericResource profile = GHNContext
						.getImplementation(GCUBEGenericResource.class);
				profile.setID(source.id());
				profile.setSecondaryType("DataSource");
				profile.setName(source.name());
				profile.setDescription(source.description());
				profile.addScope(scope);

				SourceInfo body = new SourceInfo();
				body.setCreationTime(source.creationTime());
				body.setUser(source.isUser());

				StringWriter writer = new StringWriter();
				jaxbContext.createMarshaller().marshal(body, writer);
				profile.setBody(writer.toString());

				logger.debug("publishing profile of source " + source.id()
						+ " in scope " + scope);
				if (GHNContext.getContext().getMode() == GHNContext.Mode.CONNECTED)
					publisher.registerGCUBEResource(profile, scope, manager);
			}
		});

		scheduler.run();

	}

	/**
	 * Deletes all the T-Reader and T-Writers associated with a given source
	 * and, optionally,in one or more scopes.
	 * 
	 * @param sourceId
	 *            the source identifier
	 * @param scopes the scopes. If omitted, the accessors are removed in all their scopes
	 * @throws UnsupportedOperationException
	 *             if the plugin does not support source deletion
	 * @throws Exception
	 *             if the operation fails for an unexpected error
	 */
	public void deleteAccessors(String sourceId, GCUBEScope... scopes)
			throws Exception {

		try {
			TReaderContext readContext = TReaderContext.getContext();
			TReaderHome readHome = (TReaderHome) readContext.getWSHome();
			GCUBEWSResourceKey key = readContext.makeKey(sourceId);
			List<GCUBEScope> scopes2 = null;
			if (scopes.length > 0)
				scopes2 = Arrays.asList(scopes);
			else {
				scopes2 = new ArrayList<GCUBEScope>();
				TReaderResource reader = (TReaderResource) readHome.find(key);
				for (String scope : reader.getResourcePropertySet().getScope())
					scopes2.add(GCUBEScope.getScope(scope));
			}
			logger.trace("removing reader for " + sourceId);
			for (GCUBEScope scope : scopes2) {
				ServiceContext.getContext().setScope(scope);
				readHome.remove(key);
			}
		} catch (ResourceException tolerate) {
			// may well not exist at all
		}

		try {
			TWriterContext writeContext = TWriterContext.getContext();
			TWriterHome writeHome = (TWriterHome) writeContext.getWSHome();
			GCUBEWSResourceKey key = writeContext.makeKey(sourceId);
			List<GCUBEScope> scopes2 = null;
			if (scopes.length > 0)
				scopes2 = Arrays.asList(scopes);
			else {
				scopes2 = new ArrayList<GCUBEScope>();
				TWriterResource reader = (TWriterResource) writeHome.find(key);
				for (String scope : reader.getResourcePropertySet().getScope())
					scopes2.add(GCUBEScope.getScope(scope));

			}
			logger.trace("removing writer for " + sourceId);
			for (GCUBEScope scope : scopes2) {
				ServiceContext.getContext().setScope(scope);
				writeHome.remove(key);
			}
		} catch (ResourceException tolerate) {
			// may well not exist at all
		}

	}

	/**
	 * Returns the context of a plugin with a given name.
	 * 
	 * @param name
	 *            the name.
	 * @return the context, or <code>null</code> if the plugin is unknown.
	 */
	public Plugin getPlugin(String name) throws Exception {
		Plugin plugin = new PluginManager().plugins().get(name);
		if (plugin == null)
			throw new InvalidRequestException("plugin " + name
					+ " is unknown");
		return plugin;
	}

}
