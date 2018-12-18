package gr.cite.gaap.utilities;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

public class ExceptionUtils
{
	 public static <T> Supplier<T> wrap(Callable<T> callable) {
        return () -> {
            try {
                return callable.call();
            }
            catch (RuntimeException e) {
                throw e;
            }
            catch (Exception e) {
                throw new StreamOperationException(e);
            }
        };
    }
	 
	 public static Supplier<Void> wrap(Runnable runnable) {
       return () -> {
		 try {
            runnable.run();
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw new StreamOperationException(e);
        }
		return null;
       };
    }
}
