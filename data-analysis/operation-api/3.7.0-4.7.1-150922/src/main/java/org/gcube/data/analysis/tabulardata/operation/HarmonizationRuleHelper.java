package org.gcube.data.analysis.tabulardata.operation;

import java.util.Collections;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.factories.AttributeColumnFactory;
import org.gcube.data.analysis.tabulardata.model.datatype.BooleanType;
import org.gcube.data.analysis.tabulardata.model.datatype.TextType;
import org.gcube.data.analysis.tabulardata.model.harmonization.HarmonizationRule;
import org.gcube.data.analysis.tabulardata.model.metadata.common.ImmutableLocalizedText;
import org.gcube.data.analysis.tabulardata.model.metadata.common.LocalizedText;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.type.GenericTableType;

public class HarmonizationRuleHelper {

	public static Table createTable(CubeManager cubeManager){
		AttributeColumnFactory factory=new AttributeColumnFactory();
		Column toSetCol=factory.create(new TextType(), Collections.singletonList(((LocalizedText)new ImmutableLocalizedText("To set value"))));
		Column toChangeCol=factory.create(new TextType(), Collections.singletonList(((LocalizedText)new ImmutableLocalizedText("To change value"))));
		Column toSetColDesc=factory.create(new TextType(), Collections.singletonList(((LocalizedText)new ImmutableLocalizedText("To set value description"))));
		Column toChangeColDesc=factory.create(new TextType(), Collections.singletonList(((LocalizedText)new ImmutableLocalizedText("To change value description"))));
		Column enabledCol=factory.create(new BooleanType(), Collections.singletonList(((LocalizedText)new ImmutableLocalizedText("Enabled"))));
		Column referenceCol=factory.create(new TextType(), Collections.singletonList(((LocalizedText)new ImmutableLocalizedText("Referred column"))));
		
		
		toSetCol.setName(HarmonizationRule.TO_SET_VALUE_FIELD);
		toChangeCol.setName(HarmonizationRule.TO_CHANGE_VALUE_FIELD);
		toSetColDesc.setName(HarmonizationRule.TO_SET_VALUE_DESCRIPTION);
		toChangeColDesc.setName(HarmonizationRule.TO_CHANGE_VALUE_DESCRIPTION);
		enabledCol.setName(HarmonizationRule.ENABLED);
		referenceCol.setName(HarmonizationRule.REFERRED_CODELIST_COLUMN);
		
		return cubeManager.createTable(new GenericTableType()).addColumns(
				toSetCol,
				toChangeCol,
				toSetColDesc,
				toChangeColDesc,
				enabledCol,
				referenceCol).create();
	}
	
}
