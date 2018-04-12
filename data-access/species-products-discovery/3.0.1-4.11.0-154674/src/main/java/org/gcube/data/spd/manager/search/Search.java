package org.gcube.data.spd.manager.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.ehcache.CacheManager;

import org.gcube.common.authorization.library.AuthorizedTasks;
import org.gcube.data.spd.caching.QueryCacheFactory;
import org.gcube.data.spd.manager.search.workers.CacheReaderWorker;
import org.gcube.data.spd.manager.search.workers.HavingFilterWorker;
import org.gcube.data.spd.manager.search.workers.ObjectManagerWorker;
import org.gcube.data.spd.manager.search.workers.SearchCachingEventDispatcher;
import org.gcube.data.spd.manager.search.workers.SearchWorker;
import org.gcube.data.spd.manager.search.writers.ConsumerEventHandler;
import org.gcube.data.spd.manager.search.writers.WorkerWriterPool;
import org.gcube.data.spd.model.Condition;
import org.gcube.data.spd.model.products.ResultElement;
import org.gcube.data.spd.model.service.exceptions.UnsupportedCapabilityException;
import org.gcube.data.spd.model.service.exceptions.UnsupportedPluginException;
import org.gcube.data.spd.plugin.PluginUtils;
import org.gcube.data.spd.plugin.fwk.AbstractPlugin;
import org.gcube.data.spd.plugin.fwk.Searchable;
import org.gcube.data.spd.plugin.fwk.writers.ClosableWriter;
import org.gcube.data.spd.plugin.fwk.writers.ResultElementWriterManager;
import org.gcube.data.spd.plugin.fwk.writers.Writer;
import org.gcube.data.spd.plugin.fwk.writers.rswrapper.AbstractWrapper;
import org.gcube.data.spd.utils.ExecutorsContainer;
import org.gcube.dataaccess.spd.havingengine.HavingStatement;
import org.gcube.dataaccess.spd.havingengine.HavingStatementFactory;
import org.gcube.dataaccess.spd.havingengine.exl.HavingStatementFactoryEXL;
import org.gcube.dataaccess.spql.model.ExpandClause;
import org.gcube.dataaccess.spql.model.Query;
import org.gcube.dataaccess.spql.model.ResolveClause;
import org.gcube.dataaccess.spql.model.Term;
import org.gcube.dataaccess.spql.model.UnfoldClause;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Search<T extends ResultElement> {

	Logger logger = LoggerFactory.getLogger(Search.class);

	AbstractWrapper<T> wrapper;

	Map<String, AbstractPlugin> plugins ;
	
	CacheManager cacheManager;
	
	QueryCacheFactory<T> queryCacheFactory;
	
	Class<? extends ResultElementWriterManager<T>> writerManagerClass;
	
	public Search(AbstractWrapper<T> wrapper, Map<String, AbstractPlugin> plugins,
			Class<? extends ResultElementWriterManager<T>> writerManagerClass, QueryCacheFactory<T> queryCacheFactory) {
		this.wrapper = wrapper;
		this.writerManagerClass = writerManagerClass;
		this.cacheManager = CacheManager.getInstance();
		this.plugins = plugins;
		this.queryCacheFactory = queryCacheFactory;
	}

	@SuppressWarnings("unchecked")
	public void search(Map<String, Searchable<T>> searchableMapping, Query parsedQuery,  Condition ... properties) throws UnsupportedCapabilityException, UnsupportedPluginException, Exception {
		
		ClosableWriter<T> outputWriter = new Writer<T>(wrapper);
		//preparing the query (and checking semantic)
		List<Worker<?, ?>> workers = new ArrayList<Worker<?, ?>>();
		logger.info("HAVING expression is null ?? "+(parsedQuery.getHavingExpression()==null));
		//adding Having filter if specified
		WorkerWriterPool<T> havingInputWriterPool = null;
		if (parsedQuery.getHavingExpression()!=null){
			HavingStatementFactory factory = new HavingStatementFactoryEXL();
			HavingStatement<T> havingFilter = factory.compile(parsedQuery.getHavingExpression().getExpression());
			((Writer<T>)outputWriter).register();
			Worker<T,T> havingWorker = new HavingFilterWorker<T>(outputWriter, havingFilter);
			workers.add(havingWorker);
			havingInputWriterPool = new WorkerWriterPool<T>(havingWorker);
			logger.debug("adding HavingFilterWorker");
		}
		
		List<ConsumerEventHandler<String>> consumers = new ArrayList<ConsumerEventHandler<String>>();
		for (Entry<String, Searchable<T>> entry: searchableMapping.entrySet()){
			boolean cachablePlugin = plugins.get(entry.getKey()).isUseCache();
			if(havingInputWriterPool==null)
				((Writer<T>)outputWriter).register();
			else 
				outputWriter = havingInputWriterPool.get();
			ObjectManagerWorker<T> managerWorker = new ObjectManagerWorker<T>(outputWriter, writerManagerClass.getConstructor(String.class).newInstance(entry.getKey()));
			WorkerWriterPool<T> writerPool = new WorkerWriterPool<T>(managerWorker);
			logger.debug("("+entry.getKey()+") creating search worker ");
			SearchWorker<T> searchWorker = new SearchWorker<T>( writerPool.get(),entry.getKey(), cachablePlugin, 
					entry.getValue(), cacheManager, queryCacheFactory, properties);
			workers.add(managerWorker);
			workers.add(searchWorker);
			if (cachablePlugin){
				logger.trace("key is "+entry.getKey()+" and value "+entry.getValue());
				CacheReaderWorker<T> cacheReaderWorker = new CacheReaderWorker<T>( writerPool.get(), 
						cacheManager, entry.getKey(), properties, entry.getValue().getHandledClass());
				workers.add(cacheReaderWorker);
				consumers.add(new SearchCachingEventDispatcher<ResultElement>(searchWorker, cacheReaderWorker, 
						cacheManager, entry.getKey(), properties, entry.getValue().getHandledClass()));
			}else
				consumers.add(searchWorker);
		}
		
		List<SearchFlow> searchFlows = extractFlows(parsedQuery);
		for (SearchFlow flow: searchFlows)
			workers.addAll(flow.createWorkers(consumers.toArray(new ConsumerEventHandler[consumers.size()])));
		
		//starting workers
		for (Worker<?, ?> worker: workers)
			ExecutorsContainer.execSearch(AuthorizedTasks.bind(worker));
		
		for (SearchFlow flow: searchFlows)
			flow.injectWords();
	}
	

	private List<SearchFlow> extractFlows(Query parsedQuery) throws UnsupportedCapabilityException, UnsupportedPluginException{
		List<SearchFlow> flows = new ArrayList<SearchFlow>();
		for (Term term :parsedQuery.getTerms()){
			List<String> words = term.getWords();

			Collection<AbstractPlugin> expanders = getExpanders(term.getExpandClause());

			Collection<AbstractPlugin> resolvers = getResolvers(term.getResolveClause());

			SearchFlow flow = new SearchFlow(words, expanders, resolvers);

			UnfoldClause unfoldClause = term.getUnfoldClause();
			if (unfoldClause!=null)
				flow.setUnfolder(getUnfolder(unfoldClause));

			flows.add(flow);			
		}
		return flows;

	}
	
	private Collection<AbstractPlugin> getExpanders(ExpandClause expandClause) throws UnsupportedCapabilityException, UnsupportedPluginException{
		Collection<AbstractPlugin> expanders = Collections.emptyList();
		if (expandClause!=null){
			expanders =expandClause.getDatasources().size()>0?PluginUtils.getPluginsSubList(expandClause.getDatasources(), plugins):
				PluginUtils.getExtenderPlugins(plugins.values());
			if (expanders.size()==0) throw new UnsupportedCapabilityException();
		}
		return expanders;
	}
	
	private Collection<AbstractPlugin> getResolvers(ResolveClause resolveClause) throws UnsupportedCapabilityException, UnsupportedPluginException{
		Collection<AbstractPlugin> resolvers = Collections.emptyList();
		if (resolveClause!=null){
			resolvers =resolveClause.getDatasources().size()>0?PluginUtils.getPluginsSubList(resolveClause.getDatasources(), plugins):
				PluginUtils.getResolverPlugins(plugins.values());
			if (resolvers.size()==0) throw new UnsupportedCapabilityException();
		}
		return resolvers;
	}
	
	private AbstractPlugin getUnfolder(UnfoldClause unfoldClause) throws UnsupportedCapabilityException, UnsupportedPluginException{
		String datasource = unfoldClause.getDatasource();
		AbstractPlugin unfolder = plugins.get(datasource);
		if (unfolder==null){
			logger.error(datasource+" not found");
			throw new UnsupportedPluginException();
		}
		if (unfolder.getUnfoldInterface()==null) 
			throw new UnsupportedCapabilityException();
		return unfolder;
	}
	
	
}
