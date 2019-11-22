package org.gcube.data.analysis.dataminermanagercl.server.uriresolver;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.gcube.data.analysis.dataminermanagercl.shared.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class UriResolverUtils {
	private static Logger logger = LoggerFactory.getLogger(UriResolverUtils.class);

	public String getFileName(String publicLink) throws ServiceException {
		logger.info("Public Link: " + publicLink);
		try {

			URL urlObj = new URL(publicLink);
			HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
			connection.setRequestMethod("HEAD");
			connection.setDoOutput(true);
			logger.info("Response Code:" + connection.getResponseCode());

			String fileName = null;
			List<String> contentDisposition = connection.getHeaderFields().get("Content-Disposition");
			logger.info("Content-Disposition: " + contentDisposition);
			if (contentDisposition != null && !contentDisposition.isEmpty()) {
				for (String value : contentDisposition) {
					if (value != null && !value.isEmpty() && value.startsWith("attachment; filename=")) {
						fileName = value.substring(21);
						if (fileName != null && !fileName.isEmpty()) {
							fileName = fileName.replace("\"", "");
							logger.info("File name: " + fileName);
							break;
						}
					}
				}

			}

			if (fileName == null || fileName.isEmpty()) {
				logger.error("Error retrieving filename from URI Resolver for public link: " + publicLink);
				throw new ServiceException(
						"Error retrieving filename from URI Resolver for public link: " + publicLink);
			}
			return fileName;

		} catch (ServiceException e) {
			logger.error(e.getLocalizedMessage(), e);
			throw e;
		} catch (Throwable e) {
			logger.error("Error retrieving filename from URI Resolver for public link: " + publicLink);
			logger.error(e.getLocalizedMessage(), e);
			throw new ServiceException("Error retrieving filename from URI Resolver for url: " + publicLink, e);
		}
	}

}
