package org.gcube.portlets.admin.software_upload_wizard.server.softwaremanagers.filesmanager;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.UUID;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.gcube.portlets.admin.software_upload_wizard.server.data.SoftwareFile;
import org.gcube.portlets.admin.software_upload_wizard.server.softwareprofile.Package;
import org.gcube.portlets.admin.software_upload_wizard.server.softwareprofile.ServiceProfile;
import org.gcube.portlets.admin.software_upload_wizard.shared.Deliverable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.allen_sauer.gwt.log.client.Log;
import com.google.inject.Inject;

public class DefaultFileManager implements FileManager {

	private static final Logger log = LoggerFactory.getLogger(DefaultFileManager.class);

	@Inject
	TarGzManager tarGzManager;

	private File createTempTextFile(String content, String fileName) throws Exception {

		try {
			File outFile = File.createTempFile("import", fileName);
			outFile.deleteOnExit();
			FileWriter fileWriter = new FileWriter(outFile);
			PrintWriter out = new PrintWriter(fileWriter);
			out.print(content);
			out.close();
			return outFile;
		} catch (Exception ex) {
			Log.error("Unable to create " + fileName + " file:\n", ex);
			throw ex;
		}
	}

	private boolean deleteFileOrDir(File file) {
		if (file.isDirectory()) {
			String[] children = file.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteFileOrDir(new File(file, children[i]));
				if (!success) {
					return false;
				}
			}
		}

		// The directory is now empty so delete it
		return file.delete();
	}

	/**
	 * Creates a tar.gz file at the specified path with the contents of the
	 * specified directory.
	 * 
	 * @param dirPath
	 *            The path to the directory to create an archive of
	 * @param archivePath
	 *            The path to the archive to create
	 * 
	 * @throws IOException
	 *             If anything goes wrong
	 */
	private static void createTarGzOfDirectory(String directoryPath, String tarGzPath) throws IOException {
		FileOutputStream fOut = null;
		BufferedOutputStream bOut = null;
		GzipCompressorOutputStream gzOut = null;
		TarArchiveOutputStream tOut = null;

		try {
			fOut = new FileOutputStream(new File(tarGzPath));
			bOut = new BufferedOutputStream(fOut);
			gzOut = new GzipCompressorOutputStream(bOut);
			tOut = new TarArchiveOutputStream(gzOut);

			File[] children = new File(directoryPath).listFiles();
			if (children != null) {
				for (File child : children) {
					addFileToTarGz(tOut, child.getAbsolutePath(), "");
				}
			}

		} finally {
			tOut.finish();
			tOut.close();
			gzOut.close();
			bOut.close();
			fOut.close();
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
	private static void addFileToTarGz(TarArchiveOutputStream tOut, String path, String base) throws IOException {
		File f = new File(path);
		String entryName = base + f.getName();
		TarArchiveEntry tarEntry = new TarArchiveEntry(f, entryName);

		tOut.setLongFileMode(TarArchiveOutputStream.LONGFILE_GNU);
		tOut.putArchiveEntry(tarEntry);

		if (f.isFile()) {
			IOUtils.copy(new FileInputStream(f), tOut);

			tOut.closeArchiveEntry();
		} else {
			tOut.closeArchiveEntry();

			File[] children = f.listFiles();

			if (children != null) {
				for (File child : children) {
					addFileToTarGz(tOut, child.getAbsolutePath(), entryName + "/");
				}
			}
		}
	}

	@Override
	public File createServiveArchive(String serviceProfile, List<Deliverable> miscFiles, ServiceProfile profile)
			throws Exception {

		// Prepare temporary directory structure
		String randomString = UUID.randomUUID().toString();
		File tmpDir = new File("/tmp/" + randomString);
		if (tmpDir.exists())
			deleteFileOrDir(tmpDir);
		tmpDir.mkdir();
		tmpDir.deleteOnExit();
		String tmpDirPath = tmpDir.getAbsolutePath();
		log.debug("tmpdir: " + tmpDirPath);

		// Create and copy root files
		for (Deliverable m : miscFiles) {
			FileUtils.copyFile(createTempTextFile(m.getContent(), m.getName()),
					new File(tmpDirPath + "/" + m.getName()));
		}
		FileUtils.copyFile(createTempTextFile(serviceProfile, "profile.xml"), new File(tmpDirPath + "/profile.xml"));

		// Copy main package files into package directory
		for (Package p : profile.getService().getPackages()) {

			String packageName = p.getData().getName();
			String packageDirPath = tmpDirPath + "/" + packageName;
			File packageDirFile = new File(packageDirPath);
			packageDirFile.mkdir();

			for (SoftwareFile sf : p.getFilesContainer().getFiles()) {
				FileUtils.copyFile(sf.getFile(), new File(packageDirPath + "/" + sf.getFilename()));
			}
		}

		// Create tarball
		String tarGzPath = "/tmp/" + profile.getService().getData().getName() + ".tar.gz";
		createTarGzOfDirectory(tmpDirPath, tarGzPath);
		log.debug("Tarball created with path: " + tarGzPath);
		deleteFileOrDir(tmpDir);
		return new File(tarGzPath);
	}

	@Override
	public File createPomFile(String pomContent) throws Exception {
		return createTempTextFile(pomContent, "pom");
	}

	@Override
	public File createPatchArchive(String serviceProfile, List<Deliverable> miscFiles, File patchArchive)
			throws Exception {

		File tmpDir = tarGzManager.untarFile(patchArchive);

		// Create and copy root files
		File tmpMiscFile = null;
		for (Deliverable m : miscFiles) {
			tmpMiscFile = createTempTextFile(m.getContent(), m.getName());
			FileUtils.copyFile(tmpMiscFile, new File(tmpDir, m.getName()));
			try {
				FileUtils.forceDelete(tmpMiscFile);
			} catch (Exception e) {
				log.warn("Unable to delete file." + e);
			}
		}
		tmpMiscFile = createTempTextFile(serviceProfile, "profile.xml");
		FileUtils.copyFile(tmpMiscFile, new File(tmpDir, "profile.xml"));
		try {
			FileUtils.forceDelete(tmpMiscFile);
		} catch (Exception e) {
			log.warn("Unable to delete file." + e);
		}
		// Create tar.gz archive
		File outputArchive = tarGzManager.tarDirectory(tmpDir);

		log.debug("Tarball created with path: " + outputArchive.getPath());
		FileUtils.deleteDirectory(tmpDir);
		return outputArchive;
	}

}
