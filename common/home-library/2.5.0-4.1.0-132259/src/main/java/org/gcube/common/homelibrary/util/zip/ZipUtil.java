package org.gcube.common.homelibrary.util.zip;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.folder.items.ExternalFile;
import org.gcube.common.homelibrary.home.workspace.folder.items.GCubeItem;
import org.gcube.common.homelibrary.home.workspace.folder.items.ts.TimeSeries;
import org.gcube.common.homelibrary.util.zip.zipmodel.ZipItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public class ZipUtil {

	protected static final Logger logger = LoggerFactory.getLogger(ZipUtil.class);

	/**
	 * Zip the folder content into a tmp zip file.
	 * @param folder the folder to be compressed.
	 * @return the zip file.
	 * @throws IOException if an error occurs.
	 * @throws InternalErrorException if an error occurs.
	 */
	public static File zipFolder(WorkspaceFolder folder) throws IOException, InternalErrorException
	{
		return zipWorkspaceItem(folder);
	}


	/**
	 * Zip the document into a tmp zip file.
	 * @param document the document to compress.
	 * @return the zip tmp file.
	 * @throws IOException if an error occurs.
	 * @throws InternalErrorException if an error occurs.
	 */
	public static File zipDocument(GCubeItem document) throws IOException, InternalErrorException
	{
		return zipWorkspaceItem(document);
	}


	/**
	 * @param ts the time series to zip.
	 * @return the zipped file.
	 * @throws IOException if an error occurs.
	 * @throws InternalErrorException if an error occurs.
	 */
	public static File zipTimeSeries(TimeSeries ts) throws IOException, InternalErrorException
	{
		return zipWorkspaceItem(ts);
	}


	/**
	 * Zip the folder content into a tmp zip file.
	 * @param folder the folder to be compressed.
	 * @param skipRoot
	 * @param idsToExclude
	 * @return the zip file.
	 * @throws IOException
	 * @throws InternalErrorException if an error occurs.
	 */
	public static File zipFolder(WorkspaceFolder folder, boolean skipRoot, List<String> idsToExclude) throws IOException, InternalErrorException
	{
		return zipWorkspaceItem(folder, skipRoot, idsToExclude);
	}

	/**
	 * Zip a list of items
	 * @param items the items to be compressed.
	 * @param idsToExclude
	 * @return the zip file.
	 * @throws IOException
	 * @throws InternalErrorException
	 */
	public static File zipWorkspaceItems(List<WorkspaceItem> items, List<String> idsToExclude) throws IOException, InternalErrorException
	{
		return zipWorkspaceItem(items, idsToExclude);
	}


	/**
	 * Zip the document into a tmp zip file.
	 * @param document the document to compress.
	 * @param skipRoot if the root has to be skipped
	 * @return the zip file.
	 * @throws IOException if an error occurs.
	 * @throws InternalErrorException if an error occurs.
	 */
	public static File zipDocument(GCubeItem document, boolean skipRoot) throws IOException, InternalErrorException
	{
		return zipWorkspaceItem(document, skipRoot);
	}



	/**
	 * @param ts the time series to zip.
	 * @param skipRoot if the root has to be skipped
	 * @return the zipped file.
	 * @throws IOException if an error occurs.
	 * @throws InternalErrorException if an error occurs.
	 */
	public static File zipTimeSeries(TimeSeries ts, boolean skipRoot) throws IOException, InternalErrorException
	{
		return zipWorkspaceItem(ts, skipRoot);
	}

	
	protected static File zipWorkspaceItem(WorkspaceItem workspaceItem) throws InternalErrorException, IOException
	{
		logger.trace("Zipping "+ workspaceItem.getName());
//		return zipWorkspaceItem(workspaceItem, false);
		File zipFile = File.createTempFile("zippping", "gz");
		try (ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(zipFile))) {
			processFolder(workspaceItem, zipOutputStream,  workspaceItem.getName());
		}
		return zipFile;		
	}
	


/**
 * Simple zip folder
 * @param myFolder
 * @param zipOutputStream
 * @param prefixLength
 * @throws IOException
 * @throws InternalErrorException
 */
	private static void processFolder(WorkspaceItem myFolder, final ZipOutputStream zipOutputStream, String path)
			throws IOException, InternalErrorException {
		for (WorkspaceItem file: myFolder.getChildren()) {
			if (!file.isFolder()) {
				final ZipEntry zipEntry = new ZipEntry(path + File.separator + file.getName());
				zipOutputStream.putNextEntry(zipEntry);
				ExternalFile externalFile = (ExternalFile)file;
				try (InputStream inputStream = externalFile.getData()) {
					IOUtils.copy(inputStream, zipOutputStream);
				}
				zipOutputStream.closeEntry();
			} else {
				processFolder(file, zipOutputStream, path + File.separator + file.getName());
			}
		}
	}
	
	protected static File zipWorkspaceItem(WorkspaceItem workspaceItem, boolean skipRoot) throws InternalErrorException, IOException
	{
		return zipWorkspaceItem(workspaceItem, skipRoot, new ArrayList<String>());
	}
	
	
	protected static File zipWorkspaceItem(WorkspaceItem workspaceItem, boolean skipRoot, List<String> idsToExclude) throws InternalErrorException, IOException
	{
		logger.trace("Zipping "+workspaceItem);

		logger.trace("converting to zip model");
		WorkspaceToZipModelConverter zipConverter = new WorkspaceToZipModelConverter();
		ZipItem item = zipConverter.convert(workspaceItem, idsToExclude);

//		ZipModelVisitor zipModelVisitor = new ZipModelVisitor();
//		zipModelVisitor.visitItem(item);

		logger.trace("writing model");
		ZipModelWriter zipModelWriter = new ZipModelWriter();
		File zipFile = zipModelWriter.writeItem(item, skipRoot);

		logger.trace("conversion complete in file "+zipFile.getAbsolutePath());
		return zipFile;
	}

	protected static File zipWorkspaceItem(List<WorkspaceItem> items, List<String> idsToExclude) throws InternalErrorException, IOException
	{
		
		logger.trace("Zipping items: "+items.toString());

		logger.trace("converting to zip model");
		WorkspaceToZipModelConverter zipConverter = new WorkspaceToZipModelConverter();
		ZipItem item = zipConverter.convert(items, idsToExclude);

//		ZipModelVisitor zipModelVisitor = new ZipModelVisitor();
//		zipModelVisitor.visitItem(item);

		logger.trace("writing model");
		ZipModelWriter zipModelWriter = new ZipModelWriter();
		File zipFile = zipModelWriter.writeItem(item, false);

		logger.trace("conversion complete in file "+zipFile.getAbsolutePath());
		return zipFile;
	}


	/**
	 * Zip the file content.
	 * @param input the input file content.
	 * @param output the output file.
	 * @param entryName the zip entry name.
	 * @throws IOException if an error occurs.
	 */
	public static void zip(File input, File output, String entryName) throws IOException
	{
		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(output));
		ZipEntry zipEntry = new ZipEntry(entryName);
		out.putNextEntry(zipEntry);
		IOUtils.copy(new FileInputStream(input), out);
		out.closeEntry();
		out.close();
	}




}
