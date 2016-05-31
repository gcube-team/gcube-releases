/**
 * 
 */
package org.gcube.portlets.user.td.gwtservice.shared.csv;

import java.io.Serializable;
import java.nio.charset.Charset;

import org.gcube.portlets.user.td.gwtservice.shared.file.HeaderPresence;



/**
 * 
 * @author giancarlo
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class CSVParserConfiguration implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5585106307191211813L;
	protected Charset charset;
	protected char delimiter;
	protected char comment;
	protected HeaderPresence headerPresence;
	
	public CSVParserConfiguration() {
	}
	
	/**
	 * @param charset
	 * @param delimiter
	 * @param comment
	 * @param hasHeader
	 */
	public CSVParserConfiguration(Charset charset, char delimiter, char comment, HeaderPresence headerPresence) {
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
	 * @return the headerPresence
	 */
	public HeaderPresence getHeaderPresence() {
		return headerPresence;
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
		builder.append(", headerPresence=");
		builder.append(headerPresence);
		builder.append("]");
		return builder.toString();
	}
}
