package org.gcube.documentstore.records;

import java.io.Serializable;

import com.fasterxml.jackson.databind.deser.std.StringDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class IdentifiableDeserializableModule extends SimpleModule {
    /**
	 * 
	 */
	private static final long serialVersionUID = -6210999408282132552L;

	public IdentifiableDeserializableModule() {
        addDeserializer(Serializable.class, new StringDeserializer());
    }
}