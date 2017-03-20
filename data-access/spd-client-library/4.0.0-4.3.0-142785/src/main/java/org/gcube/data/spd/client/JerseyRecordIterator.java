package org.gcube.data.spd.client;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.gcube.data.spd.client.plugins.AbstractPlugin;
import org.gcube.data.spd.client.proxies.ResultSetClient;
import org.glassfish.jersey.client.ChunkParser;
import org.glassfish.jersey.client.ChunkedInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class JerseyRecordIterator<T> implements Iterator<T>, Closeable{

	private Logger logger = LoggerFactory.getLogger(JerseyRecordIterator.class);

	private ChunkedInput<String> chunkedInput;

	private LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<String>();

	private long INTERNAL_TIMEOUT_IN_MILLIS = 1000;

	private long timeoutInMillis;

	private ResultSetClient resultSetClient;
	
	private String locator;
	
	private ChunkedInputReader chunkedInputReader;

	public JerseyRecordIterator(String endpointId, String locator, long timeout, TimeUnit timeoutUnit) {
		this.resultSetClient = AbstractPlugin.resultset(endpointId).build();
		this.locator = locator;
		this.timeoutInMillis = timeoutUnit.toMillis(timeout);
	}

	String currentElement;

	@Override
	public boolean hasNext() {
		if (this.chunkedInput==null)
			initializeChunckedInput();
				
		if (chunkedInput.isClosed() && queue.isEmpty()) return false;
		try {
			long startTime = System.currentTimeMillis();
			String retrievedElement = null;
			while(retrievedElement==null && (System.currentTimeMillis()-startTime)<=timeoutInMillis 
					&& (!chunkedInput.isClosed() || !queue.isEmpty() )) 
				retrievedElement = queue.poll(INTERNAL_TIMEOUT_IN_MILLIS, TimeUnit.MILLISECONDS);
			
			currentElement = retrievedElement;
			return currentElement!=null;
		} catch (InterruptedException e) {
			logger.warn("timeout expired", e);
			return false;
		}
	}

	private void initializeChunckedInput(){
		this.chunkedInput = resultSetClient.getResultSet(locator);
		this.chunkedInput.setParser(new RecordChunkParser());
		this.chunkedInputReader = new ChunkedInputReader();
		this.chunkedInputReader.start();
	}
	
	@Override
	public T next() {
		try {
			return convertFromString(currentElement);
		} catch (Exception e) {
			logger.warn("error deserializing element", e);
			return null;
		}
	}

	public abstract T convertFromString(String element);

	@Override
	public void remove() {
		chunkedInput.close();
		this.chunkedInputReader.interrupt();
		resultSetClient.closeResultSet(locator);
		queue = null;
	}



	@Override
	public void close() throws IOException {
		logger.debug("closing iterator");
		this.remove();
	}



	class RecordChunkParser implements ChunkParser{

		public byte[] readChunk(InputStream responseStream) throws IOException {
			XMLEventReader xmlr =null;
			XMLOutputFactory of =null;
			XMLEventWriter xmlSW =null;
			try{
				XMLInputFactory xmlif = XMLInputFactory.newInstance();
				xmlr = xmlif.createXMLEventReader(responseStream);
				of = XMLOutputFactory.newInstance(); 
				xmlSW = null;
				StringWriter sw=new StringWriter();
				while (xmlr.hasNext() && queue!=null) {
					XMLEvent e = xmlr.nextEvent();
					int eventType = e.getEventType();
					if (eventType == XMLStreamConstants.START_ELEMENT 
							&& ((StartElement) e).getName().getLocalPart().equals("Result")) {
						xmlSW = of.createXMLEventWriter(sw);
					} else if (eventType == XMLStreamConstants.END_ELEMENT 
							&& ((EndElement) e).getName().getLocalPart().equals("Result")) {
						queue.add(sw.toString());
						sw=new StringWriter();
						xmlSW.close();
						xmlSW= null;
					}else if ( eventType == XMLStreamConstants.END_ELEMENT && ((EndElement) e).getName().getLocalPart().equals("Results")){
						//THE STREAM IS ARRIVED	
						break;
					} else if (xmlSW!=null){
						xmlSW.add(e);
					}
				}	
			}catch(Exception e){
				logger.error("error parsing the input",e);
				throw new IOException(e);
			} finally {
				try {
					if (xmlr!=null)
						xmlr.close();
				} catch (XMLStreamException e) {
					logger.warn("error closing the event reader",e);
				}
				try {
					if (xmlSW!=null)
						xmlSW.close();
				} catch (XMLStreamException e) {
					logger.warn("error closing the event writer",e);
				}
			}
			return null;	
		}

	}

	class ChunkedInputReader extends Thread{

		@Override
		public void run() {
			while (!chunkedInput.isClosed() && chunkedInput.read() != null) {
			}
		}


	}

}
