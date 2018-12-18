package org.gcube.accounting.aggregator.directory;

import java.io.File;

import org.gcube.accounting.aggregator.utility.Constant;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class FileSystemDirectoryStructure extends DirectoryStructure<File> {

	@Override
	protected File getRoot() throws Exception {
		return Constant.ROOT_DIRECTORY;
	}

	@Override
	protected File createDirectory(File parent, String name) throws Exception {
		File directory = new File(parent, name);
		directory.mkdirs();
		return directory;
	}

}
