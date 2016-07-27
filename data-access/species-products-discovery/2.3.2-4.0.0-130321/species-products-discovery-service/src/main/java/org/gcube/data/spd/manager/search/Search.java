package org.gcube.data.spd.manager.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;

import net.sf.ehcache.CacheManager;

import org.gcube.common.scope.impl.ScopedTasks;
import org.gcube.data.spd.manager.search.workers.CacheReaderWorker;
import org.gcube.data.spd.manager.search.workers.HavingFilterWorker;
import org.gcube.data.spd.manager.search.workers.ObjectManagerWorker;
import org.gcube.data.spd.manager.search.workers.SearchCachingEventDispatcher;
import org.gcube.data.spd.manager.search.workers.SearchWorker;
import org.gcube.data.spd.manager.search.writers.ConsumerEventHandler;
import org.gcube.data.spd.manager.search.writers.WorkerWriterPool;
import org.gcube.data.spd.model.Condition;
import org.gcube.data.spd.model.products.ResultElement;
import org.gcube.data.spd.plugin.PluginManager;
import org.gcube.data.spd.plugin.PluginUtils;
import org.gcube.data.spd.plugin.fwk.AbstractPlugin;
import org.gcube.data.spd.plugin.fwk.Searchable;
import org.gcube.data.spd.plugin.fwk.writers.ClosableWriter;
import org.gcube.data.spd.plugin.fwk.writers.ResultElementWriterManager;
import org.gcube.data.spd.plugin.fwk.writers.Writer;
import org.gcube.data.spd.plugin.fwk.writers.rswrapper.AbstractWrapper;
import org.gcube.data.spd.stubs.UnsupportedCapabilityFault;
import org.gcube.data.spd.stubs.UnsupportedPluginFault;
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

	ExecutorService executor;

	CacheManager cacheManager;
	
	Class<? extends ResultElementWriterManager<T>> writerManagerClass;
	
	public Search(AbstractWrapper<T> wrapper, ExecutorService executor, CacheManager cacheManager, 
			Class<? extends ResultElementWriterManager<T>> writerManagerClass) {
		this.wrapper = wrapper;
		this.executor = executor; 
		this.writerManagerClass = writerManagerClass;
		this.cacheManager = cacheManager;
	}

	private Map<String, AbstractPlugin> getSystemPlugins(){
		if (plugins!=null)
			return plugins;
		else return PluginManager.get().plugins();
	}

	public void setSystemPlugins( Map<String, AbstractPlugin> plugins){
		this.plugins = plugins;
	}

	@SuppressWarnings("unchecked")
	public void search(Map<String, Searchable<T>> searchableMapping, Query parsedQuery,  Condition ... properties) throws UnsupportedCapabilityFault, UnsupportedPluginFault, Exception {
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
			boolean cachablePlugin = getSystemPlugins().get(entry.getKey()).isUseCache();
			if(havingInputWriterPool==null)
				((Writer<T>)outputWriter).register();
			else 
				outputWriter = havingInputWriterPool.get();
			ObjectManagerWorker<T> managerWorker = new ObjectManagerWorker<T>(outputWriter, writerManagerClass.getConstructor(String.class).newInstance(entry.getKey()));
			WorkerWriterPool<T> writerPool = new WorkerWriterPool<T>(managerWorker);
			logger.debug("("+entry.getKey()+") creating search worker ");
			SearchWorker<T> searchWorker = new SearchWorker<T>( writerPool.get(),entry.getKey(), cachablePlugin, 
					entry.getValue(), cacheManager, properties);
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
			this.executor.execute(ScopedTasks.bind(worker));
		
		for (SearchFlow flow: searchFlows)
			flow.injectWords();
	}
	

	private List<SearchFlow> extractFlows(Query parsedQuery) throws UnsupportedCapabilityFault, UnsupportedPluginFault{
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
	
	private Collection<AbstractPlugin> getExpanders(ExpandClause expandClause) throws UnsupportedCapabilityFault, UnsupportedPluginFault{
		Collection<AbstractPlugin> expanders = Collections.emptyList();
		if (expandClause!=null){
			expanders =expandClause.getDatasources().size()>0?PluginUtils.getPluginsSubList(expandClause.getDatasources(), getSystemPlugins()):
				PluginUtils.getExtenderPlugins(getSystemPlugins().values());
			if (expanders.size()==0) throw new UnsupportedCapabilityFault();
		}
		return expanders;
	}
	
	private Collection<AbstractPlugin> getResolvers(ResolveClause resolveClause) throws UnsupportedCapabilityFault, UnsupportedPluginFault{
		Collection<AbstractPlugin> resolvers = Collections.emptyList();
		if (resolveClause!=null){
			resolvers =resolveClause.getDatasources().size()>0?PluginUtils.getPluginsSubList(resolveClause.getDatasources(), getSystemPlugins()):
				PluginUtils.getResolverPlugins(getSystemPlugins().values());
			if (resolvers.size()==0) throw new UnsupportedCapabilityFault();
		}
		return resolvers;
	}
	
	private AbstractPlugin getUnfolder(UnfoldClause unfoldClause) throws UnsupportedCapabilityFault, UnsupportedPluginFault{
		String datasource = unfoldClause.getDatasource();
		AbstractPlugin unfolder = getSystemPlugins().get(datasource);
		if (unfolder==null){
			logger.error(datasource+" not found");
			throw new UnsupportedPluginFault();
		}
		if (unfolder.getUnfoldInterface()==null) 
			throw new UnsupportedCapabilityFault();
		return unfolder;
	}
	
	
}
