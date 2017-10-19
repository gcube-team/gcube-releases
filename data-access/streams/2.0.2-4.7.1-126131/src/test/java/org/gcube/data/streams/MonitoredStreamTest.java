package org.gcube.data.streams;

import static junit.framework.Assert.*;
import static org.gcube.data.streams.TestUtils.*;
import static org.gcube.data.streams.dsl.Streams.*;
import static org.gcube.data.streams.test.Utils.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import org.gcube.data.streams.delegates.StreamListener;
import org.gcube.data.streams.test.StreamProvider;
import org.junit.Test;

public class MonitoredStreamTest {

	static List<String> testData = Arrays.asList("1","2","3");
	static List<Object> testFailingData = Arrays.<Object>asList(fault1,"1",fault2,"2","3",fault3);

	@Test
	public void consumeAndListens() throws Exception {
		
		StreamListener listener = mock(StreamListener.class);
		
		Stream<String> stream = convert(testData); 
		stream  = monitor(stream).with(listener);
		
		//just consume stream
		elementsOf(stream);
		
		verify(listener).onStart();
		verify(listener).onEnd();
		verify(listener,times(2)).onClose();
	}
	
	@Test
	public void consume() throws Exception {
		
		StreamProvider provider = new StreamProvider() {
			public Stream<?> get() {
				Stream<String> stream = convert(testData); 
				return monitor(stream).with(mock(StreamListener.class));
			}
		};
		
		validateWith(provider);
		
		assertEquals(testData, elementsOf(provider.get()));
	}
	
	@Test
	public void handleFailure() throws Exception {
		
		StreamProvider provider = new StreamProvider() {
			public Stream<?> get() {
				Stream<String> stream = convert(stringsAndFaults(testFailingData)); 
				return monitor(stream).with(mock(StreamListener.class));
			}
		};
		
		validateWith(provider);
		
		assertEquals(testFailingData, elementsOf(provider.get()));
	}
}
