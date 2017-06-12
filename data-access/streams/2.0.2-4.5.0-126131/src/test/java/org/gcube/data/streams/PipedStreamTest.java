package org.gcube.data.streams;

import static java.util.Arrays.*;
import static junit.framework.Assert.*;
import static org.gcube.data.streams.TestUtils.*;
import static org.gcube.data.streams.dsl.Streams.*;
import static org.gcube.data.streams.test.Utils.*;

import java.util.List;

import org.gcube.data.streams.generators.Generator;
import org.gcube.data.streams.test.StreamProvider;
import org.junit.Test;

public class PipedStreamTest {
	
	static Generator<String,String> doubler = new Generator<String, String>() {
		@Override
		public String yield(String element) {
			return element+element;
		}
	};
	
	@Test
	public void pipesYieldValidStreams() throws Exception {
		
		StreamProvider provider = new StreamProvider() {
			public Stream<?> get() {
				Stream<String> stream = convert("1","2","3");
				return pipe(stream).through(doubler);
			}
		};
		
		
		validateWith(provider);
		
		List<String> piped = asList("11","22","33");
		
		assertEquals(piped,elementsOf(provider.get()));
			
	}
	
	@Test
	public void pipesHandleFailures() {
		
		StreamProvider provider = new StreamProvider() {
			public Stream<?> get() {
				Stream<String> stream = stringsAndFaults(fault1,"1",fault2,"2","3",fault3); 
				return pipe(stream).through(doubler); 
			}
		};
		
		validateWith(provider);
		
		List<? extends Object> piped = asList(fault1,"11",fault2,"22","33",fault3);
		
		assertEquals(piped,elementsOf(provider.get())); 
		
	}
	
	@Test
	public void pipesHandleSkipSignals() {
		
		StreamProvider provider = new StreamProvider() {
			public Stream<?> get() {
				Stream<String> stream = stringsAndFaults(skip,"1",skip,"2","3",skip);
				return pipe(stream).through(doubler); 
			}
		};
		
		List<String> piped = asList("11","22","33");
		
		assertEquals(piped,elementsOf(provider.get()));
		
	}
	
	@Test
	public void pipesHandleStopSignals() {
		
		StreamProvider provider = new StreamProvider() {
			public Stream<?> get() {
				Stream<String> stream = stringsAndFaults("1","2",stop,"3");
				return pipe(stream).through(doubler); 
			}
		};
		
		List<String> piped = asList("11","22");
		
		assertEquals(piped,elementsOf(provider.get()));
		
	}
	
	@Test
	public void generatorsCanSkipElements() {
		
		final Generator<String,String> generator = new Generator<String, String>() {
			@Override
			public String yield(String element) {
				if (element.equals("2"))
					iteration.skip();
				return element+element;
			}
		};
		
		StreamProvider provider = new StreamProvider() {
			public Stream<?> get() {
				Stream<String> stream = stringsAndFaults("1","2","3");
				return pipe(stream).through(generator); 
			}
		};
		
		List<String> piped = asList("11","33");
		
		assertEquals(piped,elementsOf(provider.get()));
		
	}
	
	@Test
	public void generatorsCanStopIteration() {
		
		final Generator<String,String> generator = new Generator<String, String>() {
			@Override
			public String yield(String element) {
				if (element.equals("2"))
					iteration.stop();
				return element+element;
			}
		};
		
		StreamProvider provider = new StreamProvider() {
			public Stream<?> get() {
				Stream<String> stream = stringsAndFaults("1","2","3");
				return pipe(stream).through(generator); 
			}
		};
		
		List<String> piped = asList("11");
		
		assertEquals(piped,elementsOf(provider.get()));
		
	}
}
