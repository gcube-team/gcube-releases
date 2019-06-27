package org.gcube.data.transfer.common;

import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.provider.ftp.FtpFileSystemConfigBuilder;
import org.apache.commons.vfs2.provider.sftp.SftpFileSystemConfigBuilder;

public class Utils {
	
	/**
	 * 
	 * @param URI
	 * @return
	 * @throws FileSystemException
	 */
	protected static FileSystemOptions createDefaultOptions(String URI,int connectiontimeout) {
		// Create SFTP options
		FileSystemOptions opts = new FileSystemOptions();

		// check the URL type
		if (URI.startsWith("ftp://")) {

			// Root directory set to user home
			FtpFileSystemConfigBuilder.getInstance().setUserDirIsRoot(opts,
					true);

			// Timeout is count by Milliseconds
			FtpFileSystemConfigBuilder.getInstance().setSoTimeout(opts,
					connectiontimeout);

			FtpFileSystemConfigBuilder.getInstance().setDataTimeout(opts,
					connectiontimeout);
			return opts;
		} else if (URI.startsWith("sftp://")) {
			// Root directory set to user home
			SftpFileSystemConfigBuilder.getInstance().setUserDirIsRoot(opts,
					true);

			// Timeout is count by Milliseconds
			SftpFileSystemConfigBuilder.getInstance().setTimeout(opts,
					connectiontimeout);
			return opts;
		} else if (URI.startsWith("s3://")) {

			// com.scoyo.commons.vfs.S3Util.initS3Provider(ServiceContext.getContext().getAwsKeyID(),ServiceContext.getContext().getAwsKey());
		} else if (URI.startsWith("http://") || URI.startsWith("https://")) {
			// Root directory set to user home
			HttpFileSystemConfBuilderPatched.getInstance().setTimeout(opts,
					connectiontimeout);
			HttpFileSystemConfBuilderPatched.getInstance().setFollowRedirect(opts,true);
			return opts;
		} else if (URI.startsWith("smp://")) {
			return opts;
		}
		return null;
	}

}
