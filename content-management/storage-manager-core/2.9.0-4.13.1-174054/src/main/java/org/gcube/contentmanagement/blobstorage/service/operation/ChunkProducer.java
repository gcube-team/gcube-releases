package org.gcube.contentmanagement.blobstorage.service.operation;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.apache.commons.io.IOUtils;
import org.gcube.contentmanagement.blobstorage.resource.MyFile;
import org.gcube.contentmanagement.blobstorage.transport.backend.util.Costants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Produces the chunks for large files. This class is used only for terrastore
 * @author Roberto Cirillo (ISTI - CNR)
 *
 */
public class ChunkProducer  implements Runnable{
	
	MyFile resource;
	long dimensionChunk;
	int totChunks;
	int nThreads;
	ChunkConsumer task;
	Monitor monitor;
	String bucketName;
	final Logger logger=LoggerFactory.getLogger(ChunkProducer.class);	
	
	public ChunkProducer(Monitor monitor, MyFile resource, long dimensionChunk, int totChunks,
			int nThreads, String bucket, ChunkConsumer consumer ) throws FileNotFoundException{
		this.resource=resource;
		this.dimensionChunk=dimensionChunk;
		this.totChunks=totChunks;
		this.nThreads=nThreads;
		this.monitor=monitor;
		this.task=consumer;
		this.bucketName=bucket;
	}
	
	@Override
	public synchronized void run() {
		 long start=System.currentTimeMillis();
	     ExecutorService executor = Executors.newFixedThreadPool (nThreads);
//	     MyThreadConsumer task=new MyThreadConsumer(monitor, 1, server, bucket);
//			      executor.submit (task);
	     InputStream in=null;
		try {
			in = new BufferedInputStream(new FileInputStream(resource.getLocalPath()));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (int i=0; i<totChunks; i++)
	     {
		//produco un chunk	
			byte[] chunk=null;
			if(!Costants.CLIENT_TYPE.equalsIgnoreCase("mongo")){
//				chunk=encodeFile2ByteChunk2(in, resource.getPathClient(), true , dimensionChunk);
				try {
					chunk=IOUtils.toByteArray(in);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(logger.isDebugEnabled())
				logger.debug("Chunk produced "+i+" with size: "+chunk.length);
			if (logger.isDebugEnabled()) {
				logger.debug("put(MyFile, boolean, boolean) - Produced chunk: "
						+ i);
			}
			//---- creo i task e li invio al thread-pool ----
			String key= getBucketName()+i;
			resource.setKey(key);
			MyFile copy=resource.copyProperties();
			copy.setContent(chunk);
			if(logger.isDebugEnabled()){
				logger.debug("request in queue: "+key);
			}
	   //CHUNK ready to write   	
			monitor.putRequest(copy);
			executor.submit (task);
	    }
	    System.gc(); 
	    executor.shutdown ();
	    try {
			executor.awaitTermination (Long.MAX_VALUE, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	    try {
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		 if (logger.isDebugEnabled()) {
				logger.debug(" Time for file uploading: "+(System.currentTimeMillis()-start));
				logger.debug("Used "
						+ nThreads + " threads"+"\n\n");
		}
		
	}
	
	public byte[] encodeFile2ByteChunk2(InputStream in, String path, boolean isChunk, long chunkDimension) {
		byte[] encode=null;
		try{
			encode=IOUtils.toByteArray(in);
		}catch(IOException e){
			
		}
		return encode;
	}

	public String getBucketName() {
		return bucketName;
	}

	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}
	
}
