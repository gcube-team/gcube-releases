package gr.uoa.di.madgik.searchlibrary.operatorlibrary.test.samplegenerator;

import gr.uoa.di.madgik.grs.buffer.IBuffer;
import gr.uoa.di.madgik.grs.buffer.IBuffer.Status;
import gr.uoa.di.madgik.grs.proxy.local.LocalWriterProxy;
import gr.uoa.di.madgik.grs.record.GenericRecord;
import gr.uoa.di.madgik.grs.record.GenericRecordDefinition;
import gr.uoa.di.madgik.grs.record.RecordDefinition;
import gr.uoa.di.madgik.grs.record.field.Field;
import gr.uoa.di.madgik.grs.record.field.StringField;
import gr.uoa.di.madgik.grs.writer.IRecordWriter;
import gr.uoa.di.madgik.grs.writer.RecordWriter;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.sort.OfflineSortWorker;

import java.net.URI;
import java.util.Calendar;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SampleGeneratorWorker extends Thread {
	/**
	 * Logger used by the class
	 */
	private static Logger logger = LoggerFactory.getLogger(OfflineSortWorker.class.getName());
	/**
	 * The Writer to use
	 */
	private IRecordWriter<GenericRecord> writer = null;
	private URI outLocator = null;
	private long timeout = 0;
	private TimeUnit timeUnit = null;
	
	private int tableID = 0;

	/**
	 * Used to synchronize writer retrieval
	 */
	private Object synchWriter=null;
	
	public SampleGeneratorWorker(int tableID, Object synchWriter) throws Exception {
		this.tableID = tableID;
		writer = new RecordWriter<GenericRecord>(new LocalWriterProxy(), new RecordDefinition[]{new GenericRecordDefinition()}, 100,
				RecordWriter.DefaultConcurrentPartialCapacity, RecordWriter.DefaultMirrorBufferFactor);
		outLocator = writer.getLocator();
		this.synchWriter = synchWriter;
	}
	
	/**
	 * retrieves the locator of the writer this thread is populating
	 * 
	 * @return the locator of the writer
	 */
	public URI getLocator(){
		return outLocator;
	}
	
	public void run() {
//		Random rnd = new Random(Calendar.getInstance().getTimeInMillis());
		String[] lastNames  = new String[]{"Rafferty", "Jones", "Steinberg", "Robinson", "Smith", "John"};
		String[][] departmentID = new String[2][];
		departmentID[0] = new String[]{"31", "33", "33", "34", "34", null};
		departmentID[1] = new String[]{"31", "33", "34", "35"};
		String[] departmentName = new String[]{"Sales", "Engineering", "Clerical", "Marketing"};
		Random rnd = new Random(97);
		int rc = 0;
		long now = Calendar.getInstance().getTimeInMillis();
		
		synchronized(this.synchWriter){
			this.synchWriter.notify();
		}

		try {
			int len = tableID == 0 ? lastNames.length : departmentID[1].length;
			for(int i = 0; i < len; i++) {
	
				StringBuilder record = new StringBuilder();
				record.append("<record>");
				if(tableID == 0) {
					record.append("<LastName>" + lastNames[i] + "</LastName>");
					record.append("<DepartmentID>" + departmentID[0][i] + "</DepartmentID>");
				}
				else {
					record.append("<DepartmentID>" + departmentID[1][i] + "</DepartmentID>");
					record.append("<DepartmentName>" + departmentName[i] + "</DepartmentName>");
				}
				record.append("</record>");
				GenericRecord outRec = new GenericRecord();
				StringField outField = new StringField();
				outField.setPayload(record.toString());
				outRec.setFields(new Field[]{outField});
				
				if (writer.getStatus() ==  IBuffer.Status.Close || writer.getStatus() == IBuffer.Status.Dispose) {
					logger.info("Consumer side stopped consumption. Sample generator stopping prematurely");
					System.out.println("Consumer side stopped consumption. Sample generator stopping prematurely");
					break;
				}
				
				if(!writer.put(outRec, timeout, timeUnit)) {
					if(writer.getStatus() == Status.Open) {
						logger.warn("Could not write record " + rc + ". Skipping. Available Records = " + writer.availableRecords());
						rc++;
						continue;
					}else {
						System.out.println("Consumer side stopped consumption. Sample generator stopping prematurely");
						break;
					}
				}
			}
	
			logger.info("Data generation took "+(Calendar.getInstance().getTimeInMillis()-now));
		}catch(Exception e) {
			logger.error("Error while generating sample", e);
		}finally {
			try {
				writer.close();
			}catch(Exception e) { }
		}
	}
}
