package org.gcube.common.gxrest.response.inbound;

import org.gcube.common.gxrest.response.outbound.ErrorCode;

/**
 * Deserializer for {@link ErrorCode}.
 * 
 * @author Manuele Simi (ISTI CNR)
 *
 */
final class ErrorCodeDeserializer {

	/**
	 * 
	 */
	private ErrorCodeDeserializer() {}

	/**
	 * The error code, if any
	 * @return the error code or null
	 */
	protected static ErrorCode deserialize(int id, String message) {
		if (id != 1) {
			return new ErrorCode() {
				
				@Override
				public String getMessage() {
					return message;
				}
				
				@Override
				public int getId() {
					return id;
				}
			};
		} else 
			return null;
			
	}
}
