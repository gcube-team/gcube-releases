package org.gcube.data.transfer.test;

import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.UUID;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.gcube.data.transfer.model.TransferCapabilities;
import org.gcube.data.transfer.model.TransferRequest;
import org.gcube.data.transfer.model.TransferTicket;
import org.gcube.data.transfer.model.TransferTicket.Status;
import org.gcube.data.transfer.model.options.DirectTransferOptions;
import org.gcube.data.transfer.model.options.HttpDownloadOptions;
import org.gcube.data.transfer.model.options.TransferOptions;
import org.gcube.data.transfer.model.options.TransferOptions.TransferMethod;
import org.gcube.data.transfer.model.settings.DirectTransferSettings;
import org.gcube.data.transfer.model.settings.HttpDownloadSettings;
import org.gcube.data.transfer.model.utils.DateWrapper;
import org.junit.BeforeClass;
import org.junit.Test;

public class MarshallUnmarshalTest {

	static JAXBContext ctx =null;
	
	@BeforeClass
	public static void init() throws JAXBException{
		ctx = JAXBContext.newInstance(
				TransferRequest.class,
				TransferTicket.class,				
				TransferCapabilities.class);
	}
	
	
	
	@Test
	public void Marshall() throws MalformedURLException{		
		print(createRequest(TransferMethod.HTTPDownload));
		print(createRequest(TransferMethod.DirectTransfer));
		print(createTransferCapabilities());
		print(createTicket(createRequest(TransferMethod.HTTPDownload)));
		print(createTicket(createRequest(TransferMethod.DirectTransfer)));	
	}
	
	@Test 
	public void UnMarshall() throws MalformedURLException{
		assertTrue(roundTrip(createRequest(TransferMethod.HTTPDownload)));
		assertTrue(roundTrip(createRequest(TransferMethod.DirectTransfer)));
		assertTrue(roundTrip(createTransferCapabilities()));
		assertTrue(roundTrip(createTicket(createRequest(TransferMethod.HTTPDownload))));
		assertTrue(roundTrip(createTicket(createRequest(TransferMethod.DirectTransfer))));
	}
	
	
	public static boolean roundTrip(Object obj){
		Object roundTripResult=unmarshal(obj.getClass(), new StringReader(marshal(obj,new StringWriter()).toString()));
		return obj.equals(roundTripResult);
	}
	
	/**
	 * Write the serialisation of a given resource to a {@link Result}.
	 * @param resource the resource
	 * @param stream the result
	 * @return the result in input
	 */
	public static <T extends Result> T marshal(Object resource,T result) {
		
		
		try {
			JAXBContext context = ctx;
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			
			m.marshal(resource,result);
			
			return result;
		}
		catch(Exception e) {
			throw new RuntimeException("serialisation error",e);
		} 
		
	}
	
	public static void print(Object resource) {

		marshal(resource,new OutputStreamWriter(System.out));
	}
	
	/**
	 * Write the serialisation of a given resource to a given character stream.
	 * @param resource the resource
	 * @param stream the stream in input
	 */
	public static <T extends Writer> T marshal(Object resource,T stream) {
		
		marshal(resource,new StreamResult(stream));
		return stream;
	}
	
	

	/**
	 * Creates a resource of given class from its serialisation in a given {@link Reader}.
	 * @param resourceClass the class of the resource
	 * @param reader the reader
	 * @return the resource
	 */
	public static <T> T unmarshal(Class<T> resourceClass, Reader reader) {
		return unmarshal(resourceClass,new StreamSource(reader));
	}

	/**
	 * Creates a resource of given class from its serialisation in a given {@link InputStream}.
	 * @param resourceClass the class of the resource
	 * @param stream the stream
	 * @return the resource
	 */
	public static <T> T unmarshal(Class<T> resourceClass, InputStream stream) {
		return unmarshal(resourceClass,new StreamSource(stream));
	}
	
	/**
	 * Creates a resource of given class from its serialisation in a given {@link Source}.
	 * @param resourceClass the class of the resource
	 * @param source the source
	 * @return the resource
	 */
	public static <T> T unmarshal(Class<T> resourceClass,Source source) {
		try {			
			Unmarshaller um = ctx.createUnmarshaller();
			return resourceClass.cast(um.unmarshal(source));
		}
		catch(Exception e) {
			throw new RuntimeException("deserialisation error",e);
		}
	}

	private HttpDownloadSettings createHttpSettings()throws MalformedURLException{
		return new HttpDownloadSettings(new URL("http://some.where.com"),createHttpOptions());
	}
	
	private HttpDownloadOptions createHttpOptions() {
		return HttpDownloadOptions.DEFAULT;
	}
	
	private DirectTransferSettings createDirectTransferSettings(){
		return new DirectTransferSettings(createDirectTransferOptions());
	}
	
	private DirectTransferOptions createDirectTransferOptions(){
		return new DirectTransferOptions("mySource");
	}
	
	
	private TransferCapabilities createTransferCapabilities(){
		return new TransferCapabilities("localhost", Collections.singleton((TransferOptions)createHttpOptions()));
	}
	
	private TransferRequest createRequest(TransferOptions.TransferMethod toUseMethod) throws MalformedURLException{		
		switch(toUseMethod){		
		case HTTPDownload : return new TransferRequest(UUID.randomUUID().toString(), createHttpSettings());
		case DirectTransfer : return new TransferRequest(UUID.randomUUID().toString(), createDirectTransferSettings());
		default : return null;
		}		
	}
	
	private TransferTicket createTicket(TransferRequest request){
		return new TransferTicket(request, Status.STOPPED, 1005467l, .57d, 123345, new DateWrapper(),"/dev/null","bona");
	}
}
