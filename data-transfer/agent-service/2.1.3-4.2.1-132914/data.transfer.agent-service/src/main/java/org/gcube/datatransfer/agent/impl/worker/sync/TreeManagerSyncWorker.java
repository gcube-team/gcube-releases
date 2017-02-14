package org.gcube.datatransfer.agent.impl.worker.sync;

import static org.gcube.data.tml.proxies.TServiceFactory.*;
import gr.uoa.di.madgik.grs.writer.GRS2WriterException;

import org.gcube.common.clients.fw.queries.StatefulQuery;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.streams.Stream;
import org.gcube.data.streams.dsl.Streams;
import org.gcube.data.tml.proxies.Binding;
import org.gcube.data.tml.proxies.TReader;
import org.gcube.data.tml.proxies.TServiceFactory;
import org.gcube.data.tml.proxies.TWriter;
import org.gcube.data.trees.data.Tree;
import org.gcube.datatransfer.agent.impl.event.Events.TransferTopics;
import org.gcube.datatransfer.agent.impl.streams.IdRemover;
import org.gcube.datatransfer.agent.impl.streams.Counter;
import org.gcube.datatransfer.agent.impl.streams.StreamCopyListenerOnRead;
import org.gcube.datatransfer.agent.impl.worker.SyncWorker;
import org.gcube.datatransfer.agent.stubs.datatransferagent.DestData;
import org.gcube.datatransfer.agent.stubs.datatransferagent.SourceData;
import org.gcube.datatransfer.agent.impl.utils.Utils;

/**
 * 
 * @author andrea
 *
 */
public class TreeManagerSyncWorker extends SyncWorker{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	TWriter client_writer =null;
	TReader client_reader =null;
	StreamCopyListenerOnRead listener = null;
	Counter treeGenerator=null;
	Stream<Tree> filtered = null;
	
	public TreeManagerSyncWorker(String tranferID,SourceData source, DestData dest) {
		this.transferId = tranferID;
		this.sourceParameters = source;
		this.destParameters = dest;
		treeGenerator=new Counter();
		listener = new StreamCopyListenerOnRead(tranferID,source, dest, treeGenerator);
	}

	@Override
	public Object call() {

		logger.info("Preparing the transfer");
		Stream<Tree> stream = null;

		try {
			
			StatefulQuery queryRead = readSource().withId(sourceParameters.getInputSource().getSourceId()).build();
			client_reader = TServiceFactory.reader().matching(queryRead).build();
			

			//method to use for my-container testing
			//client_reader = TServiceFactory.reader().at(bindSource().readerRef()).build();
			//client_writer = TServiceFactory.writer().at(bindSource().writerRef()).build();
			
			
			StatefulQuery queryWrite = writeSource().withId(destParameters.getOutSourceId()).build();
			client_writer = TServiceFactory.writer().matching(queryWrite).build();
			
			ScopeProvider.instance.set(sourceParameters.getScope());
			
			stream = client_reader.get(Utils.getPattern(sourceParameters.getInputSource().getPattern()));
			
			filtered = Streams.pipe(stream).through(new IdRemover());
			filtered = Streams.monitor(filtered).with(listener);
			filtered = client_writer.add(filtered);
		
		}catch (Exception e){
			listener.sendEvent(TransferTopics.TRANSFER_FAIL,"Error performing the transfer!");
			logger.error("Error performing the transfer with id: " +transferId,e);
			return e;
		}
		
		return true;

	}

	@Override
	public String getOutcomeLocator() throws GRS2WriterException {
		if (filtered == null)
			return null;
		else return filtered.locator().toString();
	}
	
	//used only for test in my-container
	private Binding bindSource() throws Exception{
		
//		TBinder binder = TServiceFactory.binder().at("localhost",9999).build();
//		
//		BindParameters params = new BindParameters();
//		params.setPlugin("tree-repository");
//		BindSource request  =new BindSource("test-source");
//		
//		RequestBinder bind = new RequestBinder();
//		ScopeProvider.instance.set(TestUtils.VO.toString());
//		
//		Payload payload = new Payload();
//		payload.set_any(new MessageElement[]{new MessageElement(bind.bind(request))});
//		params.setPayload(payload);
//		
//		params.setBroadcast(false);
//		
//		BindParams par = new BindParams(params);
//		
//		List<Binding> bindings = binder.bind(par);
//
//		return bindings.get(0);	
		return null;
		
	}

}
