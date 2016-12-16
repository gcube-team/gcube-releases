package org.gcube.common.searchservice.searchlibrary.rsclient.elements;

import org.apache.log4j.Logger;
import org.gcube.common.searchservice.searchlibrary.rsreader.RSBLOBReader;
import org.gcube.common.searchservice.searchlibrary.rsreader.RSReader;
import org.gcube.common.searchservice.searchlibrary.rsreader.RSTEXTReader;
import org.gcube.common.searchservice.searchlibrary.rsreader.RSXMLReader;

/**
 * Background initialization of a reader
 * 
 * @author UoA
 */
public class InitReaderThread extends Thread{
	/**
	 * Type of reader
	 * 
	 * @author UoA
	 */
	public enum RSReaderEnum{
		/**
		 * RSReader
		 */
		Reader,
		/**
		 * RSXMLReader
		 */
		XMLReader,
		/**
		 * RSBLOBReader
		 */
		BLOBReader,
		/**
		 * RSTEXTReader
		 */
		TEXTReader
	}
	/**
	 * The Logger used by this class
	 */
	private static Logger log = Logger.getLogger(InitReaderThread.class);
	/**
	 * The locator
	 */
	private ReaderInitParams params=null;
	/**
	 * The tye of reader
	 */
	private InitReaderThread.RSReaderEnum readerType;
	/**
	 * The initialized reader
	 */
	private Object reader=null;
	/**
	 * Creates a new instance
	 * 
	 * @param params the initialization params
	 * @param readerType the reader type to initialize
	 */
	public InitReaderThread(ReaderInitParams params,RSReaderEnum readerType){
		this.params=params;
		this.readerType=readerType;
	}
	
	/**
	 * Retrieves the initialized reader
	 * 
	 * @return the reader
	 */
	public Object getReader(){
		return this.reader;
	}
	
	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run(){
		try{
			switch(this.readerType){
			case Reader:
				RSReader r=RSReader.getRSReader(this.params.Locator);
				if(this.params.Localize && this.params.KeepTop) r=r.makeLocal(this.params.ResourceType,this.params.Count);
				else if(this.params.Localize) r=r.makeLocal(this.params.ResourceType);
				else if(this.params.KeepTop) r=r.keepTop(this.params.Count);
				this.reader=r;
				return;
			case BLOBReader:
				RSBLOBReader rb=RSBLOBReader.getRSBLOBReader(this.params.Locator);
				if(this.params.Localize && this.params.KeepTop) rb=rb.makeLocal(this.params.ResourceType,this.params.Count);
				else if(this.params.Localize) rb=rb.makeLocal(this.params.ResourceType);
				else if(this.params.KeepTop) rb=rb.keepTop(this.params.Count);
				this.reader=rb;
				return;
			case TEXTReader:
				RSTEXTReader rt=RSTEXTReader.getRSTEXTReader(this.params.Locator);
				if(this.params.Localize && this.params.KeepTop) rt=rt.makeLocal(this.params.ResourceType,this.params.Count);
				else if(this.params.Localize) rt=rt.makeLocal(this.params.ResourceType);
				else if(this.params.KeepTop) rt=rt.keepTop(this.params.Count);
				this.reader=rt;
				return;
			case XMLReader:
				RSXMLReader rx=RSXMLReader.getRSXMLReader(this.params.Locator);
				if(this.params.Localize && this.params.KeepTop) rx=rx.makeLocal(this.params.ResourceType,this.params.Count);
				else if(this.params.Localize) rx=rx.makeLocal(this.params.ResourceType);
				else if(this.params.KeepTop) rx=rx.keepTop(this.params.Count);
				this.reader=rx;
				return;
			}
			throw new Exception("non recognizable type "+this.readerType);
		}catch(Exception e){
			this.reader=null;
			log.error("could not perform reader initialization on locator "+this.params.Locator.getLocator()+" . setting null",e);
		}
	}
}
