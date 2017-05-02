package org.gcube.portlets.user.td.gwtservice.client;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.List;

import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.service.TabularDataService;
import org.gcube.data.analysis.tabulardata.service.tabular.TabularResource;
import org.gcube.data.analysis.tabulardata.service.tabular.metadata.TabularResourceMetadata;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestServiceListTR {
	private static Logger logger = LoggerFactory.getLogger(TestServiceListTR.class);

	
	private static SimpleDateFormat sdf = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm z");

	@Test
	public void listTR() throws Exception {
		logger.debug("------------List of Tabular Resources--------------");
		TDService tdService=new TDService();
		TabularDataService service = tdService.getService();
		
		List<TabularResource> trs = service.getTabularResources();
		Assert.assertTrue(trs.size() > 0);

		Table lastTable = null;
		for (TabularResource tr : trs) {
			lastTable = service.getLastTable(tr.getId());
			Collection<TabularResourceMetadata<?>> metas = tr.getAllMetadata();

			if (lastTable != null) {
				logger.debug("TabularResource: [ id="
								+ tr.getId().getValue() + ", type="
								+ tr.getTabularResourceType() + ", date="
								+ sdf.format(tr.getCreationDate().getTime())
								+ ", lastTable=[ id="
								+ lastTable.getId().getValue() + ", type="
								+ lastTable.getTableType().getName() + "]]");
				logger.debug("Last table id=" + lastTable.getId()
						+ ", name=" + lastTable.getName());
				for (Column col : lastTable.getColumns()) {
					logger.debug("Column: [name:" + col.getName()
							+ ", localId:" + col.getLocalId() + ", dataType:"
							+ col.getDataType() + "]");

				}

			} else {
				logger.debug("TabularResource: [ id="
						+ tr.getId().getValue() + ", lastTable= " + lastTable
						+ "]");

			}
			for (TabularResourceMetadata<?> meta : metas) {
				logger.debug("Meta: " + meta);
			}
			logger.debug("---------------------------------");
		}

	}

}
