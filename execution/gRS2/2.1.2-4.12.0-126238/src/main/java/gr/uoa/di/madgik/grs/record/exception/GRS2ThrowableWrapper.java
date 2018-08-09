package gr.uoa.di.madgik.grs.record.exception;

import java.io.Serializable;
import java.lang.reflect.Field;

/**
 * 
 * @author alex
 *
 */
public class GRS2ThrowableWrapper implements Serializable {
	private static final long serialVersionUID = -3042686055658047285L;

	private String className;
	private Throwable cause;
	private GRS2ThrowableWrapper causeWrap = this;

	/**
	 * Converts a throwable in {@link GRS2Exception.class}
	 * 
	 * @param th
	 * @return
	 * @throws GRS2ThrowableWrapperException
	 */
/*	public static Throwable convertToGRS2Exception(Throwable th) throws GRS2ThrowableWrapperException {
		return convertToException(GRS2Exception.class, th);
	}
*/	
	public static Throwable convertToGenaralException(Throwable th) throws GRS2ThrowableWrapperException {
		return convertToException(Throwable.class, th);
	}
	
	
	/**
	 * Converts a throwable in a specific class type <em>exClass<em>. 
	 * If exClass is not subClass of Throwable an exception will be thrown
	 * 
	 * @param exClass
	 * @param th
	 * @return
	 * @throws GRS2ThrowableWrapperException
	 */
	public static Throwable convertToException(Class<?> exClass, Throwable th) throws GRS2ThrowableWrapperException {
		Throwable grs2Ex = null;
		try {
			grs2Ex = (Throwable) exClass.newInstance();
			grs2Ex.setStackTrace(th.getStackTrace());
	
			Class<?> cl = Throwable.class;
			Field msgField = getField(cl, "detailMessage");
			msgField.setAccessible(true);
			String msg = (String) msgField.get(th);
			msgField.set(grs2Ex, msg);
			
			
			Field csField = getField(cl, "cause");
			csField.setAccessible(true);
			Throwable cause = (Throwable) csField.get(th);
			csField.set(grs2Ex, cause);
		} catch (Exception e) {
			throw new GRS2ThrowableWrapperException("error converting exception to "
					+ exClass.getCanonicalName(), e);
		}

		return grs2Ex;
	}

	/**
	 * Creates a GRS2ThrowableWrapper from a Throwable
	 * 
	 * @param th
	 * @return
	 * @throws GRS2ThrowableWrapperException
	 */
	public static GRS2ThrowableWrapper createFromThrowable(Throwable th) throws GRS2ThrowableWrapperException {
		GRS2ThrowableWrapper tw = new GRS2ThrowableWrapper();

		tw.className = th.getClass().getName();
//		System.out.println("createFromThrowable: classname = " + tw.className);
//		System.out.println("createFromThrowable: msg = " + th.getMessage());

		/* widen the throwable to a known exception class */
		Throwable grs2Ex = null;
		try {
			grs2Ex = (Throwable) convertToGenaralException(th);
		} catch (Exception e) {
			throw new GRS2ThrowableWrapperException("Could not instantiate " + Throwable.class + " object", e);
		}
		tw.cause = grs2Ex;

		/* special handling to the cause */
		Class<?> cl = Throwable.class;
		try {
			Field csField = getField(cl, "cause");
			csField.setAccessible(true);

			Throwable cause = (Throwable) csField.get(th);
			if (!cause.equals(th))
				tw.causeWrap = createFromThrowable(cause);
			else
				tw.causeWrap = null;
		} catch (Exception e) {
			throw new GRS2ThrowableWrapperException("error creating throwable wrapper from throwable", e);
		}

		return tw;
	}

	/**
	 * Creates a Throwable from a GRS2ThrowableWrapper. <em>genericException<em> needs to be given in order
	 * to specify the most generic of each Exception if the real class of the Exception is not available.
	 * 
	 * @param tw
	 * @param genericException
	 * @return
	 * @throws GRS2ThrowableWrapperException
	 */
	public static Throwable createFromGRS2ThrowableWrapper(GRS2ThrowableWrapper tw, Class<?> genericException)
			throws GRS2ThrowableWrapperException {
		Class<?> cl = null;
		Throwable grs2Exception = null;

		try {
			//System.out.println("Trying to create : " + tw.className);
			cl = Class.forName(tw.className);
			
			grs2Exception = (Throwable) cl.newInstance();
		} catch (Exception e) {
			//System.out.println("Trying to create : " + tw.className + " failed"); 
			//e.printStackTrace();
			cl = genericException;
			try {
				grs2Exception = (Throwable) cl.newInstance();
			} catch (Exception ex) {
				throw new GRS2ThrowableWrapperException("Could not instantiate " + cl + " object", ex);
			}
		}

//		System.out.println("createFromGRS2ThrowableWrapper: classname = " + tw.className + " actual classname = " + cl.getCanonicalName() );

		grs2Exception.setStackTrace(tw.cause.getStackTrace());

		try {
			Field fdm = getField(cl, "detailMessage");
			fdm.setAccessible(true);
			fdm.set(grs2Exception, fdm.get(tw.cause));

			Field fcause = getField(cl, "cause");
			fcause.setAccessible(true);
			Throwable cause = (Throwable) fcause.get(tw.cause);

			if (cause != null && !cause.equals(tw.cause)) {
				if (tw.causeWrap != null) {
					cause = createFromGRS2ThrowableWrapper(tw.causeWrap, genericException);
					fcause.set(grs2Exception, cause);
				}
			} else
				fcause.set(grs2Exception, cause);
		} catch (Exception e) {
			throw new GRS2ThrowableWrapperException("error creating throwable from wrapper", e);
		}
		return grs2Exception;
	}

	/**
	 * Gets a Field of a class. Is used in order to access all the fields of an object (even private fields of the superClass)
	 * 
	 * @param clazz
	 * @param fieldName
	 * @return
	 * @throws NoSuchFieldException
	 */
	private static Field getField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
		try {
			return clazz.getDeclaredField(fieldName);
		} catch (NoSuchFieldException e) {
			Class<?> superClass = clazz.getSuperclass();
			if (superClass == null) {
				throw e;
			} else {
				return getField(superClass, fieldName);
			}
		}
	}
}