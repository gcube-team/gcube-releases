package org.gcube.portlets.admin.software_upload_wizard.server.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArchiveManager {

	private static final Logger log = LoggerFactory.getLogger(ArchiveManager.class);

	private static final void copyInputStream(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[1024];
		int len;
		while ((len = in.read(buffer)) >= 0)
			out.write(buffer, 0, len);
		in.close();
		out.close();
	}

	private static void extractZipFile(String outputParentDir, String zipFileName) {
		log.debug("Extracting " + zipFileName + " to " + outputParentDir);

		Enumeration<? extends ZipEntry> entries;
		ZipFile zipFile;

		try {
			zipFile = new ZipFile(zipFileName);
			entries = zipFile.entries();
			while (entries.hasMoreElements()) {
				ZipEntry entry = (ZipEntry) entries.nextElement();
				if (entry.isDirectory()) {
					// Assume directories are stored parents first then
					// children.
					// System.out.println("Extracting directory: "
					// + entry.getName());
					// This is not robust, just for demonstration purposes.
					(new File(outputParentDir + entry.getName())).mkdir();
					continue;
				}
				// System.out.println("Extracting file: " + entry.getName());
				copyInputStream(zipFile.getInputStream(entry), new BufferedOutputStream(new FileOutputStream(
						outputParentDir + entry.getName())));
			}
			zipFile.close();
		} catch (IOException ioe) {
			System.err.println("Unhandled exception:");
			ioe.printStackTrace();
			return;
		}
	}

	private static void createJar(File source, File outputFile) throws IOException {
		Manifest manifest = new Manifest();
		manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
		JarOutputStream target = new JarOutputStream(new FileOutputStream(outputFile), manifest);
		add(source.getPath() + "/", source, target);
		target.close();
	}

	private static void add(String rootPath, File source, JarOutputStream target) throws IOException {
		BufferedInputStream in = null;

		try {
			if (source.isDirectory()) {
				for (File nestedFile : source.listFiles())
					add(rootPath, nestedFile, target);
				return;
			}

			String name = source.getPath().replace("\\", "/").replaceFirst(rootPath, "");
			JarEntry entry = new JarEntry(name);
			entry.setTime(source.lastModified());
			target.putNextEntry(entry);
			in = new BufferedInputStream(new FileInputStream(source));

			byte[] buffer = new byte[1024];
			while (true) {
				int count = in.read(buffer);
				if (count == -1)
					break;
				target.write(buffer, 0, count);
			}
			target.closeEntry();
		} finally {
			if (in != null)
				in.close();
		}
	}

	public static File mergeJars(ArrayList<File> inputFiles) throws Exception {
		try {
			File tmpDir = Utils.createTempDirectory();

			// Unzip jars in temp dir
			log.debug("Extracting to directory: " + tmpDir.getAbsolutePath());

			for (File i : inputFiles)
				extractZipFile(tmpDir.getAbsolutePath() + "/", i.getPath());

			// Remove files under META-INF
			log.debug("Deleting META-INF directory");
			File metainfDir = new File(tmpDir.getPath() + "/META-INF");
			FileUtils.deleteDirectory(metainfDir);

			// Create jar with all files in tmp
			log.debug("Creating jar with files under " + tmpDir.getAbsolutePath());
			File archiveFile = File.createTempFile("onejar", ".jar");
			ArchiveManager.createJar(tmpDir, archiveFile);
			log.info("Created jar: " + archiveFile.getPath());

			log.debug("Deleting temporary directory " + tmpDir);
			FileUtils.deleteDirectory(tmpDir);
			return archiveFile;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new Exception("Unable to complete jar-merge: " + e.getMessage());
		}
	}
}
