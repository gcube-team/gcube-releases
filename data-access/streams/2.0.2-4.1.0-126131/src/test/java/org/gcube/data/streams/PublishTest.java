package org.gcube.data.streams;

import static java.util.Arrays.*;
import static junit.framework.Assert.*;
import static org.gcube.data.streams.TestUtils.*;
import static org.gcube.data.streams.dsl.Streams.*;
import static org.gcube.data.streams.test.Utils.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.gcube.data.streams.generators.Generator;
import org.gcube.data.streams.test.StreamProvider;
import org.junit.BeforeClass;
import org.junit.Test;

public class PublishTest {

	static List<String> testData = Arrays.asList("1","2","3","4","5");
	
	
	@BeforeClass
	public static void setup() {
		
		System.setProperty("org.slf4j.simplelogger.defaultlog", "trace");
	}
	
	@Test
	public void publishAndRead() {
		
		StreamProvider provider = new StreamProvider() {
			public Stream<?> get() {
				Stream<String> stream = convert(testData);
				URI rs = publishStringsIn(stream).withDefaults();
				return convert(rs).ofStrings().withDefaults();
			}
		};
		
		validateWith(provider);
		
		assertEquals(testData,elementsOf(provider.get()));
	}
	
	@Test
	public void publishOnDemand() throws Exception {
		
		final List<Boolean> consumed = new ArrayList<Boolean>();
		
		final Generator<String,String> consumer = new Generator<String, String>() {
			@Override
			public String yield(String element) {
				consumed.add(true);
				return element;
			}
		};

		Stream<String> stream = convert(testData);
		stream = pipe(stream).through(consumer);
		
		URI rs = publishStringsIn(stream).withBufferOf(2).withDefaults();

		Thread.sleep(100);
		
		int generated = consumed.size();
		assertTrue("only some elements have been moved",generated<testData.size());
		
		stream = convert(rs).ofStrings().withDefaults();
		

		
		assertTrue("no more elements have been moved",generated == consumed.size());
		
		//pull some
		while (stream.hasNext())
			stream.next();

		assertTrue("some more elements have moved",generated <consumed.size());
		
	}
	
	@Test
	public void publishContinuously() throws Exception {
		
		final List<Boolean> consumed = new ArrayList<Boolean>();
		
		final Generator<String,String> consumer = new Generator<String, String>() {
			@Override
			public String yield(String element) {
				consumed.add(true);
				return element;
			}
		};

		Stream<String> stream = convert(testData);
		stream = pipe(stream).through(consumer);
		
		publishStringsIn(stream).withBufferOf(2).withTimeoutOf(100,TimeUnit.MILLISECONDS).nonstop().withDefaults();

		//wait for longer than timeout to trigger continuous publication
		Thread.sleep(200);
		
		assertEquals("all elements have been consumed",testData.size(),consumed.size());
		
	}
	
	@Test
	public void publishWithConfiguration() throws Exception {
		
		List<String> elements = asList("1","2");
		
		Stream<String> stream = convert(elements);
		
		URI resultset = publishStringsIn(stream).nonstop().withBufferOf(10).withTimeoutOf(1,TimeUnit.HOURS).withDefaults();
		
		Stream<String> published = convert(resultset).ofStrings().withDefaults();
				
		int i=0;
		while (published.hasNext()) {
			assertEquals(elements.get(i),published.next());
			i++;
		}
		
		assertEquals(2,i);
	}
	
	@Test
	public void contingenciesArePublished() {
		
		List<? extends Object> data = asList(contingency1,"1",contingency2,"2","3",contingency3);
		
		Stream<String> stream = stringsAndFaults(data);
		
		URI rs = publishStringsIn(stream).withDefaults();
		
		Stream<String> published = convert(rs).ofStrings().withDefaults();
		
		List<Object> elements = elementsOf(published);
		
		System.out.println(elements);
		
		for (int i = 0; i<data.size();i++){
			System.out.println(elements.get(i));
			if (elements.get(i) instanceof Exception) {
				Exception read = (Exception) elements.get(i);
				assertTrue(data.get(i) instanceof Exception);
				Exception original = (Exception) data.get(i);
				//causes are preserved
				assertEquals(original.getCause().getClass(),read.getCause().getClass());
			}
			else
				assertEquals(data.get(i),elements.get(i));
		}

	}
	
	@Test
	public void firstOutageIsPublished() {
		
		List<? extends Object> segmentUntilOutage = asList("1",fault1);
		List<Object> data = new ArrayList<Object>(segmentUntilOutage);
		data.add("2");
		
		Stream<String> stream = stringsAndFaults(data);
		
		URI rs = publishStringsIn(stream).withDefaults();
		
		Stream<String> published = convert(rs).ofStrings().withDefaults();
		
		List<Object> elements = elementsOf(published);
		
		//resultset stops at first outage
		assertEquals(elements.size(),segmentUntilOutage.size());
		
		for (int i = 0; i<elements.size();i++)
			if (elements.get(i) instanceof Exception)
				assertTrue(data.get(i) instanceof Exception);
			else
				assertEquals(data.get(i),elements.get(i));
	}
}
