package org.gcube.portlets.user.dataminerexecutor;

import org.gcube.portlets.user.dataminerexecutor.client.util.ElementsHighlights;
import org.gcube.portlets.user.dataminerexecutor.shared.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import junit.framework.TestCase;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class ElementsHighlightsTest extends TestCase {
	private static Logger logger = LoggerFactory.getLogger(ElementsHighlightsTest.class);

	public void testExecute() {
		if (Constants.TEST_ENABLE) {
			logger.debug("ElementsHighlightsTest Test");
			try {
				ElementsHighlights eh = new ElementsHighlights();

				/*String textWithLink = new String(
						"Result reported in https://trivial.com/error/Error.txt and https://trivial.com/error/Error.txt test2");
				logger.debug("Text with link: " + textWithLink);
				eh.createLinkFromText(textWithLink);

				String textWithoutLink = new String(
						"Error reported in https://trivial.com/error/Error.txt and https://trivial.com/error/Error.txt test2");
				logger.debug("Text without link: " + textWithoutLink);
				eh.createLinkFromText(textWithoutLink);

				String textNull = null;
				logger.debug("Text null: " + textNull);
				eh.createLinkFromText(textNull);*/

				String textWithSomeTags = new String(
						"The computation e1e862c8-2735-45ec-9e21-7663a7366316 of Netcdf Support "
						+ "Java has failed. http://www.opengis.net/ows/1.1 xmlns:wps='http://www.opengis.net/wps/1.0.0' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'> java.lang.RuntimeException: Logs of the script can be found at http://data-d.d4science.org/ZHFXaWtUS2FSOGlqSytNOHZaRHNMYTFRSkw3WEc2d3ZHbWJQNStIS0N6Yz0-VLT</ows:ExceptionText>");
				logger.debug("Text with some tags: " + textWithSomeTags);
				eh.createLinkFromText(textWithSomeTags);

				assertTrue("Success", true);

			} catch (Throwable e) {
				logger.error(e.getLocalizedMessage());
				e.printStackTrace();
				assertTrue("Error", false);
			}

		} else {
			assertTrue("Success", true);
		}

	}

}
