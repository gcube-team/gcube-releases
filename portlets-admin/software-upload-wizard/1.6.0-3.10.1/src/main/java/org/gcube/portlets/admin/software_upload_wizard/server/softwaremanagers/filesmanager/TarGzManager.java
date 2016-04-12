package org.gcube.portlets.admin.software_upload_wizard.server.softwaremanagers.filesmanager;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TarGzManager {

	private static final Logger log = LoggerFactory.getLogger(TarGzManager.class);

	public File untarFile(File sourceFile) throws Exception {
		// Create temporary directory
		String randomString = UUID.randomUUID().toString();
		File tmpDir = new File("/tmp/" + randomString);
		if (tmpDir.exists()) {
			if (tmpDir.isDirectory())
				FileUtils.deleteDirectory(tmpDir);
			else
				tmpDir.delete();
		}
		tmpDir.mkdir();
		tmpDir.deleteOnExit();
		log.debug("Created temporary directory: " + tmpDir.getAbsolutePath());

		untarFile(sourceFile, tmpDir);
		return tmpDir;
	}

	public void untarFile(File sourceFile, File destinationDir) throws Exception {
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		GzipCompressorInputStream gis = null;
		TarArchiveInputStream tais = null;

		try {
			fis = new FileInputStream(sourceFile);
			bis = new BufferedInputStream(fis);
			gis = new GzipCompressorInputStream(bis);
			tais = new TarArchiveInputStream(gis);
			TarArchiveEntry entry;
			while ((entry = tais.getNextTarEntry()) != null) {
				log.debug("Processing tar archive entry: " + entry.getName());
				File tmpFile = new File(destinationDir, entry.getName());
				if (entry.isDirectory()) {
					log.trace("Entry " + entry.getName() + " is directory. Creating directory "
							+ tmpFile.getAbsolutePath());
					tmpFile.mkdirs();
					log.debug("Created directory: " + tmpFile.getAbsolutePath());
				} else {
					tmpFile.getParentFile().mkdirs();
					IOUtils.copy(tais, new FileOutputStream(tmpFile));
					log.debug("Extracted file: " + tmpFile.getAbsolutePath());
				}
			}
		} catch (Exception e) {
			throw e;
		} finally {
			if (tais != null)
				tais.close();
			if (gis != null)
				gis.close();
			if (bis != null)
				bis.close();
			if (fis != null)
				fis.close();
		}

	}

	public File tarDirectory(File sourceDirectory) throws Exception {
		File outputFile = File.createTempFile("archive", ".tar.gz");
		outputFile.deleteOnExit();
		tarDirectory(sourceDirectory, outputFile);
		return outputFile;
	}

	public void tarDirectory(File sourceDirectory, File destinationFile) throws Exception {
		if (!sourceDirectory.isDirectory())
			throw new Exception("Source file does not point to a directory.");

		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		GzipCompressorOutputStream gzos = null;
		TarArchiveOutputStream taos = null;

		try {
			fos = new FileOutputStream(destinationFile);
			bos = new BufferedOutputStream(fos);
			gzos = new GzipCompressorOutputStream(bos);
			taos = new TarArchiveOutputStream(gzos);

			for (File tmpFile : sourceDirectory.listFiles()) {
				log.trace("Evaluating entry: " + tmpFile.getPath());
				addFileToTarGz(taos, tmpFile, "");
			}

		} catch (Exception e) {
			throw e;
		} finally {
			if (taos != null)
				taos.close();
			if (gzos != null)
				gzos.close();
			if (bos != null)
				bos.close();
			if (fos != null)
				fos.close();
		}

	}

	/**
	 * Creates a tar entry for the path specified with a name built from the
	 * base passed in and the file/directory name. If the path is a directory, a
	 * recursive call is made such that the full directory is added to the tar.
	 * 
	 * @param tOut
	 *            The tar file's output stream
	 * @param path
	 *            The filesystem path of the file/directory being added
	 * @param base
	 *            The base prefix to for the name of the tar file entry
	 * 
	 * @throws IOException
	 *             If anything goes wrong
	 */
	private void addFileToTarGz(TarArchiveOutputStream tOut, File sourceFile, String base) throws IOException {
		log.trace("Adding file " + sourceFile.getPath());
		String entryName = base + sourceFile.getName();
		TarArchiveEntry tarEntry = new TarArchiveEntry(sourceFile, entryName);

		tOut.setLongFileMode(TarArchiveOutputStream.LONGFILE_GNU);
		tOut.putArchiveEntry(tarEntry);

		if (sourceFile.isFile()) {
			IOUtils.copy(new FileInputStream(sourceFile), tOut);
			tOut.closeArchiveEntry();
		} else {
			tOut.closeArchiveEntry();

			File[] children = sourceFile.listFiles();

			if (children != null) {
				for (File child : children) {
					addFileToTarGz(tOut, child, entryName + "/");
				}
			}
		}
	}

}
