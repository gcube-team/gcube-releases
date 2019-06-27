/**
 * 
 */
package org.gcube.informationsystem.model.impl.utils.discovery;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Got from
 * http://stackoverflow.com/questions/520328/can-you-find-all-classes-in-a-package-using-reflection#answer-22462785
 *
 * The method first gets the current ClassLoader. It then fetches all resources
 * that contain said package and iterates of these URLs. It then creates a 
 * URLConnection and determines what type of URL we have. It can either be a 
 * directory (FileURLConnection) or a directory inside a jar or zip file 
 * (JarURLConnection). Depending on what type of connection we have two 
 * different methods will be called.
 * 
 * First lets see what happens if it is a FileURLConnection.
 * 
 * It first checks if the passed File exists and is a directory. If that's the 
 * case it checks if it is a class file. If so a Class object will be created 
 * and put in the List. If it is not a class file but is a directory, we 
 * simply iterate into it and do the same thing. All other cases/files will be 
 * ignored.
 * 
 * If the URLConnection is a JarURLConnection the other private helper method 
 * will be called. This method iterates over all Entries in the zip/jar 
 * archive. If one entry is a class file and is inside of the package a Class 
 * object will be created and stored in the ArrayList.
 * 
 * After all resources have been parsed it (the main method) returns the 
 * ArrayList containing all classes in the given package, that the current 
 * ClassLoader knows about.
 * 
 * If the process fails at any point a ClassNotFoundException will be thrown 
 * containing detailed information about the exact cause.
 *
 */
public class ReflectionUtility {

	/**
	 * Private helper method
	 * 
	 * @param directory
	 *            The directory to start with
	 * @param pckgname
	 *            The package name to search for. Will be needed for getting the
	 *            Class object.
	 * @param classes
	 *            if a file isn't loaded but still is in the directory
	 * @throws ClassNotFoundException
	 */
	private static void checkDirectory(File directory, String pckgname,
	        List<Class<?>> classes) throws ClassNotFoundException {
	    File tmpDirectory;

	    if (directory.exists() && directory.isDirectory()) {
	        final String[] files = directory.list();

	        if(files!=null){
		        for (final String file : files) {
		            if (file.endsWith(".class")) {
		                try {
		                    classes.add(Class.forName(pckgname + '.'
		                            + file.substring(0, file.length() - 6)));
		                } catch (final NoClassDefFoundError e) {
		                    // do nothing. this class hasn't been found by the
		                    // loader, and we don't care.
		                }
		            } else if ((tmpDirectory = new File(directory, file))
		                    .isDirectory()) {
		                checkDirectory(tmpDirectory, pckgname + "." + file, classes);
		            }
		        }
	        }
	    }
	}

	/**
	 * Private helper method.
	 * 
	 * @param connection
	 *            the connection to the jar
	 * @param pckgname
	 *            the package name to search for
	 * @param classes
	 *            the current ArrayList of all classes. This method will simply
	 *            add new classes.
	 * @throws ClassNotFoundException
	 *             if a file isn't loaded but still is in the jar file
	 * @throws IOException
	 *             if it can't correctly read from the jar file.
	 */
	private static void checkJarFile(JarURLConnection connection,
	        String pckgname, List<Class<?>> classes)
	        throws ClassNotFoundException, IOException {
	    final JarFile jarFile = connection.getJarFile();
	    final Enumeration<JarEntry> entries = jarFile.entries();
	    String name;

	    for (JarEntry jarEntry = null; entries.hasMoreElements()
	            && ((jarEntry = entries.nextElement()) != null);) {
	        name = jarEntry.getName();

	        if (name.contains(".class")) {
	            name = name.substring(0, name.length() - 6).replace('/', '.');

	            if (name.contains(pckgname)) {
	                classes.add(Class.forName(name));
	            }
	        }
	    }
	}

	public static List<Class<?>> getClassesForPackage(Package packageObject)
	        throws ClassNotFoundException {
		return getClassesForPackage(packageObject.getName());
	}
	
	/**
	 * Attempts to list all the classes in the specified package as determined
	 * by the context class loader
	 * 
	 * @param pckgname
	 *            the package name to search
	 * @return a list of classes that exist within that package
	 * @throws ClassNotFoundException
	 *             if something went wrong
	 */
	@SuppressWarnings("restriction")
	public static List<Class<?>> getClassesForPackage(String pckgname)
	        throws ClassNotFoundException {
	    final List<Class<?>> classes = new ArrayList<Class<?>>();

	    try {
	        final ClassLoader cld = Thread.currentThread()
	                .getContextClassLoader();

	        if (cld == null)
	            throw new ClassNotFoundException("Can't get class loader.");

	        final Enumeration<URL> resources = cld.getResources(pckgname
	                .replace('.', '/'));
	        URLConnection connection;

	        for (URL url = null; resources.hasMoreElements()
	                && ((url = resources.nextElement()) != null);) {
	            try {
	                connection = url.openConnection();

	                if (connection instanceof JarURLConnection) {
	                    checkJarFile((JarURLConnection) connection, pckgname,
	                            classes);
	                } else if (connection instanceof sun.net.www.protocol.file.FileURLConnection) {
	                    try {
	                        checkDirectory(
	                                new File(URLDecoder.decode(url.getPath(),
	                                        "UTF-8")), pckgname, classes);
	                    } catch (final UnsupportedEncodingException ex) {
	                        throw new ClassNotFoundException(
	                                pckgname
	                                        + " does not appear to be a valid package (Unsupported encoding)",
	                                ex);
	                    }
	                } else
	                    throw new ClassNotFoundException(pckgname + " ("
	                            + url.getPath()
	                            + ") does not appear to be a valid package");
	            } catch (final IOException ioex) {
	                throw new ClassNotFoundException(
	                        "IOException was thrown when trying to get all resources for "
	                                + pckgname, ioex);
	            }
	        }
	    } catch (final NullPointerException ex) {
	        throw new ClassNotFoundException(
	                pckgname
	                        + " does not appear to be a valid package (Null pointer exception)",
	                ex);
	    } catch (final IOException ioex) {
	        throw new ClassNotFoundException(
	                "IOException was thrown when trying to get all resources for "
	                        + pckgname, ioex);
	    }

	    return classes;
	}
	
}
