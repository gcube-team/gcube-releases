package org.gcube.data.analysis.tabulardata.model.metadata.common;

import java.util.List;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import org.gcube.data.analysis.tabulardata.model.metadata.column.ColumnMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.table.TableMetadata;

@XmlRootElement(name = "NamesMetadata")
@XmlSeeAlso(value={ImmutableLocalizedText.class})
public class NamesMetadata extends ImmutableLocalizedTextHolder implements ColumnMetadata, TableMetadata {

	private static final long serialVersionUID = 3974092398276246215L;
	
	@SuppressWarnings("unused")
	private NamesMetadata() {}

	public NamesMetadata(List<LocalizedText> texts) {
		super(texts);
	}

	@XmlElementWrapper(name = "Names")
	@XmlElementRef(name = "Name",type=ImmutableLocalizedText.class)
	private List<LocalizedText> getRawTexts() {
		return texts;
	}

	public boolean isInheritable() {
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("NamesMetadata [texts=");
		builder.append(texts);
		builder.append("]");
		return builder.toString();
	}
	
	

}
