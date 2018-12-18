package org.gcube.portlets.user.reportgenerator.server.servlet;

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

	private static final Logger logger = LoggerFactory.getLogger(ZipUtil.class);

	/**
	 * @param zipFileName zipFileName
	 * @param dir the dir to compress
	 * @throws IOException .
	 */
	public static void zipDir(String zipFileName, String dir) throws IOException {
		
		File dirObj = new File(dir);
		if(!dirObj.isDirectory())
		{
			System.err.println(dir + " is not a directory");
			System.exit(1);
		}

		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFileName));
		logger.debug("Creating : " + zipFileName);
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
			System.out.println(" Adding: " + files[i].getAbsolutePath());

			String filename = files[i].getName();
			if (filename.endsWith("jpeg") || filename.endsWith("jpg") || filename.endsWith("gif") || filename.endsWith("png"))
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
	
	/**
	 * 
	 * @param archive .
	 * @param outputDir .
	 */
	public static void unzipArchive(File archive, File outputDir) {
        try {
            ZipFile zipfile = new ZipFile(archive);
            for (Enumeration e = zipfile.entries(); e.hasMoreElements(); ) {
                ZipEntry entry = (ZipEntry) e.nextElement();
                unzipEntry(zipfile, entry, outputDir);
            }
        } catch (Exception e) {
        	logger.error("while extracting file " + archive);
            e.printStackTrace();
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

        logger.debug("Extracting: " + entry);
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
    	logger.info("Creating dir "+dir.getName());
        if(!dir.mkdirs()) throw new RuntimeException("Can not create dir "+dir);
    }

} 
