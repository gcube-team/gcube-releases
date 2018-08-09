package org.gcube.common.gxrest.response.inbound;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.commons.io.IOUtils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

/**
 * Manipulation of an {@link GXInboundResponse}'s content.
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
       return IOUtils.toByteArray(input);
    }

    /**
     * 
     * @param bytes
     * @return the string
     */
    public static String toString(byte[] bytes) {
        return new String(bytes);
    }
    
    /**
     * Deserializes the specified Json bytes into an object of the specified class
     * @param <T> the type of the desired object
     * @param json the string from which the object is to be deserialized
     * @param classOfT the class of T
     * @return an object of type T from the bytes
     * @throws Exception if the deserialization fails
     */
    public static <T> T fromJson(byte[] bytes, Class<T> raw) throws Exception {
    	try {
	    	Gson gson = new Gson();
	    	return gson.fromJson(toString(bytes),raw);
	    } catch (JsonSyntaxException jse) {
			throw new Exception("Cannot deserialize to the object.");
		}
    }
    
    /**
     * Deserializes the specified Json bytes into an object of the specified class
     * @param <T> the type of the desired object
     * @param json the string from which the object is to be deserialized
     * @param raw the class of T
     * @return an object of type T from the bytes
     * @throws Exception if the deserialization fails
     */
    public static <T> T fromJson(String json, Class<T> raw) throws Exception {
    	try {
	    	Gson gson = new Gson();
	    	return gson.fromJson(json,raw);
    	} catch (JsonSyntaxException jse) {
    		throw new Exception("Cannot deserialize to the object.");
    	}
    }
}
