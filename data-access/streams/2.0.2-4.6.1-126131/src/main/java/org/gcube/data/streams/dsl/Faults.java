package org.gcube.data.streams.dsl;

/**
 * A simple DSL for fault conversion.
 * 
 * @author Fabio Simeoni
 * 
 */
public class Faults {

	/**
	 * Fault narrowing clause;
	 * 
	 * @author Fabio Simeoni
	 * 
	 */
	public static class OngoingRethrowClause {

		final RuntimeException caught;
		final Throwable cause;

		/**
		 * Creates an instance with the fault to narrow.
		 * 
		 * @param fault the fault
		 */
		OngoingRethrowClause(RuntimeException fault) {
			this.caught=fault;
			this.cause = fault.getCause()==null?fault:fault.getCause();
		}

		/**
		 * Rethrows the fault with a narrower type, or wraps it in a {@link RuntimeException} if its type cannot be
		 * narrowed.
		 * 
		 * @param clazz1 the narrower type
		 * @return unused, allows clients to throw invocations of this method
		 * @throws T1 the narrower type
		 */
		public <T1 extends Throwable> RuntimeException as(Class<T1> clazz1) throws T1 {

			if (clazz1.isInstance(cause))
				throw clazz1.cast(cause);

			else

				if (cause instanceof RuntimeException)
					throw (RuntimeException) cause;
			
				else
					return caught;
		}

		/**
		 * Rethrows the fault with a narrower type, or wraps it in {@link RuntimeException} if its type cannot be
		 * narrowed.
		 * 
		 * @param clazz1 the narrower type
		 * @param clazz2 an alternative narrower type
		 * @return unused, allows clients to throw invocations of this method
		 * @throws T1 the narrower type
		 * @throws T2 the second narrower type
		 */
		public <T1 extends Throwable, T2 extends Throwable> RuntimeException as(Class<T1> clazz1, Class<T2> clazz2)
				throws T1, T2 {

			if (clazz2.isInstance(cause))
				throw clazz2.cast(cause);

			else
				return as(clazz1);
		}

		/**
		 * Rethrows the fault with a narrower type, or wraps it in {@link RuntimeException} if its type cannot be
		 * narrowed.
		 * 
		 * @param clazz1 the narrower type
		 * @param clazz2 an alternative narrower type
		 * @param clazz3 an alternative narrower type
		 * @return unused, allows clients to throw invocations of this method
		 * @throws T1 the narrower type
		 * @throws T2 the second narrower type
		 * @throws T3 the second narrower type
		 */
		public <T1 extends Throwable, T2 extends Throwable, T3 extends Throwable> RuntimeException as(Class<T1> clazz1,
				Class<T2> clazz2, Class<T3> clazz3) throws T1, T2, T3 {

			if (clazz3.isInstance(cause))
				throw clazz3.cast(cause);

			else
				return as(clazz1, clazz2);
		}

		/**
		 * Rethrows the fault with a narrower type, or wraps it in {@link RuntimeException} if its type cannot be
		 * narrowed.
		 * 
		 * @param clazz1 the narrower type
		 * @param clazz2 an alternative narrower type
		 * @param clazz3 an alternative narrower type
		 * @param clazz4 an alternative narrower type
		 * @return unused, allows clients to throw invocations of this method
		 * @throws T1 the narrower type
		 * @throws T2 the second narrower type
		 * @throws T3 the second narrower type
		 * @throws T4 the second narrower type
		 */
		public <T1 extends Throwable, T2 extends Throwable, T3 extends Throwable, T4 extends Throwable> RuntimeException as(
				Class<T1> clazz1, Class<T2> clazz2, Class<T3> clazz3, Class<T4> clazz4) throws T1, T2, T3, T4 {

			if (clazz4.isInstance(cause))
				throw clazz4.cast(cause);

			else
				return as(clazz1, clazz2, clazz3);
		}
	}

	/**
	 * Indicates a fault to be rethrown with a narrower type.
	 * 
	 * @param fault the fault
	 * @return the next clause in the sentence
	 */
	public static OngoingRethrowClause causeOf(RuntimeException fault) {

		return new OngoingRethrowClause(fault);

	}
}
