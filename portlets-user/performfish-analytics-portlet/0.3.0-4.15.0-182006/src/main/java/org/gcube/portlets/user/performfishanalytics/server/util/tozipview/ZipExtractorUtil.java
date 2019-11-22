/**
 *
 */
package org.gcube.portlets.user.performfishanalytics.server.util.tozipview;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.gcube.portlets.user.performfishanalytics.shared.FileContentType;
import org.gcube.portlets.user.performfishanalytics.shared.OutputFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The Class ZipExtractorUtil.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Jan 24, 2019
 */
public class ZipExtractorUtil {

	protected static Logger log = LoggerFactory.getLogger(ZipExtractorUtil.class);

	private List<OutputFile> files = new ArrayList<OutputFile>();


	/**
	 * Instantiates a new zip extractor util.
	 *
	 * @param theZipFileURL the the zip file url
	 * @param filter the filter. Extracts only the file of kind {@link FileContentType} passed as filter
	 * @throws Exception the exception
	 */
	public ZipExtractorUtil(String theZipFileURL, List<FileContentType> filter) throws Exception{

		log.info("Extracting with filter: "+filter);

		InputStream is = new URL(theZipFileURL).openConnection().getInputStream();

		try (ArchiveInputStream input = new ArchiveStreamFactory()
	    .createArchiveInputStream(new BufferedInputStream(is, 1024*64))){
			ArchiveEntry entry;
			while ((entry = input.getNextEntry()) != null) {
				if(!entry.isDirectory()){
					try {

						String entirePath = entry.getName();
						String name = entirePath.replaceAll("(.*/)*(.*)", "$2");
						String parentPath = entirePath.replaceAll("(.*/)*(.*)", "$1");
						log.debug("read the file with entire path "+entirePath+", name "+name+", parentPath "+parentPath);
						OutputFile file = new OutputFile();
						file.setName(name);
						try{
							ByteArrayOutputStream cachedBytes = new ByteArrayOutputStream();
							IOUtils.copy(input, cachedBytes);
							ByteArrayInputStream inputNew = new ByteArrayInputStream(cachedBytes.toByteArray());
							if(ImageIO.read(inputNew) == null){
								log.info("ImageIO of the file "+name+" is null. It is NOT an image");

								if(name.endsWith("csv")){
									file.setDataType(FileContentType.CSV);
								}else{
									file.setDataType(FileContentType.UNKNOWN);
								}

							}else{
								file.setDataType(FileContentType.IMAGE);
//								String encodedString = Base64.getEncoder().encodeToString(cachedBytes.toByteArray());
//								log.info("\n\nfile "+name+" as base64 is: "+encodedString);
							}

							if(filter==null || filter.size() == 0 || filter.contains(file.getDataType())){
								File tempFile = createTempFile(file.getName(), "", cachedBytes.toByteArray());
								file.setServerLocation(tempFile.getAbsolutePath());
								files.add(file);
								log.debug("Files extracted now are: {}",files);
							}

						}catch(Exception e){
							log.info("extracting error ",e);
						}
					}catch(Exception e) {
						log.warn("error getting file {}",entry.getName(),e);
					}
				}

			}
		}
	}

    /**
     * Creates the temp file.
     *
     * @param fileName the file name
     * @param extension the extension
     * @param data the data
     * @return the file
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static File createTempFile(String fileName, String extension, byte[] data) throws IOException {
        // Since Java 1.7 Files and Path API simplify operations on files
    	java.nio.file.Path path = Files.createTempFile(fileName, extension);
        File file = path.toFile();
        // writing sample data
        Files.write(path, data);
        log.info("Created the Temp File: "+file.getAbsolutePath());
        return file;
    }

	/**
	 * Gets the output files.
	 *
	 * @return the output files
	 */
	public List<OutputFile> getOutputFiles(){
		return files;
	}
}
