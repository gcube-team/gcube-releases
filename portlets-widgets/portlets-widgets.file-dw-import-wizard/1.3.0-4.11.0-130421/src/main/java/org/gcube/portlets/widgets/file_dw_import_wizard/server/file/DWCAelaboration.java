package org.gcube.portlets.widgets.file_dw_import_wizard.server.file;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.zip.GZIPOutputStream;

import org.fao.fi.comet.domain.species.tools.converters.dwca.DWCAToReferenceDataCSVConverter;
import org.fao.fi.comet.domain.species.tools.io.FileConstants;
import org.fao.vrmf.core.helpers.singletons.io.EncryptionUtils;

public class DWCAelaboration {
	private String inputPathDWCA;
	private String outputDirectory;
	private String providerID;

	public DWCAelaboration(String inputPathDWCA, String outputDirectory,
			String providerID) {
		this.providerID = providerID;
		this.outputDirectory = outputDirectory;
		this.inputPathDWCA = inputPathDWCA;
	}

	public boolean elaborations() {
		boolean sourceIsFile = false;
		inputPathDWCA = inputPathDWCA.replace("{providerId}", providerID);
		File originalInputFile = new File(inputPathDWCA);

		sourceIsFile = originalInputFile.isFile();
		File tempFolder = new File(System.getProperty("java.io.tmpdir")
				+ File.separator + "DWCA_" + System.currentTimeMillis());
		tempFolder.deleteOnExit();

		tempFolder.mkdir();

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[8192];
		int len = -1;
		try {
			InputStream is = new FileInputStream(inputPathDWCA);

			while ((len = is.read(buffer)) != -1) {
				baos.write(buffer, 0, len);
			}

			is.close();

			baos.flush();
			baos.close();

			byte[] zipped = baos.toByteArray();
			byte[] unzipped;

			File outFile;
			FileOutputStream fos;

			String[] entries = EncryptionUtils
					.listEntriesInZippedStream(zipped);
			

			for (String entry : entries) {
			
				unzipped = EncryptionUtils
						.unzipStreamAndGetEntry(zipped, entry);

				outFile = new File(tempFolder, entry);
				fos = new FileOutputStream(outFile);

				fos.write(unzipped);
				fos.flush();
				fos.close();
			}

			inputPathDWCA = tempFolder.getAbsolutePath();
			File outDir = null;

			if (outputDirectory != null) {
				outDir = new File(outputDirectory);
			} else if (sourceIsFile) {
				outDir = new File(originalInputFile.getParentFile(), "out");
			} else {
				outDir = new File(originalInputFile, "out");
			}

			File outputFolder = new File(outDir.getAbsolutePath().replace(
					"{providerId}", providerID));

			if (!outputFolder.exists())
				outputFolder.mkdirs();

			

			File coreFile = new File(outputFolder, providerID
					+ FileConstants.TAF_TAXA_DATA_FILE_SUFFIX);
			File extensionsFile = new File(outputFolder, providerID
					+ FileConstants.TAF_VERN_DATA_FILE_SUFFIX);

			FileOutputStream outCore = new FileOutputStream(coreFile);
			FileOutputStream outExtensions = new FileOutputStream(
					extensionsFile);

			GZIPOutputStream gzCore = new GZIPOutputStream(outCore);
			GZIPOutputStream gzExtensions = new GZIPOutputStream(outExtensions);

			OutputStreamWriter oswCore = new OutputStreamWriter(gzCore, "UTF-8");
			OutputStreamWriter oswExtensions = new OutputStreamWriter(
					gzExtensions, "UTF-8");

			PrintWriter pwCore = new PrintWriter(oswCore);
			PrintWriter pwExtensions = new PrintWriter(oswExtensions);

			new DWCAToReferenceDataCSVConverter().convertData(providerID,
					inputPathDWCA, pwCore, pwExtensions);

			pwCore.close();
			pwExtensions.close();

			gzCore.flush();
			outCore.flush();

			gzExtensions.flush();
			outExtensions.flush();

			gzCore.close();
			outCore.close();

			gzExtensions.close();
			outExtensions.close();
			
		} catch (Throwable e) {
			e.printStackTrace();
			return false;
		}
		return true;

	}
}
