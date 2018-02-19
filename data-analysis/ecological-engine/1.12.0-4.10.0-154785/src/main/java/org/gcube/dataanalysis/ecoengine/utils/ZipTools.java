package org.gcube.dataanalysis.ecoengine.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;

public class ZipTools {

		public static void unZip(String zipFile, String outputFolder) throws Exception {

		byte[] buffer = new byte[1024];

		try {

			// create output directory is not exists
			File folder = new File(outputFolder);
			if (!folder.exists()) {
				folder.mkdir();
			}

			// get the zip file content
			ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
			// get the zipped file list entry
			ZipEntry ze = zis.getNextEntry();

			while (ze != null) {

				String fileName = ze.getName();
				File newFile = new File(outputFolder + File.separator + fileName);

				AnalysisLogger.getLogger().debug("Unzipping : " + newFile.getAbsoluteFile());

				if (ze.isDirectory())
					new File(outputFolder,ze.getName()).mkdir();
				
				else{
				new File(newFile.getParent()).mkdirs();
				FileOutputStream fos = new FileOutputStream(newFile);

				int len;
				while ((len = zis.read(buffer)) > 0) {
					fos.write(buffer, 0, len);
				}

				fos.close();
				}
				ze = zis.getNextEntry();
			}

			zis.closeEntry();
			zis.close();

			AnalysisLogger.getLogger().debug("Unzip done");

		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	
	  static public void zipFolder(String srcFolder, String destZipFile) throws Exception {
		    ZipOutputStream zip = null;
		    FileOutputStream fileWriter = null;

		    fileWriter = new FileOutputStream(destZipFile);
		    zip = new ZipOutputStream(fileWriter);

		    addFolderToZip("", srcFolder, zip);
		    zip.flush();
		    zip.close();
		  }

		  static private void addFileToZip(String path, String srcFile, ZipOutputStream zip)
		      throws Exception {

		    File folder = new File(srcFile);
		    if (folder.isDirectory()) {
		      addFolderToZip(path, srcFile, zip);
		    } else {
		      byte[] buf = new byte[1024];
		      int len;
		      FileInputStream in = new FileInputStream(srcFile);
		      zip.putNextEntry(new ZipEntry(path + "/" + folder.getName()));
		      while ((len = in.read(buf)) > 0) {
		        zip.write(buf, 0, len);
		      }
		    }
		  }

		  static private void addFolderToZip(String path, String srcFolder, ZipOutputStream zip)
		      throws Exception {
		    File folder = new File(srcFolder);

		    for (String fileName : folder.list()) {
		      if (path.equals("")) {
		        addFileToZip(folder.getName(), srcFolder + "/" + fileName, zip);
		      } else {
		        addFileToZip(path + "/" + folder.getName(), srcFolder + "/" + fileName, zip);
		      }
		    }
		  }
	
}
