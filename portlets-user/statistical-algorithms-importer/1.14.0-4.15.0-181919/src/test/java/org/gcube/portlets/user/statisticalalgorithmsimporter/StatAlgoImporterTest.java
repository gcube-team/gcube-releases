/**
 * 
 */
package org.gcube.portlets.user.statisticalalgorithmsimporter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.Constants;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import junit.framework.TestCase;

/**
 * 
 * @author Giancarlo Panichi
 * 
 *
 */
public class StatAlgoImporterTest extends TestCase {
	private static Logger logger = LoggerFactory.getLogger(StatAlgoImporterTest.class);

	@Test
	public void testSpecialCharacters() {
		
		if (Constants.TEST_ENABLE) {
			try {
				String patternToMatch = "[\\\\!\"#$%&()*+,/:;<=>?@\\[\\]^{|}~]+";
				Pattern p = Pattern.compile(patternToMatch);
				String testString = "some text _ -";
				logger.info("Test: " + testString);
				Matcher m = p.matcher(testString);
				boolean characterFound = m.find();
				logger.info("Found: " + characterFound);
				assertTrue("Success", true);

			} catch (Throwable e) {
				logger.error(e.getLocalizedMessage(), e);
				fail("Error:" + e.getLocalizedMessage());

			}

		} else {
			assertTrue("Success", true);
		}

	}

}
