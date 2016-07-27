/**
 * 
 */
package org.gcube.common.homelibrary.util.zip;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;
import org.gcube.common.homelibrary.util.zip.zipmodel.ZipFile;
import org.gcube.common.homelibrary.util.zip.zipmodel.ZipFolder;
import org.gcube.common.homelibrary.util.zip.zipmodel.ZipItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public class ZipModelWriter {

	protected Logger logger = LoggerFactory.getLogger(ZipModelWriter.class);

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

		if (folder.getChildren().size() == 0) {
			ZipEntry zipEntry = null;
			if (skipRoot){
				String sub = folder.getPath().substring(folder.getPath().indexOf('/') + 1);
				int start = sub.indexOf('/') + 1;
				logger.trace("adding ZipFile path: "+ folder.getPath().substring(start));
				zipEntry = new ZipEntry(folder.getPath().substring(start) + "/");
			}else{
				logger.trace("adding ZipFile path: "+ folder.getPath());
				zipEntry = new ZipEntry(folder.getPath() + "/");
			}
			zos.putNextEntry(zipEntry);
			zos.closeEntry();
		}
		for (ZipItem item:folder.getChildren()) addZipItem(zos, item, skipRoot); 
	}

	protected void addZipFile(ZipOutputStream zos, ZipFile file, boolean flag) throws IOException
	{
	
		ZipEntry zipEntry = null;
		if (flag){
			String sub = file.getPath().substring(file.getPath().indexOf('/') + 1);
			int start = sub.indexOf('/') + 1;
			logger.trace("adding ZipFile path: "+ file.getPath().substring(start));
			zipEntry = new ZipEntry(file.getPath().substring(start));
		}else{
			logger.trace("adding ZipFile path: "+file.getPath());
			zipEntry = new ZipEntry(file.getPath());
		}
		zipEntry.setComment(file.getComment());
		zipEntry.setExtra(file.getExtra());

		zos.putNextEntry(zipEntry);

		InputStream stream = file.getContentStream();
		IOUtils.copy(stream, zos);
		zos.closeEntry();
		stream.close();
	}

}
