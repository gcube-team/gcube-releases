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
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.contenttype.ContentTypeEvaluator;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.datasource.DataSource;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link DataSource} created from local file system. Given a local path,
 * contents are returned through a gRS2.
 * 
 * @author john.gerbesiotis - DI NKUA
 * 
 */
public class PathDataSource extends DataSource {
	private static Logger log = LoggerFactory.getLogger(PathDataSource.class.getName());

	private String sourcePath;
	private String[] folderContents;

	/**
	 * @param input
	 *            input value of the {@link DataSource}
	 * @param inputParameters
	 *            input parameters of the {@link DataSource}
	 * @throws Exception
	 *             If the initialization of the {@link DataSource} fails
	 */
	public PathDataSource(String input, Map<String, String> inputParameters) throws Exception {
		super(input, inputParameters);

//		if (inputParameters != null)
//			filterMask = inputParameters.get("filterMask");
		fieldDefs = initializeSchema(filterMask);

		writer = new RecordWriter<Record>(new LocalWriterProxy(), new RecordDefinition[] { new GenericRecordDefinition(fieldDefs) },
				RecordWriter.DefaultBufferCapacity, RecordWriter.DefaultConcurrentPartialCapacity, RecordWriter.DefaultMirrorBufferFactor);

		sourcePath = this.input;
		File source = new File(sourcePath);
		if (source.isDirectory()) {
			if (!sourcePath.endsWith(File.pathSeparator))
				sourcePath += File.separator;

			folderContents = source.list();
		} else if (source.exists()) {
			folderContents = new String[] { source.getName() };
			sourcePath = sourcePath.substring(0, sourcePath.lastIndexOf("/") + 1);
		}

		if (folderContents == null) {
			log.error(folderContents + ": Either dir does not exist or is not a directory");
			throw new Exception(folderContents + ": Either dir does not exist or is not a directory");
		}

		log.info("Ininializing local data source at: " + input);
	}

	public void run() {
		Thread.currentThread().setName(PathDataSource.class.getName());

		long start = Calendar.getInstance().getTimeInMillis();
		long firstInputStop = start, firstOutputStop = start;
		int rc = 0;

		try {
			for (String fileName : folderContents) {
				String localFileName = sourcePath + fileName;
				File localFile = new File(localFileName);

				if (localFile.isDirectory())
					continue;
				
				if (rc == 0)
					firstInputStop = Calendar.getInstance().getTimeInMillis();

				List<Field> fieldList = new ArrayList<Field>();
				for (FieldDefinition field : fieldDefs) {
					switch (LocalFieldName.valueOf(field.getName())) {
					case id:
						fieldList.add(new StringField(fileName));
						break;
					case bytestream:
						fieldList.add(new FileField(localFile));
						break;
					case mimeType:
						fieldList.add(new StringField(ContentTypeEvaluator.getContentType(localFile)));
						break;
					default:
						log.warn("Unexpected field: " + field.getName());
						break;
					}
				}

				GenericRecord rec = new GenericRecord();
				rec.setFields(fieldList.toArray(new Field[fieldList.size()]));

				log.debug("Returning next row with id: " + fileName);
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

	private static FieldDefinition[] initializeSchema(String filterMask) {
		List<FieldDefinition> fieldDefsList = new ArrayList<FieldDefinition>();

		// if filterMask is null, use all fields
		if (filterMask == null) {
			filterMask = "[";
			for (LocalFieldName value : LocalFieldName.values())
				filterMask += value.name() + ", ";
			filterMask = filterMask.substring(0, filterMask.length() - 2);
			filterMask += "]";
		}

		// Filter mask consisted of references e.g [1, 2, 3]
		if (filterMask.replaceAll("[\\[\\],\\s]", "").matches("\\d*")) {
			for (String ref : filterMask.replaceAll("[\\[\\]\\s]", "").split(",")) {
				int index = Integer.parseInt(ref);

				if (index >= LocalFieldName.values().length) {
					log.warn("Filter mask out of range");
					continue;
				}

				if (LocalFieldName.values()[index].equals(LocalFieldName.bytestream))
					fieldDefsList.add(new FileFieldDefinition(LocalFieldName.values()[index].name()));
				else
					fieldDefsList.add(new StringFieldDefinition(LocalFieldName.values()[index].name()));
			}
		}
		// Filter mask consisted of names e.g [recordId, payload, contentType]
		else {
			for (String ref : filterMask.replaceAll("[\\[\\]\\s]", "").split(",")) {
				try {
					switch (LocalFieldName.valueOf(ref)) {
					case bytestream:
						fieldDefsList.add(new FileFieldDefinition(LocalFieldName.valueOf(ref).name()));
						break;
					default:
						fieldDefsList.add(new StringFieldDefinition(LocalFieldName.valueOf(ref).name()));
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
}
