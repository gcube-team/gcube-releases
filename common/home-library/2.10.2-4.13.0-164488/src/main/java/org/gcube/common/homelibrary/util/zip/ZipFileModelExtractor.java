/**
 * 
 */
package org.gcube.common.homelibrary.util.zip;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.util.zip.zipmodel.ZipFile;
import org.gcube.common.homelibrary.util.zip.zipmodel.ZipFolder;
import org.gcube.common.homelibrary.util.zip.zipmodel.ZipItem;
import org.gcube.common.homelibrary.util.zip.zipmodel.ZipItemType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for ZipModel extractions.
 * Extracts the model from an InputStream.
 * @author Federico De Faveri defaveri@isti.cnr.it
 */
public class ZipFileModelExtractor {

	protected Logger logger = LoggerFactory.getLogger(HomeLibrary.class.getPackage().getName());

	protected Map<String, ZipItem> pathItemMap;

	protected ZipInputStream zis;

	/**
	 * @param is the zip input stream.
	 */
	public ZipFileModelExtractor(InputStream is)
	{
		zis = new ZipInputStream(is);
		pathItemMap = new LinkedHashMap<String, ZipItem>();
	}

	/**
	 * Returns the model from the zip file.
	 * @return the model.
	 * @throws IOException if an error occurs.
	 */
	public List<ZipItem> getModel() throws IOException
	{
		ZipEntry entry;
		while((entry = zis.getNextEntry())!=null)
		{
			String zipName = entry.getName();
			logger.trace("ZipName: "+zipName);

			String comment = entry.getComment();
			logger.trace("Comment: "+comment);

			byte[] extra = entry.getExtra();
			logger.trace("Extra: "+extra);

			boolean isDirectory = entry.isDirectory();
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
				FileOutputStream fos = new FileOutputStream(contentFile);
				byte[] buffer = new byte[1024];
				int reads = 0;
				while((reads = zis.read(buffer))>=0){
					fos.write(buffer,0,reads);
				}
				fos.close();

				item = new ZipFile(null, contentFile, name, comment, extra);

			}

			pathItemMap.put(path, item);

			logger.trace("Inserted "+path+" -> "+item+"\n");
		}

		return assignParents();
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
}
