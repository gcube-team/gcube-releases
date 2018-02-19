/**
 * 
 */
package org.gcube.documentstore.persistence;

import java.io.Serializable;
import java.lang.reflect.Constructor;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public class GenericCodec<T extends Serializable> implements Codec<T> {
	
	protected final Class<T> tClass;
	
	public GenericCodec(Class<T> tClass){
		this.tClass = tClass;
	}
	
	@Override
    public void encode(BsonWriter writer, T t, EncoderContext ec) {
        writer.writeString(t.toString());
    }

    @Override
    public Class<T> getEncoderClass() {
    	return tClass;
    }

    public T getFromString(String stringRepresentation) throws Exception {
    	@SuppressWarnings("rawtypes")
		Class[] argTypes = { String.class };
        Constructor<T> constructor = getEncoderClass().getDeclaredConstructor(argTypes);
		Object[] arguments = {stringRepresentation};
		return constructor.newInstance(arguments);
    }
    
    @Override
    public T decode(BsonReader reader, DecoderContext dc) {
        String stringRepresentation = reader.readString();
        try {
	        return getFromString(stringRepresentation);
        } catch(Exception e){
        	throw new RuntimeException(e);
        }
    }
}