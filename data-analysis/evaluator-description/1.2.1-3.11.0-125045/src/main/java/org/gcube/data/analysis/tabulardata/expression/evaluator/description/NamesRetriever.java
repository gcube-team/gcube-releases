package org.gcube.data.analysis.tabulardata.expression.evaluator.description;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.expression.evaluator.ReferenceResolver;
import org.gcube.data.analysis.tabulardata.metadata.NoSuchMetadataException;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;
import org.gcube.data.analysis.tabulardata.model.metadata.common.NamesMetadata;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableId;

@Singleton
public class NamesRetriever {

	private static final String DEFAULT_LOCALE = "en";

	ReferenceResolver referenceResolver;

	CubeManager cubeManager;

	@Inject
	public NamesRetriever(ReferenceResolver referenceResolver, CubeManager cubeManager) {
		this.referenceResolver = referenceResolver;
		this.cubeManager = cubeManager;
	}

	public String getColumnName(ColumnReference reference) {
		Column column = referenceResolver.getColumn(reference);
		try {
			NamesMetadata namesMetadata = column.getMetadata(NamesMetadata.class);
			if (namesMetadata.hasTextWithLocale(DEFAULT_LOCALE))
				return namesMetadata.getTextWithLocale(DEFAULT_LOCALE).getValue();
			else if (namesMetadata.getTexts().size() != 0)
				return namesMetadata.getTexts().get(0).getValue();
		} catch (NoSuchMetadataException e) {
		}
		return String.format("Unlabelled %s column", column.getColumnType().getName());
	}

	public String getTableName(TableId tableId){
		Table table = cubeManager.getTable(tableId);
		try {
			NamesMetadata namesMeta = table.getMetadata(NamesMetadata.class);
			if (namesMeta.hasTextWithLocale(DEFAULT_LOCALE)) return namesMeta.getTextWithLocale(DEFAULT_LOCALE).getValue();
			else if (namesMeta.getTexts().size()!=0) return namesMeta.getTexts().get(0).getValue();
		} catch (NoSuchMetadataException e) {
		}
		return String.format("%s table", table.getTableType().getName());
	}
}
