package gr.cite.commons.util.datarepository.client;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

import gr.cite.commons.util.datarepository.client.FileSystemDataRepositoryClient;
import gr.cite.commons.util.datarepository.elements.RepositoryFile;

public class FileSystemDataRepositoryClientIntegrationTest {

	private static final Logger logger = LoggerFactory.getLogger(FileSystemDataRepositoryClientIntegrationTest.class);

	private static String PATH_TO_LARGE_FILES_FOLDER = "/home/yannis/Desktop/EKT-images/tifs";

	FileSystemDataRepositoryClient client = new FileSystemDataRepositoryClient("http://localhost:9290/datarepository");

	public void squentialPersistTest() throws Exception {

		File[] files = getLargeTestFiles();

		for (int i = 0; i < files.length; i++) {
			File file = files[i];

			RepositoryFile repositoryFile = new RepositoryFile();
			try (InputStream inputStream = new FileInputStream(file)) {
				repositoryFile.setOriginalName(file.getName());
				repositoryFile.setInputStream(inputStream);
				repositoryFile.setDataType("image/tiff");

				client.persist(repositoryFile);

			}

			// break;
		}

	}

	public void multithreadedPersistRequestsTest() throws InterruptedException, ExecutionException {
		File[] files = getLargeTestFiles();

		ExecutorService executorService = Executors.newFixedThreadPool(10);
		List<Future<Void>> futures = new ArrayList<>();

		for (final File file : files) {

			futures.add(executorService.submit(new Callable<Void>() {

				@Override
				public Void call() throws Exception {

					RepositoryFile repositoryFile = new RepositoryFile();
					try (InputStream inputStream = new FileInputStream(file)) {
						repositoryFile.setOriginalName(file.getName());
						repositoryFile.setInputStream(inputStream);
						repositoryFile.setDataType("image/tiff");

						client.persist(repositoryFile);

					}

					repositoryFile.setInputStream(null);
					repositoryFile = null;
					return null;
				}
			}));

			// break;
		}

		executorService.shutdown();
		
		for (Future<Void> future : futures) {
			future.get();
		}
	}

	private File[] getLargeTestFiles() {
		File largeFilesFolder = new File(PATH_TO_LARGE_FILES_FOLDER);
		Preconditions.checkArgument(largeFilesFolder.isDirectory(),
				"'PATH_TO_LARGE_FILES_FOLDER = " + PATH_TO_LARGE_FILES_FOLDER + "' is not a directory");

		return largeFilesFolder.listFiles();
	}
	
	private File testRetrieveFolder(Path inputFolder, String folderId) throws Exception {
	//	client.persistToFolder(files)
		File folder = client.retrieveFolder(folderId);
	//	Files.walk(Paths.get(folder.toURI())).
		return folder;
	}

	public static void main(String[] args) throws Exception {
		FileSystemDataRepositoryClientIntegrationTest test = new FileSystemDataRepositoryClientIntegrationTest();

//		test.squentialPersistTest();
//		logger.info("------------------------------------------");
//		logger.info("---- squentialPersistTest successfull ----");
//		logger.info("------------------------------------------");

//		test.multithreadedPersistRequestsTest();
//		logger.info("------------------------------------------------------");
//		logger.info("---- multithreadedPersistRequestsTest successfull ----");
//		logger.info("------------------------------------------------------");
		
	//	File folder test.testRetrieveFolder("");
	}

}
