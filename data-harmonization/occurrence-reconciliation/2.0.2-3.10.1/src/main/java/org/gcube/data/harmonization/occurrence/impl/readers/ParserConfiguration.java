package org.gcube.data.harmonization.occurrence.impl.readers;

import java.nio.charset.Charset;

public abstract class ParserConfiguration {

	private Charset charset;
	private char delimiter;
	private char comment;
	private boolean hasHeader;
	
	public ParserConfiguration(Charset charset, char delimiter, char comment,
			boolean hasHeader) {
		super();
		this.charset = charset;
		this.delimiter = delimiter;
		this.comment = comment;
		this.hasHeader = hasHeader;
	}
	
	public ParserConfiguration(String charset, char delimiter, char comment,
			boolean hasHeader) {
		super();
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

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ParserConfiguration [charset=");
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
