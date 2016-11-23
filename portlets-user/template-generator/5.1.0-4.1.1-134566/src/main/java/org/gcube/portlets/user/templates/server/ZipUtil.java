package org.gcube.portlets.user.templates.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author massi
 *
 */
public class ZipUtil {

	private static final Logger _log = LoggerFactory.getLogger(ZipUtil.class);

	/**
	 * @param zipFileName zipFileName
	 * @param dir the dir to compress
	 */
	public static void zipDir(String zipFileName, String dir) throws IOException {
		
		File dirObj = new File(dir);
		if(!dirObj.isDirectory())
		{
			_log.error(dir + " is not a directory");
			System.exit(1);
		}

		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFileName));
		_log.debug("Creating : " + zipFileName);
		addDir(dirObj, out);
		// Complete the ZIP file
		out.close();
	}

	private static void addDir(File dirObj, ZipOutputStream out) throws IOException
	{
		File[] files = dirObj.listFiles();
		byte[] tmpBuf = new byte[1024];

		for (int i=0; i<files.length; i++)
		{
			if(files[i].isDirectory())
			{
				addDir(files[i], out);
				continue;
			}

			FileInputStream in = new FileInputStream(files[i].getAbsolutePath());
			_log.trace(" Adding: " + files[i].getAbsolutePath());

			String filename = files[i].getName();
			String ext = filename.substring(filename.length()-4, filename.length());
			if (ext.equalsIgnoreCase(".jpg") || ext.equalsIgnoreCase(".gif") || ext.equalsIgnoreCase(".png") || ext.equalsIgnoreCase("jpeg"))
				filename = "images/" + filename;
			
			out.putNextEntry(new ZipEntry(filename));

			// Transfer from the file to the ZIP file
			int len;
			while((len = in.read(tmpBuf)) > 0)
			{
				out.write(tmpBuf, 0, len);
			}

			// Complete the entry
			out.closeEntry();
			in.close();
		}
	}
	
	
	@SuppressWarnings("unchecked")
	public static void unzipArchive(File archive, File outputDir) {
        try {
            ZipFile zipfile = new ZipFile(archive);
            for (Enumeration e = zipfile.entries(); e.hasMoreElements(); ) {
                ZipEntry entry = (ZipEntry) e.nextElement();
                unzipEntry(zipfile, entry, outputDir);
            }
        } catch (Exception e) {
        	_log.error("Error while extracting file " + archive);
        }
    }

    private static void unzipEntry(ZipFile zipfile, ZipEntry entry, File outputDir) throws IOException {

        if (entry.isDirectory()) {
            createDir(new File(outputDir, entry.getName()));
            return;
        }

        File outputFile = new File(outputDir, entry.getName());
        if (!outputFile.getParentFile().exists()){
            createDir(outputFile.getParentFile());
        }

        _log.trace("Extracting: " + entry);
        BufferedInputStream inputStream = new BufferedInputStream(zipfile.getInputStream(entry));
        BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(outputFile));

        try {
            IOUtils.copy(inputStream, outputStream);
        } finally {
            outputStream.close();
            inputStream.close();
        }
    }

    private static void createDir(File dir) {
    	_log.trace("Creating dir "+dir.getName());
        if(!dir.mkdirs()) throw new RuntimeException("Can not create dir "+dir);
    }

} 
