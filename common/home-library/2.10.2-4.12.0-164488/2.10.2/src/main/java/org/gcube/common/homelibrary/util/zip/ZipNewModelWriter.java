/**
 * 
 */
package org.gcube.common.homelibrary.util.zip;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;
import org.gcube.common.homelibrary.home.workspace.folder.items.ExternalFile;
import org.gcube.common.homelibrary.util.zip.zipmodel.ZipFile;
import org.gcube.common.homelibrary.util.zip.zipmodel.ZipFolder;
import org.gcube.common.homelibrary.util.zip.zipmodel.ZipItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public class ZipNewModelWriter {

	protected Logger logger = LoggerFactory.getLogger(ZipNewModelWriter.class);

	public File writeItem(ZipItem item, boolean skipRoot) throws IOException
	{
		File zipFile = File.createTempFile("zippping", "gz");
		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile));
		addZipItem(out, item, skipRoot);
		out.close();
		return zipFile;
	}

	protected void addZipItem(ZipOutputStream zos, ZipItem item, boolean skipRoot) throws IOException
	{

		switch (item.getType()) {

		case FILE: addZipFile(zos, (ZipFile) item, skipRoot); break;
		case FOLDER: addZipFolder(zos, (ZipFolder) item, skipRoot); break;
		}
	}

	protected void addZipFolder(ZipOutputStream zos, ZipFolder folder, boolean skipRoot) throws IOException
	{

//		System.out.println(folder.getPath());	
		ZipEntry zipEntry = new ZipEntry(folder.getPath() + File.separator);
		zos.putNextEntry(zipEntry);
		zos.closeEntry();
		for (ZipItem item:folder.getChildren()) addZipItem(zos, item, skipRoot); 

	}

	protected void addZipFile(ZipOutputStream zos, ZipFile file, boolean skipRoot) throws IOException
	{

//		System.out.println(file.getPath());

		ZipEntry zipEntry = new ZipEntry(file.getPath());
		zos.putNextEntry(zipEntry);
		try{
			ExternalFile externalFile = (ExternalFile)file;
			try (InputStream inputStream = externalFile.getData()) {
				IOUtils.copy(inputStream, zos);
			}	
		} catch (Exception e) {
			logger.error(file.getName() + " will not be compressed.");
		}

		zos.closeEntry();
	}


}
