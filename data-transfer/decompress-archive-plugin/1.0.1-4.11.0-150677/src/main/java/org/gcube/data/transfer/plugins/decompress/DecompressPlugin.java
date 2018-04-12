package org.gcube.data.transfer.plugins.decompress;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.gcube.data.transfer.model.ExecutionReport;
import org.gcube.data.transfer.model.ExecutionReport.ExecutionReportFlag;
import org.gcube.data.transfer.model.PluginInvocation;
import org.gcube.data.transfer.plugin.AbstractPlugin;
import org.gcube.data.transfer.plugin.fails.PluginCleanupException;
import org.gcube.data.transfer.plugin.fails.PluginExecutionException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DecompressPlugin extends AbstractPlugin {

	public DecompressPlugin(PluginInvocation invocation) {
		super(invocation);		
	}

	@Override
	public void cleanup() throws PluginCleanupException {


	}



	@Override
	public ExecutionReport run() throws PluginExecutionException {
		try{
			log.debug("Getting parameters from {} ",invocation);
		Map<String,String> params=invocation.getParameters();
		File source=new File(params.get(DecompressPluginFactory.SOURCE_PARAMETER));		
		String destinationPath=source.getParent() +(
				params.containsKey(DecompressPluginFactory.DESTINATION_PARAMETER)? File.separator+params.get(DecompressPluginFactory.DESTINATION_PARAMETER):"");
		Boolean overwrite=params.containsKey(DecompressPluginFactory.OVERWRITE_DESTINATION)?Boolean.parseBoolean(params.get(DecompressPluginFactory.OVERWRITE_DESTINATION)):Boolean.FALSE;
		log.trace("Unzipping {} to {} OVERWRITE= {} ",source.getAbsolutePath(),destinationPath,overwrite);		
		unzip(source,destinationPath,overwrite);	
		Boolean deleteArchive=params.containsKey(DecompressPluginFactory.DELETE_ARCHIVE)?Boolean.parseBoolean(params.get(DecompressPluginFactory.DELETE_ARCHIVE)):Boolean.FALSE;
		if(deleteArchive){
			log.trace("Deleting source {}",source.getAbsolutePath());
			deleteQuietly(source);
		}
		return new ExecutionReport(invocation,"Successfully decompressed to "+destinationPath,ExecutionReportFlag.SUCCESS);
		}catch(IOException e){
			log.error("Unable to unzip ",e);
			throw new PluginExecutionException("Unable to extract content, "+e.getMessage());
		}
	}



	public static final void unzip(File source,String destination,Boolean overwrite) throws IOException{
		log.debug("Creating destination folder {}",destination);

		File folder = new File(destination);
		if(!folder.exists()){
			folder.mkdir();
		}

		log.debug("Reading zip file ",source.getAbsolutePath());
		//get the zip file content
		ZipInputStream zis =
				new ZipInputStream(new FileInputStream(source));
		//get the zipped file list entry
		ZipEntry ze = zis.getNextEntry();


		byte[] buffer = new byte[1024];

		while(ze!=null){

			String fileName = ze.getName();
			if(fileName.endsWith(File.separator)) {
				// folder				
				fileName=fileName.substring(0, fileName.length()-1);
				
			}else {
				log.debug("Extracting {} ",fileName);

				String newFileBase=destination + File.separator + fileName;
				File newFile = new File(newFileBase);
				
				int suffixCounter=1;
				while(newFile.exists()){
					log.debug("File {} already exists, overwrite policy is {} ",newFile.getAbsolutePath(),overwrite);
					if(overwrite){
						log.debug("Deleting existing content");
						deleteQuietly(newFile);
					}else {
						newFile=new File(newFileBase+"("+suffixCounter+")");
						suffixCounter++;
					}
				}
				log.debug("Destination file is {}",newFile.getAbsolutePath());
				//create all non exists folders
				//else you will hit FileNotFoundException for compressed folder
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

		




	}


	private static final boolean deleteQuietly(File path){
		if( path.exists() ) {
		   if(!path.isDirectory()) return path.delete(); 
			File[] files = path.listFiles();
			for(int i=0; i<files.length; i++) {
				if(files[i].isDirectory()) {
					deleteQuietly(files[i]);
				}
				else {
					files[i].delete();
				}
			}
		}
		return( path.delete() );
	}


}
