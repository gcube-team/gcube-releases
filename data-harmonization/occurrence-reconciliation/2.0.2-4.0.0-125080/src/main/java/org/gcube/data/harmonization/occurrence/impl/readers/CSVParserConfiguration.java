package org.gcube.data.harmonization.occurrence.impl.readers;

import java.nio.charset.Charset;
import java.util.Arrays;


public class CSVParserConfiguration extends ParserConfiguration {

	private boolean[] fieldMap;

	public CSVParserConfiguration(Charset charset, char delimiter,
			char comment, boolean hasHeader, boolean[] fieldMap) {
		super(charset, delimiter, comment, hasHeader);
		this.fieldMap = fieldMap;
	}

	public CSVParserConfiguration(String charset, char delimiter, char comment,
			boolean hasHeader, boolean[] fieldMap) {
		super(charset, delimiter, comment, hasHeader);
		this.fieldMap = fieldMap;
	}
	
	public boolean[] getFieldMap() {
		return fieldMap;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CSVParserConfiguration [fieldMap=");
		builder.append(Arrays.toString(fieldMap));
		builder.append(", getCharset()=");
		builder.append(getCharset());
		builder.append(", getDelimiter()=");
		builder.append(getDelimiter());
		builder.append(", getComment()=");
		builder.append(getComment());
		builder.append(", isHasHeader()=");
		builder.append(isHasHeader());
		builder.append(", getClass()=");
		builder.append(getClass());
		builder.append(", hashCode()=");
		builder.append(hashCode());
		builder.append("]");
		return builder.toString();
	}
	
	
	
	
}
