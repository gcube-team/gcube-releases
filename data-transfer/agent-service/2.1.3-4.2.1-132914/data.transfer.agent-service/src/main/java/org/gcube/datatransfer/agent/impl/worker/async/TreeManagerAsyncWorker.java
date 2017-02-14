package org.gcube.datatransfer.agent.impl.worker.async;

import static org.gcube.data.tml.proxies.TServiceFactory.readSource;
import static org.gcube.data.tml.proxies.TServiceFactory.writeSource;

import org.gcube.common.clients.fw.queries.StatefulQuery;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.streams.Stream;
import org.gcube.data.streams.dsl.Streams;
import org.gcube.data.streams.generators.Generator;
import org.gcube.data.streams.handlers.IgnoreHandler;
import org.gcube.data.tml.proxies.TReader;
import org.gcube.data.tml.proxies.TServiceFactory;
import org.gcube.data.tml.proxies.TWriter;
import org.gcube.data.trees.data.Tree;
import org.gcube.datatransfer.agent.impl.context.ServiceContext;
import org.gcube.datatransfer.agent.impl.event.Events.TransferTopics;
import org.gcube.datatransfer.agent.impl.streams.IdRemover;
import org.gcube.datatransfer.agent.impl.streams.Counter;
import org.gcube.datatransfer.agent.impl.streams.StreamCopyListenerOnRead;
import org.gcube.datatransfer.agent.impl.streams.StreamCopyListenerOnWrite;
import org.gcube.datatransfer.agent.impl.utils.Utils;
import org.gcube.datatransfer.agent.impl.worker.ASyncWorker;
import org.gcube.datatransfer.agent.stubs.datatransferagent.DestData;
import org.gcube.datatransfer.agent.stubs.datatransferagent.SourceData;
import org.gcube.datatransfer.common.outcome.TransferStatus;



/**
 * 
 * @author Andrea Manzi(CERN)
 *
 */
public class TreeManagerAsyncWorker extends ASyncWorker{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	TWriter client_writer =null;
	TReader client_reader =null;
	StreamCopyListenerOnRead listenerForReadTrees = null;
	StreamCopyListenerOnWrite listenerForWrittenTrees = null;
	Counter readTreesCounter= null;
	Counter writtenTreesCounter= null;
	IgnoreHandler IGNORE_POLICY = new IgnoreHandler();
	Stream<Tree> filtered = null;
	Stream<Tree> returnedStream = null;
	
	public TreeManagerAsyncWorker(String tranferID,SourceData source, DestData dest) {
		this.transferId = tranferID;
		this.sourceParameters = source;
		this.destParameters = dest;
		readTreesCounter=new Counter();
		writtenTreesCounter=new Counter();
		listenerForReadTrees = new StreamCopyListenerOnRead(tranferID,source, dest, readTreesCounter);
		listenerForWrittenTrees = new StreamCopyListenerOnWrite(tranferID,writtenTreesCounter);

	}

	@Override
	public Object call() {

		logger.info("Preparing the transfer");
		Stream<Tree> stream = null;

		try {
			ScopeProvider.instance.set(sourceParameters.getScope());
			
			StatefulQuery queryRead = readSource().withId(sourceParameters.getInputSource().getSourceId()).build();
			client_reader = TServiceFactory.reader().matching(queryRead).build();
			
			StatefulQuery queryWrite = writeSource().withId(destParameters.getOutSourceId()).build();
			client_writer = TServiceFactory.writer().matching(queryWrite).build();
			
			stream = client_reader.get(Utils.getPattern(sourceParameters.getInputSource().getPattern()));
			
			filtered = Streams.pipe(stream).through(new IdRemover());	
			filtered = Streams.guard(filtered).with(IGNORE_POLICY);
			filtered = Streams.pipe(filtered).through(readTreesCounter);	
			filtered = Streams.monitor(filtered).with(listenerForReadTrees);			
			
			returnedStream = client_writer.add(filtered);
			returnedStream = Streams.pipe(returnedStream).through(new IdRemover());	
			returnedStream = Streams.guard(returnedStream).with(IGNORE_POLICY);
			returnedStream = Streams.pipe(returnedStream).through(writtenTreesCounter);	
			returnedStream = Streams.monitor(returnedStream).with(listenerForWrittenTrees);					
			
			Utils.consumeStream(returnedStream);
			
			if(readTreesCounter.total==0){ //it was an empty stream
				ServiceContext.getContext().getDbManager().updateReadTreesInTransfer(transferId,0);
				ServiceContext.getContext().getDbManager().updateWrittenTreesInTransfer(transferId,0);
				logger.debug("total read/written trees = 0 - empty stream");
				ServiceContext.getContext().getDbManager().updateTransferObjectStatus(transferId,TransferStatus.DONE.toString());    
			}
			
		}catch (Exception e){
			listenerForReadTrees.sendEvent(TransferTopics.TRANSFER_FAIL,"Error performing the transfer!");
			listenerForWrittenTrees.sendEvent(TransferTopics.TRANSFER_FAIL,"Error performing the transfer!");
			logger.error("Error performing the transfer with id: " +transferId,e);
			return e;
		}
		
		finally {
			try {
				getResource().getWorkerMap().remove(transferId);
			} catch (Exception e) {
				e.printStackTrace();
				return e;
			}
		}
		
		if (this.task.isCancelled()){
			logger.debug("Transfer with id: " +transferId +" has been canceled");
			listenerForReadTrees.sendEvent(TransferTopics.TRANSFER_CANCEL,"Transfer cancelled by the user!");
			listenerForWrittenTrees.sendEvent(TransferTopics.TRANSFER_CANCEL,"Transfer cancelled by the user!");

		}
		return true;

	}

}
