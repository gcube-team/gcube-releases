package org.gcube.dataanalysis.wps.statisticalmanager.synchserver.capabilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.slf4j.LoggerFactory;

public class GetCapabilitiesChecker {

	private static final char DOT = '.';

	private static final char SLASH = '/';

	private static final String CLASS_SUFFIX = ".class";

	private static final String BAD_PACKAGE_ERROR = "Unable to get resources from path '%s'. Are you sure the package '%s' exists?";

	public static List<Class<?>> getClassesInSamePackageFromJar(String packageName) throws Exception {

		String scannedPath = packageName.replace(".", "/");
		URL scannedUrl = Thread.currentThread().getContextClassLoader().getResource(scannedPath);
		String jarPath = scannedUrl.getFile();

		AnalysisLogger.getLogger().debug("Jar Path complete: " + jarPath);
		jarPath = jarPath.substring(jarPath.indexOf("file:/") + 6, jarPath.lastIndexOf("!"));
		if (jarPath.startsWith("home"))
			jarPath = "/" + jarPath;
		AnalysisLogger.getLogger().debug("Jar Path: " + jarPath);

		JarFile jarFile = null;
		List<Class<?>> result = new ArrayList<Class<?>>();

		String pathTojars = new File(jarPath).getParent();

		File[] jars = new File(pathTojars).listFiles();
		try {

			for (File jar : jars) {
				// File otherjar = new File(new File(jarPath).getParent(),"dataminer-algorithms.jar");

				if (jar.getName().equals("dataminer-algorithms.jar") || jar.getName().endsWith("_interface.jar")) {

					//File otherjar = new File(new File(jarPath).getParent(), "dataminer-algorithms.jar");
					File otherjar = jar;
					if (otherjar.exists())
						jarPath = otherjar.getAbsolutePath();
				
					AnalysisLogger.getLogger().debug("Alternative Jar Path: " + jarPath);

					jarFile = new JarFile(jarPath);
					Enumeration<JarEntry> en = jarFile.entries();

					while (en.hasMoreElements()) {
						JarEntry entry = en.nextElement();
						String entryName = entry.getName();
						packageName = packageName.replace('.', '/');

						if (entryName != null && entryName.endsWith(".class") && entryName.startsWith(packageName)) {
							try {
								Class entryClass = Class.forName(entryName.substring(0, entryName.length() - 6).replace('/', '.'));

								if (entryClass != null) {
									result.add(entryClass);
								}
							} catch (Throwable e) {
								// do nothing, just continue processing classes
							}
						}
					}// while

				}// if jar known
			}
			return result;
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (jarFile != null) {
					jarFile.close();
				}

			} catch (Exception e) {
			}
		}
	}

	public static List<Class<?>> find(String scannedPackage) {
		String scannedPath = scannedPackage.replace(DOT, SLASH);
		URL scannedUrl = Thread.currentThread().getContextClassLoader().getResource(scannedPath);
		if (scannedUrl == null) {
			throw new IllegalArgumentException(String.format(BAD_PACKAGE_ERROR, scannedPath, scannedPackage));
		}
		File scannedDir = new File(scannedUrl.getFile());
		System.out.println("scannedDir:" + scannedDir);
		System.out.println("scannedUrl:" + scannedUrl);
		System.out.println("scannedUrl List:" + scannedDir.listFiles());
		List<Class<?>> classes = new ArrayList<Class<?>>();
		for (File file : scannedDir.listFiles()) {
			classes.addAll(find(file, scannedPackage));
		}
		return classes;
	}

	private static List<Class<?>> find(File file, String scannedPackage) {
		List<Class<?>> classes = new ArrayList<Class<?>>();
		String resource = scannedPackage + DOT + file.getName();
		if (file.isDirectory()) {
			for (File child : file.listFiles()) {
				classes.addAll(find(child, resource));
			}
		} else if (resource.endsWith(CLASS_SUFFIX)) {
			int endIndex = resource.length() - CLASS_SUFFIX.length();
			String className = resource.substring(0, endIndex);
			try {
				if (!(className.contains("IClusterer") || className.contains("IEvaluator") || className.contains("IGenerator") || className.contains("IModeller") || className.contains("ITransducer")))
					classes.add(Class.forName(className));
			} catch (ClassNotFoundException ignore) {
			}
		}
		return classes;
	}

	public static String readPage(URL url) throws Exception {

		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpParams params = httpClient.getParams();
		HttpConnectionParams.setConnectionTimeout(params, 7 * 60000);
		HttpConnectionParams.setSoTimeout(params, 7 * 60000);
		HttpConnectionParams.setStaleCheckingEnabled(params, false);
		HttpConnectionParams.setSoKeepalive(params, false);

		HttpGet request = new HttpGet(url.toURI());
		HttpResponse response = httpClient.execute(request);
		System.out.println("URL executed!");
		Reader reader = null;
		try {
			reader = new InputStreamReader(response.getEntity().getContent());
			System.out.println("Read input stream!");
			StringBuffer sb = new StringBuffer();
			{
				int read;
				char[] cbuf = new char[1024];
				while ((read = reader.read(cbuf)) != -1)
					sb.append(cbuf, 0, read);
			}

			EntityUtils.consume(response.getEntity());
			httpClient.getConnectionManager().shutdown();

			return sb.toString();

		} finally {

			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static Charset getConnectionCharset(URLConnection connection) {
		String contentType = null;
		try {
			contentType = connection.getContentType();
		} catch (Exception e) {
			// specified charset is not found,
			// skip it to return the default one
			return Charset.defaultCharset();
		}
		if (contentType != null && contentType.length() > 0) {
			contentType = contentType.toLowerCase();
			String charsetName = extractCharsetName(contentType);
			if (charsetName != null && charsetName.length() > 0) {
				try {
					return Charset.forName(charsetName);
				} catch (Exception e) {
					// specified charset is not found,
					// skip it to return the default one
				}
			}
		}

		// return the default charset
		return Charset.defaultCharset();
	}

	/**
	 * Extract the charset name form the content type string. Content type string is received from Content-Type header.
	 * 
	 * @param contentType
	 *            the content type string, must be not null.
	 * @return the found charset name or null if not found.
	 */
	private static String extractCharsetName(String contentType) {
		// split onto media types
		final String[] mediaTypes = contentType.split(":");
		if (mediaTypes.length > 0) {
			// use only the first one, and split it on parameters
			final String[] params = mediaTypes[0].split(";");

			// find the charset parameter and return it's value
			for (String each : params) {
				each = each.trim();
				if (each.startsWith("charset=")) {
					// return the charset name
					return each.substring(8).trim();
				}
			}
		}

		return null;
	}

	private static String RUNTIME_RESOURCE_NAME = "ReportsStoreGateway";
	private static String CATEGORY_NAME = "Service";

	public static String readPageNoHttpClient(URL url) throws Exception {
		URLConnection conn = url.openConnection();
		// pretend you're a browser (make my request from Java more browsery-like.)
		conn.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
		conn.setDoOutput(true);
		conn.setAllowUserInteraction(true);
		conn.setConnectTimeout(25 * 60000);
		conn.setReadTimeout(25 * 60000);

		Charset charset = getConnectionCharset(conn);

		BufferedReader dis = new BufferedReader(new InputStreamReader(conn.getInputStream(), charset));
		String inputLine;
		StringBuffer pageBuffer = new StringBuffer();

		// Loop through each line, looking for the closing head element
		while ((inputLine = dis.readLine()) != null) {
			pageBuffer.append(inputLine + "\r\n");
		}

		String page = pageBuffer.toString();
		System.out.println(page);
		conn.getInputStream().close();
		return page;
	}

	public static String readPageHTTPHeader(URL url, String token) throws Exception {
		URLConnection conn = url.openConnection();
		// pretend you're a browser (make my request from Java more browsery-like.)
		conn.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
		conn.setDoOutput(true);
		conn.setAllowUserInteraction(true);
		conn.setConnectTimeout(25 * 60000);
		conn.setReadTimeout(25 * 60000);
		conn.setRequestProperty("gcube-token", token);

		Charset charset = getConnectionCharset(conn);

		BufferedReader dis = new BufferedReader(new InputStreamReader(conn.getInputStream(), charset));
		String inputLine;
		StringBuffer pageBuffer = new StringBuffer();

		// Loop through each line, looking for the closing head element
		while ((inputLine = dis.readLine()) != null) {
			pageBuffer.append(inputLine + "\r\n");
		}

		String page = pageBuffer.toString();
		System.out.println(page);
		conn.getInputStream().close();
		return page;
	}

	// build config.xml
	public static void main(String[] args) throws Exception {
		String packageS = "org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses";
		List<Class<?>> classes = GetCapabilitiesChecker.find(packageS);

		System.out.println(classes + "\n");

		for (Class<?> classfind : classes) {
			System.out.println("<Property name=\"Algorithm\" active=\"true\">" + classfind.getName() + "</Property>");
		}
		// System.exit(0);
		System.out.println("\n");
		System.out.println(classes.size() + " algorithms");

	}

	public static void main1(String[] args) throws Exception {
		String packageS = "org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses";
		List<Class<?>> classes = GetCapabilitiesChecker.find(packageS);

		System.out.println(classes + "\n");

		for (Class<?> classfind : classes) {
			System.out.println("<Property name=\"Algorithm\" active=\"true\">" + classfind.getName() + "</Property>");
		}
		// System.exit(0);
		System.out.println("\n");

		for (Class<?> classfind : classes) {
			System.out.println("http://localhost:8080/wps/WebProcessingService?Request=DescribeProcess&Service=WPS&Version=1.0.0&Identifier=" + classfind.getName() + "\n");
		}

		System.out.println("\n");
		System.out.println("Checking errors in Processes descriptions");
		ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
		root.setLevel(ch.qos.logback.classic.Level.OFF);
		int counter = 0;
		for (Class<?> classfind : classes) {
			String httplink = "http://statistical-manager-new.d4science.org:8080/wps/WebProcessingService?Request=DescribeProcess&Service=WPS&Version=1.0.0&Identifier=" + classfind.getName();
			if (!httplink.contains("IClusterer") && !httplink.contains("IEvaluator") && !httplink.contains("IGenerator") && !httplink.contains("IModeller") && !httplink.contains("ITransducer")) {
				String pageCheck = readPage(new URL(httplink));
				counter++;
				if (pageCheck.contains("ows:ExceptionText") || pageCheck.contains("Exception")) {
					System.out.println("Reading Link: " + httplink);
					System.out.println("ERROR:\n" + pageCheck);

				}
			}
		}

		System.out.println("Checked " + counter + " algorithms");

	}

}
