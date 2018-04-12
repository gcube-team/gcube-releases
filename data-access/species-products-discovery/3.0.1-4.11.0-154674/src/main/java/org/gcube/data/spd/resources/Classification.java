package org.gcube.data.spd.resources;

import static org.gcube.data.streams.dsl.Streams.convert;
import static org.gcube.data.streams.dsl.Streams.pipe;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.data.spd.exception.MaxRetriesReachedException;
import org.gcube.data.spd.manager.AppInitializer;
import org.gcube.data.spd.manager.TaxonomyItemWriterManager;
import org.gcube.data.spd.model.Constants;
import org.gcube.data.spd.model.exceptions.ExternalRepositoryException;
import org.gcube.data.spd.model.exceptions.IdNotValidException;
import org.gcube.data.spd.model.exceptions.MethodNotSupportedException;
import org.gcube.data.spd.model.exceptions.StreamBlockingException;
import org.gcube.data.spd.model.exceptions.StreamNonBlockingException;
import org.gcube.data.spd.model.products.TaxonomyItem;
import org.gcube.data.spd.model.service.exceptions.InvalidIdentifierException;
import org.gcube.data.spd.model.service.exceptions.UnsupportedCapabilityException;
import org.gcube.data.spd.model.service.exceptions.UnsupportedPluginException;
import org.gcube.data.spd.model.service.types.MultiLocatorResponse;
import org.gcube.data.spd.model.util.Capabilities;
import org.gcube.data.spd.plugin.PluginManager;
import org.gcube.data.spd.plugin.fwk.AbstractPlugin;
import org.gcube.data.spd.plugin.fwk.readers.LocalReader;
import org.gcube.data.spd.plugin.fwk.util.Util;
import org.gcube.data.spd.plugin.fwk.writers.ObjectWriter;
import org.gcube.data.spd.plugin.fwk.writers.Writer;
import org.gcube.data.spd.plugin.fwk.writers.rswrapper.LocalWrapper;
import org.gcube.data.spd.plugin.fwk.writers.rswrapper.ResultWrapper;
import org.gcube.data.spd.utils.ExecutorsContainer;
import org.gcube.data.spd.utils.JobRetryCall;
import org.gcube.data.spd.utils.QueryRetryCall;
import org.gcube.data.spd.utils.ResultWrapperMantainer;
import org.gcube.data.spd.utils.VOID;
import org.gcube.data.streams.Stream;
import org.gcube.data.streams.delegates.PipedStream;
import org.gcube.smartgears.ApplicationManagerProvider;
import org.gcube.smartgears.ContextProvider;
import org.gcube.smartgears.context.application.ApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("taxon")
public class Classification {

	private static Logger logger = LoggerFactory.getLogger(Classification.class);

	AppInitializer initializer = (AppInitializer)ApplicationManagerProvider.get(AppInitializer.class);

	ApplicationContext ctx = ContextProvider.get();

	@GET
	@Path("children/{key}")
	public Response retrieveTaxonChildrenByTaxonId(@PathParam("key") String key) throws UnsupportedPluginException,UnsupportedCapabilityException, InvalidIdentifierException {
		try{
			logger.trace("calling get taxon childs by id");
			PluginManager manager = initializer.getPluginManager();
			String pluginName = Util.getProviderFromKey(key);
			String id = Util.getIdFromKey(key);
			if (!manager.plugins().containsKey(pluginName))
				throw new UnsupportedPluginException();
			AbstractPlugin plugin = manager.plugins().get(pluginName);	
			if (!plugin.getSupportedCapabilities().contains(Capabilities.Classification)) throw new UnsupportedCapabilityException();
			try {
				logger.trace("retirievng list of taxon item");
				List<TaxonomyItem> taxonChilds = plugin.getClassificationInterface().retrieveTaxonChildrenByTaxonId(id);
				logger.trace("taxon item found are "+taxonChilds.size());
				Stream<TaxonomyItem> taxonStream =convert(taxonChilds);
				PipedStream<TaxonomyItem, TaxonomyItem> pipedTaxa = pipe(taxonStream).through(new TaxonomyItemWriterManager(plugin.getRepositoryName()));

				ResultWrapper<TaxonomyItem> wrapper = ResultWrapperMantainer.getWrapper(TaxonomyItem.class);

				while (pipedTaxa.hasNext())
					wrapper.add(pipedTaxa.next());


				// the output will be probably returned even before
				// a first chunk is written by the new thread
				StringBuilder redirectUri = new StringBuilder();
				redirectUri.append("http://").append(ctx.container().configuration().hostname()).append(":").append(ctx.container().configuration().port());
				redirectUri.append(ctx.application().getContextPath()).append(Constants.APPLICATION_ROOT_PATH).append("/").append(Constants.RESULTSET_PATH).append("/").append(wrapper.getLocator());
				logger.trace("redirect uri is {} ",redirectUri.toString());
				try{
					MultiLocatorResponse multiLocatorResponse = new MultiLocatorResponse( wrapper.getLocator(), null, ctx.profile(GCoreEndpoint.class).id());
					return Response.temporaryRedirect(new URI(redirectUri.toString())).entity(multiLocatorResponse).build();
				}catch(Exception e){
					logger.error("invalid redirect uri created",e);
					return Response.serverError().build();
				}

			} catch (IdNotValidException e) {
				logger.error("the id "+id+" is not valid",e );
				throw new IdNotValidException();
			}
		}catch (Throwable e) {
			logger.error("error getting TaxonByid",e);
			throw new RuntimeException(e);
		}
	}

	@GET
	@Path("tree/{key}")
	public Response retrieveChildrenTreeById(@PathParam("key") final String key) throws  UnsupportedPluginException,UnsupportedCapabilityException, InvalidIdentifierException{
		PluginManager manager = initializer.getPluginManager();

		try{
			String pluginName = Util.getProviderFromKey(key);
			final String id = Util.getIdFromKey(key);
			if (!manager.plugins().containsKey(pluginName))
				throw new UnsupportedPluginException();
			final AbstractPlugin plugin = manager.plugins().get(pluginName);	
			if (!plugin.getSupportedCapabilities().contains(Capabilities.Classification)) throw new UnsupportedCapabilityException();

			final ResultWrapper<TaxonomyItem> wrapper = ResultWrapperMantainer.getWrapper(TaxonomyItem.class);

			final TaxonomyItem taxon= plugin.getClassificationInterface().retrieveTaxonById(id);
			ExecutorsContainer.execSearch(new Runnable() {
				@Override
				public void run(){
					Writer<TaxonomyItem> writer  = new Writer<TaxonomyItem>(wrapper,  new TaxonomyItemWriterManager(plugin.getRepositoryName()));
					writer.register();
					Classification.retrieveTaxaTree(writer, taxon, plugin);
					writer.close();
				}
			});

			// the output will be probably returned even before
			// a first chunk is written by the new thread
			StringBuilder redirectUri = new StringBuilder();
			redirectUri.append("http://").append(ctx.container().configuration().hostname()).append(":").append(ctx.container().configuration().port());
			redirectUri.append(ctx.application().getContextPath()).append(Constants.APPLICATION_ROOT_PATH).append("/").append(Constants.RESULTSET_PATH).append("/").append(wrapper.getLocator());
			logger.trace("redirect uri is {} ",redirectUri.toString());
			try{
				MultiLocatorResponse multiLocatorResponse = new MultiLocatorResponse(wrapper.getLocator(), null, ctx.profile(GCoreEndpoint.class).id());
				return Response.temporaryRedirect(new URI(redirectUri.toString())).entity(multiLocatorResponse).build();
			}catch(Exception e){
				logger.error("invalid redirect uri created",e);
				return Response.serverError().build();
			}

		}catch(IdNotValidException inve){
			logger.error("invalid id",inve);
			throw new InvalidIdentifierException(key);
		}catch (Exception e) {
			logger.error("error retrieve Children Tree By Id",e);
			throw new RuntimeException(e);
		}


	}

	@GET
	@Path("synonyms/{key}")
	public Response retrieveSynonymsById(@PathParam("key") String key) throws UnsupportedPluginException,UnsupportedCapabilityException, InvalidIdentifierException{
		try{
			PluginManager manager = initializer.getPluginManager();
			String pluginName = Util.getProviderFromKey(key);
			final String id = Util.getIdFromKey(key);
			if (!manager.plugins().containsKey(pluginName))
				throw new UnsupportedPluginException();
			final AbstractPlugin plugin = manager.plugins().get(pluginName);	
			if (!plugin.getSupportedCapabilities().contains(Capabilities.Classification)) throw new UnsupportedCapabilityException();

			final ResultWrapper<TaxonomyItem> wrapper = ResultWrapperMantainer.getWrapper(TaxonomyItem.class);
			
			ExecutorsContainer.execSearch(new Runnable() {
				@Override
				public void run(){
					Writer<TaxonomyItem> writer  = new Writer<TaxonomyItem>(wrapper,  new TaxonomyItemWriterManager(plugin.getRepositoryName()));
					writer.register();
					try {
						plugin.getClassificationInterface().getSynonymnsById(writer, id);
					} catch (MethodNotSupportedException e) {
						logger.error("error retrieving synonyms "+e);
					}  catch (Exception e) {
						logger.error("error retrieving synonyms "+e);
					}finally{
						writer.close();
					}
				}
			});

			// the output will be probably returned even before
			// a first chunk is written by the new thread
			StringBuilder redirectUri = new StringBuilder();
			redirectUri.append("http://").append(ctx.container().configuration().hostname()).append(":").append(ctx.container().configuration().port());
			redirectUri.append(ctx.application().getContextPath()).append(Constants.APPLICATION_ROOT_PATH).append("/").append(Constants.RESULTSET_PATH).append("/").append(wrapper.getLocator());
			logger.trace("redirect uri is {} ",redirectUri.toString());
			try{
				MultiLocatorResponse multiLocatorResponse = new MultiLocatorResponse(null, wrapper.getLocator(), ctx.profile(GCoreEndpoint.class).id());
				return Response.temporaryRedirect(new URI(redirectUri.toString())).entity(multiLocatorResponse).build();
			}catch(Exception e){
				logger.error("invalid redirect uri created",e);
				return Response.serverError().build();
			}
		}catch (IdNotValidException e) {
			logger.error("error retrieving children tree by id",e);
			throw new InvalidIdentifierException(key);
		}catch (Exception e1) {
			logger.error("error retrieving children tree by id",e1);
			throw new RuntimeException(e1);
		}
	}

	/*TODO: move to the new system
	@GET
	@PathParam("taxon/list/{idsLocator}")
	public String getTaxaByIds(@PathParam("idsLocator") String idsLocator) {
		try{
			logger.trace("calling get taxon by id with locator "+idsLocator);
			Stream<String> reader = convert(URI.create(idsLocator)).ofStrings().withDefaults();
			ResultWrapper<TaxonomyItem> wrapper = new ResultWrapper<TaxonomyItem>();
			logger.trace("starting the thread");
			ExecutorsContainer.execSearch(AuthorizedTasks.bind(new RunnableTaxonomySearch(reader, wrapper)));
			return wrapper.getLocator();
		}catch (Exception e) {
			throw new RuntimeException(e);
		}
	} 
	 */

	protected static void retrieveTaxaTree(final ObjectWriter<TaxonomyItem> writer, final TaxonomyItem taxon, final AbstractPlugin plugin) {
		try {
			new JobRetryCall<VOID, IdNotValidException>() {

				@Override
				protected VOID execute()
						throws ExternalRepositoryException, IdNotValidException {
					writer.write(taxon);
					List<TaxonomyItem> items = plugin.getClassificationInterface().retrieveTaxonChildrenByTaxonId(taxon.getId());
					for(TaxonomyItem item : items){
						item.setParent(taxon);
						retrieveTaxaTree(writer, item, plugin);
					}
					return VOID.instance();
				}

			}.call();

		} catch (IdNotValidException e) {
			writer.write(new StreamNonBlockingException(plugin.getRepositoryName(), taxon.getId()));
		} catch (MaxRetriesReachedException e) {
			logger.error("blocking error retrieving taxa tree",e);
			writer.write(new StreamBlockingException(plugin.getRepositoryName()));
		}

	}


	public class RunnableTaxonomySearch implements Runnable{

		Stream<String> reader;
		ResultWrapper<TaxonomyItem> wrapper;

		public RunnableTaxonomySearch(Stream<String> reader,
				ResultWrapper<TaxonomyItem> wrapper) {
			super();
			this.reader = reader;
			this.wrapper = wrapper;
		}

		public void run(){
			Map<String, Writer<String>> pluginMap= new HashMap<String, Writer<String>>();
			while (reader.hasNext()){
				String key = reader.next();
				try{
					final String provider = Util.getProviderFromKey(key);
					String id = Util.getIdFromKey(key);
					if (!pluginMap.containsKey(provider)){
						final LocalWrapper<String> localWrapper = new LocalWrapper<String>();
						pluginMap.put(provider, new Writer<String>(localWrapper));
						ExecutorsContainer.execSearch(new Runnable(){
							public void run(){
								final AbstractPlugin plugin = initializer.getPluginManager().plugins().get(provider);
								final Writer<TaxonomyItem> writer =new Writer<TaxonomyItem>(wrapper, new TaxonomyItemWriterManager(plugin.getRepositoryName()));
								writer.register();

								try {
									new QueryRetryCall() {

										@Override
										protected VOID execute()
												throws ExternalRepositoryException {
											plugin.getClassificationInterface().retrieveTaxonByIds(new LocalReader<String>(localWrapper), writer);
											return VOID.instance();
										}

									}.call();
								} catch (MaxRetriesReachedException e) {
									writer.write(new StreamBlockingException(plugin.getRepositoryName()));
								}
							}
						});
					}
					pluginMap.get(provider).write(id);
				}catch (IdNotValidException e) {
					logger.warn("the key "+key+" is not valid");
				}
			}
			for (Writer<String> writer : pluginMap.values())
				writer.close();
		}

	}

}
