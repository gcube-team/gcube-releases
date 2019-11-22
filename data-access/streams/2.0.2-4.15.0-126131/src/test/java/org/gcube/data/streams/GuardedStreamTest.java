package org.gcube.data.streams;

import static java.util.Arrays.*;
import static java.util.Collections.*;
import static junit.framework.Assert.*;
import static org.gcube.data.streams.TestUtils.*;
import static org.gcube.data.streams.dsl.Streams.*;
import static org.gcube.data.streams.test.Utils.*;

import java.util.Arrays;
import java.util.List;

import org.gcube.data.streams.generators.Generator;
import org.gcube.data.streams.handlers.CountingHandler;
import org.gcube.data.streams.handlers.FaultHandler;
import org.gcube.data.streams.test.StreamProvider;
import org.junit.Test;

public class GuardedStreamTest {

	static List<String> testData = Arrays.asList("1","2","3");
	
	static List<Object> testFailingData1 = Arrays.<Object>asList(fault1,"1",fault2,"2",fault3,"3");
	static List<Object> testFailingData2 = Arrays.<Object>asList(skip,"1",fault2,"2","3",fault3);
	
	static Generator<String,String> doubler = new Generator<String, String>() {
		@Override
		public String yield(String element) {
			return element+element;
		}
	};
	
	@Test
	public void consumeNoFailures() throws Exception {
		
		final List<String> data = asList("1","2","3");
		
		StreamProvider provider = new StreamProvider() {
			public Stream<?> get() {
				return guard(convert(data)).with(STOPFAST_POLICY); 
			}
		};
		
		validateWith(provider);
		
		assertEquals(data,elementsOf(provider.get()));
		
	}

	@Test
	public void consumeIgnoreFailures() {
		
		final List<? extends Object> data = asList(fault1,"1",fault2,"2",fault3,"3");
		
		StreamProvider provider = new StreamProvider() {
			public Stream<?> get() {
				return guard(stringsAndFaults(data)).with(IGNORE_POLICY); 
			}
		};
		
		validateWith(provider);
		
		List<String> preserved = asList("1","2","3");
		
		assertEquals(preserved,elementsOf(provider.get())); 
		
	}
	
	@Test
	public void consumeStopFast() {
		
		final List<? extends Object> data = asList(fault1,"1",fault2,"2",fault3,"3");
		
		StreamProvider provider = new StreamProvider() {
			public Stream<?> get() {
				return guard(stringsAndFaults(data)).with(STOPFAST_POLICY); 
			}
		};
		
		validateWith(provider);
		
		assertEquals(emptyList(),elementsOf(provider.get())); 
		
	}
	
	@Test
	public void consumeStopFastWithSkips() {
		
		final List<? extends Object> data = asList(skip,"1",fault2,"2","3",fault3);
		
		StreamProvider provider = new StreamProvider() {
			public Stream<?> get() {
				return guard(stringsAndFaults(data)).with(STOPFAST_POLICY); 
			}
		};
		
		assertEquals(asList("1"),elementsOf(provider.get())); 
		
	}
	
	@Test
	public void consumeStopFastCustomPolicy() {
		
		final FaultHandler customPolicy = new FaultHandler() {
			
			@Override
			public void handle(RuntimeException failure) {
				if (failure==fault2)
					iteration.stop();
			}
		};
		
		final List<? extends Object> data = asList(fault1,"1",fault2,"2",fault3,"3");
		
		StreamProvider provider = new StreamProvider() {
			public Stream<?> get() {
				return guard(stringsAndFaults(data)).with(customPolicy); 
			}
		};
		
		assertEquals(asList("1"),elementsOf(provider.get())); 
		
	}
	
	@Test
	public void consumeStopFastcountingPolicy()  {
		
		final FaultHandler customPolicy = new CountingHandler() {
			
			@Override
			protected void handle(Exception failure, Exception lastFailure, int failureCount) {
				if (failureCount>=2)
					iteration.stop();
			}

		};
		
		final List<? extends Object> data = asList(fault1,"1",fault2,"2",fault3,"3");
		
		StreamProvider provider = new StreamProvider() {
			public Stream<?> get() {
				return guard(stringsAndFaults(data)).with(customPolicy); 
			}
		};
		
		assertEquals(asList("1","2"),elementsOf(provider.get())); 
		
	}
}
