/**
 * 
 */
package org.cotrix.gcube.stubs;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

/**
 * @author "Federico De Faveri federico.defaveri@fao.org"
 *
 */
public class CopyUtils {	
	private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;
	
	/**
	 * Copy the input reader into the output writer.
	 * 
	 * @param input
	 *            the input reader.
	 * @param output
	 *            the output writer.
	 * @return the number of char copied.
	 * @throws IOException
	 *             if an error occurs during the copy.
	 */
	public static long copy(Reader input, Writer output) throws IOException {
		char[] buffer = new char[DEFAULT_BUFFER_SIZE];
		long count = 0;
		int n = 0;
		while (-1 != (n = input.read(buffer))) {
			output.write(buffer, 0, n);
			count += n;
		}
		return count;
	}
	
    public static String toString(InputStream input) throws IOException {
        StringWriter sw = new StringWriter();
        Reader reader = new InputStreamReader(input);
        copy(reader, sw);
        reader.close();
        return sw.toString();
    }
}
