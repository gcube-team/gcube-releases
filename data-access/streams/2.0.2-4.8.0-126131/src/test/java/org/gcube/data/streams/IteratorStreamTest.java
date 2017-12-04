package org.gcube.data.streams;

import static java.util.Arrays.*;
import static junit.framework.Assert.*;
import static org.gcube.data.streams.TestUtils.*;
import static org.gcube.data.streams.dsl.Streams.*;
import static org.gcube.data.streams.test.Utils.*;

import java.util.Arrays;
import java.util.List;

import org.gcube.data.streams.test.StreamProvider;
import org.junit.Test;

public class IteratorStreamTest {
	
	@Test
	public void iteratorsMakeValidStreams() {
		
		final List<String> data = Arrays.asList("1","2","3");
		
		StreamProvider provider = new StreamProvider() {
			public Stream<?> get() {
				return convert(data);
			}
		};
		
		validateWith(provider);
		
		assertEquals(data,elementsOf(provider.get()));
	}
	
	
	@Test
	public void iteratorsWithFailuresMakeValidStreams() {
		
		final List<? extends Object> data = asList(fault1,"1",fault2,"2","3",fault3);
		
		StreamProvider provider = new StreamProvider() {
			public Stream<?> get() {
				return convert(data);
			}
		};
		
		validateWith(provider);
		
		assertEquals(data,elementsOf(provider.get())); 
		
	}
}
