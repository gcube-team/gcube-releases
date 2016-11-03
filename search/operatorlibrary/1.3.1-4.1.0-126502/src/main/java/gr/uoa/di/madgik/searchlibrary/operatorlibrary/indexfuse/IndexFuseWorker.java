package gr.uoa.di.madgik.searchlibrary.operatorlibrary.indexfuse;

import gr.uoa.di.madgik.grs.buffer.IBuffer;
import gr.uoa.di.madgik.grs.reader.ForwardReader;
import gr.uoa.di.madgik.grs.reader.IRecordReader;
import gr.uoa.di.madgik.grs.reader.RandomReader;
import gr.uoa.di.madgik.grs.record.GenericRecord;
import gr.uoa.di.madgik.grs.record.Record;
import gr.uoa.di.madgik.grs.record.field.Field;
import gr.uoa.di.madgik.grs.record.field.StringField;
import gr.uoa.di.madgik.grs.writer.IRecordWriter;
import gr.uoa.di.madgik.grs.writer.RecordWriter;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.stats.StatsContainer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper thread class used to perform the fuse operation
 * 
 * @author UoA
 */
public class IndexFuseWorker extends Thread{
	/**
	 * The Logger used by this class
	 */
	private static Logger logger = LoggerFactory.getLogger(IndexFuseWorker.class.getName());
	/**
	 * The weight of the content score
	 */
	private double weightContent = 0.5;
	/**
	 * The weight of the metadata score
	 */
	private double weightMetadata = 0.5;
	/**
	 * The readers for the content collections input
	 */
	private ForwardReader<Record>[] contentReaders = null;
	/**
	 * The actual number of RSs that gave at least one result for each collection
	 */
	private int[] actualCols = null;
	/**
	 * The readers for the metadata collections input
	 */
	private ForwardReader[][] metaReaders = null;
	/**
	 * The number of collections contained in the results
	 */
	private int numOfCols;
	/**
	 * The writer to use
	 */
	private RecordWriter<Record> writer = null;
	
	/**
	 * The name of the {@link Field} the object id will be stored to
	 */
	private String objectIdFieldName = null;
	/**
	 * The name of the {@link Field} the collection will be stored to
	 */
	private String collectionFieldName = null;
	/**
	 * The name of the {@link Field} the rank will be stored to
	 */
	private String rankFieldName = null;
	/**
	 * Used for synchronization
	 */
	private Object synchThis=null;
	/**
	 * The timeout to be used both by the {@link RandomReader}s and the {@link IRecordWriter}
	 */
	private long timeout;
	/**
	 * The time unit of the timeout that will be used
	 */
	private TimeUnit timeUnit = null;
	/**
	 * The number of joined elements
	 */
	private int count=0;
	/**
	 * Used for timing
	 */
	private long firststop=0;
	/**
	 * statistics
	 */
	private StatsContainer stats;
	/**
	 * the metadata ColIDs for tagging the results
	 */
	private String[] colIDs = null;
	
	/**
	 * Creates a new {@link IndexFuseWorker}
	 * 
	 * @param writer The {@link RSXMLWriter} to write records to
	 * @param contentReaders The iterators over the content collections input
	 * @param metaReaders The iterators over the metadata collections input
	 * @param weightContent The weight of the content scoring
	 * @param weightMetadata The weight of the metadata scoring
	 * @param stats statistics
	 */
	public IndexFuseWorker(RecordWriter<Record> writer, ForwardReader<Record>[] contentReaders, ForwardReader<Record>[][] metaReaders, String[] colIDs, double weightContent, double weightMetadata, 
			String objectIdFieldName, String collectionFieldName, String rankFieldName, long timeout, TimeUnit timeUnit, StatsContainer stats){
		this.contentReaders = contentReaders;
		this.metaReaders = metaReaders;
		this.writer=writer;
		this.weightContent =  weightContent;
		this.weightMetadata = weightMetadata;
		this.objectIdFieldName = objectIdFieldName;
		this.collectionFieldName = collectionFieldName;
		this.rankFieldName = rankFieldName;
		this.colIDs = colIDs;
		this.timeout = timeout;
		this.timeUnit = timeUnit;
		this.synchThis=new Object();
		this.stats=stats;
		this.numOfCols = contentReaders.length;
		this.actualCols = new int[contentReaders.length];
		for(int i=0; i<actualCols.length; i++)
		{
			actualCols[i] = 0;
			if(contentReaders[i] != null)
				actualCols[i]++;
			if(metaReaders[i] != null)
				actualCols[i] += metaReaders[i].length;
			logger.info(actualCols[i] + " content+metadata collections will participate for the results of the "+i+"-th collection");
		}
	}
	
	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run(){
		long start=Calendar.getInstance().getTimeInMillis();
		try{
			Queue<JoinElement> queue=new LinkedList<JoinElement>();
			ScanElement contentScans[] = new ScanElement[numOfCols];
			ScanElement metaScans[][] = new ScanElement[numOfCols][];
			//create scanners for all the readers
			for(int i = 0; i < numOfCols; i++)
			{
				if(contentReaders[i]==null)
					contentScans[i] = null;
	
				else{
					contentScans[i] = new ScanElement(contentReaders[i], queue, synchThis, (short)i, (short)0, timeout, timeUnit);
					ReaderScan scan = new ReaderScan(contentScans[i], this.objectIdFieldName, this.collectionFieldName, this.rankFieldName);
					logger.info("Starting scanner for the content of the "+i+"-th collection");
					scan.start();
				}
				
				if(metaReaders[i]==null)
					metaScans[i] = null;
				else{
					metaScans[i] = new ScanElement[metaReaders[i].length];
					for(int j=0; j < metaReaders[i].length; j++)
					{
						metaScans[i][j] = new ScanElement(metaReaders[i][j], queue, synchThis, (short)i, (short)(j+1), timeout, timeUnit);
						ReaderScan scan = new ReaderScan(metaScans[i][j], this.objectIdFieldName, this.collectionFieldName, this.rankFieldName);
						logger.info("Starting scanner for the "+j+"-th metadata col of the "+i+"-th collection");
						scan.start();
					}
				}
			}
			
			JoinElement tmp=null;
			ArrayList<HashMap<String, ResultElement>> results = new ArrayList<HashMap<String, ResultElement>>(numOfCols);
			for(int i=0; i<numOfCols; i++)
				results.add(new HashMap<String, ResultElement>());
			LinkedList<Double> ranks = new LinkedList<Double>();
			LinkedList<String> ids = new LinkedList<String>();
			LinkedList<String> cols = new LinkedList<String>();
			long now=Calendar.getInstance().getTimeInMillis();
			logger.info("Retrieving entries from global queue while scanning RSs");
			while(true){
				synchronized (this.synchThis) {
					if(scanFinished(contentScans, metaScans)){
						logger.info("All the scanners completed their work");
						break;
					}
					tmp=queue.poll();
					if(tmp==null){
						this.synchThis.wait(2000);
					}
				}
				if(tmp==null) continue;
				if(this.count==0) 
					stats.timeToFirstInput(Calendar.getInstance().getTimeInMillis()-now);
				logger.info("Retrieved entry from queue for the "+tmp.getCollectionID()+"-th collection content/metadata:"+tmp.getMetaColID()+" while scanning RSs");
				this.checkJoin(tmp,results,ranks,ids,cols);
			}
			//check if there were no elements retrieved from some Result Sets
			for(int i=0; i<contentScans.length; i++)
			{
				if(contentScans[i] != null && contentScans[i].getCounter() == 0)
				{
					actualCols[i]--;
					logger.info("There were no results retrieved from the content RS of the "+i+"-th collection");
				}
				if(metaScans[i] != null)
					for(int j=0; j<metaScans[i].length; j++)
						if(metaScans[i][j].getCounter() == 0)
						{
							actualCols[i]--;
							logger.info("There were no results retrieved from the "+j+"-th metadata RS of the "+i+"-th collection");
						}
			}
			//if there were Result Sets that did not provide any element we must check if there are results ready to be sent
			for(int i=0; i<contentScans.length; i++)
			{
				int initialCols = 0;
				if(contentScans[i] != null)
					initialCols++;
				if(metaScans[i] != null)
					initialCols += metaScans[i].length;
				if(actualCols[i] < initialCols)
				{
					logger.info("Checking if there are results ready to be sent for the "+i+"-th collection");
					Iterator<ResultElement> iter = results.get(i).values().iterator();
					while(iter.hasNext())
					{
						ResultElement res = iter.next();
						logger.info("Result for OID:"+res.getId()+" Rank:"+res.getRank()+" has "+res.getInserted()+" elements inserted");
						if(res.getInserted() == actualCols[i])
						{
							if(this.count==0) 
								firststop=Calendar.getInstance().getTimeInMillis();
							this.count+=1;
							int index = Collections.binarySearch(ranks, (-1)*res.getRank());
							if(index < 0)
								index = -index-1;
							ranks.add(index, (-1)*res.getRank());
							ids.add(index, res.getId());
							cols.add(index, colIDs[i]);
							res.tagSent();
							logger.info("Result for OID:"+res.getId()+" Rank:"+res.getRank()+" included in the sorted list");
						}
					}
				}
			}
			logger.info("Reading the rest of the global queue");
			//read the rest of the queue
			tmp=queue.poll();
			while(tmp!=null){
				this.checkJoin(tmp,results,ranks,ids,cols);
				tmp=queue.poll();
			}
			logger.info("Sending the rest of the results");
			//send the rest of the results
			for(int i=0; i<contentScans.length; i++)
			{
				Iterator<ResultElement> iter = results.get(i).values().iterator();
				while(iter.hasNext())
				{
					ResultElement res = iter.next();
					logger.info("Result for OID:"+res.getId()+" Rank:"+res.getRank()+" isSent:"+res.isSent()+" has "+res.getInserted()+" elements inserted");
					if(res.getInserted() < actualCols[i])
					{
						if(this.count==0) 
							firststop=Calendar.getInstance().getTimeInMillis();
						this.count+=1;
						int index = Collections.binarySearch(ranks, (-1)*res.getRank());
						if(index < 0)
							index = -index-1;
						ranks.add(index, (-1)*res.getRank());
						ids.add(index, res.getId());
						cols.add(index, colIDs[i]);
						res.tagSent();
						logger.info("Result for OID:"+res.getId()+" Rank:"+res.getRank()+" included in the sorted list");
					}
				}
			}
			//sent an RS with the sorted list
			logger.info("Writing results to RS");
			long startWriting = Calendar.getInstance().getTimeInMillis();
			String id = ids.poll();
			while(id != null)
			{
				if(writer.getStatus() == IBuffer.Status.Close || writer.getStatus() == IBuffer.Status.Dispose) {
					logger.info("Consumer side stopped consumption. Stopping.");
					break;
				}
				double rank = (-1)*ranks.poll();
				String colID = cols.poll();
				GenericRecord outRec = new GenericRecord();
				Field[] fields = new Field[3];
				fields[0] = new StringField(id);
				fields[1] = new StringField(colID);
				fields[2] = new StringField(String.valueOf(rank));
				outRec.setFields(fields);
	
				if(!writer.put(outRec, timeout, timeUnit) ) {
					if(writer.getStatus() == IBuffer.Status.Open)
						logger.warn("Consumer has timed out");
					break;
				}
				logger.info("Added record:" + fields[0] + " " + fields[1] + " " + fields[2]);
				id = ids.poll();
			}
			long stopWriting = Calendar.getInstance().getTimeInMillis();
			logger.info("Finished writing output RS. Time(millisecs) needed was:"+(stopWriting-startWriting));
/*			for(int i=0; i<wbufs.length; i++)
			{
				log.info("Fuse tmp file " + (i+1) + " writing time was "+wbufs[i].wtime);
				log.info("Fuse tmp file " + (i+1) + " reading time was "+rbufs[i].rtime);
				rbufs[i].clear();
			}
*/
			try { writer.close(); } catch(Exception e) { }
			closeReaders(contentReaders, metaReaders);
			
			long closestop=Calendar.getInstance().getTimeInMillis();
			stats.timeToComplete(closestop-start);
			stats.timeToFirst(firststop-start);
			stats.productionRate((((float)this.count/(float)(closestop-start))*1000));
			stats.producedResults(this.count);
			logger.info("INDEX FUSE OPERATOR:" +
					"Produced first result in "+(firststop-start)+" milliseconds\n" +
					"Produced last result in "+(closestop-start)+" milliseconds\n" +
					"Produced " + this.count + " results\n" + 
					"Production rate was "+(((float)this.count/(float)(closestop-start))*1000)+" records per second");
		}catch(Exception e){
			logger.error("Error while background index fusing. Closing",e);
			try { writer.close(); } catch(Exception ee) { }
			closeReaders(contentReaders, metaReaders);
		}
	}
	
	private boolean scanFinished(ScanElement[] contentScans, ScanElement[][] metaScans)
	{
		for(int i=0; i<contentScans.length; i++)
			if(contentScans[i] != null && contentScans[i].isActive())
				return false;
		for(int i=0; i<metaScans.length; i++)
			if(metaScans[i]!=null)
				for(int j=0; j<metaScans[i].length; j++)
					if(metaScans[i][j].isActive())
						return false;
		return true;
	}
	
	@SuppressWarnings("rawtypes")
	private void closeReaders(IRecordReader[] contentReaders, IRecordReader[][] metaReaders) {
		for(int i = 0; i < numOfCols; i++) {
			if(contentReaders[i] != null)
				try { contentReaders[i].close(); } catch(Exception e) { }
			
			if(metaReaders[i]!=null) {
				for(int j=0; j < metaReaders[i].length; j++)
					try { metaReaders[i][j].close(); } catch(Exception e) { }
			}
		}
	}
	
	/**
	 * Inserts the provided element to the result for this IO and checks if the result for this IO is ready to be sent.
	 * 
	 * @param tmp The element to check
	 * @param results The HashMaps used to store the results
	 * @param ranks The sortedList that holds the rank results to be sent
	 * @param ids The sortedList that holds the OID results to be sent
	 */
	 
	private void checkJoin(JoinElement tmp, ArrayList<HashMap<String, ResultElement>> results, LinkedList<Double> ranks, LinkedList<String> ids, LinkedList<String> cols){
		try{
		//the ID of the IO, this JoinElement refers to
		String id = tmp.getId();
		int collection = tmp.getCollectionID();
		double weightContent = this.weightContent;
		double weightMetadata = this.weightMetadata;
		//if this is the first element for this IO		
		if(!results.get(collection).containsKey(id))
		{
			logger.info("This is the first element for OID:"+id);
			if(metaReaders[collection] == null && contentReaders[collection] == null)
			{
				//This should never happen. If we get a JoinElement tagged with this collection number
				//it means that it has at least one iterator
				throw new Exception("Undefined Server Error!");				
			}else if(metaReaders[collection] == null){
				//if we don't have metadata then we must give all the weight to content
				weightContent = 1.0;
				ResultElement res = new ResultElement(id, 0);
				if(tmp.getMetaColID() != 0)
				{
					//This should never happen. There is no iterator for metadata 
					//and this element should be tagged as content
					throw new Exception("Undefined Server Error! There is no metadata for the " + collection + "-th collection");
				}
				res.insertElement(tmp, weightContent);
				res.tagSent();
				results.get(collection).put(id, res);
				//this collection contains only content, so this element is ready to be included in the sorted lists
				if(this.count==0) 
					firststop=Calendar.getInstance().getTimeInMillis();
				this.count+=1;
				int index = Collections.binarySearch(ranks, (-1)*res.getRank());
				if(index < 0)
					index = -index-1;
				ranks.add(index, (-1)*res.getRank());
				ids.add(index, id);
				cols.add(index,colIDs[collection]);
				logger.info("Result for OID:"+res.getId()+" Rank:"+res.getRank()+" included in the sorted list");
			}else if(contentReaders[collection] == null){
				//if we don't have content then we must give all the weight to metadata
				weightMetadata = 1.0;
				ResultElement res = new ResultElement(id, metaReaders[collection].length);
				if(tmp.getMetaColID() == 0)
				{
					//This should never happen. There is no iterator for content 
					//and this element should be tagged as metadata
					throw new Exception("Undefined Server Error! There is no content for the " + collection + "-th collection");
				}
				res.insertElement(tmp, weightMetadata/metaReaders[collection].length);
				logger.info("Result for OID:"+res.getId()+" has new Rank:"+res.getRank());
				//if there is only one collection that gives elements
				if(actualCols[collection] == res.getInserted())
				{
					if(this.count==0) 
						firststop=Calendar.getInstance().getTimeInMillis();
					this.count+=1;
					int index = Collections.binarySearch(ranks, (-1)*res.getRank());
					if(index < 0)
						index = -index-1;
					ranks.add(index, (-1)*res.getRank());
					ids.add(index, id);
					cols.add(index,colIDs[collection]);
					res.tagSent();
					logger.info("Result for OID:"+res.getId()+" Rank:"+res.getRank()+" included in the sorted list");
				}
				results.get(collection).put(id, res);
			}else{
				ResultElement res = new ResultElement(id, metaReaders[collection].length);
				if(tmp.getMetaColID() == 0)
					res.insertElement(tmp, weightContent);
				else
					res.insertElement(tmp, weightMetadata/metaReaders[collection].length);
				logger.info("Result for OID:"+res.getId()+" has new Rank:"+res.getRank());
				//if there is only one collection that gives elements
				if(actualCols[collection] == res.getInserted())
				{
					if(this.count==0) 
						firststop=Calendar.getInstance().getTimeInMillis();
					this.count+=1;
					int index = Collections.binarySearch(ranks, (-1)*res.getRank());
					if(index < 0)
						index = -index-1;
					ranks.add(index, (-1)*res.getRank());
					ids.add(index, id);
					cols.add(index,colIDs[collection]);
					res.tagSent();
					logger.info("Result for OID:"+res.getId()+" Rank:"+res.getRank()+" included in the sorted list");
				}
				results.get(collection).put(id, res);
			}
							
		}else{
			ResultElement res = results.get(collection).get(id);
			logger.info("There is already an element for OID:"+id+" with Rank:"+res.getRank());
			if(res.isSent())
				return ;
			boolean inserted;
			//if there is no content collection for this collection
			if(contentReaders[collection] == null)
				weightMetadata = 1.0;
			//if this element refers to content
			if(tmp.getMetaColID() == 0)
				inserted = res.insertElement(tmp, weightContent);
			else
				inserted = res.insertElement(tmp, weightMetadata/metaReaders[collection].length);
			logger.info("Result for OID:"+res.getId()+" has new Rank:"+res.getRank());
			//if the result is ready to be transfered
			if(actualCols[collection] == res.getInserted() && inserted)
			{
				if(this.count==0) 
					firststop=Calendar.getInstance().getTimeInMillis();
				this.count+=1;
				int index = Collections.binarySearch(ranks, (-1)*res.getRank());
				if(index < 0)
					index = -index-1;
				ranks.add(index, (-1)*res.getRank());
				ids.add(index, id);
				cols.add(index,colIDs[collection]);
				res.tagSent();
				logger.info("Result for OID:"+res.getId()+" Rank:"+res.getRank()+" included in the sorted list");
			}
			results.get(collection).put(id, res);
		}
		
		}catch(Exception e){
			logger.error("Could not check join. Continuing",e);
		}
	}
}
