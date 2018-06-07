package org.gcube.data.spd.resources;

import static org.gcube.data.streams.dsl.Streams.convert;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.gcube.common.authorization.library.AuthorizedTasks;
import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.data.spd.exception.MaxRetriesReachedException;
import org.gcube.data.spd.manager.AppInitializer;
import org.gcube.data.spd.manager.OccurrenceWriterManager;
import org.gcube.data.spd.model.Constants;
import org.gcube.data.spd.model.exceptions.ExternalRepositoryException;
import org.gcube.data.spd.model.exceptions.IdNotValidException;
import org.gcube.data.spd.model.exceptions.StreamBlockingException;
import org.gcube.data.spd.model.products.OccurrencePoint;
import org.gcube.data.spd.model.service.types.MultiLocatorResponse;
import org.gcube.data.spd.plugin.PluginManager;
import org.gcube.data.spd.plugin.fwk.AbstractPlugin;
import org.gcube.data.spd.plugin.fwk.capabilities.OccurrencesCapability;
import org.gcube.data.spd.plugin.fwk.readers.LocalReader;
import org.gcube.data.spd.plugin.fwk.util.Util;
import org.gcube.data.spd.plugin.fwk.writers.Writer;
import org.gcube.data.spd.plugin.fwk.writers.rswrapper.AbstractWrapper;
import org.gcube.data.spd.plugin.fwk.writers.rswrapper.LocalWrapper;
import org.gcube.data.spd.plugin.fwk.writers.rswrapper.ResultWrapper;
import org.gcube.data.spd.utils.DynamicMap;
import org.gcube.data.spd.utils.ExecutorsContainer;
import org.gcube.data.spd.utils.QueryRetryCall;
import org.gcube.data.spd.utils.ResultWrapperMantainer;
import org.gcube.data.spd.utils.VOID;
import org.gcube.data.streams.Stream;
import org.gcube.smartgears.ApplicationManagerProvider;
import org.gcube.smartgears.ContextProvider;
import org.gcube.smartgears.context.application.ApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("occurrence")
public class Occurrences{

	Logger logger = LoggerFactory.getLogger(Occurrences.class);

	ApplicationContext ctx = ContextProvider.get();

	AppInitializer initializer = (AppInitializer) ApplicationManagerProvider.get(AppInitializer.class);

	public enum ExecType {
		IDS, 
		KEYS
	} 


	@GET
	@Path("keys")
	public Response getByKeys() {
		try{
			
			String inputLocatorId = UUID.randomUUID().toString();
			DynamicMap.put(inputLocatorId);
			
			logger.trace("locator used as input is {} ",inputLocatorId);
			
			Stream<String> reader = convert(DynamicMap.get(inputLocatorId));

			ResultWrapper<OccurrencePoint> wrapper = ResultWrapperMantainer.getWrapper(OccurrencePoint.class);

			ExecutorsContainer.execJob(AuthorizedTasks.bind(new RunnableOccurrenceSearch(reader, wrapper, ExecType.KEYS)));

			// the output will be probably returned even before
			// a first chunk is written by the new thread
			StringBuilder redirectUri = new StringBuilder();
			redirectUri.append("http://").append(ctx.container().configuration().hostname()).append(":").append(ctx.container().configuration().port());
			redirectUri.append(ctx.application().getContextPath()).append(Constants.APPLICATION_ROOT_PATH).append("/").append(Constants.RESULTSET_PATH).append("/").append(wrapper.getLocator());
			
			logger.trace("redirect uri is {} ",redirectUri.toString());
			try{
				MultiLocatorResponse multiLocatorResponse = new MultiLocatorResponse(wrapper.getLocator(), inputLocatorId, ctx.profile(GCoreEndpoint.class).id());
				logger.trace("retrnign multilocator {}",multiLocatorResponse);
				return Response.temporaryRedirect(new URI(redirectUri.toString())).entity(multiLocatorResponse).build();
			}catch(Exception e){
				logger.error("invalid redirect uri created",e);
				return Response.serverError().build();
			}
		}catch (Exception e) {
			logger.error("error getting occurrences by ids",e);
			throw new RuntimeException(e);
		}
	}

	@GET
	@Path("ids")
	public Response getByIds(){
		try{
			
			String inputLocatorId = UUID.randomUUID().toString();
			DynamicMap.put(inputLocatorId);
			
			Stream<String> reader = convert(DynamicMap.get(inputLocatorId));
			
			ResultWrapper<OccurrencePoint> wrapper = ResultWrapperMantainer.getWrapper(OccurrencePoint.class);
			ExecutorsContainer.execJob(AuthorizedTasks.bind(new RunnableOccurrenceSearch(reader, wrapper, ExecType.IDS)));
			// the output will be probably returned even before
			// a first chunk is written by the new thread
			StringBuilder redirectUri = new StringBuilder();
			redirectUri.append("http://").append(ctx.container().configuration().hostname()).append(":").append(ctx.container().configuration().port());
			redirectUri.append(ctx.application().getContextPath()).append(Constants.APPLICATION_ROOT_PATH).append("/").append(Constants.RESULTSET_PATH).append("/").append(wrapper.getLocator());
			logger.trace("redirect uri is {} ",redirectUri.toString());
			try{
				MultiLocatorResponse multiLocatorResponse = new MultiLocatorResponse(wrapper.getLocator(), inputLocatorId, ctx.profile(GCoreEndpoint.class).id());
				return Response.temporaryRedirect(new URI(redirectUri.toString())).entity(multiLocatorResponse).build();
			}catch(Exception e){
				logger.error("invalid redirect uri created",e);
				return Response.serverError().build();
			}
		}catch (Exception e) {
			logger.error("error getting occurrences by ids");
			throw new RuntimeException(e);
		}		
	}
	
	
	public class RunnableOccurrenceSearch implements Runnable{

		private Stream<String> reader;
		private ResultWrapper<OccurrencePoint> wrapper;
		private ExecType execType;

		public RunnableOccurrenceSearch(Stream<String> reader,
				ResultWrapper<OccurrencePoint> wrapper, ExecType execType) {
			super();
			this.reader = reader;
			this.wrapper = wrapper;
			this.execType = execType;
		}

		@Override
		public void run(){
			Map<String, Writer<String>> pluginMap= new HashMap<String, Writer<String>>();
			while (reader.hasNext()){
				String key = reader.next();
				try{
					final String provider = Util.getProviderFromKey(key);
					String id = Util.getIdFromKey(key);
					logger.trace("key arrived "+id+" for provider "+provider);
					if (!pluginMap.containsKey(provider)){
						final LocalWrapper<String> localWrapper = new LocalWrapper<String>();
						Writer<String> localWriter = new Writer<String>(localWrapper);
						//localWriter.register();
						pluginMap.put(provider, localWriter);
						if (execType == ExecType.KEYS) 
							ExecutorsContainer.execSearch(AuthorizedTasks.bind(new RunnableOccurrenceByKeys(provider, wrapper, localWrapper)));
						else ExecutorsContainer.execSearch(AuthorizedTasks.bind(new RunnableOccurrenceByIds(provider, wrapper, localWrapper)));
					} 
					logger.trace("key put "+id+"? "+( pluginMap.get(provider).write(id)));
				}catch (IdNotValidException e) {
					logger.warn("the key "+key+" is not valid");
				}
			}
			logger.trace("is wrapper closed? "+wrapper.isClosed());
			if (pluginMap.values().isEmpty()) 
				wrapper.close();
			else 
				for (Writer<String> entry : pluginMap.values())
					entry.close();
			reader.close();
		}



	}

	public class RunnableOccurrenceByKeys implements Runnable{

		private String provider;
		private AbstractWrapper<OccurrencePoint> wrapper;
		private LocalWrapper<String> localWrapper;



		public RunnableOccurrenceByKeys(String provider,
				AbstractWrapper<OccurrencePoint> wrapper, 
				LocalWrapper<String> localWrapper) {
			super();
			this.provider = provider;
			this.wrapper = wrapper;
			this.localWrapper = localWrapper;
		}



		@Override
		public void run(){
			logger.trace("call to provider "+provider);
			final Writer<OccurrencePoint> writer = new Writer<OccurrencePoint>(wrapper, new OccurrenceWriterManager(provider));
			writer.register();
			try {
				new QueryRetryCall(){

					@Override
					protected VOID execute() throws ExternalRepositoryException {
						PluginManager pm = initializer.getPluginManager();
						AbstractPlugin plugin = pm.plugins().get(provider);
						OccurrencesCapability oc = plugin.getOccurrencesInterface();
						oc.getOccurrencesByProductKeys(writer, new LocalReader<String>(localWrapper));
						return VOID.instance();
					}

				}.call();
			} catch (MaxRetriesReachedException e) {
				writer.write(new StreamBlockingException(provider));
			}
			writer.close();
			logger.trace("writer is closed ? "+(!writer.isAlive()));
		}

	}

	public class RunnableOccurrenceByIds implements Runnable{

		private String provider;
		private AbstractWrapper<OccurrencePoint> wrapper;
		private LocalWrapper<String> localWrapper;



		public RunnableOccurrenceByIds(String provider,
				AbstractWrapper<OccurrencePoint> wrapper, 
				LocalWrapper<String> localWrapper) {
			super();
			this.provider = provider;
			this.wrapper = wrapper;
			this.localWrapper = localWrapper;
		}



		@Override
		public void run(){
			logger.trace("call to provider "+provider);
			final Writer<OccurrencePoint> writer = new Writer<OccurrencePoint>(wrapper, new OccurrenceWriterManager(provider));
			writer.register();
			try {
				new QueryRetryCall(){

					@Override
					protected VOID execute() throws ExternalRepositoryException {
						PluginManager pm = initializer.getPluginManager();
						AbstractPlugin plugin = pm.plugins().get(provider);
						OccurrencesCapability oc = plugin.getOccurrencesInterface();
						oc.getOccurrencesByIds(writer, new LocalReader<String>(localWrapper));						return VOID.instance();
					}

				}.call();
			} catch (MaxRetriesReachedException e) {
				writer.write(new StreamBlockingException(provider));
			}

			writer.close();
		}

	}

}
