package org.gcube.common.gxrest.response.outbound;

import java.util.stream.Stream;

/**
 * Helper methods to find an error code in an enumeration implementing {@link ErrorCode}.
 * 
 * @author Manuele Simi (ISTI CNR)
 *
 */
public class CodeFinder {

	/**
	 * Finds and convert the given code as enum value.
	 * @param code the code to look for.
	 * @param codes the enum values.
	 * @return the code as enum value or null if the code is not found.
	 */
	public static <E extends Enum<E> & ErrorCode> E findAndConvert(ErrorCode code, E[] codes) {
		return Stream.of(codes).filter(e -> e.getId() == code.getId() && e.getMessage().equals(code.getMessage()))
				.findFirst().orElse(null);
	}

}
