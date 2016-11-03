package org.gcube.smartgears.utils;

import static org.gcube.smartgears.Constants.ghn_home_env;
import static org.gcube.smartgears.Constants.ghn_home_property;
import static org.gcube.smartgears.handlers.application.request.RequestError.application_error;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Flushable;
import java.io.IOException;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.gcube.common.authorization.library.provider.ServiceIdentifier;
import org.gcube.common.authorization.library.provider.ServiceInfo;
import org.gcube.smartgears.context.application.ApplicationContext;
import org.gcube.smartgears.handlers.application.request.RequestError;
import org.gcube.smartgears.handlers.application.request.RequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Library-wide utils.
 * 
 * @author Fabio Simeoni
 * 
 */
public class Utils {

	private static final Logger log = LoggerFactory.getLogger(Utils.class);

	public static RuntimeException unchecked(Throwable t) {

		return (t instanceof RuntimeException) ? RuntimeException.class.cast(t) : new RuntimeException(t.getMessage(),
				t);

	}

	public static String smartgearsVersion() {
		return "1.0.0"; // @TODO
	}

	public static void rethrowUnchecked(Throwable t) throws RuntimeException {

		throw unchecked(t);

	}

	public static void closeSafely(Closeable c) {

		if (c != null) {
			try {

				if (c instanceof Flushable)
					Flushable.class.cast(c).flush();

				c.close();

			} catch (IOException e) {
				log.error("could not close {} due to error {}: msg{}", c, e.getClass().getSimpleName(), e.getMessage());
			}

		}
	}

	public static void valid(String name, Object[] o) throws IllegalArgumentException {
		notNull(name, o);

	}

	public static void notNull(Object[] o) throws IllegalArgumentException {
		notNull("argument", o);
	}

	public static void notNull(String name, Object o) throws IllegalArgumentException {
		if (o == null)
			throw new IllegalArgumentException(name + " is null");
	}

	public static void notEmpty(String name, String o) throws IllegalArgumentException {
		notNull(name, o);
		if (o.isEmpty())
			throw new IllegalArgumentException(name + " is empty");
	}

	public static void notEmpty(String name, Object[] o) throws IllegalArgumentException {
		notNull(name, o);
		if (o.length == 0)
			throw new IllegalArgumentException(name + " is empty");
	}

	public static void notEmpty(String name, Collection<?> o) throws IllegalArgumentException {
		notNull(name, o);
		if (o.isEmpty())
			throw new IllegalArgumentException(name + " is empty");
	}

	public static void valid(String name, String o) throws IllegalArgumentException {
		notNull(name, o);
		notEmpty(name, o);
	}

	public static String home() {

		String home = System.getenv(ghn_home_env);

		if (home == null)
			home = System.getProperty(ghn_home_property);

		return home;

	}

	public static void handleError(HttpServletRequest request, HttpServletResponse response, Throwable t)
			throws IOException {

		RequestError error = t instanceof RequestException ? RequestException.class.cast(t).error() : application_error;

		if (error == application_error) {
			response.getWriter().write("Error (" + error.code() + ") : " + t.getMessage() + "\nStacktrace:\n");
			t.printStackTrace(response.getWriter());
			response.setStatus(error.code());

		} else
			response.sendError(error.code(), t.getMessage());

	}


	public static interface ModeClause {

		File toRead();

		File toWrite();
	}

	public static ModeClause fileAt(final String path) {

		notNull("file path",path);

		return new ModeClause() {

			@Override
			public File toWrite() {
				return file(path,true);
			}

			@Override
			public File toRead() {
				return file(path,false);
			}
		};
	}

	private static File file(String path, boolean writeMode) throws IllegalArgumentException {


		File file = new File(path);

		if (!writeMode)

			if (!file.exists() || file.length() == 0 || !file.canRead()) {
				File backup = new File(file.getAbsolutePath() + ".backup");
				if (backup.exists())
					if (!backup.renameTo(file)) {
						log.warn("accessing directly backup {} as it cannot be renamed to {}", backup.getAbsolutePath(), file.getAbsolutePath());
						return backup; //bets effort:read from backup
					}
					else {
						log.info("cannot read {} but can access its backup {}", backup.getAbsolutePath(), file.getAbsolutePath());
						return file;
					}
			}


		if (file.isDirectory())
			throw new IllegalArgumentException(path + " cannot be used in write mode because it's folder");

		//create folder structure it does not exist
		if (!file.getParentFile().exists())
			file.getParentFile().mkdirs();
		else 
			if (file.exists())

				try (
						BufferedReader reader = new BufferedReader(new FileReader(file));
						BufferedWriter writer = new BufferedWriter(new FileWriter(new File(file.getAbsolutePath() + ".backup")));
						) 
						{
					String line;
					while ((line = reader.readLine()) != null)
						writer.write(line);

						} 
		catch (Exception e) {
			log.warn("cannot back up "+file.getAbsolutePath()+" on writing ", e);
		} 		


		return file;

	}
	
	public static ServiceInfo getServiceInfo(ApplicationContext application){
		String hostedin = String.format("%s_%d", application.container().configuration().hostname(), application.container().configuration().port());
		return 
			new ServiceInfo(new ServiceIdentifier(application.configuration().serviceClass(), application.configuration().name(), hostedin));
	}


}
