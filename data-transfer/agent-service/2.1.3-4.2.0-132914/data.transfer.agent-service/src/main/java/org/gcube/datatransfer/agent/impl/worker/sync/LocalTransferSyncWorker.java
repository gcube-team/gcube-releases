package org.gcube.datatransfer.agent.impl.worker.sync;

import gr.uoa.di.madgik.grs.writer.GRS2WriterException;

import java.io.File;
import java.net.URI;

import org.gcube.datatransfer.agent.impl.context.ServiceContext;
import org.gcube.datatransfer.agent.impl.grs.GRSFileReader;
import org.gcube.datatransfer.agent.impl.streams.Counter;
import org.gcube.datatransfer.agent.impl.streams.StreamCopyListenerOnRead;
import org.gcube.datatransfer.agent.impl.worker.SyncWorker;
import org.gcube.datatransfer.agent.stubs.datatransferagent.DestData;
import org.gcube.datatransfer.agent.stubs.datatransferagent.SourceData;

/**
 * 
 * @author Andrea Manzi(CERN)
 *
 */
public class LocalTransferSyncWorker extends SyncWorker{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	StreamCopyListenerOnRead listener = null;
	GRSFileReader t = null;
	Counter treeGenerator=null;
	
	public LocalTransferSyncWorker(String tranferID,SourceData source, DestData dest) throws GRS2WriterException {
		this.transferId = tranferID;
		this.sourceParameters = source;
		this.destParameters = dest;
		treeGenerator=new Counter();
		listener = new StreamCopyListenerOnRead(tranferID,source, dest, treeGenerator);
	}
	@Override
	public Object call() throws Exception {
		 String uri = sourceParameters.getInputURIs()[0];
		 String outFolder = destParameters.getOutUri().getOutUris()[0];
		 boolean overwrite = destParameters.getOutUri().getOptions().isOverwrite();
		 File out = new File(ServiceContext.getContext().getVfsRoot()+File.separator+outFolder);	 
		 t= new GRSFileReader(new URI(uri),out,overwrite);
		 t.run();
		 return t.getOutcomeLocator();
	}
	
	@Override
	public String getOutcomeLocator() throws GRS2WriterException {
		return t.getOutcomeLocator();
	}

}
