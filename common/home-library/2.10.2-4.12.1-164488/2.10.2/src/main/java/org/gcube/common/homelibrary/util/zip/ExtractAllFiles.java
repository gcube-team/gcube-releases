package org.gcube.common.homelibrary.util.zip;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.io.ZipInputStream;
import net.lingala.zip4j.model.FileHeader;

import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.util.zip.zipmodel.ZipFolder;
import org.gcube.common.homelibrary.util.zip.zipmodel.ZipItem;
import org.gcube.common.homelibrary.util.zip.zipmodel.ZipItemType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExtractAllFiles {


	protected Logger logger = LoggerFactory.getLogger(HomeLibrary.class.getPackage().getName());

	protected Map<String, ZipItem> pathItemMap;
	protected ZipFile zipFile;
	protected File destination;
	private final int BUFF_SIZE = 4096;

	private File zipFileName;

	public ExtractAllFiles(String path) throws InternalErrorException {

		try {

			zipFile = new ZipFile(path);
			zipFileName = zipFile.getFile();
			pathItemMap = new LinkedHashMap<String, ZipItem>();
			
		} catch (ZipException e) {
			throw new InternalErrorException(e);	
		} catch (Exception e) {
			throw new InternalErrorException(e);
		}
	}


	/**
	 * @return
	 * @throws InternalErrorException 
	 */
	public List<ZipItem> getModel() throws IOException, InternalErrorException {

		ZipInputStream is = null;
		OutputStream os = null;
		try {
			// Get a list of FileHeader. FileHeader is the header information
			// for all the files in the ZipFile
			@SuppressWarnings("unchecked")
			List<FileHeader> fileHeaderList = zipFile.getFileHeaders();
			// Loop through all the fileHeaders

			for (int i = 0; i < fileHeaderList.size(); i++) {
				FileHeader fileHeader = fileHeaderList.get(i);


				String zipName = fileHeader.getFileName();
				logger.trace("ZipName: "+zipName);

				String comment = fileHeader.getFileComment();
				logger.trace("Comment: "+comment);

				byte[] extra = fileHeader.getExternalFileAttr();
				logger.trace("Extra: "+extra);

				boolean isDirectory = fileHeader.isDirectory();
				logger.trace("isDirectory: "+isDirectory);

				File f = new File(zipName);
				String name = f.getName();
				logger.trace("Name: "+name);

				String path = f.getPath();
				logger.trace("Path: "+path);

				ZipItem item;

				if (isDirectory) item = new ZipFolder(null, name, comment, extra);
				else {

					File contentFile = File.createTempFile("uploadZip", "tmp");
					contentFile.deleteOnExit();
					FileOutputStream fos = new FileOutputStream(contentFile);
					byte[] buffer = new byte[1024];
					int reads = 0;
					is = zipFile.getInputStream(fileHeader);
					while((reads = is.read(buffer))>=0){
						fos.write(buffer,0,reads);
					}
					fos.close();

					item = new org.gcube.common.homelibrary.util.zip.zipmodel.ZipFile(null, contentFile, name, comment, extra);

				}

				pathItemMap.put(path, item);

				logger.trace("Inserted "+path+" -> "+item+"\n");

			}
			return assignParents();
		} catch (ZipException e) {
			throw new InternalErrorException(e);
		} catch (FileNotFoundException e) {
			throw new InternalErrorException(e);
		} catch (IOException e) {
			throw new InternalErrorException(e);
		} catch (Exception e) {
			throw new InternalErrorException(e);
		}
	}


	protected List<ZipItem> assignParents()
	{
		List<ZipItem> rootsElements = new LinkedList<ZipItem>();

		//we create the paths without a folder
		for (String path:new LinkedList<String>(pathItemMap.keySet())) {
			File f = new File(path);

			createPath(f.getParent());
		}

		for (Map.Entry<String, ZipItem> entry:pathItemMap.entrySet()){

			ZipItem item = entry.getValue();

			logger.trace("Elaborating "+item.getName());
			File f = new File(entry.getKey());
			String parentPath = f.getParent();
			logger.trace("ParentPath: "+parentPath);

			if (parentPath!=null){

				if (pathItemMap.containsKey(parentPath)){
					ZipItem parent = pathItemMap.get(parentPath);

					if (parent.getType()==ZipItemType.FOLDER){
						ZipFolder folderParent = (ZipFolder)parent;
						item.setParent(folderParent);
						folderParent.addChild(item);

						logger.trace("Added "+item.getName()+" to "+folderParent.getName());
					}

				}else{
					logger.error("Parent not in map!!!");
				}

			}else{
				rootsElements.add(item);
			}

			logger.trace("\n");

		}

		return rootsElements;
	}


	protected ZipFolder createPath(String path)
	{

		if (path == null ) return null;

		if (pathItemMap.containsKey(path)){
			ZipItem parent = pathItemMap.get(path);

			if (parent.getType()==ZipItemType.FOLDER){
				return (ZipFolder)parent;
			}else{
				logger.error("The parent is not a folder!!!");
			}
		}



		File f = new File(path);
		String parentPath = f.getParent();

		ZipFolder parent = createPath(parentPath);

		String name = (f.getName().equals(""))?"ZipFolder":f.getName();

		ZipFolder folder = new ZipFolder(parent, name, null, null);
		pathItemMap.put(path, folder);

		return folder;
	}


	/**
	 * 
	 */
	//	private void unzip() {
	//
	//
	//		System.out.println("* " + fileHeader.getFileName());
	//		String outFilePath = destination.getAbsolutePath()  
	//				+ System.getProperty("file.separator")
	//				+ fileHeader.getFileName();
	//		System.out.println("outFilePath " + outFilePath);
	//
	//		//					String filePath = fileHeader.getFileName();
	//		//					System.out.println("filePath " + filePath);
	//
	//		File outFile = new File(outFilePath);
	//		System.out.println("outFile getAbsolutePath " + outFile.getAbsolutePath());
	//		// Checks if the file is a directory
	//		if (fileHeader.isDirectory()) {
	//			System.out.println("is a dir");
	//			// This functionality is up to your requirements
	//			// For now I create the directory
	//			outFile.mkdirs();
	//			continue;
	//		}
	//		// Check if the directories(including parent directories)
	//		// in the output file path exists
	//		File parentDir = outFile.getParentFile();
	//		if (!parentDir.exists()) {
	//			parentDir.mkdirs();
	//		}
	//		// Get the InputStream from the ZipFile
	//		is = zipFile.getInputStream(fileHeader);
	//		// Initialize the output stream
	//
	//		os = new FileOutputStream(outFile);
	//		int readLen = -1;
	//		byte[] buff = new byte[BUFF_SIZE];
	//		// Loop until End of File and write the contents to the
	//		// output stream
	//		while ((readLen = is.read(buff)) != -1) {
	//			os.write(buff, 0, readLen);
	//		}
	//		// Please have a look into this method for some important
	//		// comments
	//		closeFileHandlers(is, os);
	//		// To restore File attributes (ex: last modified file time,
	//		// read only flag, etc) of the extracted file, a utility
	//		// class can be used as shown below
	//		UnzipUtil.applyFileAttributes(fileHeader, outFile);
	//		System.out.println("Done extracting: "
	//				+ fileHeader.getFileName());
	//		System.out.println("\n");
	//
	//	}

	private void closeFileHandlers(ZipInputStream is, OutputStream os)
			throws IOException {
		// Close output stream
		if (os != null) {
			os.close();
			os = null;
		}
		if (is != null) {
			is.close();
			is = null;
		}
	}

}
