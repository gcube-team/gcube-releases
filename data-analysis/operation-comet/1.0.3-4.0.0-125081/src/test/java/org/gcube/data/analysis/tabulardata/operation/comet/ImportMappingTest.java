package org.gcube.data.analysis.tabulardata.operation.comet;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;

import org.gcube.contentmanagement.blobstorage.service.IClient;
import org.gcube.contentmanager.storageclient.wrapper.AccessType;
import org.gcube.contentmanager.storageclient.wrapper.StorageClient;
import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.tablemanagers.TableCreator;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;
import org.gcube.data.analysis.tabulardata.model.column.factories.AttributeColumnFactory;
import org.gcube.data.analysis.tabulardata.model.column.factories.CodeColumnFactory;
import org.gcube.data.analysis.tabulardata.model.column.factories.CodeDescriptionColumnFactory;
import org.gcube.data.analysis.tabulardata.model.column.factories.CodeNameColumnFactory;
import org.gcube.data.analysis.tabulardata.model.datatype.BooleanType;
import org.gcube.data.analysis.tabulardata.model.datatype.TextType;
import org.gcube.data.analysis.tabulardata.model.metadata.column.DataLocaleMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.common.ImmutableLocalizedText;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.model.table.type.CodelistTableType;
import org.gcube.data.analysis.tabulardata.operation.test.OperationTester;
import org.gcube.data.analysis.tabulardata.operation.test.util.CopyHandler;
import org.gcube.data.analysis.tabulardata.operation.worker.WorkerFactory;
import org.junit.Before;
import org.junit.runner.RunWith;

import com.googlecode.jeeunit.JeeunitRunner;

@RunWith(JeeunitRunner.class)
public class ImportMappingTest extends OperationTester<ImportCodeListMappingFactory>{

	
	@Inject
	private ImportCodeListMappingFactory factory;
	
	@Inject
	private CubeManager cubeManager;
	
	@Inject
	private CopyHandler copyHandler;
	
	
	private Table codelist;
	private Table toCurateCodelist;
	private ColumnReference oldCodes;
	
	
	@Override
	protected WorkerFactory<?> getFactory() {
		return factory;
	}

	@Override
	protected Map getParameterInstances() {
		Map<String,Object> toReturn=new HashMap<>();
		toReturn.put(ImportCodeListMappingFactory.ID_PARAMETER.getIdentifier(), TestCommon.getMappingFileId());
		toReturn.put(ImportCodeListMappingFactory.PREVIOUS_VERSION_CODELIST_PARAMETER.getIdentifier(), oldCodes);
		return toReturn;
	}

	@Override
	protected ColumnLocalId getTargetColumnId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected TableId getTargetTableId() {
		return codelist.getId();
	}

	
	@Before
	public void init(){
		TableCreator tcCL=cubeManager.createTable(new CodelistTableType());
		tcCL.addColumn(new CodeColumnFactory().create(new ImmutableLocalizedText("speciesid"), new TextType(20)));
		tcCL.addColumn(new CodeDescriptionColumnFactory().create(new ImmutableLocalizedText("genus"), new TextType(20)));
		tcCL.addColumn(new CodeDescriptionColumnFactory().create(new ImmutableLocalizedText("species"), new TextType(20)));		
		tcCL.addColumn(new CodeNameColumnFactory().create(new ImmutableLocalizedText("scientific name"), new DataLocaleMetadata("lt")));
		codelist=tcCL.create();
		copyHandler.copy("cl1.csv", codelist);
		
		TableCreator tcToCurate=cubeManager.createTable(new CodelistTableType());
		tcToCurate.addColumn(new CodeColumnFactory().create(new ImmutableLocalizedText("speciesid"), new TextType(20)));
		tcToCurate.addColumn(new CodeDescriptionColumnFactory().create(new ImmutableLocalizedText("genus"), new TextType(20)));
		tcToCurate.addColumn(new CodeDescriptionColumnFactory().create(new ImmutableLocalizedText("species"), new TextType(20)));		
		tcToCurate.addColumn(new CodeNameColumnFactory().create(new ImmutableLocalizedText("scientific name"), new DataLocaleMetadata("lt")));
		tcToCurate.addColumn(new AttributeColumnFactory().create(new ImmutableLocalizedText("suggested"), new BooleanType()));
		toCurateCodelist=tcToCurate.create();
		copyHandler.copy("extractedcl.csv", toCurateCodelist);
		
		oldCodes=toCurateCodelist.getColumnReference(toCurateCodelist.getColumnByLabel("speciesid"));
		
		
	}
}
