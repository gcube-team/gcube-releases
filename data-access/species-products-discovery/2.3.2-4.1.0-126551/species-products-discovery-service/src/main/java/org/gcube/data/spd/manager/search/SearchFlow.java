package org.gcube.data.spd.manager.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.gcube.data.spd.manager.search.workers.CommonNameMapping;
import org.gcube.data.spd.manager.search.workers.SynonymsRetriever;
import org.gcube.data.spd.manager.search.workers.UnfolderWorker;
import org.gcube.data.spd.manager.search.writers.ConsumerEventHandler;
import org.gcube.data.spd.manager.search.writers.WorkerWriterPool;
import org.gcube.data.spd.plugin.fwk.AbstractPlugin;
import org.gcube.data.spd.stubs.UnsupportedPluginFault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SearchFlow {

	private Logger logger= LoggerFactory.getLogger(SearchFlow.class);
	
	private Collection<AbstractPlugin> expanders;
	
	private Collection<AbstractPlugin> resolvers;
	
	private List<String> words;

	private ConsumerEventHandler<String>[] consumers;
	
	private AbstractPlugin unfolder = null;
	
	public SearchFlow(List<String> words, Collection<AbstractPlugin> expanders, Collection<AbstractPlugin> resolvers) {
		super();
		this.resolvers = resolvers;
		this.expanders = expanders;
		this.words = words;
	}

	public void setUnfolder(AbstractPlugin unfolder) {
		this.unfolder = unfolder;
	}
	
	
	@SuppressWarnings("unchecked")
	public List<Worker<?, ?>> createWorkers(ConsumerEventHandler<String> ... registeredConsumers) throws UnsupportedPluginFault{
		ConsumerEventHandler<String>[] actualConsumers = registeredConsumers;
		List<Worker<?, ?>> workersToExecute = new ArrayList<Worker<?,?>>();

		if (expanders.size()>0){
			logger.trace("preparing "+expanders.size()+" expander");
			List<Worker<?, ?>> workers = new ArrayList<Worker<?,?>>();

			WorkerWriterPool<String> writerPool = new WorkerWriterPool<String>(actualConsumers);
			for (AbstractPlugin expander : expanders)
				workers.add(new SynonymsRetriever(writerPool.get(), expander));
			actualConsumers = workers.toArray(new Worker[workers.size()]);
			workersToExecute.addAll(workers);
		}
		if (resolvers.size()>0){
			logger.trace("preparing "+resolvers.size()+" resolver");
			List<Worker<?, ?>> workers = new ArrayList<Worker<?,?>>();

			WorkerWriterPool<String> writerPool = new WorkerWriterPool<String>(actualConsumers);
			for (AbstractPlugin resolver : resolvers)
				workers.add(new CommonNameMapping(writerPool.get(), resolver));
			actualConsumers = workers.toArray(new Worker[workers.size()]);
			workersToExecute.addAll(workers);
		}

		if (unfolder!=null){
			WorkerWriterPool<String> writerPool = new WorkerWriterPool<String>(actualConsumers);
			Worker<?,?> unfolderWorker = new UnfolderWorker(writerPool.get(), unfolder);
			actualConsumers = new Worker[]{unfolderWorker};
			workersToExecute.add(unfolderWorker);
		}

		this.consumers = actualConsumers;

		return workersToExecute;

	}

	public void injectWords() {
		
		if (consumers == null) 
			new RuntimeException("search flow not started");
		
		for (String word: this.words){
			logger.trace("injecting "+word);
			for (ConsumerEventHandler<String> actualConsumer : this.consumers)
				actualConsumer.onElementReady(word);
		}

		for (ConsumerEventHandler<String> actualConsumer : this.consumers)
			actualConsumer.onClose();
	}

	
}
