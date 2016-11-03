package org.gcube.data.analysis.tabulardata.operation.labels;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.gcube.data.analysis.tabulardata.metadata.NoSuchMetadataException;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.metadata.common.LocalizedText;
import org.gcube.data.analysis.tabulardata.model.metadata.common.NamesMetadata;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.operation.test.OperationTester;
import org.gcube.data.analysis.tabulardata.operation.test.util.CodelistHelper;
import org.junit.Before;
import org.junit.runner.RunWith;

import com.googlecode.jeeunit.JeeunitRunner;
@RunWith(JeeunitRunner.class)
public class RemoveColumnNameTest extends OperationTester<RemoveColumnNameFactory>{

	
	
	@Inject
	RemoveColumnNameFactory factory;
	
	@Inject
	CodelistHelper codelistHelper;
	
	Table testTable;
	
	@Before
	public void createTestTable(){
		testTable=codelistHelper.createSpeciesCodelist();
	}
	
	@Override
	protected TableId getTargetTableId() {
		return testTable.getId();
	}
	
	@Override
	protected RemoveColumnNameFactory getFactory() {
		return factory;
	}
	
	@Override
	protected Map<String, Object> getParameterInstances() {
		Map<String,Object> parameterInstances=new HashMap<String, Object>();
		parameterInstances.put(RemoveColumnNameFactory.NAME_LABEL_PARAMETER.getIdentifier(), getToRemoveLabel());		
		return parameterInstances;
	}
	
	
	private LocalizedText getToRemoveLabel(){
		try {
			return testTable.getColumnById(getTargetColumnId()).getMetadata(NamesMetadata.class).getTexts().get(0);
		} catch (NoSuchMetadataException e) {	
			throw new RuntimeException("Table has no metadata ",e);
		}
	}

	
	
	
	@Override
	protected ColumnLocalId getTargetColumnId() {
				for(Column col:testTable.getColumns()){
					try{
						col.getMetadata(NamesMetadata.class).getTexts().get(0);
						return col.getLocalId();
					}catch(Exception e){
						//skipping columns without names meta
					}
				}
				throw new RuntimeException("No labelled column to use");
	}
	
}
