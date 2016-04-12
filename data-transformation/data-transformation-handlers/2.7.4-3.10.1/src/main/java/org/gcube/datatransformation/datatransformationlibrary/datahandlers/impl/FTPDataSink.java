package org.gcube.datatransformation.datatransformationlibrary.datahandlers.impl;

import java.io.InputStream;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gcube.datatransformation.datatransformationlibrary.dataelements.DataElement;
import org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataHandlerDefinitions;
import org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataSink;
import org.gcube.datatransformation.datatransformationlibrary.model.Parameter;
import org.gcube.datatransformation.datatransformationlibrary.reports.ReportManager;
import org.gcube.datatransformation.datatransformationlibrary.reports.Record.Status;
import org.gcube.datatransformation.datatransformationlibrary.reports.Record.Type;
import org.gcube.datatransformation.datatransformationlibrary.statistics.Metric;
import org.gcube.datatransformation.datatransformationlibrary.statistics.StatisticsManager;
import org.gcube.datatransformation.datatransformationlibrary.statistics.StatisticsManager.MetricType;
import org.gcube.datatransformation.datatransformationlibrary.utils.MimeUtils;

/**
 * @author Dimitris Katris, NKUA
 *
 * <p>
 * This <tt>DataSink</tt> stores <tt>DataElements</tt> in an ftp site.
 * </p>
 */
public class FTPDataSink implements DataSink {

	protected static final String PARAMETER_DirectoryName = "directory";
	protected static final String PARAMETER_Username = "username";
	protected static final String PARAMETER_Password = "password";
	protected static final String PARAMETER_Port = "port";
	
	private static Metric ftpDataSinkMetric = StatisticsManager.createMetric("FTPDataSinkMetric", "Time to store file to FTP Location", MetricType.SINK);
	
	/**
	 * Tests the FTPDataSink
	 * @param args nothing
	 * @throws Exception If a connection could not be established with the ftp site.
	 */
	public static void main(String[] args) throws Exception {
		URIListDataSource uriDataSource = new URIListDataSource("http://dl07.di.uoa.gr:8080/myURIList.txt", null);
		
		Parameter dir = new Parameter(PARAMETER_DirectoryName, "ftp");
		Parameter username = new Parameter(PARAMETER_Username, "dimitris");
		Parameter password = new Parameter(PARAMETER_Password, "...");
		Parameter[] outputParameters = {dir, username, password};
		FTPDataSink sink = new FTPDataSink("dl07.di.uoa.gr", outputParameters);
		
		while(uriDataSource.hasNext()){
			try {
				DataElement elm = uriDataSource.next();
				if(elm==null)continue;
				sink.append(elm);
			} catch (Exception e) {
				log.error("Did not manage to append data element to the data sink", e);
			}
		}
		sink.close();
	}
	
	private FTPClient ftpClient = new FTPClient();
	
	private String server;
	private String username = "anonymous";
	private String password = "anonymous";
	private int port;
	
	/**
	 * @param output The output value of the <tt>DataSink</tt>.
	 * @param outputParameters The output parameters of the <tt>DataSink</tt>.
	 * @throws Exception If a connection with the ftp site could not be established.
	 */
	public FTPDataSink(String output, Parameter[] outputParameters) throws Exception {
		server = output;
		port = ftpClient.getDefaultPort();
		directory = "";
		
		if(outputParameters!=null && outputParameters.length>0){
			for(Parameter param: outputParameters){
				if(param.getName()!=null && param.getName().trim().length()>0
						&& param.getValue()!=null){
					if(param.getName().equals(PARAMETER_DirectoryName)){
						directory=param.getValue();
						if(!directory.endsWith("/")){
							directory=directory+"/";
						}
					}else if(param.getName().equals(PARAMETER_Username)){
						username=param.getValue();
					}else if(param.getName().equals(PARAMETER_Password)){
						password=param.getValue();
					}else if(param.getName().equals(PARAMETER_Port)){
						port = Integer.parseInt(param.getValue());
					}
				}
			}
		}
		
		ftpClient.connect(server, port);
		ftpClient.login(username, password);
		ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
		
		log.info("Connected to " + server + ".");
		log.info("FTP server replied: "+ftpClient.getReplyString());
		
		int replyCode = ftpClient.getReplyCode();
		if(!FTPReply.isPositiveCompletion(replyCode)) {
			log.error("FTP server refused connection. Reply CODE: "+replyCode);
			ftpClient.disconnect();
			throw new Exception("FTP server refused connection. Reply CODE: "+replyCode);
		}
		this.output="ftp://"+server+"/"+directory;
	}
	
	private String output;
	private String directory;
	private boolean isClosed=false;
	
	/**
	 * @see org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataSink#append(org.gcube.datatransformation.datatransformationlibrary.dataelements.DataElement)
	 * @param element {@link DataElement} to be appended to this <tt>DataSink</tt>
	 */
	public void append(DataElement element) {
		if(element!=null && !isClosed){
			String documentName = element.getAttributeValue(DataHandlerDefinitions.ATTR_DOCUMENT_NAME);
			if(documentName==null)
				documentName = String.valueOf(element.getId().hashCode());
			
			String remoteFileName = directory+documentName+"."+MimeUtils.getFileExtension(element.getContentType().getMimeType());
			int tries=0;
			while (true) {
				log.debug("Storing element "+element.getId()+" as "+remoteFileName);
				try {
					InputStream stream = element.getContent();
					if(stream!=null){
						long startTime = System.currentTimeMillis();
						boolean res = ftpClient.storeFile(remoteFileName, element.getContent());
						if (res){
							ReportManager.manageRecord(element.getId(),
									"Data element with id "+element.getId()+" and content type "+
									element.getContentType().toString()+
									" was stored successfully.", Status.SUCCESSFUL, Type.SINK);
						}else{
							ReportManager.manageRecord(element.getId(),
									"Data element with id "+element.getId()+" and content type "+
									element.getContentType().toString()+
									" was not stored.", Status.FAILED, Type.SINK);
						}
						stream.close();
						ftpDataSinkMetric.addMeasure(System.currentTimeMillis()-startTime);
					}
					element.destroy();
					return;
				} catch (Exception e) {
					if(tries==3){
						log.error("Did not manage to append element in the data sink");
						ReportManager.manageRecord(element.getId(),
								"Data element with id "+element.getId()+" and content type "+
								element.getContentType().toString()+
								" was not stored after 3 tries.", Status.FAILED, Type.SINK);
						return;
					}
					tries++;
					log.error("Did not manage to append element in the data sink, reconnecting and trying again...", e);
					try {
						reconnect();
					} catch (Exception e1) {
						log.error("Did not manage to reconnect to the ftp site", e1);
						ReportManager.manageRecord(element.getId(),
								"Data element with id "+element.getId()+" and content type "+
								element.getContentType().toString()+
								" was not stored as we could not reconnet to ftp.", Status.FAILED, Type.SINK);
						return;
					}
				}
			}
		}
	}
	
	private void reconnect() throws Exception{
		ftpClient.connect(server, port);
		ftpClient.login(username, password);
		ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
	}

	/**
	 * @see org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataHandler#close()
	 */
	public void close() {
		isClosed=true;
		ReportManager.closeReport();
		if(ftpClient.isConnected()){
			try {
				ftpClient.disconnect();
			} catch (Exception e) {
				log.error("Did not manage to disconnect from data sink", e);
			}
		}
	}

	private static Logger log = LoggerFactory.getLogger(FTPDataSink.class);
	
	/**
	 * @see org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataSink#getOutput()
	 * @return The output of the transformation.
	 */
	public String getOutput() {
		return output;
	}

	/**
	 * @see org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataHandler#isClosed()
	 * @return true if the <tt>DataHandler</tt> has been closed.
	 */
	public boolean isClosed() {
		return isClosed;
	}

}
