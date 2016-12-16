package gr.uoa.di.madgik.searchlibrary.operatorlibrary.transform;

import gr.uoa.di.madgik.grs.reader.ForwardReader;
import gr.uoa.di.madgik.grs.reader.IRecordReader;
import gr.uoa.di.madgik.grs.record.Record;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.Unary;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.stats.StatsContainer;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

/**
 * This class performs applies script transformation on the records that it receives as
 * input and produces a new set of records as output, each one of them
 * containing the transformed result
 * 
 * @author jgerbe
 */
public class ScriptOp extends Unary {
	/**
	 * The Logger used by the class
	 */
	private Logger logger = LoggerFactory.getLogger(ScriptOp.class.getName());

	private String script;

	private List<File> tempFiles = new ArrayList<File>();
	
	private String schema;
	/**
	 * Creates a new {@link ScriptOp} with the default timeout for the reader
	 * and the writer
	 * 
	 * @param inLocator
	 *            The locator of the input
	 * @param operatorParameters
	 *            operator parameters containing the name of the script which
	 *            will be applied apply the transformation
	 * @param stats
	 *            Statistics
	 * @throws Exception
	 */
	public ScriptOp(URI inLocator, Map<String, String> operatorParameters, StatsContainer stats) throws Exception {
		super(inLocator, operatorParameters, stats);

		init();
	}

	/**
	 * Creates a new {@link ScriptOp} with configurable timeout for the
	 * reader and the writer
	 * 
	 * @param inLocator
	 *            The locator of the input
	 * @param operatorParameters
	 *            operator parameters containing the name of the script which
	 *            will be applied apply the transformation
	 * @param stats
	 *            Statistics
	 * @param timeout
	 *            The timeout to be used both by the reader and the writer
	 * @param timeUnit
	 *            The time unit of the timeout used
	 * @throws Exception
	 */
	public ScriptOp(URI inLocator, Map<String, String> operatorParameters, StatsContainer stats, long timeout, TimeUnit timeUnit) throws Exception {
		super(inLocator, operatorParameters, stats, timeout, timeUnit);

		init();
	}
	
	private void init() throws Exception {
		script = operatorParameters.get("scriptCmd");
		schema = operatorParameters.get("schema");
		if (script.isEmpty())
			throw new Exception("script not specified in opearator parameters");
		
		for (String key : operatorParameters.keySet()) {
			if (key.equals("script") || key.equals("schema"))
				continue;
			
			String scriptName = key.replace("CDATA:", "");
			if (script.contains(scriptName)) {
				File f = decompressToFile(Base64.decode(operatorParameters.get(key)));
				
				script = script.replace(scriptName, f.getAbsolutePath());
				
				tempFiles.add(f);
			}
		}
		
		logger.info("Initialized script operator with script: " + script);
	}

	/**
	 * Initiates the transformation procedure
	 * 
	 * @return The {@link RSLocator} pointing to the produced
	 *         {@link org.gcube.searchservice.searchlibrary.resultset.ResultSet}
	 * @throws Exception
	 *             An unrecoverable for the operation has occurred
	 */
	public URI compute() throws Exception {
		try {
			long start = Calendar.getInstance().getTimeInMillis();
			IRecordReader<Record> reader = new ForwardReader<Record>(inLocator);
			final ScriptWorker<Record> worker = new ScriptWorker<Record>(reader, script, schema, stats, timeout, timeUnit);
			worker.start();

			long readerstop = Calendar.getInstance().getTimeInMillis();
			stats.timeToInitialize(readerstop - start);
			
			// delete temp files when worker finishes
			new Thread() {
				public void run() {
					Thread.currentThread().setName("Script file garbage collector");
					try {
						worker.join();
					} catch (InterruptedException e) {}
					for (File f : tempFiles) {
						f.delete();
					}
				}
			}.start();
			
			return worker.getLocator();
		} catch (Exception e) {
			logger.error("Could not initialize transform operation. Throwing Exception", e);
			throw new Exception("Could not initialize transform operation");
		}
	}
	
	public static File decompressToFile(byte[] zipped) {
		File tmp = null;
		InputStream inputStream = null;
		OutputStream outputStream = null;
	 
		try {
			tmp =  File.createTempFile("script", ".tmp");
			// read this file into InputStream
			ByteArrayInputStream in = new ByteArrayInputStream(zipped);

			inputStream = new GZIPInputStream(in);
	 
			// write the inputStream to a FileOutputStream
			outputStream = new FileOutputStream(tmp);
	 
			int read = 0;
			byte[] bytes = new byte[1024];
	 
			while ((read = inputStream.read(bytes)) != -1) {
				outputStream.write(bytes, 0, read);
			}
	 
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (outputStream != null) {
				try {
					// outputStream.flush();
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
	 
			}
		}
		return tmp;
	}
}
