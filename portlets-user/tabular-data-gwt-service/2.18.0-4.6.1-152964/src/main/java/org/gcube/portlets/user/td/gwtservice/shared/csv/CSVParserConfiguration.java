/**
 * 
 */
package org.gcube.portlets.user.td.gwtservice.shared.csv;

import java.io.Serializable;
import java.nio.charset.Charset;

import org.gcube.portlets.user.td.gwtservice.shared.file.HeaderPresence;

/**
 * 
 * @author Giancarlo Panichi
 * 
 *
 */
public class CSVParserConfiguration implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5585106307191211813L;
	private Charset charset;
	private char delimiter;
	private char comment;
	private HeaderPresence headerPresence;

	public CSVParserConfiguration() {
		super();
	}

	public CSVParserConfiguration(Charset charset, char delimiter, char comment, HeaderPresence headerPresence) {
		super();
		this.charset = charset;
		this.delimiter = delimiter;
		this.comment = comment;
		this.headerPresence = headerPresence;
	}

	public void update(String charset, char delimiter, char comment, HeaderPresence headerPresence) {
		this.charset = Charset.forName(charset);
		this.delimiter = delimiter;
		this.comment = comment;
		this.headerPresence = headerPresence;
	}

	public Charset getCharset() {
		return charset;
	}

	public char getDelimiter() {
		return delimiter;
	}

	public char getComment() {
		return comment;
	}

	public HeaderPresence getHeaderPresence() {
		return headerPresence;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CSVParsingConfig [charset=");
		builder.append(charset);
		builder.append(", delimiter=");
		builder.append(delimiter);
		builder.append(", comment=");
		builder.append(comment);
		builder.append(", headerPresence=");
		builder.append(headerPresence);
		builder.append("]");
		return builder.toString();
	}
}
