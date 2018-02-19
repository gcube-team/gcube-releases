package org.gcube.data.spd.utils;

import static org.gcube.data.streams.dsl.Streams.convert;

import java.net.URI;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.spd.Constants;
import org.gcube.data.spd.exception.MaxRetriesReachedException;
import org.gcube.data.spd.manager.AppInitializer;
import org.gcube.data.spd.model.Condition;
import org.gcube.data.spd.model.PluginDescription;
import org.gcube.data.spd.model.binding.Bindings;
import org.gcube.data.spd.model.exceptions.ExternalRepositoryException;
import org.gcube.data.spd.model.exceptions.IdNotValidException;
import org.gcube.data.spd.model.exceptions.StreamBlockingException;
import org.gcube.data.spd.model.exceptions.StreamNonBlockingException;
import org.gcube.data.spd.model.products.OccurrencePoint;
import org.gcube.data.spd.model.products.ResultElement;
import org.gcube.data.spd.model.products.ResultItem;
import org.gcube.data.spd.model.products.TaxonomyItem;
import org.gcube.data.spd.model.service.exceptions.InvalidIdentifierException;
import org.gcube.data.spd.model.service.exceptions.UnsupportedCapabilityException;
import org.gcube.data.spd.model.service.types.PluginDescriptions;
import org.gcube.data.spd.model.service.types.SearchCondition;
import org.gcube.data.spd.model.service.types.SearchRequest;
import org.gcube.data.spd.plugin.fwk.AbstractPlugin;
import org.gcube.data.spd.plugin.fwk.Searchable;
import org.gcube.data.spd.plugin.fwk.writers.ClosableWriter;
import org.gcube.data.spd.plugin.fwk.writers.Writer;
import org.gcube.data.spd.plugin.fwk.writers.rswrapper.ResultWrapper;
import org.gcube.data.streams.Stream;
import org.gcube.smartgears.ApplicationManagerProvider;
import org.gcube.smartgears.ContextProvider;
import org.gcube.smartgears.context.application.ApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;

@Path("remote")
public class RemoteDispatcher {

	
	private static Logger logger = LoggerFactory.getLogger(RemoteDispatcher.class);
	
	ApplicationContext ctx = ContextProvider.get();
	
	AppInitializer initializer = (AppInitializer) ApplicationManagerProvider.get(AppInitializer.class);
	
	public RemoteDispatcher(){
		super();
	}
	
	//only for test
	public RemoteDispatcher(AbstractPlugin plugin, ExecutorService executor) {
		this.plugin = plugin;
	}
	
	//only for test is not null
	AbstractPlugin plugin=null;
	
		
	
	private AbstractPlugin getPlugin(String pluginName){
		if (plugin==null)
			return initializer.getPluginManager().plugins().get(pluginName);
		else return plugin;
	}
	
		
	/* (non-Javadoc)
	 * @see org.gcube.data.spd.remotedispatcher.RemoteDispatcher#search(org.gcube.data.spd.remotedispatcher.types.SearchRequest)
	 */
/*	@GET
	@Path("retrieve/search")
	@Consumes(MediaType.APPLICATION_XML)
	public String search(SearchRequest request)
			throws RemoteException {
		logger.trace("searchByScienficName called in scope "+ScopeProvider.instance.get());

		AbstractPlugin localPlugin = getPlugin(request.getPluginName());
		logger.trace("plugin "+request.getPluginName()+" have been retrieved, it is null?"+(localPlugin==null));
		List<Condition> properties = Collections.emptyList();
		if (request.getProperties()!=null){
			properties = new ArrayList<Condition>(request.getProperties().size());
			for (SearchCondition prop : request.getProperties()){
				Object value = new XStream().fromXML(prop.getValue());
				properties.add(new Condition(prop.getType(), value,prop.getOperator()));
			}
		}
		try{
			if (request.getResultType().equals(Constants.TAXON_RETURN_TYPE)){
				ResultWrapper<TaxonomyItem> wrapper = new ResultWrapper<TaxonomyItem>();
				Writer<TaxonomyItem> writer = new Writer<TaxonomyItem>(wrapper);
				ExecutorsContainer.execSearch(new RemoteSearch<TaxonomyItem>(localPlugin.getClassificationInterface(), writer, request.getWord(), properties));
				return wrapper.getLocator();
			}else if (request.getResultType().equals(Constants.OCCURRENCE_RETURN_TYPE)){
				ResultWrapper<OccurrencePoint> wrapper = new ResultWrapper<OccurrencePoint>();
				Writer<OccurrencePoint> writer = new Writer<OccurrencePoint>(wrapper);
				ExecutorsContainer.execSearch(new RemoteSearch<OccurrencePoint>(localPlugin.getOccurrencesInterface(), writer, request.getWord(), properties));
				return wrapper.getLocator();
			}else {
				ResultWrapper<ResultItem> wrapper = new ResultWrapper<ResultItem>();
				Writer<ResultItem> writer = new Writer<ResultItem>(wrapper);
				ExecutorsContainer.execSearch(new RemoteSearch<ResultItem>(localPlugin, writer, request.getWord(), properties));
				return wrapper.getLocator();
			}
		}catch (Exception e) {
			logger.error("search error for remote plugin", e);
			throw new RemoteException(e.getMessage());
		}

	}

		
	//TAXON functions
	
	 (non-Javadoc)
	 * @see org.gcube.data.spd.remotedispatcher.RemoteDispatcher#getSynonymsById(java.lang.String, java.lang.String)
	 
	@GET
	@Path("taxon/synonyms")
	public String getSynonymsById(@QueryParam("id") final String id, @QueryParam("plugin") String pluginName)
			throws RemoteException, InvalidIdentifierException {
		final AbstractPlugin localPlugin = getPlugin(pluginName);
		try{
			final ResultWrapper<TaxonomyItem>  wrapper = new ResultWrapper<TaxonomyItem>();

			ExecutorsContainer.execSearch(new Runnable() {

				@Override
				public void run() {
					final Writer<TaxonomyItem> writer = new Writer<TaxonomyItem>(wrapper);
					try {
						new JobRetryCall<VOID, Exception>() {
							
							@Override
							protected VOID execute() throws ExternalRepositoryException, Exception {
								localPlugin.getClassificationInterface().getSynonymnsById(writer, id);
								return VOID.instance();
							}
						}.call();
					} catch (Exception e) {
						logger.error("getSynonymsById for remote plugin",e);
						writer.write(new StreamBlockingException(localPlugin.getRepositoryName(),id));
					} finally{
						writer.close();
					}
				}
			});

			return wrapper.getLocator();
		} catch (Exception e) {
			logger.error("error getting locator ",e);
			throw new RemoteException(e.getMessage());
		}
	}

	 (non-Javadoc)
	 * @see org.gcube.data.spd.remotedispatcher.RemoteDispatcher#retrieveTaxonChildrenByTaxonId(java.lang.String, java.lang.String)
	 
	@GET
	@Path("taxon/children/{key}")
	public String retrieveTaxonChildrenByTaxonId(
			@PathParam("id") final String id, @QueryParam("plugin") final String pluginName)
			throws RemoteException, InvalidIdentifierException {
		final AbstractPlugin localPlugin = getPlugin(pluginName);
		try{
			final ResultWrapper<TaxonomyItem>  wrapper = new ResultWrapper<TaxonomyItem>();

			ExecutorsContainer.execSearch(new Runnable() {

				@Override
				public void run() {
					Writer<TaxonomyItem> writer = new Writer<TaxonomyItem>(wrapper);
					try {
						
						List<TaxonomyItem> items = new JobRetryCall<List<TaxonomyItem>, IdNotValidException>() {
							
							@Override
							protected List<TaxonomyItem> execute() throws ExternalRepositoryException, IdNotValidException {
								return localPlugin.getClassificationInterface().retrieveTaxonChildrenByTaxonId(id);
							}
						}.call();
						
						for (TaxonomyItem item :items)
						 writer.write(item);
					} catch (Exception e) {
						logger.error("error retreiving children by id",e);
						writer.write(new StreamBlockingException(localPlugin.getRepositoryName(), id));
					}finally{
						writer.close();
					}
				}
			});

			return wrapper.getLocator();
		} catch (Exception e) {
			logger.error("error getting locator ",e);
			throw new RemoteException(e.getMessage());
		}
	}

	
	 (non-Javadoc)
	 * @see org.gcube.data.spd.remotedispatcher.RemoteDispatcher#retrieveTaxaByIds(java.lang.String, java.lang.String)
	 
	@GET
	@Path("taxon/tree/{plugin}/{key}")
	public String retrieveTaxaByIds(@PathParam("key") final String idsLocator, @PathParam("plugin") String pluginName)
			throws RemoteException {
		final AbstractPlugin localPlugin =  getPlugin(pluginName);
		try{
			final ResultWrapper<TaxonomyItem>  wrapper = new ResultWrapper<TaxonomyItem>();

			ExecutorsContainer.execSearch(new Runnable() {

				@Override
				public void run() {
					final Stream<String> idsStream = convert(URI.create(idsLocator)).ofStrings().withDefaults();; 
					final Writer<TaxonomyItem> writer = new Writer<TaxonomyItem>(wrapper);
					new JobRetryCall<VOID, Exception>() {

						@Override
						protected VOID execute()
								throws ExternalRepositoryException, Exception {
							localPlugin.getClassificationInterface().retrieveTaxonByIds(idsStream, writer);
							return VOID.instance();
						}
						
					};
					writer.close();
				}
			});

			return wrapper.getLocator();
		} catch (Exception e) {
			logger.error("error getting locator ",e);
			throw new RemoteException(e.getMessage());
		}
	}

	 (non-Javadoc)
	 * @see org.gcube.data.spd.remotedispatcher.RemoteDispatcher#getTaxonById(java.lang.String, java.lang.String)
	 
	@GET
	@Path("taxon/ids/{idsLocator}")
	public String getTaxonById(@PathParam("idsLocator") final String id, @QueryParam("plugin") String pluginName)
			throws RemoteException, InvalidIdentifierException {
		AbstractPlugin plugin =  getPlugin(pluginName);
		try {
			return Bindings.toXml(plugin.getClassificationInterface().retrieveTaxonById(id));
		} catch (IdNotValidException e) {
			logger.error("error in getTaxonById",e);
			throw new InvalidIdentifierException();
		}  catch (Exception e) {
			logger.error("error in getTaxonById",e);
			throw new RemoteException(e.getMessage());
		}
	}

	//END: TAXON functions
	
	
	//occurrence functions
	
	 (non-Javadoc)
	 * @see org.gcube.data.spd.remotedispatcher.RemoteDispatcher#getOccurrencesByProductKeys(java.lang.String, java.lang.String)
	 
	@GET
	@Path("occurrence/keys/{productKeysLocator}")
	public String getOccurrencesByProductKeys(
			@PathParam("productKeysLocator") final String productKeysLocator, @QueryParam("plugin") String pluginName) throws RemoteException {
		final AbstractPlugin localPlugin = getPlugin(pluginName);
		try{
			final ResultWrapper<OccurrencePoint>  wrapper = new ResultWrapper<OccurrencePoint>();
			ExecutorsContainer.execSearch(new Runnable() {
				@Override
				public void run() {
					logger.debug("searching remote occurrence for plugin "+localPlugin.getRepositoryName());
					final Stream<String> keysStream = convert(URI.create(productKeysLocator)).ofStrings().withDefaults(); 
					final Writer<OccurrencePoint> writer = new Writer<OccurrencePoint>(wrapper);
					try {
						new JobRetryCall<VOID, Exception>() {

							@Override
							protected VOID execute()
									throws ExternalRepositoryException, Exception {
								localPlugin.getOccurrencesInterface().getOccurrencesByProductKeys(writer, keysStream);
								return VOID.instance();
							}
							
						}.call();
					} catch (Exception e) {
						writer.write(new StreamBlockingException(localPlugin.getRepositoryName()));
					}
					writer.close();
				}
			});
			return wrapper.getLocator();
		} catch (Exception e) {
			logger.error("error getting locator ",e);
			throw new RemoteException(e.getMessage());
		}
	}


	 (non-Javadoc)
	 * @see org.gcube.data.spd.remotedispatcher.RemoteDispatcher#getOccurrencesByIds(java.lang.String, java.lang.String)
	 
	@GET
	@Path("occurrence/ids/{IdsLocator}")
	public String getOccurrencesByIds(final String idsLocator, String pluginName)
			throws RemoteException {
		final AbstractPlugin localPlugin =  getPlugin(pluginName);
		try{
			final ResultWrapper<OccurrencePoint>  wrapper = new ResultWrapper<OccurrencePoint>();
			ExecutorsContainer.execSearch(new Runnable() {
				@Override
				public void run() {
					final Stream<String> idsStream = convert(URI.create(idsLocator)).ofStrings().withDefaults(); 
					final Writer<OccurrencePoint> writer = new Writer<OccurrencePoint>(wrapper);
					try {
						new JobRetryCall<VOID, Exception>() {

							@Override
							protected VOID execute()
									throws ExternalRepositoryException, Exception {
								localPlugin.getOccurrencesInterface().getOccurrencesByIds(writer, idsStream);
								return VOID.instance();
							}
							
						}.call();
					} catch (Exception e) {
						writer.write(new StreamBlockingException(localPlugin.getRepositoryName()));
					}
					
					writer.close();
				}
			});
			return wrapper.getLocator();
		} catch (Exception e) {
			logger.error("error getting locator ",e);
			throw new RemoteException(e.getMessage());
		}
	}

	//END : occurrence functions
	
	
	//RESOLVE CAPABILITIES

	 (non-Javadoc)
	 * @see org.gcube.data.spd.remotedispatcher.RemoteDispatcher#namesMapping(java.lang.String, java.lang.String)
	 
	@GET
	@Path("extensions/mapping/{commonName}")
	public String namesMapping(@PathParam("commonName") final String commonName, @QueryParam("name") String pluginName)
			throws RemoteException {
		logger.trace("requesting plugin "+pluginName);
		final AbstractPlugin localPlugin =  getPlugin(pluginName);
		if (plugin==null) throw new RemoteException("error executing namesMapping on "+pluginName);
		try{
			final ResultWrapper<String>  wrapper = new ResultWrapper<String>();

			ExecutorsContainer.execSearch(new Runnable() {
				@Override
				public void run() {
					final Writer<String> writer = new Writer<String>(wrapper);
					logger.trace("calling names mapping on "+localPlugin.getRepositoryName());

					try{
						new QueryRetryCall(){

							@Override
							protected VOID execute()
									throws ExternalRepositoryException {
								localPlugin.getMappingInterface().getRelatedScientificNames(writer, commonName);
								return VOID.instance();
							}

						}.call();

					} catch (MaxRetriesReachedException e) {
						logger.error("error retreiving namesMapping on remote plugin",e);
						writer.write(new StreamBlockingException(localPlugin.getRepositoryName()));
					}finally{
						writer.close();
					}
				}
			});
			return wrapper.getLocator();
		} catch (Exception e) {
			logger.error("error getting locator ",e);
			throw new RemoteException(e.getMessage());
		}
	}
	
	//END : RESOLVE CAPABILITIES
	
	//EXPAND CAPABILITIES

	 (non-Javadoc)
	 * @see org.gcube.data.spd.remotedispatcher.RemoteDispatcher#expandWithSynonyms(java.lang.String, java.lang.String)
	 
	@GET
	@Path("extensions/expand/{scientificName}")
	public String expandWithSynonyms(@PathParam("scientificName") final String scientificName,@QueryParam("plugin") String pluginName)
			throws RemoteException {
		final AbstractPlugin localPlugin =  getPlugin(pluginName);
		try{
			final ResultWrapper<String>  wrapper = new ResultWrapper<String>();

			ExecutorsContainer.execSearch(new Runnable() {
				@Override
				public void run() {
					final Writer<String> writer = new Writer<String>(wrapper);
					try {
						//"synonyms expansion is not suported in "+plugin.getRepositoryName()
						if (localPlugin.getExpansionInterface()==null) throw new  UnsupportedCapabilityException();
						else{
							new QueryRetryCall(){

								@Override
								protected VOID execute()
										throws ExternalRepositoryException {
									localPlugin.getExpansionInterface().getSynonyms(writer, scientificName);
									return VOID.instance();
								}
							}.call();
						}
					} catch (Exception e) {
						logger.error("error getting synonyms for remote plugin",e);
					}finally{
						writer.close();
					}
				}
			});
			return wrapper.getLocator();
		} catch (Exception e) {
			logger.error("error getting locator ",e);
			throw new RemoteException(e.getMessage());
		}
	}

	//END: EXPAND CAPABILITIES
	
	//UNFOLD CAPABILITIES

		 (non-Javadoc)
		 * @see org.gcube.data.spd.remotedispatcher.RemoteDispatcher#unfold(java.lang.String, java.lang.String)
		 
	@GET
	@Path("extensions/unfold/{scientificName}")
		public String unfold(@PathParam("scientificName") final String scientificName,@QueryParam("plugin") String pluginName)
				throws RemoteException {
			final AbstractPlugin localPlugin =  getPlugin(pluginName);
			try{
				final ResultWrapper<String>  wrapper = new ResultWrapper<String>();

				ExecutorsContainer.execSearch(new Runnable() {
					@Override
					public void run() {
						final Writer<String> writer = new Writer<String>(wrapper);
						try {
							//"synonyms expansion is not suported in "+plugin.getRepositoryName()
							if (localPlugin.getUnfoldInterface()==null) throw new  UnsupportedCapabilityException();
							else{
								new QueryRetryCall(){

									@Override
									protected VOID execute()
											throws ExternalRepositoryException {
										localPlugin.getUnfoldInterface().unfold(writer, scientificName);
										return VOID.instance();
									}
								}.call();
							}
						} catch (Exception e) {
							logger.error("error getting synonyms for remote plugin",e);
						}finally{
							writer.close();
						}
					}
				});
				return wrapper.getLocator();
			} catch (Exception e) {
				logger.error("error getting locator ",e);
				throw new RemoteException(e.getMessage());
			}
		}

		//END: UNFOLD CAPABILITIES
	
	class RemoteSearch<T extends ResultElement> implements Runnable {
		
		private final Searchable<T> searchable;
		private final ClosableWriter<T> writer;		
		private final String word;
		private final Condition[] conditions;
		
		public RemoteSearch(Searchable<T> searchable,
				ClosableWriter<T> writer, String word,
				List<Condition> conditions) {
			super();
			this.searchable = searchable;
			this.writer = writer;
			this.word = word;
			this.conditions = new Condition[conditions.size()];
			conditions.toArray(this.conditions);
		}


		public void run() {
			try{
				new QueryRetryCall() {
					
					@Override
					protected VOID execute() throws ExternalRepositoryException {
						searchable.searchByScientificName(word, writer, conditions);
						return VOID.instance();
					}
				}.call();
			} catch (MaxRetriesReachedException e) {
				writer.write(new StreamNonBlockingException(word));
			}finally{
				writer.close();
			}
		}
		
	}

	@PUT
	@Path("exchange")
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML)
	public PluginDescriptions exchangePlugins(PluginDescriptions remotePlugins,@QueryParam("gCoreEndpointId") String gCoreEndpointId)
			throws RemoteException {
		initializer.getPluginManager().addRemotePlugins(remotePlugins.getDescriptions(), gCoreEndpointId);
		List<PluginDescription> descriptions = new ArrayList<PluginDescription>();
		for (AbstractPlugin plugin :initializer.getPluginManager().plugins().values())
			if(!plugin.isRemote())
				descriptions.add(Utils.getPluginDescription(plugin));
		return new PluginDescriptions(descriptions);
			
	}

	@DELETE
	@Path("remove/{gCoreEndpointId}")
	public void removeAll(@PathParam("gCoreEndpointId") String gCoreEndpointId)
			throws RemoteException {
		initializer.getPluginManager().removeRemotePlugin(gCoreEndpointId);			
	}

*/	
}
