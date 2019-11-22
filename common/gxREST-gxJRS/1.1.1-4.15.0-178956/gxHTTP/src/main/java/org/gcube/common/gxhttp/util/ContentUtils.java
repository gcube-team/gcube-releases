package org.gcube.common.gxhttp.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

/**
 * Manipulation of a response's content.
 * 
 * @author Manuele Simi (ISTI CNR)
 *
 */
final public class ContentUtils {

	/**
	 * Converts an object to an array of bytes
	 * @param obj
	 * @return the bytes
	 * @throws IOException
	 */
    public static byte[] toByteArray(Object obj) throws IOException {
        byte[] bytes = null;
        ByteArrayOutputStream bos = null;
        ObjectOutputStream oos = null;
        try {
            bos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            oos.flush();
            bytes = bos.toByteArray();
        } finally {
            if (oos != null) {
                oos.close();
            }
            if (bos != null) {
                bos.close();
            }
        }
        return bytes;
    }

    /**
     * 
     * @param inputStream
     * @param class1
     * @return an instance of type "type"
     * @throws IOException
     * @throws ClassNotFoundException
     */
    @SuppressWarnings("unchecked")
	public static  <T> T toObject(InputStream inputStream, Class<T> class1) throws IOException, ClassNotFoundException {
    	ObjectInput in = null;
    	T o = null;
    	try {
    	  in = new ObjectInputStream(inputStream);
    	  o = (T) in.readObject(); 
    	} finally {
    	  try {
    	    if (in != null) {
    	      in.close();
    	    }
    	  } catch (IOException ex) {
    	    // ignore close exception
    	  }
    	}
        return o;
    }
    
    /**
     * Converts the array of bytes into an object.
     * @param data
     * @return the object
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public Object toObject(byte[] data) throws IOException, ClassNotFoundException {
	    ByteArrayInputStream in = new ByteArrayInputStream(data);
	    ObjectInputStream is = new ObjectInputStream(in);
	    return is.readObject();
	}
	
    
    /**
     * Gets the contents of an <code>InputStream</code> as a <code>byte[]</code>.
     *
     * @param input the <code>InputStream</code> to read from
     * @return the requested byte array
     * @throws NullPointerException if the input is null
     * @throws IOException          if an I/O error occurs
     */
    public static byte[] toByteArray(final InputStream input) throws IOException {
       return _toByteArray(input);
    }
    
    /**
     * Gets the contents of an <code>InputStream</code> as a <code>byte[]</code>.
     * <p>
     * This method buffers the input internally, so there is no need to use a
     * <code>BufferedInputStream</code>.
     *
     * @param input the <code>InputStream</code> to read from
     * @return the requested byte array
     * @throws NullPointerException if the input is null
     * @throws IOException          if an I/O error occurs
     */
    private static byte[] _toByteArray(final InputStream input) throws IOException {
        try (final ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            copyStream(input, output);
            return output.toByteArray();
        }
    }
    
    private static int copyStream(final InputStream input, final OutputStream output) throws IOException {
    	final byte[] buffer = new byte[1024 * 4];
        long count = 0;
        int n;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        if (count > Integer.MAX_VALUE) {
            return -1;
        }
        return (int) count;
    }
    
    /**
     * 
     * @param bytes
     * @return the string
     */
    public static String toString(byte[] bytes) {
        return new String(bytes);
    }
}
