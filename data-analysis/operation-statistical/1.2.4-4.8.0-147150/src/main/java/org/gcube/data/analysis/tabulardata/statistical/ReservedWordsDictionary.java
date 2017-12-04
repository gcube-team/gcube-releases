package org.gcube.data.analysis.tabulardata.statistical;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import net.sf.csv4j.CSVLineProcessor;
import net.sf.csv4j.CSVReaderProcessor;
import net.sf.csv4j.ParseException;
import net.sf.csv4j.ProcessingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReservedWordsDictionary {
	private static Logger logger = LoggerFactory.getLogger(ReservedWordsDictionary.class);
	private static final ScheduledExecutorService scheduler =   Executors.newScheduledThreadPool(1);	
	
	static{
		scheduler.scheduleWithFixedDelay(new Runnable() {
			
			@Override
			public void run() {
				try {
					sem.acquire();
					if(System.currentTimeMillis()-lastAccessedTime>dictionaryTTL) singleton=null;
					sem.release();
				} catch (InterruptedException e) {										
				}
			}
		}, 5, 5, TimeUnit.MINUTES);
	}
			
	private static ReservedWordsDictionary singleton;
	
	private static final long dictionaryTTL=5*60*1000;
	private static long lastAccessedTime=0l;
	private static Semaphore sem=new Semaphore(0, true);
	
	
	
	
	public static synchronized ReservedWordsDictionary getDictionary() throws ParseException, IOException, ProcessingException{
		lastAccessedTime=System.currentTimeMillis();		
		if(singleton==null) {
			singleton=new ReservedWordsDictionary();
			sem.release();
		}
		return singleton;
	}
	
	
	private HashSet<String> reservedKeywords=new HashSet<>();
	
	private ReservedWordsDictionary() throws ParseException, IOException, ProcessingException{
		logger.debug("Initializing dicionary");
		CSVReaderProcessor processor=new CSVReaderProcessor();
		processor.setDelimiter(',');
		processor.setHasHeader(false);
		Reader reader= new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("reservedWords.csv"), Charset.defaultCharset());
		processor.processStream(reader, new CSVLineProcessor() {
			
			@Override
			public void processHeaderLine(int arg0, List<String> arg1) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void processDataLine(int arg0, List<String> arg1) {
				if(arg1.get(1).equals("reserved")) reservedKeywords.add(arg1.get(0));
			}
			
			@Override
			public boolean continueProcessing() {
				return true;
			}
		});
		logger.debug("Found "+reservedKeywords.size());
	}
	
	public boolean isReservedKeyWord(String str){
		return reservedKeywords.contains(str.toUpperCase());
	}
}
