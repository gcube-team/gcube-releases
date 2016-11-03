package org.gcube.data.spd.workers;

import org.gcube.data.spd.manager.search.Worker;
import org.gcube.data.spd.manager.search.writers.WorkerWriter;
import org.gcube.data.spd.manager.search.writers.WorkerWriterPool;
import org.gcube.data.spd.model.exceptions.StreamException;
import org.gcube.data.spd.plugin.fwk.writers.ClosableWriter;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class WorkerTest {

	Logger logger = LoggerFactory.getLogger(WorkerTest.class);
	
	@Test
	public void createWorkers() throws InterruptedException {
		
		Worker<String, String> addHelloWorker = new AddWordWorker(new ConsoleWriter("addHello"), "Hello, ");	
		
		Worker<String, String> addSpippolaWorker = new AddWordWorker(new ConsoleWriter("Spippola"), "Spippola, ");
				
		
		WorkerWriterPool<String> addHelloPool = new WorkerWriterPool<String>(addHelloWorker, addSpippolaWorker);
								
		Worker<String, String> trimWorker = new TrimWorker(addHelloPool.get());	
		Worker<String, String> oRemoverWorker = new ORemoverWorker(addHelloPool.get());
				
		WorkerWriterPool<String> trimPool = new WorkerWriterPool<String>(trimWorker, oRemoverWorker);
		WorkerWriter<String> trimWorkerWriter = trimPool.get();
		
		Worker<String, String> splitWorker = new SplitterWorker(trimWorkerWriter);
		
		String[] words = new String[]{"primo ; secodno ", "  secondo;   primo  ", "terzo ; querto ", "querto ; terzo "};
		 
		Thread trimThread = new Thread(trimWorker);
		logger.trace(" trimThread has id "+trimThread.getId());
		Thread splitThread = new Thread(splitWorker);
		logger.trace(" splitThread has id "+splitThread.getId());
		Thread oRemoverThread =  new Thread(oRemoverWorker);
		logger.trace(" oremoverThread has id "+oRemoverThread.getId());
		Thread addHelloThread =  new Thread(addHelloWorker);
		logger.trace(" addHelloThread has id "+addHelloThread.getId());
		Thread addSpippolaThread =  new Thread(addSpippolaWorker);
		logger.trace(" addSpippolaThread has id "+addSpippolaThread.getId());
		addHelloThread.start();
		addSpippolaThread.start();
		trimThread.start();
		oRemoverThread.start();
		splitThread.start();
		logger.trace(" Current thread has id "+Thread.currentThread().getId());	
		for (String word : words)
			if (!splitWorker.onElementReady(word)){
				logger.trace("bloccking input (split workler returned false)");
				break; 
			}
		
		splitWorker.onClose();
	
		addHelloThread.join();
	}
	
	
	class ConsoleWriter implements ClosableWriter<String>{
		
		String name;
		boolean alive = true;;
		int wrote = 0;
		
		public ConsoleWriter(String name) {
			super();
			this.name = name;
		}

		@Override
		public boolean write(StreamException error) {
			return false;
		}
		
		@Override
		public boolean write(String t) {
			logger.debug("output for {} element is {} ",name,t);
			/*wrote++;
			
			if (wrote==1){
				logger.trace("setting alive to false");
				alive= false;
				return false;
			}*/
			if (alive) return true;
			else return false;
		}
				
		@Override
		public boolean isAlive() {
			logger.trace("is alive called on ConosleWriter and returned "+alive);
			return alive;
		}
		
		@Override
		public void close() {alive = false;}
	}
}
