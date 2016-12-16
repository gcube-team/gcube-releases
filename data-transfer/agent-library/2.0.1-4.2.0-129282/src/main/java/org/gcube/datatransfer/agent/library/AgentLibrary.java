package org.gcube.datatransfer.agent.library;

import gr.uoa.di.madgik.commons.server.PortRange;
import gr.uoa.di.madgik.commons.server.TCPConnectionManager;
import gr.uoa.di.madgik.commons.server.TCPConnectionManagerConfig;
import gr.uoa.di.madgik.grs.proxy.tcp.TCPConnectionHandler;
import gr.uoa.di.madgik.grs.proxy.tcp.TCPStoreConnectionHandler;
import gr.uoa.di.madgik.grs.proxy.tcp.TCPWriterProxy;
import gr.uoa.di.madgik.grs.writer.GRS2WriterException;

import java.io.File;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Future;

import org.gcube.common.clients.Call;
import org.gcube.common.clients.delegates.AsyncProxyDelegate;
import org.gcube.common.clients.delegates.Callback;
import org.gcube.common.clients.delegates.ProxyDelegate;
import org.gcube.common.clients.exceptions.ServiceException;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.trees.patterns.Pattern;
import org.gcube.data.trees.patterns.Patterns;
import org.gcube.datatransfer.agent.library.exceptions.*;
import org.gcube.datatransfer.agent.library.fws.AgentServiceJAXWSStubs;
import org.gcube.datatransfer.agent.library.grs.GRSFileWriter;
import org.gcube.datatransfer.agent.library.proxies.AgentService;
import org.gcube.datatransfer.agent.library.proxies.AgentServiceAsync;
import org.gcube.datatransfer.common.agent.Types.*;
import org.gcube.datatransfer.common.objs.LocalSource;
import org.gcube.datatransfer.common.objs.LocalSources;
import org.gcube.datatransfer.common.options.TransferOptions;
import org.gcube.datatransfer.common.outcome.FileTransferOutcome;
import org.gcube.datatransfer.common.outcome.TransferOutcome;
import org.gcube.datatransfer.common.outcome.TreeTransferOutcome;
import org.gcube.datatransfer.common.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;

/**
 * 
 * 
 * @author Andrea Manzi(CERN)
 *
 */
public class AgentLibrary implements AgentService,AgentServiceAsync{


	private final AsyncProxyDelegate<AgentServiceJAXWSStubs> delegate;

	public AgentLibrary(ProxyDelegate<AgentServiceJAXWSStubs> config) {
		this.delegate=new AsyncProxyDelegate<AgentServiceJAXWSStubs>(config);
	}


	static {
		List<PortRange> ports=new ArrayList<PortRange>(); 
		ports.add(new PortRange(4000, 4050));           
		try {
			TCPConnectionManager.Init(
					new TCPConnectionManagerConfig(InetAddress.getLocalHost().getHostName(),
							ports,                               
							true                                   
							));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		TCPConnectionManager.RegisterEntry(new TCPConnectionHandler());      //Register the handler for the gRS2 incoming request
		TCPConnectionManager.RegisterEntry(new TCPStoreConnectionHandler()); //Register the handler for the gRS2 store incoming requests
	}


	Logger logger = LoggerFactory.getLogger(this.getClass().toString());


	/**
	 * {@inheritDoc}
	 */
	public ArrayList<TreeTransferOutcome> startTransferSync(Pattern patternInput, String inputSourceID, String outputStorageId) {

		final StartTransferMessage message = new StartTransferMessage();
		message.setSyncOp(true);
		DestData dest = new DestData();
		dest.setOutSourceId(outputStorageId);
		dest.setScope(ScopeProvider.instance.get());
		message.setDest(dest);
		SourceData source = new SourceData();
		InputPattern input =  new InputPattern();
		try {
			input.setPattern(Utils.toHolder(patternInput));
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		input.setSourceId(inputSourceID);
		source.setInputSource(input);
		source.setScope(ScopeProvider.instance.get());
		source.setType(transferType.TreeBasedTransfer);

		message.setSource(source);

		Call<AgentServiceJAXWSStubs,ArrayList<TreeTransferOutcome>> call = new Call<AgentServiceJAXWSStubs,ArrayList<TreeTransferOutcome>>() {
			@Override 
			public ArrayList<TreeTransferOutcome> call(AgentServiceJAXWSStubs endpoint) throws Exception {
				return  Utils.getTreeOutcomes(endpoint.startTransfer(message));
			}
		};
		try {
			return delegate.make(call);
		}

		catch(Exception e) {
			throw new ServiceException (e);
		}

	}

	public String startTransfer(Pattern patternInput, String inputSourceID, String outputStorageId) throws TransferException {

		final StartTransferMessage message = new StartTransferMessage();
		message.setSyncOp(false);
		DestData dest = new DestData();
		dest.setOutSourceId(outputStorageId);
		dest.setScope(ScopeProvider.instance.get());
		message.setDest(dest);
		SourceData source = new SourceData();
		InputPattern input =  new InputPattern();
		try {
			input.setPattern(Utils.toHolder(patternInput));
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		input.setSourceId(inputSourceID);
		source.setInputSource(input);
		source.setScope(ScopeProvider.instance.get());
		source.setType(transferType.TreeBasedTransfer);

		message.setSource(source);

		Call<AgentServiceJAXWSStubs,String> call = new Call<AgentServiceJAXWSStubs,String>() {
			@Override 
			public String call(AgentServiceJAXWSStubs endpoint) throws Exception {
				return endpoint.startTransfer(message);
			}
		};

		try {
			return delegate.make(call);
		} catch (TransferException e){
			logger.error(e.getMessage());
			throw e;
		}
		catch (ServiceException e){
			logger.error(e.getMessage());
			throw e;
		}
		catch (Exception e){
			logger.error(e.getMessage());
			throw new ServiceException(e);
		}

	}


	/**
	 * {@inheritDoc}
	 * @throws ConfigurationException 
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<FileTransferOutcome>  startTransferSync(ArrayList<URI> inputURIs, String outputFolder,TransferOptions options) throws TransferException, ConfigurationException
	{

		if (options.getType().name().compareTo(storageType.StorageManager.name()) == 0 && options.getStorageManagerDetails() == null)
			throw new ConfigurationException("The Storage Manager configuration details are missing");

		final StartTransferMessage message = new StartTransferMessage();
		message.setSyncOp(true);
		DestData dest = new DestData();
		OutUriData outURI = new OutUriData();
		if (outputFolder.startsWith("/"))
			outputFolder = outputFolder.substring(1);
		outURI.setOutUris(Arrays.asList(new String[] {outputFolder}));

		outURI.setOptions(fillTransferOptions(options));

		dest.setOutUri(outURI);
		dest.setScope(ScopeProvider.instance.get());
		message.setDest(dest);
		SourceData source = new SourceData();
		String []uris = new String[inputURIs.size()];

		for (int i = 0;i<inputURIs.size();i++){
			uris[i]=inputURIs.get(i).toString();
		}
		source.setInputURIs(Arrays.asList(uris));

		source.setScope(ScopeProvider.instance.get());
		source.setType(transferType.FileBasedTransfer);
		message.setSource(source);

		Call<AgentServiceJAXWSStubs,ArrayList<? extends TransferOutcome>> call = 
				new Call<AgentServiceJAXWSStubs,ArrayList<? extends TransferOutcome>>() {

			@Override 
			public ArrayList<? extends TransferOutcome> call(AgentServiceJAXWSStubs endpoint) throws Exception {
				return  Utils.getFileOutcomes(endpoint.startTransfer(message));
			}
		};
		try {
			return (ArrayList<FileTransferOutcome>) delegate.make(call);
		} catch (TransferException e){
			throw e;
		}
		catch (ServiceException e){
			throw e;
		}
		catch (Exception e){
			throw new ServiceException(e);
		}


	}

	/**
	 * 
	 * 
	 * @param inputURLs
	 * @param outputFolder
	 * @param type
	 * @param overwrite
	 * @param storageManagerDetails
	 * @return
	 * @throws TransferException 
	 * @throws ConfigurationException 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public String startTransfer(ArrayList<URI> inputURIs, String outputFolder, TransferOptions options) throws TransferException, ConfigurationException {

		if (options.getType().name().compareTo(storageType.StorageManager.name()) == 0 && options.getStorageManagerDetails() == null)
			throw new ConfigurationException("The Storage Manager configuration details are missing");

		final StartTransferMessage message = new StartTransferMessage();
		message.setSyncOp(false);
		DestData dest = new DestData();
		OutUriData outURI = new OutUriData();
		if (outputFolder.startsWith("/"))
			outputFolder = outputFolder.substring(1);
		outURI.setOutUris(Arrays.asList(new String[] {outputFolder}));

		outURI.setOptions(fillTransferOptions(options));

		dest.setOutUri(outURI);
		dest.setScope(ScopeProvider.instance.get());
		message.setDest(dest);
		SourceData source = new SourceData();
		String []uris = new String[inputURIs.size()];

		for (int i = 0;i<inputURIs.size();i++){
			uris[i]=inputURIs.get(i).toString();
		}
		source.setInputURIs(Arrays.asList(uris));


		source.setScope(ScopeProvider.instance.get());
		source.setType(transferType.FileBasedTransfer);
		message.setSource(source);

		Call<AgentServiceJAXWSStubs,String> call = new Call<AgentServiceJAXWSStubs,String>() {
			@Override 
			public String call(AgentServiceJAXWSStubs endpoint) throws Exception {
				return endpoint.startTransfer(message);
			}
		};

		try {
			return delegate.make(call);
		} catch (TransferException e){
			logger.error(e.getMessage());
			throw e;
		}
		catch (ServiceException e){
			logger.error(e.getMessage());
			throw e;
		}
		catch (Exception e){
			logger.error(e.getMessage());
			throw new ServiceException(e);
		}

	}

	/**
	 * Cancel a scheduled/running transfer 
	 * 
	 * @param transferId
	 * @param forceStop cancel the transfer even if it's running 
	 * @throws Exception 
	 */
	public void cancelTransfer(String transferId, boolean forceCancel) throws CancelTransferException{

		final CancelTransferMessage cancelMessage = new CancelTransferMessage();
		cancelMessage.setForceStop(forceCancel);
		cancelMessage.setTransferId(transferId);

		Call<AgentServiceJAXWSStubs,String> call = new Call<AgentServiceJAXWSStubs,String>() {
			@Override 
			public String call(AgentServiceJAXWSStubs endpoint) throws Exception {
				return endpoint.cancelTransfer(cancelMessage);
			}
		};
		try {
			delegate.make(call);
		}
		catch (CancelTransferException e){

		}
		catch (ServiceException e){
			throw e;
		}
		catch(Exception e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 
	 * @param inputFiles
	 * @param destinationFolder
	 * @param overwrite
	 * @return an Array of <code>FileTransferOutcome</code> 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public  ArrayList<FileTransferOutcome>  copyLocalFiles(ArrayList<File>  inputFiles,String destinationFolder,
			boolean overwrite, boolean unzip) throws TransferException
			{

		GRSFileWriter writer = null;
		try {
			writer = new GRSFileWriter(new TCPWriterProxy(),inputFiles);
		} catch (GRS2WriterException e1) {

			e1.printStackTrace();
		}	

		final StartTransferMessage message = new StartTransferMessage();
		message.setSyncOp(true);
		SourceData sourceData = new SourceData();	
		sourceData.setScope(ScopeProvider.instance.get());

		try {
			sourceData.setInputURIs(Arrays.asList(new String[]{writer.getLocator().toString()}));
		} catch (GRS2WriterException e1) {
			e1.printStackTrace();
		}
		sourceData.setType(transferType.LocalFileBasedTransfer);

		DestData destData = new DestData();
		OutUriData uri = new OutUriData();
		if (destinationFolder.startsWith("/"))
			destinationFolder = destinationFolder.substring(1);
		uri.setOutUris(Arrays.asList(new String[] {destinationFolder}));
		org.gcube.datatransfer.common.agent.Types.TransferOptions stubOptions =
				new org.gcube.datatransfer.common.agent.Types.TransferOptions();
		stubOptions.setOverwrite(overwrite);

		if (unzip) {
			postProcessType [] type = new postProcessType[1];
			type[0] = postProcessType.FileUnzip;	
		}

		uri.setOptions(stubOptions);
		destData.setOutUri(uri);
		message.setSource(sourceData);
		message.setDest(destData);
		writer.start();	
		try {
			writer.join();
		} catch (InterruptedException e1) {

			e1.printStackTrace();
		}

		Call<AgentServiceJAXWSStubs,ArrayList<FileTransferOutcome>> call = 
				new Call<AgentServiceJAXWSStubs,ArrayList<FileTransferOutcome>>() {
			@Override 
			public ArrayList<FileTransferOutcome> call(AgentServiceJAXWSStubs endpoint) throws Exception {
				return  Utils.getFileOutcomes(endpoint.startTransfer(message));
			}
		};
		try {
			return delegate.make(call);
		}

		catch(TransferException e) {
			throw e;
		}
		catch (ServiceException e){
			throw e;
		}
		catch(Exception e) {
			throw new ServiceException(e);
		}

			}


	@SuppressWarnings("unchecked")
	public  ArrayList<LocalSource> getLocalSources(String path){
		final String rootPath=path;

		Call<AgentServiceJAXWSStubs,String> call = 
				new Call<AgentServiceJAXWSStubs,String>() {
			@Override 
			public String call(AgentServiceJAXWSStubs endpoint) throws Exception {
				return endpoint.getLocalSources(rootPath);
			}
		};

		String result=null;
		try {
			result= delegate.make(call);
		}		
		catch (ServiceException e){
			throw e;
		}
		catch(Exception e) {
			throw new ServiceException(e);
		}

		//return value
		if (result==null){
			logger.debug("result from service = null");
			return null;
		}
		else {
			String tmpMsg=result;
			tmpMsg.replaceAll("&lt;", "<");
			tmpMsg=tmpMsg.replaceAll("&gt;", ">");

			try{
				XStream xstream = new XStream();
				LocalSources sources= new LocalSources();
				sources=(LocalSources)xstream.fromXML(tmpMsg);
				ArrayList<LocalSource> listToReturn = new ArrayList<LocalSource>();
				for(LocalSource source : sources.getList()){
					listToReturn.add(source);
				}
				return listToReturn;
			}catch(Exception e ){
				e.printStackTrace();
				return null;
			}
		}
	}

	public String createTreeSource(String sourceID,String address, int port){
		final CreateTreeSourceMsg msg = new CreateTreeSourceMsg();
		msg.setSourceID(sourceID);
		msg.setEndpoint(address);
		msg.setPort(port);
		
		Call<AgentServiceJAXWSStubs,String> call = 
				new Call<AgentServiceJAXWSStubs,String>() {
			@Override 
			public String call(AgentServiceJAXWSStubs endpoint) throws Exception {
				return endpoint.createTreeSource(msg);
			}
		};

		String result=null;
		try {
			result= delegate.make(call);
		}		
		catch (ServiceException e){
			throw e;
		}
		catch(Exception e) {
			throw new ServiceException(e);
		}

		//return value
		if (result==null)logger.debug("result from service = null");
		return result;
	}
	
	public String removeGenericResource(final String sourceID){
		
		Call<AgentServiceJAXWSStubs,String> call = 
				new Call<AgentServiceJAXWSStubs,String>() {
			@Override 
			public String call(AgentServiceJAXWSStubs endpoint) throws Exception {
				return endpoint.removeGenericResource(sourceID);
			}
		};

		String result=null;
		try {
			result= delegate.make(call);
		}		
		catch (ServiceException e){
			throw e;
		}
		catch(Exception e) {
			throw new ServiceException(e);
		}

		//return value
		if (result==null)logger.debug("result from service = null");
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public  ArrayList<String> getTreeSources(final String reader_or_writer_TYPE){

		Call<AgentServiceJAXWSStubs,String> call = 
				new Call<AgentServiceJAXWSStubs,String>() {
			@Override 
			public String call(AgentServiceJAXWSStubs endpoint) throws Exception {
				return endpoint.getTreeSources(reader_or_writer_TYPE);
			}
		};

		String result=null;
		try {
			result= delegate.make(call);
		}		
		catch (ServiceException e){
			throw e;
		}
		catch(Exception e) {
			throw new ServiceException(e);
		}

		//return value
		if (result==null){
			logger.debug("result from service = null");
			return null;
		}
		else {
			String tmpMsg=result;
			tmpMsg.replaceAll("&lt;", "<");
			tmpMsg=tmpMsg.replaceAll("&gt;", ">");

			try{
				XStream xstream = new XStream();
				ArrayList<String> sourceIds;
				sourceIds=(ArrayList<String>)xstream.fromXML(tmpMsg);
				return sourceIds;
			}catch(Exception e ){
				e.printStackTrace();
				return null;
			}
		}
	}
	@SuppressWarnings("unchecked")
	@Override
	public Future<?> startTransfer(Pattern patternInput,
			String inputSourceID, String outputStorageId, Callback callback)
			{
		final StartTransferMessage message = new StartTransferMessage();
		message.setSyncOp(false);
		DestData dest = new DestData();
		dest.setOutSourceId(outputStorageId);
		dest.setScope(ScopeProvider.instance.get());
		message.setDest(dest);
		SourceData source = new SourceData();
		InputPattern input =  new InputPattern();
		try {
			input.setPattern(Utils.toHolder(patternInput));
		} catch (Exception e1) {
			e1.printStackTrace();

		}
		input.setSourceId(inputSourceID);
		source.setInputSource(input);
		source.setScope(ScopeProvider.instance.get());
		source.setType(transferType.TreeBasedTransfer);
		message.setSource(source);

		Call<AgentServiceJAXWSStubs,String> call = new Call<AgentServiceJAXWSStubs,String>() {
			@Override 
			public String call(AgentServiceJAXWSStubs endpoint) throws Exception {
				return endpoint.startTransfer(message);
			}
		};
		try {
			return delegate.makeAsync(call,callback);
		}
		catch (ServiceException e){
			throw e;
		}

		catch(Exception e) {
			throw new ServiceException(e);
		}
			}



	@SuppressWarnings("unchecked")
	@Override
	public Future<?> startTransfer(ArrayList<URI> inputURIs, String outputFolder,TransferOptions options,
			Callback callback)
					throws ConfigurationException {

		final StartTransferMessage message = new StartTransferMessage();
		message.setSyncOp(true);
		DestData dest = new DestData();
		OutUriData outURI = new OutUriData();
		if (outputFolder.startsWith("/"))
			outputFolder = outputFolder.substring(1);
		outURI.setOutUris(Arrays.asList(new String[] {outputFolder}));

		outURI.setOptions(fillTransferOptions(options));

		dest.setOutUri(outURI);
		dest.setScope(ScopeProvider.instance.get());
		message.setDest(dest);

		SourceData source = new SourceData();

		String []uris = new String[inputURIs.size()];

		for (int i = 0;i<inputURIs.size();i++){
			uris[i]=inputURIs.get(i).toString();
		}
		source.setInputURIs(Arrays.asList(uris));

		source.setScope(ScopeProvider.instance.get());
		source.setType(transferType.FileBasedTransfer);
		message.setSource(source);

		Call<AgentServiceJAXWSStubs,ArrayList<? extends TransferOutcome>> call = 
				new Call<AgentServiceJAXWSStubs,ArrayList<? extends TransferOutcome>>() {

			@Override 
			public ArrayList<? extends TransferOutcome> call(AgentServiceJAXWSStubs endpoint) throws Exception {
				if ( message.getSource().getType().name().compareTo(transferType.FileBasedTransfer.name())==0)
					return  Utils.getFileOutcomes(endpoint.startTransfer(message));
				else 
					return  Utils.getTreeOutcomes(endpoint.startTransfer(message));
			}
		};
		try {
			return  delegate.makeAsync(call,callback);
		}
		catch (ServiceException e){
			throw e;
		}
		catch(Exception e){
			throw new ServiceException(e);
		}
	}



	@Override
	public String monitorTransfer(final String transferId) throws MonitorTransferException {
		Call<AgentServiceJAXWSStubs,String> call = new Call<AgentServiceJAXWSStubs,String>() {
			@Override 
			public String call(AgentServiceJAXWSStubs endpoint) throws Exception {
				return endpoint.monitorTransfer(transferId);
			}
		};
		try {
			return delegate.make(call);
		}
		catch(MonitorTransferException e) {
			throw e;
		}
		catch(ServiceException e) {
			throw e;
		}
		catch(Exception e){
			throw new ServiceException(e);
		}
	}



	@Override
	public <T extends TransferOutcome> ArrayList<T> getTransferOutcomes(
			final String transferId, final Class<T> outcomeType) throws GetTransferOutcomesException {

			Call<AgentServiceJAXWSStubs, ArrayList<T>> call = new Call<AgentServiceJAXWSStubs, ArrayList<T>>() {

			@Override 
			public  ArrayList<T> call(AgentServiceJAXWSStubs endpoint) throws Exception {
			//	if (outcomeType.getClass()==FileTransferOutcome.class.getClass())   //this comparison does not work at all .. 
				if (outcomeType.getName().compareTo(FileTransferOutcome.class.getName())==0){
					logger.debug("FileTransferOutcome ... ");
					String rs = endpoint.getTransferOutcomes(transferId);
					if(rs==null)return null;
					return  Utils.getFileOutcomes(rs);
				}
				else {					
					logger.debug("TreeTransferOutcome ... ");
					String rs = endpoint.getTransferOutcomes(transferId);
					if(rs==null)return null;
					return  Utils.getTreeOutcomes(rs);
				}
			}
		};
		try {
			return delegate.make(call);
		}

		catch(GetTransferOutcomesException e) {
			throw e ;
		}
		catch (ServiceException e){
			throw e;
		}
		catch(Exception e) {
			throw new ServiceException(e);
		}
	}



	@Override
	public String startTransfer(DataSource source, DataStorage storage,
			TransferOptions options) throws ConfigurationException,
			TransferException {
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String startTransfer(ArrayList<URI> inputURIs, ArrayList<URI> outputURIs, TransferOptions options)
			throws ConfigurationException, TransferException {

		if (options.getType().name().compareTo(storageType.DataStorage.name()) != 0 )
			throw new ConfigurationException("The operation is available only selecting DataStorage as storage Type");

		final StartTransferMessage message = new StartTransferMessage();
		message.setSyncOp(false);
		DestData dest = new DestData();
		OutUriData outURI = new OutUriData();
		String [] outURIs = new String[outputURIs.size()];

		for (int i = 0;i<outputURIs.size();i++){
			outURIs[i]=outputURIs.get(i).toString();
		}

		outURI.setOutUris(Arrays.asList(outURIs));
		org.gcube.datatransfer.common.agent.Types.TransferOptions stubOptions = 
				new org.gcube.datatransfer.common.agent.Types.TransferOptions();
		stubOptions.setOverwrite(options.isOverwriteFile());
		stubOptions.setStorageType(options.getType());
		stubOptions.setTransferTimeout(options.getTransferTimeout());
		outURI.setOptions(stubOptions);
		dest.setOutUri(outURI);
		dest.setScope(ScopeProvider.instance.get());
		message.setDest(dest);
		SourceData source = new SourceData();
		String []uris = new String[inputURIs.size()];

		for (int i = 0;i<inputURIs.size();i++){
			uris[i]=inputURIs.get(i).toString();
		}
		source.setInputURIs(Arrays.asList(uris));

		source.setScope(ScopeProvider.instance.get());
		source.setType(transferType.FileBasedTransfer);
		message.setSource(source);

		Call<AgentServiceJAXWSStubs,String> call = new Call<AgentServiceJAXWSStubs,String>() {
			@Override 
			public String call(AgentServiceJAXWSStubs endpoint) throws Exception {
				return endpoint.startTransfer(message);
			}
		};

		try {
			return delegate.make(call);
		} catch (TransferException e){
			logger.error(e.getMessage());
			throw e;
		}
		catch (ServiceException e){
			logger.error(e.getMessage());
			throw e;
		}
		catch (Exception e){
			logger.error(e.getMessage());
			throw new ServiceException(e);
		}
	}

	private org.gcube.datatransfer.common.agent.Types.TransferOptions fillTransferOptions(TransferOptions options){
		org.gcube.datatransfer.common.agent.Types.TransferOptions stubOptions = new
				org.gcube.datatransfer.common.agent.Types.TransferOptions();
		stubOptions.setOverwrite(options.isOverwriteFile());
		stubOptions.setStorageType(options.getType());
		stubOptions.setTransferTimeout(options.getTransferTimeout());
		int size = 0;
		if (options.isCovertFile()) size++;
		if (options.isUnzipFile()) size++;
		if (options.isDeleteOriginalFile()) size++;


		if (size!=0){
			int i=0;
			postProcessType [] type = new postProcessType[size];
			if (options.isUnzipFile()){
				type[i] = postProcessType.FileUnzip;
				i++;}
			if (options.isCovertFile()){
				type[i] = postProcessType.FileConversion;
				stubOptions.setConversionType(options.getConversionType().name());
				i++;
			}
			if (options.isDeleteOriginalFile()){
				type[i] = postProcessType.OriginalFileRemove;
			}
			stubOptions.setPostProcess(Arrays.asList(type));

		}

		if (options.getType().name().compareTo(storageType.StorageManager.name()) ==0)
			stubOptions.setStorageManagerDetails(options.getStorageManagerDetails());
		return stubOptions;

	}



	@Override
	public MonitorTransferReportMessage monitorTransferWithProgress(final String transferId)
			throws MonitorTransferException {
		Call<AgentServiceJAXWSStubs,MonitorTransferReportMessage> call = new Call<AgentServiceJAXWSStubs,MonitorTransferReportMessage>() {
			@Override 
			public MonitorTransferReportMessage call(AgentServiceJAXWSStubs endpoint) throws Exception {
				return endpoint.monitorTransferWithProgress(transferId);
			}
		};
		try {
			return delegate.make(call);
		}
		catch(MonitorTransferException e) {
			throw e;
		}
		catch(ServiceException e) {
			throw e;
		}
		catch(Exception e){
			throw new ServiceException(e);
		}
	}
}
