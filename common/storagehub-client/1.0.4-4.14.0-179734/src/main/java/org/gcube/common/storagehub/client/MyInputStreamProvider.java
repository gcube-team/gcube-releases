package org.gcube.common.storagehub.client;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import org.glassfish.jersey.message.internal.AbstractMessageReaderWriterProvider;
import org.glassfish.jersey.message.internal.ReaderInterceptorExecutor;

@Produces({"application/octet-stream", "*/*"})
@Consumes({"application/octet-stream", "*/*"})
@Singleton
public final class MyInputStreamProvider extends AbstractMessageReaderWriterProvider<InputStream> {




	@Override
	public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return InputStream.class == type;
	}

	@Override
	public InputStream readFrom(
			Class<InputStream> type,
			Type genericType,
			Annotation annotations[],
			MediaType mediaType,
			MultivaluedMap<String, String> httpHeaders,
			InputStream entityStream) throws IOException {
		return ReaderInterceptorExecutor.closeableInputStream(entityStream);
	}

	@Override
	public boolean isWriteable(Class<?> type, Type genericType, Annotation annotations[], MediaType mediaType) {
		return InputStream.class.isAssignableFrom(type);
	}

	@Override
	public long getSize(InputStream t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		if (t instanceof ByteArrayInputStream) {
			return ((ByteArrayInputStream) t).available();
		} else {
			return -1;
		}
	}

	@Override
	public void writeTo(
			InputStream t,
			Class<?> type,
			Type genericType,
			Annotation annotations[],
			MediaType mediaType,
			MultivaluedMap<String, Object> httpHeaders,
			OutputStream entityStream) throws IOException {
		try {
			writeTo(t, entityStream);
		}catch (IOException e) {
			
		} finally {
			t.close();
		}
	}

}