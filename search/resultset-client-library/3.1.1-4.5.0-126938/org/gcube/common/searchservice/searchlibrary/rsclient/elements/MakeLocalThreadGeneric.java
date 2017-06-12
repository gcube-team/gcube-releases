package org.gcube.common.searchservice.searchlibrary.rsclient.elements;

import java.util.Calendar;

import org.apache.log4j.Logger;
import org.gcube.common.searchservice.searchlibrary.resultset.helpers.RSConstants;
import org.gcube.common.searchservice.searchlibrary.rsreader.RSReader;
import org.gcube.common.searchservice.searchlibrary.rswriter.RSFullWriter;

/**
 * Worker Thread that performs a bakground localization operation
 * 
 * @author UoA
 */
public class MakeLocalThreadGeneric extends Thread{
	/**
	 * The Logger of this class
	 */
	public static Logger log = Logger.getLogger(MakeLocalThreadGeneric.class);
	/**
	 * Contstant used
	 */
	public static short ENCODED=0;
	/**
	 * Contstant used
	 */
	public static short CLEAR=1;
	/**
	 * The writer to use
	 */
	private RSFullWriter writer=null;
	/**
	 * The reader to use
	 */
	private RSReader reader=null;
	/**
	 * The type of RS procesed
	 */
	private int type=MakeLocalThreadGeneric.ENCODED;
	/**
	 * The default waiting time
	 */
	private int waittime=RSConstants.sleepMax;
	/**
	 * The port to use if available
	 */
	private int port=-1;
	private boolean SSLsupport=true;
	/**
	 *Creates a new {@link MakeLocalThreadGeneric}
	 * 
	 * @param writer The {@link RSFullWriter} this worker should append records to
	 * @param reader The {@link RSReader} this thread should get his input from
	 * @param type The type of localization to perform with repsect to content type. This can be one of
	 * {@link MakeLocalThreadGeneric#CLEAR} and {@link MakeLocalThreadGeneric#ENCODED}
	 * @param port the port to use for transport if available, -1 otherwise
	 * @param SSLsupport SSL support
	 */
	public MakeLocalThreadGeneric(RSFullWriter writer, RSReader reader,short type,int port, boolean SSLsupport){
		this.writer=writer;
		this.reader=reader;
		if(type!=MakeLocalThreadGeneric.ENCODED && type!=MakeLocalThreadGeneric.CLEAR) this.type=MakeLocalThreadGeneric.ENCODED;
		else this.type=type;
		this.port=port;
		this.SSLsupport = SSLsupport;
	}
	
	/**
	 *Creates a new {@link MakeLocalThreadGeneric}
	 * 
	 * @param writer The {@link RSFullWriter} this worker should append records to
	 * @param reader The {@link RSReader} this thread should get his input from
	 * @param type The type of localization to perform with repsect to content type. This can be one of
	 * @param waittime teh time to wait for production of new parts in milliseconds
	 * {@link MakeLocalThreadGeneric#CLEAR} and {@link MakeLocalThreadGeneric#ENCODED}
	 * @param port the port to use for transport if available, -1 otherwise
	 * @param SSLsupport SSL support
	 */
	public MakeLocalThreadGeneric(RSFullWriter writer, RSReader reader,short type,int waittime,int port, boolean SSLsupport){
		this.writer=writer;
		this.reader=reader;
		if(type!=MakeLocalThreadGeneric.ENCODED && type!=MakeLocalThreadGeneric.CLEAR) this.type=MakeLocalThreadGeneric.ENCODED;
		else this.type=type;
		this.waittime=waittime;
		this.port=port;
		this.SSLsupport = SSLsupport;
	}
	
	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run(){
		log.info("run a MakeLocal Thread.");
		try{
			long startLocal=Calendar.getInstance().getTimeInMillis();
			try{
				if (reader.isSecure()) //throw new Exception("RS is secure.");
					log.info("RS is secure.");
				Transporter.transportStream(reader,writer,port, SSLsupport);
				log.info("Localization completed in "+(Calendar.getInstance().getTimeInMillis()-startLocal)+" millis");
				return;
			}catch(Exception e){
				log.error("could not complete streamed localization.",e);
			}
			int count=0;
			while(true){
				count+=1;
				String decodedFile=null;
				try{
					//decodedFile=Transporter.transportAttached(reader); //ws core 4.1 specific for attachments
					if(this.type==MakeLocalThreadGeneric.ENCODED){
						decodedFile=Transporter.transportEncoded(reader);
					}
					else{
						decodedFile=Transporter.transportClear(reader);
					}
				}catch(Exception e){
					log.error("Could not localize current content part. Continuing",e);
				}
				writer.wrapFile(decodedFile);
				if(count==1) log.info("first chunk available in "+(Calendar.getInstance().getTimeInMillis()-startLocal)+" millis");
				if(!reader.getNextPart(this.waittime)) break;
				writer.startNewPart();
			}
			writer.endAuthoring();
		//	System.out.println("Localization completed in "+(Calendar.getInstance().getTimeInMillis()-startLocal)+" millis");
		}catch(Exception e){
			log.error("Could not end localization procedure.Ending Authoring",e);
			try{
				writer.endAuthoring();
			}catch(Exception ee){
				log.error("Could not end Authoring",ee);
			}
		}
	}
}
