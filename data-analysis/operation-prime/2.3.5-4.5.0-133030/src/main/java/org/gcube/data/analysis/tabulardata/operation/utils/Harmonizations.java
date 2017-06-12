package org.gcube.data.analysis.tabulardata.operation.utils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.bind.JAXBException;

import org.apache.commons.dbutils.DbUtils;
import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.expression.composite.comparable.Equals;
import org.gcube.data.analysis.tabulardata.expression.evaluator.sql.SQLExpressionEvaluatorFactory;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDText;
import org.gcube.data.analysis.tabulardata.model.harmonization.HarmonizationRule;
import org.gcube.data.analysis.tabulardata.model.metadata.table.HarmonizationRuleTable;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.operation.SQLHelper;
import org.gcube.data.analysis.tabulardata.operation.validation.ValidateDataWithExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Harmonizations {
	
	private static final Logger log = LoggerFactory.getLogger(ValidateDataWithExpression.class);
	
	public static void harmonizeTable(HarmonizationRuleTable rules,
			ColumnReference referredCodelistColumn,
			ColumnReference toHarmonize,
			Table toHarmonizeTable,
			DatabaseConnectionProvider connectionProvider,
			SQLExpressionEvaluatorFactory sqlEvaluatorFactory) throws SQLException{
		Connection conn=null;
		Statement stmt=null;
		ResultSet rsRule=null;
		try{
			conn=connectionProvider.getConnection();
			stmt=conn.createStatement();
			String conditionValue=sqlEvaluatorFactory.getEvaluator(new TDText(referredCodelistColumn.getColumnId().getValue())).evaluate();
			String getRulesQuery="SELECT * FROM "+rules.getRulesTable().getName()+" WHERE "+HarmonizationRule.REFERRED_CODELIST_COLUMN+" = "+conditionValue;
			log.debug("Rule query is "+getRulesQuery);
			rsRule=stmt.executeQuery(getRulesQuery);
			ArrayList<String> updateCommands=new ArrayList<>();			
			ResultSetMetaData rsMeta=rsRule.getMetaData();
			while(rsRule.next()){
				try{
					HarmonizationRule rule=getRule(rsRule, rsMeta);
					updateCommands.add(getRuleSqlCommand(rule, toHarmonize, toHarmonizeTable, sqlEvaluatorFactory));
				}catch(Exception e){
					log.error(String.format("Skipping invalid rule [ID : %s, table :]",
							rsRule.getString(HarmonizationRule.ID),rules.getRulesTable().getName()),e);
				}
			}
			log.debug("Applying "+updateCommands.size()+" rules on "+toHarmonize);
			SQLHelper.executeSQLBatchCommands(connectionProvider, updateCommands.toArray(new String[updateCommands.size()]));
		}finally{
			DbUtils.closeQuietly(rsRule);
			DbUtils.closeQuietly(stmt);
			DbUtils.closeQuietly(conn);
		}
	}
	
	public static HarmonizationRule getRule(ResultSet rs, ResultSetMetaData rsMeta) throws JAXBException, SQLException{
		HashMap<String,String> fields=new HashMap<>();
		for(int i=1;i<=rsMeta.getColumnCount();i++){			
			fields.put(rsMeta.getColumnName(i), rs.getString(i));
		}
		return new HarmonizationRule(fields);
	}
	
	public static String getRuleSqlCommand(HarmonizationRule rule,ColumnReference targetColumn,Table targetTable,SQLExpressionEvaluatorFactory sqlEvaluatorFactory){
		String targetColumnName=targetTable.getColumnById(targetColumn.getColumnId()).getName();
		String toSetValue=sqlEvaluatorFactory.getEvaluator(rule.getToSetValue()).evaluate();
		String condition=sqlEvaluatorFactory.getEvaluator(new Equals(targetColumn, rule.getToChangeValue())).evaluate();
		return String.format("UPDATE %s SET %s = %s WHERE %s", 
				targetTable.getName(),
				targetColumnName,
				toSetValue,
				condition);
	}
	
	
	public static boolean isColumnUnderRules(ColumnLocalId referredColumnId,HarmonizationRuleTable table, DatabaseConnectionProvider connectionProvider, SQLExpressionEvaluatorFactory sqlEvaluatorFactory) throws SQLException{
		Connection conn=null;
		Statement stmt=null;
		try{
			conn=connectionProvider.getConnection();
			stmt=conn.createStatement();
			String conditionValue=sqlEvaluatorFactory.getEvaluator(new TDText(referredColumnId.toString())).evaluate();
			return stmt.execute("SELECT * FROM "+table.getRulesTable().getName()+" WHERE "+HarmonizationRule.REFERRED_CODELIST_COLUMN+" = "+conditionValue+" OFFSET 0 LIMIT 1");
		}finally{
			DbUtils.closeQuietly(stmt);
			DbUtils.closeQuietly(conn);
		}
	}
}
