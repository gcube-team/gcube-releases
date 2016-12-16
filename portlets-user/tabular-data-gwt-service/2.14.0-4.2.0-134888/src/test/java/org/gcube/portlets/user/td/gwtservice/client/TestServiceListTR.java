package org.gcube.portlets.user.td.gwtservice.client;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.List;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.analysis.tabulardata.commons.utils.AuthorizationProvider;
import org.gcube.data.analysis.tabulardata.commons.utils.AuthorizationToken;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.service.TabularDataService;
import org.gcube.data.analysis.tabulardata.service.impl.TabularDataServiceFactory;
import org.gcube.data.analysis.tabulardata.service.tabular.TabularResource;
import org.gcube.data.analysis.tabulardata.service.tabular.metadata.TabularResourceMetadata;
import org.gcube.portlets.user.td.gwtservice.shared.Constants;
import org.junit.Assert;
import org.junit.Test;

public class TestServiceListTR {

	private static SimpleDateFormat sdf = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm z");

	@Test
	public void listTR() throws Exception {
		System.out
				.println("------------List of Tabular Resources--------------");
		ScopeProvider.instance.set(Constants.DEFAULT_SCOPE);
		AuthorizationProvider.instance.set(new AuthorizationToken(
				Constants.DEFAULT_USER));
		TabularDataService service = TabularDataServiceFactory.getService();

		List<TabularResource> trs = service.getTabularResources();
		Assert.assertTrue(trs.size() > 0);

		Table lastTable = null;
		for (TabularResource tr : trs) {
			lastTable = service.getLastTable(tr.getId());
			Collection<TabularResourceMetadata<?>> metas = tr.getAllMetadata();

			if (lastTable != null) {
				System.out
						.println("TabularResource: [ id="
								+ tr.getId().getValue() + ", type="
								+ tr.getTabularResourceType() + ", date="
								+ sdf.format(tr.getCreationDate().getTime())
								+ ", lastTable=[ id="
								+ lastTable.getId().getValue() + ", type="
								+ lastTable.getTableType().getName() + "]]");
				System.out.println("Last table id=" + lastTable.getId()
						+ ", name=" + lastTable.getName());
				for (Column col : lastTable.getColumns()) {
					System.out.println("Column: [name:" + col.getName()
							+ ", localId:" + col.getLocalId() + ", dataType:"
							+ col.getDataType() + "]");

				}

			} else {
				System.out.println("TabularResource: [ id="
						+ tr.getId().getValue() + ", lastTable= " + lastTable
						+ "]");

			}
			for (TabularResourceMetadata<?> meta : metas) {
				System.out.println("Meta: " + meta);
			}
			System.out.println("---------------------------------");
		}

	}

}
