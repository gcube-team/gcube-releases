package gr.uoa.di.madgik.grsbroker.helpmanagers;

import gr.uoa.di.madgik.grs.reader.ForwardReader;
import gr.uoa.di.madgik.grs.reader.GRS2ReaderException;
import gr.uoa.di.madgik.grs.reader.GRS2ReaderInvalidArgumentException;
import gr.uoa.di.madgik.grs.reader.decorators.keepalive.KeepAliveReader;
import gr.uoa.di.madgik.grs.record.GenericRecord;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReadersManager {
	private static Logger logger = Logger.getLogger(ReadersManager.class.getName());
	private static Map<String, KeepAliveReader<GenericRecord>> readers = new HashMap<String, KeepAliveReader<GenericRecord>>();
	private static Map<String, Long> leases = new HashMap<String, Long>();
	
	static long leasePeriod = 60000;
	static long gcPeriod = 60000;
	
	static Runnable gc = new Runnable() {
		@Override
		public void run() {
			while (true){
				logger.log(Level.FINE, "-- READER GC START ---");
				
				long currentTime = System.currentTimeMillis();
				Map<String, Long> oldEntries = new HashMap<String, Long>();
				for (Map.Entry<String, Long> lease : leases.entrySet())
					if (currentTime - lease.getValue() > leasePeriod)
						oldEntries.put(lease.getKey(), lease.getValue());
				
				for (String key : oldEntries.keySet()){
					try {
						closeReader(key);
					} catch (GRS2ReaderException e) {
						logger.log(Level.FINE, "error while closing the reader for key : " + key, e);
					}
				}
				
				try {
					Thread.sleep(gcPeriod);
				} catch (InterruptedException e) {
					logger.log(Level.FINE, "error while thread sleep", e);
				}
				
				logger.log(Level.FINE, "-- READER GC END ---");
				
			}
		}
	};
	
	static {
		Thread t = new Thread(gc);
		
		t.setDaemon(true);
		t.start();
	}
	
	public static KeepAliveReader<GenericRecord> getReader(String key, URI locator) throws GRS2ReaderException, GRS2ReaderInvalidArgumentException{
		if (readers.containsKey(key)){
			leases.put(key, System.currentTimeMillis());
		} else {
			ForwardReader<GenericRecord> fwr = new ForwardReader<GenericRecord>(locator);
			KeepAliveReader<GenericRecord> reader = new KeepAliveReader<GenericRecord>(fwr, 10, TimeUnit.SECONDS);
			readers.put(key, reader);
			leases.put(key, System.currentTimeMillis());
		}
		return readers.get(key);
	}
	
	public static void closeReader(String key) throws GRS2ReaderException{
		if (readers.containsKey(key)){
			readers.get(key).close();
			readers.remove(key);
			leases.remove(key);
		}
			
	}
	
	
	
}
