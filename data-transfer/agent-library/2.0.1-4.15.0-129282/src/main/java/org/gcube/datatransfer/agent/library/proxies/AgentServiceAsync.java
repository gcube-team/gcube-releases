package org.gcube.datatransfer.agent.library.proxies;

import java.net.URI;
import java.util.ArrayList;
import java.util.concurrent.Future;

import org.gcube.common.clients.delegates.Callback;
import org.gcube.data.trees.patterns.Pattern;
import org.gcube.datatransfer.agent.library.exceptions.ConfigurationException;
import org.gcube.datatransfer.agent.library.exceptions.TransferException;
import org.gcube.datatransfer.common.options.TransferOptions;


/**
 * 
 * @author Andrea Manzi(CERN)
 *
 */
public interface AgentServiceAsync {

	
	/**
	 * 
	 * @param patternInput
	 * @param inputSourceID
	 * @param outputStorageId
	 * @param callback
	 * @return
	 * @throws TransferException
	 */
	public Future<?> startTransfer(Pattern patternInput, String inputSourceID, String outputStorageId, Callback callback) ;
	
	/**
	 * 
	 * @param inputURIs
	 * @param outputFolder
	 * @param type
	 * @param overwrite
	 * @param callback
	 * @param storageManagerDetails
	 * @return
	 * @throws ConfigurationException
	 * @throws TransferException
	 */
	public Future<?> startTransfer(ArrayList<URI> inputURIs, String outputFolder, TransferOptions options,Callback callback) throws ConfigurationException;
	
}
