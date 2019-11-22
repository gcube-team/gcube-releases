package org.gcube.data.streams;

import static java.util.Arrays.*;
import static junit.framework.Assert.*;
import static org.gcube.data.streams.TestUtils.*;
import static org.gcube.data.streams.dsl.Streams.*;
import static org.gcube.data.streams.test.Utils.*;

import java.util.List;

import org.gcube.data.streams.dsl.Streams;
import org.gcube.data.streams.generators.Generator;
import org.gcube.data.streams.test.StreamProvider;
import org.junit.Test;

public class UnfoldedStreamTest {

	static Generator<List<String>, Stream<String>> streamer = new Generator<List<String>, Stream<String>>() {
		@Override
		public Stream<String> yield(List<String> element) {
			return convert(element);
		}
	};
	
	@Test
	public void consume() throws Exception {
		
		@SuppressWarnings("unchecked")
		final List<List<String>> data = asList(asList("1","2","3"),asList("1","2","3"));
		
		StreamProvider provider = new StreamProvider() {
			public Stream<?> get() {
				Stream<List<String>> stream = convert(data); 
				return unfold(stream).through(streamer);
			}
		};
		
		List<String> unfoldedData = asList("1","2","3","1","2","3");
		
		assertEquals(unfoldedData,elementsOf(provider.get()));
		
	}
	
	@Test
	public void handleFailures() {

		@SuppressWarnings("unchecked")
		final List<? extends Object> data = asList(asList(fault1,"1",fault2,"2","3",fault3),
				asList("1",fault1,"2",fault2,"3"));
		
		StreamProvider provider = new StreamProvider() {
			public Stream<?> get() {
				
				@SuppressWarnings("all")
				Stream<List<String>> stream = (Stream) Streams.convertWithFaults(List.class,data); 
				return unfold(stream).through(streamer);
			}
		};
		
		List<? extends Object> unfoldedFailingData = asList(fault1,"1",fault2,"2","3",fault3,"1",fault1,"2",fault2,"3");
		
		assertEquals(unfoldedFailingData,elementsOf(provider.get()));
	}

	@Test
	public void ignoreFailures() {

		@SuppressWarnings("unchecked")
		final List<List<String>> data = asList(asList("1","2","3"),asList("1","2","3"));
		
		StreamProvider provider = new StreamProvider() {
			public Stream<?> get() {
				Stream<List<String>> stream = convert(data); 
				Stream<String> unfolded =  unfold(stream).through(streamer);
				return guard(unfolded).with(IGNORE_POLICY);
			}
		};
		
		List<String> unfoldedData = asList("1","2","3","1","2","3");
		
		assertEquals(unfoldedData,elementsOf(provider.get())); 
	}
	
}
