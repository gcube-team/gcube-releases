package gr.uoa.di.madgik.searchlibrary.operatorlibrary.contenttype;

import java.io.File;
import java.net.URL;

import javax.activation.MimetypesFileTypeMap;
import javax.activation.URLDataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContentTypeEvaluator {

	private static Logger log = LoggerFactory.getLogger(ContentTypeEvaluator.class.getName());

	public static String getContentType(File file) {
		String contentTypeEvalueated = null;
		try {
			URLDataSource urlDS = new URLDataSource(file.toURI().toURL());
			contentTypeEvalueated = urlDS.getContentType();
			log.trace("The content type of " + file.toURI().toURL() + " is " + contentTypeEvalueated);
		} catch (Exception e) {
			log.error("Did not manage to evaluate content format of file " + file);
		}
		return contentTypeEvalueated;
	}

	public static String getContentType(URL url) {
		URLDataSource urlDS = new URLDataSource(url);
		String contentTypeEvalueated = urlDS.getContentType();
		log.trace("The content type of " + url + " is " + contentTypeEvalueated);
		if (contentTypeEvalueated.equalsIgnoreCase("application/octet-stream")) {
			log.trace("The content type of " + url + " was not detected properly");
			contentTypeEvalueated = new MimetypesFileTypeMap().getContentType(url.toString());
			log.trace("The content type of " + url + " is reset to be " + contentTypeEvalueated);
		}
		return contentTypeEvalueated;
	}
}
