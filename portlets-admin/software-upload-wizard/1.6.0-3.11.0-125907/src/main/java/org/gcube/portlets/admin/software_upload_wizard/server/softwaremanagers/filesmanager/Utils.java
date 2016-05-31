package org.gcube.portlets.admin.software_upload_wizard.server.softwaremanagers.filesmanager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Utils {
	
	public static File createTempDirectory() throws IOException {
		final File temp;

		temp = File.createTempFile("tempdir", "");

		if (!(temp.delete())) {
			throw new IOException("Could not delete temp file: "
					+ temp.getAbsolutePath());
		}

		if (!(temp.mkdir())) {
			throw new IOException("Could not create temp directory: "
					+ temp.getAbsolutePath());
		}

		return (temp);
	}

	public static File[] getFilesRecursively(File directory) {
		File[] files = directory.listFiles();
		ArrayList<File> outFiles = new ArrayList<File>();

		for (int i = 0; i < files.length; i++) {
			outFiles.add(files[i]);
			if (files[i].isDirectory()) {
				outFiles.addAll(Arrays.asList(getFilesRecursively(files[i])));
			}
		}
		File[] outArray = new File[0];
		return outFiles.toArray(outArray);
	}
}
