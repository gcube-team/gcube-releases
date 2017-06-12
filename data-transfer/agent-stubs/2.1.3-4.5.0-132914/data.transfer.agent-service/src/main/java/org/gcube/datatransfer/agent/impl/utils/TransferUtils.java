package org.gcube.datatransfer.agent.impl.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.axis.components.uuid.UUIDGen;
import org.apache.axis.components.uuid.UUIDGenFactory;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.VFS;
import org.apache.commons.vfs2.impl.DefaultFileSystemManager;
import org.apache.commons.vfs2.provider.SmpFileProvider;
import org.apache.commons.vfs2.provider.ftp.FtpFileSystemConfigBuilder;
import org.apache.commons.vfs2.provider.sftp.SftpFileSystemConfigBuilder;
import org.apache.commons.vfs2.provider.url.UrlFileProvider;
import org.apache.tools.tar.TarEntry;
import org.apache.tools.tar.TarInputStream;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.datatransfer.agent.impl.context.ServiceContext;
import org.gcube.datatransfer.agent.impl.jdo.Transfer;
import org.gcube.datatransfer.agent.impl.jdo.TransferObject;
import org.gcube.datatransfer.agent.impl.vfs.HttpFileSystemConfBuilderPatched;
import org.gcube.datatransfer.common.options.TransferOptions.ConversionType;
import org.gcube.datatransfer.common.outcome.TransferStatus;
import org.gcube.datatransfer.agent.stubs.datatransferagent.PostProcessType;
import org.gcube.datatransfer.agent.stubs.datatransferagent.TransferType;

public class TransferUtils {

	static GCUBELog logger = new GCUBELog(TransferUtils.class);

	final static int BUFFER = 2048;
	/** The UUIDGen */
	protected static final UUIDGen uuidgen = UUIDGenFactory.getUUIDGen();


	public static Transfer createTransferJDO(String transferId){
		Transfer t = new Transfer();
		t.setId(transferId);
		t.setStatus(TransferStatus.QUEUED.name());
		t.setSubmitter("N/A");
		t.setTransfersCompleted(0);
		t.setTotalSize(0);
		t.setSizeTransferred(0);
		t.setTotalTransfers(0);
		return t;
	}
	public static Transfer createTransferJDO(String transferId,String sourceID,String destID){
		Transfer t = new Transfer();
		t.setId(transferId);
		t.setSourceID(sourceID);
		t.setDestID(destID);
		t.setStatus(TransferStatus.QUEUED.name());
		t.setSubmitter("N/A");
		t.setTransfersCompleted(0);
		t.setTotalSize(0);
		t.setSizeTransferred(0);
		t.setTotalTransfers(0);
		return t;
	}
	
	public static long getTotalSize(String[] inputURIs) throws Exception  {
		long totalBytes = 0;
		for (int i =0; i<inputURIs.length;i++)
		{
			try {
				logger.debug("Trying to obtain the size of the URI : "+ inputURIs[i] );

				FileObject inputFile =prepareFileObject(inputURIs[i]);

				logger.debug("the size is  : "+ inputFile.getContent().getSize() );

				totalBytes+=inputFile.getContent().getSize();
			}catch (FileSystemException ex){
				logger.error("Error getting the size of file :" + inputURIs[i]);
				ex.printStackTrace();

			}catch (Exception e){
				logger.error("Error getting the size of file :" + inputURIs[i]);
				e.printStackTrace();
			}
		}
		return totalBytes;
	}

	public static TransferObject createTransferObjectJDO(String transferId,TransferType type){
		TransferObject t = new TransferObject();
		t.setId(uuidgen.nextUUID());
		t.setTransferType(type);
		t.setStatus(TransferStatus.STARTED.name());
		t.setTransferID(transferId);		
		return t;
	}

	public static void unzipArchive(String baseFolder,String archiveFile) throws Exception{
		BufferedOutputStream dest = null;
		FileInputStream fis = new FileInputStream(archiveFile);

		//ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
		//ZipEntry entry;
		TarInputStream zis = new TarInputStream(new BufferedInputStream(fis));
		TarEntry entry;
		
		while((entry = zis.getNextEntry()) != null) {

			int count;
			byte data[] = new byte[BUFFER];
			// write the files to the disk
			String outFile = baseFolder + File.separator+ entry.getName();
			FileOutputStream fos = new FileOutputStream(outFile);
			logger.debug("Uncompressing file "+outFile );
			dest = new 
					BufferedOutputStream(fos, BUFFER);
			while ((count = zis.read(data, 0, BUFFER)) 
					!= -1) {
				dest.write(data, 0, count);
			}
			dest.flush();
			dest.close();
		}
		zis.close();	
	}

	public static String getMimeType(String fileUrl)
			throws java.io.IOException
			{
		FileNameMap fileNameMap = URLConnection.getFileNameMap();
		String type = fileNameMap.getContentTypeFor(fileUrl);

		return type;
			}


	public static FileSystemOptions createDefaultOptions(String URI)
			throws FileSystemException {
		// Create SFTP options
		FileSystemOptions opts = new FileSystemOptions();

		int timeout = ServiceContext.getContext().getConnectionTimeout();
		//check the URL type
		if (URI.startsWith("ftp://")){

			// Root directory set to user home
			FtpFileSystemConfigBuilder.getInstance().setUserDirIsRoot(opts, true);

			// Timeout is count by Milliseconds
			FtpFileSystemConfigBuilder.getInstance().setSoTimeout(opts, timeout);

			FtpFileSystemConfigBuilder.getInstance().setDataTimeout(opts,timeout);
			return opts;
		} else if (URI.startsWith("sftp://")){
			// Root directory set to user home
			SftpFileSystemConfigBuilder.getInstance().setUserDirIsRoot(opts, true);

			// Timeout is count by Milliseconds
			SftpFileSystemConfigBuilder.getInstance().setTimeout(opts, timeout);
			return opts;
		} else if (URI.startsWith("s3://")){

			//com.scoyo.commons.vfs.S3Util.initS3Provider(ServiceContext.getContext().getAwsKeyID(),ServiceContext.getContext().getAwsKey());
		}else if (URI.startsWith("http://") || URI.startsWith("http://")) {
			// Root directory set to user home
			HttpFileSystemConfBuilderPatched.getInstance().setTimeout(opts, timeout);
			//HttpsFileSystemConfBuilderPatched.getInstance().s
			return opts;
		}
		else if(URI.startsWith("smp://") ){
			return opts;
		}
		return  null;
	}

	public static FileObject prepareFileObject(String URI)
			throws FileSystemException {
		System.out.println("prepareFileObject - "+URI);
		if(URI.startsWith("smp://")){
			DefaultFileSystemManager defaultmanag= new DefaultFileSystemManager();
			defaultmanag.addProvider("smp", new SmpFileProvider());
			defaultmanag.setDefaultProvider(new UrlFileProvider());
			defaultmanag.init();
			
			return defaultmanag.resolveFile(URI,createDefaultOptions(URI));
		}
		return VFS.getManager().resolveFile(URI,createDefaultOptions(URI));

	}


	public static boolean applyPostProcess(PostProcessType type, FileObject file, FileObject path, String conversionType) throws Exception
	{
		if(type.getValue().compareTo(PostProcessType.FileConversion.getValue())==0){
			if (conversionType == null)
				throw new Exception("File Conversione Type not specified");
			if (conversionType.compareTo(ConversionType.GEOTIFF.name())==0)
				GdalConverter.convertToGeoTiff(file.getName().getPath());		
		}else if (type.getValue().compareTo(PostProcessType.FileUnzip.getValue())==0){
			TransferUtils.unzipArchive(path.getName().getPath(),file.getName().getPath());
		}else if (type.getValue().compareTo(PostProcessType.OriginalFileRemove.getValue())==0){
			file.delete();
		}

		return true;

	}

	public static boolean applyPostProcess(PostProcessType type, String file, String path, String conversionType) throws Exception
	{
		if(type.getValue().compareTo(PostProcessType.FileConversion.getValue())==0){
			if (conversionType == null)
				throw new Exception("File Conversione Type not specified");
			if (conversionType.compareTo(ConversionType.GEOTIFF.name())==0)
				GdalConverter.convertToGeoTiff(file);		
		}else if (type.getValue().compareTo(PostProcessType.FileUnzip.getValue())==0){
			TransferUtils.unzipArchive(path,file);
		}else if (type.getValue().compareTo(PostProcessType.OriginalFileRemove.getValue())==0){
			new File(file).delete();
		}

		return true;

	}



}
