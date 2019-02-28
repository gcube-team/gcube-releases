package org.gcube.portlets.user.accountingdashboard;

import org.gcube.portlets.user.accountingdashboard.shared.Constants;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cern.colt.Arrays;
import junit.framework.TestCase;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class ColorTest extends TestCase {
	private static Logger logger = LoggerFactory.getLogger(ColorTest.class);

	@Test
	public void testService() {
		if (Constants.TEST_ENABLE) {

			try {
				String[] blue = new String[] { "#87CEEB", "#5F9EA0", "#1E90FF", "#663399", "#00008B", "#0000CD",
						"#0000FF", "#4169E1", "#00BFFF", "#6495ED", "#87CEFA", "#B0E0E6", "#B0C4DE", "#4682B4" };

				String[] brown = new String[] { "#DAA520", "#A0522D", "#D2B48C", "#FFE4C4", "#FFDEAD", "#F5DEB3",
						"#DEB887", "#BC8F8F", "#F4A460", "#B8860B", "#CD853F", "#D2691E", "#808000", "#8B4513" };

				String[] green = new String[] { "#3CB371", "#6B8E23", "#90EE90", "#ADFF2F", "#7CFC00", "#00FF00",
						"#32CD32", "#98FB98", "#00FA9A", "#2E8B57", "#228B22", "#008000", "#006400", "#9ACD32" };

				String[] red = new String[] { "#FF4500", "#FFA07A", "#B22222", "#FF0000", "#FF6347", "#FF7F50",
						"#FFA500", "#8B0000", "#800000", "#FFD700", "#DC143C", "#CD5C5C", "#F08080", "#FA8072" };

				String[] cyan = new String[] { "#8FBC8F", "#696969", "#00CED1", "#00FFFF", "#AFEEEE", "#7FFFD4",
						"#40E0D0", "#48D1CC", "#556B2F", "#66CDAA", "#20B2AA", "#008B8B", "#008080", "#778899" };

				String[] purple = new String[] { "#FF00FF", "#DDA0DD", "#9400D3", "#483D8B", "#6A5ACD", "#7B68EE",
						"#9370DB", "#800080", "#8A2BE2", "#9932CC", "#BA55D3", "#FF00FF", "#EE82EE", "#DA70D6" };

				String[] yellow = new String[] { "#BDB76B", "#C71585", "#FFDAB9", "#FFFF00", "#FFFACD", "#FFEFD5",
						"#FFE4B5", "#EEE8AA", "#F0E68C", "#FFC0CB", "#FFB6C1", "#FF69B4", "#FF1493", "#DB7093" };

				logger.debug("Dimension: red=" + red.length + ", cyan=" + cyan.length + ", purple=" + purple.length
						+ ", yellow=" + yellow.length + ", blue=" + blue.length + ", brown=" + brown.length + ", green="
						+ green.length);
				int total = (red.length + cyan.length + purple.length + yellow.length + blue.length + brown.length
						+ green.length);
				int average = total / 7;
				logger.debug("Total: " + total);
				logger.debug("Average: " + average);

				/*
				 * System.out.print("purple=["); for (int i = purple.length - 1;
				 * i > -1; i--) { if (i == purple.length - 1) {
				 * System.out.print("\"" + purple[i] + "\""); } else {
				 * System.out.print(",\"" + purple[i] + "\""); } }
				 * System.out.print("]"); System.out.println("");
				 */

				String[] palette = new String[total];
				int j = 0;
				for (int i = 0; i < average; i++) {
					j = i * 7;
					palette[j] = blue[i];
					palette[j + 1] = brown[i];
					palette[j + 2] = green[i];
					palette[j + 3] = red[i];
					palette[j + 4] = cyan[i];
					palette[j + 5] = purple[i];
					palette[j + 6] = yellow[i];

				}

				logger.debug("Palette: " + Arrays.toString(palette));
				StringBuilder paletteBuilder = new StringBuilder();
				paletteBuilder.append("[");
				for (int i = 0; i < palette.length; i++) {
					if (i == 0) {
						paletteBuilder.append("'" + palette[i] + "'");

					} else {
						paletteBuilder.append(",'" + palette[i] + "'");
					}
				}
				paletteBuilder.append("]");

				logger.debug("Palette array: " + paletteBuilder.toString());

				StringBuilder htmlBuilder = new StringBuilder();
				htmlBuilder.append("<html><head></head><body>");
				int k = 0;
				for (int i = 0; i < palette.length; i++) {
					if (i % 7 == 0) {
						k++;
						htmlBuilder.append("<div><h2>Colors: " + k + "</h2></div>");

					}
					htmlBuilder.append("<div style='height:30px;width:100px;background-color:" + palette[i] + ";'>"
							+ palette[i] + "</div>");

				}
				htmlBuilder.append("</body></html>");

				logger.debug("HTML: " + htmlBuilder.toString());

				StringBuilder htmlSingleColorBuilder = new StringBuilder();
				htmlSingleColorBuilder.append("<html><head></head><body>");
				int v = 0;
				for (int i = 0; i < green.length; i++) {
					if (i % 7 == 0) {
						v++;
						htmlSingleColorBuilder.append("<div><h2>Colors: " + v + "</h2></div>");

					}
					htmlSingleColorBuilder.append("<div style='height:30px;width:100px;background-color:" + green[i]
							+ ";'>" + green[i] + "</div>");

				}
				htmlSingleColorBuilder.append("</body></html>");

				logger.debug("HTML Color green: " + htmlSingleColorBuilder.toString());

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