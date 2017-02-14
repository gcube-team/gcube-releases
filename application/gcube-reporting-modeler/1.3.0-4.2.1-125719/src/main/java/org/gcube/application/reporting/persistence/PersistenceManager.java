package org.gcube.application.reporting.persistence;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.gcube.portlets.d4sreporting.common.shared.BasicComponent;
import org.gcube.portlets.d4sreporting.common.shared.BasicSection;
import org.gcube.portlets.d4sreporting.common.shared.ComponentType;
import org.gcube.portlets.d4sreporting.common.shared.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author massi
 *
 */
public class PersistenceManager {
	private static final Logger _log = LoggerFactory.getLogger(PersistenceManager.class);

	/**
	 * 
	 * @param model the model to persist on disk
	 * @return true if ok
	 */
	public static boolean writeModel(Model model) {
		File tempDir;
		try {
			tempDir = File.createTempFile(UUID.randomUUID().toString(),"");
			tempDir.delete();
			tempDir.mkdir();
			File temp = new File(tempDir+File.separator+model.getTemplateName()+".d4st");
			return writeModel(model, temp);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	/**
	 * 
	 * @param model the model to persist on disk
	 * @param file the file where you want the model to be persisted
	 * @return true if ok
	 */
	public static boolean writeModel(Model model, File modelInstanceFile) {

		//reopen the saved templates/report in first page
		model.setCurrPage(1);
		//create a temp file

		//persists the report
		FileOutputStream fos = null;
		ObjectOutputStream out = null;
		try {
			fos = new FileOutputStream(modelInstanceFile);
			out = new ObjectOutputStream(fos);
			out.writeObject(model);
			out.close();

			_log.trace("Trying to zip folder: " + modelInstanceFile.getParent());	
			String folderToZip = modelInstanceFile.getParent();
			String outZip = modelInstanceFile.getParent()+".d4sR";
			zipDir(outZip, folderToZip);
			_log.trace("gCube Modeler Model Instance Persisted: " + outZip);
			return true;
		} catch (IOException ex) {
			ex.printStackTrace();
			return false;
		}
	}

	public static Model readModel(String pathToModel) {
		Model toRead = null;

		FileInputStream fis = null;
		ObjectInputStream in = null;
		try {
			fis = new FileInputStream(pathToModel);
			in = new ObjectInputStream(fis);
			toRead = (Model) in.readObject();
			in.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		} 
		_log.debug("Scanning model instance with id: " + toRead.getUniqueID() + ", name: " + toRead.getTemplateName());
		int i = 1;
		for (BasicSection sec : toRead.getSections()) {
			_log.debug("Section " + i);
			for (BasicComponent bc : sec.getComponents()) {
				if (bc.getType() != ComponentType.FAKE_TEXTAREA)
					_log.debug("\t["+bc.getType()+"]" + " -> " + bc.getPossibleContent());
			}
			i++;			
		}
		return toRead;
	}

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
		System.out.println("Creating : " + zipFileName);
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



} 
