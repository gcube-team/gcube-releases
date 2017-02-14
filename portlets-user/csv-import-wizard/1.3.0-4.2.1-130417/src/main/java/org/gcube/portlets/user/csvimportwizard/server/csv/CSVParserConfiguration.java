/**
 * 
 */
package org.gcube.portlets.user.csvimportwizard.server.csv;

import java.nio.charset.Charset;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public class CSVParserConfiguration {
	
	protected Charset charset;
	protected char delimiter;
	protected char comment;
	protected boolean hasHeader;
	
	public CSVParserConfiguration() {
	}
	
	/**
	 * @param charset
	 * @param delimiter
	 * @param comment
	 * @param hasHeader
	 */
	public CSVParserConfiguration(Charset charset, char delimiter, char comment, boolean hasHeader) {
		this.charset = charset;
		this.delimiter = delimiter;
		this.comment = comment;
		this.hasHeader = hasHeader;
	}

	public void update(String charset, char delimiter, char comment, boolean hasHeader) {
		this.charset = Charset.forName(charset);
		this.delimiter = delimiter;
		this.comment = comment;
		this.hasHeader = hasHeader;
	}

	/**
	 * @return the charset
	 */
	public Charset getCharset() {
		return charset;
	}

	/**
	 * @return the delimiter
	 */
	public char getDelimiter() {
		return delimiter;
	}

	/**
	 * @return the comment
	 */
	public char getComment() {
		return comment;
	}

	/**
	 * @return the hasHeader
	 */
	public boolean isHasHeader() {
		return hasHeader;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CSVParsingConfig [charset=");
		builder.append(charset);
		builder.append(", delimiter=");
		builder.append(delimiter);
		builder.append(", comment=");
		builder.append(comment);
		builder.append(", hasHeader=");
		builder.append(hasHeader);
		builder.append("]");
		return builder.toString();
	}
}
