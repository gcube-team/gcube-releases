package org.gcube.data.streams.test;


import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.gcube.data.streams.Stream;
import org.gcube.data.streams.exceptions.StreamSkipSignal;

/**
 * Collection of test facilities to validate {@link Stream} implementation and their usage.
 * 
 * @author Fabio Simeoni
 *
 */
public class Utils {


	/**
	 * Returns the elements of a {@link Stream} as a {@link List}, including failures.
	 * @param stream the stream
	 * @return the elements
	 */
	public static List<Object> elementsOf(Stream<?> stream) {
		
		List<Object> outcomes = new ArrayList<Object>();
		
		//consume
		while (stream.hasNext())
			try {
				Object e = stream.next();
				outcomes.add(e);
			}
			catch(StreamSkipSignal skip) {
				continue;
			}
			catch(RuntimeException ex) {
				outcomes.add(ex);
			}
		
		stream.close();
		
		return outcomes;
	}
	
	/**
	 * Applies a set of sets to test a stream respects the constraints defined by the interface.
	 * @param provider a {@link StreamProvider} that repeatedly provides the stream to test
	 */
	public static void validateWith(StreamProvider provider) {
		
		isAddressableAndClosable(provider.get());
		canBeIteratedOver(provider.get());
		respectsCloseSemantics(provider.get());
				
	}
	
	//a validation test
	private static void isAddressableAndClosable(Stream<?> stream) {
		
		if (stream.locator()==null)
			throw new AssertionError("locator is null");
		
		if (stream.isClosed())
			throw new AssertionError("stream is already closed");
		
		
		stream.close();
		
		if (!stream.isClosed())
			throw new AssertionError("stream has not been closed");
	}
	
	//a validation test
	private static void respectsCloseSemantics(Stream<?> stream) {
		
		if (stream.isClosed())
			throw new AssertionError("stream is already closed");
		
		stream.close();
		
		if (!stream.isClosed())
			throw new AssertionError("stream has been closed but does not reveal it");
		
		if (stream.hasNext())
			throw new AssertionError("stream indicates that it has elements after being closed");
		
		
		try {
			stream.next();
			throw new AssertionError("stream returns elements after being closed");
		}
		catch(NoSuchElementException ex) {
			//expected
		}
		
	}
	
	//a validation test
	private static void canBeIteratedOver(Stream<?> stream) {
		
		//can be iterated without hasNext();
		try {
			if (stream.next()==null)
				throw new AssertionError("next() returns null");
		}
		catch(NoSuchElementException e) {
			if (stream.hasNext())
				throw new AssertionError("stream has no elements but hasNext() returns true");
		}
		catch (RuntimeException e) {
			//ignore exception for this test
		}
		
		//consume
		while (stream.hasNext())
			try {
				stream.next();
			}
			catch(RuntimeException e) {
				//ignore exceptions for this test
			}
		
		//hasNext() is idempotent
		if (stream.hasNext())
			throw new AssertionError("hasNext() is not idempotent");
		
		//verify reading past end
		try {
			stream.next();
			throw new AssertionError("stream can be read past its end");
		}
		catch(NoSuchElementException e) {
			//expected
		}
		
		stream.close();
	}
}
