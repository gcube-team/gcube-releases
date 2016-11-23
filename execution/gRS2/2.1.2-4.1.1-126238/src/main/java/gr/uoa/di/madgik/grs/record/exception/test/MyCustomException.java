package gr.uoa.di.madgik.grs.record.exception.test;

public class MyCustomException extends Throwable {

	private static final long serialVersionUID = 8683542572315875098L;

	/**
	 * Create a new instance
	 */
	public MyCustomException() {
		super();
	}

	/**
	 * Create a new instance
	 * 
	 * @param message
	 *            the error message
	 */
	public MyCustomException(String message) {
		super(message);
	}

	/**
	 * Create a new instance
	 * 
	 * @param message
	 *            the error message
	 * @param cause
	 *            the cause of the error
	 */
	public MyCustomException(String message, Throwable cause) {
		super(message, cause);
	}

	public MyCustomException(Throwable cause) {
		super(cause);
	}

	// //////////
	public static class InnerException extends Throwable {
		private static final long serialVersionUID = 8683542572315875098L;

		/**
		 * Create a new instance
		 */
		public InnerException() {
			super();
		}

		/**
		 * Create a new instance
		 * 
		 * @param message
		 *            the error message
		 */
		public InnerException(String message) {
			super(message);
		}

		/**
		 * Create a new instance
		 * 
		 * @param message
		 *            the error message
		 * @param cause
		 *            the cause of the error
		 */
		public InnerException(String message, Throwable cause) {
			super(message, cause);
		}

		public InnerException(Throwable cause) {
			super(cause);
		}
	}

}
