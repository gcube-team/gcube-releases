package org.gcube.data.spd.plugin.fwk.writers.rswrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.xml.bind.JAXBException;
import gr.uoa.di.madgik.commons.server.PortRange;
import gr.uoa.di.madgik.commons.server.TCPConnectionManager;
import gr.uoa.di.madgik.commons.server.TCPConnectionManagerConfig;
import gr.uoa.di.madgik.grs.buffer.IBuffer.Status;
import gr.uoa.di.madgik.grs.proxy.tcp.TCPConnectionHandler;
import gr.uoa.di.madgik.grs.proxy.tcp.TCPStoreConnectionHandler;
import gr.uoa.di.madgik.grs.proxy.tcp.TCPWriterProxy;
import gr.uoa.di.madgik.grs.record.GenericRecord;
import gr.uoa.di.madgik.grs.record.GenericRecordDefinition;
import gr.uoa.di.madgik.grs.record.RecordDefinition;
import gr.uoa.di.madgik.grs.record.field.FieldDefinition;
import gr.uoa.di.madgik.grs.record.field.StringField;
import gr.uoa.di.madgik.grs.record.field.StringFieldDefinition;
import gr.uoa.di.madgik.grs.writer.GRS2WriterException;
import gr.uoa.di.madgik.grs.writer.RecordWriter;
import java.net.InetAddress;
import org.gcube.data.spd.model.binding.Bindings;
import org.gcube.data.spd.model.exceptions.InvalidRecordException;
import org.gcube.data.spd.model.exceptions.StreamException;
import org.gcube.data.spd.model.exceptions.WrapperAlreadyDisposedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ResultWrapper<T> extends AbstractWrapper<T>{ 

	
	static{
		List<PortRange> ports=new ArrayList<PortRange>(); //The ports that the TCPConnection manager should use
		ports.add(new PortRange(3050, 3100));            
		
		String hostname ="";
		try{
			hostname = InetAddress.getLocalHost().getHostName();
		}catch (Exception e) {
			//error getting the hosname
		}
		
		TCPConnectionManager.Init(
		  new TCPConnectionManagerConfig(hostname, //The hostname by which the machine is reachable 
		    ports,                                    //The ports that can be used by the connection manager
		    true                                      //If no port ranges were provided, or none of them could be used, use a random available port
		));
		TCPConnectionManager.RegisterEntry(new TCPConnectionHandler());      //Register the handler for the gRS2 incoming requests
		TCPConnectionManager.RegisterEntry(new TCPStoreConnectionHandler());
	}
	
	private static final Logger logger = LoggerFactory.getLogger(ResultWrapper.class);
	
	private RecordWriter<GenericRecord> writer=null;
	
	public ResultWrapper() throws Exception{
		StringFieldDefinition fieldDefinition = new StringFieldDefinition("result");
		RecordDefinition[] defs=new RecordDefinition[]{          //A gRS can contain a number of different record definitions
		        new GenericRecordDefinition((new FieldDefinition[] { //A record can contain a number of different field definitions
		        		fieldDefinition				        //The definition of the field
		      }))
		    };
		 this.writer=new RecordWriter<GenericRecord>(
		       new TCPWriterProxy(), //The proxy that defines the way the writer can be accessed
		       defs,   //The definitions of the records the gRS handles
		       50,              //The capacity of the underlying synchronization buffer
		       2,               //The maximum number of parallel records that can be concurrently accessed on partial transfer  
		       0.5f,            //The maximum fraction of the buffer that should be transfered during mirroring 
		       5,              //The timeout in time units after which an inactive gRS can be disposed  
		       TimeUnit.MINUTES //The time unit in timeout after which an inactive gRS can be disposed
		 );
		 
		 this.links = 0;
	}
	
	public String getLocator() throws GRS2WriterException {
		return this.writer.getLocator().toString();
	}
	
	public synchronized boolean add(T input) throws InvalidRecordException, WrapperAlreadyDisposedException{
					
		GenericRecord gr=new GenericRecord();
		String bindElement = null;
		
		if (input instanceof String)
			bindElement = (String)input;
		else
			try{
				bindElement = Bindings.toXml(input);
			}catch(JAXBException e){
				throw new InvalidRecordException(e);
			}
		
		gr.setFields(new StringField[]{new StringField(bindElement)});
		try {
			return writer.put(gr,5,TimeUnit.MINUTES);
		}catch (Exception e) {
			logger.trace("the writer is already disposed (trying to write something when it is closed)");
			throw new WrapperAlreadyDisposedException(e);
		}
	}
	
	
	public void close(){
		try {
			if (!this.isClosed())
				this.writer.close();
		} catch (Exception e) {
			logger.warn("error closing the writer", e);
		}
	}

	
	@Override
	public boolean isClosed() {
		return (writer.getStatus()==Status.Dispose || writer.getStatus()==Status.Close);
	}

	@Override
	public boolean add(StreamException result) throws InvalidRecordException,
			WrapperAlreadyDisposedException {
		try {
			return writer.put(result,5,TimeUnit.MINUTES);
		}catch (Exception e) {
			logger.trace("the writer is already disposed (trying to write something when it is closed)");
			throw new WrapperAlreadyDisposedException(e);
		}
	}
	
}
