package org.gcube.elasticsearch.filters;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class Stopwords {

	private static final Logger logger = LoggerFactory.getLogger(Stopwords.class);
	private static final String STOPWORDS_BASEPATH = "stopword_files";
	private static String[] stopwords = null;

	private Stopwords(){}
	
	public static String[] getStopwords() throws FileNotFoundException{
		if(stopwords==null)
			loadStopwords();
		return stopwords;
	}
	
	private static void loadStopwords() throws FileNotFoundException {
		
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		URI fullpath = null;
		try {
			fullpath = classLoader.getResource(STOPWORDS_BASEPATH).toURI();
		} catch (URISyntaxException e1) {
			logger.error("Could not parse the stopwords for the index from directory WEB-INF/classes/"+STOPWORDS_BASEPATH);
			return;
		}
		File dir = new File(fullpath);
		if(!dir.isDirectory())
			throw new FileNotFoundException("The "+STOPWORDS_BASEPATH+ " is not a directory!");
		List<String> stopwordsList = new ArrayList<String>();
		for(File stopwordFile : dir.listFiles()){
			try (Stream<String> stream = Files.lines(Paths.get(stopwordFile.getAbsolutePath()))) {
				stream.forEach((line) -> {
					stopwordsList.add(line.trim());
				});
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		stopwords = stopwordsList.toArray(new String[0]);
	}
	
}

