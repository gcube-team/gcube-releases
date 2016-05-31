package org.gcube.data.analysis.tabulardata.operation.comet;

import java.io.File;

import javax.inject.Inject;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.expression.evaluator.sql.SQLExpressionEvaluatorFactory;
import org.gcube.data.analysis.tabulardata.model.column.factories.CodeColumnFactory;
import org.gcube.data.analysis.tabulardata.model.column.factories.CodeDescriptionColumnFactory;
import org.gcube.data.analysis.tabulardata.model.column.type.CodeColumnType;
import org.gcube.data.analysis.tabulardata.model.metadata.common.ImmutableLocalizedText;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.type.CodelistTableType;
import org.gcube.data.analysis.tabulardata.operation.comet.MappingParser.ParserConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.googlecode.jeeunit.JeeunitRunner;

@RunWith(JeeunitRunner.class)
public class ParserTest {

	@Inject
	private SQLExpressionEvaluatorFactory evaluatorFactory;
	@Inject
	private CubeManager cubeManager;
	@Inject
	private DatabaseConnectionProvider provider;
	
	
	@Test
	public void testMapping() throws Exception{		
		ParserConfiguration config=new MappingParser.ParserConfiguration(theCodelist, theCodelist,theCodelist.getColumnsByType(CodeColumnType.class).get(0));
		MappingParser parser=new MappingParser(config, evaluatorFactory, cubeManager, provider);
		
		parser.parse(TestCommon.getMappingFileId());		
	}
	
	private Table theCodelist;
	
	
	
	@Before
	public void init(){
		ScopeProvider.instance.set("/gcube/devsec/devVRE");
		CodeDescriptionColumnFactory descFactory=new CodeDescriptionColumnFactory();
		CodeColumnFactory codeFactory=new CodeColumnFactory();
		theCodelist=cubeManager.createTable(new CodelistTableType()).
				addColumn(descFactory.create(new ImmutableLocalizedText("urn"))).
				addColumn(descFactory.create(new ImmutableLocalizedText("uuid"))).
				addColumn(codeFactory.create(new ImmutableLocalizedText("uri"))).
				create();
	}
}
