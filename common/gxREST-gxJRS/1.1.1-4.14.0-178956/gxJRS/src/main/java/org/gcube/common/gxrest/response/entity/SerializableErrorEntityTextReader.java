package org.gcube.common.gxrest.response.entity;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NoContentException;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Objects;

/**
 * Text reader for {@link SerializableErrorEntity}
 * @author Manuele Simi (ISTI-CNR)
 */
@Provider
@Consumes(MediaType.TEXT_PLAIN)
public class SerializableErrorEntityTextReader implements MessageBodyReader<SerializableErrorEntity> {

    /**
     * Ascertain if the MessageBodyReader can produce an instance of a
     * particular type. The {@code type} parameter gives the
     * class of the instance that should be produced, the {@code genericType} parameter
     * gives the {@link Type java.lang.reflect.Type} of the instance
     * that should be produced.
     * E.g. if the instance to be produced is {@code List<String>}, the {@code type} parameter
     * will be {@code java.util.List} and the {@code genericType} parameter will be
     * {@link ParameterizedType java.lang.reflect.ParameterizedType}.
     *
     * @param type        the class of instance to be produced.
     * @param genericType the type of instance to be produced. E.g. if the
     *                    message body is to be converted into a method parameter, this will be
     *                    the formal type of the method parameter as returned by
     *                    {@code Method.getGenericParameterTypes}.
     * @param annotations an array of the annotations on the declaration of the
     *                    artifact that will be initialized with the produced instance. E.g. if the
     *                    message body is to be converted into a method parameter, this will be
     *                    the annotations on that parameter returned by
     *                    {@code Method.getParameterAnnotations}.
     * @param mediaType   the media type of the HTTP entity, if one is not
     *                    specified in the request then {@code application/octet-stream} is
     *                    used.
     * @return {@code true} if the type is supported, otherwise {@code false}.
     */
    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return type == SerializableErrorEntity.class;
    }

    /**
     * Read a type from the {@link InputStream}.
     * <p>
     * In case the entity input stream is empty, the reader is expected to either return a
     * Java representation of a zero-length entity or throw a {@link NoContentException}
     * in case no zero-length entity representation is defined for the supported Java type.
     * A {@code NoContentException}, if thrown by a message body reader while reading a server
     * request entity, is automatically translated by JAX-RS server runtime into a {@link BadRequestException}
     * wrapping the original {@code NoContentException} and rethrown for a standard processing by
     * the registered {@link ExceptionMapper exception mappers}.
     * </p>
     *
     * @param type         the type that is to be read from the entity stream.
     * @param genericType  the type of instance to be produced. E.g. if the
     *                     message body is to be converted into a method parameter, this will be
     *                     the formal type of the method parameter as returned by
     *                     {@code Method.getGenericParameterTypes}.
     * @param annotations  an array of the annotations on the declaration of the
     *                     artifact that will be initialized with the produced instance. E.g.
     *                     if the message body is to be converted into a method parameter, this
     *                     will be the annotations on that parameter returned by
     *                     {@code Method.getParameterAnnotations}.
     * @param mediaType    the media type of the HTTP entity.
     * @param httpHeaders  the read-only HTTP headers associated with HTTP entity.
     * @param entityStream the {@link InputStream} of the HTTP entity. The
     *                     caller is responsible for ensuring that the input stream ends when the
     *                     entity has been consumed. The implementation should not close the input
     *                     stream.
     * @return the type that was read from the stream. In case the entity input stream is empty, the reader
     * is expected to either return an instance representing a zero-length entity or throw
     * a {@link NoContentException} in case no zero-length entity representation is
     * defined for the supported Java type.
     * @throws IOException             if an IO error arises. In case the entity input stream is empty
     *                                 and the reader is not able to produce a Java representation for
     *                                 a zero-length entity, {@code NoContentException} is expected to
     *                                 be thrown.
     * @throws WebApplicationException if a specific HTTP error response needs to be produced.
     *                                 Only effective if thrown prior to the response being committed.
     */
    @Override
    public SerializableErrorEntity readFrom(Class<SerializableErrorEntity> type, Type genericType,
                                            Annotation[] annotations, MediaType mediaType, MultivaluedMap<String,
                                            String> httpHeaders, InputStream entityStream)
                            throws IOException, WebApplicationException {
        String stringEntity = convertStreamToString(entityStream);
        String[] tokens = stringEntity.split(Character.toString(SerializableErrorEntity.ENTITY_CHAR_SEPARATOR));
        if (Objects.isNull(tokens) || tokens.length != 3)
            throw new IOException("Unable to decode SerializableErrorEntity from the response.");
        SerializableErrorEntity entity = new SerializableErrorEntity();
        entity.setExceptionClass(tokens[0]);
        entity.setEncodedTrace(tokens[1]);
        entity.setMessage(tokens[2]);
        return entity;
    }

    /**
     * Reads and converts the content of the input stream.
     * @param is the input stream to read from
     * @return the string read from the stream
     */
    private static String convertStreamToString(InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}
