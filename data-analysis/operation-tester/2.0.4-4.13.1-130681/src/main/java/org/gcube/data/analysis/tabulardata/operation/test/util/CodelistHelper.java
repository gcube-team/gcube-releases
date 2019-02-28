package org.gcube.data.analysis.tabulardata.operation.test.util;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.eclipse.persistence.jpa.jpql.Assert;
import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.cube.tablemanagers.TableCreator;
import org.gcube.data.analysis.tabulardata.cube.tablemanagers.TableMetaCreator;
import org.gcube.data.analysis.tabulardata.model.column.factories.AnnotationColumnFactory;
import org.gcube.data.analysis.tabulardata.model.column.factories.CodeColumnFactory;
import org.gcube.data.analysis.tabulardata.model.column.factories.CodeNameColumnFactory;
import org.gcube.data.analysis.tabulardata.model.metadata.column.DataLocaleMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.common.DescriptionsMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.common.ImmutableLocalizedText;
import org.gcube.data.analysis.tabulardata.model.metadata.common.LocalizedText;
import org.gcube.data.analysis.tabulardata.model.metadata.common.NamesMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.common.TableDescriptorMetadata;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.type.CodelistTableType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class CodelistHelper {

	private static final Logger log = LoggerFactory.getLogger(CodelistHelper.class);

	@Inject
	public CubeManager cm;

	@Inject
	private DatabaseConnectionProvider connectionProvider;

	@Inject
	private CopyHandler copyHandler;

	public Table createSpeciesCodelist() {
		TableCreator tc = cm.createTable(new CodelistTableType());
		Table codelist = null;

		// Create table structure
		try {
			CodeNameColumnFactory factory=new CodeNameColumnFactory();
			
			tc.addColumn(new CodeColumnFactory().createDefault());
			tc.addColumn(factory.create("en"));
			tc.addColumn(factory.create("fr"));
			tc.addColumn(factory.create("es"));
			tc.addColumn(factory.create("la"));
			tc.addColumn(new AnnotationColumnFactory().create(new ImmutableLocalizedText("author", "en"),
					new DataLocaleMetadata("en")));
			codelist = tc.create();
			
			List<LocalizedText> tableNames = new ArrayList<LocalizedText>();
			tableNames.add(new ImmutableLocalizedText("Species"));
			tableNames.add(new ImmutableLocalizedText("Specie","it"));
			
			List<LocalizedText> tableDescriptions = new ArrayList<LocalizedText>();
			tableNames.add(new ImmutableLocalizedText("Marine species"));
			tableNames.add(new ImmutableLocalizedText("Specie marine","it"));
			TableMetaCreator tmc = cm.modifyTableMeta(codelist.getId());
			
			tmc.setTableMetadata(new NamesMetadata(tableNames));
			tmc.setTableMetadata(new DescriptionsMetadata(tableDescriptions));
			tmc.setTableMetadata(new TableDescriptorMetadata("test_cl", "1", 1));
			codelist = tmc.create();
			
			log.debug("Created sample codelist table:\n" + codelist);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}

		// Fill with data
		copyHandler.copy("cl_species.csv", codelist);
		return codelist;
	}

}
