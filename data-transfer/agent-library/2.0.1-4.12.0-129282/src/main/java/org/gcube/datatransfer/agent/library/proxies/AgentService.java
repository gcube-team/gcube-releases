package org.gcube.datatransfer.agent.library.proxies;

import java.io.File;
import java.net.URI;

import java.util.ArrayList;

import org.gcube.data.trees.patterns.Pattern;
import org.gcube.datatransfer.agent.library.DataSource;
import org.gcube.datatransfer.agent.library.DataStorage;
import org.gcube.datatransfer.agent.library.exceptions.CancelTransferException;
import org.gcube.datatransfer.agent.library.exceptions.ConfigurationException;
import org.gcube.datatransfer.agent.library.exceptions.GetTransferOutcomesException;
import org.gcube.datatransfer.agent.library.exceptions.MonitorTransferException;
import org.gcube.datatransfer.agent.library.exceptions.TransferException;
import org.gcube.datatransfer.common.agent.Types.MonitorTransferReportMessage;
import org.gcube.datatransfer.common.options.TransferOptions;
import org.gcube.datatransfer.common.outcome.FileTransferOutcome;
import org.gcube.datatransfer.common.outcome.TransferOutcome;
import org.gcube.datatransfer.common.outcome.TreeTransferOutcome;

/**
 * 
 * @author Andrea Manzi(CERN)
 *
 */
public interface AgentService {
	
	/**
	 * 
	 * @param source
	 * @param storage
	 * @param options
	 * @return
	 * @throws ConfigurationException
	 * @throws TransferException
	 */
	public String startTransfer(DataSource source, DataStorage storage, TransferOptions options) throws ConfigurationException,TransferException;

	
	/**
	 * 
	 * @param patternInput
	 * @param inputSourceID
	 * @param outputStorageId
	 * @return  ArrayList<TreeTransferOutcome> 
	 * @throws TransferException
	 */
	public  ArrayList<TreeTransferOutcome>  startTransferSync(Pattern patternInput, String inputSourceID, String outputStorageId);

	/**
	 * 
	 * @param inputURIs
	 * @param outputFolder
	 * @param options
	 * @return
	 * @throws ConfigurationException
	 * @throws TransferException
	 */
	public String startTransfer(ArrayList<URI> inputURIs, String outputFolder, TransferOptions options) throws ConfigurationException,TransferException;
	
	/**
	 * 
	 * @param inputURIs
	 * @param outputURIs
	 * @param options
	 * @return
	 * @throws ConfigurationException
	 * @throws TransferException
	 */
	public String startTransfer(ArrayList<URI> inputURIs, ArrayList<URI> outputURIs, TransferOptions options) throws ConfigurationException,TransferException;
	
	
	/**
	 * 
	 * @param transferId
	 * @param forceCancel
	 * @throws CancelTransferException
	 */
	public void cancelTransfer(String transferId, boolean forceCancel) throws CancelTransferException;
	
	/**
	 * 
	 * @param transferId
	 * @return
	 * @throws MonitorTransferException
	 */
	public String monitorTransfer(String transferId) throws MonitorTransferException;
	
	/**
	 * 
	 * @param transferId
	 * @return
	 * @throws MonitorTransferException
	 */
	public MonitorTransferReportMessage monitorTransferWithProgress(String transferId) throws MonitorTransferException;
	/**
	/**
	 * 
	 * @param transferId
	 * @param outcomeType
	 * @return
	 * @throws GetTransferOutcomesException
	 */
	public <T extends TransferOutcome> ArrayList<T> getTransferOutcomes(String transferId,Class<T> outcomeType) throws GetTransferOutcomesException;
	
	/**
	 * 
	 * @param inputURLs
	 * @param outputFolder
	 * @param type
	 * @param overwrite
	 * @param storageManagerDetails
	 * @return
	 * @throws TransferException
	 * @throws ConfigurationException 
	 */
	public  ArrayList<FileTransferOutcome>  startTransferSync(ArrayList<URI> inputURIs, String outputFolder,TransferOptions options) throws TransferException, ConfigurationException;
	
	/**
	 * 
	 * @param inputFiles
	 * @param destinationFolder
	 * @param overwrite
	 * @param unzip
	 * @return
	 * @throws TransferException
	 */

	public ArrayList<FileTransferOutcome>  copyLocalFiles(ArrayList<File>  inputFiles,String destinationFolder,boolean overwrite,boolean unzip) throws TransferException;
	
}

