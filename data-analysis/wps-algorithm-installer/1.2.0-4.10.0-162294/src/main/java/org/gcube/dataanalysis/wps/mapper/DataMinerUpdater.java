package org.gcube.dataanalysis.wps.mapper;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import javax.tools.Diagnostic;
import javax.tools.Diagnostic.Kind;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.gcube.common.resources.gcore.GenericResource;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.contentmanagement.lexicalmatcher.utils.FileTools;
import org.gcube.informationsystem.publisher.RegistryPublisher;
import org.gcube.informationsystem.publisher.RegistryPublisherFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

public class DataMinerUpdater {

	// Example of call with modifications for private users
	// ./addAlgorithm.sh NETCDF_SUPPORT_JAVA_13
	// BLACK_BOX-[gianpaolo.coro,giancarlo.panichi]
	// org.gcube.dataanalysis.executor.rscripts.NetCDFSupportJava
	// /gcube/devNext/NextNext transducerers N
	// http://data-d.d4science.org/ZGZSUHlQODRZY3pBQVZxNlVOK0xzUVZKc0dZczBKaEpHbWJQNStIS0N6Yz0
	// "NetCDF Support Java" dev/software/
	public static void main(String args[]) throws Exception {
		String algorithmName = null;
		String implementation = null;
		String category = null;
		String configPath = null;
		String libPath = null;
		String scope = null;
		String description = null;
		String atype = null;
		String skipJava = null;
		String url = null;
		String privateusers = null;

		int i = 0;
		for (String arg : args) {
			if (arg.startsWith("-c")) {
				configPath = arg.substring(2);
			}
			if (arg.startsWith("-i")) {
				implementation = arg.substring(2);
			}
			if (arg.startsWith("-a")) {
				algorithmName = arg.substring(2);
			}
			if (arg.startsWith("-t")) {
				category = arg.substring(2);
				int idxprivusers = category.indexOf("-[");
				if (idxprivusers > 1) {
					privateusers = category.substring(idxprivusers + 2, category.length() - 1);
					System.out.println("Found private Users " + privateusers);
				}
			}
			if (arg.startsWith("-l")) {
				libPath = arg.substring(2);
			}
			if (arg.startsWith("-s")) {
				scope = arg.substring(2);
			}

			if (arg.startsWith("-e")) {
				atype = arg.substring(2);
			}

			if (arg.startsWith("-k")) {
				skipJava = arg.substring(2);
				if (skipJava.equals("Y"))
					skipJava = null;
			}

			if (arg.startsWith("-u")) {
				url = arg.substring(2);
			}

			if (arg.startsWith("-d")) {
				description = arg.substring(2);

				for (int j = i + 1; j < args.length; j++) {
					description = description + " " + args[j];
				}
				System.out.println("DESCRIPTION " + description);
				break;
			}

			i++;
		}

		if (libPath == null || category == null || implementation == null || configPath == null || algorithmName == null || scope == null) {
			System.out.println("Wrong parameters");
			System.out.println("Usage example: ");
			System.exit(0);
		}

		Update(algorithmName, implementation, category, configPath, libPath, scope, description, atype, skipJava, url, privateusers);
	}

	public static void UpdateFiles(String configPath, String atype, String algorithmName, String implementation, String category) throws Exception {
		File transducers = new File(configPath, atype + ".properties");
		File userpersp = new File(configPath, "userperspective.properties");

		// UPDATING TRANDUCERERS FILES
		System.out.println("*******Modifying transducerer file");
		BufferedReader br = new BufferedReader(new FileReader(transducers));
		String entry = algorithmName + "=" + implementation;
		String line = br.readLine();
		boolean found = false;
		boolean endline = false;
		String lastline = "";
		StringBuffer sbb = new StringBuffer();
		while (line != null) {
			lastline = line;
			if (line.trim().equalsIgnoreCase(entry)) {
				found = true;
				break;
			}
			sbb.append(line.trim() + System.lineSeparator());
			line = br.readLine();
		}
		br.close();

		if (!found) {

			FileWriter fw = new FileWriter(transducers, false);
			sbb.append(entry + System.lineSeparator());
			fw.write(sbb.toString());

			fw.close();
			System.out.println("*******Modified transducerer file");
		} else
			System.out.println("*******Transducerer file was not modified, since it already contains the algorithm");

		// UPDATING USER PERSPECTIVE FILE
		System.out.println("*******Modifying user perspective file");
		br = new BufferedReader(new FileReader(userpersp));
		line = br.readLine();
		found = false;
		StringBuffer sb = new StringBuffer();
		boolean foundCategory = false;
		while (line != null) {
			String cat = line.substring(0, line.indexOf("="));
			if (cat.equalsIgnoreCase(category)) {
				foundCategory = true;
				String arguments = line.substring(line.indexOf("=") + 1).trim();
				String argums[] = arguments.split(",");
				List valid = Arrays.asList(argums);

				// if (!line.contains(algorithmName)){
				if (!valid.contains(algorithmName)) {
					if (line.substring(line.indexOf("=") + 1).trim().length() == 0)
						line = line + algorithmName;
					else
						line = line + "," + algorithmName;
				} else
					found = true;
			}
			if (line.trim().length() > 0)
				sb.append(line + System.lineSeparator());
			line = br.readLine();
		}
		br.close();

		if (!foundCategory) {
			sb.append(category + "=" + algorithmName + System.lineSeparator());
			FileWriter fw = new FileWriter(userpersp, false);
			fw.write(sb.toString());
			fw.close();
			System.out.println("*******Modified user perspective file");
		} else {
			if (!found) {

				FileWriter fw = new FileWriter(userpersp, false);
				fw.write(sb.toString());
				fw.close();
				System.out.println("*******Modified user perspective file");
			} else
				System.out.println("*******Perspective file was not modified, since it already contains the algorithm");
		}

		if (atype.equals("models"))
			atype = "modellers";
		if (atype.equals("nodealgorithms"))
			atype = "generators";
		if (atype.equals("algorithms"))
			atype = "generators";

	}

	public static void Update(String algorithmName, String implementation, String category, String configPath, String applicationlibs, String scope, String description, String atype, String skipJava, String url, String privateusers) throws Exception {
		BufferedReader br;
		String line;
		if (atype == null || atype.trim().length() == 0)
			atype = "transducerers";

		System.out.println("*****Parameters");
		System.out.println("*****algorithmName:" + algorithmName);
		System.out.println("*****implementation:" + implementation);
		System.out.println("*****category:" + category);
		System.out.println("*****configPath:" + configPath);
		System.out.println("*****applicationlibs:" + applicationlibs);
		System.out.println("*****scope:" + scope);
		System.out.println("*****description:" + description);
		System.out.println("*****atype:" + atype);
		System.out.println("*****skipJava:" + ((skipJava == null) ? true : false));
		System.out.println("*****url:" + url);

		System.out.println("*******1 - Downloading file");

		if (url != null && url.length() > 1 && skipJava != null) {
			File jarfile = new File(applicationlibs, algorithmName + ".jar");
			System.out.println("*******Downloading to " + jarfile.getAbsolutePath());
			downloadFromUrl(url, jarfile.getAbsolutePath());
			System.out.println("*******Download OK - check " + jarfile.exists());
			System.out.println("*******Updating classpath");
			// load the jar into the classpath
			URLClassLoader sysloader = (URLClassLoader) Thread.currentThread().getContextClassLoader();
			Class sysclass = URLClassLoader.class;
			Class[] parameters = new Class[] { URL.class };

			try {
				Method method = sysclass.getDeclaredMethod("addURL", parameters);
				method.setAccessible(true);
				method.invoke(sysloader, new Object[] { jarfile.toURI().toURL() });
			} catch (Throwable t) {
				t.printStackTrace();
				throw new IOException("Error, could not add URL to system classloader");
			}// end try catch

		} else
			System.out.println("*******1 - Nothing to download");

		System.out.println("*******2 - Updating files");
		UpdateFiles(configPath, atype, algorithmName, implementation, category);
		if (atype.equals("nodealgorithms")) {
			System.out.println("*******2 - Updating also algorithms file");
			UpdateFiles(configPath, "algorithms", algorithmName, implementation, category);
		}
		System.out.println("*******2 - Files updated!");

		// GENERATING CLASS

		if (skipJava != null) {

			System.out.println("*******3 - Generating classes");
			String generationPath = "./org/gcube/dataanalysis/wps/statisticalmanager/synchserver/mappedclasses/";
			ClassGenerator classGenerator = new ClassGenerator(algorithmName, implementation, generationPath, configPath);
			System.out.println("*******3 - Classes generated! " + classGenerator.getJavaFileName());

			// PREPARING JAR FILE
			File dataminerjar = new File(applicationlibs, algorithmName + "_interface.jar");
			dataminerjar.delete();

			// COMPILING JAR
			System.out.println("*******4 - Compiling the Jar");

			compileJava(classGenerator.getJavaFileName());

			System.out.println("*******->creating jar");

			createJar(dataminerjar, classGenerator.getPackageString(), algorithmName);

			// command(classes);
			// command(createjar);
			System.out.println("*******Size " + dataminerjar.length());
			System.out.println("*******4 - Jar compiled!! " + dataminerjar);

		} else {
			System.out.println("*******3 - Generating classes skipped");
			System.out.println("*******4 - Compiling the Jar skipped");

		}

		// INDEX ON THE IS
		System.out.println("*******5 - Indexing on the IS");
		System.out.println("Indexing on IS in scope " + scope);
		indexOnIS(algorithmName, description, scope, privateusers);
		System.out.println("*******5 - Finished indexing on the IS");

		System.out.println("*******All done!");
		System.out.println("*******Table entry:");
		System.out.println("| " + algorithmName + " | " + "system" + " | " + category + "| Prod | <notextile>./addAlgorithm.sh " + algorithmName + " " + category + " " + implementation + " " + scope + " " + atype + " " + (skipJava != null ? "N" : "Y") + " " + ((url != null && url.length() > 1) ? url : "k") + " \"" + description + "\"" + " </notextile> | none |");

	}

	private static void compileJava(String javaFileName) throws Exception {
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
		Iterable<? extends JavaFileObject> compilationUnits1 = fileManager.getJavaFileObjectsFromFiles(Arrays.asList(new File(javaFileName)));

		DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();

		boolean success = compiler.getTask(null, fileManager, diagnostics, null, null, compilationUnits1).call();

		for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics())
			if (diagnostic.getKind() == Kind.ERROR)
				System.out.format("Error on line %d in %s%n", diagnostic.getLineNumber(), diagnostic.getSource().toUri());

		if (!success)
			throw new Exception("error compiling generated class");

	}

	private static void createJar(File dataminerjar, String packageString, String algorithmName) throws Exception {
		Manifest manifest = new Manifest();
		manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
		try (JarOutputStream target = new JarOutputStream(new FileOutputStream(dataminerjar), manifest)) {
			target.putNextEntry(new JarEntry("org/gcube/dataanalysis/wps/statisticalmanager/synchserver/mappedclasses/" + packageString + "/"));
			String source = "org/gcube/dataanalysis/wps/statisticalmanager/synchserver/mappedclasses/" + packageString + "/" + algorithmName + ".class";
			File fileSource = new File(source);
			JarEntry entry = new JarEntry(source);
			target.putNextEntry(entry);
			try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(fileSource));) {
				byte[] buffer = new byte[1024];
				while (true) {
					int count = in.read(buffer);
					if (count == -1)
						break;
					target.write(buffer, 0, count);
				}
			}
			target.closeEntry();
			target.close();
		}
	}

	public static String ExecuteGetLineOld(String cmd) {

		Process process = null;
		String lastline = "";
		StringBuffer sb = new StringBuffer();
		try {
			System.out.println("ExecuteScript-> OSCommand-> Executing Control ->" + cmd);

			process = Runtime.getRuntime().exec(cmd);

			BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = br.readLine();
			System.out.println("ExecuteScript-> OSCommand->  line->" + line);

			while (line != null) {
				try {
					lastline = line;
					System.out.println("ExecuteScript-> OSCommand-> line->" + line);
					line = br.readLine();
					if (line != null)
						sb.append(line + System.lineSeparator());
				} catch (EOFException e) {
					System.out.println("ExecuteScript-> OSCommand -> Process Finished with EOF");
					break;
				} catch (Exception e) {
					line = "ERROR";
					break;
				}
			}

			System.out.println("ExecuteScript-> OSCommand -> Process Finished");

		} catch (Throwable e) {
			System.out.println("ExecuteScript-> OSCommand-> error ");
			e.printStackTrace();
			lastline = "ERROR";
		}
		process.destroy();
		System.out.println("ExecuteScript-> OSCommand-> Process destroyed ");
		return sb.toString();
	}

	public static void indexOnIS(String algorithmName, String algorithmDescription, String scope, String privateusers) throws Exception {
		System.out.println("setting scope to " + scope);

		String secondaryTypePublic = "StatisticalManagerAlgorithm";
		String secondaryTypePrivate = "StatisticalManagerAlgorithmPrivate";
		String secondaryType = secondaryTypePublic;
		if (privateusers != null)
			secondaryType = secondaryTypePrivate;

		InfrastructureDialoguer id = new InfrastructureDialoguer(scope);
		if (privateusers != null) {
			System.out.println("Transforming Algorithm " + algorithmName + " into private algorithm");
			ScopeProvider.instance.set(scope);
			System.out.println("Deleting previous algorithm " + algorithmName + " from private algorithms");
			id.deleteAlgorithmInScope(algorithmName, secondaryTypePrivate);
			System.out.println("Deleting previous algorithm " + algorithmName + " from public algorithms");
			id.deleteAlgorithmInScope(algorithmName, secondaryTypePublic);
		} else {
			ScopeProvider.instance.set(scope);
			List<String> algorithms = id.getAlgorithmsInScope(secondaryType);
			System.out.println("Deleting previous algorithm " + algorithmName + " from private algorithms");
			id.deleteAlgorithmInScope(algorithmName, secondaryTypePrivate);
			boolean found = false;
			for (String alg : algorithms) {
				System.out.println("Algorithm in scope " + alg);
				if (alg.equals(algorithmName)) {
					System.out.println("Found Match! ");
					found = true;
					break;
				}
			}

			if (found) {
				return;
			}
		}

		String xml = FileTools.loadString("algorithmTemplate", "UTF-8");

		xml = xml.replace("#UUID#", UUID.randomUUID().toString());
		xml = xml.replace("#SCOPE#", scope);
		xml = xml.replace("#NAME#", algorithmName);
		xml = xml.replace("#DESCRIPTION#", algorithmDescription);

		// patch to add private users property - GP
		if (privateusers != null) {
			ScopeProvider.instance.set(scope);
			/* encryption using d4science */
			/*
			 * InputStream privateusersstream = new
			 * ByteArrayInputStream(privateusers
			 * .getBytes(StandardCharsets.UTF_8.name())); ByteArrayOutputStream
			 * baos = new ByteArrayOutputStream(); new
			 * EncryptionUtil().encrypt(privateusersstream, baos); String
			 * privateusersencr = new String( baos.toByteArray());
			 */
			String privateuserencr = privateusers;//encrypt(privateusers);

			xml = xml.replace("</inputs>", "</inputs><privateusers>" + privateuserencr + "</privateusers>");
		}

		xml = xml.trim();
		System.out.println("XML:" + xml);

		ScopeProvider.instance.set(scope);
		GenericResource toPublish = new GenericResource();

		Document document = toPublish.newProfile().description(algorithmDescription.replace("\"", "")).name(algorithmName).type(secondaryType).newBody().getOwnerDocument();

		toPublish.profile().newBody(xml);
		Node n = toPublish.profile().body();
		DOMImplementationLS lsImpl = (DOMImplementationLS) n.getOwnerDocument().getImplementation().getFeature("LS", "3.0");
		LSSerializer serializer = lsImpl.createLSSerializer();
		serializer.getDomConfig().setParameter("xml-declaration", false); // by
																			// default
																			// its
																			// true,
																			// so
																			// set
																			// it
																			// to
																			// false
																			// to
																			// get
																			// String
																			// without
																			// xml-declaration
		String str = serializer.writeToString(n);

		System.out.println("STRING:" + str);

		System.out.println(toPublish.profile().body());
		RegistryPublisher rp = RegistryPublisherFactory.create();

		toPublish = rp.create(toPublish);
		System.out.println("PUBLISHED");
	}

	public static String encrypt(String text) {
		return new String(Base64.getEncoder().encode(xor(text.getBytes())));
	}

	public static String decrypt(String hash) {
		try {
			return new String(xor(Base64.getDecoder().decode(hash.getBytes())), "UTF-8");
		} catch (java.io.UnsupportedEncodingException ex) {
			throw new IllegalStateException(ex);
		}
	}

	private static byte[] xor(final byte[] input) {
		final byte[] output = new byte[input.length];
		final byte[] secret = "dminstall".getBytes();
		int spos = 0;
		for (int pos = 0; pos < input.length; ++pos) {
			output[pos] = (byte) (input[pos] ^ secret[spos]);
			spos += 1;
			if (spos >= secret.length) {
				spos = 0;
			}
		}
		return output;
	}

	static void downloadFromUrl(String urlString, String localFilename) throws IOException {
		InputStream is = null;
		FileOutputStream fos = null;
		System.out.println("Downloading :" + urlString);
		URL url = new URL(urlString);
		try {
			URLConnection urlConn = url.openConnection();// connect

			is = urlConn.getInputStream(); // get connection inputstream
			fos = new FileOutputStream(localFilename); // open outputstream to
														// local file

			byte[] buffer = new byte[4096]; // declare 4KB buffer
			int len;

			// while we have availble data, continue downloading and storing to
			// local file
			while ((len = is.read(buffer)) > 0) {
				fos.write(buffer, 0, len);
			}
		} finally {
			try {
				if (is != null) {
					is.close();
				}
			} finally {
				if (fos != null) {
					fos.close();
				}
			}
		}
	}
}
