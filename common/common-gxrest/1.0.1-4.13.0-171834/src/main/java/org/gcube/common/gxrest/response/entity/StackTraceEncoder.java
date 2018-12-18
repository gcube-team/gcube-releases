package org.gcube.common.gxrest.response.entity;

import java.util.StringJoiner;

/**
 * Encoder for {@link StackTraceElement}.
 * 
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public class StackTraceEncoder {

	private final static String FIELD_SEPARATOR = "~~";

	private final static String ELEMENT_SEPARATOR = "~!~";

	private StackTraceEncoder() {
	}

	/**
	 * Encodes the stacktrace element as string.
	 * 
	 * @param element
	 * @return the encoded element
	 */
	public static String encodeElement(StackTraceElement element) {
		return element.getClassName() + FIELD_SEPARATOR + element.getMethodName() + FIELD_SEPARATOR
				+ element.getFileName() + FIELD_SEPARATOR + element.getLineNumber();

	}

	/**
	 * Decodes the string as stacktrace element.
	 * 
	 * @param encoded
	 * @return the decoded element
	 */
	public static StackTraceElement decodeElement(String encoded) {
		String[] elements = encoded.split(FIELD_SEPARATOR, 4);
		return new StackTraceElement(elements[0], elements[1], elements[2], Integer.valueOf(elements[3]));
	}

	public static String encodeTrace(StackTraceElement[] elements, int lines) {
		StringJoiner joiner = new StringJoiner(ELEMENT_SEPARATOR);
		for (int i = 0; i < lines; i++)
			joiner.add(encodeElement(elements[i]));
		return joiner.toString();
	}

	public static StackTraceElement[] decodeTrace(String joinedTrace) {
		String[] encodedElements = joinedTrace.split(ELEMENT_SEPARATOR);
		StackTraceElement[] elements = new StackTraceElement[encodedElements.length];
		for (int i = 0; i < encodedElements.length; i++)
			elements[i] = StackTraceEncoder.decodeElement(encodedElements[i]);
		return elements;
	}
}
