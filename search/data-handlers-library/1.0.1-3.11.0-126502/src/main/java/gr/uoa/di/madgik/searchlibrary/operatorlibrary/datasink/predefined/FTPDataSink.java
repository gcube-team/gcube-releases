package gr.uoa.di.madgik.searchlibrary.operatorlibrary.datasink.predefined;

import gr.uoa.di.madgik.grs.buffer.IBuffer.Status;
import gr.uoa.di.madgik.grs.reader.ForwardReader;
import gr.uoa.di.madgik.grs.reader.IRecordReader;
import gr.uoa.di.madgik.grs.record.Record;
import gr.uoa.di.madgik.grs.record.RecordDefinition;
import gr.uoa.di.madgik.grs.record.field.Field;
import gr.uoa.di.madgik.grs.record.field.FileField;
import gr.uoa.di.madgik.grs.record.field.StringField;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.FieldNaming.FTPFieldName;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.datasink.DataSink;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.stats.StatsContainer;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.Calendar;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link DataSink} created for ftp. Contents are retrieved through a gRS2 are
 * stored in given ftp.
 * 
 * @author john.gerbesiotis - DI NKUA
 * @param <T>
 *            extends {@link Record}
 * 
 */
public class FTPDataSink<T extends Record> extends DataSink {
	private static final String PARAMETER_DirectoryName = "directory";
	private static final String PARAMETER_Username = "username";
	private static final String PARAMETER_Password = "password";
	private static final String PARAMETER_Port = "port";

	private static Logger log = LoggerFactory.getLogger(FTPDataSink.class.getName());

	private FTPClient ftpClient = new FTPClient();

	private String server;
	private int port;
	private String username = "anonymous";
	private String password = "anonymous";
	private String directory = "";

	/**
	 * The reader to use
	 */
	private IRecordReader<T> reader = null;

	/**
	 * @param inLocator
	 *            input locator of the consuming result set
	 * @param output
	 *            output value of the {@link DataSink}
	 * @param outputParameters
	 *            output parameters of the {@link DataSink}
	 * @param statsCont
	 *            statistics container
	 * @throws Exception
	 *             If the initialization of the {@link DataSink} fails
	 */
	public FTPDataSink(URI inLocator, String output, Map<String, String> outputParameters, StatsContainer statsCont) throws Exception {
		super(inLocator, output, outputParameters, statsCont);

		reader = new ForwardReader<T>(inLocator);
		RecordDefinition defs = reader.getRecordDefinitions()[0];
		if (defs.getDefinition(FTPFieldName.id.name()) < 0 || defs.getDefinition(FTPFieldName.bytestream.name()) < 0 || defs.getDefinition(FTPFieldName.mimeType.name()) < 0) {
			log.error("FTPDataSink could not be initialized, cause corresponding fields are missing from resultSet");
			throw new Exception("FTPDataSink could not be initialized, cause corresponding fields are missing from resultSet");
		}
		
		server = output;
		port = ftpClient.getDefaultPort();
		directory = "";
		
		if (outputParameters != null && outputParameters.size() > 0) {
			for (Entry<String, String> param : outputParameters.entrySet()) {
				if (param.getKey() != null && param.getKey().trim().length() > 0 && param.getValue() != null) {
					if (param.getKey().equals(PARAMETER_DirectoryName)) {
						directory = param.getValue();
						if (!directory.endsWith("/")) {
							directory = directory + "/";
						}
					} else if (param.getKey().equals(PARAMETER_Username)) {
						username = param.getValue();
					} else if (param.getKey().equals(PARAMETER_Password)) {
						password = param.getValue();
					} else if (param.getKey().equals(PARAMETER_Port)) {
						port = Integer.parseInt(param.getValue());
					}
				}
			}
		}
		log.info("Ininializing ftp data sink at: " + username + "@" + server + ":" + port + "/" + directory);

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

		this.output = "ftp://" + server + "/" + directory;
	}

	public void run() {
		Thread.currentThread().setName(FTPDataSink.class.getName());

		long start = Calendar.getInstance().getTimeInMillis();
		long firstInputStop = start, firstOutputStop = start;
		int rc = 0;

		try {
			while (true) {
				try {
					if (reader.getStatus() == Status.Dispose || (reader.getStatus() == Status.Close && reader.availableRecords() == 0))
						break;

					T rec = reader.get(timeout, timeUnit);
					if (rec == null) {
						if (reader.getStatus() == Status.Open)
							log.warn("Producer has timed out");
						break;
					}

					if (rc == 0)
						firstInputStop = Calendar.getInstance().getTimeInMillis();

					String recordId = null;
					File payload = null;
					String contentType = "";
					try {
						Field recIdField = rec.getField(FTPFieldName.id.name());
						if (recIdField instanceof StringField)
							recordId = ((StringField) recIdField).getPayload();
						Field payloadField = rec.getField(FTPFieldName.bytestream.name());
						if (payloadField instanceof FileField)
							payload = ((FileField) payloadField).getPayload();
						Field contentTypeField = rec.getField(FTPFieldName.mimeType.name());
						if (contentTypeField instanceof StringField)
							contentType = ((StringField) contentTypeField).getPayload();
					} catch (Exception e) {
						log.warn("Could not extract payload from record #" + rc + ". Continuing");
						continue;
					}

					String filename = recordId.hashCode() + contentType.replaceAll("/", ".");
					String remoteFilename = directory + filename;
					
					storeFTPFile(remoteFilename, payload);

					rc++;
					if (rc == 1)
						firstOutputStop = Calendar.getInstance().getTimeInMillis();
				} catch (Exception e) {
					log.warn("Could not retrieve and store the record. Continuing", e);
				}
			}
		} catch (Exception e) {
			log.error("Error during datasink retrieval. Closing", e);
		} finally {
			try {
				reader.close();
				if (ftpClient.isConnected())
					ftpClient.disconnect();
			} catch (Exception ee) {
			}
		}

		long closeStop = Calendar.getInstance().getTimeInMillis();

		stats.timeToComplete(closeStop - start);
		stats.timeToFirstInput(firstInputStop - start);
		stats.timeToFirst(firstOutputStop - start);
		stats.producedResults(rc);
		stats.productionRate(((float) rc / (float) (closeStop - start)) * 1000);
		log.info("DATASINK OPERATOR:Produced first result in " + (firstOutputStop - start) + " milliseconds\n" + "Produced last result in "
				+ (closeStop - start) + " milliseconds\n" + "Produced " + rc + " results\n" + "Production rate was "
				+ (((float) rc / (float) (closeStop - start)) * 1000) + " records per second");
	}
	
	public void storeFTPFile(String remoteFilename, File payload) {
			int tries = 0;
			while (true) {
				log.debug("Storing element as " + remoteFilename);
				try {
					InputStream stream = new FileInputStream(payload);
					if (stream != null) {
						boolean res = ftpClient.storeFile(remoteFilename, stream);
						if (res) {
							log.debug("Data element stored succesfuly at " + remoteFilename);
						} else {
							log.warn("Data element was not stored succesfuly at " + remoteFilename);
						}
						stream.close();
					}
					return;
				} catch (Exception e) {
					if (tries == 3) {
						log.error("Did not manage to append element in the data sink");
						return;
					}
					tries++;
					log.warn("Did not manage to append element in the data sink, reconnecting and trying again...", e);
					try {
						reconnect();
					} catch (Exception e1) {
						log.error("Did not manage to reconnect to the ftp site", e1);
						return;
					}
				}
			}
		}

	private void reconnect() throws Exception {
		ftpClient.connect(server, port);
		ftpClient.login(username, password);
		ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
	}

	@Override
	public String getOutput() {
		return output;
	}
}
