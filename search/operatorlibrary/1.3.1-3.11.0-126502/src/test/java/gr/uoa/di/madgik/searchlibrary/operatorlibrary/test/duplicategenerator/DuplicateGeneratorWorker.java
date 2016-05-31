package gr.uoa.di.madgik.searchlibrary.operatorlibrary.test.duplicategenerator;

import gr.uoa.di.madgik.grs.buffer.IBuffer;
import gr.uoa.di.madgik.grs.buffer.IBuffer.Status;
import gr.uoa.di.madgik.grs.events.KeyValueEvent;
import gr.uoa.di.madgik.grs.record.GenericRecord;
import gr.uoa.di.madgik.grs.record.Record;
import gr.uoa.di.madgik.grs.record.field.Field;
import gr.uoa.di.madgik.grs.record.field.StringField;
import gr.uoa.di.madgik.grs.writer.IRecordWriter;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.sort.OfflineSortWorker;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.test.generators.Generator;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.utils.OperatorLibraryConstants;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DuplicateGeneratorWorker extends Thread {
	/**
	 * Logger used by the class
	 */
	private static Logger logger = LoggerFactory.getLogger(OfflineSortWorker.class.getName());
	/**
	 * The Writer to use
	 */
	private IRecordWriter<Record> writer = null;
	private int count = 0;
	private double duplicateProbability = 0.3f;
	private Integer seed = null;
	String[] fieldNames = null;
	String objIdFieldName = null;
	String objRankFieldName = null;
	private Generator<? extends Object>[] fieldGenerators;
	private boolean singleField = false;
	private boolean onlyFinalEvent = false;
	private int bufferCapacity;
	private File outFile = null;
	
	public DuplicateGeneratorWorker(IRecordWriter<Record> writer, int count, String[] fieldNames, String objIdFieldName, String objRankFieldName, Generator<? extends Object>[] fieldGenerators, boolean singleField, boolean onlyFinalEvent, double duplicateProbability, Integer seed, File outFile) throws Exception {
		this.count = count;
	    this.duplicateProbability = duplicateProbability;
	    this.fieldNames = fieldNames;
	    this.objIdFieldName = objIdFieldName;
	    this.objRankFieldName = objRankFieldName;
	    this.fieldGenerators = fieldGenerators;
	    this.singleField = singleField;
	    this.onlyFinalEvent = onlyFinalEvent;
	    this.seed = seed;
	    this.writer = writer;
	    this.outFile = outFile;
	    
	}
	
	public void run() {
		int objIdIndex = -1;
		int objRankIndex = -1;
		for(int i = 0; i < fieldNames.length; i++)
		{
			if(fieldNames[i].equals(objIdFieldName))
				objIdIndex = i;
			else if(fieldNames[i].equals(objRankFieldName))
				objRankIndex = i;
		}
		BufferedWriter out = null;
		
		try {
			
			if(outFile != null) {
				try {
					out = new BufferedWriter(new FileWriter(this.outFile));
				} catch (IOException e) {
					logger.warn("Could not open output file", e);
				}
			}
			
			Random rnd = null;
			if(seed != null)
				rnd = new Random(seed);
			else
				rnd = new Random(Calendar.getInstance().getTimeInMillis());
		
			Integer rc = 0;
			int eventsEmitted = 0;
			boolean finalEmitted = false;
			long now = Calendar.getInstance().getTimeInMillis();
			Map<Integer, List<String>> storedRecs = new HashMap<Integer, List<String>>();
			
			while(rc < count) {
				if(rc==1)
					logger.info("Time to first input: " + (Calendar.getInstance().getTimeInMillis()-now));
				String id = ((Integer)rc).toString();
				String collection = "TestCol";
				String rank = ((Double)rnd.nextDouble()).toString();
				
				StringBuilder record = new StringBuilder();
				String objId = null;
				String objRank = null;
				record.append("<record>");
				
				Field[] fields = null;	
				if(!singleField)
					fields = new Field[this.fieldGenerators.length];
				for(int f = 0; f < fieldNames.length; f++) {
					String payload = fieldGenerators[f].next().toString();
					if(!fieldNames[f].equals(objIdFieldName) && !fieldNames.equals(objRankFieldName))
					{
						record.append("<" + fieldNames[f] + ">");
						record.append(payload);
						record.append("</" + fieldNames[f] + ">");
					}
					if(fieldNames[f].equals(objIdFieldName))
						objId = payload;
					if(fieldNames[f].equals(objRankFieldName))
						objRank = payload;
					if(!singleField)
						fields[f] = new StringField(payload);
				}
				
				if (writer.getStatus() ==  IBuffer.Status.Close || writer.getStatus() == IBuffer.Status.Dispose) {
					logger.info("Consumer side stopped consumption. Stopping prematurely");
					System.out.println("Consumer side stopped consumption. Stopping prematurely");
					break;
				}
				
				Record outRec=null;
				outRec = new GenericRecord();
				if(!singleField) {
					outRec.setFields(fields);
				}else
				{
					StringBuilder outRecord = new StringBuilder(record.toString());
					outRec.setFields(new Field[]{new StringField(outRecord.append("<").append(objIdFieldName).append(">").append(objId).append("</").append(objIdFieldName).append(">").
							append("<").append(objRankFieldName).append(">").append(objRank).append("</").append(objRankFieldName).append(">").append("</record>").toString())});
				}
				
				if(!onlyFinalEvent || eventsEmitted < 10) {
					if(rc % 100 == 0 && rc > 0) {
						writer.emit(new KeyValueEvent(OperatorLibraryConstants.RESULTSNO_EVENT, ""+rc));
						eventsEmitted++;
					}
				}else if(!finalEmitted) {
					writer.emit(new KeyValueEvent(OperatorLibraryConstants.RESULTSNOFINAL_EVENT, ""+count));
					finalEmitted = true;
				}
				
				if(!writer.put(outRec, 60, TimeUnit.SECONDS)) {
					if(writer.getStatus() == Status.Open) {
						logger.warn("Could not write record " + rc + ". Skipping. Available Records = " + writer.availableRecords());
						rc++;
						continue;
					}else {
						System.out.println("Consumer side stopped consumption. Random generator #" + id + " stopping prematurely");
						break;
					}
				}
				
				if(out != null) {
					if(singleField) {
						try {
							out.write(((StringField)outRec.getField(0)).getPayload());
							out.newLine();
							
						}catch(Exception e) {
							logger.warn("Could not persist record to output file", e);
						}
					}else {
						try {
							for(int f = 0; f < fieldNames.length; f++)
								out.write(((StringField)outRec.getField(f)).getPayload() + " ");
							out.newLine();
						}catch(Exception e) {
							logger.warn("Could not persist record to output file", e);
						}
					}
				}
				
				if(rc < 0) {
					if(singleField)
						System.out.println(((StringField)outRec.getField(0)).getPayload());
					else {
						for(int f = 0; f < fieldNames.length; f++) {
							if(outRec.getField(f) instanceof StringField)
								System.out.print(((StringField)outRec.getField(f)).getPayload() + " ");
						}
						System.out.println();
					}
				}
				
				if(rnd.nextDouble() < duplicateProbability + 0.1) {
					storedRecs.put(rc, new ArrayList<String>());
					if(singleField)
					{
						storedRecs.get(rc).add(record.toString());
						storedRecs.get(rc).add(objRank);
						storedRecs.get(rc).add(objId);
					}
					else {
						for(int f = 0; f < fieldNames.length; f++) {
							if(outRec.getField(f) instanceof StringField)
								storedRecs.get(rc).add(((StringField)outRec.getField(f)).getPayload());
						}
					}
						
				}
				
				rc++;
				
				if(rnd.nextDouble() < duplicateProbability && !storedRecs.isEmpty()) {
					Integer dupID = null;
					List<String> dupPayload = null;
					int pos = rnd.nextInt(storedRecs.size());
					int i = 0;
					for(Map.Entry<Integer, List<String>> e : storedRecs.entrySet()) {
						if(i == pos) {
							dupID = e.getKey();
							dupPayload = e.getValue();
							break;
						}
						i++;
					}
					String dupRank = ((Double)rnd.nextDouble()).toString();
					if (writer.getStatus() ==  IBuffer.Status.Close || writer.getStatus() == IBuffer.Status.Dispose) {
						logger.info("Consumer side stopped consumption. Stopping prematurely");
						System.out.println("Consumer side stopped consumption. Stopping prematurely");
						break;
					}
					Record dupRec = null;
					if(singleField)
					{
						//dupRec = new GCubeXMLRecord(dupID.toString(), collection, dupRank, dupPayload.get(0));
						dupRec = new GenericRecord();
						StringBuilder dupRecord = new StringBuilder(dupPayload.get(2));
						dupRec.setFields(new Field[]{new StringField(dupRecord.append("<").append(objIdFieldName).append(">").append(dupID).append("</").append(objIdFieldName).append(">").
								append("<").append(objRankFieldName).append(">").append(dupRank).append("</").append(objRankFieldName).append(">").append("</record>").toString())});
					}
					else {
						Field[] dupFields = new Field[fieldNames.length];
						for(int f = 0; f < dupPayload.size(); f++) { 
							if(!fieldNames[f].equals(objRankFieldName)) //This could break if fields other than StringField are added
								dupFields[f] = new StringField(dupPayload.get(f));
							else
								dupFields[f] = new StringField(dupRank);
						}
						dupRec = new GenericRecord();
						dupRec.setFields(dupFields);
					}
					if(!writer.put(dupRec, 60, TimeUnit.SECONDS)) {
						if(writer.getStatus() == Status.Open) {
							logger.warn("Could not write record " + rc + ". Skipping. Available Records = " + writer.availableRecords());
							rc++;
							continue;
						}else {
							System.out.println("Consumer side stopped consumption. Random generator #" + id + " stopping prematurely");
							break;
						}
					}
					
					if(out != null) {
						try {
							out.write("DUPLICATE:");
							if(singleField)
								out.write(((StringField)((GenericRecord)dupRec).getField(0)).getPayload());
							else {
								for(int f = 0; f < fieldNames.length; f++) {
									if(dupRec.getField(f) instanceof StringField)
										out.write(((StringField)dupRec.getField(f)).getPayload() + " ");
								}
							}
							out.newLine();
								
						}catch(Exception e) {
							logger.warn("Could not persist record to output file", e);
						}
					}
			//		if(rc < 60)
						//System.out.println(dupGcRec.downgrade());
				}
			}
			logger.info("Data generation took "+(Calendar.getInstance().getTimeInMillis()-now));
			logger.info("Production rate was " + ((float)rc/((float)(Calendar.getInstance().getTimeInMillis()-now)))*1000);
		}catch(Exception e) {
			logger.error("Error while generating duplicates", e);
		}finally {
			try {
				writer.close();
			}catch(Exception e) { }
			
			if(out != null) {
				try {
				out.close();
				}catch(Exception e) {
					logger.warn("Could not close output file", e);
				}
			}
		}
	
	}
}
