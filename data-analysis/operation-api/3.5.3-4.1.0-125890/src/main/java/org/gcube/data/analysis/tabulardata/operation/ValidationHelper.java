package org.gcube.data.analysis.tabulardata.operation;

import java.sql.SQLException;

import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.expression.composite.comparable.Equals;
import org.gcube.data.analysis.tabulardata.expression.evaluator.EvaluatorException;
import org.gcube.data.analysis.tabulardata.expression.evaluator.sql.SQLExpressionEvaluatorFactory;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDBoolean;
import org.gcube.data.analysis.tabulardata.model.metadata.Locales;
import org.gcube.data.analysis.tabulardata.model.metadata.column.DataValidationMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.common.ImmutableLocalizedText;
import org.gcube.data.analysis.tabulardata.model.metadata.common.LocalizedText;
import org.gcube.data.analysis.tabulardata.model.metadata.common.NamesMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.table.GlobalDataValidationReportMetadata;
import org.gcube.data.analysis.tabulardata.model.table.Table;

public class ValidationHelper {


	public static GlobalDataValidationReportMetadata createDataValidationReport(Column validationColumn){
		LocalizedText name=validationColumn.getMetadata(NamesMetadata.class).getTextWithLocale(Locales.getDefaultLocale());
		DataValidationMetadata dataMeta=validationColumn.getMetadata(DataValidationMetadata.class);
		return new GlobalDataValidationReportMetadata(new ImmutableLocalizedText(dataMeta.getDescription()), name, dataMeta.getInvalidRowsCount(), validationColumn.getLocalId());
	}
	
	public static int getErrorCount(DatabaseConnectionProvider connectionProvider,Table targetTable,Column validationColumn,SQLExpressionEvaluatorFactory evaluatorFactory ) throws EvaluatorException, SQLException{
		return SQLHelper.getCount(connectionProvider, targetTable.getName(),
				evaluatorFactory.getEvaluator(new Equals(targetTable.getColumnReference(validationColumn), new TDBoolean(false))).evaluate());
	}
	
}
