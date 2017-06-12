package org.gcube.data.streams;

import static java.util.Arrays.*;
import static junit.framework.Assert.*;
import static org.gcube.data.streams.TestUtils.*;
import static org.gcube.data.streams.dsl.Streams.*;
import static org.gcube.data.streams.test.Utils.*;

import java.net.URI;
import java.util.List;

import org.gcube.data.streams.exceptions.StreamOpenException;
import org.gcube.data.streams.test.StreamProvider;
import org.junit.BeforeClass;
import org.junit.Test;

public class RsStreamTest {
	
	@BeforeClass
	public static void setup() {
		
		System.setProperty("org.slf4j.simplelogger.defaultlog", "trace");
	}

	@Test
	public void resultsetsMakeValidStreams() throws Exception {
		
		final List<String> elements = asList("1",null,"3");
		
		StreamProvider provider = new StreamProvider() {
			
			@Override
			public Stream<?> get() {
				Stream<String> stream = convert(elements);
				URI resultset = publishStringsIn(stream).withDefaults();
				return convert(resultset).ofStrings().withDefaults();
			}
		};
		
		validateWith(provider);
		
		assertEquals(elements,elementsOf(provider.get()));
	}

	
	@Test
	public void resultsetsWithFailuresMakeValidStreams() throws Exception {
		
		RuntimeException fault = new RuntimeException(new TestContingency());
		
		final List<? extends Object> elements = asList(fault,"1",fault,"2","3",fault);
		
		StreamProvider provider = new StreamProvider() {
			
			@Override
			public Stream<?> get() {
				Stream<String> stream = stringsAndFaults(elements);
				URI resultset = publishStringsIn(stream).withDefaults();
				return convert(resultset).ofStrings().withDefaults();
			}
		};
		
		validateWith(provider);
		
		//exceptions instances will be different
		assertEquals(elements.size(),elementsOf(provider.get()).size());
	}
	
	
	
	@Test
	public void resultsetCannotBeOpened() throws Exception {
		
		//publish mock stream
		URI resultset = URI.create("tcp://malformed");
		
		//stream resultset
		Stream<String> stream =convert(resultset).ofStrings().withDefaults();
		
		try {
			stream.next();
			fail();
		}
		catch(StreamOpenException e) {}
		
	}
}
