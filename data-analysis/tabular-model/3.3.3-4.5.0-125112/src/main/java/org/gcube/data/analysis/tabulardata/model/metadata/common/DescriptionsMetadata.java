package org.gcube.data.analysis.tabulardata.model.metadata.common;

import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import org.gcube.data.analysis.tabulardata.model.metadata.column.ColumnMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.table.TableMetadata;

@XmlRootElement(name = "DescriptionsMetadata")
@XmlSeeAlso(value = { ImmutableLocalizedText.class })
public class DescriptionsMetadata extends ImmutableLocalizedTextHolder implements TableMetadata, ColumnMetadata {

	private static final long serialVersionUID = 3974092398276246215L;

	@SuppressWarnings("unused")
	private DescriptionsMetadata() {
	}

	public DescriptionsMetadata(List<LocalizedText> texts) {
		super(texts);
	}

	@XmlElementWrapper(name = "Descriptions")
	@XmlElementRef(name = "Description",type=ImmutableLocalizedText.class)
	private List<LocalizedText> getRawTexts() {
		return texts;
	}


	public boolean isInheritable() {
		return true;
	}

	@Override
	public List<LocalizedText> getTexts() {
		return Collections.unmodifiableList(texts);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DescriptionsMetadata [texts=");
		builder.append(texts);
		builder.append("]");
		return builder.toString();
	}

}
