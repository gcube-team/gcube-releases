package gr.cite.geoanalytics.environmental.data.retriever.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ResourceUtils {

	public static InputStream getResource(String resourceName) throws IOException {
		return ResourceUtils.class.getClassLoader().getResourceAsStream(resourceName);
	}

	public static Map<String, String> getResourcesNames(String folder, String suffix) throws Exception {
		File jarFile = new File(ResourceUtils.class.getProtectionDomain().getCodeSource().getLocation().getPath());
		return jarFile.isFile() ? getResourceNamesFromJAR(jarFile, folder, suffix) : getResourceNamesFromIDE(folder, suffix);
	}

	public static Map<String, String> getResourceNamesFromIDE(String folder, String suffix) throws IOException {
		Map<String, String> results = new HashMap<>();

		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		URL url = loader.getResource(folder);
		String path = url.getPath();

		for (File file : new File(path).listFiles()) {
			int indexOfSuffix = file.getName().lastIndexOf(suffix);
			String date = file.getName().substring(0, indexOfSuffix);

			results.put(date, folder + file.getName());
		}

		return results;
	}

	public static Map<String, String> getResourceNamesFromJAR(File jarFile, String folder, String suffix) throws IOException {
		Map<String, String> results = new HashMap<>();

		JarFile jar = new JarFile(jarFile);
		Enumeration<JarEntry> entries = jar.entries();

		while (entries.hasMoreElements()) {
			JarEntry entry = entries.nextElement();

			if (entry.getName().contains(folder) && entry.getName().contains(suffix)) {
				int indexOfFolder = entry.getName().lastIndexOf(folder) + folder.length();
				int indexOfSuffix = entry.getName().lastIndexOf(suffix);
				String date = entry.getName().substring(indexOfFolder, indexOfSuffix);

				results.put(date, entry.getName());
			}
		}

		jar.close();

		return results;
	}
}
