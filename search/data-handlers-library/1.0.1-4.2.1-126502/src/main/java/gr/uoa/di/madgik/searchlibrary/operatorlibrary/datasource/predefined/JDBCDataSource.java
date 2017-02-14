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
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.FieldNaming.LocalFieldName;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.datasource.DataSource;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.extjdbc.QueryBridge;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.extjdbc.QueryParser;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.utils.FileUtils;

import java.io.File;
import java.net.URI;
import java.net.URLDecoder;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link DataSource} created from a database. Given a sql query, results
 * received from the given database are returned through a gRS2.
 * 
 * @author john.gerbesiotis - DI NKUA
 * 
 */
public class JDBCDataSource extends DataSource {
	private static Logger log = LoggerFactory.getLogger(JDBCDataSource.class.getName());

	private String query;
	private String[] columnNames;
	private ResultSet rs;

	/**
	 * @param input
	 *            input value of the {@link DataSource}
	 * @param inputParameters
	 *            input parameters of the {@link DataSource}
	 * @throws Exception
	 *             If the initialization of the {@link DataSource} fails
	 */
	public JDBCDataSource(String input, Map<String, String> inputParameters) throws Exception {
		super(input, inputParameters);

		query = this.input;
		QueryParser parser = new QueryParser(new URI(query));
		QueryBridge qb = new QueryBridge(parser.getDriverName(), parser.getConnectionString(), 20);
		rs = qb.executeQuery(parser.getQuery());
		columnNames = qb.getColumnNames();

		if (inputParameters != null)
			filterMask = inputParameters.get("filterMask");
		fieldDefs = initializeSchema(columnNames, filterMask);

		writer = new RecordWriter<Record>(new LocalWriterProxy(), new RecordDefinition[] { new GenericRecordDefinition(fieldDefs) },
				RecordWriter.DefaultBufferCapacity, RecordWriter.DefaultConcurrentPartialCapacity, RecordWriter.DefaultMirrorBufferFactor);

		log.info("Ininializing JDBC data source at: " + parser.getConnectionString());
	}

	public void run() {
		Thread.currentThread().setName(JDBCDataSource.class.getName());

		long start = Calendar.getInstance().getTimeInMillis();
		long firstInputStop = start, firstOutputStop = start;
		int rc = 0;

		try {
			while (rs.next()) {
				if (rc == 0)
					firstInputStop = Calendar.getInstance().getTimeInMillis();

				List<Field> fieldList = new ArrayList<Field>();
				for (FieldDefinition field : fieldDefs) {
					if (field.getName().equals(LocalFieldName.bytestream.name())) {
						File localFile = null;
						localFile = File.createTempFile("jdbcDataSource", ".tmp");

						localFile.deleteOnExit();

						FileUtils.fileFromInputStream(rs.getBinaryStream(field.getName()), localFile);
						fieldList.add(new FileField(localFile));
					} else {
						fieldList.add(new StringField(rs.getString(field.getName())));
					}
				}

				GenericRecord rec = new GenericRecord();
				rec.setFields(fieldList.toArray(new Field[fieldList.size()]));

//				log.trace("Returning next row with id: ");
				if (!writer.importRecord(rec, timeout, timeUnit)) {
					if (writer.getStatus() == Status.Open)
						log.warn("Consumer has timed out");
					break;
				}

				rc++;

				if (rc % 10000 == 0)
					log.debug("Got " + rc + " records from source");
				if (rc == 1)
					firstOutputStop = Calendar.getInstance().getTimeInMillis();
			}

		} catch (Exception e) {
			log.error("Error during datasource retrieval. Closing", e);
		} finally {
			try {
				writer.close();
			} catch (Exception ee) {
			}
		}

		long closeStop = Calendar.getInstance().getTimeInMillis();

		log.info("DATASOURCE OPERATOR:Produced first result in " + (firstOutputStop - start) + " milliseconds\n" + "Produced last result in "
				+ (closeStop - start) + " milliseconds\n" + "Produced " + rc + " results\n" + "Production rate was "
				+ (((float) rc / (float) (closeStop - start)) * 1000) + " records per second");
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

	private static FieldDefinition[] initializeSchema(String[] columnNames, String filterMask) {
		List<FieldDefinition> fieldDefsList = new ArrayList<FieldDefinition>();

		// if filterMask is null, use all fields
		if (filterMask == null) {
			filterMask = "[";
			for (String value : columnNames)
				filterMask += value + ", ";
			filterMask = filterMask.substring(0, filterMask.length() - 2);
			filterMask += "]";
		}

		// Filter mask consisted of references e.g [1, 2, 3]
		if (filterMask.replaceAll("[\\[\\],\\s]", "").matches("\\d*")) {
			for (String ref : filterMask.replaceAll("[\\[\\]\\s]", "").split(",")) {
				int index = Integer.parseInt(ref);

				if (index >= columnNames.length) {
					log.warn("Filter mask out of range");
					continue;
				}

				if (columnNames[index].equals(LocalFieldName.bytestream))
					fieldDefsList.add(new FileFieldDefinition(columnNames[index]));
				else
					fieldDefsList.add(new StringFieldDefinition(columnNames[index]));
			}
		}
		// Filter mask consisted of names e.g [recordId, payload, contentType]
		else {
			for (String ref : filterMask.replaceAll("[\\[\\]\\s]", "").split(",")) {
				if (ref.equals(LocalFieldName.bytestream.name())) {
					FileFieldDefinition fd = new FileFieldDefinition(ref);
					fd.setDeleteOnDispose(true);
					fieldDefsList.add(fd);
				} else {
					fieldDefsList.add(new StringFieldDefinition(ref));
				}
			}
		}

		log.info("ResultSet schema that will be used: " + fieldDefsList);
		return fieldDefsList.toArray(new FieldDefinition[fieldDefsList.size()]);
	}
}