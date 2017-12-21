package org.gcube.data.transfer.service.transfers.engine.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.gcube.data.transfer.model.ExecutionReport;
import org.gcube.data.transfer.model.PluginInvocation;
import org.gcube.data.transfer.model.TransferTicket;
import org.gcube.data.transfer.model.TransferTicket.Status;
import org.gcube.data.transfer.model.options.TransferOptions.TransferMethod;
import org.gcube.data.transfer.model.settings.FileUploadSettings;
import org.gcube.data.transfer.model.settings.HttpDownloadSettings;
import org.gcube.data.transfer.plugin.fails.PluginException;
import org.gcube.data.transfer.service.transfers.engine.PersistenceProvider;
import org.gcube.data.transfer.service.transfers.engine.PluginManager;
import org.gcube.data.transfer.service.transfers.engine.faults.ManagedException;
import org.gcube.data.transfer.service.transfers.engine.faults.NotSupportedMethodException;
import org.gcube.data.transfer.service.transfers.engine.faults.PluginNotFoundException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractTicketHandler {

	private TransferTicket ticket;

	PersistenceProvider persistenceProvider;
	PluginManager pluginManager;

	
	private MessageDigest md;
	

	public AbstractTicketHandler(PersistenceProvider persProv,PluginManager plugMan, TransferTicket ticket) {		
		this.persistenceProvider=persProv;
		this.pluginManager=plugMan;
		this.ticket=ticket;
		try {
			md = MessageDigest.getInstance("SHA1");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Unable to initialize",e);
		}
	}

	protected void onStep(String msg,double progress,Status status,long transferredBytes){
		ticket.setStatus(status);
		ticket.setMessage(msg);
		ticket.setPercent(progress);
		ticket.setTransferredBytes(transferredBytes);
		try{
			long elapsedTime=System.currentTimeMillis()-ticket.getSubmissionTime().getValue().getTimeInMillis();
			long average=(transferredBytes/((elapsedTime==0?1:elapsedTime)))*1000;
			ticket.setAverageTransferSpeed(average);
		}catch(Exception e){
			log.warn("Unable to evaluate average ",e);
		}
	}
	protected void onError(String message){
		onStep(message,ticket.getPercent(),Status.ERROR);
	}

	protected void onStep(String msg,double progress,Status status){
		onStep(msg,progress,status,ticket.getTransferredBytes());
	}

	protected void addExecutionReport(ExecutionReport toAdd){
		if(ticket.getExecutionReports()==null) ticket.setExecutionReports(new HashMap<String,ExecutionReport>());
		ticket.getExecutionReports().put(toAdd.getInvocation().getPluginId(), toAdd);
		onStep(ticket.getMessage(),ticket.getPercent(),ticket.getStatus());
	}
	
	
	public TransferTicket getTicket(){
		return ticket;
	}
	
	
	
	public TransferTicket handle(){
		
		InputStream is=null;
		BufferedOutputStream out=null;
		Boolean completedTransfer=false;
		File destination=null;
		File tempFile=null;
		
		try{
			if(ticket.getSettings().getOptions().getMethod().equals(TransferMethod.DirectTransfer))
				throw new NotSupportedMethodException("Unable to manage request [ID "+ticket.getId()+"]. Method not supported : "+ticket.getSettings().getOptions().getMethod());


			log.trace("Request handling started. Ticket is "+ticket);

			String destinationFileName=ticket.getDestinationSettings().getDestinationFileName();
			log.debug("Checking destination file name : {} ",destinationFileName);
			if(destinationFileName==null||destinationFileName.isEmpty()){				
				setDestinationFileName(ticket);
				log.trace("Destination filename not specified. Automatically set {} ",ticket.getDestinationSettings().getDestinationFileName());
			}
			
			
			onStep("Checking destination",0d,Status.TRANSFERRING,0l);
			destination =persistenceProvider.prepareDestination(ticket.getDestinationSettings());
			ticket.setDestinationFileName(destination.getAbsolutePath());
			onStep("Opening input stream",0d,Status.TRANSFERRING,0l);

			
			is=getInputStream();
			tempFile=File.createTempFile("transfer_"+ticket.getId(), ".tmp");

			try{
				out=new BufferedOutputStream(new FileOutputStream(tempFile));
			} catch (IOException e) {
				log.warn("Unable to create destination file.",e);
				throw new ManagedException("Cannot save file in host");
			}
			
			String checksum=transferStream(is, out);
			
			completedTransfer=true;
			// IF TRANSFER FAILS, EXCEPTIONS AR THROWN

			log.debug("Completed transfer to {} [ SHA1 : {}]. moving to destination {}  ",tempFile.getAbsolutePath(),checksum,destination.getAbsolutePath());
			Files.copy(tempFile.toPath(), destination.toPath(),StandardCopyOption.REPLACE_EXISTING);
			Files.deleteIfExists(tempFile.toPath());
			log.debug("Moved. Size is [temp : {} , dest : {}] ",tempFile.length(),destination.length());
			
			//Plugin execution
			if(ticket.getPluginInvocations()!=null){				
				for(PluginInvocation invocation:ticket.getPluginInvocations()){
					log.debug("Execution {}",invocation);
					if(invocation.getParameters()!=null && invocation.getParameters().containsValue(PluginInvocation.DESTINATION_FILE_PATH)){
						log.debug("Checking for param value : "+PluginInvocation.DESTINATION_FILE_PATH);
						for(Entry<String,String> param:invocation.getParameters().entrySet())
							if(param.getValue().equals(PluginInvocation.DESTINATION_FILE_PATH)){
								log.debug("Setting {} = {} ",param.getKey(),ticket.getDestinationFileName());
								param.setValue(ticket.getDestinationFileName());
							}

					}
					log.debug("Executing invocation {} ",invocation);
					onStep("Executing invocation "+invocation.getPluginId(),1d,Status.PLUGIN_EXECUTION);
					ExecutionReport report=pluginManager.execute(invocation,destination.getAbsolutePath());
					log.debug("Adding plugin execution report {} to ticket {} ",report,ticket.getId());
					addExecutionReport(report);
				}
			}

			
			log.info("Completed Transfer for ticket ID {} ",ticket.getId());
			onStep("Completed transfer",1d,Status.SUCCESS);		
		
		}catch(PluginNotFoundException e){
			log.error("Error while serving {} ",ticket,e);
			onError("Invalid plugin invocation "+e.getMessage());
		}catch(PluginException e){
			log.error("Error while serving {} ",ticket,e);
			onError("Failed Plugin Execution : "+e.getMessage());
		}catch(NotSupportedMethodException e){
			log.error("Error while serving {} ",ticket,e);
			onError(e.getMessage());
		}catch(ManagedException e){
			log.error("Error while serving {} ",ticket,e);
			onError(e.getMessage());
		}catch(Throwable t){
			onError("Unexpected error while downloading : "+t.getMessage());
			log.error("Unexpected error occurred",t);
		}finally{
			log.debug("Finalizing transfer, ticket ID {} ",ticket.getId());
			if(out!=null)IOUtils.closeQuietly(out);
			if(is!=null)IOUtils.closeQuietly(is);
			
			if((!completedTransfer)&& (destination!=null) && (destination.exists())) {
				log.debug("Removing incomplete transfer..");
				try{
					FileUtils.forceDelete(destination);
				}catch(Exception e){
					log.warn("Unable to clean {} ",destination);
				}
			}

		}
		return getTicket();
	}

	
	
	private String transferStream(InputStream in, OutputStream out) throws ManagedException{
		md.reset();
		
		long receivedTotal=0l;

		try{
			byte[] internalBuf=new byte[1024];
			int received=0;
			while ((received=in.read(internalBuf))!=-1){
				md.update(internalBuf, 0, received);
				out.write(internalBuf,0,received);
				receivedTotal+=received;
				onStep("Transferring",0d,Status.TRANSFERRING,receivedTotal);					            
			}
			out.flush();
			
			 byte[] mdbytes = md.digest();

			//convert the byte to hex format
		    StringBuffer sb = new StringBuffer("");
		    for (int i = 0; i < mdbytes.length; i++) {
		    	sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
		    }
		    log.debug("Completed transfer phase for ticket ID {}. Transferred {} bytes. ",ticket.getId(),receivedTotal);
		    return sb.toString();
		}catch(IOException e){
			log.debug("Unable to read from source",e);
			throw new ManagedException("Unable to read from source.");
		}
	}
	
	private InputStream getInputStream() throws ManagedException{
		switch(ticket.getSettings().getOptions().getMethod()){
		case HTTPDownload:{  
			try{
				HttpDownloadSettings options=(HttpDownloadSettings) (ticket.getSettings());
				String sourceUrl=resolveRedirects(options.getSource().toString());
				return new BufferedInputStream(new URL(sourceUrl).openStream());
			}catch(Exception e){
				log.debug("Unable to open connection ",e);
				throw new ManagedException("Cannot open connection to source");
			}
		}
		case FileUpload :{
			try{
				FileUploadSettings options=(FileUploadSettings) (ticket.getSettings());
				return new BufferedInputStream(options.getPassedStream());
			}catch(Exception e){
				log.debug("Unable to open connection ",e);
				throw new ManagedException("Cannot open connection to source");
			}
		}
		default:
			throw new ManagedException(ticket.getSettings().getOptions().getMethod()+" cannot be managed");
		}
	}
	
	
	private static final void setDestinationFileName(TransferTicket ticket){
		
		switch(ticket.getSettings().getOptions().getMethod()){
		case HTTPDownload : {
			HttpDownloadSettings options=(HttpDownloadSettings) (ticket.getSettings());		
			String toSetFilename=retrieveFileName(options.getSource().toString(), ticket.getId());			
			ticket.getDestinationSettings().setDestinationFileName(toSetFilename);
			break;
		}
		default : ticket.getDestinationSettings().setDestinationFileName(ticket.getId());
		}		
		log.info("Set filename in ticket {} ",ticket);
	}
	
	
	private static String resolveRedirects(String url) throws IOException{
		log.debug("Resolving redirect for url {} ",url);
		URL urlObj=new URL(url);
		HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
		int status=connection.getResponseCode();
		if(status>=300&&status<400){
			String newUrl=connection.getHeaderField("Location");
			log.debug("Following redirect from {} to {} ",url,newUrl);
			return resolveRedirects(newUrl);
		}else return url;
	}
	
	
	private static String retrieveFileName(String url,String defaultName){
		
		try{
			String fileName=null;
			log.debug("Resolving name for url {} ",url);
			url=resolveRedirects(url);
			URL urlObj = new URL(url);

			HttpURLConnection connection = (HttpURLConnection) urlObj
					.openConnection();
			
			String contentDisposition = connection
					.getHeaderField("Content-Disposition");
			
			
			
			Pattern regex = Pattern.compile("(?<=filename=\").*?(?=\")");
			Matcher regexMatcher = regex.matcher(contentDisposition);
			if (regexMatcher.find()) {
				fileName = regexMatcher.group();
			}

			if (fileName == null || fileName.isEmpty()) {
				throw new Exception ("Filename was null or empty.");
			}

			return fileName;		
		}catch (Throwable t){
			log.debug("Unable to retrieve name from url {}, reverting to default {}.",url,defaultName,t);
			return defaultName;
		}
	}
}
