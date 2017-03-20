package gr.uoa.di.madgik.searchlibrary.operatorlibrary.datasource.predefined;

import gr.uoa.di.madgik.grs.buffer.IBuffer.Status;
import gr.uoa.di.madgik.grs.proxy.local.LocalWriterProxy;
import gr.uoa.di.madgik.grs.record.GenericRecord;
import gr.uoa.di.madgik.grs.record.GenericRecordDefinition;
import gr.uoa.di.madgik.grs.record.Record;
import gr.uoa.di.madgik.grs.record.RecordDefinition;
import gr.uoa.di.madgik.grs.record.field.Field;
import gr.uoa.di.madgik.grs.record.field.FieldDefinition;
import gr.uoa.di.madgik.grs.record.field.FileField;
import gr.uoa.di.madgik.grs.record.field.FileFieldDefinition;
import gr.uoa.di.madgik.grs.record.field.StringField;
import gr.uoa.di.madgik.grs.record.field.StringFieldDefinition;
import gr.uoa.di.madgik.grs.writer.GRS2WriterException;
import gr.uoa.di.madgik.grs.writer.RecordWriter;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.FieldNaming.FTPFieldName;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.contenttype.ContentTypeEvaluator;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.datasource.DataSource;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPListParseEngine;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link DataSource} created from a ftp location. Given an ftp url and possible
 * required credentials, contents are returned through a gRS2.
 * 
 * @author john.gerbesiotis - DI NKUA
 * 
 */
public class FTPDataSource extends DataSource {
//	private static final String PARAMETER_DirectoryName = "directory";
//	private static final String PARAMETER_Username = "username";
//	private static final String PARAMETER_Password = "password";
//	private static final String PARAMETER_Port = "port";

	private static final String extensionSeparator = ".";

	private static Logger log = LoggerFactory.getLogger(FTPDataSource.class.getName());

	private FTPClient ftpClient = new FTPClient();
	private FTPListParseEngine engine;

	private String server;
	private int port;
	private String username = "anonymous";
	private String password = "anonymous";
	private String directory = "";

	/**
	 * @param input
	 *            input value of the {@link DataSource}
	 * @param inputParameters
	 *            input parameters of the {@link DataSource}
	 * @throws Exception
	 *             If the initialization of the {@link DataSource} fails
	 */
	public FTPDataSource(String input, Map<String, String> inputParameters) throws Exception {
		super(input, inputParameters);

		if (inputParameters != null)
			filterMask = inputParameters.get("filterMask");
		fieldDefs = initializeSchema(filterMask);

		URLParser parser = new URLParser(input);
		server = parser.getHostname();
		port = parser.getPort() == -1? ftpClient.getDefaultPort() : parser.getPort();
		directory = parser.getPath().startsWith("/")? parser.getPath().substring(1) : parser.getPath();
		directory = directory.endsWith("/")? directory : directory + "/";
		username = parser.getUsername() != null? parser.getUsername() : username;
		password = parser.getPassword() != null? parser.getPassword() : password;

		writer = new RecordWriter<Record>(new LocalWriterProxy(), new RecordDefinition[] { new GenericRecordDefinition(fieldDefs) },
				RecordWriter.DefaultBufferCapacity, RecordWriter.DefaultConcurrentPartialCapacity, RecordWriter.DefaultMirrorBufferFactor);

		if (inputParameters != null && inputParameters.size() > 0) {
			for (Entry<String, String> param : inputParameters.entrySet()) {
				if (param.getKey() != null && param.getKey().trim().length() > 0 && param.getValue() != null) {
//					if (param.getKey().equals(PARAMETER_DirectoryName)) {
//						directory = param.getValue();
//						if (!directory.endsWith("/")) {
//							directory = directory + "/";
//						}
//					} else if (param.getKey().equals(PARAMETER_Username)) {
//						username = param.getValue();
//					} else if (param.getKey().equals(PARAMETER_Password)) {
//						password = param.getValue();
//					} else if (param.getKey().equals(PARAMETER_Port)) {
//						port = Integer.parseInt(param.getValue());
//					}
				}
			}
		}
		log.info("Ininializing ftp data source at: " + username + "@" + server + ":" + port + "/" + directory);

		ftpClient.connect(server, port);
		ftpClient.login(username, password);
		ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

		log.info("Connected to " + server + ".");
		log.info("FTP server replied: " + ftpClient.getReplyString());

		int replyCode = ftpClient.getReplyCode();
		if (!FTPReply.isPositiveCompletion(replyCode)) {
			log.error("FTP server refused connection. Reply CODE: " + replyCode);
			ftpClient.disconnect();
			throw new Exception("FTP server refused connection. Reply CODE: " + replyCode);
		}

		engine = ftpClient.initiateListParsing(directory);
	}

	public void run() {
		Thread.currentThread().setName(FTPDataSource.class.getName());

		long start = Calendar.getInstance().getTimeInMillis();
		long firstInputStop = 0, firstOutputStop = 0;
		int rc = 0;

		try {
			while (engine.hasNext()) {
				GenericRecord rec = retrieveFTPFile(engine.getNext(1)[0]);
				if (rec == null)
					continue;
				
				if (rc == 0)
					firstInputStop = Calendar.getInstance().getTimeInMillis();

				if (!writer.importRecord(rec, timeout, timeUnit)) {
					if (writer.getStatus() == Status.Open)
						log.warn("Consumer has timed out");
					break;
				}

				rc++;

				if (rc == 1)
					firstOutputStop = Calendar.getInstance().getTimeInMillis();
			}

		} catch (Exception e) {
			log.error("Error during datasource retrieval. Closing", e);
		} finally {
			try {
				writer.close();
				if (ftpClient.isConnected())
					ftpClient.disconnect();
			} catch (Exception ee) {
			}
		}

		long closeStop = Calendar.getInstance().getTimeInMillis();

		log.info("DATASOURCE OPERATOR:Produced first result in " + (firstOutputStop - start) + " milliseconds\n" + "Produced last result in "
				+ (closeStop - start) + " milliseconds\n" + "Produced " + rc + " results\n" + "Production rate was "
				+ (((float) rc / (float) (closeStop - start)) * 1000) + " records per second");

	}

	private GenericRecord retrieveFTPFile(FTPFile file) {
		int tries = 0;
		while (true) {
			try {
				if (!file.isDirectory()) {
					String remoteFileName = directory + file.getName();
					log.debug("Returning next row with id: " + remoteFileName);
					String extention = getFileExtention(file.getName());



					List<Field> fieldList = new ArrayList<Field>();
					for (FieldDefinition field : fieldDefs) {
						switch (FTPFieldName.valueOf(field.getName())) {
						case id:
							fieldList.add(new StringField(remoteFileName));
							break;
						case bytestream:
							File localFile = null;
							if (extention != null)
								localFile = File.createTempFile("ftpDataSource", extensionSeparator + extention);
							else
								localFile = File.createTempFile("ftpDataSource", extensionSeparator + "tmp");

							localFile.deleteOnExit();

							FileOutputStream outStream = new FileOutputStream(localFile);
							ftpClient.retrieveFile(remoteFileName, outStream);

							outStream.close();
							
							fieldList.add(new FileField(localFile));
							break;
						case mimeType:
							fieldList.add(new StringField(ContentTypeEvaluator.getContentType(new File(remoteFileName))));
							break;
						default:
							log.warn("Unexpected field: " + field.getName());
							break;
						}
					}
					
					GenericRecord rec = new GenericRecord();
					rec.setFields(fieldList.toArray(new Field[fieldList.size()]));

					return rec;
				}
				return null;
			} catch (Exception e) {
				if (tries == 3) {
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

	private void reconnect() throws Exception {
		ftpClient.connect(server, port);
		ftpClient.login(username, password);
		ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
	}

	private static String getFileExtention(String fileName) {
		int dot = fileName.lastIndexOf(extensionSeparator);
		if (dot == -1) {
			return null;
		}
		return fileName.substring(dot + 1);
	}

	@Override
	public URI getLocator() {
		if (writer != null)
			try {
				return writer.getLocator();
			} catch (GRS2WriterException e) {
				log.error("Could not retrieve locator", e);
			}
		return null;
	}
	
	private static FieldDefinition[] initializeSchema(String filterMask) {
		List<FieldDefinition> fieldDefsList = new ArrayList<FieldDefinition>();
		
		// if filterMask is null, use all fields
		if (filterMask == null) {
			filterMask = "[";
			for (FTPFieldName value : FTPFieldName.values())
				filterMask += value.name() + ", ";
			filterMask = filterMask.substring(0, filterMask.length() - 2);
			filterMask += "]";
		}

		// Filter mask consisted of references e.g [1, 2, 3]
		if (filterMask.replaceAll("[\\[\\],\\s]", "").matches("\\d*")) {
			for (String ref : filterMask.replaceAll("[\\[\\]\\s]", "").split(",")) {
				int index = Integer.parseInt(ref);

				if (index >= FTPFieldName.values().length) {
					log.warn("Filter mask out of range");
					continue;
				}

				if (FTPFieldName.values()[index].equals(FTPFieldName.bytestream)) {
					FileFieldDefinition fd = new FileFieldDefinition(FTPFieldName.values()[index].name());
					fd.setDeleteOnDispose(true);
					fieldDefsList.add(fd);
				} else
					fieldDefsList.add(new StringFieldDefinition(FTPFieldName.values()[index].name()));
			}
		}
		// Filter mask consisted of names e.g [recordId, payload, contentType]
		else {
			for (String ref : filterMask.replaceAll("[\\[\\]\\s]", "").split(",")) {
				try {
					switch (FTPFieldName.valueOf(ref)) {
					case bytestream:
						fieldDefsList.add(new FileFieldDefinition(FTPFieldName.valueOf(ref).name()));
						break;
					default:
						fieldDefsList.add(new StringFieldDefinition(FTPFieldName.valueOf(ref).name()));
						break;
					}
				} catch (IllegalArgumentException e) {
					log.warn("Filter mask out of range for value: " + ref);
				}
			}
		}
		log.info("ResultSet schema that will be used: " + fieldDefsList);
		return fieldDefsList.toArray(new FieldDefinition[fieldDefsList.size()]);
	}
	
	public static void main(String[] args) throws URISyntaxException {
		ArrayList<String> strs = new ArrayList<String>();
		
		strs.add("foo://username:password@example.com:8042/over/there/index.dtb?type=animal&name=narwhal#nose");
		strs.add("ftp://giannis:aplagiaftp@meteora.di.uoa.gr");
		strs.add("ftp://giannis:aplagiaftp@meteora.di.uoa.gr:1234/testArea/src");
		strs.add("ftp://giannis:password@meteora.di.uoa.gr:1234/testArea/src");
		strs.add("ftp://giannis:aplagiaftp@meteora.di.uoa.gr/testArea/src");

		ArrayList<URLParser> list = new ArrayList<URLParser>();
		for(String str : strs) {
			list.add(new URLParser(str));
		}
		
		for (URLParser l : list) {
//			System.out.println(l.uri);
			System.out.println("user: " + l.getUsername());
			System.out.println("pass: " + l.getPassword());
			System.out.println("host: " + l.getHostname());
			System.out.println("port: " + l.getPort());
			System.out.println("path: " + l.getPath());
			System.out.println("---------------------------------");
		}
	}
}

class URLParser {
	private URI uri; 
	URLParser(String uri) throws URISyntaxException {
		this.uri = new URI(uri);
	}
	
	public String getUsername() {
		String userInfo = uri.getUserInfo();
		if (userInfo != null) {
			if (userInfo.contains(":"))
				return userInfo.substring(0, userInfo.indexOf(":"));
			else
				return userInfo;
		}
		
		return null;
	}
	
	public String getPassword() {
		String userInfo = uri.getUserInfo();
		if (userInfo != null) {
			if (userInfo.contains(":"))
				return userInfo.substring(userInfo.indexOf(":") + 1);
		}
		
		return null;
	}
	
	public String getHostname() {
		return uri.getHost();
	}

	public int getPort() {
		return uri.getPort();
	}

	public String getPath() {
		String path = uri.getPath();
		if (path != null) {
			return path;
		}
		
		return null;
	}
}