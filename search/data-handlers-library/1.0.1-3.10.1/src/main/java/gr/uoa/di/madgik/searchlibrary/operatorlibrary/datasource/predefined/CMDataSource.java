//package gr.uoa.di.madgik.searchlibrary.operatorlibrary.datasource.predefined;
//
//import gr.uoa.di.madgik.grs.buffer.IBuffer.Status;
//import gr.uoa.di.madgik.grs.proxy.local.LocalWriterProxy;
//import gr.uoa.di.madgik.grs.record.GenericRecord;
//import gr.uoa.di.madgik.grs.record.GenericRecordDefinition;
//import gr.uoa.di.madgik.grs.record.Record;
//import gr.uoa.di.madgik.grs.record.RecordDefinition;
//import gr.uoa.di.madgik.grs.record.field.Field;
//import gr.uoa.di.madgik.grs.record.field.FieldDefinition;
//import gr.uoa.di.madgik.grs.record.field.FileField;
//import gr.uoa.di.madgik.grs.record.field.FileFieldDefinition;
//import gr.uoa.di.madgik.grs.record.field.StringField;
//import gr.uoa.di.madgik.grs.record.field.StringFieldDefinition;
//import gr.uoa.di.madgik.grs.writer.GRS2WriterException;
//import gr.uoa.di.madgik.grs.writer.RecordWriter;
//import gr.uoa.di.madgik.searchlibrary.operatorlibrary.FieldNaming.CMFieldName;
//import gr.uoa.di.madgik.searchlibrary.operatorlibrary.datasource.DataSource;
//import gr.uoa.di.madgik.searchlibrary.operatorlibrary.stats.StatsContainer;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.net.URI;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.List;
//import java.util.Map;
//
//import org.gcube.common.core.scope.GCUBEScope;
//import org.gcube.contentmanagement.contentmanager.stubs.calls.iterators.RemoteIterator;
//import org.gcube.contentmanagement.gcubedocumentlibrary.io.DocumentReader;
//import org.gcube.contentmanagement.gcubedocumentlibrary.projections.DocumentProjection;
//import org.gcube.contentmanagement.gcubedocumentlibrary.projections.Projections;
//import org.gcube.contentmanagement.gcubemodellibrary.elements.GCubeDocument;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
///**
// * {@link DataSource} created from Content Manager using gDL. Given a collection
// * id, documents are returned through a gRS2.
// * 
// * @author john.gerbesiotis - DI NKUA
// * 
// */
//public class CMDataSource extends DataSource {
//	private static final String GCUBEACTIONSCOPE = "GCubeActionScope";
//	private String scope;
//
//	private static Logger log = LoggerFactory.getLogger(CMDataSource.class.getName());
//
//	private String collectionId;
//	private DocumentReader cmReader;
//
//	/**
//	 * @param input
//	 *            input value of the {@link DataSource}
//	 * @param inputParameters
//	 *            input parameters of the {@link DataSource}
//	 * @param statsCont
//	 *            statistics container
//	 * @throws Exception
//	 *             If the initialization of the {@link DataSource} fails
//	 */
//	public CMDataSource(String input, Map<String, String> inputParameters, StatsContainer statsCont) throws Exception {
//		super(input, inputParameters, statsCont);
//
//		if (inputParameters != null) {
//			filterMask = inputParameters.get("filterMask");
//			scope = inputParameters.get(GCUBEACTIONSCOPE);
//		}
//		fieldDefs = initializeSchema(filterMask);
//
//		writer = new RecordWriter<Record>(new LocalWriterProxy(), new RecordDefinition[] { new GenericRecordDefinition(fieldDefs) },
//				RecordWriter.DefaultBufferCapacity, RecordWriter.DefaultConcurrentPartialCapacity, RecordWriter.DefaultMirrorBufferFactor);
//
//		collectionId = this.input;
//
//		cmReader = new DocumentReader(collectionId, GCUBEScope.getScope(scope));
//
//		if (cmReader == null) {
//			log.error("Could not retrieve collection " + collectionId + " for specified scope " + scope);
//			throw new Exception("Could not retrieve collection " + collectionId + " for specified scope " + scope);
//		}
//
//		log.debug("Ininializing cm data source for collection: " + collectionId);
//	}
//
//	public void run() {
//		long start = Calendar.getInstance().getTimeInMillis();
//		long firstInputStop = start, firstOutputStop = start;
//		int rc = 0;
//
//		try {
//			DocumentProjection dp = Projections.document();//.with(Projections.MIME_TYPE, Projections.NAME, Projections.METADATA);
//			if (filterMask != null)
//				for (FieldDefinition f: fieldDefs){
//					switch (CMFieldName.valueOf(f.getName())) {
//					case id:
//						dp = dp.with(Projections.NAME); // XXX Projections.ID not present
//						break;
//					case name:
//						dp = dp.with(Projections.NAME);
//						break;
//					case creationTime:
//						dp = dp.with(Projections.CREATION_TIME);
//						break;
//					case lastUpdateTime:
//						dp = dp.with(Projections.LAST_UPDATE);
//						break;
//					case mimeType:
//						dp = dp.with(Projections.MIME_TYPE);
//						break;
//					case length:
//						dp = dp.with(Projections.LENGTH);
//						break;
//					case bytestream:
//						dp = dp.with(Projections.BYTESTREAM);
//						break;
//					case bytestreamURI:
//						dp = dp.with(Projections.BYTESTREAM_URI);
//						break;
//					case language:
//						dp = dp.with(Projections.LANGUAGE);
//						break;
//					case schemaName:
//						dp = dp.with(Projections.SCHEMA_NAME);
//						break;
//					case schemaURI:
//						dp = dp.with(Projections.SCHEMA_URI);
//						break;
//					default:
//						log.warn("Unexpected field name");
//						break;
//					}
//				}
//			RemoteIterator<GCubeDocument> documentIterator = cmReader.get(dp);
//
//			while (documentIterator.hasNext()) {
//				GCubeDocument document = documentIterator.next();
//
//				if (rc == 0)
//					firstInputStop = Calendar.getInstance().getTimeInMillis();
//
//				List<Field> fieldList = new ArrayList<Field>();
//				for (FieldDefinition field : fieldDefs) {
//					switch (CMFieldName.valueOf(field.getName())){
//					case id:
//						fieldList.add(new StringField(document.id() != null? document.id() : "null"));
//						break;
//					case name:
//						fieldList.add(new StringField(document.name() != null? document.name() : "null"));
//						break;
//					case creationTime:
//						fieldList.add(new StringField(document.creationTime() != null? new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(document.creationTime().getTime()) : "null"));
//						break;
//					case lastUpdateTime:
//						fieldList.add(new StringField(document.lastUpdate() != null? new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(document.lastUpdate().getTime()) : "null"));
//						break;
//					case mimeType:
//						fieldList.add(new StringField(document.mimeType() != null? document.mimeType() : "null"));
//						break;
//					case length:
//						fieldList.add(new StringField(document.length() != null? document.length().toString() : "null"));
//						break;
//					case bytestreamURI:
//						fieldList.add(new StringField(document.bytestreamURI() != null? document.bytestreamURI().toASCIIString() : "null"));
//						break;
//					case language:
//						fieldList.add(new StringField(document.language() != null? document.language() : "null"));
//						break;
//					case schemaURI:
//						fieldList.add(new StringField(document.schemaURI() != null? document.schemaURI().toASCIIString() : "null"));
//						break;
//					case schemaName:
//						fieldList.add(new StringField(document.schemaName() != null? document.schemaName() : "null"));
//						break;
////					case propertyKey:
////						break;
////					case propertyType:
////						break;
////					case propertyValue:
////						break;
//					case bytestream:
//						File f = File.createTempFile("cmDataSource", ".tmp");
//						f.deleteOnExit();
//						FileOutputStream fos = new FileOutputStream(f);
//						fos.write(document.bytestream());
//						fos.close();
//						fieldList.add(new FileField(f));
//						break;
//					default:
//						log.warn("Unexpected field: " + field.getName());
//						break;
//					}
//				}
//
//				GenericRecord rec = new GenericRecord();
//				rec.setFields(fieldList.toArray(new Field[fieldList.size()]));
//
//				log.debug("Returning next row with id: " + document.id());
//				if (!writer.importRecord(rec, timeout, timeUnit)) {
//					if (writer.getStatus() == Status.Open)
//						log.warn("Consumer has timed out");
//					else
//						log.warn("Consumer has closed");
//					break;
//				}
//
//				rc++;
//
//				if (rc == 1)
//					firstOutputStop = Calendar.getInstance().getTimeInMillis();
//			}
//
//		} catch (Exception e) {
//			log.error("Error during datasource retrieval. Closing", e);
//		} finally {
//			try {
//				writer.close();
//			} catch (Exception ee) {
//			}
//		}
//
//		long closeStop = Calendar.getInstance().getTimeInMillis();
//
//		stats.timeToComplete(closeStop - start);
//		stats.timeToFirstInput(firstInputStop - start);
//		stats.timeToFirst(firstOutputStop - start);
//		stats.producedResults(rc);
//		stats.productionRate(((float) rc / (float) (closeStop - start)) * 1000);
//		log.info("DATASOURCE OPERATOR:Produced first result in " + (firstOutputStop - start) + " milliseconds\n" + "Produced last result in "
//				+ (closeStop - start) + " milliseconds\n" + "Produced " + rc + " results\n" + "Production rate was "
//				+ (((float) rc / (float) (closeStop - start)) * 1000) + " records per second");
//	}
//
//	@Override
//	public URI getLocator() {
//		if (writer != null)
//			try {
//				return writer.getLocator();
//			} catch (GRS2WriterException e) {
//				log.error("Could not retrieve locator", e);
//			}
//		return null;
//	}
//
//	private static FieldDefinition[] initializeSchema(String filterMask) {
//		List<FieldDefinition> fieldDefsList = new ArrayList<FieldDefinition>();
//
//		// if filterMask is null, use all fields
//		if (filterMask == null) {
//			filterMask = "[";
//			for (CMFieldName value : CMFieldName.values())
//				filterMask += value.name() + ", ";
//			filterMask = filterMask.substring(0, filterMask.length() - 2);
//			filterMask += "]";
//		}
//
//		// Filter mask consisted of references e.g [1, 2, 3]
//		if (filterMask.replaceAll("[\\[\\],\\s]", "").matches("\\d*")) {
//			for (String ref : filterMask.replaceAll("[\\[\\]\\s]", "").split(",")) {
//				int index = Integer.parseInt(ref);
//
//				if (index >= CMFieldName.values().length) {
//					log.warn("Filter mask out of range");
//					continue;
//				}
//
//				if (CMFieldName.values()[index].equals(CMFieldName.bytestream))
//					fieldDefsList.add(new FileFieldDefinition(CMFieldName.values()[index].name()));
//				else
//					fieldDefsList.add(new StringFieldDefinition(CMFieldName.values()[index].name()));
//			}
//		}
//		// Filter mask consisted of names e.g [recordId, payload, contentType]
//		else {
//			for (String ref : filterMask.replaceAll("[\\[\\]\\s]", "").split(",")) {
//				try {
//					switch (CMFieldName.valueOf(ref)) {
//					case bytestream:
//						fieldDefsList.add(new FileFieldDefinition(CMFieldName.valueOf(ref).name()));
//						break;
//					default:
//						fieldDefsList.add(new StringFieldDefinition(CMFieldName.valueOf(ref).name()));
//						break;
//					}
//				} catch (IllegalArgumentException e) {
//					log.warn("Filter mask out of range for value: " + ref);
//				}
//			}
//		}
//		log.info("ResultSet schema that will be used: " + fieldDefsList);
//		return fieldDefsList.toArray(new FieldDefinition[fieldDefsList.size()]);
//	}
//}
