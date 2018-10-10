package org.gcube.portlets.docxgenerator.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.tidy.Tidy;

/**
 * 
 * Transforms HTML code into valid XHTML.
 * 
 * @author Luca Santocono
 * 
 */
public class HTML2XML {

	private static final Log log = LogFactory.getLog(HTML2XML.class);

	/**
	 * Disables new line wrapping. Otherwise Word outputs 4 spaces instead of
	 * the inserted new line.
	 * 
	 * By disabling the wrap length it is assured that pieces of XHTML like the
	 * following
	 * 
	 * <br/>
	 * <br/>
	 * 
	 * {@code <span style="font-style: italic;">dddddddddddddd
	 * ddddddddddddddd</span>}
	 * 
	 * <br/>
	 * <br/>
	 * 
	 * are printed in one line.
	 * 
	 */
	private static final int wraplength = 0;

	/**
	 * Converts an InputStream containing HTML code into valid XHTML.
	 * 
	 * @param inputStream
	 *            The InputStream containing HTML code.
	 * @return A String containing valid XHTML code resulting from the
	 *         transformation.
	 * @throws IOException
	 *             if there are problems in writing to the output string.
	 */
	public String convert(final InputStream inputStream) throws IOException {
		Writer out;
		Tidy tidy = new Tidy();
		// Tell Tidy to convert HTML to XML
		tidy.setXmlOut(true);
		// Set file for error messages
		// tidy
		// .setErrout(new PrintWriter(new FileWriter(errOutFileName),
		// true));
		tidy.setXmlOut(true);
		tidy.setXHTML(true);
		out = new StringWriter();
		// Convert files
		tidy.parse(inputStream, out);
		// Clean up
		out.flush();
		out.close();
		return out.toString();

	}

	/**
	 * Converts a String containing HTML code into valid XHTML.
	 * 
	 * @param toConvert
	 *            The String containing HTML code.
	 * @return A String containing valid XHTML code resulting from the
	 *         transformation.
	 * @throws IOException
	 *             if there are problems in writing to the output string.
	 */
	public static String convert(final String toConvert) throws IOException {
		Reader in;
		Writer out;
		Tidy tidy = new Tidy();
		// no wrapping
		tidy.setWraplen(wraplength);
		log.debug("to parse........." + tidy.getWraplen() + " " + toConvert);
		tidy.setXmlOut(true);
		tidy.setIndentContent(false);
		in = new StringReader(toConvert);
		out = new StringWriter();
		tidy.parse(in, out);
		in.close();
		out.flush();
		out.close();
		return out.toString();
	}

}