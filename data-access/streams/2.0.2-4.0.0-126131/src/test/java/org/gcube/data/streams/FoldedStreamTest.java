package org.gcube.data.streams;

import static java.util.Arrays.*;
import static junit.framework.Assert.*;
import static org.gcube.data.streams.TestUtils.*;
import static org.gcube.data.streams.dsl.Streams.*;
import static org.gcube.data.streams.test.Utils.*;

import java.util.List;

import org.gcube.data.streams.test.StreamProvider;
import org.junit.Test;

public class FoldedStreamTest {

	static int foldStep = 2;

	@Test
	public void foldedStreamsAreValidStreams() throws Exception {

		StreamProvider provider = new StreamProvider() {
			public Stream<?> get() {
				return fold(convert("1", "2", "3")).in(foldStep);
			}
		};
		
		validateWith(provider);

		@SuppressWarnings("unchecked")
		List<? extends Object> folded = asList(asList("1","2"),asList("3"));

		assertEquals(folded, elementsOf(provider.get()));

	}

	@Test
	public void foldingHandlesFailures() throws Exception {

		StreamProvider provider = new StreamProvider() {
			public Stream<?> get() {
				return fold(stringsAndFaults(fault1,"1",fault2,"2","3",fault3)).in(foldStep);
			}
		};
		
		validateWith(provider);

		List<? extends Object> folded = asList(fault1,fault2,asList("2", "3"),fault3);

		assertEquals(folded, elementsOf(provider.get()));

	}
	

}
