package org.gcube.data.spd.remotedispatcher;

import static org.gcube.data.streams.dsl.Streams.convert;

import java.net.URI;
import java.rmi.RemoteException;
import java.util.List;
import java.util.concurrent.ExecutorService;

import org.gcube.common.core.contexts.GCUBEServiceContext;
import org.gcube.common.core.porttypes.GCUBEPortType;
import org.gcube.common.core.types.VOID;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.spd.Constants;
import org.gcube.data.spd.context.ServiceContext;
import org.gcube.data.spd.exception.MaxRetriesReachedException;
import org.gcube.data.spd.model.Condition.Operator;
import org.gcube.data.spd.model.Conditions;
import org.gcube.data.spd.model.Condition;
import org.gcube.data.spd.model.binding.Bindings;
import org.gcube.data.spd.model.exceptions.ExternalRepositoryException;
import org.gcube.data.spd.model.exceptions.IdNotValidException;
import org.gcube.data.spd.model.exceptions.StreamBlockingException;
import org.gcube.data.spd.model.exceptions.StreamNonBlockingException;
import org.gcube.data.spd.model.products.OccurrencePoint;
import org.gcube.data.spd.model.products.ResultElement;
import org.gcube.data.spd.model.products.ResultItem;
import org.gcube.data.spd.model.products.TaxonomyItem;
import org.gcube.data.spd.plugin.PluginManager;
import org.gcube.data.spd.plugin.fwk.AbstractPlugin;
import org.gcube.data.spd.plugin.fwk.Searchable;
import org.gcube.data.spd.plugin.fwk.writers.ClosableWriter;
import org.gcube.data.spd.plugin.fwk.writers.Writer;
import org.gcube.data.spd.plugin.fwk.writers.rswrapper.ResultWrapper;
import org.gcube.data.spd.stubs.ExpandWithSynonymsRequest;
import org.gcube.data.spd.stubs.GetOccurrencesByIdsRequest;
import org.gcube.data.spd.stubs.GetOccurrencesByProductKeysRequest;
import org.gcube.data.spd.stubs.GetSynonymsByIdRequest;
import org.gcube.data.spd.stubs.GetTaxonByIdRequest;
import org.gcube.data.spd.stubs.IdNotValidFault;
import org.gcube.data.spd.stubs.NamesMappingRequest;
import org.gcube.data.spd.stubs.RemoteDispatcherPortType;
import org.gcube.data.spd.stubs.RetrieveTaxaByIdsRequest;
import org.gcube.data.spd.stubs.RetrieveTaxonChildrenByTaxonIdRequest;
import org.gcube.data.spd.stubs.SearchCondition;
import org.gcube.data.spd.stubs.SearchRequest;
import org.gcube.data.spd.stubs.UnfoldRequest;
import org.gcube.data.spd.stubs.UnsupportedCapabilityFault;
import org.gcube.data.spd.utils.JobRetryCall;
import org.gcube.data.spd.utils.QueryRetryCall;

import org.gcube.data.streams.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.thoughtworks.xstream.XStream;

public class RemoteDispatcherPT extends GCUBEPortType implements RemoteDispatcherPortType {

	
	private static Logger logger = LoggerFactory.getLogger(RemoteDispatcherPT.class);
	
	public RemoteDispatcherPT(){
		super();
	}
	
	//only for test
	public RemoteDispatcherPT(AbstractPlugin plugin, ExecutorService executor) {
		this.plugin = plugin;
		this.executor = executor;
	}
	
	//only for test is not null
	AbstractPlugin plugin=null;
	
	//only for test is not null
	ExecutorService executor=null;
	
	private ExecutorService getExecutorPool() {
		if (executor ==null)
			return ServiceContext.getContext().getSearchThreadPool();
		else return executor;
			
	}	
	
	private AbstractPlugin getPlugin(String pluginName){
		if (plugin==null)
			return PluginManager.get().plugins().get(pluginName);
		else return plugin;
	}
	
	@Override
	protected GCUBEServiceContext getServiceContext() {
		return ServiceContext.getContext();
	}
		
	
	@Override
	public String search(SearchRequest request)
			throws RemoteException {
		logger.trace("searchByScienficName called in scope "+ScopeProvider.instance.get());

		AbstractPlugin localPlugin = getPlugin(request.getPluginName());
		logger.trace("plugin "+request.getPluginName()+" have been retrieved, it is null?"+(localPlugin==null));
		Condition[] properties;
		if (request.getProperties()!=null){
			properties = new Condition[request.getProperties().length];
			for (int i =0; i< request.getProperties().length; i++){
				SearchCondition prop = request.getProperties()[i];
				Conditions condition = Conditions.valueOf(prop.getType());
				Object value = new XStream().fromXML(prop.getValue());
				properties[i]=new Condition(condition, value,Operator.valueOf(prop.getOp()));
			}
		}else properties = new Condition[0];
		try{
			if (request.getResultType().equals(Constants.TAXON_RETURN_TYPE)){
				ResultWrapper<TaxonomyItem> wrapper = new ResultWrapper<TaxonomyItem>();
				Writer<TaxonomyItem> writer = new Writer<TaxonomyItem>(wrapper);
				getExecutorPool().execute(new RemoteSearch<TaxonomyItem>(localPlugin.getClassificationInterface(), writer, request.getWord(), properties));
				return wrapper.getLocator();
			}else if (request.getResultType().equals(Constants.OCCURRENCE_RETURN_TYPE)){
				ResultWrapper<OccurrencePoint> wrapper = new ResultWrapper<OccurrencePoint>();
				Writer<OccurrencePoint> writer = new Writer<OccurrencePoint>(wrapper);
				getExecutorPool().execute(new RemoteSearch<OccurrencePoint>(localPlugin.getOccurrencesInterface(), writer, request.getWord(), properties));
				return wrapper.getLocator();
			}else {
				ResultWrapper<ResultItem> wrapper = new ResultWrapper<ResultItem>();
				Writer<ResultItem> writer = new Writer<ResultItem>(wrapper);
				getExecutorPool().execute(new RemoteSearch<ResultItem>(localPlugin, writer, request.getWord(), properties));
				return wrapper.getLocator();
			}
		}catch (Exception e) {
			logger.error("search error for remote plugin", e);
			throw new RemoteException(e.getMessage());
		}

	}

		
	//TAXON functions
		
	@Override
	public String getSynonymsById(final GetSynonymsByIdRequest request)
			throws RemoteException, IdNotValidFault {
		final AbstractPlugin localPlugin = getPlugin(request.getPluginName());
		try{
			final ResultWrapper<TaxonomyItem>  wrapper = new ResultWrapper<TaxonomyItem>();

			getExecutorPool().execute(new Runnable() {

				@Override
				public void run() {
					final Writer<TaxonomyItem> writer = new Writer<TaxonomyItem>(wrapper);
					try {
						new JobRetryCall<VOID, Exception>() {
							
							@Override
							protected VOID execute() throws ExternalRepositoryException, Exception {
								localPlugin.getClassificationInterface().getSynonymnsById(writer, request.getId());
								return new VOID();
							}
						}.call();
					} catch (Exception e) {
						logger.error("getSynonymsById for remote plugin",e);
						writer.write(new StreamBlockingException(localPlugin.getRepositoryName(),request.getId()));
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

	@Override
	public String retrieveTaxonChildrenByTaxonId(
			final RetrieveTaxonChildrenByTaxonIdRequest request)
			throws RemoteException, IdNotValidFault {
		final AbstractPlugin localPlugin = getPlugin(request.getPluginName());
		try{
			final ResultWrapper<TaxonomyItem>  wrapper = new ResultWrapper<TaxonomyItem>();

			getExecutorPool().execute(new Runnable() {

				@Override
				public void run() {
					Writer<TaxonomyItem> writer = new Writer<TaxonomyItem>(wrapper);
					try {
						
						List<TaxonomyItem> items = new JobRetryCall<List<TaxonomyItem>, IdNotValidException>() {
							
							@Override
							protected List<TaxonomyItem> execute() throws ExternalRepositoryException, IdNotValidException {
								return localPlugin.getClassificationInterface().retrieveTaxonChildrenByTaxonId(request.getId());
							}
						}.call();
						
						for (TaxonomyItem item :items)
						 writer.write(item);
					} catch (Exception e) {
						logger.error("error retreiving children by id",e);
						writer.write(new StreamBlockingException(localPlugin.getRepositoryName(), request.getId()));
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

	
	public String retrieveTaxaByIds(final RetrieveTaxaByIdsRequest request)
			throws RemoteException {
		final AbstractPlugin localPlugin =  getPlugin(request.getPluginName());
		try{
			final ResultWrapper<TaxonomyItem>  wrapper = new ResultWrapper<TaxonomyItem>();

			getExecutorPool().execute(new Runnable() {

				@Override
				public void run() {
					final Stream<String> idsStream = convert(URI.create(request.getIdsLocator())).ofStrings().withDefaults();; 
					final Writer<TaxonomyItem> writer = new Writer<TaxonomyItem>(wrapper);
					new JobRetryCall<VOID, Exception>() {

						@Override
						protected VOID execute()
								throws ExternalRepositoryException, Exception {
							localPlugin.getClassificationInterface().retrieveTaxonByIds(idsStream, writer);
							return new VOID();
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

	@Override
	public String getTaxonById(GetTaxonByIdRequest request)
			throws RemoteException, IdNotValidFault {
		AbstractPlugin plugin =  getPlugin(request.getPluginName());
		try {
			return Bindings.toXml(plugin.getClassificationInterface().retrieveTaxonById(request.getId()));
		} catch (IdNotValidException e) {
			logger.error("error in getTaxonById",e);
			throw new IdNotValidFault();
		}  catch (Exception e) {
			logger.error("error in getTaxonById",e);
			throw new RemoteException(e.getMessage());
		}
	}

	//END: TAXON functions
	
	
	//occurrence functions
	
	@Override
	public String getOccurrencesByProductKeys(
			final GetOccurrencesByProductKeysRequest request) throws RemoteException {
		final AbstractPlugin localPlugin = getPlugin(request.getPluginName());
		try{
			final ResultWrapper<OccurrencePoint>  wrapper = new ResultWrapper<OccurrencePoint>();
			getExecutorPool().execute(new Runnable() {
				@Override
				public void run() {
					logger.debug("searching remote occurrence for plugin "+localPlugin.getRepositoryName());
					final Stream<String> keysStream = convert(URI.create(request.getProductKeysLocator())).ofStrings().withDefaults(); 
					final Writer<OccurrencePoint> writer = new Writer<OccurrencePoint>(wrapper);
					try {
						new JobRetryCall<VOID, Exception>() {

							@Override
							protected VOID execute()
									throws ExternalRepositoryException, Exception {
								localPlugin.getOccurrencesInterface().getOccurrencesByProductKeys(writer, keysStream);
								return new VOID();
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

	@Override
	public String getOccurrencesByIds(final GetOccurrencesByIdsRequest request)
			throws RemoteException {
		final AbstractPlugin localPlugin =  getPlugin(request.getPluginName());
		try{
			final ResultWrapper<OccurrencePoint>  wrapper = new ResultWrapper<OccurrencePoint>();
			getExecutorPool().execute(new Runnable() {
				@Override
				public void run() {
					final Stream<String> idsStream = convert(URI.create(request.getIdsLocator())).ofStrings().withDefaults(); 
					final Writer<OccurrencePoint> writer = new Writer<OccurrencePoint>(wrapper);
					try {
						new JobRetryCall<VOID, Exception>() {

							@Override
							protected VOID execute()
									throws ExternalRepositoryException, Exception {
								localPlugin.getOccurrencesInterface().getOccurrencesByIds(writer, idsStream);
								return new VOID();
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

	@Override
	public String namesMapping(final NamesMappingRequest request)
			throws RemoteException {
		logger.trace("requesting plugin "+request.getPluginName());
		final AbstractPlugin localPlugin =  getPlugin(request.getPluginName());
		if (plugin==null) throw new RemoteException("error executing namesMapping on "+request.getPluginName());
		try{
			final ResultWrapper<String>  wrapper = new ResultWrapper<String>();

			getExecutorPool().execute(new Runnable() {
				@Override
				public void run() {
					final Writer<String> writer = new Writer<String>(wrapper);
					logger.trace("calling names mapping on "+localPlugin.getRepositoryName());

					try{
						new QueryRetryCall(){

							@Override
							protected VOID execute()
									throws ExternalRepositoryException {
								localPlugin.getMappingInterface().getRelatedScientificNames(writer, request.getCommonName());
								return new VOID();
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

	@Override
	public String expandWithSynonyms(final ExpandWithSynonymsRequest request)
			throws RemoteException {
		final AbstractPlugin localPlugin =  getPlugin(request.getPluginName());
		try{
			final ResultWrapper<String>  wrapper = new ResultWrapper<String>();

			getExecutorPool().execute(new Runnable() {
				@Override
				public void run() {
					final Writer<String> writer = new Writer<String>(wrapper);
					try {
						//"synonyms expansion is not suported in "+plugin.getRepositoryName()
						if (localPlugin.getExpansionInterface()==null) throw new  UnsupportedCapabilityFault();
						else{
							new QueryRetryCall(){

								@Override
								protected VOID execute()
										throws ExternalRepositoryException {
									localPlugin.getExpansionInterface().getSynonyms(writer, request.getScientificName());
									return new VOID();
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

		@Override
		public String unfold(final UnfoldRequest request)
				throws RemoteException {
			final AbstractPlugin localPlugin =  getPlugin(request.getPluginName());
			try{
				final ResultWrapper<String>  wrapper = new ResultWrapper<String>();

				getExecutorPool().execute(new Runnable() {
					@Override
					public void run() {
						final Writer<String> writer = new Writer<String>(wrapper);
						try {
							//"synonyms expansion is not suported in "+plugin.getRepositoryName()
							if (localPlugin.getUnfoldInterface()==null) throw new  UnsupportedCapabilityFault();
							else{
								new QueryRetryCall(){

									@Override
									protected VOID execute()
											throws ExternalRepositoryException {
										localPlugin.getUnfoldInterface().unfold(writer, request.getScientificName());
										return new VOID();
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
				Condition[] conditions) {
			super();
			this.searchable = searchable;
			this.writer = writer;
			this.word = word;
			this.conditions = conditions;
		}


		public void run() {
			try{
				new QueryRetryCall() {
					
					@Override
					protected VOID execute() throws ExternalRepositoryException {
						searchable.searchByScientificName(word, writer, conditions);
						return new VOID();
					}
				}.call();
			} catch (MaxRetriesReachedException e) {
				writer.write(new StreamNonBlockingException(word));
			}finally{
				writer.close();
			}
		}
		
	}

	
	
}
