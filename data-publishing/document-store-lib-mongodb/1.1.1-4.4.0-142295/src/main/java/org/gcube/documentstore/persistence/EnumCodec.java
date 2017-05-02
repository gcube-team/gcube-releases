/**
 * 
 */
package org.gcube.documentstore.persistence;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public class EnumCodec<E extends Enum<E>> implements Codec<E> {
	
	protected final Class<E> eClass;
	
	public EnumCodec(Class<E> eClass){
		this.eClass = eClass;
	}
	
	@Override
    public void encode(BsonWriter writer, E e, EncoderContext ec) {
        writer.writeString(e.name());
    }

    @Override
    public Class<E> getEncoderClass() {
    	return this.eClass;
    }

    @Override
    public E decode(BsonReader reader, DecoderContext dc) {
        String enumString = reader.readString();
        return (E) Enum.valueOf(getEncoderClass(), enumString);
    }
}