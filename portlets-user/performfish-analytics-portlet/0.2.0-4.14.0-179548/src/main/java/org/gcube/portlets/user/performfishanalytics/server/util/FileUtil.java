/**
 *
 */
package org.gcube.portlets.user.performfishanalytics.server.util;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The Class FileUtil.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Feb 4, 2019
 */
public class FileUtil {

	protected static Logger log = LoggerFactory.getLogger(FileUtil.class);

	/**
	 * Delete directory recursion.
	 *
	 * @param path the path
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void deleteDirectoryRecursion(Path path) throws IOException {

		if (Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)) {
			try (DirectoryStream<Path> entries = Files.newDirectoryStream(path)) {
				for (Path entry : entries) {
					deleteDirectoryRecursion(entry);
				}
			}
		}
		log.info("Deleting File/Directory: %s", path.getFileName());
		Files.delete(path);


	}
}
