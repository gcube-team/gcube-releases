package org.gcube.portlets.admin.software_upload_wizard.server.filetypes;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.gcube.portlets.admin.software_upload_wizard.server.softwareprofile.Package;
import org.gcube.portlets.admin.software_upload_wizard.shared.filetypes.FileType;
import org.gcube.portlets.admin.software_upload_wizard.shared.filetypes.PatchArchiveFileType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileValidator {

	private static Logger logger = LoggerFactory.getLogger(FileValidator.class);

	public static FileValidationOutcome validateFile(File fileToCheck,
			Package relatedPackage, String fileTypeName)
			throws FileValidatorException {
		ArrayList<FileType> allowedFileTypes = relatedPackage
				.getAllowedFileTypes();
		for (FileType allowedFileType : allowedFileTypes) {
			if (fileTypeName.equals(allowedFileType.getName())) {

				// Check if number of uploaded files of that type is valid
				if (relatedPackage.getFilesContainer().hasFileWithFileType(
						fileTypeName)
						&& !allowedFileType.allowsMulti()) {
					throw new FileValidatorException(
							"Cannot upload more than one instance of selected file type");
				}

				// Checking
				if (allowedFileType.getName() == PatchArchiveFileType.NAME) {
					logger.trace("Validating Patch Archive File");
					FileInputStream fis;
					BufferedInputStream bis;
					GZIPInputStream gis;
					TarArchiveInputStream tais;

					boolean isApplyFileAvailable = false;
					try {
						fis = new FileInputStream(fileToCheck);
						bis = new BufferedInputStream(fis);
						gis = new GZIPInputStream(bis);
						tais = new TarArchiveInputStream(gis);
						ArchiveEntry entry;
						while ((entry = tais.getNextEntry()) != null) {
							logger.trace("Evaluating archive entry with name: "
									+ entry.getName());
							if (entry.getName().equals("apply.sh")) {
								isApplyFileAvailable = true;
								break;
							}
						}
						tais.close();
						gis.close();
						bis.close();
						fis.close();
					} catch (IOException e) {
						logger.error(e.getClass().getName() + " occurred while validating file " + fileToCheck.getName());
						throw new FileValidatorException("IOException occurred while validating " +PatchArchiveFileType.NAME + ": "+ fileToCheck.getName());
					} finally {
						
					}
					if (isApplyFileAvailable == false)
						return new FileValidationOutcome(false, "Cannot find 'apply.sh' on the root of the " + PatchArchiveFileType.NAME);
				}

			}
		}
		return new FileValidationOutcome(true, "File is valid");
	}
}
