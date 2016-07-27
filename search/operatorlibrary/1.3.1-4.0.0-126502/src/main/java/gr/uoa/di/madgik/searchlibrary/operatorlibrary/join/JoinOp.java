package gr.uoa.di.madgik.searchlibrary.operatorlibrary.join;

import gr.uoa.di.madgik.grs.proxy.local.LocalWriterProxy;
import gr.uoa.di.madgik.grs.reader.RandomReader;
import gr.uoa.di.madgik.grs.record.GenericRecordDefinition;
import gr.uoa.di.madgik.grs.record.Record;
import gr.uoa.di.madgik.grs.record.RecordDefinition;
import gr.uoa.di.madgik.grs.record.field.Field;
import gr.uoa.di.madgik.grs.record.field.FieldDefinition;
import gr.uoa.di.madgik.grs.writer.IRecordWriter;
import gr.uoa.di.madgik.grs.writer.RecordWriter;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.stats.StatsContainer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.URI;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Operator class used to perform a hash inner join on its input and produces an output holding the joined results.
 * The operator can also function as part of an intersection operation if its {@link RecordGenerationPolicy} is set
 * to {@link RecordGenerationPolicy#KeepLeft} (or, less commonly to {@link RecordGenerationPolicy#KeepRight}.
 * If not set, the {@link RecordGenerationPolicy} is equal to {@link RecordGenerationPolicy#Concatenate} which yields
 * standard inner join functionality.
 * 
 * @author UoA
 */
public class JoinOp {
	/**
	 * The Logger used by this class
	 */
	private static Logger logger = LoggerFactory.getLogger(JoinOp.class.getName());

	/**
	 * The locator of the left input
	 */
	private URI leftLocator = null;
	
	/**
	 * The locator of the right input
	 */
	private URI rightLocator = null;
	
	/**
	 * The default record generation policy. Currently set to {@link RecordGenerationPolicy#Concatenate} which corresponds
	 * to the standard inner join policy
	 */
	public static final RecordGenerationPolicy recordGenerationPolicyDef = RecordGenerationPolicy.Concatenate;
	/**
	 * The default timeout
	 */
	public static final long TimeoutDef = 180;
	/**
	 * The default time unit
	 */
	public static final TimeUnit TimeUnitDef = TimeUnit.SECONDS;
	/**
	 * The default capacity of the {@link RecordWriter}s and, if applicable, of all {@link IRecordReader}s' buffers
	 */
	public static final int BufferCapacityDef = 100;
	/**
	 * The timeout that will be used both by the two {@link RandomReader}s and the {@link IRecordWriter} involved in the join operation
	 */
	private long timeout = TimeoutDef;
	/**
	 * The time unit of the timeout that will be used
	 */
	private TimeUnit timeUnit = TimeUnitDef;
	/**
	 * The unique ID of this operator invocation
	 */
	private String uid = UUID.randomUUID().toString();
	
	/**
	 * The record generation policy that should be followed. {@link RecordGenerationPolicy#Concatenate} should be used for standard inner
	 * join functionality. {@link RecordGenerationPolicy#KeepLeft} should be used if the client wishes to use the operator as the first
	 * step an intersection operation, the second one being a duplicate elimination step. {@link RecordGenerationPolicy#KeepRight}, if used
	 * for the same reason as the former, should yield the same results and is included for completeness.
	 */
	private RecordGenerationPolicy recordGenerationPolicy = recordGenerationPolicyDef;
	/**
	 * A mapping from pairs of definition indices to producer definition indices.
	 * Used when record generation policy is {@link RecordGenerationPolicy#Concatenate}
	 */
	public Map<IndexPair, Integer> producerDefinitionMap = new HashMap<IndexPair, Integer>();
	/**
	 * Container of statistics
	 */
	private StatsContainer stats;
	
	private int bufferCapacity= BufferCapacityDef;
	
	/**
	 * Creates a new {@link JoinOp} with the default timeout
	 * 
	 * @param leftLocator The locator of the left input
	 * @param rightLocator The locator of the right input
	 * @param stats statistics
	 */
	public JoinOp(URI leftLocator, URI rightLocator,StatsContainer stats){
		this.leftLocator = leftLocator;
		this.rightLocator = rightLocator;
		this.stats=stats;
	}
	
	/**
	 * 
	 * @param leftLocator The locator of the left input
	 * @param rightLocator The locator of the right input
	 * @param recordGenerationPolicy The record generation policy that should be followed. {@link RecordGenerationPolicy#Concatenate} should be used for standard inner
	 * join functionality. {@link RecordGenerationPolicy#KeepLeft} should be used if the client wishes to use the operator as the first
	 * step an intersection operation, the second one being a duplicate elimination step. {@link RecordGenerationPolicy#KeepRight}, if used
	 * for the same reason as the former, should yield the same results and is included for completeness.
	 * @param stats Statistics
	 */
	public JoinOp(URI leftLocator, URI rightLocator, RecordGenerationPolicy recordGenerationPolicy, StatsContainer stats) {
		this.leftLocator = leftLocator;
		this.rightLocator = rightLocator;
		this.recordGenerationPolicy = recordGenerationPolicy;
		this.stats = stats;
	}
	/**
	 * Creates a new {@link JoinOp} with configurable timeout
	 * 
	 * @param leftLocator The locator of the left input
	 * @param rightLocator The locator of the right input
	 * @param recordGenerationPolicy The record generation policy that should be followed. {@link RecordGenerationPolicy#Concatenate} should be used for standard inner
	 * join functionality. {@link RecordGenerationPolicy#KeepLeft} should be used if the client wishes to use the operator as the first
	 * step an intersection operation, the second one being a duplicate elimination step. {@link RecordGenerationPolicy#KeepRight}, if used
	 * for the same reason as the former, should yield the same results and is included for completeness.
	 * @param timeout The timeout which will be used by the two {@link RandomReader}s and the {@link RecordWriter}
	 * @param timeUnit The unit of the timeout
	 * @param stats Statistics
	 */
	public JoinOp(URI leftLocator, URI rightLocator, RecordGenerationPolicy recordGenerationPolicy, long timeout, TimeUnit timeUnit, StatsContainer stats){
		this.leftLocator = leftLocator;
		this.rightLocator = rightLocator;
		this.recordGenerationPolicy = recordGenerationPolicy;
		this.timeout = timeout;
		this.timeUnit = timeUnit;
		this.stats=stats;
	}
	
	/**
	 * Creates a new {@link JoinOp} with configurable timeout
	 * 
	 * @param leftLocator The locator of the left input
	 * @param rightLocator The locator of the right input
	 * @param recordGenerationPolicy The record generation policy that should be followed. {@link RecordGenerationPolicy#Concatenate} should be used for standard inner
	 * join functionality. {@link RecordGenerationPolicy#KeepLeft} should be used if the client wishes to use the operator as the first
	 * step an intersection operation, the second one being a duplicate elimination step. {@link RecordGenerationPolicy#KeepRight}, if used
	 * for the same reason as the former, should yield the same results and is included for completeness.
	 * @param timeout The timeout which will be used by the two {@link RandomReader}s and the {@link RecordWriter}
	 * @param timeUnit The unit of the timeout
	 * @param bufferCapacity The capacity of the buffer which will be used by the {@link RecordWriter} and all {@link RandomReader}s (if applicable)
	 * @param stats Statistics
	 */
	public JoinOp(URI leftLocator, URI rightLocator, RecordGenerationPolicy recordGenerationPolicy, long timeout, TimeUnit timeUnit, int bufferCapacity, StatsContainer stats){
		this.leftLocator = leftLocator;
		this.rightLocator = rightLocator;
		this.recordGenerationPolicy = recordGenerationPolicy;
		this.timeout = timeout;
		this.timeUnit = timeUnit;
		this.bufferCapacity = bufferCapacity;
		this.stats=stats;
	}
	
	private RecordDefinition[] getProducerRecordDefinitions(String leftKeyFieldName, String rightKeyFieldName, RandomReader<Record> reader1, RandomReader<Record> reader2) throws Exception {
		
		RecordDefinition[] producerRecordDefinitions = null;
		RecordDefinition leftRecordDefinition = null;
		RecordDefinition rightRecordDefinition = null;
		
		boolean found = false;
		int leftCount = 0;
		int rightCount = 0;
		
		logger.trace(this.uid + ": Reading record definitions of input readers");
		for(int i = 0; i < reader1.getRecordDefinitions().length; i++) {
			if(reader1.getRecordDefinitions()[i].getDefinition(leftKeyFieldName) == -1) {
				logger.warn(this.uid + ": Could not find a field \"" + leftKeyFieldName + "\" in definition #" + i + " of left reader");
				continue;
			}
			found = true;
			leftCount++;
		}
		if(!found) logger.warn(this.uid + ": No record definitions containing the join key field " + "\"" + leftKeyFieldName + " were found in the left input");
		
		found = false;
		for(int i = 0; i < reader2.getRecordDefinitions().length; i++) {
			if(reader2.getRecordDefinitions()[i].getDefinition(rightKeyFieldName) == -1) {
				logger.warn(this.uid + ": Could not find a field \"" + rightKeyFieldName + "\" in definition #" + i + " of right reader");
				continue;
			}
			found = true;
			rightCount++;
		}
		if(!found) {
			logger.warn(this.uid + ": No record definitions containing the join key field " + "\"" + rightKeyFieldName + " were found in the right input");
			return new RecordDefinition[] {};
		}
		
		producerRecordDefinitions = new RecordDefinition[leftCount*rightCount];
		
		int producerDef = 0;
		for(int i = 0; i < reader1.getRecordDefinitions().length; i++) {
			if(reader1.getRecordDefinitions()[i].getDefinition(leftKeyFieldName) == -1) continue;
			for(int j = 0; j < reader2.getRecordDefinitions().length; j++) {
				if(reader2.getRecordDefinitions()[j].getDefinition(rightKeyFieldName) == -1) continue;
				
				leftRecordDefinition = reader1.getRecordDefinitions()[i];
				rightRecordDefinition = reader2.getRecordDefinitions()[j];
				if(!leftRecordDefinition.getClass().equals(rightRecordDefinition.getClass())) {
					logger.error(this.uid + "Left and right record definition type mismatch");
					throw new Exception("Left and right record definition type mismatch");
				}
				FieldDefinition[] producerDefFields = new FieldDefinition[leftRecordDefinition.getDefinitionSize() + rightRecordDefinition.getDefinitionSize() -1];
				
				int ii = 0;
				int jj = 0;
				for(; jj < leftRecordDefinition.getDefinitionSize(); jj++) {
					producerDefFields[ii] = (FieldDefinition)Class.forName(leftRecordDefinition.getDefinition(jj).getClass().getName()).newInstance();
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					leftRecordDefinition.getDefinition(jj).deflate(new DataOutputStream(out));
					producerDefFields[ii++].inflate(new DataInputStream(new ByteArrayInputStream(out.toByteArray())));
				}
				
				jj = 0;
				for(; jj < rightRecordDefinition.getDefinitionSize(); jj++) {
					if(!rightRecordDefinition.getDefinition(jj).getName().equals(rightKeyFieldName)) {
						producerDefFields[ii] = (FieldDefinition)Class.forName(rightRecordDefinition.getDefinition(jj).getClass().getName()).newInstance();
						ByteArrayOutputStream out = new ByteArrayOutputStream();
						rightRecordDefinition.getDefinition(jj).deflate(new DataOutputStream(out));
						producerDefFields[ii++].inflate(new DataInputStream(new ByteArrayInputStream(out.toByteArray())));
					}
				}
		
				producerDefinitionMap.put(new IndexPair(i,j), producerDef);
				producerRecordDefinitions[producerDef++] = new GenericRecordDefinition(producerDefFields);

				//Resolve naming conflicts
				for(int k = 0; k < producerDefFields.length; k++) {
					int cnt = 0;
					for(int l = 0; l < producerDefFields.length; l++) {
						if(k == l) continue;
						if(producerDefFields[k].getName().equals(producerDefFields[l].getName()))
							producerDefFields[l].setName(producerDefFields[l].getName()+"." + cnt++);
					}
				}
			}
		}
		
		return producerRecordDefinitions;
 	}
	
	private int[] getProducerKeyIndices(RecordDefinition[] defs, String keyFieldName) {
		int[] indices = new int[defs.length];
		for(int def = 0; def < defs.length; def++) {
			for(int i = 0; i < defs[def].getDefinitionSize(); i++) {
				if(defs[def].getDefinition(i).getName().equals(keyFieldName))
					indices[def] = i;
			}
		}
		return indices;
	}
	
	/**
	 * Performs the join operation
	 * 
	 * @param type The type of resource to create
	 * @param leftKeyFieldName The name of the {@link Field} of the join key originating from the left locator
	 * @param rightKeyFieldName The name of the {@link Field} of the join key origiating from the right locator
	 * @return The join result
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public URI compute(/*RSResourceType type,*/String leftKeyFieldName, String rightKeyFieldName) throws Exception{
		try{
			long start=Calendar.getInstance().getTimeInMillis();
			
			logger.trace(this.uid + ": Initializing left input reader with locator " + leftLocator);
			RandomReader<Record> reader1 = new RandomReader<Record>(leftLocator, this.bufferCapacity);
			logger.trace(this.uid + ": Initializing right input reader with locator " + rightLocator);
			RandomReader<Record> reader2 = new RandomReader<Record>(rightLocator, this.bufferCapacity);
			IRecordWriter<Record> writer = null;
			RecordDefinition[] producerDef = null;
			if(recordGenerationPolicy == RecordGenerationPolicy.Concatenate) {
				producerDef = getProducerRecordDefinitions(leftKeyFieldName, rightKeyFieldName, reader1, reader2);
				writer = new RecordWriter<Record>(new LocalWriterProxy(), producerDef, 
						this.bufferCapacity, RecordWriter.DefaultConcurrentPartialCapacity, RecordWriter.DefaultMirrorBufferFactor);	
			}
			else if(recordGenerationPolicy == RecordGenerationPolicy.KeepLeft) {
				writer = new RecordWriter<Record>(new LocalWriterProxy(), reader1,
						this.bufferCapacity, RecordWriter.DefaultConcurrentPartialCapacity, RecordWriter.DefaultMirrorBufferFactor);
				producerDef = reader1.getRecordDefinitions();
			}
			else {
				writer = new RecordWriter<Record>(new LocalWriterProxy(), reader2,
						this.bufferCapacity, RecordWriter.DefaultConcurrentPartialCapacity, RecordWriter.DefaultMirrorBufferFactor);
				producerDef = reader2.getRecordDefinitions();
			}
			
		
			stats.timeToInitialize(Calendar.getInstance().getTimeInMillis()-start);
			DefinitionIndexResolver defResolver = new DefinitionIndexResolver(this.recordGenerationPolicy);
			if(this.recordGenerationPolicy.equals(RecordGenerationPolicy.Concatenate))
				defResolver.setDefinitionMap(producerDefinitionMap);
	
			JoinWorker worker=new JoinWorker(writer, reader1, reader2, leftKeyFieldName, rightKeyFieldName, defResolver, 
					 getProducerKeyIndices(producerDef, leftKeyFieldName), recordGenerationPolicy, timeout, timeUnit, stats, uid);
			worker.start();
			logger.trace(this.uid+ ": Returning " + writer.getLocator());
			return writer.getLocator();
		}catch(Exception e){
			logger.error("Could not initialize join operation " + this.uid + ". Throwing Exception", e);
			throw new Exception("Could not initialize join operation");
		}
	}
}
