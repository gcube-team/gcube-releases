/**
 * 
 */
package org.gcube.data.tml;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.gcube.data.streams.generators.Generator;
import org.gcube.data.tml.proxies.Path;


/**
 * 
 * Library-wide utilities.
 * 
 * @author Fabio Simeoni
 * 
 */
public class Utils {

	/**
	 * Checks that the input is not <code>null</code>
	 * 
	 * @param name the name of the value to report in error messages
	 * @param value the value
	 * @throws IllegalArgumentException if the value is <code>null</code>
	 */
	public static void notNull(String name, Object value) throws IllegalArgumentException {
		if (value == null)
			throw new IllegalArgumentException(name + " is null");
	}
	

	/**
	 * A {@link Generator} that serialises {@link Path}s into streams.
	 * 
	 * @author Fabio Simeoni
	 *
	 */
	public static class PathSerialiser implements Generator<Path,String> {
		
		private static Marshaller marshaller;
		
		static {
			try {
				marshaller=JAXBContext.newInstance(Path.class).createMarshaller();
			}
			catch(Exception e) {
				throw new RuntimeException(e);
			}
		}
		
		public String yield(Path path) {
			
			try {
				
				StringWriter w = new StringWriter();
				
				marshaller.marshal(path,w);
			
				return w.toString();
			}
			catch (Exception e) {//we take this serialisation failure to be unrecoverable
				throw new RuntimeException(e);
			}
		}
	};
}
