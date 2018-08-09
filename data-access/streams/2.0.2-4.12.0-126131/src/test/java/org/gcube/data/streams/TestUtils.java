package org.gcube.data.streams;

import java.util.List;

import org.gcube.data.streams.dsl.Streams;
import org.gcube.data.streams.exceptions.StreamException;
import org.gcube.data.streams.exceptions.StreamSkipSignal;
import org.gcube.data.streams.exceptions.StreamStopSignal;


public class TestUtils {

	
	static RuntimeException fault1 = new RuntimeException();
	static RuntimeException fault2 = new RuntimeException();
	static RuntimeException fault3 = new RuntimeException();
	static StreamSkipSignal skip = new StreamSkipSignal();
	static StreamStopSignal stop = new StreamStopSignal();
	static StreamException contingency1 = new StreamException(new TestContingency());
	static StreamException contingency2 = new StreamException(new TestContingency());
	static StreamException contingency3 = new StreamException(new TestContingency());
	

	static Stream<String> stringsAndFaults(Object ... elements) {
		return Streams.convertWithFaults(String.class, elements);
	}
	
	static Stream<String> stringsAndFaults(List<? extends Object> elements) {
		return Streams.convertWithFaults(String.class, elements);
	}

	


}
