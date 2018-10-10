package org.gcube.data.streams;

import static java.util.Arrays.*;
import static junit.framework.Assert.*;
import static org.gcube.data.streams.TestUtils.*;
import static org.gcube.data.streams.dsl.Streams.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class CallbackTest {
	
	@Test
	public void callbackConsumeStreams() {
	
		final List<String> data = asList("1","2","3");
		
		Stream<String> stream = convert(data);
		
		final List<String> consumed = new ArrayList<String>();
		
		Callback<String> callback = new Callback<String>() {
			
			@Override
			public void consume(String element) {
				consumed.add(element);
			}
		};
		
		consume(stream).with(callback);
		
		assertTrue(stream.isClosed());
		
		assertEquals(data,consumed);
	}
	
	
	@Test
	public void callbackStopIteration() {
		
		final List<String> data = asList("1","2","3");
		
		Stream<String> stream = convert(data);
		
		final List<String> consumed = new ArrayList<String>();
		
		Callback<String> callback = new Callback<String>() {
			
			@Override
			public void consume(String element) {
				if (element.equals("2"))
					iteration.stop();
				else
					consumed.add(element);
			}
		};
		
		consume(stream).with(callback);
		
		assertTrue(stream.isClosed());
		
		assertEquals(asList("1"),consumed);		
	}
	
	@Test
	public void callbackSkipsElement() {
		
		final List<String> data = asList("1","2","3");
		
		Stream<String> stream = convert(data);
		
		final List<String> consumed = new ArrayList<String>();
		
		Callback<String> callback = new Callback<String>() {
			
			@Override
			public void consume(String element) {
				if (element.equals("2"))
					iteration.skip();
				else
					consumed.add(element);
			}
		};
		
		consume(stream).with(callback);
		
		assertTrue(stream.isClosed());
		
		assertEquals(asList("1","3"),consumed);		
	}
	
	@Test
	public void consumerPropagatesFailures() {
		
		
		Stream<String> stream = stringsAndFaults("1",fault1,"3");
		
		@SuppressWarnings("unchecked")
		Callback<String> callback = mock(Callback.class);
		
		try {
			consume(stream).with(callback);
			fail();
		}
		catch(Exception e) {
			assertEquals(fault1,e);
		}	
	}
}
