package org.gcube.datatransformation.datatransformationlibrary.datahandlers.impl;

import java.io.File;
import java.io.FileOutputStream;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPListParseEngine;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gcube.datatransformation.datatransformationlibrary.dataelements.impl.LocalFileDataElement;
import org.gcube.datatransformation.datatransformationlibrary.dataelements.impl.URLDataElement;
import org.gcube.datatransformation.datatransformationlibrary.dataelements.DataElement;
import org.gcube.datatransformation.datatransformationlibrary.datahandlers.ContentTypeDataSource;
import org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataHandlerDefinitions;
import org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataSource;
import org.gcube.datatransformation.datatransformationlibrary.datahandlers.DistributableDataSource;
import org.gcube.datatransformation.datatransformationlibrary.model.ContentType;
import org.gcube.datatransformation.datatransformationlibrary.model.Parameter;
import org.gcube.datatransformation.datatransformationlibrary.statistics.Metric;
import org.gcube.datatransformation.datatransformationlibrary.statistics.StatisticsManager;
import org.gcube.datatransformation.datatransformationlibrary.statistics.StatisticsManager.MetricType;
import org.gcube.datatransformation.datatransformationlibrary.tmpfilemanagement.TempFileManager;

/**
 * @author Dimitris Katris, NKUA
 *
 * <p>
 * This <tt>DataSource</tt> fetches <tt>DataElements</tt> from an ftp site.
 * </p>
 */
public class FTPDataSource implements DataSource, DistributableDataSource, ContentTypeDataSource {

	protected static final String PARAMETER_DirectoryName = "directory";
	protected static final String PARAMETER_Username = "username";
	protected static final String PARAMETER_Password = "password";
	protected static final String PARAMETER_Port = "port";
	
	private static Logger log = LoggerFactory.getLogger(FTPDataSource.class);
	
	private static Metric ftpDownloadObjectMetric = StatisticsManager.createMetric("FTPDownloadObjectMetric", "Time to download file from FTP Location", MetricType.SOURCE);
	private static Metric ftpGetNextRemoteObjectMetric = StatisticsManager.createMetric("FTPGetNextRemoteObjectMetric", "Time to get next file from FTP Location", MetricType.SOURCE);
	
	/**
	 * Tests ftp data source and sink.
	 * @param args The arguments of the main.
	 * @throws Exception If an error occurred in the test.
	 */
	public static void main(String[] args) throws Exception {
		
		Parameter srcdir = new Parameter(PARAMETER_DirectoryName, "tmp/atlas/source");
//		Parameter trgdir = new Parameter(PARAMETER_DirectoryName, "sink");
//		Parameter username = new Parameter(PARAMETER_Username, "dimitris");
//		Parameter password = new Parameter(PARAMETER_Password, "...");
		Parameter[] inputParameters = {srcdir/*, username, password*/};
//		Parameter[] outputParameters = {trgdir, username, password};
		
		FTPDataSource ftpDataSource = new FTPDataSource("meteora.di.uoa.gr", inputParameters);
//		FTPDataSink   ftpDataSink   = new FTPDataSink("dl07.di.uoa.gr", outputParameters);
		
		while(ftpDataSource.hasNext()){
			try {
				DataElement elm = ftpDataSource.next();
				if(elm==null)continue;
				System.out.println(elm.getContentType().getMimeType());
//				Thread.sleep(360000);
				System.out.println("Going to store: "+elm.getId());
//				ftpDataSink.append(elm);
				System.out.println(elm);
			} catch (Exception e) {
				log.error("Did not manage to append data element to the data sink", e);
			}
		}
		System.out.println("----- Statistics -----");
		System.out.println(StatisticsManager.toXML());
	}
	
	private FTPClient ftpClient = new FTPClient();
	private FTPListParseEngine engine;
	
	private String directory;
	
	private String server;
	private int port;
	private String username = "anonymous";
	private String password = "anonymous";
	
	/**
	 * @param input The input value of the <tt>DataSource</tt>.
	 * @param inputParameters The input parameters of the <tt>DataSource</tt>.
	 * @throws Exception If a connection could not be established.
	 */
	public FTPDataSource(String input, Parameter[] inputParameters) throws Exception {
		server=input;
		port = ftpClient.getDefaultPort();
		directory = "";
		
		if(inputParameters!=null && inputParameters.length>0){
			for(Parameter param: inputParameters){
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
		log.debug("Ininializing ftp data source at: "+username+"@"+server+":"+port);
		
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

		engine = ftpClient.initiateListParsing(directory);
		
		//Creating a tmp directory which will contain the downloaded files...
		tmpDownloadDir = TempFileManager.genarateTempSubDir();
		log.debug("Managed to create temporary directory to "+tmpDownloadDir);
	}

	private String tmpDownloadDir;
	
	/**
	 * @see org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataSource#hasNext()
	 * @return true if the <tt>DataSource</tt> has more elements.
	 */
	public boolean hasNext() {
		return engine.hasNext();
	}

	/**
	 * @see org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataSource#next()
	 * @return the next element of the <tt>DataSource</tt>.
	 */
	public DataElement next() {
		long startTimeGN = System.currentTimeMillis();
		FTPFile[] files = engine.getNext(1); //Page size...
		ftpGetNextRemoteObjectMetric.addMeasure(System.currentTimeMillis()-startTimeGN);
		FTPFile file = files[0];
		int tries=0;
		while (true) {
			try {
				if (!file.isDirectory()) {
					String remoteFileName = directory + file.getName();
					log.debug("Returning next data element: " + remoteFileName);
					long startTime = System.currentTimeMillis();
					String extention = getFileExtention(file.getName());
					String localFileName = TempFileManager.generateTempFileName(tmpDownloadDir);
					if (extention != null) {
						localFileName += (extensionSeparator + extention);
					}
					File localFile = new File(localFileName);
					FileOutputStream outStream = new FileOutputStream(localFile);
					ftpClient.retrieveFile(remoteFileName, outStream);

					outStream.close();
					ftpDownloadObjectMetric.addMeasure(System.currentTimeMillis() - startTime);

					LocalFileDataElement elm = new LocalFileDataElement();
					elm.setAttribute(DataHandlerDefinitions.ATTR_DOCUMENT_NAME, file.getName());
					elm.setContent(localFile);
					elm.setId(remoteFileName);

					return elm;
				}
				return null;
			} catch (Exception e) {
				if(tries==3){
					log.error("Did not manage to get next element...", e);
					return null;
				}
				tries++;
				log.error("Did not manage to get next element, reconnecting and trying again...", e);
				try {
					log.debug("Trying to reconnect to the ftp");
					reconnect();
				} catch (Exception e1) {
					log.error("Could not reconnect to the server", e1);
					return null;
				}
			}
		}
	}
	private void reconnect() throws Exception{
		ftpClient.connect(server, port);
		ftpClient.login(username, password);
		ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
	}

	private static final String extensionSeparator = ".";
	private static String getFileExtention(String fileName){
		int dot = fileName.lastIndexOf(extensionSeparator);
		if(dot==-1){
			return null;
		}
		return fileName.substring(dot + 1);
	}
	
	/**
	 * @see org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataHandler#close()
	 */
	public void close() {
		isClosed=true;
		if(ftpClient.isConnected()){
			try {
				ftpClient.disconnect();
			} catch (Exception e) {
				log.error("Did not manage to disconnect from data sink", e);
			}
		}
	}

	private boolean isClosed=false;
	
	/**
	 * @see org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataHandler#isClosed()
	 * @return true if the <tt>DataHandler</tt> has been closed.
	 */
	public boolean isClosed() {
		return isClosed;
	}
	
	/* DISTRIBUTABLE METHODS */
	
	/**
	 * @see org.gcube.datatransformation.datatransformationlibrary.datahandlers.DistributableDataSource#getDataElement(java.lang.String)
	 * @param dataElementID The id of the {@link DataElement}.
	 * @return The {@link DataElement} instance.
	 * @throws Exception If an error occurred in getting the {@link DataElement}.
	 */
	public DataElement getDataElement(String dataElementID) throws Exception {
		long startTime = System.currentTimeMillis();
		String extention = getFileExtention(dataElementID);
		int tries=0;
		while (true) {
			try {
				String localFileName = TempFileManager.generateTempFileName(tmpDownloadDir);
				if (extention != null) {
					localFileName += (extensionSeparator + extention);
				}
				File localFile = new File(localFileName);
				FileOutputStream outStream = new FileOutputStream(localFile);
				ftpClient.retrieveFile(dataElementID, outStream);
				outStream.close();
				ftpDownloadObjectMetric.addMeasure(System.currentTimeMillis() - startTime);
				LocalFileDataElement elm = new LocalFileDataElement();
				elm.setContent(localFile);
				elm.setId(dataElementID);
				return elm;
			} catch (Exception e) {
				if(tries==3){
					log.error("Did not manage to get next element...", e);
					throw new Exception("Did not manage to get next element...", e);
				}
				tries++;
				log.error("Did not manage to get next element, reconnecting and trying again...", e);
				try {
					log.debug("Trying to reconnect to the ftp");
					reconnect();
				} catch (Exception e1) {
					log.error("Could not reconnect to the server", e1);
					throw new Exception("Could not reconnect to the server", e1);
				}
			}
		}
	}

	/**
	 * @see org.gcube.datatransformation.datatransformationlibrary.datahandlers.DistributableDataSource#getNextDataElementID()
	 * @return The next data element id. 
	 * @throws Exception If the <tt>DistributableDataSource</tt> did not manage to get another data element id.
	 */
	public String getNextDataElementID() throws Exception {
		long startTimeGN = System.currentTimeMillis();
		FTPFile[] files = engine.getNext(1); //Page size...
		ftpGetNextRemoteObjectMetric.addMeasure(System.currentTimeMillis()-startTimeGN);
		FTPFile file = files[0];
		try {
			if(!file.isDirectory()){
				String remoteFileName = directory+file.getName();
				log.debug("Returning next data element: "+remoteFileName);
				return remoteFileName;
			}return null;
		} catch (Exception e) {
			log.error("Did not manage to get next element, returning null...");
			return null;
		}
	}

	/**
	 * Instantiates an FTPDataSource object.
	 */
	public FTPDataSource(){
	}
	
	/**
	 * @see org.gcube.datatransformation.datatransformationlibrary.datahandlers.DistributableDataSource#initializeDistributableDataSource(java.lang.String, org.gcube.datatransformation.datatransformationlibrary.model.Parameter[])
	 * @param input The input of the <tt>DistributableDataSource</tt>
	 * @param inputParameters Any input parameters required by the <tt>DistributableDataSource</tt>.
	 * @throws Exception If the <tt>DistributableDataSource</tt> could not be initialized.
	 */
	public void initializeDistributableDataSource(String input, Parameter[] inputParameters) throws Exception {
		server = input;
		username = "anonymous";
		password = "anonymous";
		port = ftpClient.getDefaultPort();
		directory = "";
		
		if(inputParameters!=null && inputParameters.length>0){
			for(Parameter param: inputParameters){
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
		log.debug("Ininializing ftp data source at: "+username+"@"+server+":"+port);
		
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

		engine = ftpClient.initiateListParsing(directory);
		
		//Creating a tmp directory which will contain the downloaded files...
		tmpDownloadDir = TempFileManager.genarateTempSubDir();
	}
	
	public ContentType nextContentType() {
		try {
			String builder = "ftp://"
					+ server + "/"
					+ getNextDataElementID();
			
			URLDataElement de = new URLDataElement(builder);

			return de == null? null : de.getContentType();
		} catch (Exception e) {
			log.error("Did not manage to get next element's contentn type, returning null...");
			return null;
		}	
	}
}
