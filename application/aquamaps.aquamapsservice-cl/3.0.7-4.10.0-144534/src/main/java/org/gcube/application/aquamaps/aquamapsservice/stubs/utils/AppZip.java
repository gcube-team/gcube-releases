package org.gcube.application.aquamaps.aquamapsservice.stubs.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppZip {
	private static final Logger logger = LoggerFactory.getLogger(AppZip.class);

	//********************** STATIC -Testing

	private static final String OUTPUT_ZIP_FILE = "/home/fabio/desktop.zip";
	private static final String SOURCE_FOLDER = "/home/fabio/Desktop";
	private static final String DEST_FOLDER = "/home/fabio/Desktop_Unzipped";

	public static void main( String[] args ) throws IOException
	{
		//		AppZip appZip = new AppZip(SOURCE_FOLDER);
		//		int zipped=appZip.zipIt(OUTPUT_ZIP_FILE);
		File destination=new File(DEST_FOLDER);
		int unzipped=unzipToDirectory(OUTPUT_ZIP_FILE, destination);
		System.out.println("DONE, zipped = "+0+"; unzipped = "+unzipped);
	}






	//******************** INSTANCE 



	private final List<String> fileList=new ArrayList<String>();

	private final String sourceFolder; 

	public AppZip(String toZipFolder){
		sourceFolder=toZipFolder;
		generateFileList(new File(toZipFolder));
	}


	/**
	 * Zip it
	 * @param zipFile output ZIP file location
	 * @throws IOException 
	 */
	public int zipIt(String zipFile) throws IOException{
		int zippedFiles=0;
		byte[] buffer = new byte[1024];



		FileOutputStream fos = new FileOutputStream(zipFile);
		ZipOutputStream zos = new ZipOutputStream(fos);


		for(String file : this.fileList){

			ZipEntry ze= new ZipEntry(file);
			zos.putNextEntry(ze);

			FileInputStream in = 
				new FileInputStream(sourceFolder + File.separator + file);

			int len;
			while ((len = in.read(buffer)) > 0) {
				zos.write(buffer, 0, len);
			}

			in.close();
			zippedFiles++;
		}

		zos.closeEntry();
		//remember close it
		zos.close();

		return zippedFiles;
	}

	/**
	 * Traverse a directory and get all files,
	 * and add the file into fileList  
	 * @param node file or directory
	 */
	public void generateFileList(File node){

		//add file only
		if(node.isFile()){
			fileList.add(generateZipEntry(node.getAbsoluteFile().toString()));
		}

		if(node.isDirectory()){
			String[] subNote = node.list();
			for(String filename : subNote){
				generateFileList(new File(node, filename));
			}
		}

	}

	/**
	 * Format the file path for zip
	 * @param file file path
	 * @return Formatted file path
	 */
	private String generateZipEntry(String file){
		return file.substring(sourceFolder.length()+1, file.length());
	}



	public static int unzipToDirectory(String zipName,File destinationFolder) throws IOException{
		destinationFolder.mkdirs();
		ZipFile zipFile = new ZipFile(zipName);
		Enumeration enumeration = zipFile.entries();
		int unzippedCount=0;
		while (enumeration.hasMoreElements()) {
			ZipEntry zipEntry = (ZipEntry) enumeration.nextElement();
			if(!zipEntry.isDirectory()){
				String zipAbsoluteName=zipEntry.getName();
				if(zipAbsoluteName.contains(File.separator)){
					File subDir=new File(destinationFolder,zipAbsoluteName.substring(0, zipAbsoluteName.lastIndexOf(File.separator)));
					subDir.mkdirs();
				}


				File toWrite=new File(destinationFolder,zipAbsoluteName);          

				//Checking for needed subdirectories



				BufferedInputStream bis = new BufferedInputStream(zipFile.getInputStream(zipEntry));
				int size;
				byte[] buffer = new byte[2048];

				BufferedOutputStream bos = new BufferedOutputStream(
						new FileOutputStream(toWrite), buffer.length);
				while ((size = bis.read(buffer, 0, buffer.length)) != -1) {
					bos.write(buffer, 0, size);
				}
				bos.flush();
				bos.close();
				bis.close();
				unzippedCount++;
			}
		}
		return unzippedCount;
	}

}