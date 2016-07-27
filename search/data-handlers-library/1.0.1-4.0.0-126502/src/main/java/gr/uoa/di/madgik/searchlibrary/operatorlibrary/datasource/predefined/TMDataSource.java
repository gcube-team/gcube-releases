package gr.uoa.di.madgik.searchlibrary.operatorlibrary.datasource.predefined;

import gr.uoa.di.madgik.grs.buffer.IBuffer.Status;
import gr.uoa.di.madgik.grs.proxy.local.LocalWriterProxy;
import gr.uoa.di.madgik.grs.record.GenericRecord;
import gr.uoa.di.madgik.grs.record.GenericRecordDefinition;
import gr.uoa.di.madgik.grs.record.Record;
import gr.uoa.di.madgik.grs.record.RecordDefinition;
import gr.uoa.di.madgik.grs.record.field.Field;
import gr.uoa.di.madgik.grs.record.field.FieldDefinition;
import gr.uoa.di.madgik.grs.record.field.StringField;
import gr.uoa.di.madgik.grs.record.field.StringFieldDefinition;
import gr.uoa.di.madgik.grs.writer.GRS2WriterException;
import gr.uoa.di.madgik.grs.writer.RecordWriter;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.FieldNaming.TMFieldName;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.datasource.DataSource;

import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.gcube.common.clients.fw.queries.StatefulQuery;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.streams.Stream;
import org.gcube.data.tml.proxies.TReader;
import org.gcube.data.tml.proxies.TServiceFactory;
import org.gcube.data.trees.data.Tree;
import org.gcube.data.trees.io.XMLBindings;
import org.gcube.data.trees.patterns.Patterns;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link DataSource} created from Tree Manager. Given a tree collection id, xml
 * representation of trees is returned through a gRS2.
 * 
 * @author john.gerbesiotis - DI NKUA
 * 
 */
public class TMDataSource extends DataSource {
	private static final String GCUBEACTIONSCOPE = "GCubeActionScope";
	private String scope;

	private static Logger log = LoggerFactory.getLogger(TMDataSource.class.getName());

	private String collectionId;
	private TReader tReader;

	/**
	 * @param input
	 *            input value of the {@link DataSource}
	 * @param inputParameters
	 *            input parameters of the {@link DataSource}
	 * @param statsCont
	 *            statistics container
	 * @throws Exception
	 *             If the initialization of the {@link DataSource} fails
	 */
	public TMDataSource(String input, Map<String, String> inputParameters) throws Exception {
		super(input, inputParameters);

		if (inputParameters != null) {
			filterMask = inputParameters.get("filterMask");
			scope = inputParameters.get(GCUBEACTIONSCOPE);
		}
		
		try {
			URI uri = new URI(this.input);
			collectionId = uri.getHost();
			if (uri.getQuery().matches("scope=[^&=]+"))
				scope = uri.getQuery().split("=")[1];
		} catch (Exception e) {
			collectionId = this.input;
		}
		
		if (scope == null)
			throw new Exception("Scope not set");
		
		fieldDefs = initializeSchema(filterMask);

		writer = new RecordWriter<Record>(new LocalWriterProxy(), new RecordDefinition[] { new GenericRecordDefinition(fieldDefs) },
				RecordWriter.DefaultBufferCapacity, RecordWriter.DefaultConcurrentPartialCapacity, RecordWriter.DefaultMirrorBufferFactor);


		StatefulQuery query = TServiceFactory.readSource().withId(collectionId).build();
		tReader = TServiceFactory.reader().matching(query).build();

		if (tReader == null) {
			log.error("Could not retrieve collection " + collectionId + " for specified scope " + scope);
			throw new Exception("Could not retrieve collection " + collectionId + " for specified scope " + scope);
		}

		log.info("Ininializing tm data source for collection: " + collectionId);
	}

	public void run() {
		Thread.currentThread().setName(TMDataSource.class.getName());

		long start = Calendar.getInstance().getTimeInMillis();
		long firstInputStop = start, firstOutputStop = start;
		int rc = 0;

		try {
			ScopeProvider.instance.set(scope);

			Stream<Tree> treesReader = tReader.get(Patterns.tree());

			while (treesReader.hasNext()) {
				Tree t = treesReader.next();

				if (rc == 0)
					firstInputStop = Calendar.getInstance().getTimeInMillis();

				List<Field> fieldList = new ArrayList<Field>();
				for (FieldDefinition field : fieldDefs) {
					switch (TMFieldName.valueOf(field.getName())) {
					case id:
						fieldList.add(new StringField(t.id() != null ? t.id() : "null"));
						break;
					case sourceId:
						fieldList.add(new StringField(t.sourceId() != null ? t.sourceId() : "null"));
						break;
					case uri:
						fieldList.add(new StringField(t.uri() != null ? t.uri().toASCIIString() : "null"));
						break;
					case payload:
						String payload = XMLBindings.toString(t);
						fieldList.add(new StringField(payload != null ? payload : "null"));
						break;
					default:
						log.warn("Unexpected field: " + field.getName());
						break;
					}
				}

				GenericRecord rec = new GenericRecord();
				rec.setFields(fieldList.toArray(new Field[fieldList.size()]));

				if (!writer.importRecord(rec, timeout, timeUnit)) {
					if (writer.getStatus() == Status.Open)
						log.warn("Consumer has timed out");
					else
						log.warn("Consumer has closed");
					break;
				}

				rc++;

				if (rc % 1000 == 0)
					log.debug("Retrieved " + rc + " records so far");
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
			for (TMFieldName value : TMFieldName.values())
				filterMask += value.name() + ", ";
			filterMask = filterMask.substring(0, filterMask.length() - 2);
			filterMask += "]";
		}

		// Filter mask consisted of references e.g [1, 2, 3]
		if (filterMask.replaceAll("[\\[\\],\\s]", "").matches("\\d*")) {
			for (String ref : filterMask.replaceAll("[\\[\\]\\s]", "").split(",")) {
				int index = Integer.parseInt(ref);

				if (index >= TMFieldName.values().length) {
					log.warn("Filter mask out of range");
					continue;
				}

				fieldDefsList.add(new StringFieldDefinition(TMFieldName.values()[index].name()));
			}
		}
		// Filter mask consisted of names e.g [recordId, payload, contentType]
		else {
			for (String ref : filterMask.replaceAll("[\\[\\]\\s]", "").split(",")) {
				try {
					switch (TMFieldName.valueOf(ref)) {
					// case bytestream:
					// fieldDefsList.add(new
					// FileFieldDefinition(TMFieldName.valueOf(ref).name()));
					// break;
					default:
						fieldDefsList.add(new StringFieldDefinition(TMFieldName.valueOf(ref).name()));
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
